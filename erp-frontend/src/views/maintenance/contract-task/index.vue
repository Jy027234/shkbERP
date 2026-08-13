<template>
  <div>
    <div v-permission="['maintenance:contract-task']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="ContractTask"
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
          height="auto"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
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
                <j-form-item label="任务类型">
                  <a-select v-model:value="searchFormData.taskType" allow-clear>
                    <a-select-option
                      v-for="item in $enums.TASK_TYPE.values()"
                      :key="item.code"
                      :value="item.code"
                      >{{ item.desc }}</a-select-option
                    >
                  </a-select>
                </j-form-item>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              
              <!-- 技术评估按钮 -->
              <a-button 
                type="primary" 
                :icon="h(CloudUploadOutlined)" 
                @click="batchOfflineAppraisal"
                v-permission="['maintenance:contract-task']"
                :disabled="!hasSelectedRows || !canOfflineAppraisal"
              >
                技术评估
              </a-button>
              
              <!-- 派发按钮 -->
              <a-button 
                type="primary" 
                :icon="h(ThunderboltOutlined)" 
                @click="batchDispatch"
                v-permission="['maintenance:contract-task']"
                :disabled="!hasSelectedRows || !canDispatch"
              >
                派发
              </a-button>
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <available-tag :available="row.available" />
          </template>
          
          <!-- 维修类型 列自定义内容 -->
          <template #repairTypes_default="{ row }">
            <template v-if="row.repairTypes && row.repairTypes.length > 0">
              <a-tag v-for="item in row.repairTypes" :key="item.id" color="blue" style="margin-right: 4px; margin-bottom: 4px;">
                {{ item.name }} ({{ item.code }})
              </a-tag>
            </template>
            <template v-else>-</template>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <a-button type="link" @click="handleView(row)">查看</a-button>
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />

    <!-- 技术评估窗口 -->
    <offline-appraisal :id="id" ref="offlineAppraisalDialog" @confirm="search" />

    <!-- 任务派发窗口 -->
    <dispatch :id="id" ref="dispatchDialog" @confirm="search" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Detail from './detail.vue';
  import OfflineAppraisal from './offline-appraisal.vue';
  import Dispatch from './dispatch.vue';
  import {
    CheckOutlined,
    CloudUploadOutlined,
    DownOutlined,
    PlusOutlined,
    SearchOutlined,
    SettingOutlined,
    StopOutlined,
    ThunderboltOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/maintenance/contract-task/index.ts';
import * as machineTypeApi from '@/api/base-data/machine-type/index';
import * as partNumberApi from '@/api/base-data/part-number/index';
import * as repairTypeApi from '@/api/base-data/repair-type/index';
import * as customerApi from '@/api/base-data/customer';
import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    name: 'ContractTask',
    components: {
    Detail,
    DownOutlined,
    CustomerSelector,
    OfflineAppraisal,
    Dispatch,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        ThunderboltOutlined,
        SettingOutlined,
        CheckOutlined,
        StopOutlined,
        CloudUploadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        // 选中的行
        selectedRows: [],
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
        contractName: '', // 合同名称
        customerId: '', // 客户ID
        customerName: '', // 客户名称
          machineTypeId: '', // 机型ID
          partNumberCode: '', // 件号编码
          serialNumber: '', // 序号
          repairTypeIds: [], // 维修类型
          storageTimeStart: '', // 入库时间开始
          storageTimeEnd: '', // 入库时间结束
          plannedCompletionTimeStart: '', // 计划完工时间开始
          plannedCompletionTimeEnd: '', // 计划完工时间结束
          taskStatus: '', // 任务状态
          taskType: '', // 任务类型
        },
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'taskStatusName', title: '任务状态', width: 120 },
          { field: 'contractCode', title: '合同编号', width: 100 },
          { field: 'contractName', title: '合同名称', width: 120 },
          { field: 'customerName', title: '客户名称', width: 120 },
          { field: 'machineTypeName', title: '机型', width: 120 },
          { field: 'partNumberCode', title: '件号', width: 100 },
          { field: 'serialNumber', title: '序号', width: 100 },
          { field: 'repairTypes', title: '维修类型', width: 200, slots: { default: 'repairTypes_default' } },
          { field: 'dispatch', title: '派发至', width: 100 },
          { field: 'taskUserName', title: '派发人', width: 100 },
          { field: 'taskTypeName', title: '任务类型', width: 120 },
          { field: 'storageTime', title: '入库时间', width: 150, sortable: true },
          { field: 'plannedCompletionTime', title: '计划完工时间', width: 150, sortable: true },
          { field: 'createTime', title: '创建时间', width: 150, sortable: true },
          { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
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
            // 查询接口
            query: ({ page, sorts }) => {
              return api.query(this.buildQueryParams(page, sorts))
                .then(res => {
                  // 确保 datas 是数组
                  if (res && res.datas && !Array.isArray(res.datas)) {
                    res.datas = [];
                  }
                  return res;
                });
            },
          },
        },
      };
    },
    created() {
      // 在methods对象中定义了这些方法，所以这里可以直接调用
      // 加载机型列表
      this.loadMachineTypeList();
      // 加载维修类型列表
      this.loadRepairTypeList();
    },
    computed: {
      // 是否有选中的行（基于响应式 selectedRows，保证按钮实时更新）
      hasSelectedRows() {
        return this.selectedRows && this.selectedRows.length > 0;
      },
      // 是否可以进行技术评估操作
      canOfflineAppraisal() {
        // 待技术评估状态才能评估；若未选择行则返回 false
        if (!this.hasSelectedRows) return false;
        return this.selectedRows.every(row => this.isWaitEvaluationRow(row));
      },
      // 是否可以进行派发操作
      canDispatch() {
        // 待派发状态才能派发；若未选择行则返回 false
        if (!this.hasSelectedRows) return false;
        return this.selectedRows.every(row => this.isWaitDispatchRow(row));
      },
    },
    
    methods: {
      // 判断是否为待评估（兼容多种数据形态）
      isWaitEvaluationRow(row) {
        const enumCode = this.$enums?.TASK_STATUS?.WAIT_EVALUATION?.code;
        const enumDesc = this.$enums?.TASK_STATUS?.WAIT_EVALUATION?.desc;
        const code = row?.taskStatus ?? row?.taskStatusCode;
        const name = row?.taskStatusName ?? row?.taskStatusDesc;
        return (
          code === enumCode ||
          code === 'WAIT_EVALUATION' ||
          name === enumDesc ||
          (typeof name === 'string' && name.includes('待技术评估'))
        );
      },
      // 判断是否为待派发（兼容多种数据形态）
      isWaitDispatchRow(row) {
        const enumCode = this.$enums?.TASK_STATUS?.WAIT_DISPATCH?.code;
        const enumDesc = this.$enums?.TASK_STATUS?.WAIT_DISPATCH?.desc;
        const code = row?.taskStatus ?? row?.taskStatusCode;
        const name = row?.taskStatusName ?? row?.taskStatusDesc;
        return (
          code === enumCode ||
          code === 'WAIT_DISPATCH' ||
          name === enumDesc ||
          (typeof name === 'string' && name.includes('待派发'))
        );
      },
      // 列表发生查询时的事件
      search() {
        // 清空勾选并刷新列表
        this.selectedRows = [];
        if (this.$refs.grid && this.$refs.grid.clearCheckboxRow) {
          this.$refs.grid.clearCheckboxRow();
        }
        this.$refs.grid.commitProxy('reload');
      },
      // 过滤机型选项
      filterMachineTypeOption(input, option) {
        if (!input) return true;
        const item = this.machineTypeList.find(item => item.id === option.value);
        if (item) {
          const searchText = `${item.name} ${item.code}`.toLowerCase();
          return searchText.indexOf(input.toLowerCase()) >= 0;
        }
        return false;
      },
      // 处理客户选择变更
      handleCustomerChange(value) {
        if (value) {
          customerApi.get(value).then(res => {
            if (res) {
              this.searchFormData.customerName = res.name;
            }
          });
        } else {
          this.searchFormData.customerName = '';
        }
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
        // 切换机型时不再联动件号下拉，仅可选地清空文本输入
        // this.searchFormData.partNumberCode = '';
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
      // 处理表格行勾选变更
      handleCheckboxChange({ checked, row }) {
        if (checked) {
          this.selectedRows.push(row);
        } else {
          const index = this.selectedRows.findIndex(item => item.id === row.id);
          if (index !== -1) {
            this.selectedRows.splice(index, 1);
          }
        }
      },
      // 处理表格全选/取消全选
      handleCheckboxAll({ checked, data }) {
        if (checked) {
          this.selectedRows = [...data];
        } else {
          this.selectedRows = [];
        }
      },
      // 批量技术评估
      batchOfflineAppraisal() {
        if (this.selectedRows.length === 0) {
          this.$message.warning('请选择需要技术评估的任务');
          return;
        }
        
        if (this.selectedRows.length === 1) {
          // 单个任务处理
          this.id = this.selectedRows[0].id;
          this.$nextTick(() => this.$refs.offlineAppraisalDialog.openDialog());
        } else {
          // 多个任务处理 - 可以根据需求实现批量处理逻辑
          this.$message.warning('暂不支持批量技术评估，请选择单个任务进行操作');
        }
      },
      // 批量派发
      batchDispatch() {
        if (this.selectedRows.length === 0) {
          this.$message.warning('请选择需要派发的任务');
          return;
        }
        
        if (this.selectedRows.length === 1) {
          // 单个任务处理
          this.id = this.selectedRows[0].id;
          this.$nextTick(() => this.$refs.dispatchDialog.openDialog());
        } else {
          // 多个任务处理 - 可以根据需求实现批量处理逻辑
          this.$message.warning('暂不支持批量派发，请选择单个任务进行操作');
        }
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        return {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
      },
      // 构建查询条件
      buildSearchFormData() {
        return {
          contractCode: this.searchFormData.code, // 合同编号
          contractName: this.searchFormData.contractName, // 合同名称
          customerId: this.searchFormData.customerId, // 客户ID
          customerName: this.searchFormData.customerName, // 客户名称
          machineTypeId: this.searchFormData.machineTypeId, // 机型ID
          partNumberCode: this.searchFormData.partNumberCode, // 件号编码
          serialNumber: this.searchFormData.serialNumber, // 序号
          repairTypeIds: this.searchFormData.repairTypeIds, // 维修类型
          storageTimeStart: this.searchFormData.storageTimeStart, // 入库时间开始
          storageTimeEnd: this.searchFormData.storageTimeEnd, // 入库时间结束
          plannedCompletionTimeStart: this.searchFormData.plannedCompletionTimeStart, // 计划完工时间开始
          plannedCompletionTimeEnd: this.searchFormData.plannedCompletionTimeEnd, // 计划完工时间结束
          taskStatus: this.searchFormData.taskStatus, // 任务状态
          taskType: this.searchFormData.taskType, // 任务类型
        };
      },
      // 处理查看按钮点击
      handleView(row) {
        this.id = row.id;
        this.$nextTick(() => this.$refs.viewDialog.openDialog());
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
</style>
