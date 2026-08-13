<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="50%"
    title="修改合同任务"
    :style="{ top: '20px' }"
    :confirm-loading="submitLoading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <div v-if="visible" v-permission="['maintenance:contract-task:modify']" v-loading="loading">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-row>
          <a-col :span="12">
            <a-form-item label="合同编号" name="contractCode">
              <a-input v-model:value="formData.contractCode" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同名称" name="contractName">
              <a-input v-model:value="formData.contractName" disabled />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="客户" name="customerId">
              <a-input v-model:value="formData.customerId" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="客户名称" name="customerName">
              <a-input v-model:value="formData.customerName" disabled />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="机型" name="machineTypeId">
              <a-select 
                v-model:value="formData.machineTypeId" 
                placeholder="请选择机型"
                show-search
                :filter-option="filterMachineTypeOption"
                @change="handleMachineTypeChange"
                :loading="machineTypeLoading"
              >
                <a-select-option
                  v-for="item in machineTypeList"
                  :key="item.id"
                  :value="item.id"
                  >{{ item.name }} ({{ item.code }})</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="件号" name="partNumberId">
              <a-select 
                v-model:value="formData.partNumberId" 
                placeholder="请选择件号"
                show-search
                :filter-option="filterPartNumberOption"
                :disabled="!formData.machineTypeId"
                :loading="partNumberLoading"
              >
                <a-select-option
                  v-for="item in partNumberList"
                  :key="item.id"
                  :value="item.id"
                  >{{ item.name }} ({{ item.code }})</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="序号" name="serialNumber">
              <a-input v-model:value="formData.serialNumber" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="维修类型" name="repairTypeIds">
              <a-select 
                v-model:value="formData.repairTypeIds" 
                mode="multiple"
                placeholder="请选择维修类型"
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
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="入库时间" name="storageTime">
              <a-date-picker
                v-model:value="formData.storageTime"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                show-time
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="计划完工时间" name="plannedCompletionTime">
              <a-date-picker
                v-model:value="formData.plannedCompletionTime"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                show-time
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="任务状态" name="taskStatus">
              <a-select v-model:value="formData.taskStatus">
                <a-select-option
                  v-for="item in $enums.TASK_STATUS.values()"
                  :key="item.code"
                  :value="item.code"
                  >{{ item.desc }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="任务类型" name="taskType">
              <a-select v-model:value="formData.taskType">
                <a-select-option
                  v-for="item in $enums.TASK_TYPE.values()"
                  :key="item.code"
                  :value="item.code"
                  >{{ item.desc }}</a-select-option
                >
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-item label="其他维修需求" name="otherRepairRequirements" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model:value="formData.otherRepairRequirements" :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-item label="备注" name="description" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model:value="formData.description" :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/maintenance/contract-task/index';
  import * as machineTypeApi from '@/api/base-data/machine-type';
  import * as partNumberApi from '@/api/base-data/part-number';
  import * as repairTypeApi from '@/api/base-data/repair-type';

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
        // 是否显示提交加载框
        submitLoading: false,
        // 机型列表
        machineTypeList: [],
        machineTypeLoading: false,
        // 件号列表
        partNumberList: [],
        partNumberLoading: false,
        // 维修类型列表
        repairTypeList: [],
        repairTypeLoading: false,
        // 表单数据
        formData: {},
        // 表单校验规则
        rules: {
          machineTypeId: [{ required: true, message: '请选择机型' }],
          partNumberId: [{ required: true, message: '请选择件号' }],
          serialNumber: [{ required: true, message: '请输入序号' }],
          repairTypeIds: [{ required: true, type: 'array', message: '请选择维修类型' }],
          storageTime: [{ required: true, message: '请选择入库时间' }],
          plannedCompletionTime: [{ required: true, message: '请选择计划完工时间' }],
          taskStatus: [{ required: true, message: '请选择任务状态' }],
          taskType: [{ required: true, message: '请选择任务类型' }],
        },
      };
    },
    created() {
      this.initFormData();
      // 加载机型列表
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
          repairTypeIds: [],
          otherRepairRequirements: '',
          storageTime: '',
          plannedCompletionTime: '',
          taskType: '',
          taskStatus: '',
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
            // 设置表单数据
            this.formData = {
              ...data,
              // 将维修类型列表转换为ID数组
              repairTypeIds: data.repairTypes ? data.repairTypes.map(item => item.id) : [],
            };
            
            // 如果有机型ID，加载对应的件号列表
            if (this.formData.machineTypeId) {
              this.loadPartNumberList(this.formData.machineTypeId);
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 提交表单
      handleOk() {
        this.$refs.formRef.validate().then(() => {
          this.submitLoading = true;
          
          // 构建提交的数据
          const data = {
            id: this.formData.id,
            machineTypeId: this.formData.machineTypeId,
            partNumberId: this.formData.partNumberId,
            serialNumber: this.formData.serialNumber,
            repairTypeIds: this.formData.repairTypeIds,
            otherRepairRequirements: this.formData.otherRepairRequirements,
            storageTime: this.formData.storageTime,
            plannedCompletionTime: this.formData.plannedCompletionTime,
            taskStatus: this.formData.taskStatus,
            taskType: this.formData.taskType,
            description: this.formData.description,
          };
          
          api
            .update(data)
            .then(() => {
              this.$message.success('修改成功');
              this.closeDialog();
              this.$emit('confirm');
            })
            .finally(() => {
              this.submitLoading = false;
            });
        });
      },
      // 取消
      handleCancel() {
        this.closeDialog();
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
      // 处理机型变更
      handleMachineTypeChange(machineTypeId) {
        // 清空件号
        this.formData.partNumberId = '';
        this.partNumberList = [];
        
        if (machineTypeId) {
          // 加载件号列表
          this.loadPartNumberList(machineTypeId);
        }
      },
      // 加载件号列表
      loadPartNumberList(machineTypeId) {
        this.partNumberLoading = true;
        partNumberApi.selector({ machineTypeId }).then((res) => {
          this.partNumberList = res.datas || [];
        }).finally(() => {
          this.partNumberLoading = false;
        });
      },
      // 加载维修类型列表
      loadRepairTypeList() {
        this.repairTypeLoading = true;
        repairTypeApi.selector({}).then((res) => {
          this.repairTypeList = res.datas || [];
        }).finally(() => {
          this.repairTypeLoading = false;
        });
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
      
      // 过滤件号选项
      filterPartNumberOption(input, option) {
        if (!input) return true;
        
        // 获取选项的原始数据
        const item = this.partNumberList.find(item => item.id === option.value);
        if (item) {
          // 直接使用原始数据进行搜索
          const searchText = `${item.name} ${item.code}`.toLowerCase();
          return searchText.indexOf(input.toLowerCase()) >= 0;
        }
        
        return false;
      },
    },
  });
</script>
<style scoped>
.ant-form-item {
  margin-bottom: 12px;
}
</style>
