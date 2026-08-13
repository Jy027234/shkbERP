<template>
  <a-modal
    :open="visible"
    title="必换件单管理"
    :width="1100"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" :loading="saveLoading" @click="handleSave" style="margin-left: 8px">
        保存
      </a-button>
    </template>
    <div class="replacement-part-container">
      <!-- 基础信息区域 -->
      <div class="basic-info-container" v-if="selectedTask">
        <a-descriptions title="基础信息" bordered :column="2">
          <a-descriptions-item label="合同编号">
            {{ selectedTask.contractCode }}
          </a-descriptions-item>
          <a-descriptions-item label="必换件单号">
            {{ selectedTask.replacementPartNumber || '无' }}
          </a-descriptions-item>
          <a-descriptions-item label="机型">
            {{ selectedTask.machineTypeName }}
          </a-descriptions-item>
          <a-descriptions-item label="件号">
            {{ selectedTask.partNumberCode }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
      <a-divider />

      <!-- 必换件单列表 -->
      <a-table
        :dataSource="replacementPartList"
        :columns="columns"
        :rowKey="record => record.id"
        :pagination="false"
        :loading="loading"
        :scroll="{ y: 400 }"
        bordered
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'quantity'">
            <a-input-number
              v-model:value="record.quantity"
              :min="1"
              :max="9999"
            />
          </template>
          <template v-else-if="column.key === 'originProduct'">
            <template v-if="record.originProductId">
              <div>{{ record.originProductName }}</div>
              <div class="product-code-text">{{ record.originProductCode }}</div>
            </template>
            <span v-else class="text-muted">未替换</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                type="link"
                size="small"
                @click="openProduct(record)"
              >
                替换
              </a-button>
              <a-button
                v-if="record.originProductId"
                type="link"
                size="small"
                @click="handleRestore(record)"
              >
                还原
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 航材选择器 -->
      <product-selector
        ref="productSelectorRef"
        v-show="false"
        v-model:value="selectedProductIds"
        :request-params="{ available: true }"
        multiple
        @input-row="handleProductSelected"
      />
    </div>
  </a-modal>
</template>

<script>
import { defineComponent, nextTick } from 'vue';
import { Modal } from 'ant-design-vue';
import {
  getTaskReplacementParts,
  saveTaskReplacementPartsQuantity,
  replaceReplacementPart,
  restoreReplacementPart
} from '/@/api/maintenance/contract-task';
import ProductSelector from '/@/components/Selector/src/ProductSelector.vue';

export default defineComponent({
  name: 'ReplacementPartManagement',
  components: { ProductSelector },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    tasks: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      saveLoading: false,
      actionLoading: false,
      selectedTask: null,
      currentReplaceRecord: null,
      selectedProductIds: [],
      // 必换件列表
      replacementPartList: [],
      // 表格列定义
      columns: [
        {
          title: '工卡号',
          dataIndex: 'workCardNumber',
          key: 'workCardNumber',
          width: 120
        },
        {
          title: '工卡名称',
          dataIndex: 'workCardName',
          key: 'workCardName',
          width: 160
        },
        {
          title: '航材名称',
          dataIndex: 'productName',
          key: 'productName',
          width: 180
        },
        {
          title: '件号',
          dataIndex: 'partNumber',
          key: 'partNumber',
          width: 120
        },
        {
          title: '维修类型',
          dataIndex: 'repairType',
          key: 'repairType',
          width: 100
        },
        {
          title: '原始必换件',
          key: 'originProduct',
          width: 180
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          key: 'quantity',
          width: 120
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          fixed: 'right'
        }
      ]
    };
  },
  watch: {
    visible(val) {
      if (val && this.tasks && this.tasks.length > 0) {
        this.selectedTask = this.tasks[0];
        this.loadReplacementParts();
      }
    },
    tasks: {
      handler(val) {
        if (val && val.length > 0) {
          this.selectedTask = val[0];
          if (this.visible) {
            this.loadReplacementParts();
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    // 加载必换件列表
    loadReplacementParts() {
      if (!this.selectedTask) {
        return;
      }

      this.loading = true;

      getTaskReplacementParts(this.selectedTask.id)
        .then((data) => {
          this.replacementPartList = data.map((item) => ({
            id: item.id,
            workCardId: item.workCardId,
            workCardNumber: item.workCardCode,
            workCardName: item.workCardName,
            repairType: item.repairTypeName,
            partNumber: item.partNumberCode,
            productId: item.productId,
            productCode: item.productCode,
            productName: item.productName,
            originProductId: item.originProductId,
            originProductCode: item.originProductCode,
            originProductName: item.originProductName,
            quantity: item.quantity || 1
          }));
        })
        .catch((error) => {
          this.$message.error('获取必换件列表失败：' + error.message);
          this.replacementPartList = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 打开航材选择器
    openProduct(record) {
      this.currentReplaceRecord = record;
      this.selectedProductIds = [];

      nextTick(() => {
        const selector = this.$refs.productSelectorRef;
        if (selector && selector.$refs && selector.$refs.selector) {
          selector.$refs.selector.dialogVisible = true;
        }
      });
    },

    // 航材选择回调
    handleProductSelected(selectedRows) {
      if (!selectedRows || selectedRows.length === 0) {
        return;
      }

      const newProduct = selectedRows[0];
      const record = this.currentReplaceRecord;
      if (!record) {
        return;
      }

      this.actionLoading = true;
      replaceReplacementPart(record.id, newProduct.id)
        .then(() => {
          this.$message.success('替换成功');
          this.loadReplacementParts();
        })
        .finally(() => {
          this.actionLoading = false;
          this.currentReplaceRecord = null;
        });
    },

    // 还原必换件
    handleRestore(record) {
      Modal.confirm({
        title: '提示',
        content: `确认将【${record.productName}】还原为原始必换件【${record.originProductName || '原始必换件'}】？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.actionLoading = true;
          return restoreReplacementPart(record.id)
            .then(() => {
              this.$message.success('还原成功');
              this.loadReplacementParts();
            })
            .finally(() => {
              this.actionLoading = false;
            });
        }
      });
    },

    // 处理取消
    handleCancel() {
      this.$emit('update:visible', false);
    },

    // 保存数量修改
    handleSave() {
      this.saveLoading = true;

      const saveData = {
        taskId: this.selectedTask.id,
        products: this.replacementPartList.map((item) => ({
          workCardId: item.workCardId,
          productId: item.productId,
          quantity: item.quantity
        }))
      };

      saveTaskReplacementPartsQuantity(saveData)
        .then(() => {
          this.$message.success('必换件单保存成功');
          this.$emit('confirm', this.replacementPartList);
          this.handleCancel();
        })
        .catch((error) => {
          console.error('保存必换件单失败:', error);
        })
        .finally(() => {
          this.saveLoading = false;
        });
    }
  }
});
</script>

<style scoped>
.replacement-part-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.basic-info-container {
  margin-bottom: 16px;
}

.product-code-text {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

.text-muted {
  color: rgba(0, 0, 0, 0.25);
}
</style>
