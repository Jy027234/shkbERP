<template>
  <div>
    <div v-permission="['maintenance:aviation']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="MaintenanceAviation"
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
          :pager-config="{}"
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
                  <a-input v-model:value="searchFormData.code" allow-clear />
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
                  <a-select 
                    v-model:value="searchFormData.partNumberId" 
                    allow-clear
                    placeholder="请选择件号"
                    show-search
                    :filter-option="filterPartNumberOption"
                    :disabled="!searchFormData.machineTypeId"
                    :loading="partNumberLoading"
                  >
                    <a-select-option
                      v-for="item in partNumberList"
                      :key="item.id"
                      :value="item.id"
                      >{{ item.code }}</a-select-option
                    >
                  </a-select>
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
                <j-form-item label="维修类型">
                  <a-select 
                    v-model:value="searchFormData.repairTypeIds" 
                    mode="multiple"
                    allow-clear
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
            <a-button v-if="row.approvalFile === '已上传'" type="link" @click="viewApprovalFile(row)">
              <file-outlined /> 查看
            </a-button>
            <span v-else>未上传</span>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />
    
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
    
    <!-- 必换件单窗口 -->
    <replacement-part-management
      v-model:visible="replacementPartModalVisible"
      :tasks="selectedRows"
      @confirm="handleReplacementPartConfirm"
    />
    
    <!-- 非必换件申请单窗口 -->
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
          合同编号：{{ row.contractCode }} 机型：{{ row.machineTypeName }} 件号：{{ row.partNumberName }}
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
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import { createMaterialApply } from '@/api/maintenance/contract-task';
  import Detail from './detail.vue';
  import StatusManagement from '../components/StatusManagement.vue';
  import WorkCardManagement from '../components/WorkCardManagement.vue';
  import ReplacementPartManagement from '../components/ReplacementPartManagement.vue';
  import NonReplacementPartManagement from '../components/NonReplacementPartManagement.vue';
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
  import * as api from '@/api/maintenance/aviation';
  import * as machineTypeApi from '@/api/base-data/machine-type';
  import * as partNumberApi from '@/api/base-data/part-number';
  import * as repairTypeApi from '@/api/base-data/repair-type';
  import * as customerApi from '@/api/base-data/customer';
  import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    name: 'MaintenanceAviation',
    components: {
      Detail,
      StatusManagement,
      WorkCardManagement,
      ReplacementPartManagement,
      NonReplacementPartManagement,
      CustomerSelector,
    },
    setup() {
      return {
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
        CloseCircleOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 是否使用模拟数据
        useMockData: false,
        // 当前行数据
        id: '',
        // 机型列表
        machineTypeList: [],
        machineTypeLoading: false,
        // 件号列表
        partNumberList: [],
        partNumberLoading: false,
        // 维修类型列表
        repairTypeList: [],
        repairTypeLoading: false,
        // 查询列表的查询条件
        searchFormData: {
          code: '', // 合同编号
          customerId: '', // 客户ID
          customerName: '', // 客户名称
          machineTypeId: '', // 机型ID
          partNumberId: '', // 件号ID
          repairTypeIds: [], // 维修类型
          storageTimeStart: '', // 入库时间开始
          storageTimeEnd: '', // 入库时间结束
          plannedCompletionTimeStart: '', // 计划完工时间开始
          plannedCompletionTimeEnd: '', // 计划完工时间结束
          taskStatus: '', // 任务状态
          approvalFileNumber: '', // 放行文件编号
        },
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 选中行的keys
        selectedRowKeys: [],
        // 选中行的数据
        selectedRows: [],
        // 状态管理弹窗相关
        statusModalVisible: false,
        statusConfirmLoading: false,
        // 工卡管理弹窗相关
        workCardModalVisible: false,
        // 必换件单弹窗相关
        replacementPartModalVisible: false,
        // 非必换件申请单弹窗相关
        nonReplacementPartModalVisible: false,
        // 领料申请相关
        materialApplyModalVisible: false,
        materialApplyForm: {
          remark: ''
        },
        materialApplyLoading: false,
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'repairStatusLabel', title: '维修状态', width: 100 },
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
          { field: 'approvalFile', title: '放行文件', width: 120, slots: { default: 'approvalFile_default' } },
          { field: 'approvalFileNumber', title: '放行文件编号', width: 180 },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '创建人', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170, sortable: true },
          { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
        ],
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
            repairStatus: '检查中',
            description: '客户要求加急',
            createBy: '李四',
            createTime: '2025-05-05 13:00:00'
          },
          {
            id: '3',
            contractCode: 'HT-2025-003',
            customerName: '东方航空',
            machineTypeName: 'B787',
            machineTypeCode: 'B787',
            partNumberName: 'AV-003',
            partNumberCode: 'AV-003',
            serialNumber: 'SN20250003',
            repairTypesLabel: '小修,例检',
            otherRepairRequirements: '软件升级',
            storageTime: '2025-05-10 09:15:00',
            plannedCompletionTime: '2025-05-25 18:00:00',
            workCardNumberList: 'WC-005',
            otherWorkCardNumber: '',
            otherReplacementPartNumber: 'HT-2025-003-FBH',
            replacementPartNumber: 'HT-2025-003-BHJ',
            approvalFile: '未上传',
            approvalFileNumber: '',
            available: 'COMPLETED',
            repairStatusLabel: '完工',
            description: '常规维护',
            createBy: '王五',
            createTime: '2025-05-10 08:30:00'
          },
          {
            id: '4',
            contractCode: 'HT-2025-004',
            customerName: '海南航空',
            machineTypeName: 'A350',
            machineTypeCode: 'A350',
            partNumberName: 'APU-004',
            partNumberCode: 'APU-004',
            serialNumber: 'SN20250004',
            repairType: '大修',
            otherRepairRequirements: '全面检修',
            storageTime: '2025-05-15 11:20:00',
            plannedCompletionTime: '2025-07-15 18:00:00',
            workCardNumberList: 'WC-006, WC-007, WC-008',
            otherWorkCardNumber: 'WC-EXT-003',
            otherReplacementPartNumber: 'HT-2025-004-FBH',
            replacementPartNumber: 'HT-2025-004-BHJ',
            approvalFile: '已上传',
            approvalFileNumber: 'AP-2025-003',
            available: 'WAIT_DISPATCH',
            repairStatus: '等料暂停',
            description: '延长保修期',
            createBy: '赵六',
            createTime: '2025-05-15 10:00:00'
          },
          {
            id: '5',
            contractCode: 'HT-2025-005',
            customerName: '四川航空',
            machineTypeName: 'CRJ900',
            partNumberName: 'HYD-005',
            serialNumber: 'SN20250005',
            repairTypesLabel: '中修,特检',
            otherRepairRequirements: '压力测试',
            storageTime: '2025-05-20 16:45:00',
            plannedCompletionTime: '2025-06-20 18:00:00',
            workCardNumberList: 'WC-009',
            otherWorkCardNumber: '',
            otherReplacementPartNumber: 'HT-2025-005-FBH',
            replacementPartNumber: 'HT-2025-005-BHJ',
            approvalFile: '已上传',
            approvalFileNumber: 'AP-2025-004',
            available: 'EXECUTING',
            repairStatusLabel: '维修中',
            description: '按标准流程执行',
            createBy: '钱七',
            createTime: '2025-05-20 15:30:00'
          }
        ],
        // 请求接口配置
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
                if (this.searchFormData.code) {
                  filteredData = filteredData.filter(item => 
                    item.code && item.code.toLowerCase().includes(this.searchFormData.code.toLowerCase())
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
                    return type ? type.name : '';
                  }).filter(name => name);
                  
                  return selectedRepairTypes.some(typeName => 
                    item.repairType.includes(typeName)
                  );
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
                // 添加任务类型参数 - 民航维修任务
                params.taskType = 'AVIATION';
                // 调用合同任务列表接口
                return api.query(params);
              }
            },
          },
        },
      };
    },
    created() {
      // 加载机型列表
      this.loadMachineTypeList();
      // 加载维修类型列表
      this.loadRepairTypeList();
    },
    beforeUnmount() {
      // 清理组件状态
      this.selectedRows = [];
      this.selectedRowKeys = [];
      this.loading = false;
      this.statusModalVisible = false;
      this.workCardModalVisible = false;
      this.replacementPartModalVisible = false;
      this.nonReplacementPartModalVisible = false;
      this.materialApplyModalVisible = false;
    },
    methods: {
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        return {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
      },
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        // 构建查询参数，确保字段名称与后端 API 期望的参数名称匹配
        return {
          code: this.searchFormData.code, // 合同编号
          customerId: this.searchFormData.customerId, // 客户ID
          customerName: this.searchFormData.customerName, // 客户名称
          machineTypeId: this.searchFormData.machineTypeId, // 机型ID
          partNumberId: this.searchFormData.partNumberId, // 件号ID
          repairTypeIds: this.searchFormData.repairTypeIds, // 维修类型
          storageTimeStart: this.searchFormData.storageTimeStart, // 入库时间开始
          storageTimeEnd: this.searchFormData.storageTimeEnd, // 入库时间结束
          plannedCompletionTimeStart: this.searchFormData.plannedCompletionTimeStart, // 计划完工时间开始
          plannedCompletionTimeEnd: this.searchFormData.plannedCompletionTimeEnd, // 计划完工时间结束
          taskStatus: this.searchFormData.taskStatus, // 任务状态
          approvalFileNumber: this.searchFormData.approvalFileNumber, // 放行文件编号
        };
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
        this.searchFormData.partNumberId = '';
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
      
      // 处理客户选择变更
      handleCustomerChange(value) {
        if (value) {
          // 获取客户详情
          customerApi.get(value).then(res => {
            if (res) {
              this.searchFormData.customerName = res.name;
            }
          });
        } else {
          this.searchFormData.customerName = '';
        }
      },
      // 处理复选框变化
      handleCheckboxChange({ checked, row, $event }) {
        if (checked) {
          // 将选中的行添加到选中数组
          this.selectedRowKeys.push(row.id);
          this.selectedRows.push(row);
        } else {
          // 从选中数组中移除取消选中的行
          const index = this.selectedRowKeys.indexOf(row.id);
          if (index > -1) {
            this.selectedRowKeys.splice(index, 1);
            this.selectedRows.splice(index, 1);
          }
        }
      },
      
      // 处理全选/取消全选
      handleCheckboxAll({ checked, rows, $event }) {
        if (checked) {
          // 全选
          this.selectedRowKeys = rows.map(row => row.id);
          this.selectedRows = [...rows];
        } else {
          // 取消全选
          this.selectedRowKeys = [];
          this.selectedRows = [];
        }
      },
      
      // 处理查看选中的任务
      handleViewSelected() {
        if (this.selectedRowKeys.length === 1) {
          // 如果只选中了一个，直接查看详情
          this.id = this.selectedRowKeys[0];
          this.$nextTick(() => this.$refs.viewDialog.openDialog());
        } else {
          this.$message.warning('请选择一个任务进行查看');
        }
      },
      
      // 处理非必换件申请单
      handleNonReplacementParts() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        // 打开非必换件申请单弹窗
        this.nonReplacementPartModalVisible = true;
      },
      
      // 处理非必换件申请单确认
      handleNonReplacementPartConfirm(nonReplacementParts) {
        // 处理非必换件申请单结果
        console.log('非必换件申请单结果', nonReplacementParts);
        this.$message.success('非必换件申请单保存成功');
        this.search();
      },
      
      // 处理必换件单
      handleReplacementParts() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        // 打开必换件单弹窗
        this.replacementPartModalVisible = true;
      },
      
      // 处理必换件单确认
      handleReplacementPartConfirm(replacementParts) {
        // 处理必换件单结果
        console.log('必换件单结果', replacementParts);
        this.search();
      },
      
      // 处理工卡管理
      handleWorkCard() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        // 打开工卡管理弹窗
        this.workCardModalVisible = true;
      },
      
      // 处理工卡管理确认
      handleWorkCardConfirm(workCards) {
        // 处理工卡管理结果
        console.log('工卡管理结果', workCards);
        this.$message.success('工卡管理操作成功');
        this.search();
      },
      
      // 处理领料申请
      handleMaterialRequest() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        // 显示领料申请弹窗
        this.materialApplyModalVisible = true;
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
            return createMaterialApply({
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
      
      // 处理状态管理
      handleStatusManagement() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        // 打开状态管理弹窗
        this.statusModalVisible = true;
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
            '测试中': 'TESTING',
            '完工': 'COMPLETED'
          };
          
          // 为每个选中的任务添加维修状态记录
          const promises = this.selectedRows.map(row => {
            return contractTaskApi.addRepairStatusRecord({
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
      
      // 处理放行文件管理
      handleApprovalFile() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        const codes = this.selectedRows.map(row => row.code).join(', ');
        this.$message.info(`打开放行文件管理：${codes}`);
      },
      
      // 处理任务关闭
      handleCloseTask() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        const codes = this.selectedRows.map(row => row.code).join(', ');
        this.$message.info(`关闭任务：${codes}`);
      },

      // 处理任务退修
      handleReturnedTask() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个任务');
          return;
        }
        
        const codes = this.selectedRows.map(row => row.code).join(', ');
        this.$message.info(`退修任务：${codes}`);
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
      
      // 组件卸载前清理状态
      beforeUnmount() {
        // 确保所有弹窗关闭
        this.materialApplyModalVisible = false;
        this.statusModalVisible = false;
        this.workCardModalVisible = false;
        this.replacementPartModalVisible = false;
        this.nonReplacementPartModalVisible = false;
        
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
