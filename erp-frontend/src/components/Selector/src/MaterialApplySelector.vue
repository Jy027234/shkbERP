<template>
  <div>
    <dialog-table
      ref="selector"
      :value="value"
      :request="getList"
      :load="getLoad"
      :dialog-width="'80%'"
      :column-option="{ label: 'applyCode', value: 'id' }"
      :request-params="_requestParams"
      :table-column="[
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
          // 直接使用vxe-grid的formatter将布尔值转换为“是/否”
          formatter: ({ cellValue }) => (cellValue ? '已创建' : '未创建'),
        },
      ]"
      @update:value="(val) => $emit('update:value', val)"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.applyCode)" label="发料申请编号">
              <a-input v-model:value="searchParams.applyCode" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.contractCode)" label="合同编号">
              <a-input v-model:value="searchParams.contractCode" />
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
              >
                <a-select-option :value="true">已创建</a-select-option>
                <a-select-option :value="false">未创建</a-select-option>
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
  import * as api from '@/api/material/apply';
  import moment from 'moment';

  export default defineComponent({
    name: 'MaterialApplySelector',
    components: { SearchOutlined },
    props: {
      value: {
        type: [String, Array],
        default: '',
      },
      requestParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
      onlyApproved: {
        type: Boolean,
        default: true,
      },
    },
    emits: ['update:value'],
    watch: {
      value: {
        handler(newVal) {
          if (this.$refs.selector) {
            this.$refs.selector.$props.value = newVal;
          }
        },
        immediate: true,
      },
    },
    data() {
      return {
        searchParams: {
          applyCode: '',
          contractCode: '',
          createTimeStart: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMinTime(moment().subtract(1, 'M')),
          ),
          createTimeEnd: this.$utils.formatDateTime(this.$utils.getDateTimeWithMaxTime(moment())),
          approvalStatus: this.onlyApproved ? 1 : undefined, // 1=审核通过
          // 默认只查询“未创建发料单”的申请
          hasMaterialOrder: false,
        },
      };
    },
    computed: {
      _requestParams() {
        const params = {};
        // 默认：若要求仅已过审，强制加上审批通过
        if (this.onlyApproved) {
          params.approvalStatus = 1;
        }
        // 合并搜索参数
        Object.assign(params, this.searchParams);
        // 最后覆盖传入参数
        Object.assign(params, this.requestParams);
        return params;
      },
    },
    methods: {
      /**
       * 获取发料申请列表
       */
      getList(params) {
        return api.query({
          ...params,
          ...this._requestParams,
        });
      },
      /**
       * 根据ID加载详情
       */
      getLoad(ids) {
        // 由于没有专门的加载详情接口，我们可以通过查询接口来模拟
        // 这里直接返回一个Promise，包含根据ID匹配的数据
        // 注意：加载详情时不应该包含hasMaterialOrder条件，因为需要加载所有ID的记录
        return api.query({
          pageIndex: 1,
          pageSize: 100,
          approvalStatus: this.onlyApproved ? 1 : undefined,
          ...this.searchParams,
          hasMaterialOrder: undefined, // 清除hasMaterialOrder条件
        }).then(res => {
          if (res && res.datas) {
            return res.datas.filter(item => ids.includes(item.id));
          }
          return [];
        });
      },
    },
  });
</script>

<style lang="less"></style>
