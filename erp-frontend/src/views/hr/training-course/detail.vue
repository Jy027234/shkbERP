<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="600px"
    title="培训课程详情"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:training:query']" v-loading="loading">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="课程名称">
          {{ formData.courseName }}
        </a-descriptions-item>
        <a-descriptions-item label="课程类型">
          {{ formData.courseType }}
        </a-descriptions-item>
        <a-descriptions-item label="实施间隔">
          {{ formData.implementationInterval }} {{ formData.intervalUnit === 'month' ? '个月' : '年' }}
        </a-descriptions-item>
        <a-descriptions-item label="初训时长(h)">
          {{ formData.initialTrainingHours || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="复训时长(h)">
          {{ formData.retrainingHours || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="教学方式">
          {{ formData.teachingMethod || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="参训人员">
          {{ formData.participants || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="教员">
          {{ formData.instructor || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="考核方式">
          {{ formData.assessmentMethod || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="formData.status === 1 ? 'success' : 'default'">
            {{ formData.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formData.createTime }}
        </a-descriptions-item>
        <a-descriptions-item label="培训提纲" :span="2">
          {{ formData.trainingOutline || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="课程说明" :span="2">
          {{ formData.description || '-' }}
        </a-descriptions-item>
      </a-descriptions>
      
      <div class="modal-footer">
        <a-button @click="handleCancel">关闭</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/hr/training-course';

  export default defineComponent({
    name: 'HrTrainingCourseDetail',
    props: {
      id: {
        type: String,
        default: ''
      }
    },
    setup() {
      return {};
    },
    data() {
      return {
        visible: false,
        loading: false,
        formData: {
          courseName: '',
          courseType: '',
          implementationInterval: '',
          intervalUnit: '',
          initialTrainingHours: '',
          retrainingHours: '',
          teachingMethod: '',
          participants: '',
          instructor: '',
          assessmentMethod: '',
          trainingOutline: '',
          status: '',
          createTime: '',
          description: '',
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
            implementationInterval: data.implementationInterval || '',
            intervalUnit: data.intervalUnit || '',
            initialTrainingHours: data.initialTrainingHours || '',
            retrainingHours: data.retrainingHours || '',
            teachingMethod: data.teachingMethod || '',
            participants: data.participants || '',
            instructor: data.instructor || '',
            assessmentMethod: data.assessmentMethod || '',
            trainingOutline: data.trainingOutline || '',
            status: data.status || '',
            createTime: data.createTime || '',
            description: data.description || '',
          };
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      },
      handleCancel() {
        this.visible = false;
      },
    },
  });
</script>

<style scoped>
  .modal-footer {
    margin-top: 24px;
    text-align: center;
  }
</style>
