<template>
  <div>
    <dialog-table
      ref="selector"
      :value="modelValue"
      :disabled="disabled || $utils.isEmpty(scId)"
      :placeholder="title || '选择发料单'"
      :request="getList"
      :load="emptyLoad"
      :dialog-width="'80%'"
      :request-params="requestParams"
      :column-option="{label: 'code', value: 'id'}"
      @update:value="onUpdateValue"
      @input-row="handleRowChange"
      :table-column="[
        { field: 'code', title: '发料单号', minWidth: 180 },
        { field: 'contractCode', title: '合同编号', minWidth: 150 },
        { field: 'scName', title: '仓库名称', minWidth: 120 },
        { field: 'totalNum', title: '航材数量', minWidth: 100, align: 'right' },
        { field: 'totalOutNum', title: '已出库数量', minWidth: 100, align: 'right' },
        {
          field: 'remainOutNum',
          title: '剩余出库数量',
          minWidth: 120,
          align: 'right',
          formatter: ({ row }) => {
            return row.totalNum - (row.totalOutNum || 0);
          }
        },
        { field: 'createTime', title: '操作时间', minWidth: 150 },
        { field: 'createBy', title: '操作人', minWidth: 100 },
      ]"
      @confirm="handleConfirm"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item label="发料单号">
              <a-input v-model:value="searchParams.code" />
            </j-form-item>
            <j-form-item label="合同编号">
              <a-input v-model:value="searchParams.contractCode" />
            </j-form-item>
            <j-form-item label="操作人">
              <user-selector v-model:value="searchParams.createBy" />
            </j-form-item>
            <j-form-item label="操作日期" :content-nest="false">
              <div class="date-range-container">
                <a-date-picker
                  v-model:value="searchParams.createTimeStart"
                  placeholder=""
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD 00:00:00"
                />
                <span class="date-split">至</span>
                <a-date-picker
                  v-model:value="searchParams.createTimeEnd"
                  placeholder=""
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD 23:59:59"
                />
              </div>
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
            查询
          </a-button>
        </a-space>
      </template>
    </dialog-table>
  </div>
</template>

<script>
  import { defineComponent } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/material/order';
  import moment from 'moment';

  export default defineComponent({
    name: 'MaterialOrderSelectorForOutSheet',
    components: { SearchOutlined },
    props: {
      modelValue: {
        type: String,
        default: '',
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
        default: '',
      },
      scId: {
        type: String,
        default: '',
      },
    },
    emits: ['update:modelValue', 'select'],
    data() {
      return {
        loading: false,
        searchParams: {
          code: '',
          createBy: '',
          contractCode: '',
          createTimeStart: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMinTime(moment().subtract(1, 'M')),
          ),
          createTimeEnd: this.$utils.formatDateTime(this.$utils.getDateTimeWithMaxTime(moment())),
        },
      };
    },
    computed: {
      requestParams() {
        // 构建请求参数，确保必要的参数不被覆盖
        return {
          ...this.searchParams,
          scId: this.scId, // 使用传入的仓库ID
          isOutFinish: false, // 只查询未完成出库的发料单
        };
      },
    },
    methods: {

      /**
       * 获取发料单列表
       * @param {Object} params - 分页参数
       * @returns {Promise} - 返回发料单列表数据
       */
      getList(params) {
        this.loading = true;
        // 合并分页参数和请求参数
        return api.query({
          ...params,
          ...this.requestParams,
        }).finally(() => {
          this.loading = false;
        });
      },
      
      /**
       * 空的load函数，仅用于在DialogTable组件中防止报错
       * @param {Array} ids - ID列表
       * @returns {Promise} - 返回空数组
       */
      emptyLoad() {
        // 返回空数组的Promise，避免加载详情
        return Promise.resolve([]);
      },
      

      /**
       * 处理DialogTable的update:value事件
       * @param {string} value - 选中的发料单ID
       */
      onUpdateValue(value) {
        this.$emit('update:modelValue', value);
        // 仅发送ID，不加载详情，由父组件加载详情
        this.$emit('select', value ? { id: value } : null);
      },

      /**
       * 处理行选中事件
       * @param {Array} rows - 选中的行数据
       */
      handleRowChange(rows) {
        // 仅返回ID和编号，不返回完整对象
        if (rows && rows.length > 0) {
          const { id, code } = rows[0];
          this.$emit('select', { id, code });
        } else {
          this.$emit('select', null);
        }
      },
    },
  });
</script>

<style lang="less" scoped>
.selector-container {
  display: flex;
  align-items: center;
}
</style>
