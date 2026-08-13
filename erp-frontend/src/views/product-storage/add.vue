<template>
  <a-modal v-model:open="visible" :mask-closable="false" width="40%" title="新增成品出入库" :style="{ top: '20px' }" :footer="null">
    <div v-if="visible" v-permission="['product:storage']" v-loading="loading">
      <a-form ref="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }" :model="formData" :rules="rules">
        <a-form-item label="客户名称" name="clientName">
          <a-input v-model:value.trim="formData.clientName" allow-clear />
        </a-form-item>
        <a-form-item label="产品名称" name="productName">
          <a-input v-model:value.trim="formData.productName" allow-clear />
        </a-form-item>
        <a-form-item label="件号" name="productCode">
          <a-input v-model:value.trim="formData.productCode" allow-clear />
        </a-form-item>
        <a-form-item label="序列号" name="serialNumber">
          <a-input v-model:value.trim="formData.serialNumber" allow-clear />
        </a-form-item>
        <a-form-item label="入库时间" name="storageTime">
          <a-date-picker v-model:value="formData.storageTime" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" :allow-clear="true" />
        </a-form-item>
        <a-form-item label="入库单号" name="storageTrackingNumber">
          <a-input v-model:value.trim="formData.storageTrackingNumber" allow-clear />
        </a-form-item>
        <a-form-item label="出库时间" name="deliveryTime">
          <a-date-picker v-model:value="formData.deliveryTime" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" :allow-clear="true" />
        </a-form-item>
        <a-form-item label="出库原因" name="deliveryReason">
          <a-input v-model:value.trim="formData.deliveryReason" allow-clear />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value.trim="formData.description" />
        </a-form-item>
        <div class="form-modal-footer">
          <a-space>
            <a-button type="primary" :loading="loading" html-type="submit" @click="submit">保存</a-button>
            <a-button :loading="loading" @click="closeDialog">取消</a-button>
          </a-space>
        </div>
      </a-form>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/shkb/product-storage';

  export default defineComponent({
    name: 'ShkbProductStorageAdd',
    data() {
      return {
        visible: false,
        loading: false,
        formData: {},
        rules: {
          clientName: [{ required: true, message: '请输入客户名称' }],
          productName: [{ required: true, message: '请输入产品名称' }],
          productCode: [{ required: true, message: '请输入件号' }],
        },
      };
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => this.open());
      },
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
      initFormData() {
        this.formData = {
          clientName: '',
          productName: '',
          productCode: '',
          serialNumber: '',
          storageTime: null,
          deliveryTime: null,
          description: '',
          storageTrackingNumber: '',
          deliveryReason: '',
        };
      },
      submit() {
        this.$refs.form.validate().then((valid) => {
          if (!valid) return;
          this.loading = true;
          const params = Object.assign({}, this.formData);
          // 转换日期为字符串（到天，补零点时间，兼容后端 LocalDateTime）
          if (this.formData.storageTime) {
            params.storageTime = this.formData.storageTime + ' 00:00:00';
          }
          if (this.formData.deliveryTime) {
            params.deliveryTime = this.formData.deliveryTime + ' 00:00:00';
          }
          api
            .create(params)
            .then(() => {
              this.$msg.createSuccess('新增成功！');
              this.initFormData();
              this.$emit('confirm');
              this.visible = false;
            })
            .finally(() => (this.loading = false));
        });
      },
      open() {
        this.initFormData();
      },
    },
  });
</script>
<style scoped></style>
