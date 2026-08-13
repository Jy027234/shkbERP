<template>
    <div>
      <div v-permission="[permissionCode]">
        <page-wrapper content-full-height fixed-height>
          <!-- 数据列表 -->
          <vxe-grid
            :id="gridId"
            ref="grid"
            resizable
            show-overflow
            highlight-hover-row
            keep-source
            row-id="id"
            :proxy-config="proxyConfig"
            :columns="tableColumn"
            :toolbar-config="toolbarConfig"
            :custom-config="{}"
            :pager-config="pagerConfig"
            :loading="loading"
            :checkbox-config="{highlight: true, trigger: 'row'}"
            @checkbox-change="handleCheckboxChange"
            @checkbox-all="handleCheckboxAll"
            height="auto"
          >
            <template #form>
              <j-border>
                <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                  <j-form-item label="合同编号">
                    <a-input v-model:value="searchFormData.contractCode" allow-clear />
                  </j-form-item>
                  <j-form-item label="客户">
                    <customer-selector v-model:value="searchFormData.customerId" @update:value="handleCustomerChange" />
                  </j-form-item>
                  <j-form-item label="入库时间" :content-nest="false">
                    <div class="date-range-container">
                      <a-date-picker
                        v-model:value="searchFormData.storageTimeStart"
                        placeholder=""
                        value-format="YYYY-MM-DD 00:00:00"
                      />
                      <span class="date-split">至</span>
                      <a-date-picker
                        v-model:value="searchFormData.storageTimeEnd"
                        placeholder=""
                        value-format="YYYY-MM-DD 23:59:59"
                      />
                    </div>
                  </j-form-item>
                  <j-form-item label="机型">
                    <a-select 
                      v-model:value="searchFormData.machineTypeId" 
                      allow-clear
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
                        >{{ item.name }}</a-select-option
                      >
                    </a-select>
                  </j-form-item>
                  <j-form-item label="件号">
                    <a-input 
                      v-model:value="searchFormData.partNumberCode" 
                      allow-clear
                      placeholder="请输入件号"
                    />
                  </j-form-item>
                  <j-form-item label="计划完工时间" :content-nest="false">
                    <div class="date-range-container">
                      <a-date-picker
                        v-model:value="searchFormData.plannedCompletionTimeStart"
                        placeholder=""
                        value-format="YYYY-MM-DD 00:00:00"
                      />
                      <span class="date-split">至</span>
                      <a-date-picker
                        v-model:value="searchFormData.plannedCompletionTimeEnd"
                        placeholder=""
                        value-format="YYYY-MM-DD 23:59:59"
                      />
                    </div>
                  </j-form-item>
                  <j-form-item label="任务状态">
                    <a-select v-model:value="searchFormData.taskStatus" allow-clear>
                      <a-select-option
                        v-for="item in $enums.TASK_STATUS.values()"
                        :key="item.code"
                        :value="item.code"
                        >{{ item.desc }}</a-select-option
                      >
                    </a-select>
                  </j-form-item>
                  <j-form-item label="维修状态">
                    <a-select v-model:value="searchFormData.repairStatus" allow-clear placeholder="请选择维修状态">
                      <a-select-option
                        v-for="item in repairStatusOptions"
                        :key="item.code"
                        :value="item.code"
                        >{{ item.desc }}</a-select-option
                      >
                    </a-select>
                  </j-form-item>
                  <j-form-item label="放行文件编号">
                    <a-input v-model:value="searchFormData.approvalFileNumber" allow-clear />
                  </j-form-item>
                </j-form>
              </j-border>
            </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-divider type="vertical" />
              
              <!-- 操作按钮组 -->
              <a-button :disabled="!selectedRowKeys.length" :icon="h(SolutionOutlined)" @click="handleWorkCard">
                工卡管理
              </a-button>

              <a-button :disabled="!selectedRowKeys.length" :icon="h(FileTextOutlined)" @click="handleReplacementParts">
                必换件单
              </a-button>
               
              <a-button :disabled="!selectedRowKeys.length" :icon="h(FileOutlined)" @click="handleNonReplacementParts">
                非必换件申请单
              </a-button>
              
              <a-button :disabled="!selectedRowKeys.length" :icon="h(InboxOutlined)" @click="handleMaterialRequest">
                领料申请
              </a-button>
              
              <a-button :disabled="!selectedRowKeys.length" :icon="h(SettingOutlined)" @click="handleStatusManagement">
                状态管理
              </a-button>   
              
              <a-button :disabled="!selectedRowKeys.length" :icon="h(CloudUploadOutlined)" @click="handleApprovalFile">
                放行文件管理
              </a-button>
              
              <a-button :disabled="!selectedRowKeys.length" :icon="h(CloseCircleOutlined)" @click="handleCloseTask">
                任务关闭
              </a-button>

              <a-button :disabled="!selectedRowKeys.length" :icon="h(CloseCircleOutlined)" @click="handleReturnedTask">
                任务退修
              </a-button>
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <available-tag :available="row.available" />
          </template>

          <!-- 放行文件列自定义内容 -->
          <template #approvalFile_default="{ row }">
            <a v-if="row.approvalFile" @click="viewApprovalFile(row)">查看</a>
            <span v-else>未上传</span>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>

    <!-- 查看详情对话框 -->
    <contract-task-detail ref="viewDialog" :id="id" />
    
    <!-- 状态管理窗口 -->
    <status-management
      v-model:visible="statusModalVisible"
      :confirmLoading="statusConfirmLoading"
      :selectedTasks="selectedRows"
      :defaultStatus="getDefaultRepairStatus()"
      @confirm="handleStatusConfirm"
    />
    
    <!-- 工卡管理窗口 -->
    <work-card-management
      v-model:visible="workCardModalVisible"
      :tasks="selectedRows"
      :taskId="selectedRows.length === 1 ? selectedRows[0].id : ''"
      @confirm="handleWorkCardConfirm"
    />
    
    <!-- 必换件管理窗口 -->
    <replacement-part-management
      v-model:visible="replacementPartModalVisible"
      :tasks="selectedRows"
      @confirm="handleReplacementPartConfirm"
    />
    
    <!-- 非必换件管理窗口 -->
    <non-replacement-part-management
      v-model:visible="nonReplacementPartModalVisible"
      :tasks="selectedRows"
      @confirm="handleNonReplacementPartConfirm"
    />
    
    <!-- 领料申请弹窗 -->
    <a-modal
      v-model:open="materialApplyModalVisible"
      title="领料申请"
      :maskClosable="false"
      :width="500"
      :footer="null"
    >
    <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <div class="selected-tasks-title">已选择任务：</div>
      <div class="selected-tasks-list">
        <div v-for="row in selectedRows" :key="row.id" class="selected-task-item">
          合同编号：{{ row.contractCode }} 机型：{{ row.machineTypeName }} 件号：{{ row.partNumberCode }}
        </div>
      </div>
      
      <a-form-item label="备注" :wrapper-col="{ span: 20 }">
        <a-textarea
          v-model:value="materialApplyForm.remark"
          placeholder="请输入备注信息（选填）"
          :rows="4"
          :maxlength="200"
          show-count
        />
      </a-form-item>
      
      <a-form-item :wrapper-col="{ span: 24 }" style="text-align: right;">
        <a-space>
          <a-button @click="materialApplyModalVisible = false">取消</a-button>
          <a-button type="primary" :loading="materialApplyLoading" @click="submitMaterialApply">确认提交</a-button>
        </a-space>
      </a-form-item>
    </a-form>
    </a-modal>
    
    <!-- 任务关闭确认弹窗 -->
    <a-modal
      v-model:open="closeTaskModalVisible"
      title="确认关闭任务"
      :maskClosable="false"
      :keyboard="false"
      :closable="true"
      @cancel="closeTaskModalVisible = false"
    >
      <div>
        <p>确定要关闭以下任务吗？</p>
        <div class="selected-tasks-list">
          <div v-for="(row, index) in selectedRows" :key="row.id" class="selected-task-item">
            合同编号：{{ row.contractCode }} 客户：{{ row.customerName }} 机型：{{ row.machineTypeName || '未知机型' }} 件号：{{ row.partNumberCode }}
          </div>
        </div>
        <p class="mt-2">关闭后任务将不能再进行维修操作。</p>
      </div>
      <template #footer>
        <a-button @click="closeTaskModalVisible = false">取消</a-button>
        <a-button type="primary" :loading="taskStatusLoading" @click="confirmCloseTask">确定</a-button>
      </template>
    </a-modal>
    
    <!-- 任务退修确认弹窗 -->
    <a-modal
      v-model:open="returnTaskModalVisible"
      title="确认任务退修"
      :maskClosable="false"
      :keyboard="false"
      :closable="true"
      @cancel="returnTaskModalVisible = false"
    >
      <div>
        <p>确定要将以下任务标记为退修吗？</p>
        <div class="selected-tasks-list">
          <div v-for="(row, index) in selectedRows" :key="row.id" class="selected-task-item">
             合同编号：{{ row.contractCode }} 客户：{{ row.customerName }} 机型：{{ row.machineTypeName || '未知机型' }} 件号：{{ row.partNumberCode }}
          </div>
        </div>
        <a-form layout="vertical">
          <a-form-item label="退修原因" required>
            <a-textarea
              v-model:value="returnTaskReason"
              :rows="4"
              placeholder="请输入退修原因"
              :maxlength="500"
              show-count
            />
          </a-form-item>
        </a-form>
      </div>
      <template #footer>
        <a-button @click="returnTaskModalVisible = false">取消</a-button>
        <a-button type="primary" :loading="taskStatusLoading" @click="confirmReturnTask">确定</a-button>
      </template>
    </a-modal>
  
    <contract-task-approval-manage ref="approvalManageDialog" />
  </div>
