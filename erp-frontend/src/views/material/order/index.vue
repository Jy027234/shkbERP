<template>
  <div>
    <div v-permission="['material:order']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="MaterialOrder"
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
        >
          <template #form>
            <j-border>
              <j-form @collapse="$refs.grid.refreshColumn()">
                <j-form-item label="发料单号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>
                <j-form-item label="仓库">
                  <store-center-selector v-model:value="searchFormData.scId" />
                </j-form-item>
                <j-form-item label="合同编号">
                  <a-input v-model:value="searchFormData.contractCode" allow-clear />
                </j-form-item>
                <j-form-item v-show="false" label="操作人">
                  <user-selector v-model:value="searchFormData.createBy" />
                </j-form-item>
                <j-form-item v-show="false" label="操作日期" :content-nest="false">
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
              <a-button type="primary" :icon="h(SearchOutlined)" @click="reload">查询</a-button>
              <a-button
                v-permission="['material:order']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="openCreateFromApplyDialog"
                >创建发料单</a-button
              >
              <a-button
                type="primary"
                :icon="h(UnorderedListOutlined)"
                @click="openMaterialApplyListDialog"
                >发料申请单</a-button
              >
              <a-button
                v-show="false"
                v-permission="['material:order']"
                type="primary"
                :icon="h(DownloadOutlined)"
                @click="exportList"
                >导出</a-button
              >
            </a-space>
          </template>
          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>

      <!-- 查看窗口 -->
      <detail :id="id" ref="viewDialog" />

      <!-- 基于申请单创建发料单窗口 -->
      <create-from-apply ref="createFromApplyDialog" @confirm="reload" />

      <!-- 发料申请单列表窗口 -->
      <material-apply-list-dialog ref="materialApplyListDialog" />
    </div>
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import Detail from './detail.vue';
  import CreateFromApply from './create-from-apply.vue';
  import MaterialApplyListDialog from './material-apply-list-dialog.vue';
  import { SearchOutlined, DownloadOutlined, PlusOutlined, UnorderedListOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/material/order';

  export default defineComponent({
    name: 'MaterialOrder',
    components: {
      Detail,
      CreateFromApply,
      MaterialApplyListDialog,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        DownloadOutlined,
        PlusOutlined,
        UnorderedListOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        // 查询列表的查询条件
        searchFormData: {
          code: '',
          scId: '',
          contractCode: '',
          createBy: '',
          createTimeStart: '',
          createTimeEnd: '',
        },
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
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
              return this.search(page, sorts);
            },
          },
        },
        // 列表列配置
        tableColumn: [
          { type: 'seq', width: 50 },
          { field: 'isOutFinish', title: '出库完成', width: 80, formatter: ({ cellValue }) => {
            return cellValue ? '是' : '否';
          } },
          { field: 'code', title: '发料单号', width: 180, sortable: true },
          { field: 'scName', title: '仓库', width: 120 },
          { field: 'materialApplyCode', title: '发料申请单号', width: 180 },
          { field: 'contractCode', title: '合同编号', width: 150 },
          { field: 'machineTypeCode', title: '机型', width: 120 },
          { field: 'partNumberCode', title: '件号', width: 120 },
          { field: 'customerName', title: '客户', width: 150 },
          { field: 'totalNum', title: '航材数量', width: 100, align: 'right' },
          { field: 'totalAmount', title: '总金额', width: 120, align: 'right' },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '操作人', width: 100, visible: false },
          { field: 'createTime', title: '操作时间', width: 170, sortable: true },
          { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
        ],
      };
    },
    created() {
      this.openDialog = false;
    },
    methods: {
      // 重新加载列表（回到第一页）
      reload() {
        this.$refs.grid.commitProxy('reload');
      },
      // 列表发生查询时的事件
      search(page, sorts) {
        ++this.loading;
        return api
          .query(this.buildQueryParams(page, sorts))
          .then((res) => {
            return res;
          })
          .finally(() => {
            --this.loading;
          });
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        return {
          ...this.buildSearchFormData(),
          ...this.$utils.buildSortPageVo(page, sorts),
        };
      },
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        const params = Object.assign({}, this.searchFormData);
        return params;
      },
      exportList() {
        this.loading = true;
        api
          .exportList(this.buildSearchFormData())
          .finally(() => {
            this.loading = false;
          });
      },
      // 打开基于申请单创建发料单对话框
      openCreateFromApplyDialog() {
        this.$refs.createFromApplyDialog.openDialog();
      },
      // 打开发料申请单列表对话框
      openMaterialApplyListDialog() {
        this.$refs.materialApplyListDialog.openDialog();
      },
      createActions(row) {
        const actions = [
          {
            label: '查看',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.viewDialog.openDialog());
            },
          },
        ];

        if (row.totalNum === 0) {
          actions.push({
            label: '撤回',
            onClick: () => {
              this.$confirm({
                title: '确认撤回',
                content: '确定要撤回该发料单吗？撤回后将无法恢复！',
                okText: '确定',
                cancelText: '取消',
                onOk: () => {
                  api.withdraw(row.id).then(() => {
                    this.$message.success('撤回成功');
                    this.reload();
                  });
                },
              });
            },
          });
        }

        return actions;
      },
    },
  });
</script>
<style scoped></style>
