<template>
  <div v-permission="[`contract:${contractType}`]">
    <div>
      <page-wrapper content-full-height fixed-height>
        <vxe-grid
          id="ContractList"
          ref="grid"
          v-loading="loading"
          show-overflow
          highlight-hover-row
          keep-source
          row-id="id"
          :proxy-config="proxyConfig"
          :columns="tableColumn"
          :toolbar-config="toolbarConfig"
          :custom-config="{}"
          :pager-config="{}"
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
                <j-form-item label="合同名称">
                  <a-input v-model:value="searchFormData.name" allow-clear />
                </j-form-item>
                <j-form-item label="客户">
                  <customer-selector v-model:value="searchFormData.customerId" @update:value="handleCustomerChange" />
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
                  <a-input v-model:value="searchFormData.partNumberCode" allow-clear placeholder="请输入件号" />
                </j-form-item>
                <j-form-item label="产品序号">
                  <a-input v-model:value="searchFormData.serialNumber" allow-clear />
                </j-form-item>
                <j-form-item label="发货时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.deliveryTimeStart"
                      value-format="YYYY-MM-DD"
                      placeholder="开始日期"
                    />
                    <span class="date-split">-</span>
                    <a-date-picker
                      v-model:value="searchFormData.deliveryTimeEnd"
                      value-format="YYYY-MM-DD"
                      placeholder="结束日期"
                    />
                  </div>
                </j-form-item>
                <j-form-item label="入库时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.storageTimeStart"
                      value-format="YYYY-MM-DD"
                      placeholder="开始日期"
                    />
                    <span class="date-split">-</span>
                    <a-date-picker
                      v-model:value="searchFormData.storageTimeEnd"
                      value-format="YYYY-MM-DD"
                      placeholder="结束日期"
                    />
                  </div>
                </j-form-item>
                <j-form-item label="维修类型">
                  <a-select 
                    v-model:value="searchFormData.repairTypeIds" 
                    mode="multiple"
                    allow-clear
                    placeholder="请选择维修类型"
                    show-search
                    option-filter-prop="children"
                    :filter-option="filterOption"
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
                <j-form-item label="合同状态">
                  <a-select v-model:value="searchFormData.available" allow-clear>
                    <a-select-option :value="true">启用</a-select-option>
                    <a-select-option :value="false">停用</a-select-option>
                  </a-select>
                </j-form-item>
                <j-form-item label="合同进度">
                  <a-select v-model:value="searchFormData.contractStatus" allow-clear>
                    <a-select-option
                      v-for="item in $enums.CONTRACT_STATUS.values()"
                      :key="item.code"
                      :value="item.code"
                      >{{ item.desc }}</a-select-option
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
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button
                :icon="h(ReloadOutlined)"
                @click="resetSearchForm"
                >重置</a-button
              >
              <a-button
                v-permission="[`contract:${contractType}`]"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="openAdd"
                >新增</a-button
              >
              <a-button
                v-permission="[`contract:${contractType}`]"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
              >
                导出
              </a-button>
              <a-divider type="vertical" />
              
              <!-- 批量操作按钮组 -->
              <a-button 
                v-permission="[`contract:${contractType}`]"
                :disabled="!selectedRowKeys.length || creatingTask" 
                :loading="creatingTask"
                :icon="h(FormOutlined)" 
                @click="handleCreateTask"
              >
                生成合同任务
              </a-button>
              <a-button
                :disabled="!selectedRowKeys.length" 
                v-permission="[`contract:${contractType}`]"
                :icon="h(PaperClipOutlined)" 
                @click="handleAttachmentManage"
              >
                合同附件管理
              </a-button>

              <a-button 
                :disabled="!selectedRowKeys.length" 
                v-permission="[`contract:${contractType}`]"
                :icon="h(CloseCircleOutlined)" 
                @click="handleCloseContract"
              >
                关闭合同
              </a-button>
            </a-space>
          </template>
          <!-- 任务状态 列自定义内容 -->
          <template #taskStatus_default="{ row }">
            <span>
              {{ ($enums && $enums.TASK_STATUS && $enums.TASK_STATUS.get(row.taskStatus)) ? $enums.TASK_STATUS.get(row.taskStatus).desc : (row.taskStatus || '-') }}
            </span>
          </template>
          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <a-tag :color="row.available ? 'green' : 'red'">{{
              row.available ? '启用' : '停用'
            }}</a-tag>
          </template>
          <template #repairTypes_default="{ row }">
            <template v-if="row.repairTypes && row.repairTypes.length > 0">
              <a-tag v-for="item in row.repairTypes" :key="item.id" color="blue" style="margin-right: 4px; margin-bottom: 4px;">
                {{ item.name }}
              </a-tag>
            </template>
            <template v-else>-</template>
          </template>
          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <a-space>
              <a-button type="link" size="small" @click="handleView(row)">
                查看
              </a-button>
              <a-button 
                v-permission="[`contract:${contractType}`]"
                type="link" 
                size="small" 
                @click="handleModify(row)"
              >
                修改
              </a-button>
            </a-space>
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add :contract-type="contractType" ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" :contract-type="contractType" ref="updateDialog" @confirm="search" />

    <!-- 查看窗口 -->
    <detail :id="id" :contract-type="contractType" ref="viewDialog" />
    
    <!-- 附件管理窗口 -->
    <attachment-manage ref="attachmentManageDialog" />
    
    <!-- 关闭合同对话框 -->
    <a-modal
      v-model:open="closeContractModalVisible"
      title="关闭合同"
      @ok="confirmCloseContract"
      :confirmLoading="closeContractLoading"
    >
      <div v-if="selectedRows.length">
        <div class="contract-info-list">
          <div class="contract-info-item" v-for="(row, index) in selectedRows" :key="row.id">
            <div class="contract-info-title">合同 {{ index + 1 }}:</div>
            <div class="contract-info-content">
              <div><b>合同编号:</b> {{ row.code }}</div>
              <div><b>客户名称:</b> {{ row.customerName }}</div>
              <div><b>机型:</b> {{ row.machineTypeName }}</div>
              <div><b>件号:</b> {{ row.partNumberCode }}</div>
              <div><b>当前状态:</b> {{ row.contractStatusName }}</div>
            </div>
          </div>
        </div>
        <a-form layout="vertical" class="close-contract-form">
          <a-form-item label="备注信息">
            <a-textarea 
              v-model:value="closeContractRemark" 
              placeholder="请输入关闭合同的备注信息（选填）" 
              :rows="4" 
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent } from 'vue';
  import { h } from 'vue';
  import { useMessage } from '@/hooks/web/useMessage';
  import { usePermission } from '@/hooks/web/usePermission';
  import Add from '../components/ContractAdd.vue';
  import Modify from '../components/ContractModify.vue';
  import Detail from '../components/ContractDetail.vue';
  import AttachmentManage from '../components/ContractAttachmentManage.vue';
  import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, EyeOutlined, DownOutlined, FormOutlined, CloseCircleOutlined, PaperClipOutlined, DownloadOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/contract';
