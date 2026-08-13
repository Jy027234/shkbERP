<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="60%"
    title="创建发料单"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['material:order']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-form-item label="发料申请单" name="materialApplyId">
          <material-apply-selector
            v-model:value="formData.materialApplyId"
            :only-approved="true"
            @update:value="materialApplyChange"
          />
        </a-form-item>
        <a-form-item label="出库仓库" name="scId">
          <store-center-selector v-model:value="formData.scId" />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea
            v-model:value.trim="formData.description"
            maxlength="200"
            placeholder="最多可输入200字"
          />
        </a-form-item>
      </a-form>

      <div class="form-modal-footer">
        <a-space>
          <a-button type="primary" :loading="loading" @click="submit">保存</a-button>
          <a-button :loading="loading" @click="closeDialog">取消</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/material/order';
  import MaterialApplySelector from '@/components/Selector/src/MaterialApplySelector.vue';
  import StoreCenterSelector from '@/components/Selector/src/StoreCenterSelector.vue';

  export default defineComponent({
    components: {
      MaterialApplySelector,
      StoreCenterSelector,
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 表单校验规则
        rules: {
          materialApplyId: [{ required: true, message: '请选择发料申请单' }],
          scId: [{ required: true, message: '请选择仓库' }],
        },
      };
    },
    computed: {},
    created() {
      // 初始化表单数据
      this.initFormData();
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        this.visible = true;

        this.$nextTick(() => this.open());
      },
      // 关闭对话框
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
      // 初始化
      initFormData() {
        this.formData = {
          materialApplyId: '',
          scId: '',
          description: '',
        };
      },
      // 页面显示时触发
      open() {
        // 初始化数据
        this.initFormData();
      },
      // 发料申请单变更
      materialApplyChange(value) {
        // 可以在这里根据申请单自动设置仓库等信息
      },
      // 提交表单
      submit() {
        this.$refs.form
          .validate()
          .then(() => {
            this.loading = true;
            return api.createFromApply({
              materialApplyId: this.formData.materialApplyId,
              scId: this.formData.scId,
              description: this.formData.description,
            });
          })
          .then(() => {
            this.$msg.createSuccess('发料单创建成功');
            this.closeDialog();
            this.$emit('confirm');
          })
          .catch(() => {
            // 校验失败或请求异常时无需额外处理，保持表单提示
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>
