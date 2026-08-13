<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="600px"
    title="编辑授权项目"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:authorization:update']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-col="80px"
        layout="vertical"
      >
        <a-form-item label="岗位" field="projectName">
          <a-input v-model:value="formData.projectName" placeholder="请输入岗位" />
        </a-form-item>
        
        <a-form-item label="授权项目/限制" field="authorizationItem">
          <a-textarea v-model:value="formData.authorizationItem" placeholder="请输入授权项目/限制" :rows="3" />
        </a-form-item>
        
        <a-form-item label="资质要求" field="qualificationRequirement">
          <a-textarea v-model:value="formData.qualificationRequirement" placeholder="请输入资质要求" :rows="3" />
        </a-form-item>
        
        <a-form-item label="培训要求" field="trainingRequirement">
          <a-textarea v-model:value="formData.trainingRequirement" placeholder="请输入培训要求" :rows="3" />
        </a-form-item>
        
        <a-form-item label="有效期" required>
          <a-input-group compact>
            <a-input-number v-model:value="formData.validityPeriod" :min="1" style="width: 60%" />
            <a-select v-model:value="formData.validityUnit" style="width: 40%">
              <a-select-option value="month">个月</a-select-option>
              <a-select-option value="year">年</a-select-option>
            </a-select>
          </a-input-group>
        </a-form-item>
        
        <a-form-item label="备注" field="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入备注" :rows="3" />
        </a-form-item>
        
        <a-form-item label="状态" field="status">
          <a-select v-model:value="formData.status" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      
      <div class="modal-footer">
        <a-button @click="handleCancel">取消</a-button>
        <a-button type="primary" @click="handleSubmit">提交</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/hr/authorization-project';

  const mockDataMap = {
    '1': {
      id: '1',
      projectName: '维修工程师授权',
      projectType: '岗位授权',
      validityPeriod: 12,
      validityUnit: 'month',
      description: '维修工程师岗位授权项目'
    },
    '2': {
      id: '2',
      projectName: '设备操作授权',
      projectType: '设备授权',
      validityPeriod: 6,
      validityUnit: 'month',
      description: '设备操作授权项目'
    },
    '3': {
      id: '3',
      projectName: '高空作业授权',
      projectType: '作业授权',
      validityPeriod: 1,
      validityUnit: 'year',
      description: '高空作业授权项目'
    }
  };

  export default defineComponent({
    name: 'HrAuthorizationProjectModify',
    props: {
      id: {
        type: String,
        default: ''
      }
    },
    emits: ['confirm'],
    setup(_, { emit }) {
      return {
        emit,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        formData: {
          projectName: '',
          authorizationItem: '',
          qualificationRequirement: '',
          trainingRequirement: '',
          validityPeriod: 12,
          validityUnit: 'month',
          description: '',
          status: 1,
        },
        rules: {
          projectName: [
            { required: true, message: '请输入岗位', trigger: 'blur' },
          ],
          status: [
            { required: true, message: '请选择状态', trigger: 'change' },
          ],
        },
      };
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => {
          this.loadFormData();
        });
      },
      async loadFormData() {
        this.loading = true;
        try {
          const data = await api.get(this.id) || {};
          this.formData = {
            projectName: data.projectName || '',
            authorizationItem: data.authorizationItem || '',
            qualificationRequirement: data.qualificationRequirement || '',
            trainingRequirement: data.trainingRequirement || '',
            validityPeriod: data.validityPeriod || 12,
            validityUnit: data.validityUnit || 'month',
            description: data.description || '',
            status: data.status !== undefined ? data.status : 1,
          };
        } catch (error) {
          // 错误已在拦截器处理
        } finally {
          this.loading = false;
        }
      },
      handleCancel() {
        this.visible = false;
        this.$refs.formRef?.resetFields();
      },
      async handleSubmit() {
        const valid = await this.$refs.formRef?.validate();
        if (!valid) return;
        
        this.loading = true;
        try {
          const updateData = {
            id: this.id,
            ...this.formData
          };
          await api.update(updateData);
          this.$message.success('更新成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
          this.emit('confirm');
        } catch (error) {
          // 错误已在拦截器处理
        } finally {
          this.loading = false;
        }
      },
    },
  });
</script>

<style scoped>
  .modal-footer {
    margin-top: 24px;
    text-align: right;
  }
</style>
