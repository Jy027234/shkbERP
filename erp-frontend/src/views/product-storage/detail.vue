<template>
  <a-modal v-model:open="visible" :mask-closable="false" width="50%" title="成品出入库详情" :style="{ top: '20px' }" :footer="null">
    <div v-if="visible" v-permission="['product:storage']" v-loading="loading">
      <a-descriptions bordered :column="1" size="small">
        <a-descriptions-item label="客户名称">{{ formData.clientName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="产品名称">{{ formData.productName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="件号">{{ formData.productCode || '-' }}</a-descriptions-item>
        <a-descriptions-item label="序列号">{{ formData.serialNumber || '-' }}</a-descriptions-item>
        <a-descriptions-item label="入库时间">{{ $utils.formatDateTime(formData.storageTime) || '-' }}</a-descriptions-item>
        <a-descriptions-item label="出库时间">{{ $utils.formatDateTime(formData.deliveryTime) || '-' }}</a-descriptions-item>
        <a-descriptions-item label="入库单号">{{ formData.storageTrackingNumber || '-' }}</a-descriptions-item>
        <a-descriptions-item label="出库原因">{{ formData.deliveryReason || '-' }}</a-descriptions-item>
        <a-descriptions-item label="备注">{{ formData.description || '-' }}</a-descriptions-item>
      </a-descriptions>
      <div class="form-modal-footer" style="margin-top: 12px; text-align: right">
        <a-button @click="closeDialog">关闭</a-button>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/shkb/product-storage';

  export default defineComponent({
    name: 'ShkbProductStorageDetail',
    props: { id: { type: String, required: true } },
    data() {
      return { visible: false, loading: false, formData: {} };
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => this.open());
      },
      closeDialog() {
        this.visible = false;
      },
      open() {
        this.loadFormData();
      },
      loadFormData() {
        this.loading = true;
        api
          .get(this.id)
          .then((data) => {
            this.formData = data || {};
          })
          .finally(() => (this.loading = false));
      },
    },
  });
</script>
<style scoped></style>
