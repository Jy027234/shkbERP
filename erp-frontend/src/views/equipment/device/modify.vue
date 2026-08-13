<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="修改"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['equipment:device']" v-loading="loading">
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
        <a-form-item label="设备编号" name="code">
          <a-input v-model:value="formData.code" allow-clear />
        </a-form-item>
        <a-form-item label="设备名称" name="name">
          <a-input v-model:value="formData.name" allow-clear />
        </a-form-item>
        <a-form-item label="维保项目" name="maintenanceProject">
          <a-textarea v-model:value="formData.maintenanceProject" allow-clear />
        </a-form-item>
        <a-form-item label="维保间隔（天）" name="maintenanceInterval">
          <a-input v-model:value="formData.maintenanceInterval" allow-clear />
        </a-form-item>
        <a-form-item label="上次维保时间" name="lastMaintenanceTime">
          <a-date-picker v-model:value="formData.lastMaintenanceTime" style="width: 100%" value-format="YYYY-MM-DD" />
        </a-form-item>
        <a-form-item label="下次维保时间" name="nextMaintenanceTime">
          <a-date-picker v-model:value="formData.nextMaintenanceTime" style="width: 100%" value-format="YYYY-MM-DD" disabled />
        </a-form-item>
        <a-form-item label="维保工卡" name="maintenanceCard">
          <a-input v-model:value="formData.maintenanceCard" allow-clear />
        </a-form-item>
        <a-form-item label="设备状态" name="available">
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
  // import LocationMap from '@/components/LocationMap';
  import { validCode } from '@/utils/validate';
  import * as api from '@/api/equipment';

  export default defineComponent({
    // 使用组件
    components: {
      // LocationMap,
    },
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
        // 位置数据
        location: {},
        // 表单校验规则
        rules: {
          managementArea: [{ required: true, message: '请输入管理区域' }],
          code: [{ required: true, message: '请输入设备编号' }, { validator: validCode }],
          name: [{ required: true, message: '请输入设备名称' }],
          maintenanceProject: [],
          maintenanceInterval: [{ required: true, message: '请输入维保间隔（天）' }],
          lastMaintenanceTime: [{ required: true, message: '请选择上次维保时间' }],
          // nextMaintenanceTime 由后端与前端自动计算，不做必填
          maintenanceCard: [],
          available: [{ required: true, message: '请选择设备状态' }],
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
          code: '',
          name: '',
          maintenanceProject: '',
          maintenanceInterval: '',
          lastMaintenanceTime: '',
          nextMaintenanceTime: '',
          maintenanceCard: '',
          available: '',
          description: '',
        };
      },
      // 提交表单事件
      submit() {
        this.$refs.form.validate().then((valid) => {
          if (valid) {
            this.loading = true;
            const params = Object.assign({}, this.formData);
            api
              .update(params)
              .then(() => {
                this.$msg.createSuccess('修改成功！');
                this.$emit('confirm');
                this.visible = false;
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
            // 加载完毕后按现有值尝试计算一次
            this.computeNextMaintenanceTime();
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 根据上次维保时间与维保间隔（天）计算下次维保时间
      computeNextMaintenanceTime() {
        const interval = Number(this.formData.maintenanceInterval);
        const last = this.formData.lastMaintenanceTime;
        if (!last || !interval || interval <= 0) {
          this.formData.nextMaintenanceTime = '';
          return;
        }
        const d = new Date(String(last).replace(/-/g, '/'));
        if (isNaN(d.getTime())) {
          this.formData.nextMaintenanceTime = '';
          return;
        }
        d.setDate(d.getDate() + interval);
        const y = d.getFullYear();
        const m = (d.getMonth() + 1).toString().padStart(2, '0');
        const day = d.getDate().toString().padStart(2, '0');
        this.formData.nextMaintenanceTime = `${y}-${m}-${day}`;
      },
    },
  });
</script>
