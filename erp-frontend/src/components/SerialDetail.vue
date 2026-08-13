<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="80%"
    title="序列号明细"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <!-- 已选序列号展示 -->
      <div v-if="isSerial && selectedRows.length" style="margin-bottom: 12px;">
        <span style="margin-right:8px;">已选序列号：</span>
        <a-space wrap>
          <a-tag
            v-for="row in selectedRows"
            :key="row.id"
            closable
            @close.prevent="toggleRowSelection(row, false)"
          >{{ row.serialNumber }}</a-tag>
        </a-space>
      </div>

      <vxe-grid
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        height="500"
        :data="details"
        :columns="tableColumn"
        :checkbox-config="isSerial ? { trigger: 'row' } : null"
        @checkbox-change="onCheckboxChange"
        @checkbox-all="onCheckboxChange"
      >
      </vxe-grid>

      <div class="form-modal-footer">
        <a-space>
          <a-button type="primary" v-if="isSerial" @click="handleConfirm">确定</a-button>
          <a-button @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';

  export default defineComponent({
    props: {
      details: {
        type: Array,
        default: () => [],
      },
      // 是否为序列号库存模式（为 true 开启多选）
      isSerial: {
        type: Boolean,
        default: true,
      },
      // 由父组件传入的已选序列号ID列表（可选，用于回显）
      selectedIds: {
        type: Array,
        default: () => [],
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
          // 多选列（通过 checkbox-config 启用，列仍需声明）
          { type: 'checkbox', width: 48, visible: this.isSerial },
          { field: 'serialNumber', title: '序列号', width: 160 },
          { field: 'batchNumber', title: '批次号', width: 140 },
          { field: 'scName', title: '仓库', width: 140 },
          { field: 'productName', title: '航材名称', minWidth: 160 },
          { field: 'partNumberCode', title: '件号', width: 150 },
          { field: 'machineType', title: '机型', width: 120 },
          { field: 'stockStatus', title: '在库状态', width: 100, formatter: ({ cellValue }) => (cellValue === 1 ? '在库' : '非在库') },
          { field: 'productionDate', title: '生产日期', width: 120 },
          { field: 'expiryDate', title: '失效日期', width: 120 },
        ],
        // 当前已选行
        selectedRows: [],
      };
    },
    methods: {
      // 行唯一键：使用接口返回的 id
      getRowKey(row) {
        return String(row && row.id);
      },
      // 根据 selectedIds 回显勾选
      applySelection() {
        if (!this.visible || !this.isSerial) return;
        const grid = this.$refs.grid;
        if (!grid) return;
        const ids = Array.isArray(this.selectedIds) ? this.selectedIds.map((v) => String(v)) : [];
        // 等待 grid 数据渲染稳定后再执行一次，避免时序导致未回显
        const doApply = () => {
          grid.clearCheckboxRow();
          if (!ids.length) {
            this.selectedRows = [];
            return;
          }
          // 支持根据 行id 或 序列号 进行匹配，提升回显兼容性
          const rows = (this.details || []).filter((r) => {
            const keyId = this.getRowKey(r);
            const keySn = r && r.serialNumber != null ? String(r.serialNumber) : '';
            return ids.includes(keyId) || (!!keySn && ids.includes(keySn));
          });
          if (rows.length) grid.setCheckboxRow(rows, true);
          this.selectedRows = grid.getCheckboxRecords();
        };
        // 执行两次以确保渲染后能正确回显
        doApply();
        this.$nextTick(() => doApply());
      },
      // 打开对话框
      openDialog() {
        this.visible = true;
        // 回显：根据父级传入的 selectedIds 勾选对应行
        this.$nextTick(() => {
          this.applySelection();
        });
      },
      // 关闭对话框
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
      // 切换某行的勾选状态
      toggleRowSelection(row, checked = undefined) {
        const grid = this.$refs.grid;
        if (!grid) return;
        const isChecked = typeof checked === 'boolean' ? checked : !grid.isCheckedByCheckboxRow(row);
        grid.setCheckboxRow(row, isChecked);
        this.selectedRows = grid.getCheckboxRecords();
      },
      // 确认选择
      handleConfirm() {
        const grid = this.$refs.grid;
        const rows = grid ? grid.getCheckboxRecords() : [];
        this.selectedRows = rows;
        const ids = rows.map(r => this.getRowKey(r));
        this.$emit('confirm', { ids, rows });
        // 选择后关闭弹窗
        this.closeDialog();
      },
      // checkbox 变化时同步已选
      onCheckboxChange() {
        const grid = this.$refs.grid;
        if (!grid) return;
        this.selectedRows = grid.getCheckboxRecords();
      },
    },
    watch: {
      // details 数据变化时重新回显（仅在弹窗可见时）
      details: {
        handler() {
          this.$nextTick(() => this.applySelection());
        },
        deep: true,
      },
      // 选中ID变化时重新回显（仅在弹窗可见时）
      selectedIds() {
        this.$nextTick(() => this.applySelection());
      },
    },
  });
</script>
