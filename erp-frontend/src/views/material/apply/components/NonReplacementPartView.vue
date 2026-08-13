<template>
  <a-modal
    :open="visible"
    title="非必换件申请单管理"
    :width="900"
    @cancel="handleCancel"
  >
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" :loading="saveLoading" @click="handleSave" style="margin-left: 8px">
        保存
      </a-button>
    </template>
    <div class="non-replacement-part-container">
      <!-- 基础信息区域 -->
      <div class="basic-info-container" v-if="record">
        <a-descriptions title="基础信息" bordered :column="2">
          <a-descriptions-item label="合同编号">
            {{ record.contractCode }}
          </a-descriptions-item>
          <a-descriptions-item label="非必换件单号">
            {{ record.nonReplacementPartCode || '无' }}
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
      
      <!-- 非必换件申请列表 -->
      <a-table
        :dataSource="nonReplacementPartList"
        :columns="columns"
        :rowKey="record => record.id"
        :pagination="{ pageSize: 10 }"
        :loading="loading"
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
          <template v-else-if="column.key === 'attachments'">
            <div>
              <div v-if="record.fileList && record.fileList.length > 0">
                <div v-for="file in record.fileList" :key="file.uid" class="file-link">
                  <a :href="file.url" target="_blank" rel="noopener noreferrer">
                    <paper-clip-outlined /> {{ file.name }}
                  </a>
                </div>
              </div>
              <span v-else>无附件</span>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { PaperClipOutlined } from '@ant-design/icons-vue';
import { getTaskNonPartProducts, batchUpdateTaskNonPartProductQuantity } from '/@/api/maintenance/contract-task';

