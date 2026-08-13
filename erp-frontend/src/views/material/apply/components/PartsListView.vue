<template>
  <a-modal
    v-model:open="modelOpen"
    title="换件清单"
    width="900px"
    :footer="null"
    :destroyOnClose="true"
    @afterVisibleChange="handleVisibleChange"
  >
    <div class="parts-list-view">
      <!-- 申请基本信息 -->
      <div class="info-section">
        <h3>申请基本信息</h3>
        <a-descriptions :column="3" bordered>
          <a-descriptions-item label="申请编号">{{ record.applyCode }}</a-descriptions-item>
          <a-descriptions-item label="合同编号">{{ record.contractCode }}</a-descriptions-item>
          <a-descriptions-item label="申请时间">{{ record.createTime }}</a-descriptions-item>
          <a-descriptions-item label="机型">{{ record.machineTypeName }}</a-descriptions-item>
          <a-descriptions-item label="件号">{{ record.partNumberName }}</a-descriptions-item>
          <a-descriptions-item label="审批状态">
            <a-tag v-if="record.approvalStatus === 1" color="success">{{ record.approvalStatusText }}</a-tag>
            <a-tag v-else-if="record.approvalStatus === 2" color="error">{{ record.approvalStatusText }}</a-tag>
            <a-tag v-else-if="record.approvalStatus === 0" color="processing">{{ record.approvalStatusText }}</a-tag>
            <span v-else>-</span>
          </a-descriptions-item>
        </a-descriptions>
      </div>
      
      <!-- 仓库选择 -->
      <div class="warehouse-section">
        <h3>仓库选择</h3>
        <div class="warehouse-selector">
          <a-form-item label="仓库" :labelCol="{ span: 4 }" :wrapperCol="{ span: 8 }">
            <store-center-selector v-model:value="selectedScId" @change="handleWarehouseChange" />
          </a-form-item>
        </div>
      </div>

      <!-- 换件清单列表 -->
      <div class="parts-list-section">
        <h3>换件清单列表</h3>
        <a-table
          :dataSource="partsList"
          :columns="columns"
          :pagination="false"
          :loading="loading"
          size="middle"
          :scroll="{ y: 300 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'isReplacement'">
              <a-tag :color="record.isReplacement ? 'blue' : 'orange'">
                {{ record.isReplacement ? '必换件' : '非必换件' }}
              </a-tag>
            </template>
            <template v-if="column.dataIndex === 'stockNum'">
              <span :class="{ 'stock-warning': record.stockNum < record.quantity }">
                {{ record.stockNum }}
              </span>
            </template>
          </template>
        </a-table>
      </div>

      <!-- 底部按钮 -->
      <div class="footer">
        <a-space>
          <a-button type="primary" :loading="printLoading" @click="handlePrint">
            <template #icon><PrinterOutlined /></template>
            打印
          </a-button>
          <a-button @click="handleCancel">关闭</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { getTaskPartList } from '@/api/material/apply';
