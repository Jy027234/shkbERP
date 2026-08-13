<template>
  <div>
    <div v-permission="['hr:employee:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="statistics-row">
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-total">{{ statistics.total }}</div>
              <div class="stat-label">培训总数</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-completed">{{ statistics.completed }}</div>
              <div class="stat-label">已完成</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-inprogress">{{ statistics.inProgress }}</div>
              <div class="stat-label">进行中</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-pending">{{ statistics.pending }}</div>
              <div class="stat-label">待开始</div>
            </a-card>
          </a-col>
        </a-row>
        <!-- 数据列表 -->
        <vxe-grid
          id="TrainingRecord"
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
                <j-form-item label="培训名称">
                  <a-input v-model:value="searchFormData.trainingName" allow-clear />
                </j-form-item>
                <j-form-item label="培训类型">
                  <a-input v-model:value="searchFormData.trainingType" allow-clear />
                </j-form-item>
                <j-form-item label="培训结果">
                  <a-select
                    v-model:value="searchFormData.trainingResult"
                    placeholder="请选择结果"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option value="优秀">优秀</a-select-option>
                    <a-select-option value="良好">良好</a-select-option>
                    <a-select-option value="合格">合格</a-select-option>
                    <a-select-option value="不合格">不合格</a-select-option>
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
                >新增培训记录</a-button
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
                >批量删除</a-button
              >
            </a-space>
          </template>

          <!-- 培训结果 列自定义内容 -->
          <template #trainingResult_default="{ row }">
            <a-tag :color="getResultColor(row.trainingResult)">
              {{ getResultText(row.trainingResult) }}
            </a-tag>
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

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import * as api from '@/api/hr/training-record';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    DeleteOutlined,
    DownloadOutlined
  } from '@ant-design/icons-vue';
  
  const mockData = {
    total: 3,
    pageIndex: 1,
    pageSize: 10,
    datas: [
      {
        id: '1',
        employeeId: '1',
        employeeName: '张三',
        trainingName: '安全培训',
        trainingType: '岗前培训',
        trainingOrg: '安监部',
        trainingContent: '安全生产知识培训',
        startDate: '2024-01-10',
        endDate: '2024-01-15',
        trainingHours: 40,
        trainingResult: '优秀',
        certificateNo: 'CERT001',
        createBy: 'admin',
        createTime: '2024-01-10 10:00:00',
        remark: '年度安全培训，已通过考核'
      },
      {
        id: '2',
        employeeId: '2',
        employeeName: '李四',
        trainingName: '技能培训',
        trainingType: '在岗培训',
        trainingOrg: '技术部',
        trainingContent: '专业技能提升培训',
        startDate: '2024-02-01',
        endDate: '2024-02-05',
        trainingHours: 30,
        trainingResult: '良好',
        certificateNo: 'CERT002',
        createBy: 'admin',
        createTime: '2024-02-01 09:00:00',
        remark: '技能提升培训，表现优秀'
      },
      {
        id: '3',
        employeeId: '3',
        employeeName: '王五',
        trainingName: '管理培训',
        trainingType: '管理培训',
        trainingOrg: '人事部',
        trainingContent: '团队管理能力培训',
        startDate: '2024-03-01',
        endDate: '2024-03-10',
        trainingHours: 60,
        trainingResult: '合格',
        certificateNo: 'CERT003',
        createBy: 'admin',
        createTime: '2024-03-01 14:00:00',
        remark: '管理能力提升培训'
      }
    ]
  };

  export default defineComponent({
    name: 'HrTrainingRecord',
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
        id: '',
        selectedRows: [],
        selectedRowKeys: [],
        statistics: {
          total: 0,
          completed: 0,
          inProgress: 0,
          pending: 0
        },
        searchFormData: {
          trainingName: '',
          trainingType: '',
          trainingResult: undefined,
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
          { field: 'employeeName', title: '员工姓名', width: 100 },
          { field: 'trainingName', title: '培训名称', width: 150 },
          { field: 'trainingType', title: '培训类型', width: 120 },
          { field: 'trainingOrg', title: '培训机构', width: 120 },
          { field: 'startDate', title: '开始日期', width: 120 },
          { field: 'endDate', title: '结束日期', width: 120 },
          { field: 'trainingHours', title: '学时', width: 80 },
          { field: 'trainingResult', title: '结果', width: 100, slots: { default: 'trainingResult_default' } },
          { field: 'certificateNo', title: '证书编号', width: 120 },
          { field: 'remark', title: '备注', width: 150 },
          { field: 'createTime', title: '创建时间', width: 180 },
          { title: '操作', width: 200, fixed: 'right', slots: { default: 'action_default' } },
        ],
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'total',
          },
          ajax: {
            query: ({ page }) => {
              const params = {
                ...this.$utils.buildSortPageVo(page),
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
      this.loadStatistics();
    },
    methods: {
      // 加载统计数据
      async loadStatistics() {
        try {
          const res = await api.getStatistics();
          if (res) {
            this.statistics = {
              total: res.total || 0,
              completed: res.completed || 0,
              inProgress: res.inProgress || 0,
              pending: res.pending || 0
            };
          }
        } catch (error) {
          console.error('加载统计失败:', error);
        }
      },

      getResultColor(result) {
        const colors = {
          '优秀': 'green',
          '良好': 'blue',
          '合格': 'orange',
          '不合格': 'red'
        };
        return colors[result] || 'default';
      },
      
      getResultText(result) {
        return result || '-';
      },
      
      calculateStatistics(data) {
        this.statistics.total = data.length;
        this.statistics.completed = data.filter(item => item.trainingResult).length;
        this.statistics.inProgress = data.filter(item => !item.trainingResult && new Date(item.endDate) < new Date()).length;
        this.statistics.pending = data.filter(item => !item.trainingResult && new Date(item.startDate) > new Date()).length;
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
          trainingName: this.searchFormData.trainingName,
          trainingType: this.searchFormData.trainingType,
          trainingResult: this.searchFormData.trainingResult,
        };
      },
      
      reset() {
        this.searchFormData = {
          trainingName: '',
          trainingType: '',
          trainingResult: undefined,
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
      
      handleDelete(record) {
        this.$confirm({
          title: '确定要删除该培训记录吗？',
          onOk: async () => {
            try {
              await api.del(record.id);
              this.$message.success('删除成功');
              this.search();
              this.loadStatistics();
            } catch (error) {
              // 错误已在拦截器处理
            }
          }
        });
      },
      
      handleBatchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择要删除的记录');
          return;
        }
        
        this.$confirm({
          title: '确定要批量删除所选培训记录吗？',
          onOk: async () => {
            try {
              await api.batchDelete(this.selectedRowKeys);
              this.$message.success(`成功删除 ${this.selectedRowKeys.length} 条记录`);
              this.selectedRowKeys = [];
              this.search();
              this.loadStatistics();
            } catch (error) {
              // 错误已在拦截器处理
            }
          }
        });
      },
      
      handleExport() {
        const params = this.buildSearchFormData();
        api.exportList(params).then((res) => {
          if (res) {
            const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `培训记录_${new Date().toLocaleDateString()}.xlsx`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            this.$message.success('导出成功');
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
      
      &.stat-completed {
        color: #52c41a;
      }
      
      &.stat-inprogress {
        color: #faad14;
      }
      
      &.stat-pending {
        color: #999;
      }
    }
    
    .stat-label {
      font-size: 14px;
      color: #666;
    }
  }
</style>
