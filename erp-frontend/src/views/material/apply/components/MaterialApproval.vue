<template>
  <a-modal
    :open="visible"
    title="发料审批"
    :width="600"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" :loading="loading" @click="handleSubmit" style="margin-left: 8px">
        提交
      </a-button>
    </template>
    <div class="material-approval-container">
      <a-alert
        v-if="selectedRecords.length === 0"
        message="请先选择需要审批的记录"
        type="warning"
        show-icon
        style="margin-bottom: 16px"
      />
      
      <!-- 选中记录列表 -->
      <a-table
        :dataSource="selectedRecords"
        :columns="columns"
        :rowKey="record => record.id"
        :pagination="false"
        size="small"
        bordered
        style="margin-bottom: 16px"
      />
      
      <!-- 审批状态 -->
      <div class="approval-status">
        <div class="form-label">审批状态:</div>
        <a-radio-group v-model:value="approvalStatus">
          <a-radio :value="true">通过</a-radio>
          <a-radio :value="false">不通过</a-radio>
        </a-radio-group>
      </div>
      
      <!-- 审批意见 -->
      <div class="approval-comment">
        <div class="form-label">审批意见:</div>
        <a-textarea 
          v-model:value="comment" 
          placeholder="请输入审批意见（选填）" 
          :rows="4" 
          class="form-textarea" 
        />
      </div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { approveMaterialApply } from '@/api/material/apply';

export default defineComponent({
  name: 'MaterialApproval',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    selectedRecords: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      comment: '',
      approvalStatus: true, // 默认为通过
      columns: [
        { title: '申请编号', dataIndex: 'applyCode', key: 'applyCode', width: 120 },
        { title: '合同编号', dataIndex: 'contractCode', key: 'contractCode', width: 120 },
        { title: '申请时间', dataIndex: 'createTime', key: 'createTime', width: 150 },
      ]
    };
  },
  methods: {
    handleCancel() {
      this.comment = '';
      this.approvalStatus = true; // 重置为默认值
      this.$emit('update:visible', false);
    },
    handleSubmit() {
      if (this.selectedRecords.length === 0) {
        this.$message.warning('请先选择需要审批的记录');
        return;
      }
      
      this.loading = true;
      
      // 调用后端审批接口
      const ids = this.selectedRecords.map(record => record.id);
      
      approveMaterialApply({
        ids: ids,
        approved: this.approvalStatus,
        comment: this.comment
      }).then(() => {
        const statusText = this.approvalStatus ? '审批通过' : '审批不通过';
        this.$message.success(`${statusText}成功`);
        this.$emit('confirm', {
          approved: this.approvalStatus,
          comment: this.comment,
          records: this.selectedRecords
        });
        this.handleCancel();
      }).catch(e => {
        this.$message.error(e.message);
      }).finally(() => {
        this.loading = false;
      });
    }
  }
});
</script>

<style scoped>
.material-approval-container {
  padding: 0 10px;
}
.approval-status {
  margin-top: 16px;
}
.approval-comment {
  margin-top: 16px;
}
.form-label {
  font-weight: 500;
  margin-bottom: 8px;
}
.form-textarea {
  width: 100%;
}
</style>