export default defineComponent({
  name: 'NonReplacementPartView',
  components: {
    PaperClipOutlined
  },
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
      nonReplacementPartList: [],
      originalData: [], // 保存原始数据，用于比较变更
      columns: [
        { title: '航材名称', dataIndex: 'productName', key: 'productName', width: 150 },
        { title: '机型', dataIndex: 'machineTypeName', key: 'machineTypeName', width: 100 },
        { title: '件号', dataIndex: 'productCode', key: 'productCode', width: 120 },
        { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 100 },
        { title: '说明', dataIndex: 'reason', key: 'reason', width: 120 },
        { title: '附件', dataIndex: 'attachments', key: 'attachments', width: 200 }
      ],
      hasChanges: false // 是否有修改
    };
  },
  watch: {
    visible(val) {
      if (val && this.record) {
        this.fetchNonReplacementPartData();
      }
    }
  },
  methods: {
    handleCancel() {
      // 如果有未保存的修改，提示用户
      if (this.hasChanges) {
        this.$confirm({
          title: '提示',
          content: '您有未保存的修改，确定要关闭吗？',
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            this.$emit('update:visible', false);
          }
        });
      } else {
        this.$emit('update:visible', false);
      }
    },
    
    // 数量变更处理
    handleQuantityChange(record, value) {
      record.quantity = value;
      record.quantityChanged = true;
      record.originalQuantity = record.originalQuantity || record.quantity;
      this.hasChanges = true;
      
      // 检查是否所有记录都恢复到原始状态
      const allUnchanged = this.nonReplacementPartList.every(item => {
        const original = this.originalData.find(orig => orig.id === item.id);
        return original && original.quantity === item.quantity;
      });
      
      if (allUnchanged) {
        this.hasChanges = false;
      }
    },
    
    // 保存修改
    async handleSave() {
      if (!this.hasChanges) {
        this.$message.info('没有需要保存的修改');
        return;
      }
      
      this.saveLoading = true;
      
      try {
        // 获取所有数量已变更的记录
        const changedRecords = this.nonReplacementPartList.filter(part => part.quantityChanged);
        
        if (changedRecords.length > 0) {
          // 构建批量更新请求数据
          const updateData = {
            taskId: this.record.taskId,
            records: changedRecords.map(part => ({
              id: part.id,
              quantity: part.quantity,
              reason: part.reason
            }))
          };
          
          // 调用批量更新API
          await batchUpdateTaskNonPartProductQuantity(updateData);
          
          // 更新成功，重置所有记录的状态
          changedRecords.forEach(part => {
            part.quantityChanged = false;
            part.originalQuantity = part.quantity;
          });
          
          // 更新原始数据，以便下次比较
          this.originalData = JSON.parse(JSON.stringify(this.nonReplacementPartList));
          this.hasChanges = false;
          this.$message.success('保存成功');
          
          // 通知父组件数据已更新
          this.$emit('confirm', {
            record: this.record,
            updatedParts: this.nonReplacementPartList
          });
        }
      } catch (error) {
        console.error('保存非必换件数量失败:', error);
        this.$message.error('保存失败: ' + (error.message || '未知错误'));
        
        // 恢复原始数量
        this.nonReplacementPartList.forEach(part => {
          if (part.quantityChanged) {
            part.quantity = part.originalQuantity || part.quantity;
            part.quantityChanged = false;
          }
        });
      } finally {
        this.saveLoading = false;
      }
    },
    
    // 打开附件（同页跳转，避免新标签被拦截/限制）
    openAttachment(url) {
      if (!url) return;
      // 规范化URL，确保相对路径可用
      let targetUrl = url;
      try {
        if (/^https?:\/\//i.test(url)) {
          targetUrl = url;
        } else if (/^\/\//.test(url)) {
          targetUrl = window.location.protocol + url;
        } else if (/^\//.test(url)) {
          targetUrl = window.location.origin + url;
        } else {
          targetUrl = window.location.origin + '/' + url;
        }
      } catch (_) {}

      // 直接同页跳转，最稳妥
      try {
        window.location.assign(targetUrl);
      } catch (e) {
        window.location.href = targetUrl;
      }
    },

    // 获取非必换件数据
    async fetchNonReplacementPartData() {
      this.loading = true;
      
      try {
        // 确保有任务ID
        if (this.record && this.record.taskId) {
          console.log('正在获取非必换件数据，任务ID:', this.record.taskId);
          
          // 调用API获取非必换件列表
          const nonPartProducts = await getTaskNonPartProducts(this.record.taskId);
          
          // 处理返回的数据，转换为组件所需格式
          this.nonReplacementPartList = nonPartProducts.map(item => ({
            id: item.id,
            productId: item.productId,
            productName: item.productName,
            productCode: item.productCode,
            machineTypeName: item.machineTypeName || '',
            partNumber: item.partNumber || '',
            quantity: item.quantity,
            reason: item.reason || '',
            createTime: item.createTime,
            // 处理附件列表
            fileList: item.files ? item.files.map(file => ({
              uid: file.id,
              name: file.fileName,
              status: 'done',
              url: file.url
            })) : [],
            // 处理多个附件的情况，用于显示
            attachments: item.files ? item.files.map(file => file.fileName).join(', ') : ''
          }));
          
          console.log('获取到非必换件数据:', this.nonReplacementPartList.length, '条记录');
        } else {
          console.warn('没有任务ID，无法获取非必换件列表');
          this.nonReplacementPartList = [];
        }
        
        // 保存原始数据副本，用于比较变更
        this.originalData = JSON.parse(JSON.stringify(this.nonReplacementPartList));
        this.hasChanges = false;
      } catch (error) {
        console.error('获取非必换件列表失败:', error);
        this.$message.error('获取非必换件列表失败: ' + (error.message || '未知错误'));
        this.nonReplacementPartList = [];
      } finally {
        this.loading = false;
      }
    }
  }
});
</script>

<style scoped>
.non-replacement-part-container {
  padding: 0 10px;
}

.file-link {
  display: block;
  margin-bottom: 4px;
  pointer-events: auto;
  cursor: pointer;
  position: relative;
  z-index: 2;
}
</style>