import { exportReplacementList } from '@/api/material/order';
import { message } from 'ant-design-vue';
import StoreCenterSelector from '@/components/Selector/src/StoreCenterSelector.vue';
import { PrinterOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: 'PartsListView',
  components: {
    StoreCenterSelector,
    PrinterOutlined
  },
  props: {
    open: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:open', 'confirm'],
  computed: {
    modelOpen: {
      get() {
        return this.open;
      },
      set(value) {
        this.$emit('update:open', value);
        // 当窗口关闭时，清空数据以便于下次打开时重新加载
        if (!value) {
          this.partsList = [];
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      printLoading: false,
      partsList: [],
      selectedScId: null, // 选中的仓库ID
      storeCenters: [], // 仓库列表
      columns: [
        {
          title: '类型',
          dataIndex: 'isReplacement',
          width: 100
        },
        {
          title: '航材名称',
          dataIndex: 'productName',
          width: 200
        },
        {
          title: '件号',
          dataIndex: 'productCode',
          width: 120
        },
        {
          title: '机型',
          dataIndex: 'machineTypeName',
          width: 120
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          width: 80
        },
        {
          title: '当前库存',
          dataIndex: 'stockNum',
          width: 100
        }
      ]
    };
  },
  watch: {
    // 监听 open 属性变化
    open(val) {
      if (val && this.record && this.record.id) {
        console.log('换件清单窗口打开，重新加载数据');
        // 每次打开窗口时重置数据并重新获取
        this.partsList = [];
        this.loadData();
      }
    },
    // 当记录变化时重新加载数据
    'record.id': {
      handler(val) {
        if (val && this.open) {
          console.log('记录ID变化，重新加载数据');
          this.partsList = [];
          this.loadData();
        }
      },
      immediate: false
    },
    selectedScId(val) {
      if (val && this.record && this.record.id) {
        this.fetchPartsList();
      }
    }
  },
  methods: {
    // 集中处理数据加载逻辑
    loadData() {
      if (!this.record || !this.record.id) {
        return;
      }
      
      console.log('加载换件清单数据');
      // 如果已经有仓库ID，直接获取换件清单数据
      if (this.selectedScId) {
        this.fetchPartsList();
      } else {
        this.fetchStoreCenters();
      }
    },
    
    // 模态窗口可见性变化时触发
    handleVisibleChange(visible) {
      if (visible && this.record && this.record.id) {
        console.log('换件清单窗口可见性变化，重新加载数据');
        this.partsList = [];
        this.loadData();
      }
    },
    
    // 获取仓库列表
    fetchStoreCenters() {
      // 从 API 获取仓库列表并选择第一个仓库
      import('@/api/base-data/store-center').then(api => {
        api.selector({ pageIndex: 1, pageSize: 20 }).then(res => {
          if (res && res.datas && res.datas.length > 0) {
            // 选择第一个仓库
            this.selectedScId = res.datas[0].id;
            // 数据会通过 watch selectedScId 自动加载
          }
        }).catch(err => {
          message.error(err.message || '获取仓库列表失败');
        });
      });
    },
    
    // 仓库变更处理
    handleWarehouseChange(value) {
      this.selectedScId = value;
      if (value) {
        this.fetchPartsList();
      }
    },
    
    // 获取换件清单
    fetchPartsList() {
      if (!this.record || !this.record.id || !this.selectedScId) {
        return;
      }

      this.loading = true;
      // 调用后端接口获取任务换件清单，传入仓库ID
      getTaskPartList(this.record.taskId, this.selectedScId)
        .then(res => {
          // 处理后端返回的数据
          if (res && res.mandatoryParts && res.nonMandatoryParts) {
            // 合并必换件和非必换件列表
            const mandatoryParts = res.mandatoryParts.map(item => ({
              ...item,
              isReplacement: true // 必换件标识
            }));
            
            const nonMandatoryParts = res.nonMandatoryParts.map(item => ({
              ...item,
              isReplacement: false // 非必换件标识
            }));
            
            // 合并两个列表
            this.partsList = [...mandatoryParts, ...nonMandatoryParts];
            console.log('换件清单数据:', this.partsList);
          } else {
            this.partsList = [];
            message.warning('未获取到换件清单数据');
          }
        })
        .catch(err => {
          message.error(err.message || '获取换件清单失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 取消
    handleCancel() {
      this.$emit('update:open', false);
    },

    // 打印换件清单
    handlePrint() {
      if (!this.record || !this.record.id) {
        message.warning('没有可打印的数据');
        return;
      }

      this.printLoading = true;
      exportReplacementList(this.record.id)
        .then(res => {
          if (res) {
            const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            const fileName = `换件清单_${this.record.applyCode || ''}_${new Date().toLocaleDateString().replace(/\//g, '-')}.docx`;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            message.success('打印文件下载成功');
          }
        })
        .catch(err => {
          console.error('打印失败:', err);
          message.error(err.message || '打印失败，请重试');
        })
        .finally(() => {
          this.printLoading = false;
        });
    }
  }
});
</script>

<style scoped>
.parts-list-view {
  padding: 0 10px;
}

.info-section, .parts-list-section, .warehouse-section {
  margin-bottom: 20px;
}

.footer {
  text-align: right;
  margin-top: 20px;
}

.stock-warning {
  color: #ff4d4f;
  font-weight: bold;
}
</style>
