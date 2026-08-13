<template>
  <div v-permission="['stock:product-serial']">
    <page-wrapper content-full-height fixed-height>
      <!-- 数据列表 -->
      <vxe-grid
        id="ProductStockSerial"
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
            <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="仓库">
                <store-center-selector v-model:value="searchFormData.scId" />
              </j-form-item>
              <j-form-item label="航材件号">
                <a-input v-model:value="searchFormData.productCode" allow-clear />
              </j-form-item>
              <j-form-item label="航材名称">
                <a-input v-model:value="searchFormData.productName" allow-clear />
              </j-form-item>
              <j-form-item label="航材分类">
                <product-category-selector
                  v-model:value="searchFormData.categoryId"
                  :only-final="false"
                />
              </j-form-item>
              <j-form-item label="航材制造商">
                <product-brand-selector v-model:value="searchFormData.brandId" />
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
            <a-button
              v-permission="['stock:product-serial']"
              type="primary"
              :icon="h(DownloadOutlined)"
              @click="exportList"
              >导出</a-button
            >
          </a-space>
        </template>
        <!-- 操作列 -->
        <template #col_action="{ row }">
          <a-space>
            <a
              v-permission="['stock:product-serial']"
              href="javascript:void(0);"
              @click="modify(row)"
            >
              修改
            </a>
            <a
              v-permission="['stock:product-serial:modify']"
              href="javascript:void(0);"
              @click="modifySerialNumber(row)"
            >
              修改序列号
            </a>
            <a
              v-permission="['stock:product-serial']"
              href="javascript:void(0);"
              @click="openAttachmentDialog(row)"
            >
              附件管理
            </a>
          </a-space>
        </template>
      </vxe-grid>
    </page-wrapper>
    
    <!-- 修改序列号库存 -->
    <update-product-stock-serial-dialog
      :id="id"
      ref="updateDialog"
      @success="search"
    />
    
    <!-- 修改序列号 -->
    <update-product-stock-serial-number-dialog
      :id="id"
      ref="updateSerialNumberDialog"
      @success="search"
    />
    
    <!-- 序列号库存附件管理 -->
    <product-serial-attachment
      ref="attachmentDialog"
    />
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import { SearchOutlined, DownloadOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/sc/stock/product-stock-serial/index';
  import UpdateProductStockSerialDialog from './components/update-product-stock-serial-dialog.vue';
import UpdateProductStockSerialNumberDialog from './components/update-product-stock-serial-number-dialog.vue';
import ProductSerialAttachment from './components/product-serial-attachment.vue';

  export default defineComponent({
    name: 'ProductStockSerial',
    components: {
      UpdateProductStockSerialDialog,
      UpdateProductStockSerialNumberDialog,
      ProductSerialAttachment,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        DownloadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',

        // 查询列表的查询条件
        searchFormData: {
          scId: '',
          productCode: '',
          productName: '',
          categoryId: '',
          brandId: '',
          serialNumber: '',
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
          { type: 'seq', width: 50 },
          { field: 'scCode', title: '仓库编号', width: 100, sortable: true },
          { field: 'scName', title: '仓库名称', minWidth: 160 },
          { field: 'productCode', title: '航材件号', width: 120,},
          { field: 'productName', title: '航材名称', minWidth: 180 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          { field: 'supplierName', title: '供应商', width: 150 },
          { field: 'serialNumber', title: '序列号', width: 120 },
          { field: 'shelfLocation', title: '架位', minWidth: 120 },
          { field: 'stockStatus', title: '库存状态', width: 80, formatter: ({ cellValue }) => {
            return cellValue === true ? '在库' : '出库';
          } },
          { field: 'productionDate', title: '生产日期', width: 130 },
          { field: 'expiryDate', title: '失效日期', width: 130 },
          { title: '操作', width: 160, fixed: 'right', slots: { default: 'col_action' } },
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
              return api.query(this.buildQueryParams(page, sorts));
            },
          },
        },
      };
    },
    created() {},
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
        const params = Object.assign({}, this.searchFormData, {
          scId: this.searchFormData.scId,
          categoryId: this.searchFormData.categoryId,
          brandId: this.searchFormData.brandId,
          serialNumber: this.searchFormData.serialNumber,
        });

        return params;
      },
      // 导出数据
      exportList() {
        this.loading = true;
        api
          .exportList(this.buildQueryParams({}))
          .then(() => {
            this.$msg.createSuccessTip('导出成功！');
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 修改序列号库存
      modify(row) {
        this.id = row.id;
        this.$refs.updateDialog.openDialog();
      },
      // 修改序列号
      modifySerialNumber(row) {
        this.id = row.id;
        this.$refs.updateSerialNumberDialog.openDialog();
      },
      // 打开附件管理对话框
      openAttachmentDialog(row) {
        this.$refs.attachmentDialog.openDialog(row.id);
      },
    },
  });
</script>
<style scoped></style>
