<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="查看"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['contract:aviation']" v-loading="loading">
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="合同编号" :span="2">
          {{ formData.code }}
        </a-descriptions-item>
        <a-descriptions-item label="合同名称" :span="2">
          {{ formData.name }}
        </a-descriptions-item>
        <a-descriptions-item label="客户" :span="2">
          {{ formData.customerName }} ({{ formData.mnemonicCode }})
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
        <a-descriptions-item label="状态" :span="2">
          {{ formData.available ? '启用' : '停用' }}
        </a-descriptions-item>
        <a-descriptions-item label="合同进度" :span="2">
          {{ formData.contractStatusName }}
        </a-descriptions-item>
        <a-descriptions-item label="维修类型" :span="2">
          <template v-if="formData.repairTypes && formData.repairTypes.length > 0">
            <a-tag v-for="item in formData.repairTypes" :key="item.id" color="blue" style="margin-right: 4px">
              {{ item.name }}
            </a-tag>
          </template>
          <template v-else>-</template>
        </a-descriptions-item>
        <a-descriptions-item label="其他维修需求" :span="2">
          {{ formData.otherRepairRequirements }}
        </a-descriptions-item>
        <a-descriptions-item label="合同时间" :span="2">
          {{ formData.contractTime }}
        </a-descriptions-item>
        <a-descriptions-item label="入库时间" :span="2">
          {{ formData.storageTime }}
        </a-descriptions-item>
        <a-descriptions-item label="计划完工时间" :span="2">
          {{ formData.plannedCompletionTime }}
        </a-descriptions-item>
        <a-descriptions-item label="发货时间" :span="2">
          {{ formData.deliveryTime }}
        </a-descriptions-item>
        <a-descriptions-item label="合同报价" :span="2">
          {{ formData.contractPrice }}
        </a-descriptions-item>
        <a-descriptions-item label="更换件价格" :span="2">
          {{ formData.replacementPartPrice }}
        </a-descriptions-item>
        <a-descriptions-item label="状态" :span="2">
          <available-tag :available="formData.available" />
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ formData.description }}
        </a-descriptions-item>
        <a-descriptions-item label="合同类型" :span="2">
          {{ getContractTypeName(formData.contractType) }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/contract/index';

  export default defineComponent({
    name: 'ContractDetail',
    // 使用组件
    components: {},
    props: {
      id: {
        type: String,
        required: true,
      },
      // 合同类型：aviation-航空维修合同，factory-l-工厂维修合同(L)，factory-wb-工厂维修合同(WB)
      contractType: {
        type: String,
        required: true,
        validator: (value) => ['aviation', 'factory-l', 'factory-wb'].includes(value),
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
      // 获取合同类型名称
      getContractTypeName(type) {
        const typeMap = {
          '1': '民航维修合同',
          '2': '返厂L合同',
          '3': '返厂WB合同',
        };
        return typeMap[type] || type;
      },
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
          code: '', // 合同编号
          customerId: '', // 客户代码
          customerName: '', // 客户名称
          mnemonicCode: '', // 客户简码
          aircraftType: '', // 机型
          partNumber: '', // 件号
          serialNumber: '', // 序号
          repairType: '', // 维修类型
          otherRepairRequirements: '', // 其他维修需求
          contractTime: null, // 合同时间
          storageTime: null, // 入库时间
          plannedCompletionTime: null, // 计划完工时间
          deliveryTime: null, // 发货时间
          contractPrice: 0, // 合同报价
          replacementPartPrice: 0, // 更换件价格
          available: '', // 状态
          description: '', // 备注
          contractType: this.contractType, // 合同类型
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
            // 如果后端返回的数据没有合同类型，则添加合同类型
            if (!this.formData.contractType) {
              this.formData.contractType = this.contractType;
            }
            
            // 调试输出维修类型数据
            console.log('查看页面 - 维修类型数据:', this.formData.repairTypes);
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>
