<template>
  <div>
    <div v-permission="['hr:certificate:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="statistics-row">
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-total">{{ statistics.total }}</div>
              <div class="stat-label">证书总数</div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <div class="stat-value stat-valid">{{ statistics.valid }}</div>
              <div class="stat-label">有效证书</div>
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
              <div class="stat-label">已过期</div>
            </a-card>
          </a-col>
        </a-row>
        <!-- 数据列表 -->
        <vxe-grid
          id="Certificate"
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
          :pager-config="{
            layouts: ['PrevJump', 'PrevPage', 'Jump', 'PageCount', 'NextPage', 'NextJump', 'Sizes', 'Total'],
            pageSizes: [10, 20, 50, 100],
            autoHidden: false,
            alwaysShow: true
          }"
          :sort-config="{ remote: true }"
          :loading="loading"
          height="500px"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
        >
          <template #form>
            <j-border>
              <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                <j-form-item label="证书类型">
                  <a-input v-model:value="searchFormData.certificateType" allow-clear />
                </j-form-item>
                <j-form-item label="证书名称">
                  <a-input v-model:value="searchFormData.certificateName" allow-clear />
                </j-form-item>
                <j-form-item label="状态">
                  <a-select
                    v-model:value="searchFormData.status"
                    placeholder="请选择状态"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option :value="1">有效</a-select-option>
                    <a-select-option :value="0">过期</a-select-option>
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
                v-permission="['hr:certificate:create']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >新增证书</a-button
              >
              <a-button
                v-permission="['hr:certificate:query']"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
                >导出Excel</a-button
              >
              <a-button
                v-permission="['hr:certificate:delete']"
                danger
                :disabled="!selectedRowKeys.length"
                :icon="h(DeleteOutlined)"
                @click="handleBatchDelete"
                >批量删除</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="getStatusColor(row.status)">
              {{ getStatusText(row.status) }}
            </a-tag>
            <a-tag v-if="row.expiring" color="warning">即将过期</a-tag>
          </template>

          <!-- 有效期 列自定义内容 -->
          <template #validPeriod_default="{ row }">
            <span v-if="row.validEndDate">{{ row.validStartDate }} 至 {{ row.validEndDate }}</span>
            <span v-else-if="row.validStartDate">{{ row.validStartDate }} 起长期有效</span>
            <span v-else>长期有效</span>
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
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    DeleteOutlined,
    DownloadOutlined
  } from '@ant-design/icons-vue';
  import * as api from '@/api/hr/certificate';
  
  // 模拟数据
  const mockData = {
    total: 3,
    pageIndex: 1,
    pageSize: 10,
    datas: [
      {
        id: '1',
        employeeId: '1',
        employeeName: '张三',
        certificateType: '职业资格证书',
        certificateName: '电工证',
        certificateNo: 'CERT001',
        issueOrg: '人力资源和社会保障局',
        issueDate: '2024-01-15',
        validStartDate: '2024-01-15',
        validEndDate: '2027-01-14',
        status: 1,
        expiring: false,
        description: ''
      },
      {
        id: '2',
        employeeId: '2',
        employeeName: '李四',
        certificateType: '技能等级证书',
        certificateName: '焊工证',
        certificateNo: 'CERT002',
        issueOrg: '职业技能鉴定中心',
        issueDate: '2024-03-01',
        validStartDate: '2024-03-01',
        validEndDate: '2025-03-01',
        status: 1,
        expiring: true,
        description: ''
      },
      {
        id: '3',
        employeeId: '3',
        employeeName: '王五',
        certificateType: '操作证书',
        certificateName: '叉车驾驶证',
        certificateNo: 'CERT003',
        issueOrg: '质量监督局',
        issueDate: '2023-06-01',
        validStartDate: '2023-06-01',
        validEndDate: '2024-06-01',
        status: 0,
        expiring: false,
        description: '已过期'
      }
    ]
  };

  export default defineComponent({
    name: 'HrCertificate',
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
        // 当前行数据
        id: '',
        // 选中的行数据
        selectedRows: [],
        // 选中的证书ID列表
        selectedRowKeys: [],
        // 统计数据
        statistics: {
          total: 0,
          valid: 0,
          expiring: 0,
          expired: 0
        },
        // 查询列表的查询条件
        searchFormData: {
          certificateType: '',
          certificateName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        },
        // 工具栏配置
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'employeeName', title: '员工姓名', width: 100 },
          { field: 'certificateType', title: '证书类型', width: 120 },
          { field: 'certificateName', title: '证书名称', width: 180 },
          { field: 'certificateNo', title: '证书编号', width: 120 },
          { field: 'issueOrg', title: '发证机构', width: 120 },
          { field: 'validPeriod', title: '有效期', width: 200, slots: { default: 'validPeriod_default' } },
          { field: 'status', title: '状态', width: 120, slots: { default: 'status_default' } },
          { field: 'description', title: '备注', minWidth: 150 },
          { title: '操作', width: 180, fixed: 'right', slots: { default: 'action_default' } },
        ],
        // 请求接口配置
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
      // 获取状态颜色
      getStatusColor(status) {
        const colors = {
          0: 'default',
          1: 'success'
        };
        return colors[status] || 'default';
      },

      // 获取状态文本
      getStatusText(status) {
        const texts = {
          0: '过期',
          1: '有效'
        };
        return texts[status] || '';
      },

      // 加载统计数据
      async loadStatistics() {
        try {
          const res = await api.getStatistics();
          if (res) {
            this.statistics = {
              total: res.total || 0,
              valid: res.valid || 0,
              expiring: res.expiring || 0,
              expired: res.expired || 0
            };
          }
        } catch (error) {
          console.error('加载统计失败:', error);
        }
      },
      
      // 计算统计数据
      calculateStatistics(data) {
        this.statistics.total = data.length;
        this.statistics.valid = data.filter(item => item.status === 1 && !item.expiring).length;
        this.statistics.expiring = data.filter(item => item.expiring).length;
        this.statistics.expired = data.filter(item => item.status === 0).length;
      },
      
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
        // 同时刷新统计数据
        this.loadStatistics();
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
          keyword: this.searchFormData.keyword,
          certificateType: this.searchFormData.certificateType,
          certificateName: this.searchFormData.certificateName,
          status: this.searchFormData.status,
        };
      },
      
      // 重置筛选项为默认值
      reset() {
        this.searchFormData = {
          keyword: '',
          certificateType: '',
          certificateName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        };
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
            permission: ['hr:certificate:update'],
            label: '编辑',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
          {
            permission: ['hr:certificate:delete'],
            label: '删除',
            type: 'danger',
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
      
      // 新增证书
      handleAdd() {
        this.$nextTick(() => this.$refs.addDialog.openDialog());
      },
      
      // 删除证书
      handleDelete(row) {
        this.$confirm({
          title: '确定要删除该证书吗？',
          onOk: async () => {
            try {
              await api.del(row.id);
              this.$message.success('删除成功');
              this.search();
            } catch (error) {
              // 错误已在拦截器处理
            }
          }
        });
      },
      
      // 批量删除
      handleBatchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择要删除的证书');
          return;
        }
        
        this.$confirm({
          title: '确定要删除所选证书吗？',
          onOk: async () => {
            try {
              await api.batchDelete(this.selectedRowKeys);
              this.$message.success(`删除成功：${this.selectedRowKeys.length}条`);
              this.selectedRowKeys = [];
              this.search();
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
            link.setAttribute('download', `证书列表_${new Date().toLocaleDateString()}.xlsx`);
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
</style>