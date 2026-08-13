<template>
  <a-modal
    v-model:open="modelOpen"
    title="发料出库"
    width="600px"
    :maskClosable="false"
    :destroyOnClose="true"
    @ok="handleConfirm"
    @cancel="handleCancel"
  >
    <div class="material-issue-dialog">
      <!-- 申请基本信息 -->
      <div class="info-section">
        <h3>申请基本信息</h3>
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="申请编号">{{ record.applyCode }}</a-descriptions-item>
          <a-descriptions-item label="合同编号">{{ record.contractCode }}</a-descriptions-item>
          <a-descriptions-item label="机型">{{ record.machineTypeName }}</a-descriptions-item>
          <a-descriptions-item label="件号">{{ record.partNumberName }}</a-descriptions-item>
          <a-descriptions-item label="审批状态" :span="2">
            <a-tag v-if="record.approvalStatus === 1" color="success">{{ record.approvalStatusText }}</a-tag>
            <a-tag v-else-if="record.approvalStatus === 2" color="error">{{ record.approvalStatusText }}</a-tag>
            <a-tag v-else-if="record.approvalStatus === 0" color="processing">{{ record.approvalStatusText }}</a-tag>
            <span v-else>-</span>
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <!-- 仓库选择 -->
      <div class="warehouse-section">
        <h3>仓库选择</h3>
        <a-form-item label="仓库" :labelCol="{ span: 4 }" :wrapperCol="{ span: 8 }">
          <store-center-selector v-model:value="formData.scId" placeholder="请选择仓库" />
        </a-form-item>
      </div>

      <!-- 备注信息 -->
      <div class="remark-section">
        <h3>备注信息</h3>
        <a-form-item label="备注" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注信息（选填）"
            :rows="4"
            :maxLength="200"
            showCount
          />
        </a-form-item>
      </div>

      <!-- 发料提示 -->
      <div class="issue-tips">
        <a-alert
          message="发料出库提示"
          description="确认发料后，系统将自动扣减相应库存。此操作不可撤销，请确认后操作。"
          type="warning"
          show-icon
        />
      </div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { issueMaterial } from '@/api/material/apply';
import { message } from 'ant-design-vue';
import StoreCenterSelector from '@/components/Selector/src/StoreCenterSelector.vue';

export default defineComponent({
  name: 'MaterialIssueDialog',
  components: {
    StoreCenterSelector
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:visible', 'confirm'],
  computed: {
    modelOpen: {
      get() {
        return this.visible;
      },
      set(value) {
        this.$emit('update:visible', value);
      }
    }
  },
  data() {
    return {
      loading: false,
      formData: {
        scId: '', // 仓库ID
        remark: ''
      }
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.formData.remark = '';
        this.fetchDefaultWarehouse();
      }
    }
  },
  methods: {
    // 获取默认仓库
    fetchDefaultWarehouse() {
      import('@/api/base-data/store-center').then(api => {
        api.selector({ pageIndex: 1, pageSize: 20 }).then(res => {
          if (res && res.datas && res.datas.length > 0) {
            // 选择第一个仓库作为默认
            this.formData.scId = res.datas[0].id;
          }
        }).catch(err => {
          message.error(err.message || '获取仓库列表失败');
        });
      });
    },
    
    // 确认发料
    handleConfirm() {
      if (!this.record || !this.record.id) {
        message.error('未选择有效的领料申请');
        return;
      }

      if (this.record.approvalStatus !== 1) {
        message.error('只有审批通过的申请才能进行发料出库');
        return;
      }
      
      if (!this.formData.scId) {
        message.error('请选择发料仓库');
        return;
      }

      this.loading = true;
      // 调用后端接口执行发料出库
      issueMaterial({
        taskId: this.record.taskId,
        scId: this.formData.scId,
        remark: this.formData.remark
      })
        .then(res => {
          this.$emit('confirm', { success: true, record: this.record });
          this.$emit('update:visible', false);
        })
        .catch(err => {
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 取消
    handleCancel() {
      this.$emit('update:visible', false);
    }
  }
});
</script>

<style scoped>
.material-issue-dialog {
  padding: 0 10px;
}

.info-section, .remark-section {
  margin-bottom: 20px;
}

.issue-tips {
  margin-top: 20px;
}
</style>
