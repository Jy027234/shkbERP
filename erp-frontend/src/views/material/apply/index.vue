<template>
    <div>
      <div v-permission="['material:apply']">
        <page-wrapper content-full-height fixed-height>
          <!-- 数据列表 -->
          <vxe-grid
            id="Shop"
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
            :fit="true"
            :scroll-x="{
              gt: 10,
              enabled: true
            }" 
            @checkbox-change="handleCheckboxChange"
            @checkbox-all="handleCheckboxAll"
          >
            <template #form>
              <j-border>
                <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                  <j-form-item label="申请编号">
                    <a-input v-model:value="searchFormData.applyCode" allow-clear />
                  </j-form-item>
                  <j-form-item label="合同编号">
                    <a-input v-model:value="searchFormData.contractCode" allow-clear />
                  </j-form-item>
                  <j-form-item label="申请时间" :content-nest="false">
                    <div class="date-range-container">
                      <a-date-picker
                        v-model:value="searchFormData.applyTimeStart"
                        placeholder=""
                        value-format="YYYY-MM-DD 00:00:00"
                      />
                      <span class="date-split">至</span>
                      <a-date-picker
                        v-model:value="searchFormData.applyTimeEnd"
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
                <a-divider type="vertical" />
                <a-button 
                  type="primary" 
                  :icon="h(CheckOutlined)" 
                  @click="handleMaterialApproval"
                  :disabled="selectedRowKeys.length === 0"
                >发料审批</a-button>
                <a-divider type="vertical" />
                <a-button 
                  type="primary" 
                  :icon="h(FileTextOutlined)" 
                  @click="handleViewPartsList"
                  :disabled="selectedRowKeys.length !== 1"
                >换件清单</a-button>
                <a-divider type="vertical" />
                <a-button 
                  v-show="false"
                  type="primary" 
                  :icon="h(ThunderboltOutlined)" 
                  @click="handleMaterialIssue"
                  :disabled="selectedRowKeys.length !== 1"
                >发料出库</a-button>
              </a-space>
            </template>
  
            <!-- 必换件单号 列自定义内容 -->
            <template #replacementPartCode_default="{ row }">
              <a @click="handleViewReplacementPart(row)">{{ row.replacementPartCode }}</a>
            </template>
            
            <!-- 非必换件单号 列自定义内容 -->
            <template #nonReplacementPartCode_default="{ row }">
              <a @click="handleViewNonReplacementPart(row)">{{ row.nonReplacementPartCode }}</a>
            </template>
            
            <!-- 审批状态 列自定义内容 -->
            <template #approvalStatusText_default="{ row }">
              <a-tag v-if="row.approvalStatus === 1" color="success">{{ row.approvalStatusText }}</a-tag>
              <a-tag v-else-if="row.approvalStatus === 2" color="error">{{ row.approvalStatusText }}</a-tag>
              <a-tag v-else-if="row.approvalStatus === 0" color="processing">{{ row.approvalStatusText }}</a-tag>
              <span v-else>-</span>
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
      <detail :record="currentViewRecord" ref="viewDialog" />
      
      <!-- 发料审批窗口 -->
      <material-approval 
        v-model:visible="materialApprovalVisible" 
        :selectedRecords="selectedRows"
        @confirm="handleApprovalConfirm"
      />
      
      <!-- 必换件单查看窗口 -->
      <replacement-part-view 
        v-model:visible="replacementPartViewVisible" 
        :record="currentRecord"
        @confirm="handleReplacementPartConfirm"
      />
      
      <!-- 非必换件单查看窗口 -->
      <non-replacement-part-view 
        v-model:visible="nonReplacementPartViewVisible" 
        :record="currentRecord"
        @confirm="handleNonReplacementPartConfirm"
      />
      
      <!-- 换件清单查看窗口 -->
      <parts-list-view
        v-model:open="partsListViewVisible"
        :record="currentRecord"
      />
      
      <!-- 发料出库窗口 -->
      <material-issue-dialog
        v-model:visible="materialIssueVisible"
        :record="currentRecord"
        @confirm="handleMaterialIssueConfirm"
      />
    </div>
  </template>
  
  <script>
    import { defineComponent, h } from 'vue';
    import Add from './add.vue';
    import Modify from './modify.vue';
    import Detail from './detail.vue';
    import MaterialApproval from './components/MaterialApproval.vue';
    import ReplacementPartView from './components/ReplacementPartView.vue';
    import NonReplacementPartView from './components/NonReplacementPartView.vue';
    import PartsListView from './components/PartsListView.vue';
    import MaterialIssueDialog from './components/MaterialIssueDialog.vue';
    import {
      CheckOutlined,
      CloudUploadOutlined,
      DownOutlined,
      PlusOutlined,
      SearchOutlined,
      SettingOutlined,
      StopOutlined,
      ThunderboltOutlined,
      FileOutlined,
      FileTextOutlined,
    } from '@ant-design/icons-vue';
    import { message } from 'ant-design-vue';
    import { query, reopenByApplyCode } from '@/api/material/apply';
  
    export default defineComponent({
      name: 'MaterialApply',
      components: {
        Add,
        Modify,
        Detail,
        MaterialApproval,
        ReplacementPartView,
        NonReplacementPartView,
        PartsListView,
        MaterialIssueDialog,
        DownOutlined,
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
          FileOutlined,
          FileTextOutlined,
        };
      },
      data() {
        return {
          loading: false,
          // 当前行数据
          id: '',
          // 当前查看的记录
          currentViewRecord: {},
          // 选中的行键值
          selectedRowKeys: [],
          // 选中的行数据
          selectedRows: [],
          // 当前操作的记录
          currentRecord: null,
          // 发料审批弹窗可见性
          materialApprovalVisible: false,
          // 必换件单查看弹窗可见性
          replacementPartViewVisible: false,
          // 非必换件单查看弹窗可见性
          nonReplacementPartViewVisible: false,
          // 换件清单查看弹窗可见性
          partsListViewVisible: false,
          // 发料出库弹窗可见性
          materialIssueVisible: false,
          // 查询列表的查询条件
          searchFormData: {
            applyCode: '',
            contractCode: '',
            replacementPartCode: '',
            nonReplacementPartCode: '',
            applyTimeStart: '',
            applyTimeEnd: '',
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
            { type: 'checkbox', width: 40 },
            { field: 'applyCode', title: '申请编号', width: 160 },
            { field: 'contractCode', title: '合同编号', width: 160 },
            { field: 'createTime', title: '申请时间', width: 160 },
            { field: 'machineTypeName', title: '机型', width: 120 },
            { field: 'partNumberName', title: '件号', width: 120 },
            { field: 'serialNumber', title: '产品序号', width: 140 },
            { field: 'replacementPartCode', title: '必换件单号', width: 160, slots: { default: 'replacementPartCode_default' } },
            { field: 'nonReplacementPartCode', title: '非必换件单号', width: 160, slots: { default: 'nonReplacementPartCode_default' } },
            { field: 'approvalStatusText', title: '审批状态', width: 120, slots: { default: 'approvalStatusText_default' } },
            { field: 'approvalTime', title: '审批时间', width: 160 },
            { field: 'remark', title: '备注', minWidth: 200 },
            { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
          ],
          // 请求接口配置
          proxyConfig: {
            autoLoad: true,
            message: true,
            props: {
              result: 'datas',
              total: 'totalCount',
            },
            ajax: {
              query: ({ page, sorts }) => {
                return this.query({ page, sorts });
              },
            },
          },
        };
      },
      created() {},
      methods: {
        // 查询接口
        query({ page }) {
          // 构建查询参数
          const params = this.buildQueryParams(page);
          this.loading = true;
          
          // 调用真实API
          return query(params)
            .then(res => {
              console.log('API返回数据:', res);
              // 直接使用后端返回的数据，不需要再补充模拟数据
              return res;
            })
            .catch(err => {
              message.error(err.message || '获取领料申请列表失败');
              return {
                datas: [],
                totalCount: 0,
                pageSize: 10,
                pageIndex: 1,
                totalPage: 0,
                hasPrev: false,
                hasNext: false
              };
            })
            .finally(() => {
              this.loading = false;
            });
        },
        // 列表发生查询时的事件
        search() {
          this.$refs.grid.commitProxy('reload');
        },
        // 查询前构建查询参数结构
        buildQueryParams(page, sorts) {
          // 后端需要pageIndex和pageSize
          const currentPage = page.currentPage || 1;
          const pageSize = page.pageSize || 10;
          
          return {
            pageIndex: currentPage,
            pageSize: pageSize,
            ...this.buildSearchFormData(),
          };
        },
        // 查询前构建具体的查询参数
        buildSearchFormData() {
          return {
            applyCode: this.searchFormData.applyCode || undefined,
            contractCode: this.searchFormData.contractCode || undefined,
            createTimeStart: this.searchFormData.applyTimeStart || undefined,
            createTimeEnd: this.searchFormData.applyTimeEnd || undefined,
          };
        },
        createActions(row) {
          const actions = [
            {
              label: '查看',
              onClick: () => {
                this.currentViewRecord = { ...row };
                this.$nextTick(() => this.$refs.viewDialog.openDialog());
              },
            },
          ];

          // 已审核通过的，补充“补提重审”按钮
          if (row.approvalStatus === 1) {
            actions.push({
              label: '补提重审',
              onClick: () => this.reopenApply(row),
            });
          }

          return actions;
        },

        // 补提重审
        reopenApply(row) {
          const code = row.applyCode;
          const contractCode = row.contractCode;
          if (!code) {
            this.$message.error('申请编号缺失，无法执行补提重审');
            return;
          }
          const msg = `确定要对合同编号【${contractCode}】执行补提重审吗？\n此操作会将该申请退回至待审状态。`;
          this.$msg
            .createConfirm(msg)
            .then(() => {
              this.loading = true;
              return reopenByApplyCode(code)
                .then(() => {
                  this.$message.success('已退回重审');
                  this.search();
                })
                .finally(() => {
                  this.loading = false;
                });
            })
            .catch(() => {});
        },
        
        // 复选框变化事件
        handleCheckboxChange({ checked, row }) {
          if (checked) {
            this.selectedRowKeys.push(row.id);
            this.selectedRows.push(row);
          } else {
            const index = this.selectedRowKeys.indexOf(row.id);
            if (index !== -1) {
              this.selectedRowKeys.splice(index, 1);
              this.selectedRows.splice(index, 1);
            }
          }
        },
        
        // 复选框全选事件
        handleCheckboxAll({ checked, data }) {
          if (checked) {
            this.selectedRowKeys = data.map(item => item.id);
            this.selectedRows = [...data];
          } else {
            this.selectedRowKeys = [];
            this.selectedRows = [];
          }
        },
        
        // 发料审批处理
        handleMaterialApproval() {
          if (this.selectedRowKeys.length === 0) {
            this.$message.warning('请先选择需要审批的记录');
            return;
          }
          this.materialApprovalVisible = true;
        },
        
        // 审批确认处理
        handleApprovalConfirm(result) {
          const action = result.approved ? '审批通过' : '审批不通过';
          this.$message.success(`${action}成功，共${result.records.length}条记录`);
          
          // 刷新列表
          this.search();
          
          // 清空选中状态
          this.selectedRowKeys = [];
          this.selectedRows = [];
        },
        
        // 查看必换件单
        handleViewReplacementPart(row) {
          this.currentRecord = row;
          this.replacementPartViewVisible = true;
        },
        
        // 查看非必换件单
        handleViewNonReplacementPart(row) {
          this.currentRecord = row;
          this.nonReplacementPartViewVisible = true;
        },
        
        // 必换件单保存确认
        handleReplacementPartConfirm(result) {
          // this.$message.success(`必换件单修改成功，单号：${result.record.replacementPartCode}`);
          // 刷新列表
          this.search();
        },
        
        // 非必换件单保存确认
        handleNonReplacementPartConfirm(result) {
          // this.$message.success(`非必换件单修改成功，单号：${result.record.nonReplacementPartCode}`);
          // 刷新列表
          this.search();
        },
        
        // 查看换件清单
        handleViewPartsList() {
          if (this.selectedRowKeys.length !== 1) {
            this.$message.warning('请选择一条记录查看换件清单');
            return;
          }
          this.currentRecord = this.selectedRows[0];
          this.partsListViewVisible = true;
        },
        
        // 发料出库
        handleMaterialIssue() {
          if (this.selectedRowKeys.length !== 1) {
            this.$message.warning('请选择一条记录进行发料出库');
            return;
          }
          
          const record = this.selectedRows[0];
          
          // 检查是否已审批通过
          if (record.approvalStatus !== 1) {
            this.$message.warning('只有审批通过的申请才能进行发料出库');
            return;
          }
          
          this.currentRecord = record;
          this.materialIssueVisible = true;
        },
        
        // 发料出库确认
        handleMaterialIssueConfirm(result) {
          if (result.success) {
            this.$message.success('发料出库成功');
            // 刷新列表
            this.search();
            // 清空选中状态
            this.selectedRowKeys = [];
            this.selectedRows = [];
          }
        },
      },
    });
  </script>
  <style scoped></style>
  