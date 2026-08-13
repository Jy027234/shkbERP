<template>
  <div>
    <div v-permission="['hr:employee:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="statistics-row">
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-total">{{ statistics.total }}</div>
              <div class="stat-label">员工总数</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-active">{{ statistics.active }}</div>
              <div class="stat-label">在职员工</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-probation">{{ statistics.probation }}</div>
              <div class="stat-label">试用期</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-resigned">{{ statistics.resigned }}</div>
              <div class="stat-label">已离职</div>
            </a-card>
          </a-col>
        </a-row>
        <!-- 数据列表 -->
        <vxe-grid
          id="Employee"
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
                <!-- 常显的核心筛选项 -->
                <j-form-item label="工号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>
                <j-form-item label="姓名">
                  <a-input v-model:value="searchFormData.name" allow-clear />
                </j-form-item>
                <j-form-item label="部门">
                  <a-select
                    v-model:value="searchFormData.deptId"
                    placeholder="请选择部门"
                    allow-clear
                    style="width: 160px"
                  >
                    <a-select-option v-for="dept in deptList" :key="dept.id" :value="dept.id">
                      {{ dept.name }}
                    </a-select-option>
                  </a-select>
                </j-form-item>
                <j-form-item label="状态">
                  <a-select
                    v-model:value="searchFormData.status"
                    placeholder="请选择状态"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option :value="1">在职</a-select-option>
                    <a-select-option :value="2">试用期</a-select-option>
                    <a-select-option :value="0">离职</a-select-option>
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
                v-permission="['hr:employee:create']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >新增员工</a-button
              >
              <a-button
                v-permission="['hr:employee:query']"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
                >导出Excel</a-button
              >
              <a-button
                v-permission="['hr:employee:delete']"
                danger
                :disabled="!selectedRowKeys.length"
                :icon="h(DeleteOutlined)"
                @click="handleBatchDelete"
                >批量操作</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="getStatusColor(row.status)">
              {{ getStatusText(row.status) }}
            </a-tag>
          </template>

          <!-- 性别 列自定义内容 -->
          <template #gender_default="{ row }">
            {{ getGenderText(row.gender) }}
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="handleAddConfirm" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="handleUpdateConfirm" />

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" @edit="handleDetailEdit" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    DeleteOutlined,
    DownloadOutlined
  } from '@ant-design/icons-vue';
  import * as api from '@/api/hr/employee';
  
  export default defineComponent({
    name: 'HrEmployee',
    components: {
      Add,
      Modify,
      Detail,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        SyncOutlined,
        PlusOutlined,
        DeleteOutlined,
        DownloadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 部门列表
        deptList: [],
        // 当前行数据
        id: '',
        // 选中的行数据
        selectedRows: [], // 选中的行数据
        // 选中的员工ID列表
        selectedRowKeys: [],
        // 统计数据
        statistics: {
          total: 0,
          active: 0,
          probation: 0,
          resigned: 0
        },
        // 查询列表的查询条件
        searchFormData: {
          code: '',
          name: '',
          deptId: undefined,
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        },
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'code', title: '工号', width: 100, sortable: true },
          { field: 'name', title: '姓名', width: 100, sortable: true },
          { field: 'gender', title: '性别', width: 80, slots: { default: 'gender_default' } },
          { field: 'deptName', title: '部门', width: 120 },
          { field: 'position', title: '职位', width: 120 },
          { field: 'phone', title: '联系电话', width: 130 },
          { field: 'entryDate', title: '入职日期', width: 120, sortable: true },
          { field: 'status', title: '状态', width: 100, slots: { default: 'status_default' } },
          { field: 'createTime', title: '创建时间', width: 180, sortable: true },
          { title: '操作', width: 200, fixed: 'right', slots: { default: 'action_default' } },
        ],
        // 请求接口配置
        proxyConfig: {
          props: {
            // 响应结果列表字段
            result: 'datas',
            // 响应结果总条数字段
            total: 'total',
          },
          ajax: {
            // 查询接口
            query: ({ page, sorts, form }) => {
              // 调用真实API
              const params = {
                ...this.$utils.buildSortPageVo(page, sorts),
                ...this.buildSearchFormData(),
              };
              return api.query(params).then((res) => {
                return res;
              });
            },
          },
        },
      };
    },
    created() {
      this.loadDeptList();
      this.loadStatistics();
    },
    methods: {
      async loadDeptList() {
        try {
          const res = await api.getDepts();
          this.deptList = res || [];
        } catch (error) {
          // 错误已在拦截器处理
        }
      },
      async loadStatistics() {
        try {
          const res = await api.getStatistics();
          if (res) {
            this.statistics = {
              total: res.total || 0,
              active: res.active || 0,
              probation: res.probation || 0,
              resigned: res.resigned || 0
            };
          }
        } catch (error) {
          // 错误已在拦截器处理
        }
      },
      // 获取状态颜色
      getStatusColor(status) {
        const colors = {
          0: 'default',
          1: 'success',
          2: 'processing'
        };
        return colors[status] || 'default';
      },
      
      // 获取状态文本
      getStatusText(status) {
        const texts = {
          0: '离职',
          1: '在职',
          2: '试用期'
        };
        return texts[status] || '';
      },
      
      // 获取性别文本
      getGenderText(gender) {
        const texts = {
          0: '未知',
          1: '男',
          2: '女'
        };
        return texts[gender] || '';
      },
      
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        const params = {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };

        return params;
      },
      
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        return {
          keyword: this.searchFormData.name || this.searchFormData.code,
          deptId: this.searchFormData.deptId,
          status: this.searchFormData.status,
        };
      },
      
      // 重置筛选项为默认值
      reset() {
        this.searchFormData = {
          code: '',
          name: '',
          deptId: undefined,
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        };
        // 清除表格排序状态，避免残留排序影响查询
        if (this.$refs.grid && this.$refs.grid.clearSort) {
          this.$refs.grid.clearSort();
        }
        this.search();
      },
      
      // 创建操作按钮
      createActions(row) {
        return [
          {
            label: '详情',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.viewDialog.openDialog());
            },
          },
          {
            permission: ['hr:employee:update'],
            label: '编辑',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
          {
            permission: ['hr:employee:delete'],
            label: row.status === 0 ? '删除' : '离职',
            type: row.status !== 0 ? 'default' : 'danger',
            onClick: () => {
              this.handleDelete(row);
            },
          },
        ];
      },
      
      // 处理表格行选择变化
      handleCheckboxChange({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },

      // 处理表格全选/取消全选
      handleCheckboxAll({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },
      
      // 新增员工
      handleAdd() {
        this.$nextTick(() => this.$refs.addDialog.openDialog());
      },
      
      // 新增成功后刷新
      handleAddConfirm() {
        this.search();
        this.loadStatistics();
      },
      
      // 删除/离职
      handleDelete(record) {
        const title = record.status === 0 ? '确定要删除该员工吗？' : '确定要将该员工设为离职状态吗？';
        this.$confirm({
          title,
          onOk: async () => {
            try {
              if (record.status === 0) {
                // 已离职员工，执行物理删除
                await api.del(record.id);
                this.$message.success('删除成功');
              } else {
                // 在职/试用期员工，设为离职状态
                await api.leaveStatus({ id: record.id });
                this.$message.success('已设为离职状态');
              }
              this.search();
              this.loadStatistics();
            } catch (error) {
              // 错误已在拦截器处理
            }
          }
        });
      },
      
      // 批量删除/离职
      handleBatchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择要操作的员工');
          return;
        }
        
        this.$confirm({
          title: '确定要批量操作所选员工吗？',
          onOk: async () => {
            try {
              // 区分已离职和在职员工
              const leaveEmployees = this.selectedRows.filter(e => e.status !== 0);
              const deleteEmployees = this.selectedRows.filter(e => e.status === 0);
              
              // 并行执行删除和离职操作
              if (deleteEmployees.length > 0) {
                await api.batchDelete(deleteEmployees.map(e => e.id));
              }
              if (leaveEmployees.length > 0) {
                await api.batchLeaveStatus({ ids: leaveEmployees.map(e => e.id) });
              }
              
              this.$message.success(`操作成功：${leaveEmployees.length}人设为离职，${deleteEmployees.length}人删除`);
              this.selectedRowKeys = [];
              this.search();
              this.loadStatistics();
            } catch (error) {
              // 错误已在拦截器处理
            }
          }
        });
      },
      
      // 导出Excel
      handleExport() {
        const params = this.buildSearchFormData();
        api.exportList(params).then((res) => {
          if (res) {
            const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `员工列表_${new Date().toLocaleDateString()}.xlsx`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            this.$message.success('导出成功');
          }
        });
      },

      handleDetailEdit(record) {
        this.id = record.id;
        this.$nextTick(() => this.$refs.updateDialog.openDialog());
      },
      handleUpdateConfirm() {
        // 刷新列表
        this.search();
        this.loadStatistics();
        // 刷新详情页面
        if (this.$refs.viewDialog) {
          this.$refs.viewDialog.refresh();
        }
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
      
      &.stat-active {
        color: #52c41a;
      }
      
      &.stat-probation {
        color: #faad14;
      }
      
      &.stat-resigned {
        color: #999;
      }
    }
    
    .stat-label {
      font-size: 14px;
      color: #666;
    }
  }
</style>