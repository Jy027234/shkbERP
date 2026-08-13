<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="700px"
    title="证书详情"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:certificate']" v-loading="loading">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="员工姓名">
          {{ formData.employeeName }}
        </a-descriptions-item>
        <a-descriptions-item label="证书类型">
          {{ formData.certificateType }}
        </a-descriptions-item>
        <a-descriptions-item label="证书名称">
          {{ formData.certificateName }}
        </a-descriptions-item>
        <a-descriptions-item label="证书编号">
          {{ formData.certificateNo }}
        </a-descriptions-item>
        <a-descriptions-item label="发证机构">
          {{ formData.issueOrg }}
        </a-descriptions-item>
        <a-descriptions-item label="发证日期">
          {{ formData.issueDate || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="有效期" :span="2">
          <span v-if="formData.validEndDate">{{ formData.validStartDate }} 至 {{ formData.validEndDate }}</span>
          <span v-else-if="formData.validStartDate">{{ formData.validStartDate }} 起长期有效</span>
          <span v-else>长期有效</span>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(formData.status)">
            {{ getStatusText(formData.status) }}
          </a-tag>
          <a-tag v-if="formData.expiring" color="warning">即将过期</a-tag>
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
  import * as api from '@/api/hr/certificate';

  export default defineComponent({
    name: 'HrCertificateDetail',
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    data() {
      return {
        visible: false,
        loading: false,
        formData: {
          employeeName: '',
          certificateType: '',
          certificateName: '',
          certificateNo: '',
          issueOrg: '',
          issueDate: '',
          validStartDate: '',
          validEndDate: '',
          status: 1,
          expiring: false,
          description: '',
        },
      };
    },
    watch: {
      id: {
        handler(newVal) {
          if (newVal && this.visible) {
            this.loadFormData();
          }
        },
        immediate: false,
      },
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => this.loadFormData());
      },
      handleCancel() {
        this.visible = false;
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
      getStatusColor(status) {
        const colors = {
          0: 'default',
          1: 'success'
        };
        return colors[status] || 'default';
      },
      
      getStatusText(status) {
        const texts = {
          0: '过期',
          1: '有效'
        };
        return texts[status] || '';
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