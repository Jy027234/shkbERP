<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="修改序列号"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['stock:product-serial:modify']" v-loading="spinning">
      <a-form
        ref="form"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
      >
        <a-form-item label="仓库">
          <span>{{ getInfo?.scName }}</span>
        </a-form-item>
        <a-form-item label="航材">
          <span>{{ getInfo?.productName }}</span>
        </a-form-item>
        <a-form-item label="原序列号">
          <span>{{ getInfo?.serialNumber }}</span>
        </a-form-item>
        <a-form-item label="新序列号" name="serialNumber" :rules="[{ required: true, message: '请输入新序列号' }]">
          <a-input v-model:value="formData.serialNumber" placeholder="请输入新序列号" />
        </a-form-item>
        <div class="form-modal-footer">
          <a-space>
            <a-button type="primary" :loading="loading" html-type="submit" @click="submit">确定</a-button>
            <a-button :loading="loading" @click="closeDialog">取消</a-button>
          </a-space>
        </div>
      </a-form>
    </div>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/sc/stock/product-stock-serial/index';

  export default defineComponent({
    name: 'UpdateProductStockSerialNumberDialog',
    emits: ['success', 'cancel', 'update:modelValue'],
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    data() {
      return {
        getInfo: null,
        loading: false,
        spinning: false,
        visible: false,
        formData: {
          serialNumber: null,
        },
      };
    },
    watch: {
      id() {
        if (this.visible && this.id) {
          this.loadInfo();
        }
      },
    },
    created() {},
    
    methods: {
      openDialog() {
        this.visible = true;
        // loadInfo 将通过 id 的 watcher 触发，避免重复请求
      },
      closeDialog() {
        this.visible = false;
        this.$emit('cancel');
      },

      async loadInfo() {
        this.spinning = true;
        try {
          const data = await api.get(this.id);
          this.getInfo = data;
          this.formData.serialNumber = '';
        } finally {
          this.spinning = false;
        }
      },
      async submit() {
        this.loading = true;
        try {
          const params = {
            id: this.id,
            serialNumber: this.formData.serialNumber,
          };

          await api.updateSerialNumber(params);
          this.$msg.createSuccessTip('修改成功！');
          this.$emit('success');
          this.closeDialog();
        } catch (e) {
          console.error(e);
        } finally {
          this.loading = false;
        }
      },
    },
  });
</script>

<style scoped></style>