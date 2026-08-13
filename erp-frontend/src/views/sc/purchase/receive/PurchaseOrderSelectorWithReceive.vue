<template>
  <div>
    <dialog-table
      ref="selector"
      :request="getList"
      :load="getLoad"
      :dialog-width="'80%'"
      :request-params="_requestParams"
      :option="{ label: 'code', value: 'id' }"
      :column-option="{ label: 'code', value: 'id' }"
      :table-column="[
        { field: 'code', title: '采购单号', width: 180 },
        { field: 'productCode', title: '航材件号', minWidth: 120 },
        { field: 'productName', title: '航材名称', minWidth: 180 },
        { field: 'statusName', title: '审批状态', minWidth: 100 },
        {
          field: 'received',
          title: '是否已创建收货单',
          minWidth: 120,
          formatter: ({ row }) => {
            return row.received === true ? '是' : '否';
          },
        },
        { field: 'scCode', title: '仓库编号', minWidth: 100 },
        { field: 'scName', title: '仓库名称', minWidth: 120 },
        { field: 'supplierCode', title: '供应商编号', minWidth: 100 },
        { field: 'supplierName', title: '供应商名称', minWidth: 120 },
        { field: 'createTime', title: '操作时间', minWidth: 150 },
        { field: 'createBy', title: '操作人', minWidth: 100 },
      ]"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="采购单号">
              <a-input v-model:value="searchParams.code" allow-clear />
            </j-form-item>
            <j-form-item label="仓库">
              <store-center-selector
                v-if="$utils.isEmpty(requestParams.scId)"
                v-model:value="searchParams.scId"
              />
            </j-form-item>
            <j-form-item label="供应商">
              <supplier-selector
                v-if="$utils.isEmpty(requestParams.supplierId)"
                v-model:value="searchParams.supplierId"
              />
            </j-form-item>
            <j-form-item label="操作人">
              <user-selector
                v-if="$utils.isEmpty(requestParams.createBy)"
                v-model:value="searchParams.createBy"
              />
            </j-form-item>
            <j-form-item label="操作日期" :content-nest="false" :span="12">
              <div class="date-range-container">
                <a-date-picker
                  v-model:value="searchParams.createStartTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 00:00:00"
                />
                <span class="date-split">至</span>
                <a-date-picker
                  v-model:value="searchParams.createEndTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 23:59:59"
                />
              </div>
            </j-form-item>
            <j-form-item label="审批状态">
              <a-select v-model:value="searchParams.status" placeholder="全部">
                <a-select-option :value="1">待审核</a-select-option>
                <a-select-option :value="2">已拒绝</a-select-option>
                <a-select-option :value="3">已通过</a-select-option>
              </a-select>
            </j-form-item>
            <j-form-item label="是否已创建收货单">
              <a-select v-model:value="searchParams.received" placeholder="全部">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </j-form-item>
          </j-form>
        </j-border>
      </template>
      <!-- 工具栏 -->
      <template #toolbar_buttons>
        <a-space class="operator">
          <a-button type="primary" @click="$refs.selector.search()">
            <template #icon>
              <SearchOutlined />
            </template>
            查询</a-button
          >
        </a-space>
      </template>
    </dialog-table>
  </div>
</template>

<script>
  import { defineComponent } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import Moment from 'moment';
  import * as api from '@/api/sc/purchase/order';

  export default defineComponent({
    name: 'PurchaseOrderSelectorWithReceive',
    components: { SearchOutlined },
    props: {
      requestParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    setup() {
      const moment = Moment;
      return {
        moment,
      };
    },
    data() {
      return {
        searchParams: {
          code: '',
          scId: '',
          supplierId: '',
          createBy: '',
          createStartTime: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMinTime(this.moment().subtract(1, 'M')),
          ),
          createEndTime: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMaxTime(this.moment()),
          ),
          status: 3,
          received: 0,
        },
      };
    },
    computed: {
      _requestParams() {
        return Object.assign({}, this.searchParams, { available: true }, this.requestParams);
      },
    },
    methods: {
      getList(params) {
        const reqParams = {
          ...params,
          code: params.code,
          scId: params.scId || '',
          supplierId: params.supplierId || '',
          createBy: params.createBy || '',
          createStartTime: params.createStartTime,
          createEndTime: params.createEndTime,
          status: params.status,
          received: params.received === undefined ? undefined : params.received === 1,
        };
        return api.querySelectorForReceive(reqParams);
      },
      getLoad(ids) {
        return api.loadWithReceive(ids);
      },
    },
  });
</script>

<style lang="less"></style>
