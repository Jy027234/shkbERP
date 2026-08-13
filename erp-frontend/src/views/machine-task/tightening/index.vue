<template>
  <div v-permission="['machine-task:tightening']">
    <page-wrapper content-full-height fixed-height>
      <!-- 数据列表 -->
      <vxe-grid
        id="Tightening"
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
        :loading="loading"
        height="auto"
      >
        <template #form>
          <j-border>
            <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="合同号">
                <a-input v-model:value="searchFormData.contractNo" allow-clear />
              </j-form-item>
              <j-form-item label="件号">
                <a-input v-model:value="searchFormData.partNo" allow-clear />
              </j-form-item>
              <j-form-item label="序列号">
                <a-input v-model:value="searchFormData.serialNumber" allow-clear />
              </j-form-item>
              <j-form-item label="状态">
                <a-select v-model:value="searchFormData.machineTaskStatus" placeholder="全部" allow-clear>
                  <a-select-option :value="0">待装配</a-select-option>
                  <a-select-option :value="1">已完成</a-select-option>
                </a-select>
              </j-form-item>
              <j-form-item label="任务类型">
                <a-select v-model:value="searchFormData.taskType" placeholder="全部" allow-clear>
                  <a-select-option :value="0">平台任务</a-select-option>
                  <a-select-option :value="1">线下任务</a-select-option>
                </a-select>
              </j-form-item>
              <j-form-item label="创建起">
                <a-date-picker v-model:value="searchFormData.createTimeStart" show-time value-format="YYYY-MM-DD HH:mm:ss" allow-clear />
              </j-form-item>
              <j-form-item label="创建止">
                <a-date-picker v-model:value="searchFormData.createTimeEnd" show-time value-format="YYYY-MM-DD HH:mm:ss" allow-clear />
              </j-form-item>
            </j-form>
          </j-border>
        </template>

        <!-- 状态 列自定义内容 -->
        <template #status_default="{ row }">
          <a-tag v-if="row.machineTaskStatus === 0" color="orange">待装配</a-tag>
          <a-tag v-else-if="row.machineTaskStatus === 1" color="green">已完成</a-tag>
          <span v-else>-</span>
        </template>

        <!-- 任务类型 列自定义内容 -->
        <template #taskType_default="{ row }">
          <a-tag v-if="row.taskType === 0" color="blue">平台任务</a-tag>
          <a-tag v-else-if="row.taskType === 1" color="purple">线下任务</a-tag>
          <span v-else>-</span>
        </template>

        <!-- 操作 列自定义内容 -->
        <template #action_default="{ row }">
          <table-action outside :actions="createActions(row)" />
        </template>

        <!-- 工具栏 自定义按钮槽位 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" @click="search">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
          </a-space>
        </template>
      </vxe-grid>
    </page-wrapper>
    <!-- 查看数据弹窗 -->
    <ReportDialog ref="reportDialog" v-model="showReport" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import {
    SearchOutlined,
    PlusOutlined,
    ThunderboltOutlined,
    SettingOutlined,
    CheckOutlined,
    StopOutlined,
    DownOutlined,
    CloudUploadOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/machine-task/tightening';
  import ReportDialog from './components/ReportDialog.vue';

  export default defineComponent({
    name: 'MachineTaskTightening',
    components: {
      DownOutlined,
      ReportDialog,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        ThunderboltOutlined,
        SettingOutlined,
        CheckOutlined,
        StopOutlined,
        CloudUploadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        ids: [],
        // 查询列表的查询条件
        searchFormData: {
          contractNo: '',
          partNo: '',
          serialNumber: '',
          machineTaskStatus: undefined,
          taskType: undefined,
          createTimeStart: undefined,
          createTimeEnd: undefined,
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
          { type: 'checkbox', width: 45 },
          { field: 'taskId', title: ' 任务ID', width: 220 },
          { field: 'contractNo', title: '合同号', width: 180 },
          { field: 'partNo', title: '件号', minWidth: 160 },
          { field: 'serialNo', title: '序列号', minWidth: 180 },
          { field: 'taskType', title: '任务类型', width: 100, slots: { default: 'taskType_default' } },
          { field: 'machineTaskStatus', title: '状态', width: 100, slots: { default: 'status_default' } },
          { field: 'createTime', title: '创建时间', width: 180 },
          { field: 'reportTime', title: '上报时间', width: 180 },
          { title: '操作', width: 140, fixed: 'right', slots: { default: 'action_default' } },
        ],
        // 请求接口配置
        proxyConfig: {
          props: {
            // 响应结果列表字段
            result: 'datas',
            // 响应结果总条数字段
            total: 'totalCount',
          },
          ajax: {
            // 查询接口
            query: ({ page, sorts }) => {
              return api.query(this.buildQueryParams(page, sorts));
            },
          },
        },
        batchHandleDatas: [],
        showReport: false,
      };
    },
    created() {},
    methods: {
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        return {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
      },
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        return {
          ...this.searchFormData,
        };
      },
      handleCommand({ key }) {
        if (key === 'batchEnable') {
          this.batchEnable();
        } else if (key === 'batchUnable') {
          this.batchUnable();
        }
      },
      createActions(row) {
        return [
          {
            label: '查看数据',
            onClick: () => {
              this.loading = true;
              api
                .detail(row.id)
                .then((res) => {
                  const payload = res?.reportData;
                  if (!payload) {
                    this.$msg.createWarning('该任务暂无上报数据');
                    return;
                  }
                  this.showReport = true;
                  this.$nextTick(() => this.$refs.reportDialog.setReportData(payload));
                })
                .finally(() => {
                  this.loading = false;
                });
            },
          },
        ];
      },
    },
  });
</script>
<style scoped></style>
