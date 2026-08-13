<template>
  <div>
    <div v-permission="['hr:authorization:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="statistics-row">
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-total">{{ statistics.total }}</div>
              <div class="stat-label">授权总数</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-valid">{{ statistics.valid }}</div>
              <div class="stat-label">有效授权</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-expiring">{{ statistics.expiring }}</div>
              <div class="stat-label">即将过期</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-expired">{{ statistics.expired }}</div>
              <div class="stat-label">已过期/无效</div>
            </a-card>
          </a-col>
        </a-row>
        <!-- 数据列表 -->
        <vxe-grid
          id="PersonAuthorization"
          ref="grid"
          resizable
          show-overflow
          highlight-hover-row
          keep-source
          row-id="id"
          :proxy-config="proxyConfig"
          :columns="tableColumn"
          :toolbar-config="toolbarConfig"
          :custom-config="{}"
          :pager-config="{}"
          :sort-config="{ remote: true }"
          :loading="loading"
          height="530px"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
        >
          <template #form>
            <j-border>
              <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                <j-form-item label="员工姓名">
                  <a-input v-model:value="searchFormData.employeeName" placeholder="请输入员工姓名" allow-clear />
                </j-form-item>
                <j-form-item label="岗位">
                  <a-input v-model:value="searchFormData.projectName" placeholder="请输入岗位名称" allow-clear />
                </j-form-item>
                <j-form-item label="授权状态">
                  <a-select
                    v-model:value="searchFormData.status"
                    placeholder="请选择"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option :value="1">有效</a-select-option>
                    <a-select-option :value="2">即将过期</a-select-option>
                    <a-select-option :value="0">已过期</a-select-option>
                    <a-select-option :value="3">无效</a-select-option>
                  </a-select>
                </j-form-item>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button :icon="h(SyncOutlined)" @click="reset">重置</a-button>
              <a-button
                v-permission="['hr:authorization:query']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >新增授权</a-button
              >
            </a-space>
          </template>

          <!-- 授权项目 列自定义内容 -->
          <template #projectName_default="{ row }">
            <div v-if="row.projects && row.projects.length > 0" class="multi-projects">
              <a-tag
                v-for="proj in row.projects"
                :key="proj.projectId"
                :color="getProjectStatusColor(proj.status)"
                class="project-tag"
              >
                {{ proj.projectName }}
                <a-tooltip v-if="!proj.requiredCoursesCompleted" title="必修课程未完成">
                  <a-icon type="exclamation-circle" class="warning-icon" />
                </a-tooltip>
              </a-tag>
            </div>
            <span v-else>{{ row.projectName || '-' }}</span>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="getStatusColor(row.status)">
              {{ getStatusText(row.status) }}
            </a-tag>
          </template>

          <!-- 凭据附件 列自定义内容 -->
          <template #credential_default="{ row }">
            <a v-if="row.credentialFileName" @click="handleDownloadCredential(row)">
              {{ row.credentialFileName }}
            </a>
            <span v-else>-</span>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />

    <!-- 有效性检查结果弹窗 -->
    <a-modal
      v-model:open="validityModalVisible"
      title="授权有效性检查"
      :footer="null"
      width="600px"
    >
      <div v-if="validityResult" class="validity-result">
        <div class="result-item">
          <span class="label">授权状态:</span>
          <a-tag :color="validityResult.isValid ? 'success' : 'error'">
            {{ validityResult.isValid ? '有效' : '无效' }}
          </a-tag>
        </div>
        <div class="result-item">
          <span class="label">有效期状态:</span>
          <span :class="validityResult.expiryStatus">{{ getExpiryStatusText(validityResult.expiryStatus) }}</span>
        </div>
        <div class="result-item">
          <span class="label">课程完成状态:</span>
          <span :class="validityResult.courseCompletionStatus">{{ getCourseStatusText(validityResult.courseCompletionStatus) }}</span>
        </div>
        <div v-if="validityResult.incompleteCourses && validityResult.incompleteCourses.length > 0" class="result-item">
          <span class="label">未完成课程:</span>
          <ul class="incomplete-list">
            <li v-for="course in validityResult.incompleteCourses" :key="course">{{ course }}</li>
          </ul>
        </div>
        <div v-if="validityResult.daysUntilExpiry !== undefined" class="result-item">
          <span class="label">距离到期:</span>
          <span>{{ validityResult.daysUntilExpiry }} 天</span>
        </div>
      </div>
    </a-modal>

    <!-- 续期弹窗 -->
    <a-modal
      v-model:open="renewModalVisible"
      title="授权续期"
      @ok="handleRenewSubmit"
      @cancel="handleRenewCancel"
      width="800px"
    >
      <div v-if="validityResult && validityResult.projects">
        <p style="margin-bottom: 16px; color: #666;">请选择需要续期的项目并设置新的到期日期：</p>
        <a-table
          :columns="renewProjectColumns"
          :data-source="renewProjects"
          :pagination="false"
          size="small"
          bordered
          row-key="projectId"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'selected'">
              <a-checkbox v-model:checked="record.selected" />
            </template>
            <template v-else-if="column.key === 'projectName'">
              {{ record.projectName }}
            </template>
            <template v-else-if="column.key === 'authorizationDate'">
              {{ record.authorizationDate }}
            </template>
            <template v-else-if="column.key === 'expiryDate'">
              <span :class="{ 'expiry-expired': record.expiryStatus === 'expired', 'expiry-warning': record.expiryStatus === 'expiring' }">
                {{ record.expiryDate }}
              </span>
            </template>
            <template v-else-if="column.key === 'newExpiryDate'">
              <a-form-item
                :name="['renewProjects', index, 'newExpiryDate']"
                :rules="record.selected ? [{ required: true, message: '请选择新到期日期', trigger: 'change' }] : []"
                :no-style="true"
              >
                <a-date-picker
                  v-model:value="record.newExpiryDate"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                  :disabled="!record.selected"
                />
              </a-form-item>
            </template>
          </template>
        </a-table>
      </div>
      <a-form :model="renewForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" style="margin-top: 16px;">
        <a-form-item label="续期凭据">
          <a-upload
            :custom-request="handleRenewCredentialUpload"
            :show-upload-list="false"
            accept=".pdf,.jpg,.jpeg,.png"
          >
            <a-button>
              <upload-outlined />
              {{ renewForm.credentialFile ? '重新上传' : '上传凭据' }}
            </a-button>
          </a-upload>
          <span v-if="renewForm.credentialFile" class="file-name">{{ renewForm.credentialFile.name }}</span>
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="renewForm.description" :rows="3" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import * as api from '@/api/hr/authorization-person';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    UploadOutlined,
    ExclamationCircleOutlined
  } from '@ant-design/icons-vue';
  
  const projectList = [
    { id: '1', projectName: '维修工程师授权' },
    { id: '2', projectName: '设备操作授权' },
    { id: '3', projectName: '高空作业授权' }
  ];

  export default defineComponent({
    name: 'HrPersonAuthorization',
    components: {
      Add,
      Modify,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        SyncOutlined,
        PlusOutlined,
        UploadOutlined,
        ExclamationCircleOutlined,
        projectList,
      };
    },
    data() {
      return {
        loading: false,
        id: '',
        selectedRows: [],
        selectedRowKeys: [],
        validityModalVisible: false,
        validityResult: null,
        renewModalVisible: false,
        renewProjects: [],
        renewForm: {
          credentialFile: null,
          description: ''
        },
        renewProjectColumns: [
          {
            title: '选择',
            key: 'selected',
            width: 60,
          },
          {
            title: '岗位',
            key: 'projectName',
            width: 200,
          },
          {
            title: '授权日期',
            key: 'authorizationDate',
            width: 120,
          },
          {
            title: '到期日期',
            key: 'expiryDate',
            width: 120,
          },
          {
            title: '新到期日期',
            key: 'newExpiryDate',
            width: 180,
          },
        ],
        statistics: {
          total: 0,
          valid: 0,
          expiring: 0,
          expired: 0
        },
        searchFormData: {
          employeeName: '',
          projectName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        },
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'employeeName', title: '员工姓名', width: 120 },
          { field: 'employeeCode', title: '员工工号', width: 120 },
          { field: 'projectName', title: '岗位', width: 220, slots: { default: 'projectName_default' } },
          { field: 'status', title: '授权状态', width: 100, slots: { default: 'status_default' } },
          { field: 'credentialFileName', title: '凭据附件', width: 150, slots: { default: 'credential_default' } },
          { field: 'createTime', title: '创建时间', width: 180 },
          { title: '操作', width: 230, fixed: 'right', slots: { default: 'action_default' } },
        ],
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'total',
          },
          ajax: {
            query: ({ page, sorts }) => {
              return api.query(this.buildQueryParams(page, sorts)).then((res) => {
                this.calculateStatistics(res.datas);
                return res;
              });
            },
          },
        },
      };
    },
    created() {},
    methods: {
      getStatusColor(status) {
        const colors = {
          1: 'success',
          2: 'warning',
          0: 'error',
          3: 'default'
        };
        return colors[status] || 'default';
      },
      
      getProjectStatusColor(status) {
        const colors = {
          1: 'green',
          2: 'orange',
          0: 'red',
          3: 'default'
        };
        return colors[status] || 'default';
      },
      
      getStatusText(status) {
        const texts = {
          1: '有效',
          2: '即将过期',
          0: '已过期',
          3: '无效'
        };
        return texts[status] || '';
      },
      
      getExpiryStatusText(status) {
        const texts = {
          valid: '有效期内',
          expiring: '即将过期',
          expired: '已过期'
        };
        return texts[status] || status;
      },
      
      getCourseStatusText(status) {
        const texts = {
          completed: '已完成',
          incomplete: '未完成'
        };
        return texts[status] || status;
      },
      
      calculateStatistics(data) {
        this.statistics.total = data.length;
        this.statistics.valid = data.filter(item => item.status === 1).length;
        this.statistics.expiring = data.filter(item => item.status === 2).length;
        this.statistics.expired = data.filter(item => item.status === 0 || item.status === 3).length;
      },
      
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      
      buildQueryParams(page, sorts) {
        const params = {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
        return params;
      },
      
      buildSearchFormData() {
        return {
          employeeName: this.searchFormData.employeeName,
          projectName: this.searchFormData.projectName,
          status: this.searchFormData.status,
        };
      },

      reset() {
        this.searchFormData = {
          employeeName: '',
          projectName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        };
        if (this.$refs.grid && this.$refs.grid.clearSort) {
          this.$refs.grid.clearSort();
        }
        this.search();
      },
      
      createActions(row) {
        return [
          {
            label: '检查有效性',
            onClick: () => {
              this.handleCheckValidity(row);
            },
          },
          {
            label: '续期',
            onClick: () => {
              this.handleRenew(row);
            },
          },
          {
            permission: ['hr:authorization:update'],
            label: '编辑',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
          {
            permission: ['hr:authorization:delete'],
            label: '删除',
            type: 'danger',
            onClick: () => {
              this.handleDelete(row);
            },
          },
        ];
      },
      
      handleCheckboxChange({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },

      handleCheckboxAll({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },
      
      handleAdd() {
        this.$nextTick(() => this.$refs.addDialog.openDialog());
      },
      
      async handleCheckValidity(row) {
        this.loading = true;
        try {
          const res = await api.checkValidity(row.id);
          this.validityResult = res;
          this.validityModalVisible = true;
        } catch (error) {
          this.$message.error('检查有效性失败');
        } finally {
          this.loading = false;
        }
      },
      
      async handleRenew(row) {
        this.id = row.id;
        this.renewForm = {
          credentialFile: null,
          description: ''
        };
        this.renewProjects = [];
        
        try {
          const res = await api.checkValidity(row.id);
          if (res && res.projects) {
            this.renewProjects = res.projects.map(project => ({
              ...project,
              selected: false,
              newExpiryDate: ''
            }));
          }
          this.renewModalVisible = true;
        } catch (error) {
          this.$message.error('获取项目信息失败');
        }
      },
      
      async handleRenewSubmit() {
        const selectedProjects = this.renewProjects.filter(p => p.selected);
        
        if (selectedProjects.length === 0) {
          this.$message.warning('请至少选择一个项目进行续期');
          return;
        }
        
        const invalidProjects = selectedProjects.filter(p => !p.newExpiryDate);
        if (invalidProjects.length > 0) {
          this.$message.warning('请为选中的项目设置新到期日期');
          return;
        }
        
        this.loading = true;
        try {
          for (const project of selectedProjects) {
            await api.extend(this.id, project.projectId, project.newExpiryDate);
          }
          
          this.$message.success('续期成功');
          this.renewModalVisible = false;
          this.search();
        } catch (error) {
          this.$message.error('续期失败');
        } finally {
          this.loading = false;
        }
      },
      
      handleRenewCancel() {
        this.renewModalVisible = false;
      },
      
      handleRenewCredentialUpload(options) {
        const { file } = options;
        this.renewForm.credentialFile = file;
      },
      
      handleDownloadCredential(row) {
        if (row.credentialFileUrl) {
          window.open(row.credentialFileUrl, '_blank');
        }
      },
      
      handleDelete(record) {
        this.$confirm({
          title: '确定要删除该授权吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.del(record.id);
              this.$message.success('删除成功');
              this.search();
            } catch (error) {
              this.$message.error('删除失败');
            } finally {
              this.loading = false;
            }
          }
        });
      },
    },
  });
