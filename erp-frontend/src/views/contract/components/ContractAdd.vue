<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="新增"
    :style="{ top: '20px' }"
    :footer="null"
    :autofocus="false"
  >
    <div v-if="visible" v-permission="['contract:factory-w','contract:aviation','contract:factory-l']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-form-item label="合同编号" name="code">
          <a-input v-model:value="formData.code" allow-clear />
        </a-form-item>
        <a-form-item label="合同名称" name="name">
          <a-input v-model:value="formData.name" allow-clear />
        </a-form-item>
        <a-form-item label="客户" name="customerId">
          <customer-selector v-model:value="formData.customerId" @update:value="handleCustomerChange" />
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
        <a-form-item label="产品序号" name="serialNumber">
          <a-input v-model:value="formData.serialNumber" allow-clear placeholder="请输入产品序号" />
        </a-form-item>
        <a-form-item label="维修类型" name="repairTypeIds">
          <a-select 
            v-model:value="formData.repairTypeIds" 
            mode="multiple"
            allow-clear
            placeholder="请选择维修类型"
            show-search
            option-filter-prop="children"
            :filter-option="filterOption"
            @popupScroll="handleRepairTypeScroll"
            :loading="repairTypeLoading"
          >
            <a-select-option
              v-for="item in repairTypeList"
              :key="item.id"
              :value="item.id"
              >{{ item.name }}</a-select-option
            >
          </a-select>
        </a-form-item>
        <a-form-item label="其他维修需求" name="otherRepairRequirements">
          <a-textarea v-model:value="formData.otherRepairRequirements" allow-clear />
        </a-form-item>
        <a-form-item label="合同时间" name="contractTime">
          <a-date-picker v-model:value="formData.contractTime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </a-form-item>
        <a-form-item label="入库时间" name="storageTime">
          <a-date-picker v-model:value="formData.storageTime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </a-form-item>
        <a-form-item label="计划完工时间" name="plannedCompletionTime">
          <a-date-picker v-model:value="formData.plannedCompletionTime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </a-form-item>
        <a-form-item label="实际完工时间" name="actualCompletionTime">
          <a-date-picker v-model:value="formData.actualCompletionTime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </a-form-item>
        <a-form-item label="发货时间" name="deliveryTime">
          <a-date-picker v-model:value="formData.deliveryTime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </a-form-item>
        <a-form-item label="合同报价" name="contractPrice">
          <a-input-number v-model:value="formData.contractPrice" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="更换件价格" name="replacementPartPrice">
          <a-input-number v-model:value="formData.replacementPartPrice" :min="0" style="width: 100%" />
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
import * as contractApi from '@/api/contract';
import * as productApi from '@/api/base-data/product/info';
import * as repairTypeApi from '@/api/base-data/repair-type';
import * as customerApi from '@/api/base-data/customer';
import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';
import { CONTRACT_TYPE } from '@/enums/biz/contractType';

  export default defineComponent({
    name: 'ContractAdd',
    components: {
      CustomerSelector,
    },
    props: {
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
        
        // 维修类型列表
        repairTypeList: [],
        
        // 维修类型选择器加载状态
        repairTypeLoading: false,
        
        // 维修类型选择器分页信息
        repairTypePagination: {
          pageIndex: 1,
          pageSize: 20,
          totalCount: 0,
        },
        // 表单校验规则
        rules: {
          code: [{ required: true, message: '请输入合同编号' }, { validator: validCode }],
          customerId: [{ required: true, message: '请输入客户代码' }],
          name: [{ required: true, message: '请输入合同名称' }],
          partNumberCode: [{ required: true, message: '请输入件号' }],
          // 机型仅用于展示，非必填
          serialNumber: [{ required: true, message: '请输入产品序号' }],
          repairTypeIds: [{ required: true, message: '请选择维修类型' }],
          contractTime: [{ required: true, message: '请选择合同时间' }],
        },
      };
    },
    created() {
      // 初始化表单数据
      this.initFormData();
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
          id: '',
          code: '', // 合同编号
          name: '', // 合同名称
          customerId: '', // 客户ID
          customerName: '', // 客户名称
          mnemonicCode: '', // 助记码
          partNumberCode: '', // 件号（商品编号）
          machineTypeId: '', // 机型ID
          machineTypeName: '', // 机型名称（只读展示）
          partNumberId: '', // 件号ID
          serialNumber: '', // 产品序号
          repairTypeIds: [], // 维修类型ID列表
          otherRepairRequirements: '', // 其他维修需求
          contractTime: '', // 合同时间
          storageTime: '', // 入库时间
          plannedCompletionTime: '', // 计划完工时间
          actualCompletionTime: '', // 实际完工时间
          deliveryTime: '', // 发货时间
          contractPrice: 0, // 合同报价
          replacementPartPrice: 0, // 更换件价格
          description: '', // 备注
          available: this.$enums.AVAILABLE.ENABLE.code, // 状态
          contractType: this.contractType, // 合同类型
        };
      },
      // 提交表单事件
      async submit() {
        // 若填写了件号但尚未解析出商品与机型，则先自动解析一次
        if (this.formData.partNumberCode && (!this.formData.partNumberId || !this.formData.machineTypeId)) {
          await this.onPartNumberBlur();
        }

        const valid = await this.$refs.form.validate();
        if (!valid) return;

        this.loading = true;
        // 构建提交参数（将解析得到的商品ID作为 partNumberId 提交）
        const params = {
          code: this.formData.code, // 合同编号
          name: this.formData.name, // 合同名称
          customerId: this.formData.customerId, // 客户ID
          machineTypeId: this.formData.machineTypeId, // 机型ID（由件号反查）
          partNumberId: this.formData.partNumberId, // 件号对应的航材ID
          serialNumber: this.formData.serialNumber, // 产品序号
          repairTypeIds: this.formData.repairTypeIds, // 维修类型ID列表
          otherRepairRequirements: this.formData.otherRepairRequirements, // 其他维修需求
          contractTime: this.formData.contractTime, // 合同时间
          storageTime: this.formData.storageTime, // 入库时间
          plannedCompletionTime: this.formData.plannedCompletionTime, // 计划完工时间
          actualCompletionTime: this.formData.actualCompletionTime, // 实际完工时间
          deliveryTime: this.formData.deliveryTime, // 发货时间
          contractPrice: this.formData.contractPrice, // 合同报价
          replacementPartPrice: this.formData.replacementPartPrice, // 更换件价格
          description: this.formData.description, // 备注
          available: this.formData.available, // 状态
          contractType: this.getContractTypeCode(this.contractType), // 合同类型
        };
        contractApi
          .create(params)
          .then(() => {
            this.$msg.createSuccess('新增成功！');
            this.$emit('confirm');
            this.visible = false;
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 页面显示时触发
      open() {
        // 初始化表单数据
        this.initFormData();
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
      loadRepairTypeList(isAppend = false) {
        if (!isAppend) {
          // 重置分页信息
          this.repairTypePagination.pageIndex = 1;
          this.repairTypeList = [];
        }
        
        if (this.repairTypeLoading) {
          return;
        }
        
        // 如果已经加载完所有数据，则不再加载
        if (isAppend && this.repairTypeList.length >= this.repairTypePagination.totalCount) {
          return;
        }
        
        this.repairTypeLoading = true;
        repairTypeApi.selector({
          pageIndex: this.repairTypePagination.pageIndex,
          pageSize: this.repairTypePagination.pageSize,
        }).then((res) => {
          if (isAppend) {
            // 追加数据
            this.repairTypeList = [...this.repairTypeList, ...(res.datas || [])];
          } else {
            this.repairTypeList = res.datas || [];
          }
          this.repairTypePagination.totalCount = res.totalCount || 0;
        }).finally(() => {
          this.repairTypeLoading = false;
        });
      },
      
      // 处理维修类型选择器滚动事件
      handleRepairTypeScroll(e) {
        // 判断是否滚动到底部
        const { target } = e;
        if (target.scrollTop + target.offsetHeight >= target.scrollHeight - 20) {
          // 增加页码，加载下一页数据
          this.repairTypePagination.pageIndex++;
          this.loadRepairTypeList(true);
        }
      },
      
      // 获取合同类型的枚举对象
      getContractTypeCode(contractTypeStr) {
        // 将字符串类型转换为枚举对象
        switch(contractTypeStr) {
          case 'aviation':
            return 1;
          case 'factory-wb':
            return 2;
          case 'factory-l':
            return 3;
          default:
            console.error('未知的合同类型:', contractTypeStr);
            return null;
        }
      },
    },
  });
</script>
