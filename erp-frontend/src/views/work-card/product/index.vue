<template>
  <div>
    <page-wrapper>
      <!-- 工卡基本信息 -->
      <a-card title="工卡信息" :bordered="false" class="card-info">
        <a-descriptions :column="4" bordered>
          <a-descriptions-item label="工卡号" :span="2">
            {{ workCardInfo.code }}
          </a-descriptions-item>
          <a-descriptions-item label="工卡名称" :span="2">
            {{ workCardInfo.name }}
          </a-descriptions-item>
          <a-descriptions-item label="机型" :span="2">
            {{ workCardInfo.machineTypeName }}
          </a-descriptions-item>
          <a-descriptions-item label="件号" :span="2">
            {{ workCardInfo.partNumber }}
          </a-descriptions-item>
          <a-descriptions-item label="维修类型" :span="2">
            {{ workCardInfo.repairTypeName }}
          </a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 必换件管理 -->
      <div class="table-operator">
        <a-space>
          <a-button
            v-permission="['work-card']"
            type="primary"
            @click="handleAddProducts"
          >
            <template #icon>
              <PlusOutlined />
            </template>
            添加必换件
          </a-button>
          <a-button
            v-permission="['work-card']"
            :disabled="selectedRowKeys.length === 0"
            danger
            @click="handleBatchDelete"
          >
            <template #icon>
              <DeleteOutlined />
            </template>
            批量删除
          </a-button>
          <a-button
            v-permission="['work-card']"
            :disabled="!hasQuantityChanged"
            type="primary"
            @click="handleSaveQuantity"
          >
            <template #icon>
              <SaveOutlined />
            </template>
            保存数量
          </a-button>
          <a-button @click="goBack">
            <template #icon>
              <RollbackOutlined />
            </template>
            返回
          </a-button>
        </a-space>
        <div class="table-summary">共 {{ productList.length }} 条记录</div>
      </div>

      <div class="table-scroll-wrap">
        <vxe-grid
          ref="gridRef"
          border
          :show-header-overflow="true"
          :show-overflow="true"
          :height="430"
          :scroll-y="{ enabled: true }"
          :data="productList"
          :columns="vxColumns"
          @checkbox-change="onGridSelectChange"
          @checkbox-all="onGridSelectChange"
        >
          <template #quantity_default="{ row }">
            <a-input-number
              v-model:value="row.quantity"
              :min="1"
              :max="9999"
              @change="() => handleQuantityChange(row, row.quantity)"
            />
          </template>
        </vxe-grid>
      </div>

      <!-- 航材选择器组件 -->
      <product-selector
        v-show="false"
        ref="productSelector"
        v-model:value="selectedProductIds"
        :request-params="{ available: true }"
        multiple
        @input-row="handleProductSelected"
      />
      <!-- 航材选择器直接打开 -->
    </page-wrapper>
  </div>
</template>

