<template>
  <div>
    <a-modal
      v-model:open="visible"
      :title="title"
      :width="600"
      :footer="null"
      :maskClosable="false"
      @cancel="closeDialog"
    >
      <div v-if="loading" class="loading-container">
        <a-spin />
      </div>
      <div v-else>
        <!-- 基础信息 -->
        <div class="info-container">
          <div class="info-title">任务基础信息</div>
          <a-descriptions :column="2" size="small" bordered>
            <a-descriptions-item label="合同编号">{{ model.contractCode }}</a-descriptions-item>
            <a-descriptions-item label="客户">{{ model.customerName }}</a-descriptions-item>
            <a-descriptions-item label="机型">{{ model.machineTypeName }}</a-descriptions-item>
            <a-descriptions-item label="件号">{{ model.partNumberName }}</a-descriptions-item>
            <a-descriptions-item label="序号">{{ model.serialNumber }}</a-descriptions-item>
            <a-descriptions-item label="任务类型">{{ model.taskTypeName }}</a-descriptions-item>
            <a-descriptions-item label="入库时间">{{ model.storageTime }}</a-descriptions-item>
            <a-descriptions-item label="计划完工时间">{{ model.plannedCompletionTime }}</a-descriptions-item>
          </a-descriptions>
        </div>

        <!-- 派发表单 -->
        <a-form
          ref="formRef"
          :model="form"
          :rules="rules"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 20 }"
          class="dispatch-form"
        >
          <a-form-item label="派发至" name="taskUserId">
            <user-selector 
              v-model:value="form.taskUserId" 
              @input-row="handleUserSelected"
            />
          </a-form-item>
          
          <!-- 显示所选用户的详细信息 -->
          <div v-if="selectedUser" class="selected-user-info">
            <a-alert type="info" show-icon>
              <template #message>
                <div>已选择：<strong>{{ selectedUser.name }}</strong> ({{ selectedUser.code }})</div>
                <div>单位编码：<strong>{{ selectedUser.unitCode || '无' }}</strong></div>
              </template>
            </a-alert>
          </div>
        </a-form>

        <!-- 底部按钮 -->
        <div class="footer">
          <a-space>
            <a-button @click="closeDialog">取消</a-button>
            <a-button type="primary" :loading="submitting" @click="submit">确定</a-button>
          </a-space>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import { message } from 'ant-design-vue';
import UserSelector from '@/components/Selector/src/UserSelector.vue';
import * as api from '@/api/maintenance/contract-task';

export default defineComponent({
  name: 'ContractTaskDispatch',
  components: {
    UserSelector,
  },
  props: {
    id: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      visible: false,
      loading: false,
      submitting: false,
      title: '任务派发',
      model: {},
      form: {
        id: '',
        taskUserId: '',
      },
      selectedUser: null,
      rules: {
        taskUserId: [{ required: true, message: '请选择派发人员', trigger: 'change' }],
      },
    };
  },
  methods: {
    // 打开对话框
    openDialog() {
      this.visible = true;
      this.resetForm();
      this.loadData();
    },
    // 关闭对话框
    closeDialog() {
      this.visible = false;
    },
    // 重置表单
    resetForm() {
      this.form = {
        id: this.id,
        taskUserId: '',
      };
      this.selectedUser = null;
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields();
      }
    },
    // 处理用户选择
    handleUserSelected(row) {
      if (row && row.length > 0) {
        this.selectedUser = row[0];
      } else {
        this.selectedUser = null;
      }
    },
    // 加载数据
    loadData() {
      if (!this.id) {
        return;
      }
      this.loading = true;
      api
        .get(this.id)
        .then((res) => {
          this.model = res || {};
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 提交表单
    submit() {
      this.$refs.formRef.validate().then(() => {
        this.submitting = true;
        api
          .dispatchTask(this.form)
          .then(() => {
            message.success('派发成功');
            this.closeDialog();
            this.$emit('confirm');
          })
          .finally(() => {
            this.submitting = false;
          });
      });
    },
  },
});
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}
.info-container {
  margin-bottom: 20px;
}
.info-title {
  font-weight: bold;
  margin-bottom: 10px;
}
.dispatch-form {
  margin-top: 20px;
}
.footer {
  margin-top: 24px;
  text-align: right;
}
.selected-user-info {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
