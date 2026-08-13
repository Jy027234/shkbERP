<template>
  <div>
    <div v-permission="['work-card']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="WorkCard"
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
                <j-form-item label="工卡号">
                  <a-input v-model:value="searchFormData.workCardNumber" allow-clear />
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
                      >{{ item.name }}</a-select-option>
                  </a-select>
                </j-form-item>
                <j-form-item label="件号">
                  <a-input 
                    v-model:value="searchFormData.partNumberCode" 
                    allow-clear 
                    placeholder="请输入件号"
                  />
                </j-form-item>
                <j-form-item label="版本号">
                  <a-input v-model:value="searchFormData.versionNumber" allow-clear />
                </j-form-item>
                <j-form-item label="维修类型">
                  <a-select 
                    v-model:value="searchFormData.repairTypeId" 
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
                </j-form-item>
                <j-form-item label="状态">
                <a-select v-model:value="searchFormData.available" placeholder="全部" allow-clear>
                  <a-select-option
                    v-for="item in $enums.AVAILABLE.values()"
                    :key="item.code"
                    :value="item.code"
                    >{{ item.desc }}</a-select-option
                  >
                </a-select>
              </j-form-item>
                <j-form-item label="批准日期" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.approvalDateStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.approvalDateEnd"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
                </j-form-item>
                <j-form-item label="客户">
                  <customer-selector v-model:value="searchFormData.customerCode" @update:value="handleCustomerChange" />
                </j-form-item>
                <j-form-item label="创建时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.createTimeStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.createTimeEnd"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
                </j-form-item>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button
                v-permission="['work-card']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="$refs.addDialog.openDialog()"
                >新增</a-button
              >
              <!-- 工卡附件管理按钮 -->
              <a-button
                :disabled="!selectedRowKeys.length" 
                v-permission="['work-card']"
                :icon="h(PaperClipOutlined)" 
                @click="handleAttachmentManage"
              >
                工卡附件管理
              </a-button>
              <!-- 暂时注释导入按钮 -->
              <!-- <a-button
                v-permission="['work-card:import']"
                :icon="h(CloudUploadOutlined)"
                @click="$refs.importer.openDialog()"
                >导入</a-button
              > -->
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <available-tag :available="row.available" />
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />
    
    <!-- 必换件管理已改为独立页面 -->

    <!-- 工卡附件管理组件 -->
    <work-card-attachment-manage ref="attachmentManageDialog" />

    <!-- 暂时注释导入器组件 -->
    <!-- <work-card-importer ref="importer" @confirm="search" /> -->
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import WorkCardAttachmentManage from '../components/WorkCardAttachmentManage.vue';
  import { useMessage } from '@/hooks/web/useMessage';
  import {
    CheckOutlined,
    CloudUploadOutlined,
    DownOutlined,
    PlusOutlined,
    SearchOutlined,
    PaperClipOutlined,
  } from '@ant-design/icons-vue';
  // 如果不存在导入器组件，可以暂时注释掉
  // import WorkCardImporter from './importer.vue';
  import { workCardApi } from '@/api/work-card/index';
  import * as machineTypeApi from '@/api/base-data/machine-type/index';
  import * as partNumberApi from '@/api/base-data/part-number/index';
  import * as repairTypeApi from '@/api/base-data/repair-type/index';
  import * as customerApi from '@/api/base-data/customer';
  import CustomerSelector from '@/components/Selector/src/CustomerSelector.vue';

  export default defineComponent({
    name: 'WorkCardList',
    components: {
      Add,
      Modify,
      Detail,
      DownOutlined,
      CustomerSelector,
      WorkCardAttachmentManage,
      // WorkCardImporter,
    },
    setup() {
      const { createConfirm, createMessage } = useMessage();
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        CheckOutlined,
        CloudUploadOutlined,
        PaperClipOutlined,
        createConfirm,
        createMessage,
      };
    },
    data() {
      return {
        loading: false,
        id: null,
        selectedRowKeys: [],
        selectedRows: [],
        searchFormData: {
          workCardNumber: '',
          machineTypeId: '',
          partNumberCode: '',
          versionNumber: '',
          repairTypeId: '',
          approvalDateStart: '',
          approvalDateEnd: '',
          customerCode: '',
          available: true,
          createTimeStart: '',
          createTimeEnd: '',
        },
        machineTypeList: [],
        machineTypeLoading: false,
        partNumberList: [],
        partNumberLoading: false,
        repairTypeList: [],
        repairTypeLoading: false,
        customerList: [],
        customerLoading: false,
        availableOptions: [
          { value: 1, text: '启用' },
          { value: 0, text: '停用' },
        ],
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
          zoom: true,
          custom: true,
        },
        tableColumn: [
          { type: 'checkbox', width: 40 },
          { type: 'seq', width: 60 },
          { field: 'code', title: '工卡号', minWidth: 120 },
          { field: 'name', title: '工卡名称', minWidth: 120 },
          { field: 'machineTypeName', title: '机型', minWidth: 120 },
          { field: 'partNumber', title: '件号', minWidth: 120 },
          { field: 'customerName', title: '客户', minWidth: 120 },
          { field: 'repairTypeName', title: '维修类型', minWidth: 120 },
          { field: 'version', title: '版本号', minWidth: 120 },
          { field: 'approvalDate', title: '批准日期', minWidth: 120 },
          {
            field: 'available',
            title: '状态',
            width: 80,
            slots: { default: 'available_default' } 
          },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '创建人', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170 },
          { title: '操作', slots: { default: 'action_default' }, fixed: 'right', width: 220 },
        ],
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
              return workCardApi.query(this.buildQueryParams(page, sorts))
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
      // 加载机型列表
      this.loadMachineTypeList();
      // 加载维修类型列表
      this.loadRepairTypeList();
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
        return {
          code: this.searchFormData.workCardNumber,
          machineTypeId: this.searchFormData.machineTypeId,
          // 注意：后端已移除machineTypeId字段，不需要再传递
          // machineTypeId: this.searchFormData.machineTypeId,
          partNumberCode: this.searchFormData.partNumberCode,
          repairTypeId: this.searchFormData.repairTypeId,
          customerId: this.searchFormData.customerId,
          available: this.searchFormData.available,
          createTimeStart: this.searchFormData.createTimeStart,
          createTimeEnd: this.searchFormData.createTimeEnd,
        };
      },
      createActions(row) {
        return [
          {
            label: '查看',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.viewDialog.openDialog());
            },
          },
          {
            permission: ['work-card'],
            label: '修改',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
          {
            permission: ['work-card'],
            label: '必换件管理',
            onClick: () => {
              // 导航到必换件管理页面
              this.$router.push(`/work-card/product/${row.id}`);
            },
          },
          {
            permission: ['work-card'],
            label: '删除',
            color: 'error',
            danger: true,
            onClick: () => {
              this.createConfirm({
                title: '提示',
                iconType: 'warning',
                content: '确认删除该工卡？',
                onOk: async () => {
                  try {
                    await workCardApi.delete(row.id);
                    this.createMessage.success('删除成功！');
                    this.search();
                  } catch (e) {
                    this.createMessage.error(e?.message || '删除失败');
                  }
                },
              });
            },
          },
        ];
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
        // 切换机型不再联动件号下拉；若需要可在此清空文本输入
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
      // 加载客户列表
      loadCustomerList() {
        this.customerLoading = true;
        customerApi.selector({}).then((res) => {
          this.customerList = res.datas || [];
        }).finally(() => {
          this.customerLoading = false;
        });
      },
      // 处理客户选择变更
      handleCustomerChange(value) {
        if (value) {
          // 获取客户详情
          customerApi.get(value).then(res => {
            if (res) {
              // 可以在这里处理客户相关的其他字段
              // 例如：this.searchFormData.customerName = res.name;
            }
          });
        }
      },
      
      // 处理复选框选择变更
      handleCheckboxChange({ checked, records }) {
        this.selectedRowKeys = records.map(record => record.id);
        this.selectedRows = records;
      },
      
      // 处理全选/取消全选
      handleCheckboxAll({ checked, records }) {
        this.selectedRowKeys = checked ? records.map(record => record.id) : [];
        this.selectedRows = checked ? records : [];
      },
      
      // 处理附件管理
      handleAttachmentManage() {
        if (this.selectedRowKeys.length !== 1) {
          this.$message.warning('请选择一个工卡进行附件管理');
          return;
        }
        
        // 打开附件管理对话框
        this.$refs.attachmentManageDialog.openDialog(this.selectedRowKeys[0]);
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
<style scoped></style>
