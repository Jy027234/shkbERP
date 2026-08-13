<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="70%"
    title="必换件管理"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
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
        </a-descriptions>
      </a-card>

      <!-- 必换件管理 -->
      <a-tabs>
        <!-- 必换件列表 -->
        <a-tab-pane key="productList" tab="必换件列表">
          <div class="table-operator">
            <a-space>
              <a-button
                v-permission="['work-card']"
                type="primary"
                @click="activeTab = 'productSelector'"
              >
                <template #icon>
                  <PlusOutlined />
                </template>
                添加必换件
              </a-button>
              <a-button
                v-permission="['work-card']"
                :disabled="selectedRowKeys.length === 0"
                type="danger"
                @click="handleBatchDelete"
              >
                <template #icon>
                  <DeleteOutlined />
                </template>
                批量删除
              </a-button>
            </a-space>
          </div>

          <a-table
            :columns="columns"
            :data-source="productList"
            :loading="tableLoading"
            :pagination="false"
            :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'productCode'">
                {{ record.productCode }}
              </template>
              <template v-else-if="column.dataIndex === 'productName'">
                {{ record.productName }}
              </template>
              <template v-else-if="column.dataIndex === 'productSpec'">
                {{ record.productSpec }}
              </template>
              <template v-else-if="column.dataIndex === 'productUnit'">
                {{ record.productUnit }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        
        <!-- 航材选择器 -->
        <a-tab-pane key="productSelector" tab="选择航材" :force-render="true">
          <!-- 航材搜索条件 -->
          <a-card :bordered="false" class="search-card">
            <a-form layout="inline">
              <a-form-item label="件号">
                <a-input v-model:value="searchParams.code" placeholder="请输入件号" />
              </a-form-item>
              <a-form-item label="航材名称">
                <a-input v-model:value="searchParams.name" placeholder="请输入航材名称" />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="searchProducts">
                  <template #icon><SearchOutlined /></template>
                  查询
                </a-button>
                <a-button style="margin-left: 8px" @click="resetSearch">
                  重置
                </a-button>
              </a-form-item>
            </a-form>
          </a-card>
          
          <!-- 航材列表 -->
          <a-card :bordered="false" class="product-card">
            <a-table
              :columns="productColumns"
              :data-source="searchProductList"
              :loading="searchLoading"
              :pagination="{ showSizeChanger: true, showQuickJumper: true }"
              :row-selection="{ selectedRowKeys: selectedProductKeys, onChange: onProductSelectChange }"
              row-key="id"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'code'">
                  {{ record.code }}
                </template>
                <template v-else-if="column.dataIndex === 'name'">
                  {{ record.name }}
                </template>
                <template v-else-if="column.dataIndex === 'spec'">
                  {{ record.spec }}
                </template>
                <template v-else-if="column.dataIndex === 'unit'">
                  {{ record.unit }}
                </template>
              </template>
            </a-table>
            
            <div class="table-operator" style="margin-top: 16px; text-align: right;">
              <a-button @click="activeTab = 'productList'">取消</a-button>
              <a-button 
                type="primary" 
                :disabled="selectedProductKeys.length === 0" 
                style="margin-left: 8px;"
                @click="handleConfirmAddProducts"
              >
                确认添加
              </a-button>
            </div>
          </a-card>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 已集成到标签页中，不再需要单独的航材选择器组件 -->
  </a-modal>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons-vue';
import { workCardApi } from '@/api/work-card/index';
import * as productApi from '@/api/base-data/product/info';
import ProductSelector from '@/components/Selector/src/ProductSelector.vue';

export default defineComponent({
  name: 'WorkCardProductManage',
  components: {
    ProductSelector,
    PlusOutlined,
    DeleteOutlined,
    SearchOutlined
  },
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    // 是否可见
    const visible = ref(false);
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
    // 航材选择器引用
    const productSelector = ref(null);
    
    // 当前激活的标签页
    const activeTab = ref('productList');
    
    // 航材搜索参数
    const searchParams = ref({
      code: '',
      name: '',
      available: true
    });
    
    // 航材搜索加载状态
    const searchLoading = ref(false);
    
    // 航材搜索结果列表
    const searchProductList = ref([]);
    
    // 选中的航材行
    const selectedProductKeys = ref([]);

    // 必换件表格列定义
    const columns = [
      {
        title: '航材编号',
        dataIndex: 'productCode',
        width: 120,
      },
      {
        title: '航材名称',
        dataIndex: 'productName',
        width: 250,
      },
      {
        title: '规格',
        dataIndex: 'productSpec',
        width: 120,
      },
      {
        title: '单位',
        dataIndex: 'productUnit',
        width: 80,
      }
    ];
    
    // 航材选择表格列定义
    const productColumns = [
      {
        title: '件号',
        dataIndex: 'code',
        width: 160,
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
      }
    ];

    // 打开对话框
    const openDialog = () => {
      visible.value = true;
      activeTab.value = 'productList'; // 默认显示必换件列表标签页
      loadData();
      initProductList(); // 初始化航材列表
    };

    // 关闭对话框
    const closeDialog = () => {
      visible.value = false;
      selectedRowKeys.value = [];
    };

    // 加载数据
    const loadData = () => {
      loading.value = true;
      
      // 加载工卡信息
      workCardApi.get(props.id)
        .then(data => {
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
      workCardApi.getProducts(props.id)
        .then(data => {
          productList.value = data || [];
        })
        .finally(() => {
          loading.value = false;
          tableLoading.value = false;
        });
    };

    // 选择行变更
    const onSelectChange = (keys) => {
      selectedRowKeys.value = keys;
    };

    // 搜索航材
    const searchProducts = () => {
      searchLoading.value = true;
      productApi.selector({
        code: searchParams.value.code,
        name: searchParams.value.name,
        available: searchParams.value.available
      }).then(res => {
        searchProductList.value = res.datas || [];
      }).finally(() => {
        searchLoading.value = false;
      });
    };
    
    // 重置搜索
    const resetSearch = () => {
      searchParams.value = {
        code: '',
        name: '',
        available: true
      };
      searchProducts();
    };
    
    // 航材选择变更
    const onProductSelectChange = (keys) => {
      selectedProductKeys.value = keys;
    };
    
    // 确认添加航材
    const handleConfirmAddProducts = () => {
      if (selectedProductKeys.value.length === 0) {
        return;
      }
      
      // 提取选中的航材ID
      const productIds = selectedProductKeys.value;
      
      // 批量添加必换件
      tableLoading.value = true;
      workCardApi.batchAddProducts({
        workCardId: props.id,
        productIds
      }).then(() => {
        message.success('添加成功');
        // 重新加载必换件列表
        loadProductList();
        // 切换回必换件列表标签页
        activeTab.value = 'productList';
        // 清空选中
        selectedProductKeys.value = [];
      }).finally(() => {
        tableLoading.value = false;
      });
    };

    // 页面初始化时加载航材列表
    const initProductList = () => {
      // 初始化时加载航材列表
      searchProducts();
    };

    // 批量删除
    const handleBatchDelete = () => {
      if (selectedRowKeys.value.length === 0) {
        return;
      }

      // 获取选中的航材ID
      const productIds = productList.value
        .filter(item => selectedRowKeys.value.includes(item.id))
        .map(item => item.productId);

      if (productIds.length === 0) {
        return;
      }

      // 确认删除
      message.confirm({
        title: '提示',
        content: '确认删除选中的必换件？',
        onOk: () => {
          // 批量删除必换件
          tableLoading.value = true;
          workCardApi.batchDeleteProducts({
            workCardId: props.id,
            productIds
          }).then(() => {
            message.success('删除成功');
            // 重新加载必换件列表
            loadProductList();
            // 清空选中
            selectedRowKeys.value = [];
          }).finally(() => {
            tableLoading.value = false;
          });
        }
      });
    };

    return {
      visible,
      loading,
      tableLoading,
      workCardInfo,
      productList,
      columns,
      selectedRowKeys,
      activeTab,
      searchParams,
      searchLoading,
      searchProductList,
      selectedProductKeys,
      productColumns,
      openDialog,
      closeDialog,
      onSelectChange,
      searchProducts,
      resetSearch,
      onProductSelectChange,
      handleConfirmAddProducts,
      handleBatchDelete
    };
  }
});
</script>

<style lang="less" scoped>
.card-info {
  margin-bottom: 16px;
}

.table-operator {
  margin-bottom: 16px;
}
</style>
