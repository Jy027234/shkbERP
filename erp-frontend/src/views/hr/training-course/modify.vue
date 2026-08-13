<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="600px"
    title="编辑培训课程"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:training:update']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-col="80px"
        layout="vertical"
      >
        <a-form-item label="课程名称" field="courseName">
          <a-input v-model:value="formData.courseName" placeholder="请输入课程名称" />
        </a-form-item>
        
        <a-form-item label="课程类型" field="courseType">
          <a-select v-model:value="formData.courseType" placeholder="请选择课程类型">
            <a-select-option value="公共类">公共类</a-select-option>
            <a-select-option value="技术类">技术类</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="实施间隔" required>
          <a-input-group compact>
            <a-input-number v-model:value="formData.implementationInterval" :min="1" style="width: 60%" />
            <a-select v-model:value="formData.intervalUnit" style="width: 40%">
              <a-select-option value="month">个月</a-select-option>
              <a-select-option value="year">年</a-select-option>
            </a-select>
          </a-input-group>
        </a-form-item>
        
        <a-form-item label="初训时长(h)" field="initialTrainingHours">
          <a-input-number v-model:value="formData.initialTrainingHours" :min="1" style="width: 100%" />
        </a-form-item>
        
        <a-form-item label="复训时长(h)" field="retrainingHours">
          <a-input-number v-model:value="formData.retrainingHours" :min="1" style="width: 100%" />
        </a-form-item>
        
        <a-form-item label="教学方式" field="teachingMethod">
          <a-select v-model:value="formData.teachingMethod" placeholder="请选择教学方式">
            <a-select-option value="理论">理论</a-select-option>
            <a-select-option value="OJT">OJT</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="参训人员" field="participants">
          <a-input v-model:value="formData.participants" placeholder="请输入参训人员" />
        </a-form-item>
        
        <a-form-item label="教员" field="instructor">
          <a-input v-model:value="formData.instructor" placeholder="请输入教员" />
        </a-form-item>
        
        <a-form-item label="考核方式" field="assessmentMethod">
          <a-select v-model:value="formData.assessmentMethod" placeholder="请选择考核方式">
            <a-select-option value="笔试">笔试</a-select-option>
            <a-select-option value="问答">问答</a-select-option>
            <a-select-option value="实操">实操</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="培训提纲" field="trainingOutline">
          <a-textarea v-model:value="formData.trainingOutline" placeholder="请输入培训提纲" :rows="3" />
        </a-form-item>
        
        <a-form-item label="课程说明" field="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入课程说明" :rows="3" />
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
  import * as api from '@/api/hr/training-course';

  export default defineComponent({
    name: 'HrTrainingCourseModify',
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
          courseName: '',
          courseType: '',
          implementationInterval: 12,
          intervalUnit: 'month',
          initialTrainingHours: null,
          retrainingHours: null,
          teachingMethod: '',
          participants: '',
          instructor: '',
          assessmentMethod: '',
          trainingOutline: '',
          description: '',
          status: 1,
        },
        rules: {
          courseName: [
            { required: true, message: '请输入课程名称', trigger: 'blur' },
          ],
          courseType: [
            { required: true, message: '请选择课程类型', trigger: 'change' },
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
      loadFormData() {
        if (!this.id) return;
        
        this.loading = true;
        return api.getTrainingCourse(this.id).then((res) => {
          const data = res || {};
          this.formData = {
            courseName: data.courseName || '',
            courseType: data.courseType || '',
            implementationInterval: data.implementationInterval || 12,
            intervalUnit: data.intervalUnit || 'month',
            initialTrainingHours: data.initialTrainingHours || null,
            retrainingHours: data.retrainingHours || null,
            teachingMethod: data.teachingMethod || '',
            participants: data.participants || '',
            instructor: data.instructor || '',
            assessmentMethod: data.assessmentMethod || '',
            trainingOutline: data.trainingOutline || '',
            description: data.description || '',
            status: data.status || 1,
          };
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
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
          const params = {
            ...this.formData,
            id: this.id
          };
          await api.updateTrainingCourse(params);
          this.$message.success('更新成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
          this.emit('confirm');
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
