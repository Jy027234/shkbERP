<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="80%"
    title="批次明细"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <vxe-grid
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        height="500"
        :data="innerDetails"
        :columns="tableColumn"
        :radio-config="{ trigger: 'row' }"
      >
      </vxe-grid>

      <div class="form-modal-footer">
        <a-space>
          <a-button type="primary" @click="confirmSelect">确定</a-button>
          <a-button @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as outApi from '@/api/material/out';

  export default defineComponent({
    emits: ['confirm', 'close'],
    props: {
      details: {
        type: Array,
        default: () => [],
      },
      scId: {
        type: String,
        default: '',
      },
      productId: {
        type: String,
        default: '',
      },
      // 回显用：已选择的批次ID
      selectedId: {
        type: [String, Number],
        default: '',
      },
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 列配置
        tableColumn: [
          { type: 'radio', width: 50 },
          { field: 'batchNumber', title: '批次号', width: 140 },
          { field: 'quantity', title: '数量', width: 100, align: 'right' },
          { field: 'scName', title: '仓库', width: 140 },
          { field: 'productName', title: '商品', minWidth: 160, showOverflow: true },
          { field: 'partNumberCode', title: '件号', width: 140 },
          { field: 'machineType', title: '机型', width: 120 },
          { field: 'productionDate', title: '生产日期', width: 120, formatter: ({ cellValue }) => (cellValue ? cellValue : '') },
          { field: 'expiryDate', title: '失效日期', width: 120, formatter: ({ cellValue }) => (cellValue ? cellValue : '') },
        ],
        innerDetails: [],
      };
    },
    methods: {
      // 回显：根据 selectedId 勾选单选行
      applySelection() {
        const grid = this.$refs.grid;
        if (!grid) return;
        const id = this.selectedId !== undefined && this.selectedId !== null ? String(this.selectedId) : '';
        if (!id) {
          grid.clearRadioRow && grid.clearRadioRow();
          return;
        }
        const rows = (this.innerDetails || []).filter((r) => String(r.id) === id);
        if (rows.length) {
          grid.setRadioRow ? grid.setRadioRow(rows[0]) : null;
        }
      },
      // 打开对话框
      openDialog() {
        this.visible = true;
        this.loadData();
        this.$nextTick(() => this.applySelection());
      },
      // 关闭对话框
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
      // 加载数据：优先使用传入的 details；否则按条件查询
      loadData() {
        if (this.details && this.details.length > 0) {
          this.innerDetails = this.details;
          this.$nextTick(() => this.applySelection());
          return;
        }

        if (this.$utils.isEmpty(this.scId) || this.$utils.isEmpty(this.productId)) {
          this.innerDetails = [];
          return;
        }

        this.loading = true;
        outApi
          .getBatchStock(this.scId, this.productId)
          .then((res) => {
            // 接口期望返回批次库存列表
            this.innerDetails = res || [];
            this.$nextTick(() => this.applySelection());
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 确认选择（单选）
      confirmSelect() {
        const row = this.$refs.grid ? this.$refs.grid.getRadioRecord() : null;
        const result = row ? [row] : [];
        this.$emit('confirm', result);
        this.closeDialog();
      },
    },
    watch: {
      innerDetails: {
        handler() {
          this.$nextTick(() => this.applySelection());
        },
        deep: true,
      },
      selectedId() {
        this.$nextTick(() => this.applySelection());
      },
    },
  });
</script>
