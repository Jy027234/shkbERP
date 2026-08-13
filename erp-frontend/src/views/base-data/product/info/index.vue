<template>
  <div>
    <div v-show="visible" v-permission="['base-data:product:info:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="ProductInfo"
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
                <j-form-item label="件号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>
                <j-form-item label="名称">
                  <a-input v-model:value="searchFormData.name" allow-clear />
                </j-form-item>
                <j-form-item v-if="false" label="简称">
                  <a-input v-model:value="searchFormData.shortName" allow-clear />
                </j-form-item>
                <j-form-item v-if="false" label="SKU编号">
                  <a-input v-model:value="searchFormData.skuCode" allow-clear />
                </j-form-item>
                <j-form-item label="分类">
                  <product-category-selector v-model:value="searchFormData.categoryId" />
                </j-form-item>
                <j-form-item label="制造商">
                  <product-brand-selector v-model:value="searchFormData.brandId" />
                </j-form-item>
                <j-form-item label="机型">
                  <a-select
                    v-model:value="searchFormData.machineTypeId"
                    placeholder="请选择机型"
                    allow-clear
                    show-search
                    :filter-option="false"
                    :options="machineTypeList"
                    :loading="machineTypeLoading"
                    @search="handleMachineTypeSearch"
                    @popupScroll="handleMachineTypePopupScroll"
                    @change="handleMachineTypeChange"
                  />
                </j-form-item>
                
                <j-form-item v-if="false" label="航材类型">
                  <a-select
                    v-model:value="searchFormData.productType"
                    placeholder="全部"
                    allow-clear
                  >
                    <a-select-option
                      v-for="item in $enums.PRODUCT_TYPE.values()"
                      :key="item.code"
                      :value="item.code"
                      >{{ item.desc }}</a-select-option
                    >
                  </a-select>
                </j-form-item>
                <j-form-item label="创建日期" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.startTime"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.endTime"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
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
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button
                v-permission="['base-data:product:info:add']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="$router.push('/product/info/add')"
                >新增</a-button
              >
              <a-button
                v-permission="['base-data:product:info:import']"
                :icon="h(CloudUploadOutlined)"
                @click="$refs.importer.openDialog()"
                >导入Excel</a-button
              >
              <a-button
                v-permission="['base-data:product:info:import']"
                :icon="h(CloudUploadOutlined)"
                @click="$refs.batchImporter.openDialog()"
                >批量修改导入</a-button
              >
              <a-button
                v-permission="['base-data:product:info:query']"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
                >导出航材</a-button
              >
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

      <!-- 查看窗口 -->
      <detail :id="id" ref="viewDialog" />
    </div>

    <product-importer ref="importer" @confirm="search" />
    <product-batch-update-importer ref="batchImporter" @confirm="search" />
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import Detail from './detail.vue';
  import ProductImporter from '@/components/Importor/src/ProductImporter.vue';
  import ProductBatchUpdateImporter from '@/components/Importor/src/ProductBatchUpdateImporter.vue';
  import * as api from '@/api/base-data/product/info';
  import * as machineTypeApi from '@/api/base-data/machine-type';
  import { CloudUploadOutlined, PlusOutlined, SearchOutlined, DownloadOutlined } from '@ant-design/icons-vue';

  export default defineComponent({
    name: 'ProductInfo',
    components: {
      Detail,
      ProductImporter,
      ProductBatchUpdateImporter,
    },
    setup() {
      return {
        h,
        CloudUploadOutlined,
        PlusOutlined,
        SearchOutlined,
        DownloadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        visible: true,
        // 当前行数据
        id: '',
        ids: [],
        // 查询列表的查询条件
        searchFormData: {
          code: '',
          name: '',
          skuCode: '',
          categoryId: '',
          brandId: '',
          machineTypeId: '',
          startTime: '',
          endTime: '',
          available: this.$enums.AVAILABLE.ENABLE.code,
        },
        // 机型相关数据
        machineTypeList: [],
        machineTypeLoading: false,
        machineTypePagination: {
          pageIndex: 1,
          pageSize: 20,
          totalCount: 0,
        },
        machineTypeFilter: {
          condition: '',
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
          { type: 'checkbox', width: 45 },
          { field: 'code', title: '件号', width: 120, sortable: true, visible: true },
          { field: 'name', title: '名称', minWidth: 160, sortable: true },
          { field: 'shortName', title: '简称', width: 140, visible: false },
          { field: 'skuCode', title: 'SKU编号', width: 120, visible: false },
          { field: 'categoryName', title: '分类', width: 120 },
          { field: 'brandName', title: '制造商', width: 120 },
          { field: 'machineTypeName', title: '机型', width: 120 },
          { field: 'partNumberCode', title: '件号(旧)', width: 120, visible: false },
          {
            field: 'productType',
            title: '航材类型',
            width: 120,
            formatter: ({ cellValue }) => {
              return this.$enums.PRODUCT_TYPE.getDesc(cellValue);
            },
            visible: false,
          },
          { field: 'available', title: '状态', width: 80, slots: { default: 'available_default' } },
          { 
            field: 'isBatch', 
            title: '批次号管理', 
            width: 90, 
            formatter: ({ cellValue }) => {
              return cellValue ? '是' : '否';
            } 
          },
          { 
            field: 'isSerial', 
            title: '序列号管理', 
            width: 90, 
            formatter: ({ cellValue }) => {
              return cellValue ? '是' : '否';
            } 
          },
          { field: 'createTime', title: '创建时间', width: 170, sortable: true },
          { field: 'updateTime', title: '修改时间', width: 170, sortable: true },
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
              return api.query(this.buildQueryParams(page, sorts));
            },
          },
        },
      };
    },
    created() {
      // 初始化时检查是否需要刷新数据
      this.checkNeedRefresh();
      // 加载机型列表
      this.loadMachineTypeList();
    },
    
    // 页面激活时触发
    activated() {
      // 页面激活时检查是否需要刷新数据
      this.checkNeedRefresh();
    },
    methods: {
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      async handleExport() {
        try {
          this.loading = true;
          const params = this.buildSearchFormData();
          const res = await api.exportCustom(params);
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = '航材信息.xls';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        } finally {
          this.loading = false;
        }
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
          ...this.searchFormData,
        };
      },
      // 检查是否需要刷新数据
      checkNeedRefresh() {
        const needRefresh = localStorage.getItem('product_info_need_refresh');
        if (needRefresh === 'true') {
          // 清除标记
          localStorage.removeItem('product_info_need_refresh');
          // 刷新数据
          this.$nextTick(() => {
            this.search();
          });
        }
      },

      // 加载机型列表
      loadMachineTypeList(isReset = false) {
        if (isReset) {
          this.machineTypePagination.pageIndex = 1;
          this.machineTypeList = [];
        }

        this.machineTypeLoading = true;
        const params = {
          pageIndex: this.machineTypePagination.pageIndex,
          pageSize: this.machineTypePagination.pageSize,
          available: true
        };

        if (this.machineTypeFilter.condition) {
          params.condition = this.machineTypeFilter.condition;
        }

        machineTypeApi.query(params).then(res => {
          if (res && res.datas) {
            if (isReset) {
              this.machineTypeList = [];
            }
            const newList = res.datas.map((item) => ({
              value: item.id,
              label: `${item.name}`,
            }));
            this.machineTypeList = [...this.machineTypeList, ...newList];
            this.machineTypePagination.totalCount = res.totalCount;
          }
        }).finally(() => {
          this.machineTypeLoading = false;
        });
      },

      // 处理机型搜索
      handleMachineTypeSearch(value) {
        this.machineTypeFilter.condition = value;
        this.loadMachineTypeList(true);
      },

      // 处理机型下拉框滚动
      handleMachineTypePopupScroll(e) {
        const { target } = e;
        const { scrollTop, scrollHeight, clientHeight } = target;
        const scrollBottom = scrollHeight - clientHeight - scrollTop;
        if (scrollBottom < 10) {
          if (
            this.machineTypePagination.totalCount > 0 &&
            this.machineTypeList.length < this.machineTypePagination.totalCount &&
            !this.machineTypeLoading
          ) {
            this.machineTypePagination.pageIndex++;
            this.loadMachineTypeList();
          }
        }
      },

      // 处理机型变更
      handleMachineTypeChange(value) {
        // 仅重载列表
        this.search();
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
            permission: ['base-data:product:info:modify'],
            label: '修改',
            onClick: () => {
              this.$router.push('/product/info/modify/' + row.id);
            },
          },
        ];
      },
    },
  });
</script>
<style scoped></style>
