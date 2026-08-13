<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="50%"
    title="新增"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['equipment:tool']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="管理区域" name="managementArea">
              <a-input v-model:value="formData.managementArea" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="设备名称" name="name">
              <a-input v-model:value="formData.name" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="管理编号" name="code">
              <a-input v-model:value="formData.code" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="证书编号" name="certificateNumber">
              <a-input v-model:value="formData.certificateNumber" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="型号/规格" name="specification">
              <a-input v-model:value="formData.specification" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="型号" name="model">
              <a-input v-model:value="formData.model" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="计量标准" name="standard">
              <a-input v-model:value="formData.standard" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="精度" name="precision">
              <a-input v-model:value="formData.precision" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="存放位置" name="storageLocation">
              <a-input v-model:value="formData.storageLocation" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="计量周期(天)" name="calibrationPeriod">
              <a-input v-model:value="formData.calibrationPeriod" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="上次计量日期" name="lastMaintenanceTime">
              <a-date-picker v-model:value="formData.lastMaintenanceTime" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下次计量日期" name="nextMaintenanceTime">
              <a-date-picker v-model:value="formData.nextMaintenanceTime" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="有效期" name="expirationTime">
              <a-input v-model:value="formData.expirationTime" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="上次维保单位" name="lastMaintenanceUnit">
              <a-input v-model:value="formData.lastMaintenanceUnit" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="维保人" name="maintenancenUser">
              <a-input v-model:value="formData.maintenancenUser" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="计量证书编号" name="recordCertificateNumber">
              <a-input v-model:value="formData.recordCertificateNumber" allow-clear />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="附件上传">
              <a-upload
                :file-list="fileList"
                :before-upload="beforeUpload"
                @change="handleFileChange"
                @remove="handleRemove"
              >
                <a-button>
                  <upload-outlined /> 上传附件
                </a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
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
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="备注" name="description">
              <a-textarea v-model:value="formData.description" allow-clear style="width: 200%" />
            </a-form-item>
          </a-col>
        </a-row>
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
  import { UploadOutlined } from '@ant-design/icons-vue';

  export default defineComponent({
    components: {
      UploadOutlined
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 附件列表
        fileList: [],

        // 表单校验规则
        rules: {
          managementArea: [{ required: true, message: '请输入管理区域' }],
          name: [{ required: true, message: '请输入设备名称' }],
          code: [{ required: true, message: '请输入管理编号' }, { validator: validCode }],
          certificateNumber: [{ required: true, message: '请输入证书编号' }],
          specification: [{ required: true, message: '请输入型号/规格' }],
          model: [{ required: true, message: '请输入型号' }],
          standard: [{ required: true, message: '请输入计量标准' }],
          storageLocation: [{ required: true, message: '请输入存放位置' }],
          lastMaintenanceTime: [{ required: true, message: '请选择上次计量日期' }],
          nextMaintenanceTime: [{ required: true, message: '请选择下次计量日期' }],
          calibrationPeriod: [{ required: true, message: '请输入计量周期（天）' }],
          // 有效期由“上次计量日期+计量周期（天）”自动计算，不做必填校验
          lastMaintenanceUnit: [{ required: true, message: '请输入上次维保单位' }],
          maintenancenUser: [{ required: true, message: '请输入维保人' }],
          recordCertificateNumber: [{ required: true, message: '请输入计量证书编号' }],
          available: [{ required: true, message: '请选择状态' }],
        },
      };
    },
    computed: {},
    created() {
      // 初始化表单数据
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
          maintenancenUser: '',
          recordCertificateNumber: '',
          available: this.$enums.AVAILABLE.ENABLE.code,
          description: '',
        };
        this.fileList = [];
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
      // 文件上传前处理
      beforeUpload(file) {
        // 限制文件大小为50MB
        const isLt50M = file.size / 1024 / 1024 < 50;
        if (!isLt50M) {
          this.$msg.createError('文件大小不能超过50MB!');
          return false;
        }
        return false; // 阻止自动上传，由我们手动控制
      },
      // 文件变更处理
      handleFileChange(info) {
        // 保留所有文件
        this.fileList = [...info.fileList];
      },
      // 移除文件
      handleRemove(file) {
        const index = this.fileList.indexOf(file);
        const newFileList = this.fileList.slice();
        newFileList.splice(index, 1);
        this.fileList = newFileList;
      },
      // 提交表单事件
      submit() {
        this.$refs.form.validate().then((valid) => {
          if (valid) {
            this.loading = true;
            // 创建FormData对象
            const formData = new FormData();
            
            // 添加基本表单数据
            formData.append('managementArea', this.formData.managementArea);
            formData.append('name', this.formData.name);
            formData.append('code', this.formData.code);
            formData.append('certificateNumber', this.formData.certificateNumber);
            formData.append('specification', this.formData.specification);
            formData.append('model', this.formData.model);
            formData.append('standard', this.formData.standard);
            formData.append('precision', this.formData.precision);
            formData.append('storageLocation', this.formData.storageLocation);
            formData.append('lastMaintenanceTime', this.formData.lastMaintenanceTime);
            formData.append('nextMaintenanceTime', this.formData.nextMaintenanceTime);
            formData.append('calibrationPeriod', this.formData.calibrationPeriod);
            formData.append('lastMaintenanceUnit', this.formData.lastMaintenanceUnit);
            formData.append('maintenancenUser', this.formData.maintenancenUser);
            formData.append('recordCertificateNumber', this.formData.recordCertificateNumber);
            formData.append('available', this.formData.available);
            if (this.formData.description) {
              formData.append('description', this.formData.description);
            }
            
            // 添加文件
            if (this.fileList && this.fileList.length > 0) {
              this.fileList.forEach(file => {
                if (file.originFileObj) {
                  formData.append('files', file.originFileObj);
                }
              });
            }
            
            api
              .create(formData)
              .then(() => {
                this.$msg.createSuccess('新增成功！');
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
        // 初始化表单数据
        this.initFormData();
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
