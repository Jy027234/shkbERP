<template>
  <div v-permission="['product:storage']">
    <page-wrapper content-full-height fixed-height>
      <vxe-grid
        id="ShkbProductStorage"
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
            <j-form label-width="100px" @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="客户名称">
                <a-input v-model:value="searchFormData.clientName" allow-clear />
              </j-form-item>
              <j-form-item label="产品名称">
                <a-input v-model:value="searchFormData.productName" allow-clear />
              </j-form-item>
              <j-form-item label="件号">
                <a-input v-model:value="searchFormData.productCode" allow-clear />
              </j-form-item>
              <j-form-item label="序列号">
                <a-input v-model:value="searchFormData.serialNumber" allow-clear />
              </j-form-item>
            </j-form>
          </j-border>
        </template>
        <!-- 工具栏 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
            <a-button type="primary" :icon="h(PlusOutlined)" @click="$refs.addDialog.openDialog()">新增</a-button>
            <a-button v-if="false" danger :icon="h(DeleteOutlined)" @click="batchDelete">批量删除</a-button>
            <a-divider type="vertical" />
            <a-button
              :disabled="selectedRowKeys.length !== 1"
              :icon="h(PaperClipOutlined)"
              @click="handleAttachmentManage"
            >接收单附件管理</a-button>
          </a-space>
        </template>

        <!-- 操作 列自定义内容 -->
        <template #action_default="{ row }">
          <table-action outside :actions="createActions(row)" />
        </template>
      </vxe-grid>
    </page-wrapper>

    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />
    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />
    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />
    <!-- 附件管理窗口 -->
    <attachment-manage ref="attachmentManageDialog" />
  </div>
</template>
<script>
  import { defineComponent, h } from 'vue';
  import { SearchOutlined, PlusOutlined, DeleteOutlined, PaperClipOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/shkb/product-storage';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import AttachmentManage from './AttachmentManage.vue';

  export default defineComponent({
    name: 'ShkbProductStorage',
    components: { Add, Modify, Detail, AttachmentManage },
    setup() {
      return { h, SearchOutlined, PlusOutlined, DeleteOutlined, PaperClipOutlined };
    },
    data() {
      return {
        loading: false,
        id: '',
        // 选中行
        selectedRowKeys: [],
        selectedRows: [],
        // 查询条件
        searchFormData: {
          clientName: '',
          productName: '',
          productCode: '',
          serialNumber: '',
        },
        // 工具栏配置
        toolbarConfig: {
          slots: { buttons: 'toolbar_buttons' },
        },
        // 列配置
        tableColumn: [
          { type: 'checkbox', width: 45 },
          { field: 'clientName', title: '客户名称', minWidth: 140 },
          { field: 'productName', title: '产品名称', minWidth: 160 },
          { field: 'productCode', title: '件号', minWidth: 120 },
          { field: 'serialNumber', title: '序列号', minWidth: 140 },
          { field: 'storageTime', title: '入库时间', width: 170, sortable: true },
          { field: 'storageTrackingNumber', title: '入库单号', minWidth: 160 },
          { field: 'deliveryTime', title: '出库时间', width: 170, sortable: true },
          { field: 'deliveryReason', title: '出库原因', minWidth: 160 },
          { field: 'description', title: '备注', minWidth: 180 },
          { title: '操作', minWidth: 180, fixed: 'right', slots: { default: 'action_default' } },
        ],
        // 请求接口配置
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'totalCount',
          },
          ajax: {
            query: ({ page, sorts }) => api.query(this.buildQueryParams(page, sorts)),
          },
        },
      };
    },
    methods: {
      // 查询
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      // 复选框变化
      handleCheckboxChange({ checked, row }) {
        if (checked) {
          this.selectedRowKeys.push(row.id);
          this.selectedRows.push(row);
        } else {
          const i = this.selectedRowKeys.indexOf(row.id);
          if (i > -1) {
            this.selectedRowKeys.splice(i, 1);
            this.selectedRows.splice(i, 1);
          }
        }
      },
      // 全选/取消全选
      handleCheckboxAll({ checked, rows }) {
        if (checked) {
          this.selectedRowKeys = rows.map((r) => r.id);
          this.selectedRows = [...rows];
        } else {
          this.selectedRowKeys = [];
          this.selectedRows = [];
        }
      },
      // 构造查询参数
      buildQueryParams(page, sorts) {
        return {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.searchFormData,
        };
      },
      // 附件管理
      handleAttachmentManage() {
        if (this.selectedRowKeys.length !== 1) {
          this.$msg.createWarning('请选择一条记录进行附件管理');
          return;
        }
        this.$refs.attachmentManageDialog.openDialog(this.selectedRowKeys[0]);
      },
      // 单条删除
      handleDelete(id) {
        this.$msg.createConfirm('是否确认删除所选记录？').then(() => {
          this.loading = true;
          api
            .deleteById(id)
            .then(() => {
              this.$msg.createSuccess('删除成功！');
              this.search();
            })
            .finally(() => (this.loading = false));
        });
      },
      // 批量删除
      batchDelete() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择需要删除的数据！');
          return;
        }
        const ids = records.map((i) => i.id);
        this.$msg.createConfirm('是否确认批量删除所选记录？').then(() => {
          this.loading = true;
          api
            .batchDelete(ids)
            .then(() => {
              this.$msg.createSuccess('删除成功！');
              this.search();
            })
            .finally(() => (this.loading = false));
        });
      },
      createActions(row) {
        return [
          {
            label: '查看',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => {
                if (this.$refs.viewDialog && this.$refs.viewDialog.openDialog) {
                  this.$refs.viewDialog.openDialog();
                }
              });
            },
          },
          {
            label: '修改',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => {
                if (this.$refs.updateDialog && this.$refs.updateDialog.openDialog) {
                  this.$refs.updateDialog.openDialog();
                }
              });
            },
          },
          {
            label: '删除',
            color: 'error',
            onClick: () => this.handleDelete(row.id),
          },
        ];
      },
    },
  });
</script>
<style scoped></style>
