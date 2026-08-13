<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="50%"
    title="领料申请详情"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="申请编号" :span="1">
          {{ formData.applyCode }}
        </a-descriptions-item>
        <a-descriptions-item label="合同编号" :span="1">
          {{ formData.contractCode }}
        </a-descriptions-item>
        <a-descriptions-item label="机型" :span="1">
          {{ formData.machineTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="件号" :span="1">
          {{ formData.partNumberName }}
        </a-descriptions-item>
        <a-descriptions-item label="产品序号" :span="1">
          {{ formData.serialNumber || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="必换件单号" :span="1">
          {{ formData.replacementPartCode }}
        </a-descriptions-item>
        <a-descriptions-item label="非必换件单号" :span="1">
          {{ formData.nonReplacementPartCode }}
        </a-descriptions-item>
        <a-descriptions-item label="申请时间" :span="1">
          {{ formData.createTime }}
        </a-descriptions-item>
        <a-descriptions-item label="审批状态" :span="1">
          <a-tag v-if="formData.approvalStatus === 1" color="success">{{ formData.approvalStatusText }}</a-tag>
          <a-tag v-else-if="formData.approvalStatus === 2" color="error">{{ formData.approvalStatusText }}</a-tag>
          <a-tag v-else-if="formData.approvalStatus === 0" color="processing">{{ formData.approvalStatusText }}</a-tag>
          <span v-else>-</span>
        </a-descriptions-item>
        <a-descriptions-item label="审批时间" :span="1">
          {{ formData.approvalTime || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="1">
          {{ formData.remark || '-' }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';

  export default defineComponent({
    // 使用组件
    components: {},
    props: {
      record: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
      };
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        this.visible = true;
        this.loading = true;
        
        // 直接使用传入的record数据
        if (this.record) {
          this.formData = { ...this.record };
        }
        
        this.loading = false;
      },
      // 关闭对话框
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
    },
  });
</script>