</template>
<script>
import { defineComponent, h, reactive, ref } from 'vue';
import {
  CheckOutlined,
  CloudUploadOutlined,
  DownOutlined,
  EditOutlined,
  FileOutlined,
  FileTextOutlined,
  PlusOutlined,
  SearchOutlined,
  SettingOutlined,
  SolutionOutlined,
  ThunderboltOutlined,
  ToolOutlined,
  InboxOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons-vue';
import * as contractTaskApi from '@/api/maintenance/contract-task';
import * as repairTypeApi from '@/api/base-data/repair-type';
import * as customerApi from '@/api/base-data/customer';
import * as machineTypeApi from '@/api/base-data/machine-type';
import * as partNumberApi from '@/api/base-data/part-number';
import * as api from '@/api/maintenance/aviation';
import ContractTaskDetail from '@/views/maintenance/components/ContractTaskDetail.vue';
import ContractTaskApprovalManage from '@/views/maintenance/components/ContractTaskApprovalManage.vue';
import StatusManagement from '@/views/maintenance/components/StatusManagement.vue';
import WorkCardManagement from '@/views/maintenance/components/WorkCardManagement.vue';
import ReplacementPartManagement from '@/views/maintenance/components/ReplacementPartManagement.vue';
import NonReplacementPartManagement from '@/views/maintenance/components/NonReplacementPartManagement.vue';

export default defineComponent({
  name: 'ContractTaskList',
  components: {
    ContractTaskDetail,
    ContractTaskApprovalManage,
    StatusManagement,
    WorkCardManagement,
    ReplacementPartManagement,
    NonReplacementPartManagement
  },
  props: {
    // 合同任务类型：AVIATION, FACTORY_WB, FACTORY_L
    contractType: {
      type: String,
      required: true,
      validator: (value) => {
        return ['AVIATION', 'FACTORY_WB', 'FACTORY_L'].includes(value);
      }
    }
  },
  setup(props) {
    // 根据合同类型获取权限代码
    const getPermissionCode = () => {
      const typeMap = {
        'AVIATION': 'maintenance:aviation',
        'FACTORY_WB': 'maintenance:factory-wb',
        'FACTORY_L': 'maintenance:factory-l'
      };
      return typeMap[props.contractType];
    };

    // 根据合同类型获取表格ID
    const getGridId = () => {
      const typeMap = {
        'AVIATION': 'MaintenanceAviation',
        'FACTORY_WB': 'MaintenanceFactoryWb',
        'FACTORY_L': 'MaintenanceFactoryL'
      };
      return typeMap[props.contractType];
    };

    return {
      permissionCode: getPermissionCode(),
      gridId: getGridId(),
      h,
      SearchOutlined,
      PlusOutlined,
      ThunderboltOutlined,
      EditOutlined,
      CheckOutlined,
      CloudUploadOutlined,
      FileOutlined,
      FileTextOutlined,
      SettingOutlined,
      SolutionOutlined,
      ToolOutlined,
      InboxOutlined,
      CloseCircleOutlined
    };
  },
  data() {
    return {
      // 模拟数据
      mockData: [
        {
          id: '1',
          contractCode: 'HT-2025-001',
          customerName: '中国航空公司',
          machineTypeName: 'B737',
          machineTypeCode: 'B737',
          partNumberName: 'ECU-001',
          partNumberCode: 'ECU-001',
          serialNumber: 'SN20250001',
          repairTypesLabel: '大修,定检',
          otherRepairRequirements: '检查燃油系统',
          storageTime: '2025-05-01 10:00:00',
          plannedCompletionTime: '2025-06-01 18:00:00',
          workCardNumberList: 'WC-001, WC-002',
          otherWorkCardNumber: 'WC-EXT-001',
          otherReplacementPartNumber: 'HT-2025-001-FBH',
          replacementPartNumber: 'HT-2025-001-BHJ',
          approvalFile: '已上传',
          approvalFileNumber: 'AP-2025-001',
          available: 'WAIT_DISPATCH',
          repairStatusLabel: '待检查',
          description: '优先处理',
          createBy: '张三',
          createTime: '2025-05-01 09:00:00'
        },
        {
          id: '2',
          contractCode: 'HT-2025-002',
          customerName: '南方航空',
          machineTypeName: 'A320',
          machineTypeCode: 'A320',
          partNumberName: 'LG-002',
          partNumberCode: 'LG-002',
          serialNumber: 'SN20250002',
          repairTypesLabel: '中修,特检',
          otherRepairRequirements: '更换密封件',
          storageTime: '2025-05-05 14:30:00',
          plannedCompletionTime: '2025-06-10 18:00:00',
          workCardNumberList: 'WC-003, WC-004',
          otherWorkCardNumber: 'WC-EXT-002',
          otherReplacementPartNumber: 'HT-2025-002-FBH',
          replacementPartNumber: 'HT-2025-002-BHJ',
          approvalFile: '已上传',
          approvalFileNumber: 'AP-2025-002',
          available: 'EXECUTING',
          repairStatusLabel: '维修中',
          description: '标准流程',
          createBy: '李四',
          createTime: '2025-05-05 13:00:00'
        }
      ],
      // 是否使用模拟数据
      useMockData: false,
      // 加载状态
      loading: false,
      // 表格列
      tableColumn: [
        { type: 'checkbox', width: 50 },
        { type: 'seq', width: 50 },
        { field: 'taskStatusName', title: '任务状态', width: 100 },
        { field: 'repairStatusLabel', title: '维修状态', width: 100 },
        { field: 'materialStatusName', title: '航材状态', width: 100 },
        { field: 'contractCode', title: '合同编号', width: 120 },
        { field: 'customerName', title: '客户名称', width: 120 },
        { field: 'machineTypeName', title: '机型', width: 180 },
        { field: 'partNumberCode', title: '件号', width: 180 },
        { field: 'serialNumber', title: '序号', width: 180 },
        { field: 'repairTypesLabel', title: '维修类型', width: 180 },
        { field: 'otherRepairRequirements', title: '其他维修需求', width: 180 },
        { field: 'storageTime', title: '入库时间', width: 180, sortable: true },
        { field: 'plannedCompletionTime', title: '计划完工时间', width: 180, sortable: true },
        { field: 'workCardNumberList', title: '工卡列表', width: 180 },
        { field: 'otherWorkCardNumber', title: '其他工卡', width: 180 },
        { field: 'otherReplacementPartNumber', title: '非必换件单号', width: 180 },
        { field: 'replacementPartNumber', title: '必换件单号', width: 180 },
        { field: 'approvalFileNumber', title: '放行文件编号', width: 180 },
        { field: 'description', title: '备注', minWidth: 200 },
        { field: 'createBy', title: '创建人', width: 100 },
        { field: 'createTime', title: '创建时间', width: 170, sortable: true },
        { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
      ],
      // 工具栏配置
      toolbarConfig: {
        // 自定义左侧工具栏
        slots: {
          buttons: 'toolbar_buttons',
        },
      },
      // 分页配置
      pagerConfig: {
        pageSize: 20,
        pageSizes: [10, 20, 50, 100],
        layouts: ['PrevPage', 'Number', 'NextPage', 'Sizes', 'Total']
      },
      // 代理配置
      proxyConfig: {
        props: {
          // 响应结果列表字段
          result: 'datas',
          // 响应结果总条数字段
          total: 'totalCount',
        },
        ajax: {
          // 查询接口 - 根据配置使用模拟数据或真实数据
          query: ({ page }) => {
            // 如果使用模拟数据
            if (this.useMockData) {
              // 模拟搜索功能
              let filteredData = [...this.mockData];
              
              // 根据搜索条件过滤数据
              if (this.searchFormData.contractCode) {
                filteredData = filteredData.filter(item => 
                  item.contractCode && item.contractCode.toLowerCase().includes(this.searchFormData.contractCode.toLowerCase())
                );
              }
            
              if (this.searchFormData.customerId) {
                // 在实际情况下，这里应该根据customerId匹配，但在模拟数据中我们用customerName
                filteredData = filteredData.filter(item => 
                  item.customerName && this.searchFormData.customerName && 
                  item.customerName.includes(this.searchFormData.customerName)
                );
              }
              
              if (this.searchFormData.machineTypeId) {
                // 在模拟数据中我们用aircraftType字段
                const selectedMachineType = this.machineTypeList.find(item => item.id === this.searchFormData.machineTypeId);
                if (selectedMachineType && selectedMachineType.code) {
                  filteredData = filteredData.filter(item => 
                    item.aircraftType && item.aircraftType.includes(selectedMachineType.code)
                  );
                }
              }
              
              if (this.searchFormData.partNumberId) {
                // 在模拟数据中我们用partNumber字段
                const selectedPartNumber = this.partNumberList.find(item => item.id === this.searchFormData.partNumberId);
                if (selectedPartNumber && selectedPartNumber.code) {
                  filteredData = filteredData.filter(item => 
                    item.partNumber && item.partNumber.includes(selectedPartNumber.code)
                  );
                }
              }
              
              if (this.searchFormData.repairTypeIds && this.searchFormData.repairTypeIds.length > 0) {
                // 在模拟数据中我们用repairType字段
                filteredData = filteredData.filter(item => {
                  if (!item.repairType) return false;
                  
                  // 检查是否至少包含一个选中的维修类型
                  const selectedRepairTypes = this.searchFormData.repairTypeIds.map(id => {
                    const type = this.repairTypeList.find(item => item.id === id);
                    return type ? type.name : null;
                  }).filter(Boolean);
                  
                  return selectedRepairTypes.some(type => item.repairType.includes(type));
                });
              }
              
              if (this.searchFormData.storageTimeStart) {
                filteredData = filteredData.filter(item => 
                  item.storageTime && item.storageTime >= this.searchFormData.storageTimeStart
                );
              }
              
              if (this.searchFormData.storageTimeEnd) {
                filteredData = filteredData.filter(item => 
                  item.storageTime && item.storageTime <= this.searchFormData.storageTimeEnd
                );
              }
              
              if (this.searchFormData.plannedCompletionTimeStart) {
                filteredData = filteredData.filter(item => 
                  item.plannedCompletionTime && item.plannedCompletionTime >= this.searchFormData.plannedCompletionTimeStart
                );
              }
              
              if (this.searchFormData.plannedCompletionTimeEnd) {
                filteredData = filteredData.filter(item => 
                  item.plannedCompletionTime && item.plannedCompletionTime <= this.searchFormData.plannedCompletionTimeEnd
                );
              }
              
              if (this.searchFormData.approvalFileNumber) {
                filteredData = filteredData.filter(item => 
                  item.approvalFileNumber && item.approvalFileNumber.toLowerCase().includes(this.searchFormData.approvalFileNumber.toLowerCase())
                );
              }
              
              if (this.searchFormData.taskStatus) {
                // 模拟数据中没有taskStatus字段，所以这里不进行过滤
                // 实际应用中应该有这个字段
              }
              
              // 返回模拟数据
              return Promise.resolve({
                datas: filteredData,
                totalCount: filteredData.length
              });
            } else {
              // 使用真实数据 - 调用后端接口
              const params = this.buildQueryParams(page);
              params.taskType = this.contractType;
              return api.query(params);
            }
          },
        }
      },
      // 查询表单数据
      searchFormData: {
        contractCode: '', // 合同编号
        customerId: '', // 客户ID
        customerName: '', // 客户名称
        machineTypeId: '', // 机型ID
        partNumberCode: '', // 件号编码
        repairTypeIds: [], // 维修类型
        storageTimeStart: '', // 入库时间开始
        storageTimeEnd: '', // 入库时间结束
        plannedCompletionTimeStart: '', // 计划完工时间开始
        plannedCompletionTimeEnd: '', // 计划完工时间结束
        taskStatus: '', // 任务状态
        repairStatus: '', // 维修状态
        approvalFileNumber: '', // 放行文件编号
      },
      // 机型列表
      machineTypeList: [],
      // 机型加载状态
      machineTypeLoading: false,
      // 件号列表
      partNumberList: [],
      // 件号加载状态
      partNumberLoading: false,
      // 维修类型列表
      repairTypeList: [],
      // 维修类型加载状态
      repairTypeLoading: false,
      // 选中的行键
      selectedRowKeys: [],
      // 选中的行
      selectedRows: [],
      // 查看详情的ID
      id: '',
      // 领料申请相关
      materialApplyModalVisible: false,
      materialApplyLoading: false,
      materialApplyForm: {
        remark: ''
      },
      // 维修状态相关
      statusModalVisible: false,
      statusConfirmLoading: false,
      statusForm: {
        repairStatus: undefined,
        remark: ''
      },
      // 工卡管理相关
      workCardModalVisible: false,
      // 必换件管理相关
      replacementPartModalVisible: false,
      // 非必换件管理相关
      nonReplacementPartModalVisible: false,
      // 任务关闭相关
      closeTaskModalVisible: false,
      taskStatusLoading: false,
      // 任务退修相关
      returnTaskModalVisible: false,
      returnTaskReason: '',
      // 放行文件管理
      approvalFileModalVisible: false,
      approvalFileUploading: false,
      approvalUploadList: [],
      approvalFiles: [],
      approvalTaskId: '',
      approvalTask: null,
      approvalListLoading: false,
      // 维修状态选项（与后端 RepairStatus 枚举 code 对应）
      repairStatusOptions: [
        { code: 'WAIT_CHECK', desc: '待检查' },
        { code: 'CHECKING', desc: '检查中' },
        { code: 'WAIT_ASSEMBLY', desc: '待装配' },
        { code: 'REPAIRING', desc: '维修中' },
        { code: 'PAUSE_MATERIAL', desc: '等料暂停' },
        { code: 'PAUSE_OTHER', desc: '其他暂停' },
        { code: 'TESTING', desc: '测试中' },
        { code: 'COMPLETED', desc: '完工' }
      ],
    };
  },
  mounted() {
    // 加载机型数据
    this.loadMachineTypes();
    // 加载维修类型数据
    this.loadRepairTypes();
  },
  methods: {
    // 构建查询参数
    buildQueryParams(page) {
      const params = {};
      
      // 合同编号
      if (this.searchFormData.contractCode) {
        params.contractCode = this.searchFormData.contractCode;
      }
      
      // 客户ID
      if (this.searchFormData.customerId) {
        params.customerId = this.searchFormData.customerId;
      }
      
      // 机型ID
      if (this.searchFormData.machineTypeId) {
        params.machineTypeId = this.searchFormData.machineTypeId;
      }
      
      // 件号编码
      if (this.searchFormData.partNumberCode) {
        params.partNumberCode = this.searchFormData.partNumberCode;
      }
      
      // 维修类型IDs
      if (this.searchFormData.repairTypeIds && this.searchFormData.repairTypeIds.length > 0) {
        params.repairTypeIds = this.searchFormData.repairTypeIds;
      }
      
      // 入库时间范围
      if (this.searchFormData.storageTimeStart) {
        params.storageTimeStart = this.searchFormData.storageTimeStart;
      }
      
      if (this.searchFormData.storageTimeEnd) {
        params.storageTimeEnd = this.searchFormData.storageTimeEnd;
      }
      
      // 计划完工时间范围
      if (this.searchFormData.plannedCompletionTimeStart) {
        params.plannedCompletionTimeStart = this.searchFormData.plannedCompletionTimeStart;
      }
      
      if (this.searchFormData.plannedCompletionTimeEnd) {
        params.plannedCompletionTimeEnd = this.searchFormData.plannedCompletionTimeEnd;
      }
      
      // 任务状态
      if (this.searchFormData.taskStatus) {
        params.taskStatus = this.searchFormData.taskStatus;
      }

      // 维修状态
      if (this.searchFormData.repairStatus) {
        params.repairStatus = this.searchFormData.repairStatus;
      }
      
      // 放行文件编号
      if (this.searchFormData.approvalFileNumber) {
        params.approvalFileNumber = this.searchFormData.approvalFileNumber;
      }
      
      // 分页参数
      if (page) {
        params.pageIndex = page.currentPage;
        params.pageSize = page.pageSize;
      }
      
      return params;
    },

    // 查询所有数据（用于导出等功能）
    queryAllData() {
      const params = this.buildAllQueryParams();
      params.taskType = this.contractType;
      
      return contractTaskApi.queryAll(params)
        .then(res => {
          return {
            result: true,
            data: res
          };
        })
        .catch(error => {
          console.error('查询所有合同任务失败:', error);
          this.$message.error(`查询失败: ${error.message || '未知错误'}`);
          return {
            result: false
          };
        });
    },

    // 构建查询参数（无分页，用于导出等场景）
    buildAllQueryParams() {
      const params = {};
      
      if (this.searchFormData.contractCode) {
        params.contractCode = this.searchFormData.contractCode;
      }
      
      if (this.searchFormData.customerId) {
        params.customerId = this.searchFormData.customerId;
      }
      
      if (this.searchFormData.storageTimeStart) {
        params.storageTimeStart = this.searchFormData.storageTimeStart;
      }
      
      if (this.searchFormData.storageTimeEnd) {
        params.storageTimeEnd = this.searchFormData.storageTimeEnd;
      }
      
      if (this.searchFormData.machineTypeId) {
        params.machineTypeId = this.searchFormData.machineTypeId;
      }
      
      if (this.searchFormData.partNumberCode) {
        params.partNumberCode = this.searchFormData.partNumberCode;
      }
      
      if (this.searchFormData.plannedCompletionTimeStart) {
        params.plannedCompletionTimeStart = this.searchFormData.plannedCompletionTimeStart;
      }
      
      if (this.searchFormData.plannedCompletionTimeEnd) {
        params.plannedCompletionTimeEnd = this.searchFormData.plannedCompletionTimeEnd;
      }
      
      if (this.searchFormData.taskStatus) {
        params.taskStatus = this.searchFormData.taskStatus;
      }
      
      if (this.searchFormData.repairStatus) {
        params.repairStatus = this.searchFormData.repairStatus;
      }
      
      if (this.searchFormData.approvalFileNumber) {
        params.approvalFileNumber = this.searchFormData.approvalFileNumber;
      }
      
      return params;
    },


    // 搜索
    search() {
      this.$refs.grid.commitProxy('query');
    },

    // 加载机型数据
    loadMachineTypes() {
      this.machineTypeLoading = true;
      machineTypeApi.query({})
        .then(res => {
          this.machineTypeList = res.datas || [];
        })
        .catch(error => {
          console.error('获取机型列表失败:', error);
          this.$message.error(`获取机型列表失败: ${error.message || '未知错误'}`);
        })
        .finally(() => {
          this.machineTypeLoading = false;
        });
    },

    // 加载维修类型数据
    loadRepairTypes() {
      this.repairTypeLoading = true;
      repairTypeApi.query({})
        .then(res => {
          this.repairTypeList = res.datas || [];
        })
        .catch(error => {
          console.error('获取维修类型列表失败:', error);
          this.$message.error(`获取维修类型列表失败: ${error.message || '未知错误'}`);
        })
        .finally(() => {
          this.repairTypeLoading = false;
        });
    },

    // 机型选择过滤
    filterMachineTypeOption(input, option) {
      return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },

    // 处理机型变更
    handleMachineTypeChange(value) {
      // 清空件号选择
      this.searchFormData.partNumberCode = '';
    },

    // 加载件号数据
    loadPartNumbers(machineTypeId) {
      this.partNumberLoading = true;
      partNumberApi.query({ machineTypeId })
        .then(res => {
          this.partNumberList = res.datas || [];
        })
        .catch(error => {
          console.error('获取件号列表失败:', error);
          this.$message.error(`获取件号列表失败: ${error.message || '未知错误'}`);
        })
        .finally(() => {
          this.partNumberLoading = false;
        });
    },

    // 件号选择过滤
    filterPartNumberOption(input, option) {
      return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },

    // 处理客户变更
    handleCustomerChange() {
      // 可以在这里添加客户变更后的逻辑
    },

    // 处理复选框变更
    handleCheckboxChange({ checked, records }) {
      this.selectedRowKeys = this.$refs.grid.getCheckboxRecords().map(item => item.id);
      this.selectedRows = this.$refs.grid.getCheckboxRecords();
    },

    // 处理全选
    handleCheckboxAll({ checked, records }) {
      this.selectedRowKeys = this.$refs.grid.getCheckboxRecords().map(item => item.id);
      this.selectedRows = this.$refs.grid.getCheckboxRecords();
    },

    // 处理工卡管理
    handleWorkCard() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.workCardModalVisible = true;
    },

    // 处理领料申请
    handleMaterialRequest() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.materialApplyForm.remark = '';
      this.materialApplyModalVisible = true;
    },
    
    // 处理状态管理
    handleStatusManagement() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.statusForm.repairStatus = undefined;
      this.statusForm.remark = '';
      this.statusModalVisible = true;
    },

    // 提交领料申请
    async submitMaterialApply() {
      // 领料暂时支持单个任务
      if (this.selectedRowKeys.length !== 1) {
        this.$message.warning('请选择一个任务');
        return;
      }
      // 检查维修状态，维修中才能发起领料申请
      const repairStatusLabel = this.selectedRows[0].repairStatusLabel;
      if (repairStatusLabel !== '维修中') {
        this.$message.warning('维修状态必须为维修中才能发起领料申请');
        return;
      }
      if (this.materialApplyLoading) return;
      
      this.materialApplyLoading = true;
      
      try {
        // 为每个选中的任务发起领料申请
        const promises = this.selectedRows.map(row => {
          contractTaskApi.createMaterialApply({
                taskId: row.id,
                remark: this.materialApplyForm.remark
              });
        });
        
        // 等待所有请求完成
        await Promise.all(promises);
        
        // 重新加载数据
        this.search();
        
        // 清空选中状态
        this.selectedRowKeys = [];
        this.selectedRows = [];
        
        // 关闭弹窗
        this.materialApplyModalVisible = false;
        
        // 重置表单
        this.materialApplyForm.remark = '';
        
        // 提示成功
        this.$message.success('领料申请提交成功！');
      } catch (error) {
        console.error('领料申请提交失败:', error);
        // this.$message.error(`领料申请提交失败: ${error.message || '未知错误'}`);
      } finally {
        this.materialApplyLoading = false;
      }
    },
    
    // 获取默认维修状态
    getDefaultRepairStatus() {
      // 如果没有选中任务，返回默认状态
      if (!this.selectedRows || this.selectedRows.length === 0) {
        return '待检查';
      }
      
      // 如果选中了多个任务，但状态不一致，返回第一个任务的状态
      // 如果所有选中的任务的维修状态都一致，则返回该状态
      const firstTaskStatus = this.selectedRows[0].repairStatusLabel;
      
      // 检查是否所有选中的任务都有相同的维修状态
      const allSameStatus = this.selectedRows.every(task => task.repairStatusLabel === firstTaskStatus);
      
      // 如果所有任务的状态相同，返回该状态，否则返回第一个任务的状态
      return firstTaskStatus || '待检查';
    },
    
    // 处理状态确认
    async handleStatusConfirm(formData) {
      if (!formData.repairStatus) {
        this.$message.warning('请选择维修状态');
        return;
      }
      
      this.statusConfirmLoading = true;
      
      try {
        // 获取状态枚举值
        const statusMap = {
          '待检查': 'WAIT_CHECK',
          '检查中': 'CHECKING',
          '维修中': 'REPAIRING',
          '等料暂停': 'WAITING_FOR_PARTS',
          '其他暂停': 'PAUSED_OTHER',
          '待装配': 'WAIT_ASSEMBLY',
          '测试中': 'TESTING',
          '完工': 'COMPLETED'
        };
        
        // 为每个选中的任务添加维修状态记录
        const promises = this.selectedRows.map(row => {
          // 根据合同类型选择正确的API
          return api.addRepairStatusRecord({
                taskId: row.id,
                repairStatus: statusMap[formData.repairStatus],
                description: formData.remark || ''
              });
        });
        
        // 等待所有请求完成
        await Promise.all(promises);
        
        // 重新加载数据
        this.search();
        
        // 清空选中状态
        this.selectedRowKeys = [];
        this.selectedRows = [];
        
        // 关闭弹窗
        this.statusModalVisible = false;
        
        // 提示成功
        this.$message.success(`状态更新成功！`);
      } catch (error) {
        console.error('更新维修状态失败:', error);
        this.$message.error(`状态更新失败: ${error.message || '未知错误'}`);
      } finally {
        this.statusConfirmLoading = false;
      }
    },
    
    // 处理工卡确认
    handleWorkCardConfirm() {
      this.search();
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    
    // 处理必换件确认
    handleReplacementPartConfirm() {
      this.search();
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    
    // 处理非必换件确认
    handleNonReplacementPartConfirm() {
      this.search();
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },

    // 处理必换件单
    handleReplacementParts() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.replacementPartModalVisible = true;
    },

    // 处理非必换件申请单
    handleNonReplacementParts() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.nonReplacementPartModalVisible = true;
    },

    // 处理状态更新
    handleStatusUpdate() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      this.statusForm.repairStatus = undefined;
      this.statusForm.remark = '';
      this.statusModalVisible = true;
    },

    // 处理状态提交
    async handleStatusSubmit() {
      if (!this.statusForm.repairStatus) {
        this.$message.warning('请选择维修状态');
        return;
      }
      
      this.statusConfirmLoading = true;
      
      try {
        // 状态码映射
        const statusMap = {
          '待检查': 'WAIT_CHECK',
          '检查中': 'CHECKING',
          '维修中': 'REPAIRING',
          '等料暂停': 'WAITING_FOR_PARTS',
          '其他暂停': 'PAUSED_OTHER',
          '待装配': 'WAIT_ASSEMBLY',
          '测试中': 'TESTING',
          '完工': 'COMPLETED'
        };
        
        // 为每个选中的任务添加维修状态记录
        const promises = this.selectedRows.map(row => {
          return contractTaskApi.addRepairStatusRecord({
            taskId: row.id,
            repairStatus: statusMap[this.statusForm.repairStatus],
            description: this.statusForm.remark || ''
          });
        });
        
        // 等待所有请求完成
        await Promise.all(promises);
        
        // 重新加载数据
        this.search();
        
        // 清空选中状态
        this.selectedRowKeys = [];
        this.selectedRows = [];
        
        // 关闭弹窗
        this.statusModalVisible = false;
        
        // 提示成功
        this.$message.success(`状态更新成功！`);
      } catch (error) {
        console.error('更新维修状态失败:', error);
        this.$message.error(`状态更新失败: ${error.message || '未知错误'}`);
      } finally {
        this.statusConfirmLoading = false;
      }
    },
    
    // 处理放行文件管理
    handleApprovalFile() {
      if (this.selectedRowKeys.length !== 1) {
        this.$message.warning('请选择一个任务进行放行文件管理');
        return;
      }
      const row = this.selectedRows[0];
      this.$refs.approvalManageDialog.openDialog(row);
    },
    
    // 处理任务关闭
    handleCloseTask() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      // 检查是否有已关闭的任务
      const hasClosedTask = this.selectedRows.some(row => row.taskStatus === 'CLOSED');
      if (hasClosedTask) {
        this.$message.warning('已关闭的任务不能再次关闭');
        return;
      }
      
      // 打开确认弹窗
      this.closeTaskModalVisible = true;
    },
    
    // 确认关闭任务
    async confirmCloseTask() {
      if (this.selectedRowKeys.length === 0) {
        return;
      }
      
      this.taskStatusLoading = true;
      
      try {
        // 为每个选中的任务调用修改状态接口
        const promises = this.selectedRows.map(row => {
          return api.updateTaskStatus({
            taskId: row.id,
            taskStatus: 'CLOSED'
          });
        });
        
        // 等待所有请求完成
        await Promise.all(promises);
        
        // 关闭弹窗
        this.closeTaskModalVisible = false;
        
        // 重新加载数据
        this.search();
        
        // 清空选中状态
        this.selectedRowKeys = [];
        this.selectedRows = [];
        
        // 提示成功
        this.$message.success('任务关闭成功！');
      } catch (error) {
        console.error('关闭任务失败:', error);
        this.$message.error(`关闭任务失败: ${error.message || '未知错误'}`);
      } finally {
        this.taskStatusLoading = false;
      }
    },

    // 处理任务退修
    handleReturnedTask() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择至少一个任务');
        return;
      }
      
      // 检查是否有已关闭或已退修的任务
      const hasInvalidTask = this.selectedRows.some(row => 
        row.taskStatus === 'CLOSED' || row.taskStatus === 'RETURNED'
      );
      if (hasInvalidTask) {
        this.$message.warning('已关闭或已退修的任务不能再次退修');
        return;
      }
      
      // 清空退修原因
      this.returnTaskReason = '';
      
      // 打开确认弹窗
      this.returnTaskModalVisible = true;
    },
    
    // 确认退修任务
    async confirmReturnTask() {
      if (this.selectedRowKeys.length === 0) {
        return;
      }
      
      // 验证退修原因
      if (!this.returnTaskReason.trim()) {
        this.$message.warning('请输入退修原因');
        return;
      }
      
      this.taskStatusLoading = true;
      
      try {
        // 为每个选中的任务调用修改状态接口
        const promises = this.selectedRows.map(row => {
          return api.updateTaskStatus({
            taskId: row.id,
            taskStatus: 'RETURNED',
            reason: this.returnTaskReason.trim()
          });
        });
        
        // 等待所有请求完成
        await Promise.all(promises);
        
        // 关闭弹窗
        this.returnTaskModalVisible = false;
        
        // 重新加载数据
        this.search();
        
        // 清空选中状态
        this.selectedRowKeys = [];
        this.selectedRows = [];
        
        // 提示成功
        this.$message.success('任务退修成功！');
      } catch (error) {
        console.error('退修任务失败:', error);
        this.$message.error(`退修任务失败: ${error.message || '未知错误'}`);
      } finally {
        this.taskStatusLoading = false;
      }
    },
    
    // 查看放行文件
    viewApprovalFile(row) {
      // 实际应用中应该打开文件查看器
      this.$message.info(`查看放行文件：${row.approvalFileNumber}`);
    },
    
    // 创建操作按钮
    createActions(row) {
      const actions = [
        {
          label: '查看',
          onClick: () => {
            this.id = row.id;
            this.$nextTick(() => this.$refs.viewDialog.openDialog());
          },
        }
      ];
      
      return actions;
    },
    
    // 阻止 a-upload 默认发请求（采用手动上传）
    customUploadRequest() {
      return Promise.resolve();
    },
    
    // 组件卸载前清理状态
    beforeUnmount() {
      // 确保所有弹窗关闭
      this.materialApplyModalVisible = false;
      this.statusModalVisible = false;
      this.workCardModalVisible = false;
      this.replacementPartModalVisible = false;
      this.nonReplacementPartModalVisible = false;
      this.approvalFileModalVisible = false;
      
      // 清理选中状态
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
  },
});
</script>

<style scoped>
.date-range-container {
  display: flex;
  align-items: center;
}
.date-split {
  margin: 0 8px;
}
.contract-info {
  margin-bottom: 20px;
}
.upload-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
.attachment-list {
  max-height: 300px;
  overflow-y: auto;
}
.selected-tasks-title {
  font-weight: bold;
  margin-top: 16px;
  margin-bottom: 8px;
}
.selected-tasks-list {
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 8px;
  background-color: #fafafa;
}
.selected-task-item {
  padding: 4px 0;
  border-bottom: 1px dashed #f0f0f0;
}
.selected-task-item:last-child {
  border-bottom: none;
}
</style>