<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="技术评估"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['maintenance:contract-task']" v-loading="loading">
      <!-- 基础信息展示 -->
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="合同编号" :span="2">
          {{ formData.contractCode }}
        </a-descriptions-item>
        <a-descriptions-item label="合同名称" :span="2">
          {{ formData.contractName }}
        </a-descriptions-item>
        <a-descriptions-item label="客户" :span="2">
          {{ formData.customerName }}
        </a-descriptions-item>
        <a-descriptions-item label="机型" :span="2">
          {{ formData.machineTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="件号" :span="2">
          {{ formData.partNumber }}
        </a-descriptions-item>
      </a-descriptions>

      <!-- 鉴定表单 -->
      <a-form 
        :model="appraisalForm" 
        :label-col="{ span: 4 }" 
        :wrapper-col="{ span: 20 }"
        class="appraisal-form"
      >
        <a-form-item label="评估结果" required>
          <a-radio-group v-model:value="appraisalForm.approved">
            <a-radio :value="true">通过</a-radio>
            <a-radio :value="false">不通过</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea 
            v-model:value="appraisalForm.description" 
            placeholder="请输入备注信息" 
            :rows="4" 
          />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 4, span: 20 }">
          <a-space>
            <a-button @click="closeDialog">取消</a-button>
            <a-button type="primary" :loading="submitting" @click="submit">确定</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/maintenance/contract-task/index.ts';
  import { message } from 'ant-design-vue';

  export default defineComponent({
    // 使用组件
    components: {},
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 是否正在提交
        submitting: false,
        // 表单数据
        formData: {},
        // 鉴定表单
        appraisalForm: {
          approved: true,
          description: '',
        },
      };
    },
    created() {
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
      // 初始化表单数据
      initFormData() {
        this.formData = {
          id: '',
          contractCode: '',
          contractName: '',
          customerName: '',
          machineTypeName: '',
          partNumber: '',
        };
        
        // 重置鉴定表单
        this.appraisalForm = {
          approved: true,
          description: '',
        };
      },
      // 页面显示时触发
      open() {
        // 初始化数据
        this.initFormData();

        // 查询数据
        this.loadFormData();
      },
      // 查询数据
      loadFormData() {
        this.loading = true;
        api
          .get(this.id)
          .then((data) => {
            this.formData = data;
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 提交鉴定
      submit() {
        if (this.submitting) {
          return;
        }
        
        // 构建提交数据
        const data = {
          id: this.id,
          approved: this.appraisalForm.approved,
          description: this.appraisalForm.description,
        };
        
        this.submitting = true;
        api.offlineAppraisal(data)
          .then(() => {
            message.success('技术评估成功');
            this.closeDialog();
            this.$emit('confirm');
          })
          .catch((error) => {
            console.error('技术评估失败:', error);
            message.error('技术评估失败: ' + (error.message || '未知错误'));
          })
          .finally(() => {
            this.submitting = false;
          });
      },
    },
  });
</script>
<style scoped>
.appraisal-form {
  margin-top: 20px;
}
</style>