</script>
<style scoped>
  .statistics-row {
    margin-bottom: 24px;
  }

  .stat-card {
    text-align: center;
    
    :deep(.ant-card-body) {
      padding: 16px;
    }
    
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 8px;
      
      &.stat-total {
        color: #1890ff;
      }
      
      &.stat-valid {
        color: #52c41a;
      }
      
      &.stat-expiring {
        color: #faad14;
      }
      
      &.stat-expired {
        color: #ff4d4f;
      }
    }
    
    .stat-label {
      font-size: 14px;
      color: #666;
    }
  }

  .validity-result {
    .result-item {
      margin-bottom: 16px;
      display: flex;
      align-items: flex-start;

      .label {
        width: 120px;
        color: #666;
        flex-shrink: 0;
      }

      .valid {
        color: #52c41a;
      }

      .expiring {
        color: #faad14;
      }

      .expired {
        color: #ff4d4f;
      }

      .completed {
        color: #52c41a;
      }

      .incomplete {
        color: #ff4d4f;
      }

      .incomplete-list {
        margin: 0;
        padding-left: 20px;
        color: #ff4d4f;
      }
    }
  }

  .multi-projects {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }

  .project-tag {
    margin: 2px 0;

    .warning-icon {
      margin-left: 4px;
      color: #faad14;
    }
  }

  .expiry-warning {
    color: #faad14;
  }

  .expiry-expired {
    color: #ff4d4f;
  }

  .file-name {
    margin-left: 8px;
    color: #666;
  }

  .modal-footer {
    margin-top: 24px;
    text-align: center;
  }
</style>
