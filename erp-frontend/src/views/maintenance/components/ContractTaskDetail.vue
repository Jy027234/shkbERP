<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="80%"
    :title="getTaskTitle()"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <a-descriptions :column="3" bordered>
        <a-descriptions-item label="合同编号" :span="1">
          {{ formData.contractCode }}
        </a-descriptions-item>
        <a-descriptions-item label="客户名称" :span="1">
          {{ formData.customerName }}
        </a-descriptions-item>
        <a-descriptions-item label="机型" :span="1">
          {{ formData.machineTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="件号" :span="1">
          {{ formData.partNumberCode }}
        </a-descriptions-item>
        <a-descriptions-item label="序号" :span="1">
          {{ formData.serialNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="维修类型" :span="1">
          {{ formData.repairType }}
        </a-descriptions-item>
        <a-descriptions-item label="其他维修需求" :span="3">
          {{ formData.otherRepairRequirements }}
        </a-descriptions-item>
        <a-descriptions-item label="入库时间" :span="1">
          {{ formData.storageTime }}
        </a-descriptions-item>
        <a-descriptions-item label="计划完工时间" :span="2">
          {{ formData.plannedCompletionTime }}
        </a-descriptions-item>
        <a-descriptions-item label="维修状态" :span="1">
          {{ formData.repairStatus }}
        </a-descriptions-item>
        <a-descriptions-item label="航材状态" :span="1">
          {{ formData.materialStatusName }}
        </a-descriptions-item>
        <a-descriptions-item label="任务状态" :span="1">
          {{ formData.taskStatusName }}
        </a-descriptions-item>

        <a-descriptions-item label="派发至" :span="1">
          {{ formData.dispatch }}
        </a-descriptions-item>
        <a-descriptions-item label="工卡列表" :span="3">
          {{ formData.workCardNumberList }}
        </a-descriptions-item>
        <a-descriptions-item label="其他工卡" :span="3">
          {{ formData.otherWorkCardNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="非必换件单号" :span="1">
          {{ formData.otherReplacementPartNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="必换件单号" :span="1">
          {{ formData.replacementPartNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="放行文件" :span="1">
          {{ formData.approvalFile }}
        </a-descriptions-item>
        <a-descriptions-item label="放行文件编号" :span="1">
          {{ formData.approvalFileNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ formData.description }}
        </a-descriptions-item>
        <a-descriptions-item label="退修原因" :span="2">
          {{ formData.returnRepairReason }}
        </a-descriptions-item>
      </a-descriptions>
      
      <!-- 操作按钮区域 -->
      <div class="footer-button" style="margin-top: 16px; text-align: right;">
        <a-button @click="closeDialog">关闭</a-button>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as contractTaskApi from '@/api/maintenance/contract-task';

  export default defineComponent({
    name: 'ContractTaskDetail',
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
        // 领料申请相关
        materialApplyVisible: false,
        materialApplyLoading: false,
        materialApplySubmitting: false,
        materialApplyForm: {
          remark: ''
        }
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
      // 获取任务标题
      getTaskTitle() {
        // 根据当前路径判断是哪种类型的任务
        const taskType = this.getTaskType();
        const typeMap = {
          'AVIATION': '民航维修执行任务详情',
          'FACTORY_WB': 'WB厂返修执行任务详情',
          'FACTORY_L': 'L厂返修执行任务详情'
        };
        
        return typeMap[taskType] || '合同任务详情';
      },
      
      // 获取任务类型
      getTaskType() {
        // 通过window.location.pathname获取当前路径
        const path = window.location.pathname;
        
        if (path.includes('/maintenance/factory-wb')) {
          return 'FACTORY_WB';
        } else if (path.includes('/maintenance/factory-l')) {
          return 'FACTORY_L';
        } else {
          return 'AVIATION';
        }
      },
      
      // 初始化表单数据
      initFormData() {
        this.formData = {
          id: '',
          contractCode: '',
          customerName: '',
          machineTypeName: '',
          partNumberName: '',
          partNumberCode: '',
          serialNumber: '',
          repairType: '',
          otherRepairRequirements: '',
          storageTime: '',
          plannedCompletionTime: '',
          workCardNumberList: [],
          otherWorkCardNumber: '',
          otherReplacementPartNumber: '',
          replacementPartNumber: '',
          approvalFile: '',
          approvalFileNumber: '',
          repairStatus: '',
          materialStatusName: '',
          description: '',
          createBy: '',
          createTime: '',
          taskStatusName: '',
          dispatch: '',
          returnRepairReason: ''
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
        
        // 获取当前任务类型
        const taskType = this.getTaskType();
        
        // 使用合同任务API获取真实数据
        contractTaskApi.get(this.id, { taskType })
          .then(data => {
            if (data) {
              // 将API返回的数据映射到表单数据
              this.formData = {
                id: data.id,
                contractCode: data.contractCode,
                customerName: data.customerName,
                machineTypeName: data.machineTypeName,
                partNumberName: data.partNumberName,
                partNumberCode: data.partNumberCode,
                serialNumber: data.serialNumber,
                repairType: this.formatRepairTypes(data.repairTypes),
                otherRepairRequirements: data.otherRepairRequirements || '',
                storageTime: data.storageTime,
                plannedCompletionTime: data.plannedCompletionTime,
                workCardNumberList: this.formatWorkCardList(data.workCards),
                otherWorkCardNumber: data.otherWorkCardNumber || '',
                otherReplacementPartNumber: data.contractCode + 'FBH',
                replacementPartNumber: data.contractCode + 'BHJ',
                approvalFile: data.approvalFileNumber ? '已上传' : '未上传',
                approvalFileNumber: data.approvalFileNumber || '',
                repairStatus: this.formatRepairStatus(data.repairStatus),
                materialStatusName: data.materialStatusName,
                description: data.description || '',
                createBy: data.createBy,
                dispatch: data.dispatch,
                createTime: data.createTime,
                taskStatusName: data.taskStatusName,
                returnRepairReason: data.returnRepairReason
              };
            } else {
              this.$message.error('获取任务详情失败：数据不存在');
            }
          })
          .catch(error => {
            console.error('获取任务详情失败:', error);
            this.$message.error(`获取任务详情失败: ${error.message || '未知错误'}`);
          })
          .finally(() => {
            this.loading = false;
          });
      },
      
      // 格式化维修类型
      formatRepairTypes(repairTypes) {
        if (!repairTypes || !repairTypes.length) return '';
        return repairTypes.map(type => type.name).join(',');
      },
      
      // 格式化工卡列表
      formatWorkCardList(workCards) {
        if (!workCards || !workCards.length) return '';
        return workCards.map(card => card.workCardName).join(', ');
      },
      
      // 格式化维修状态
      formatRepairStatus(status) {
        const statusMap = {
          'WAIT_CHECK': '待检查',
          'CHECKING': '检查中',
          'REPAIRING': '维修中',
          'WAITING_FOR_PARTS': '等料暂停',
          'PAUSED_OTHER': '其他暂停',
          'COMPLETED': '完工'
        };
        
        return statusMap[status] || status;
      }
    },
  });
</script>
