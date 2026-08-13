<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="修改"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['contract:aviation']" v-loading="loading">
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
        <a-form-item label="序号" name="serialNumber">
          <a-input v-model:value="formData.serialNumber" allow-clear />
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
  import * as api from '@/api/contract';
  import * as repairTypeApi from '@/api/base-data/repair-type/index';
  import * as productApi from '@/api/base-data/product/info';
  import * as customerApi from '@/api/base-data/customer';
  import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    name: 'ContractModify',
    // 使用组件
    components: {
      CustomerSelector,
    },
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
        // 维修类型列表
        repairTypeList: [],
        // 维修类型加载状态
        repairTypeLoading: false,
        // 维修类型分页信息
        repairTypePagination: {
          pageIndex: 1,
          pageSize: 20,
          totalCount: 0,
        },
        
        // 表单校验规则
        rules: {
          code: [{ required: true, message: '请输入合同编号' }, { validator: validCode }],
          customerCode: [{ required: true, message: '请选择客户' }],
          partNumberCode: [{ required: true, message: '请输入件号' }],
          // 机型用于展示，非必填
          // partNumberId 由件号解析得到
          serialNumber: [{ required: true, message: '请输入序号' }],
          repairTypeIds: [{ required: true, message: '请选择维修类型' }],
          contractTime: [{ required: true, message: '请选择合同时间' }],
          available: [{ required: true, message: '请选择状态' }],
        },
      };
    },
    created() {
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
          machineTypeId: '', // 机型ID
          partNumberId: '', // 件号ID
          serialNumber: '', // 序号
          repairTypeIds: [], // 维修类型IDs
          otherRepairRequirements: '', // 其他维修需求
          contractTime: null, // 合同时间
          storageTime: null, // 入库时间
          plannedCompletionTime: null, // 计划完工时间
          deliveryTime: null, // 发货时间
          contractPrice: 0, // 合同报价
          replacementPartPrice: 0, // 更换件价格
          available: true, // 状态
          description: '', // 备注
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
        const params = {
          id: this.formData.id,
          code: this.formData.code,
          name: this.formData.name,
          customerId: this.formData.customerId,
          machineTypeId: this.formData.machineTypeId, // 展示用，可为空
          partNumberId: this.formData.partNumberId, // 件号对应的航材ID
          serialNumber: this.formData.serialNumber,
          repairTypeIds: this.formData.repairTypeIds,
          otherRepairRequirements: this.formData.otherRepairRequirements,
          contractTime: this.formData.contractTime,
          storageTime: this.formData.storageTime,
          plannedCompletionTime: this.formData.plannedCompletionTime,
          actualCompletionTime: this.formData.actualCompletionTime,
          deliveryTime: this.formData.deliveryTime,
          contractPrice: this.formData.contractPrice,
          replacementPartPrice: this.formData.replacementPartPrice,
          description: this.formData.description,
          available: this.formData.available,
          contractType: this.getContractTypeEnum(this.contractType),
        };
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
      },
      // 页面显示时触发
      open() {
        // 初始化数据
        this.initFormData();

        // 查询数据
        this.loadFormData();
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
          
          // 调试输出
          console.log('维修类型列表:', this.repairTypeList);
          console.log('已选维修类型:', this.formData.repairTypeIds);
        }).finally(() => {
          this.repairTypeLoading = false;
        });
      },
      
      // 处理维修类型滚动加载
      handleRepairTypeScroll(e) {
        // 如果滚动到底部，则加载更多数据
        const { scrollTop, scrollHeight, clientHeight } = e.target;
        if (scrollTop + clientHeight >= scrollHeight - 10) {
          this.repairTypePagination.pageIndex++;
          this.loadRepairTypeList(true);
        }
      },
      
      // 处理客户变更
      handleCustomerChange(value) {
        if (value) {
          // 获取客户详情
          customerApi.get(value).then(res => {
            if (res) {
              this.formData.customerName = res.name;
              this.formData.mnemonicCode = res.mnemonicCode;
            }
          });
        } else {
          this.formData.customerName = '';
          this.formData.mnemonicCode = '';
        }
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
      
      // 过滤选项
      filterOption(input, option) {
        return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
      },
      // 将字符串类型的合同类型转换为对应的整数值
      getContractTypeEnum(contractTypeStr) {
        // 根据字符串返回对应的整数值
        if (typeof contractTypeStr === 'string') {
          const typeStr = contractTypeStr.toLowerCase();
          if (typeStr === 'aviation') {
            return 1; // 民航维修合同
          } else if (typeStr === 'factory-wb' || typeStr === 'receive_wb') {
            return 2; // 返厂WB合同
          } else if (typeStr === 'factory-l' || typeStr === 'receive_l') {
            return 3; // 返厂L合同
          }
        }
        // 如果已经是数字类型，直接返回
        if (typeof contractTypeStr === 'number') {
          return contractTypeStr;
        }
        // 默认返回民航维修合同类型
        return 1; // 默认为 AVIATION
      },
      
      // 查询数据
      loadFormData() {
        this.loading = true;
        api
          .get(this.id)
          .then((data) => {
            this.formData = data;
            // 如果后端返回了件号与机型名称，填充到只读展示字段
            if (data.partNumberCode) this.formData.partNumberCode = data.partNumberCode;
            if (data.machineTypeName) this.formData.machineTypeName = data.machineTypeName;
            // 如果后端返回的数据没有合同类型，则添加合同类型
            if (!this.formData.contractType) {
              this.formData.contractType = this.getContractTypeEnum(this.contractType);
            }
            
            // 处理维修类型数据
            if (data.repairTypes && data.repairTypes.length > 0) {
              this.formData.repairTypeIds = data.repairTypes.map(item => item.id);
              console.log('从后端获取的维修类型:', data.repairTypes);
              console.log('处理后的维修类型IDs:', this.formData.repairTypeIds);
            } else {
              this.formData.repairTypeIds = [];
              console.log('后端未返回维修类型数据');
            }
            
            // 加载维修类型列表
            this.loadRepairTypeList();
            
            // 不再加载机型与件号下拉列表
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>
