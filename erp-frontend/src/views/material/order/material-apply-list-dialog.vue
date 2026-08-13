<template>
  <a-modal
    v-model:open="visible"
    title="发料申请单列表"
    :mask-closable="false"
    width="90%"
    :footer="null"
  >
    <div>
      <!-- 搜索条件 -->
      <j-border>
        <j-form>
          <j-form-item label="发料申请编号">
            <a-input v-model:value="searchParams.applyCode" allow-clear />
          </j-form-item>
          <j-form-item label="合同编号">
            <a-input v-model:value="searchParams.contractCode" allow-clear />
          </j-form-item>
          <j-form-item label="申请日期" :content-nest="false">
            <div class="date-range-container">
              <a-date-picker
                v-model:value="searchParams.createTimeStart"
                placeholder=""
                value-format="YYYY-MM-DD 00:00:00"
              />
              <span class="date-split">至</span>
              <a-date-picker
                v-model:value="searchParams.createTimeEnd"
                placeholder=""
                value-format="YYYY-MM-DD 23:59:59"
              />
            </div>
          </j-form-item>
          <j-form-item label="审批状态">
            <a-select
              v-model:value="searchParams.approvalStatus"
              placeholder="全部"
              allow-clear
              style="width: 100%"
            >
              <a-select-option :value="0">待审批</a-select-option>
              <a-select-option :value="1">审批通过</a-select-option>
              <a-select-option :value="2">审批不通过</a-select-option>
            </a-select>
          </j-form-item>
          <j-form-item label="发料单状态">
            <a-select
              v-model:value="searchParams.hasMaterialOrder"
              placeholder="全部"
              allow-clear
              style="width: 100%"
            >
              <a-select-option :value="true">已创建</a-select-option>
              <a-select-option :value="false">未创建</a-select-option>
            </a-select>
          </j-form-item>
          <a-space class="operator">
            <a-button type="primary" @click="reload">
              <template #icon>
                <SearchOutlined />
              </template>
              查询
            </a-button>
          </a-space>
        </j-form>
      </j-border>

      <!-- 数据列表 -->
      <vxe-grid
        ref="gridRef"
        resizable
        show-overflow
        keep-source
        row-id="id"
        :data="tableData"
        :loading="loading"
        :columns="tableColumn"
        height="500"
        :pager-config="pagerConfig"
        @page-change="handlePageChange"
      ></vxe-grid>
    </div>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/material/apply';
  import moment from 'moment';

  export default defineComponent({
    name: 'MaterialApplyListDialog',
    components: { SearchOutlined },
    setup() {
      return {};
    },
    data() {
      return {
        visible: false,
        loading: false,
        tableData: [],
        total: 0,
        pageIndex: 1,
        pageSize: 10,
        searchParams: {
          applyCode: '',
          contractCode: '',
          createTimeStart: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMinTime(moment().subtract(1, 'M')),
          ),
          createTimeEnd: this.$utils.formatDateTime(this.$utils.getDateTimeWithMaxTime(moment())),
          approvalStatus: 1,
          hasMaterialOrder: false,
        },
        tableColumn: [
          { type: 'seq', width: 60, title: '序号' },
          { field: 'applyCode', title: '发料申请编号', minWidth: 160 },
          { field: 'contractCode', title: '合同编号', minWidth: 160 },
          { field: 'machineTypeName', title: '机型', minWidth: 120 },
          { field: 'partNumberName', title: '件号', minWidth: 120 },
          { field: 'createTime', title: '申请时间', minWidth: 150 },
          { field: 'approvalStatusText', title: '审批状态', minWidth: 100 },
          {
            field: 'hasMaterialOrder',
            title: '发料单状态',
            minWidth: 140,
            formatter: ({ cellValue }) => (cellValue ? '已创建' : '未创建'),
          },
        ],
      };
    },
    computed: {
      pagerConfig() {
        return {
          total: this.total,
          pageSize: this.pageSize,
          currentPage: this.pageIndex,
          pageSizes: [10, 20, 50, 100],
        };
      },
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => {
          this.loadData();
        });
      },
      reload() {
        this.pageIndex = 1;
        this.loadData();
      },
      handlePageChange({ currentPage, pageSize }) {
        this.pageIndex = currentPage;
        this.pageSize = pageSize;
        this.loadData();
      },
      loadData() {
        this.loading = true;
        api
          .query({
            pageIndex: this.pageIndex,
            pageSize: this.pageSize,
            applyCode: this.searchParams.applyCode || undefined,
            contractCode: this.searchParams.contractCode || undefined,
            createTimeStart: this.searchParams.createTimeStart || undefined,
            createTimeEnd: this.searchParams.createTimeEnd || undefined,
            approvalStatus: this.searchParams.approvalStatus,
            hasMaterialOrder: this.searchParams.hasMaterialOrder,
          })
          .then((res) => {
            this.tableData = res.datas || [];
            this.total = res.totalCount || 0;
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>

<style scoped>
  .date-range-container {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .date-split {
    color: #909399;
  }
</style>
