<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="修改"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['equipment:tool']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-form-item label="管理区域" name="managementArea">
          <a-input v-model:value="formData.managementArea" allow-clear />
        </a-form-item>
        <a-form-item label="设备名称" name="name">
          <a-input v-model:value="formData.name" allow-clear />
        </a-form-item>
        <a-form-item label="管理编号" name="code">
          <a-input v-model:value="formData.code" allow-clear />
        </a-form-item>
        <a-form-item label="证书编号" name="certificateNumber">
          <a-input v-model:value="formData.certificateNumber" allow-clear />
        </a-form-item>
        <a-form-item label="规格" name="specification">
          <a-input v-model:value="formData.specification" allow-clear />
        </a-form-item>
        <a-form-item label="型号" name="model">
          <a-input v-model:value="formData.model" allow-clear />
        </a-form-item>
        <a-form-item label="计量标准" name="standard">
          <a-input v-model:value="formData.standard" allow-clear />
        </a-form-item>
        <a-form-item label="精度" name="precision">
          <a-input v-model:value="formData.precision" allow-clear />
        </a-form-item>
        <a-form-item label="存放位置" name="storageLocation">
          <a-input v-model:value="formData.storageLocation" allow-clear />
        </a-form-item>
        <a-form-item label="计量周期（天）" name="calibrationPeriod">
          <a-input v-model:value="formData.calibrationPeriod" allow-clear />
        </a-form-item>
        <a-form-item label="上次计量日期" name="lastMaintenanceTime">
          <a-date-picker v-model:value="formData.lastMaintenanceTime" value-format="YYYY-MM-DD" style="width: 100%" />
        </a-form-item>
        <a-form-item label="下次计量日期" name="nextMaintenanceTime">
          <a-date-picker v-model:value="formData.nextMaintenanceTime" value-format="YYYY-MM-DD" style="width: 100%" />
        </a-form-item>
        <a-form-item label="有效期" name="expirationTime">
          <a-input v-model:value="formData.expirationTime" disabled />
        </a-form-item>
        <a-form-item label="上次维保单位" name="lastMaintenanceUnit">
          <a-input v-model:value="formData.lastMaintenanceUnit" allow-clear />
        </a-form-item>
        <a-form-item label="状态" name="available">
          <a-select v-model:value="formData.available" allow-clear>
            <a-select-option
              v-for="item in $enums.AVAILABLE.values()"
              :key="item.code"
              :value="item.code"
              >{{ item.desc }}</a-select-option
            >
          </a-select>
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="formData.description" allow-clear />
        </a-form-item>
        <div class="form-modal-footer">
          <a-space>
            <a-button type="primary" :loading="loading" html-type="submit" @click="submit"
              >保存</a-button
            >
            <a-button :loading="loading" @click="closeDialog">取消</a-button>
          </a-space>
        </div>
      </a-form>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';

  import { validCode } from '@/utils/validate';
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

        // 表单校验规则
        rules: {
          code: [{ required: true, message: '请输入管理编号' }, { validator: validCode }],
          name: [{ required: true, message: '请输入设备名称' }],
          managementArea: [{ required: true, message: '请输入管理区域' }],
          available: [{ required: true, message: '请选择状态' }],
        },
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
          standard: '',
          precision: '',
          storageLocation: '',
          lastMaintenanceTime: '',
          nextMaintenanceTime: '',
          calibrationPeriod: '',
          expirationTime: '',
          lastMaintenanceUnit: '',
          available: '',
          description: '',
        };
      },

      // 根据上次计量日期与计量周期（天）计算有效期（到期日期）
      computeExpiration() {
        const interval = Number(this.formData.calibrationPeriod);
        const last = this.formData.lastMaintenanceTime;
        if (!last || !interval || interval <= 0) {
          this.formData.expirationTime = '';
          return;
        }
        const d = new Date(String(last).replace(/-/g, '/'));
        if (isNaN(d.getTime())) {
          this.formData.expirationTime = '';
          return;
        }
        d.setDate(d.getDate() + interval);
        const y = d.getFullYear();
        const m = (d.getMonth() + 1).toString().padStart(2, '0');
        const day = d.getDate().toString().padStart(2, '0');
        this.formData.expirationTime = `${y}-${m}-${day}`;
      },

      // 提交表单事件
      submit() {
        this.$refs.form.validate().then((valid) => {
          if (valid) {
            this.loading = true;
            api
              .update(this.formData)
              .then(() => {
                this.$msg.createSuccess('修改成功！');
                this.$emit('confirm');
              })
              .finally(() => {
                this.loading = false;
              });
          }
        });
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
            // 初始载入后根据现有的上次计量日期与计量周期计算一次有效期
            this.computeExpiration();
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
    watch: {
      'formData.lastMaintenanceTime': function () {
        this.computeExpiration();
      },
      'formData.calibrationPeriod': function () {
        this.computeExpiration();
      },
    },
  });
</script>
