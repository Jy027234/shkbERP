<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="700px"
    title="培训记录详情"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="员工">
          {{ formData.employeeName }}
        </a-descriptions-item>
        <a-descriptions-item label="培训名称">
          {{ formData.trainingName }}
        </a-descriptions-item>
        <a-descriptions-item label="培训类型">
          {{ formData.trainingType }}
        </a-descriptions-item>
        <a-descriptions-item label="培训机构">
          {{ formData.trainingOrg }}
        </a-descriptions-item>
        <a-descriptions-item label="开始日期">
          {{ formData.startDate }}
        </a-descriptions-item>
        <a-descriptions-item label="结束日期">
          {{ formData.endDate }}
        </a-descriptions-item>
        <a-descriptions-item label="培训学时">
          {{ formData.trainingHours }}
        </a-descriptions-item>
        <a-descriptions-item label="培训结果">
          <a-tag :color="getResultColor(formData.trainingResult)">
            {{ formData.trainingResult || '-' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="证书编号">
          {{ formData.certificateNo || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formData.createTime }}
        </a-descriptions-item>
        <a-descriptions-item label="培训内容" :span="2">
          {{ formData.trainingContent || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
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
  import * as api from '@/api/hr/training-record';

  export default defineComponent({
    name: 'HrTrainingRecordDetail',
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
          employeeName: '',
          trainingName: '',
          trainingType: '',
          trainingOrg: '',
          trainingContent: '',
          startDate: '',
          endDate: '',
          trainingHours: '',
          trainingResult: '',
          certificateNo: '',
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
        return api.get(this.id).then((res) => {
          this.formData = res || {};
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      },
      getResultColor(result) {
        const colors = {
          '优秀': 'green',
          '良好': 'blue',
          '合格': 'orange',
          '不合格': 'red'
        };
        return colors[result] || 'default';
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
