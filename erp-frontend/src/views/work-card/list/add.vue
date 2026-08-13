<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="新增"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['work-card']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-form-item label="工卡号" name="code">
          <a-input v-model:value="formData.code" allow-clear />
        </a-form-item>
        <a-form-item label="件号" name="partNumberCode">
          <a-input
            v-model:value="formData.partNumberCode"
            allow-clear
            placeholder="请输入件号"
            @blur="onPartNumberBlur"
          />
        </a-form-item>
        <a-form-item label="机型" name="machineTypeId">
          <a-input v-model:value="formData.machineTypeName" disabled placeholder="将根据件号自动填充" />
        </a-form-item>
        <a-form-item label="工卡名称" name="name">
          <a-input v-model:value="formData.name" allow-clear />
        </a-form-item>
        <a-form-item label="维修类型" name="repairTypeId">
          <a-select 
            v-model:value="formData.repairTypeId" 
            allow-clear 
            placeholder="请选择维修类型"
            :loading="repairTypeLoading"
          >
            <a-select-option
              v-for="item in repairTypeList"
              :key="item.id"
              :value="item.id"
              >{{ item.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="版本号" name="version">
          <a-textarea v-model:value="formData.version" allow-clear />
        </a-form-item>
        <a-form-item label="批准日期" name="approvalDate">
          <a-date-picker v-model:value="formData.approvalDate" style="width: 100%" value-format="YYYY-MM-DD"></a-date-picker>
        </a-form-item>
        <a-form-item label="客户" name="customerId">
          <customer-selector v-model:value="formData.customerId" @update:value="handleCustomerChange" />
        </a-form-item>
        <a-form-item label="状态" name="available">
          <a-select 
            v-model:value="formData.available" 
            placeholder="请选择状态"
          >
            <a-select-option :value="true">启用</a-select-option>
            <a-select-option :value="false">停用</a-select-option>
          </a-select>
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
  import { workCardApi } from '@/api/work-card/index';
  import * as productApi from '@/api/base-data/product/info';
  import * as machineTypeApi from '@/api/base-data/machine-type/index';
  import * as repairTypeApi from '@/api/base-data/repair-type/index';
  import * as customerApi from '@/api/base-data/customer';
  import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    components: {
      CustomerSelector,
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
        // 机型列表（不再必填，仅用于展示解析结果，可保留选择器数据以备后续需求）
        machineTypeList: [],
        machineTypeLoading: false,
        // 维修类型列表
        repairTypeList: [],
        repairTypeLoading: false,
        // 表单校验规则
        rules: {
          code: [{ required: true, message: '请输入工卡号' }, { validator: validCode }],
          name: [{ required: true, message: '请输入工卡名称' }],
          // 机型非必填，仅展示解析结果
          partNumberCode: [{ required: true, message: '请输入件号' }],
          repairTypeId: [{ required: true, message: '请选择维修类型' }],
          approvalDate: [{ required: true, message: '请选择批准日期' }],
          // customerId: [{ required: true, message: '请选择客户' }],
        },
      };
    },
    computed: {},
    created() {
      // 初始化表单数据
      this.initFormData();
      // 加载机型列表（非必填，仅用于展示）
      this.loadMachineTypeList();
      // 加载维修类型列表
      this.loadRepairTypeList();
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
          code: '',
          name: '',
          machineTypeId: '',
          partNumberId: '',
          repairTypeId: '',
          customerId: '',
          approvalDate: '',
          available: true,
          description: '',
          version: '',
        };
      },
      // 提交表单事件
      async submit() {
        // 若填写了件号但尚未解析出商品与机型，则先自动解析一次
        if (this.formData.partNumberCode && (!this.formData.partNumberId || !this.formData.machineTypeId)) {
          await this.onPartNumberBlur();
        }
        this.$refs.form.validate().then((valid) => {
          if (valid) {
            this.loading = true;
            const params = Object.assign({}, this.formData, this.location);
            
            // 不需要处理日期格式，保持YYYY-MM-DD格式
            
            workCardApi
              .create(params)
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
      // 加载机型列表
      loadMachineTypeList() {
        this.machineTypeLoading = true;
        machineTypeApi.selector({}).then((res) => {
          this.machineTypeList = res.datas || [];
        }).finally(() => {
          this.machineTypeLoading = false;
        });
      },
      // 通过件号（商品编号）自动获取机型与商品ID
      async onPartNumberBlur() {
        const code = (this.formData.partNumberCode || '').trim();
        if (!code) {
          this.formData.partNumberId = '';
          this.formData.machineTypeId = '';
          this.formData.machineTypeName = '';
          return;
        }
        try {
          const res = await productApi.query({ pageIndex: 1, pageSize: 1, code, codeExact: true });
          const item = (res && res.datas && res.datas[0]) || null;
          if (!item) {
            this.$msg.createError('未找到该件号对应的航材！');
            this.formData.partNumberId = '';
            this.formData.machineTypeId = '';
            this.formData.machineTypeName = '';
            return;
          }
          // 设置商品ID作为件号ID，设置机型
          this.formData.partNumberId = item.id;
          this.formData.machineTypeId = item.machineTypeId || '';
          this.formData.machineTypeName = item.machineTypeName || '';
        } catch (e) {
          this.$msg.createError('查询件号失败，请稍后重试');
        }
      },
      // 加载维修类型列表
      loadRepairTypeList() {
        this.repairTypeLoading = true;
        repairTypeApi.selector({}).then((res) => {
          this.repairTypeList = res.datas || [];
          // 如果有维修类型数据，默认选中第一个
          if (this.repairTypeList && this.repairTypeList.length > 0) {
            this.formData.repairTypeId = this.repairTypeList[0].id;
          }
        }).finally(() => {
          this.repairTypeLoading = false;
        });
      },
      // 处理客户选择变更
      handleCustomerChange(value) {
        if (value) {
          // 获取客户详情
          customerApi.get(value).then(res => {
            if (res) {
              // 可以在这里处理客户相关的其他字段
              // 例如：this.formData.customerName = res.name;
            }
          });
        }
      },
      // 过滤机型选项
      filterMachineTypeOption(input, option) {
        if (!input) return true;
        
        // 获取选项的原始数据
        const item = this.machineTypeList.find(item => item.id === option.value);
        if (item) {
          // 直接使用原始数据进行搜索
          const searchText = `${item.name} ${item.code}`.toLowerCase();
          return searchText.indexOf(input.toLowerCase()) >= 0;
        }
        
        return false;
      },
      // 已移除件号下拉过滤逻辑
    },
  });
</script>