import { CONTRACT_STATUS } from '@/enums/biz/contractStatus';
  import * as machineTypeApi from '@/api/base-data/machine-type/index';
  import * as repairTypeApi from '@/api/base-data/repair-type/index';
  import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    name: 'ContractList',
    // 使用组件
    components: {
      Add,
      Modify,
      Detail,
      AttachmentManage,
      CustomerSelector,
      PlusOutlined,
      SearchOutlined,
      ReloadOutlined,
      EditOutlined,
      EyeOutlined,
      DownOutlined,
      PaperClipOutlined,
      CloseCircleOutlined,
    },
    props: {
      // 合同类型：aviation-航空维修合同，factory-l-工厂维修合同(L)，factory-wb-工厂维修合同(WB)
      contractType: {
        type: String,
        required: true,
        validator: (value) => ['aviation', 'factory-l', 'factory-wb'].includes(value),
      },
    },
    setup() {
      const { hasPermission } = usePermission();
      const { createMessage, createConfirm } = useMessage();
      return {
        h,
        PlusOutlined,
        SearchOutlined,
        ReloadOutlined,
        EditOutlined,
        EyeOutlined,
        DownOutlined,
      FormOutlined,
      CloseCircleOutlined,
      DownloadOutlined,
        hasPermission,
        createMessage,
        createConfirm
      };
    },
    data() {
      return {
        // 选中的行ID列表
        selectedRowKeys: [],
        // 选中的行数据列表
        selectedRows: [],
        // 是否显示加载框
        loading: false,
        // 创建合同任务中，防重复触发
        creatingTask: false,
        // 当前行ID
        id: '',
        // 查询条件
        searchFormData: {
          code: '', // 合同编号
          name: '', // 合同名称
          customerId: '', // 客户ID
          machineTypeId: '', // 机型ID
          partNumberCode: '', // 件号（文本）
          contractType: 0, // 合同类型 - 民航维修
          serialNumber: '', // 序号
          repairTypeIds: [], // 维修类型ID列表
          storageTimeStart: '', // 入库时间开始
          storageTimeEnd: '', // 入库时间结束
          deliveryTimeStart: '', // 发货时间开始
          deliveryTimeEnd: '', // 发货时间结束
          available: this.$enums.AVAILABLE.ENABLE.code, // 合同状态
          contractStatus: '', // 合同进度
          taskStatus: '', // 任务状态
        },
        // 机型列表
        machineTypeList: [],

        // 维修类型列表
        repairTypeList: [],
        // 机型选择器加载状态
        machineTypeLoading: false,
        
        // 维修类型选择器加载状态
        repairTypeLoading: false,
        // 关闭合同对话框可见性
        closeContractModalVisible: false,
        // 关闭合同加载状态
        closeContractLoading: false,
        // 关闭合同备注
        closeContractRemark: '',
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons'
          }
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 40 },
          { type: 'seq', width: 50 },
          { field: 'contractStatusName',
            title: '合同进度',
            width: 120,
          },
          { field: 'repairStatusName', title: '维修状态', width: 120 },
          { field: 'code', title: '合同编号', width: 100 },
          { field: 'name', title: '合同名称', width: 100 },
          { field: 'customerName', title: '客户名称', width: 150 },
          { field: 'machineTypeName', title: '机型', width: 150 },
          { field: 'partNumberCode', title: '件号', width: 120 },
          { field: 'serialNumber', title: '产品序号', width: 180 },
          { field: 'repairTypes', title: '维修类型', width: 200, slots: { default: 'repairTypes_default' } },
          { field: 'otherRepairRequirements', title: '其他维修需求', width: 180 },
          { field: 'contractTime', title: '合同时间', width: 180, sortable: true },
          { field: 'storageTime', title: '入库时间', width: 180, sortable: true },
          { field: 'plannedCompletionTime', title: '计划完工时间', width: 180, sortable: true },
          { field: 'actualCompletionTime', title: '实际完工时间', width: 180, sortable: true },
          { field: 'deliveryTime', title: '发货时间', width: 180, sortable: true },
          { field: 'contractPrice', title: '合同报价', width: 180 },
          { field: 'replacementPartPrice', title: '更换件价格', width: 180 },
          {
            field: 'available',
            title: '状态',
            width: 100,
            slots: { default: 'available_default' },
          },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '创建人', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170, sortable: true },
          { title: '操作', width: 100, fixed: 'right', slots: { default: 'action_default' } },
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
      // 设置合同类型
      this.searchFormData.contractType = this.getContractTypeCode();
      // 加载机型列表
      this.loadMachineTypeList();
      // 加载维修类型列表
      this.loadRepairTypeList();
    },
    methods: {
      // 获取合同类型代码
      getContractTypeCode() {
        switch (this.contractType) {
          case 'aviation':
            return 1; // 民航维修
          case 'factory-wb':
            return 2; // 工厂维修(WB)
          case 'factory-l':
            return 3; // 工厂维修(L)
          default:
            return 1;
        }
      },
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
        // 输出维修类型参数进行调试
        console.log('原始 repairTypeIds参数:', this.searchFormData.repairTypeIds);
        
        // 将维修类型ID数组转换为逗号分隔的字符串
        let repairTypeIdsStr = '';
        if (Array.isArray(this.searchFormData.repairTypeIds) && this.searchFormData.repairTypeIds.length > 0) {
          repairTypeIdsStr = this.searchFormData.repairTypeIds.join(',');
        }
        console.log('转换后 repairTypeIds参数:', repairTypeIdsStr);
        
        const params = {
          code: this.searchFormData.code,
          name: this.searchFormData.name,
          customerId: this.searchFormData.customerId,
          machineTypeId: this.searchFormData.machineTypeId,
          partNumberCode: this.searchFormData.partNumberCode,
          serialNumber: this.searchFormData.serialNumber,
          // 使用逗号分隔的字符串
          repairTypeIds: repairTypeIdsStr,
          storageTimeStart: this.searchFormData.storageTimeStart,
          storageTimeEnd: this.searchFormData.storageTimeEnd,
          deliveryTimeStart: this.searchFormData.deliveryTimeStart,
          deliveryTimeEnd: this.searchFormData.deliveryTimeEnd,
          available: this.searchFormData.available,
          contractStatus: this.searchFormData.contractStatus,
          taskStatus: this.searchFormData.taskStatus,
          contractType: this.getContractTypeCode(),
        };
        return params;
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
          this.selectedRowKeys = rows.map((row) => row.id);
          this.selectedRows = [...rows];
        } else {
          // 取消全选
          this.selectedRowKeys = [];
          this.selectedRows = [];
        }
      },
      // 查看合同详情
      handleView(row) {
        this.id = row.id;
        this.$nextTick(() => this.$refs.viewDialog.openDialog());
      },
      // 修改合同
      handleModify(row) {
        this.id = row.id;
        this.$nextTick(() => this.$refs.updateDialog.openDialog());
      },
      // 生成合同任务
      handleCreateTask() {
        if (this.creatingTask) return;
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择至少一个合同');
          return;
        }
        // 检查所有选中的合同状态是否为待生成合同任务
        const invalidContracts = this.selectedRows.filter((row) => row.contractStatus !== 0);
        if (invalidContracts.length > 0) {
          this.$message.warning('只有待生成合同任务状态的合同才能生成合同任务！');
          return;
        }
        const contractCodes = this.selectedRows.map((row) => row.code).join(', ');
        this.createConfirm({
          title: '提示',
          content: `确认要为选中的合同(${contractCodes})生成合同任务吗？`,
          okText: '确认',
          cancelText: '取消',
          onOk: async () => {
            // 显示加载状态
            this.loading = true;
            this.creatingTask = true;
            try {
              // 为每个选中的合同生成任务
              const promises = this.selectedRows.map((row) => {
                return api.createContractTask({ contractId: row.id });
              });
              await Promise.all(promises);
              this.$message.success('合同任务生成成功！');
              // 刷新列表
              this.search();
              // 清空选中状态
              this.selectedRowKeys = [];
              this.selectedRows = [];
            } catch (err) {
              this.$message.error(`生成合同任务失败：${err.message || '未知错误'}`);
            } finally {
              this.loading = false;
              this.creatingTask = false;
            }
          },
        });
      },
      // 关闭合同 - 打开对话框
      handleCloseContract() {
        if (this.selectedRowKeys.length === 0) {
          this.createMessage.warning('请选择要关闭的合同');
          return;
        }
        
        // 重置备注
        this.closeContractRemark = '';
        // 显示关闭合同对话框
        this.closeContractModalVisible = true;
      },

      // 确认关闭合同
      async confirmCloseContract() {
        if (this.selectedRowKeys.length === 0) {
          this.createMessage.warning('请选择要关闭的合同');
          return;
        }
        
        this.closeContractLoading = true;
        try {
          // 获取CONTRACT_CLOSE状态的code
          const contractCloseStatus = CONTRACT_STATUS.get('CONTRACT_CLOSE').code.toString();
          
          // 为每个选中的合同更新状态
          const promises = this.selectedRows.map(row => {
            return api.updateContractStatus({
              contractId: row.id,
              contractStatus: contractCloseStatus,
              remark: this.closeContractRemark || undefined
            });
          });
          
          await Promise.all(promises);
          
          // 关闭对话框
          this.closeContractModalVisible = false;
          // 显示成功消息
          this.createMessage.success(`成功关闭 ${this.selectedRows.length} 个合同`);
          // 刷新列表
          this.search();
          // 清空选中状态
          this.selectedRowKeys = [];
          this.selectedRows = [];
        } catch (error) {
          this.createMessage.error(`关闭合同失败: ${error.message || '未知错误'}`);
        } finally {
          this.closeContractLoading = false;
        }
      },
      
      /**
       * 处理附件管理
       */
      handleAttachmentManage() {
        if (this.selectedRowKeys.length !== 1) {
          this.createMessage.warning('请选择一个合同进行附件管理');
          return;
        }
        
        // 打开附件管理对话框
        this.$refs.attachmentManageDialog.openDialog(this.selectedRowKeys[0]);
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
        // 切换机型时，仅清空件号文本
        this.searchFormData.partNumberCode = '';
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
      // 过滤选项
      filterOption(input, option) {
        return (option?.children ?? '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
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
        
        // 如果没有原始数据，则使用选项的显示文本
        const label = option.label || '';
        const value = option.value ? String(option.value) : '';
        const children = option.children ? String(option.children) : '';
        
        return label.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
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
      
      // 打开新增对话框
      openAdd() {
        this.$refs.addDialog.openDialog();
      },
      
      // 重置搜索表单
      resetSearchForm() {
        this.searchFormData = {
          code: '',
          name: '',
          customerId: '',
          machineTypeId: '',
          partNumberCode: '',
          serialNumber: '',
          deliveryTimeStart: '',
          deliveryTimeEnd: '',
          storageTimeStart: '',
          storageTimeEnd: '',
          repairTypeIds: [],
          contractStatus: '',
          contractType: this.getContractTypeCode()
        };
        this.search();
      },

      // 导出合同
      async handleExport() {
        try {
          this.loading = true;
          const params = this.buildSearchFormData();

          // 如果有勾选的合同，则优先导出勾选的合同
          if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
            // 后端使用ids参数按ID列表导出
            params.ids = this.selectedRowKeys.join(',');
          }
          const res = await api.exportContract(params);
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = '合同信息.xls';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        } catch (error) {
          this.createMessage.error(`导出合同失败: ${error.message || '未知错误'}`);
        } finally {
          this.loading = false;
        }
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

/* 合同关闭对话框样式 */
.contract-info-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.contract-info-item {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fafafa;
}

.contract-info-item:last-child {
  margin-bottom: 0;
}

.contract-info-title {
  font-weight: bold;
  margin-bottom: 8px;
  color: #1890ff;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 4px;
}

.contract-info-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
}

.close-contract-form {
  margin-top: 16px;
}
</style>
