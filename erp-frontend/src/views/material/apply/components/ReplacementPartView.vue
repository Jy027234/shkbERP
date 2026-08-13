  <template>
  <a-modal
    :open="visible"
    title="必换件单管理"
    :width="900"
    @cancel="handleCancel"
  >
    <template #footer>
      <div class="footer-actions">
        <div class="footer-actions-left">
          <a-button v-if="false" @click="handleExport">导出</a-button>
        </div>
        <div class="footer-actions-right">
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" :loading="saveLoading" @click="handleSave" style="margin-left: 8px">
            保存
          </a-button>
        </div>
      </div>
    </template>
    <div class="replacement-part-container">
      <!-- 基础信息区域 -->
      <div class="basic-info-container" v-if="record">
        <div class="basic-info-title">基础信息</div>
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="合同编号">
            {{ record.contractCode }}
          </a-descriptions-item>
          <a-descriptions-item label="必换件单号">
            {{ record.replacementPartCode || '无' }}
          </a-descriptions-item>
          <a-descriptions-item label="申请时间">
            {{ record.createTime }}
          </a-descriptions-item>
          <a-descriptions-item label="申请编号">
            {{ record.applyCode }}
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
              :max="999"
              @change="value => handleQuantityChange(record, value)"
            />
          </template>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { getTaskReplacementParts, saveTaskReplacementPartsQuantity, exportTaskReplacementParts } from '@/api/maintenance/contract-task';
import { downloadByData } from '@/utils/file/download';

export default defineComponent({
  name: 'ReplacementPartView',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      loading: false,
      saveLoading: false,
      replacementPartList: [],
      columns: [
        {
          title: '工卡号',
          dataIndex: 'workCardNumber',
          key: 'workCardNumber',
          width: 100
        },
        {
          title: '工卡名称',
          dataIndex: 'workCardName',
          key: 'workCardName',
          width: 150
        },
        {
          title: '航材名称',
          dataIndex: 'productName',
          key: 'productName',
          width: 150
        },
        {
          title: '维修类型',
          dataIndex: 'repairType',
          key: 'repairType',
          width: 100
        },
        {
          title: '件号',
          dataIndex: 'productCode',
          key: 'productCode',
          width: 120
        },
        {
          title: '机型',
          dataIndex: 'machineTypeName',
          key: 'machineTypeName',
          width: 150
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          key: 'quantity',
          width: 100
        }
      ]
    };
  },
  watch: {
    visible(val) {
      if (val && this.record && this.record.taskId) {
        this.loadReplacementParts();
      }
    }
  },
  methods: {
    // 加载必换件列表
    loadReplacementParts() {
      if (!this.record || !this.record.taskId) {
        return;
      }
      
      this.loading = true;
      
      // 调用API获取任务必换件列表
      getTaskReplacementParts(this.record.taskId)
        .then(data => {
          // 转换API返回的数据格式为组件需要的格式
          this.replacementPartList = data.map(item => ({
            id: item.id,
            workCardId: item.workCardId,
            workCardNumber: item.workCardCode,
            workCardName: item.workCardName,
            repairType: item.repairTypeName,
            partNumber: item.partNumber,
            productId: item.productId,
            productCode: item.productCode,
            productName: item.productName,
            machineTypeName: item.machineTypeName,
            quantity: item.quantity || 1 // 使用后端返回的数量，如果没有则默认为1
          }));
        })
        .catch(error => {
          this.$message.error('获取必换件列表失败：' + error.message);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 处理数量变更
    handleQuantityChange(record, value) {
      // 更新数量
      record.quantity = value;
    },
    
    // 处理取消
    handleCancel() {
      this.$emit('update:visible', false);
    },
    
    // 保存数量修改
    handleSave() {
      if (!this.record || !this.record.taskId) {
        this.$message.warning('无法获取申请ID，保存失败');
        return;
      }
      
      this.saveLoading = true;
      
      // 构建保存数据
      const saveData = {
        taskId: this.record.taskId,
        products: this.replacementPartList.map(item => ({
          workCardId: item.workCardId,
          productId: item.productId,
          quantity: item.quantity
        }))
      };
      
      // 调用保存API
      saveTaskReplacementPartsQuantity(saveData)
        .then(() => {
          this.$message.success('必换件单保存成功');
          this.$emit('confirm', {
            record: this.record,
            updatedParts: this.replacementPartList
          });
          this.$emit('update:visible', false);
        })
        .catch(error => {
          // this.$message.error('保存失败：' + (error.message || '未知错误'));
        })
        .finally(() => {
          this.saveLoading = false;
        });
    },

    // 导出必换件单
    handleExport() {
      if (!this.record || !this.record.taskId) {
        this.$message.warning('无法获取任务ID，导出失败');
        return;
      }

      exportTaskReplacementParts(this.record.taskId)
        .then((res) => {
          const blob = res?.data || res;
          if (!blob) {
            this.$message.error('导出失败：未获取到文件数据');
            return;
          }

          const fileName = `${this.record.contractCode || '必换件单'}-必换件清单.xlsx`;
          downloadByData(blob, fileName);
        })
        .catch((error) => {
          this.$message.error('导出必换件单失败：' + (error?.message || '未知错误'));
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

.basic-info-title {
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
}

.footer-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.footer-actions-right {
  display: flex;
  align-items: center;
}
</style>
