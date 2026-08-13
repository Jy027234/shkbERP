<template>
  <div>
    <div v-permission="['hr:training:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="TrainingImplementation"
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
                <j-form-item label="课程名称">
                  <a-input v-model:value="searchFormData.courseName" allow-clear />
                </j-form-item>
                <j-form-item label="实施状态">
                  <a-select
                    v-model:value="searchFormData.status"
                    placeholder="请选择"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option :value="0">计划中</a-select-option>
                    <a-select-option :value="1">进行中</a-select-option>
                    <a-select-option :value="2">已完成</a-select-option>
                    <a-select-option :value="3">已取消</a-select-option>
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
                v-permission="['hr:training:create']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >创建实施计划</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="getStatusColor(row.status)">
              {{ getStatusText(row.status) }}
            </a-tag>
          </template>

          <!-- 学员管理 列自定义内容 -->
          <template #participantCount_default="{ row }">
            <a-button type="link" size="small" @click="handleManageParticipants(row)">
              管理学员 ({{ row.participantCount || 0 }})
            </a-button>
          </template>

          <!-- 扫描件 列自定义内容 -->
          <template #attachment_default="{ row }">
            <a v-if="row.fileName && row.url" :href="row.url" target="_blank" :title="row.fileName" class="file-name">{{ row.fileName }}</a>
            <span v-else-if="row.fileName" :title="row.fileName" class="file-name">{{ row.fileName }}</span>
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

    <!-- 学员管理弹窗 -->
    <a-modal
      v-model:open="participantModalVisible"
      title="学员管理"
      :footer="null"
      width="900px"
    >
      <div v-if="participantModalVisible" class="participant-manager">
        <div class="manager-header">
          <a-button 
            v-if="currentImplementationStatus === 0 || currentImplementationStatus === 1" 
            type="primary" 
            :icon="h(PlusOutlined)" 
            @click="handleAddParticipants"
          >
            添加学员
          </a-button>
          <span v-else class="completed-tip">该培训已完成，无法添加学员</span>
        </div>
        <a-table
          :columns="participantColumns"
          :data-source="participantList"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getParticipantStatusColor(record.status)">
                {{ getParticipantStatusText(record.status) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'trainingResult'">
              <span v-if="record.trainingResult">{{ record.trainingResult }}</span>
              <span v-else class="no-result">-</span>
            </template>
            <template v-if="column.key === 'certificateNo'">
              <span v-if="record.certificateNo">{{ record.certificateNo }}</span>
              <span v-else class="no-result">-</span>
            </template>
            <template v-if="column.key === 'action'">
              <a-popconfirm 
                v-if="currentImplementationStatus === 0 || currentImplementationStatus === 1"
                title="确定要移除该学员吗？" 
                @confirm="handleRemoveParticipant(record)"
              >
                <a-button type="link" danger size="small">移除</a-button>
              </a-popconfirm>
              <span v-else class="completed-tip">已完成</span>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>

    <!-- 添加学员弹窗 -->
    <a-modal
      v-model:open="addParticipantModalVisible"
      title="添加学员"
      @ok="handleAddParticipantSubmit"
      @cancel="handleAddParticipantCancel"
      width="700px"
    >
      <div class="add-participant-tip">请选择要添加的学员（已添加的学员不会显示在列表中）</div>
      <a-transfer
        v-model:target-keys="selectedEmployeeIds"
        :data-source="employeeDataSource"
        :titles="['可选员工', '已选学员']"
        :render="item => item.title"
        show-search
        :filter-option="(inputValue, item) => item.title.indexOf(inputValue) !== -1"
      />
    </a-modal>

    <!-- 完成培训弹窗 -->
    <a-modal
      v-model:open="completeModalVisible"
      title="完成培训"
      @ok="handleCompleteSubmit"
      @cancel="handleCompleteCancel"
      width="800px"
    >
      <a-form :model="completeForm">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="实际结束日期" required>
              <a-date-picker v-model:value="completeForm.actualEndDate" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="培训类型">
              <a-input v-model:value="completeForm.trainingType" placeholder="请输入培训类型" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="培训机构">
              <a-input v-model:value="completeForm.trainingOrg" placeholder="请输入培训机构" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="培训学时">
              <a-input-number v-model:value="completeForm.trainingHours" :min="0" style="width: 100%" placeholder="请输入培训学时" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="培训内容">
          <a-textarea v-model:value="completeForm.trainingContent" :rows="2" placeholder="请输入培训内容" />
        </a-form-item>
        <a-form-item label="培训完成附件">
          <a-upload
            :file-list="completeForm.fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            @remove="handleFileRemove"
            accept=".pdf,.jpg,.jpeg,.png"
            :max-count="1"
          >
            <a-button>
              <upload-outlined />
              上传附件
            </a-button>
          </a-upload>
          <div class="form-hint">支持 PDF、图片格式，用于存储培训完成扫描件</div>
        </a-form-item>
        <a-divider>学员培训结果</a-divider>
        <a-table
          :columns="resultColumns"
          :data-source="completeForm.participantResults"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'trainingResult'">
              <a-select v-model:value="record.trainingResult" style="width: 120px">
                <a-select-option value="合格">合格</a-select-option>
                <a-select-option value="优秀">优秀</a-select-option>
                <a-select-option value="不合格">不合格</a-select-option>
              </a-select>
            </template>
            <template v-if="column.key === 'certificateNo'">
              <a-input v-model:value="record.certificateNo" placeholder="证书编号（可选）" />
            </template>
          </template>
        </a-table>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import * as api from '@/api/hr/training-implementation';
  import * as employeeApi from '@/api/hr/employee';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    UploadOutlined
  } from '@ant-design/icons-vue';
  


  export default defineComponent({
    name: 'HrTrainingImplementation',
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
      };
    },
    data() {
      return {
        loading: false,
        id: '',
        selectedRows: [],
        selectedRowKeys: [],
        participantModalVisible: false,
        addParticipantModalVisible: false,
        completeModalVisible: false,
        currentImplementationId: '',
        currentImplementationStatus: 0,
        currentCourseName: '',
        currentRowData: null,
        participantList: [],
        selectedEmployeeIds: [],
        employeeList: [],
        completeForm: {
          implementationId: '',
          actualEndDate: null,
          trainingType: '',
          trainingOrg: '',
          trainingHours: null,
          trainingContent: '',
          participantResults: [],
          fileList: []
        },
        searchFormData: {
          courseName: '',
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
          { field: 'courseName', title: '课程名称', width: 200 },
          { field: 'status', title: '实施状态', width: 100, slots: { default: 'status_default' } },
          { field: 'planStartDate', title: '计划开始日期', width: 130 },
          { field: 'planEndDate', title: '计划结束日期', width: 130 },
          { field: 'trainingLocation', title: '培训地点', width: 120 },
          { field: 'instructor', title: '培训讲师', width: 100 },
          { field: 'participantCount', title: '学员管理', width: 120, slots: { default: 'participantCount_default' } },
          { field: 'hasAttachment', title: '扫描件', width: 100, slots: { default: 'attachment_default' } },
          { field: 'createTime', title: '创建时间', width: 180 },
          { title: '操作', width: 200, fixed: 'right', slots: { default: 'action_default' } },
        ],
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'totalCount',
          },
          ajax: {
            query: ({ page, sorts }) => {
              return api.query(this.buildQueryParams(page, sorts));
            },
          },
        },
      };
    },
    created() {},
    computed: {
      employeeDataSource() {
        // 过滤掉已添加的学员
        const addedEmployeeIds = this.participantList.map(p => p.employeeId);
        return this.employeeList
          .filter(emp => !addedEmployeeIds.includes(emp.id))
          .map(emp => ({
            key: emp.id,
            title: `${emp.name} (${emp.code}) - ${emp.deptName || '无部门'}`,
            employeeName: emp.name,
            employeeCode: emp.code,
            deptName: emp.deptName,
          }));
      },
      participantColumns() {
        return [
          { title: '学员姓名', dataIndex: 'employeeName', key: 'employeeName', width: 120 },
          { title: '工号', dataIndex: 'employeeCode', key: 'employeeCode', width: 120 },
          { title: '部门', dataIndex: 'deptName', key: 'deptName', width: 150 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100, slots: { default: 'status_default' } },
          { title: '培训结果', key: 'trainingResult', width: 100 },
          { title: '证书编号', dataIndex: 'certificateNo', key: 'certificateNo', width: 150 },
          { title: '操作', key: 'action', width: 80 }
        ];
      },
      resultColumns() {
        return [
          { title: '学员姓名', dataIndex: 'employeeName', key: 'employeeName', width: 150 },
          { title: '培训结果', key: 'trainingResult', width: 150 },
          { title: '证书编号', key: 'certificateNo', width: 200 }
        ];
      }
    },
    methods: {
      getStatusColor(status) {
        const colors = {
          0: 'default',
          1: 'processing',
          2: 'success',
          3: 'error'
        };
        return colors[status] || 'default';
      },
      
      getStatusText(status) {
        const texts = {
          0: '计划中',
          1: '进行中',
          2: '已完成',
          3: '已取消'
        };
        return texts[status] || '';
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
          courseName: this.searchFormData.courseName,
          status: this.searchFormData.status,
        };
      },
      
      reset() {
        this.searchFormData = {
          courseName: '',
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
        const actions = [];
        
        if (row.status === 0) {
          actions.push({
            label: '开始培训',
            onClick: () => {
              this.handleStart(row);
            },
          });
        }
        
        if (row.status === 1) {
          actions.push({
            label: '完成培训',
            onClick: () => {
              this.handleComplete(row);
            },
          });
        }
        
        if (row.status === 0 || row.status === 1) {
          actions.push({
            label: '取消',
            type: 'danger',
            onClick: () => {
              this.handleCancel(row);
            },
          });
        }
        
        actions.push({
          permission: ['hr:training:update'],
          label: '编辑',
          onClick: () => {
            this.id = row.id;
            this.$nextTick(() => this.$refs.updateDialog.openDialog());
          },
        });
        
        actions.push({
          permission: ['hr:training:delete'],
          label: '删除',
          type: 'danger',
          onClick: () => {
            this.handleDelete(row);
          },
        });
        
        return actions;
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
      
      handleStart(record) {
        this.$confirm({
          title: '确定要开始该培训吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.start(record.id);
              this.$message.success('培训已开始');
              this.search();
            } catch (error) {
              this.$message.error('操作失败');
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleCancel(record) {
        this.$confirm({
          title: '确定要取消该培训吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.cancel(record.id);
              this.$message.success('培训已取消');
              this.search();
            } catch (error) {
              this.$message.error('操作失败');
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleDelete(record) {
        this.$confirm({
          title: '确定要删除该实施计划吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.del(record.id);
              this.$message.success('删除成功');
              this.search();
            } catch (error) {
              this.$message.error('操作失败');
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      async handleManageParticipants(row) {
        this.currentImplementationId = row.id;
        this.currentImplementationStatus = row.status;
        this.currentCourseName = row.courseName || '';
        this.currentRowData = row;
        
        this.loading = true;
        try {
          const data = await api.queryParticipants(row.id);
          this.participantList = data.datas || [];
        } catch (error) {
          this.$message.error('获取学员列表失败');
          this.participantList = [];
        } finally {
          this.loading = false;
          this.participantModalVisible = true;
        }
      },
      
      async handleAddParticipants() {
        const addedEmployeeIds = this.participantList.map(p => p.employeeId);
        this.selectedEmployeeIds = [];
        
        // 加载员工列表
        this.loading = true;
        try {
          const res = await employeeApi.query({ pageIndex: 1, pageSize: 1000 });
          console.log('员工数据:', res);
          this.employeeList = res.datas || [];
          console.log('员工列表:', this.employeeList);
        } catch (error) {
          console.error('加载员工列表失败:', error);
          this.$message.error('加载员工列表失败');
          this.employeeList = [];
        } finally {
          this.loading = false;
        }
        
        this.addParticipantModalVisible = true;
      },
      
      async handleAddParticipantSubmit() {
        if (this.selectedEmployeeIds.length === 0) {
          this.$message.warning('请选择学员');
          return;
        }
        
        this.loading = true;
        try {
          // 准备要创建的学员数据
          const participantsData = this.selectedEmployeeIds.map(empId => {
            const emp = this.employeeList.find(e => e.id === empId);
            return {
              implementationId: this.currentImplementationId,
              employeeId: empId,
              employeeName: emp ? emp.name : '',
              employeeCode: emp ? emp.code : '',
              deptName: emp ? emp.deptName : '',
            };
          });
          
          // 调用后端API保存学员
          await api.createParticipants(participantsData);
          
          // 重新加载学员列表
          const res = await api.queryParticipants(this.currentImplementationId);
          this.participantList = res.datas || [];
          
          this.addParticipantModalVisible = false;
          this.selectedEmployeeIds = [];
          this.$message.success('添加成功');
        } catch (error) {
          this.$message.error('添加学员失败');
        } finally {
          this.loading = false;
        }
      },
      
      handleAddParticipantCancel() {
        this.addParticipantModalVisible = false;
        this.selectedEmployeeIds = [];
      },
      
      async handleRemoveParticipant(record) {
        this.loading = true;
        try {
          await api.deleteParticipant(record.id);
          this.participantList = this.participantList.filter(p => p.id !== record.id);
          this.$message.success('移除成功');
        } catch (error) {
          this.$message.error('操作失败');
        } finally {
          this.loading = false;
        }
      },
      
      getParticipantStatusColor(status) {
        const colors = {
          0: 'default',
          1: 'processing',
          2: 'success'
        };
        return colors[status] || 'default';
      },
      
      getParticipantStatusText(status) {
        const texts = {
          0: '未开始',
          1: '进行中',
          2: '已完成'
        };
        return texts[status] || '';
      },
      
      async handleComplete(row) {
        this.loading = true;
        try {
          // 查询学员列表
          const res = await api.queryParticipants(row.id);
          const participants = res.datas || [];

          this.completeForm = {
            implementationId: row.id,
            actualEndDate: null,
            // 培训机构、培训类型、培训学时默认为空，需要用户手动填写
            trainingType: '',
            trainingOrg: '',
            trainingHours: null,
            trainingContent: '',
            participantResults: participants.map(p => ({
              participantId: p.id,
              employeeId: p.employeeId,
              employeeName: p.employeeName,
              trainingResult: '合格',
              certificateNo: ''
            })),
            fileList: []
          };
          this.completeModalVisible = true;
        } catch (error) {
          this.$message.error('获取学员列表失败');
        } finally {
          this.loading = false;
        }
      },
      
      // 文件上传前处理
      beforeUpload(file) {
        // 限制文件大小为10MB
        const isLt10M = file.size / 1024 / 1024 < 10;
        if (!isLt10M) {
          this.$message.error('文件大小不能超过10MB!');
          return false;
        }
        return false; // 阻止自动上传，由我们手动控制
      },
      
      // 文件变更处理
      handleFileChange(info) {
        this.completeForm.fileList = [...info.fileList];
      },
      
      // 移除文件
      handleFileRemove(file) {
        const index = this.completeForm.fileList.indexOf(file);
        const newFileList = this.completeForm.fileList.slice();
        newFileList.splice(index, 1);
        this.completeForm.fileList = newFileList;
      },
      
      async handleCompleteSubmit() {
        if (!this.completeForm.actualEndDate) {
          this.$message.warning('请选择实际结束日期');
          return;
        }
        
        this.loading = true;
        try {
          // 格式化日期为 YYYY-MM-DD
          const formatDate = (date) => {
            if (!date) return '';
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
          };
          
          // 创建FormData对象
          const formData = new FormData();
          formData.append('id', this.completeForm.implementationId);
          formData.append('actualEndDate', formatDate(this.completeForm.actualEndDate));
          
          // 添加培训基础信息
          if (this.completeForm.trainingType) {
            formData.append('trainingType', this.completeForm.trainingType);
          }
          if (this.completeForm.trainingOrg) {
            formData.append('trainingOrg', this.completeForm.trainingOrg);
          }
          if (this.completeForm.trainingHours !== null && this.completeForm.trainingHours !== undefined) {
            formData.append('trainingHours', this.completeForm.trainingHours);
          }
          if (this.completeForm.trainingContent) {
            formData.append('trainingContent', this.completeForm.trainingContent);
          }
          
          // 添加学员培训结果
          if (this.completeForm.participantResults && this.completeForm.participantResults.length > 0) {
            formData.append('participantResults', JSON.stringify(this.completeForm.participantResults));
          }
          
          // 添加文件
          if (this.completeForm.fileList && this.completeForm.fileList.length > 0) {
            const file = this.completeForm.fileList[0];
            if (file.originFileObj) {
              formData.append('file', file.originFileObj);
            }
          }
          
          await api.completeWithFile(formData);
          this.$message.success('培训已完成，学员培训记录已生成');
          this.completeModalVisible = false;
          this.completeForm = {
            implementationId: '',
            actualEndDate: null,
            trainingType: '',
            trainingOrg: '',
            trainingHours: null,
            trainingContent: '',
            participantResults: [],
            fileList: []
          };
          this.search();
        } catch (error) {
          this.$message.error('操作失败');
        } finally {
          this.loading = false;
        }
      },
      
      handleCompleteCancel() {
        this.completeModalVisible = false;
        this.completeForm = {
          implementationId: '',
          actualEndDate: null,
          trainingType: '',
          trainingOrg: '',
          trainingHours: null,
          trainingContent: '',
          participantResults: [],
          fileList: []
        };
      },
    },
  });
</script>

<style scoped>
  .participant-manager {
    .manager-header {
      margin-bottom: 16px;
      
      .completed-tip {
        color: #999;
        font-size: 14px;
      }
    }
  }

  .add-participant-tip {
    margin-bottom: 16px;
    color: #666;
    font-size: 14px;
  }

  .no-result {
    color: #999;
  }

  .completed-tip {
    color: #999;
  }

  .file-name {
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    color: #1890ff;
  }
</style>
