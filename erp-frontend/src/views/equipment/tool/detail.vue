<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="查看"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['equipment:tool']" v-loading="loading">
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="管理区域" :span="2">
          {{ formData.managementArea }}
        </a-descriptions-item>
        <a-descriptions-item label="设备名称" :span="2">
          {{ formData.name }}
        </a-descriptions-item>
        <a-descriptions-item label="管理编号" :span="2">
          {{ formData.code }}
        </a-descriptions-item>
        <a-descriptions-item label="证书编号" :span="2">
          {{ formData.certificateNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="规格" :span="2">
          {{ formData.specification }}
        </a-descriptions-item>
        <a-descriptions-item label="型号" :span="2">
          {{ formData.model }}
        </a-descriptions-item>
        <a-descriptions-item label="计量周期" :span="2">
          {{ formData.calibrationPeriod }}
        </a-descriptions-item>
        <a-descriptions-item label="计量标准" :span="2">
          {{ formData.standard }}
        </a-descriptions-item>
        <a-descriptions-item label="精度" :span="2">
          {{ formData.precision }}
        </a-descriptions-item>
        <a-descriptions-item label="存放位置" :span="2">
          {{ formData.storageLocation }}
        </a-descriptions-item>
        <a-descriptions-item label="上次计量日期" :span="2">
          {{ formData.lastMaintenanceTime }}
        </a-descriptions-item>
        <a-descriptions-item label="下次计量日期" :span="2">
          {{ formData.nextMaintenanceTime }}
        </a-descriptions-item>
        <a-descriptions-item label="有效期" :span="2">
          {{ formData.expirationTime }}
        </a-descriptions-item>
        <a-descriptions-item label="上次维保单位" :span="2">
          {{ formData.lastMaintenanceUnit }}
        </a-descriptions-item>
        <a-descriptions-item label="状态" :span="2">
          <a-tag :color="formData.available ? 'green' : 'red'">
            {{ formData.available ? '启用' : '停用' }}
          </a-tag>
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
  import * as api from '@/api/equipment/tool';

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
          managementArea: '',
          name: '',
          code: '',
          certificateNumber: '',
          specification: '',
          model: '',
          calibrationPeriod: '',
          standard: '',
          precision: '',
          storageLocation: '',
          lastMaintenanceTime: '',
          nextMaintenanceTime: '',
          expirationTime: '',
          lastMaintenanceUnit: '',
          available: '',
          description: '',
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
