<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="查看"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['maintenance:contract-task']" v-loading="loading">
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="合同编号" :span="2">
          {{ formData.contractCode }}
        </a-descriptions-item>
        <a-descriptions-item label="合同名称" :span="2">
          {{ formData.contractName }}
        </a-descriptions-item>
        <a-descriptions-item label="客户名称" :span="2">
          {{ formData.customerName }}
        </a-descriptions-item>
        <a-descriptions-item label="机型" :span="2">
          {{ formData.machineTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="件号" :span="2">
          {{ formData.partNumberCode }}
        </a-descriptions-item>
        <a-descriptions-item label="序号" :span="2">
          {{ formData.serialNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="派发至" :span="2">
          {{ formData.dispatch }}
        </a-descriptions-item>
        <a-descriptions-item label="派发人" :span="2">
          {{ formData.taskUserName }}
        </a-descriptions-item>
        <a-descriptions-item label="维修类型" :span="2">
          <span v-for="(type, index) in formData.repairTypes" :key="index">
            {{ type.name }}<span v-if="index < formData.repairTypes.length - 1">, </span>
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="其他维修需求" :span="2">
          {{ formData.otherRepairRequirements }}
        </a-descriptions-item>
        <a-descriptions-item label="入库时间" :span="2">
          {{ formData.storageTime }}
        </a-descriptions-item>
        <a-descriptions-item label="计划完工时间" :span="2">
          {{ formData.plannedCompletionTime }}
        </a-descriptions-item>
        <a-descriptions-item label="任务类型" :span="2">
          {{ formData.taskTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="任务状态" :span="2">
          {{ formData.taskStatusName }}
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ formData.description }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/maintenance/contract-task/index';

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
        // 表单数据
        formData: {},
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
          customerCode: '',
          customerName: '',
          machineTypeId: '',
          machineTypeCode: '',
          machineTypeName: '',
          partNumberId: '',
          partNumberCode: '',
          partNumberName: '',
          serialNumber: '',
          repairTypes: [],
          otherRepairRequirements: '',
          storageTime: '',
          plannedCompletionTime: '',
          taskType: '',
          taskTypeName: '',
          taskStatus: '',
          taskStatusName: '',
          description: '',
          createBy: '',
          createTime: '',
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
    },
  });
</script>