<script>
  import { defineComponent, ref, reactive, onMounted, nextTick } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { useRoute, useRouter } from 'vue-router';
  import {
    PlusOutlined,
    DeleteOutlined,
    SearchOutlined,
    RollbackOutlined,
    SaveOutlined,
  } from '@ant-design/icons-vue';
  import { workCardApi } from '@/api/work-card/index';
  import * as productApi from '@/api/base-data/product/info';
  import ProductSelector from '@/components/Selector/src/ProductSelector.vue';

  export default defineComponent({
    name: 'WorkCardProductManage',
    components: {
      ProductSelector,
      PlusOutlined,
      DeleteOutlined,
      SearchOutlined,
      RollbackOutlined,
      SaveOutlined,
    },
    setup() {
      const route = useRoute();
      const router = useRouter();

      // 获取路由参数中的工卡ID
      const workCardId = ref(route.params.id || '');

      // 航材选择器选中的航材ID
      const selectedProductIds = ref([]);

      // 是否显示加载框
      const loading = ref(false);
      // 表格加载状态
      const tableLoading = ref(false);
      // 工卡信息
      const workCardInfo = ref({});
      // 必换件列表
      const productList = ref([]);
      // 选中的行
      const selectedRowKeys = ref([]);
      // 原始数量数据，用于比较是否有变化
      const originalQuantities = ref({});
      // 是否有数量变化
      const hasQuantityChanged = ref(false);

      // 当前激活的标签页
      const activeTab = ref('productList');

      // 航材搜索参数
      const searchParams = ref({
        code: '',
        name: '',
        available: true,
      });

      // 航材搜索加载状态
      const searchLoading = ref(false);

      // 航材搜索结果列表
      const searchProductList = ref([]);

      // 选中的航材行
      const selectedProductKeys = ref([]);

      // vxe-grid 列定义（不分页，显示全部数据）
      const vxColumns = [
        { type: 'checkbox', width: 40, align: 'center' },
        { type: 'seq', title: '序号', width: 70, align: 'center' },
        { field: 'productName', title: '航材名称', minWidth: 250 },
        { field: 'partNumber', title: '件号', minWidth: 250 },
        { field: 'machineType', title: '机型', minWidth: 250 },
        { field: 'productSpec', title: '规格', minWidth: 120 },
        { field: 'productUnit', title: '单位', minWidth: 80 },
        { field: 'quantity', title: '数量', minWidth: 100, slots: { default: 'quantity_default' } },
      ];

      // 航材选择表格列定义
      const productColumns = [
        {
          title: '航材编号',
          dataIndex: 'code',
          width: 120,
          visible: false,
        },
        {
          title: '航材名称',
          dataIndex: 'name',
          width: 250,
        },
        {
          title: '规格',
          dataIndex: 'spec',
          width: 120,
        },
        {
          title: '单位',
          dataIndex: 'unit',
          width: 80,
        },
      ];

      // 返回工卡列表
      const goBack = () => {
        router.back();
      };

      // 加载数据
      const loadData = () => {
        if (!workCardId.value) {
          message.error('工卡ID不能为空');
          return;
        }

        loading.value = true;

        // 加载工卡信息
        workCardApi
          .get(workCardId.value)
          .then((data) => {
            workCardInfo.value = data;
            // 加载必换件列表
            loadProductList();
          })
          .catch(() => {
            loading.value = false;
          });
      };

      // 加载必换件列表
      const loadProductList = () => {
        tableLoading.value = true;
        workCardApi
          .getProducts(workCardId.value)
          .then((data) => {
            const list = Array.isArray(data) ? data : [];
            productList.value = list.map((item, idx) => ({
              ...item,
              _key: item.id ?? `${item.productId || item.code || 'row'}-${idx}`,
            }));
            // 初始化每个产品的数量，如果后端没有返回数量，默认为1
            productList.value.forEach(item => {
              if (!item.quantity) {
                item.quantity = 1;
              }
            });
            // 保存原始数量数据，用于比较是否有变化
            saveOriginalQuantities();
          })
          .finally(() => {
            loading.value = false;
            tableLoading.value = false;
          });
      };
      
      // 保存原始数量数据
      const saveOriginalQuantities = () => {
        const quantities = {};
        productList.value.forEach(item => {
          quantities[item.id] = item.quantity;
        });
        originalQuantities.value = quantities;
        hasQuantityChanged.value = false;
      };
      
      // 处理数量变更
      const handleQuantityChange = (record, value) => {
        // 检查是否有数量变化
        const hasChanges = productList.value.some(item => {
          return originalQuantities.value[item.id] !== item.quantity;
        });
        hasQuantityChanged.value = hasChanges;
      };

      // vxe-grid 选择变更
      const gridRef = ref(null);
      const onGridSelectChange = () => {
        const rows = gridRef.value ? gridRef.value.getCheckboxRecords() : [];
        selectedRowKeys.value = rows.map((r) => r._key || r.id || r.productId || r.code);
      };

      // 搜索航材
      const searchProducts = () => {
        searchLoading.value = true;
        productApi
          .selector({
            code: searchParams.value.code,
            name: searchParams.value.name,
            available: searchParams.value.available,
          })
          .then((res) => {
            searchProductList.value = res.datas || [];
          })
          .finally(() => {
            searchLoading.value = false;
          });
      };

      // 重置搜索
      const resetSearch = () => {
        searchParams.value = {
          code: '',
          name: '',
          available: true,
        };
        searchProducts();
      };

      // 打开航材选择器
      const handleAddProducts = () => {
        // 清空选中的航材ID
        selectedProductIds.value = [];

        // 使用 nextTick 确保组件已渲染
        nextTick(() => {
          // 直接访问内部的 selector 组件
          if (productSelector.value && productSelector.value.$refs.selector) {
            productSelector.value.$refs.selector.dialogVisible = true;
          }
        });
      };

      // 处理航材选择
      const handleProductSelected = (selectedRows) => {
        if (!selectedRows || selectedRows.length === 0) {
          return;
        }

        // 提取航材ID
        const productIds = selectedRows.map((item) => item.id);

        // 批量添加必换件
        tableLoading.value = true;
        workCardApi
          .batchAddProducts({
            workCardId: workCardId.value,
            productIds,
          })
          .then(() => {
            message.success('添加成功');
            // 重新加载必换件列表
            loadProductList();
            // 清空选中
            selectedProductIds.value = [];
          })
          .finally(() => {
            tableLoading.value = false;
          });
      };

      // 批量删除
      const handleBatchDelete = () => {
        if (selectedRowKeys.value.length === 0) {
          return;
        }

        // 获取选中的航材ID
        const rows = gridRef.value ? gridRef.value.getCheckboxRecords() : [];
        const productIds = rows.map((item) => item.productId);

        if (productIds.length === 0) {
          return;
        }

        // 确认删除
        Modal.confirm({
          title: '提示',
          content: '确认删除选中的必换件？',
          onOk: () => {
            // 批量删除必换件
            tableLoading.value = true;
            workCardApi
              .batchDeleteProducts({
                workCardId: workCardId.value,
                productIds,
              })
              .then(() => {
                message.success('删除成功');
                // 重新加载必换件列表
                loadProductList();
                // 清空选中
                selectedRowKeys.value = [];
              })
              .finally(() => {
                tableLoading.value = false;
              });
          },
        });
      };

      // 初始化
      onMounted(() => {
        // 初始化数据
        loadData();
        // 初始化航材列表
        searchProducts();
      });

      // 航材选择器引用
      const productSelector = ref(null);

      // 保存数量
      const handleSaveQuantity = () => {
        if (!hasQuantityChanged.value) {
          return;
        }
        
        // 准备请求数据
        const products = productList.value.map(item => ({
          id: item.id,
          quantity: item.quantity
        }));
        
        // 调用批量修改数量接口
        tableLoading.value = true;
        workCardApi
          .batchUpdateProductQuantity({
            workCardId: workCardId.value,
            products
          })
          .then(() => {
            message.success('数量保存成功');
            // 更新原始数量数据
            saveOriginalQuantities();
          })
          .catch(() => {
            message.error('数量保存失败');
          })
          .finally(() => {
            tableLoading.value = false;
          });
      };
      
      const rowKey = (record) => record._key || record.id || record.productId || record.code;

      return {
        workCardInfo,
        productList,
        vxColumns,
        selectedRowKeys,
        tableLoading,
        selectedProductIds,
        productSelector,
        hasQuantityChanged,
        rowKey,
        gridRef,
        onGridSelectChange,
        goBack,
        handleAddProducts,
        handleProductSelected,
        handleBatchDelete,
        handleQuantityChange,
        handleSaveQuantity,
      };
    },
  });
</script>

<style lang="less" scoped>
  .card-info {
    margin-bottom: 16px;
  }

  .table-operator {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .product-card {
    margin-bottom: 16px;
  }

  :deep(.ant-table-body) {
    overflow-y: auto !important;
    max-height: 100% !important;
  }

  .table-summary {
    color: rgba(0, 0, 0, 0.45);
    white-space: nowrap;
  }
</style>
