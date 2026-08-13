<template>
  <a-modal
    :open="visible"
    title="非必换件申请单"
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
      
      <!-- 申请表单区域 -->
      <div class="application-form">
        <div class="form-title">非必换件申请单</div>
        <div class="form-content">
          <div class="form-row">
            <div class="form-label">选择航材:</div>
            <div class="form-selector">
              <product-selector 
                @select="handleProductSelect" 
                @input-row="handleProductInput"
                :showSum="false" 
                placeholder="请选择航材" 
                ref="productSelector" 
              />
              <div v-if="form.productId" class="selected-product-info">
                <div>已选航材: {{ form.productName }}</div>
                <div v-if="form.productCode">件号: {{ form.productCode }}</div>
                <div v-if="form.machineTypeName">机型: {{ form.machineTypeName }}</div>
              </div>
            </div>
            <div class="form-label" style="margin-left: 20px;">数量:</div>
            <a-input-number v-model:value="form.quantity" :min="1" placeholder="请输入" class="form-input-number" />
          </div>
          <div class="form-row">
            <div class="form-label">原因说明:</div>
            <a-textarea v-model:value="form.reason" placeholder="请输入" :rows="4" class="form-textarea" />
          </div>
          <div class="form-row">
            <div class="form-label">附件上传:</div>
            <a-upload
              :file-list="fileList"
              :before-upload="beforeUpload"
              @change="handleFileChange"
              @remove="handleRemove"
            >
              <a-button>
                <upload-outlined /> 附件上传
              </a-button>
            </a-upload>
            <a-button 
              type="primary" 
              @click="handleAddPart" 
              style="margin-left: auto;"
              :disabled="!isFormValid"
            >
              <plus-outlined />
            </a-button>
          </div>
        </div>
      </div>
      <a-divider />
      
      <!-- 非必换件申请单列表 -->
      <div class="application-list">
        <div class="list-title">非必换件申请单: {{ selectedTask?.otherReplacementPartNumber || (selectedTask?.contractCode ? `${selectedTask.contractCode}-FBH` : '自动生成') }}</div>
        <div class="list-header">
          <div class="header-item">合同号</div>
          <div class="header-item">{{ selectedTask?.contractCode || '无' }}</div>
          <div class="header-item">申请时间</div>
          <div class="header-item">{{ currentTime }}</div>
        </div>
        
        <a-table
          :dataSource="nonReplacementPartList"
          :columns="columns"
          :pagination="false"
          :loading="loading"
          rowKey="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'quantity'">
              <a-input-number 
                v-model:value="record.quantity" 
                :min="1" 
                size="small"
                @change="(value) => handleQuantityChange(record, value)"
              />
            </template>
            <template v-else-if="column.key === 'attachments'">
              <div>
                <template v-if="record.fileList && record.fileList.length > 0">
                  <div v-for="(file, index) in record.fileList" :key="file.uid" style="margin-bottom: 4px">
                    <a :href="file.url" target="_blank">
                      <paper-clip-outlined /> {{ file.name }}
                    </a>
                  </div>
                </template>
                <span v-else>无附件</span>
              </div>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-spin v-if="record.saving" size="small" />
                <a-button type="link" danger @click="handleDeletePart(record)">
                  <delete-outlined />
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
        
        <div class="list-footer">
          <div class="footer-left">申请人: {{ currentUserName }}</div>
          <div class="footer-right">
            <a-button>
              <printer-outlined /> 预览/打印
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { PlusOutlined, UploadOutlined, DeleteOutlined, PrinterOutlined, PaperClipOutlined, InfoCircleOutlined, CheckOutlined } from '@ant-design/icons-vue';
import ProductSelector from '/@/components/Selector/src/ProductSelector.vue';
import { saveTaskNonPartProduct, deleteTaskNonPartProduct, getTaskNonPartProducts, updateTaskNonPartProductQuantity, batchUpdateTaskNonPartProductQuantity } from '/@/api/maintenance/contract-task';
import { useUserStore } from '/@/store/modules/user';

export default defineComponent({
  name: 'NonReplacementPartManagement',
  components: {
    PlusOutlined,
    DeleteOutlined,
    UploadOutlined,
    PrinterOutlined,
    PaperClipOutlined,
    InfoCircleOutlined,
    CheckOutlined,
    ProductSelector
  },
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
      selectedTask: null,
      fileList: [],

      // 表单数据
      form: {
        productId: null, // 改为null初始值，避免空字符串判断问题
        productName: '',
        productCode: '',
        machineTypeName: '',
        partNumberName: '',
        quantity: 1,
        reason: ''
      },
      // 调试用变量
      debugProductId: null,
      // 非必换件列表
      nonReplacementPartList: [],
      // 表格列定义
      columns: [
        {
          title: '航材名称',
          dataIndex: 'productName',
          key: 'productName',
          width: 150
        },
        {
          title: '机型',
          dataIndex: 'machineTypeName',
          key: 'machineTypeName',
          width: 100
        },
        {
          title: '件号',
          dataIndex: 'partNumber',
          key: 'partNumber',
          width: 120
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          key: 'quantity',
          width: 100
        },
        {
          title: '说明',
          dataIndex: 'reason',
          key: 'reason',
          width: 120
        },
        {
          title: '附件',
          dataIndex: 'attachments',
          key: 'attachments',
          width: 200
        },
        {
          title: '操作',
          key: 'action',
          width: 80
        }
      ]
    };
  },
  computed: {
    isFormValid() {
      // 打印调试信息，查看表单数据状态
      console.log('计算isFormValid:', this.form.productId, this.form.quantity);
      // 直接检查form.productId是否存在并且数量大于0
      return !!this.form.productId && this.form.quantity > 0;
    },
    currentTime() {
      const now = new Date();
      return now.toLocaleDateString() + ' ' + now.toLocaleTimeString();
    },
    currentUserName() {
      const userStore = useUserStore();
      return userStore.getUserInfo?.name || '未知用户';
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.initData();
      }
    },
    tasks: {
      handler(val) {
        if (val && val.length > 0) {
          this.selectedTask = val[0];
        }
      },
      immediate: true
    }
  },
  methods: {
    // 初始化数据
    initData() {
      // 确保选中了任务
      if (this.tasks && this.tasks.length > 0) {
        this.selectedTask = this.tasks[0];
        console.log('选中的任务:', this.selectedTask);
      } else {
        console.warn('没有选中任务');
      }
      this.resetForm();
      this.loadNonReplacementParts();
    },
    
    // 加载非必换件列表
    async loadNonReplacementParts() {
      this.loading = true;
      
      try {
        // 检查是否有选中的任务
        if (this.selectedTask && this.selectedTask.id) {
          console.log('正在获取非必换件数据，任务ID:', this.selectedTask.id);
          
          // 调用API获取非必换件列表
          const nonPartProducts = await getTaskNonPartProducts(this.selectedTask.id);
          
          // 处理返回的数据，转换为组件所需格式
          this.nonReplacementPartList = nonPartProducts.map(item => ({
            id: item.id,
            productId: item.productId,
            productName: item.productName,
            productCode: item.productCode,
            machineTypeName: item.machineTypeName || '',
            partNumber: item.productCode || '',
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
          console.warn('没有选中任务，无法获取非必换件列表');
          this.nonReplacementPartList = [];
        }
      } catch (error) {
        console.error('获取非必换件列表失败:', error);
        this.$message.error('获取非必换件列表失败: ' + (error.message || '未知错误'));
        this.nonReplacementPartList = [];
      } finally {
        this.loading = false;
      }
    },
    
    // 重置表单方法保留，其他模拟数据生成方法已删除
    
    // 重置表单
    resetForm() {
      this.form = {
        productId: '',
        productName: '',
        productCode: '',
        machineTypeName: '',
        partNumberName: '',
        quantity: 1,
        reason: ''
      };
      this.fileList = [];
    },
    
    // 添加非必换件
    async handleAddPart() {
      if (!this.isFormValid) {
        this.$message.warning('请选择航材并填写数量');
        return;
      }
      
      // 检查是否有任务ID
      if (!this.selectedTask || !this.selectedTask.id) {
        this.$message.warning('请先选择任务');
        return;
      }
      
      // 创建临时ID，用于UI展示
      const tempId = `nrp-new-${Date.now()}`;
      
      // 创建新的非必换件记录
      const newPart = {
        id: tempId,
        productId: this.form.productId,
        partNumber: this.form.productCode,
        productName: this.form.productName,
        productCode: this.form.productCode,
        machineTypeName: this.form.machineTypeName,
        quantity: this.form.quantity,
        reason: this.form.reason || '',
        // 保存文件列表的完整信息
        fileList: [...this.fileList],
        // 处理多个附件的情况，用于显示
        attachments: this.fileList.map(file => file.name).join(', '),
        taskId: this.selectedTask.id,
        createTime: new Date().toISOString(),
        saving: true // 标记为正在保存
      };
      
      // 添加到列表顶部
      this.nonReplacementPartList.unshift(newPart);
      
      try {
        // 准备实际文件对象列表
        const actualFiles = [];
        for (const fileItem of this.fileList) {
          if (fileItem.originFileObj) {
            actualFiles.push(fileItem.originFileObj);
          }
        }
        
        // 调用API保存非必换件记录
        const id = await saveTaskNonPartProduct(
          this.selectedTask.id,
          this.form.productId,
          this.form.quantity,
          this.form.reason || '',
          actualFiles
        );
        
        console.log('保存非必换件成功，返回ID:', id);
        
        // 更新列表中的记录，将临时ID替换为服务器返回的ID
        const index = this.nonReplacementPartList.findIndex(item => item.id === tempId);
        if (index !== -1) {
          this.nonReplacementPartList[index].id = id;
          this.nonReplacementPartList[index].saving = false;
        }
        
        // 重置表单和文件列表
        this.resetForm();
        this.$message.success('添加成功');
      } catch (error) {
        console.error('保存非必换件失败:', error);
        
        // 从列表中移除失败的记录
        this.nonReplacementPartList = this.nonReplacementPartList.filter(item => item.id !== tempId);
        
        // this.$message.error('保存失败: ' + (error.message || '未知错误'));
      }
    },
    
    // 删除非必换件
    async handleDeletePart(record) {
      try {
        // 如果是已保存到服务器的记录（ID不是以nrp-new-开头），则调用API删除
        if (!record.id.startsWith('nrp-new-')) {
          await deleteTaskNonPartProduct(record.id);
        }
        
        // 从列表中移除
        this.nonReplacementPartList = this.nonReplacementPartList.filter(item => item.id !== record.id);
        this.$message.success('删除成功');
      } catch (error) {
        this.$message.error('删除失败: ' + (error.message || '未知错误'));
      }
    },
    
    // 文件上传前处理
    beforeUpload(file) {
      // 限制文件大小为10MB
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        this.$message.error('文件大小不能超过10MB!');
        return false;
      }
      return false; // 阻止自动上传，由我们手动控制
    },
    
    // 文件变更处理
    handleFileChange(info) {
      // 保留所有文件，不再限制只保留最后一个
      this.fileList = [...info.fileList];
    },
    
    // 移除文件
    handleRemove(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },
    
    // 处理数量变更
    handleQuantityChange(record, value) {
      // 标记数量已变更，需要保存
      record.quantityChanged = true;
      record.originalQuantity = record.originalQuantity || record.quantity;
      record.quantity = value;
    },
    
    // 保存处理
    async handleSave() {
      this.saveLoading = true;
      
      try {
        // 如果没有非必换件记录，直接返回
        if (this.nonReplacementPartList.length === 0) {
          this.$message.info('没有非必换件记录需要保存');
          this.saveLoading = false;
          return;
        }
        
        const promises = [];
        
        // 1. 保存所有未保存的新记录
        const newPartPromises = this.nonReplacementPartList
          .filter(part => part.id.startsWith('nrp-new-')) // 只处理新添加的记录
          .map(async (part) => {
            try {
              // 获取实际文件对象
              const actualFiles = [];
              
              // 从fileList中提取实际的File对象
              for (const fileItem of part.fileList) {
                if (fileItem.originFileObj) {
                  // 如果有originFileObj，说明是新上传的文件
                  actualFiles.push(fileItem.originFileObj);
                }
              }
              
              console.log('准备上传文件:', actualFiles.length, '个');
              
              // 调用新的API保存非必换件记录和上传文件
              const id = await saveTaskNonPartProduct(
                part.taskId,
                part.productId,
                part.quantity,
                part.reason || '',
                actualFiles
              );
              
              console.log('保存非必换件成功，返回ID:', id);
              // 更新本地记录的ID
              part.id = id;
              part.quantityChanged = false; // 重置数量变更标记
              return part;
            } catch (error) {
              console.error('保存非必换件失败:', error);
              throw error;
            }
          });
        
        promises.push(...newPartPromises);
        
        // 2. 批量处理所有数量已变更的现有记录
        const changedRecords = this.nonReplacementPartList
          .filter(part => !part.id.startsWith('nrp-new-') && part.quantityChanged); // 只处理已有记录且数量已变更
        
        if (changedRecords.length > 0) {
          // 添加批量更新操作的Promise
          const batchUpdatePromise = async () => {
            // 标记所有记录为正在保存
            changedRecords.forEach(part => {
              part.saving = true;
            });
            
            try {
              // 构建批量更新请求数据
              const updateData = {
                taskId: this.selectedTask.id,
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
              
              return changedRecords;
            } catch (error) {
              // 更新失败，恢复原始数量
              console.error('批量更新数量失败:', error);
              changedRecords.forEach(part => {
                part.quantity = part.originalQuantity || part.quantity;
              });
              throw error;
            } finally {
              // 重置保存状态
              changedRecords.forEach(part => {
                part.saving = false;
              });
            }
          };
          
          promises.push(batchUpdatePromise());
        }
        
        // 等待所有操作完成
        await Promise.all(promises);
        this.$message.success('保存成功');
      } catch (error) {
        console.error('保存非必换件失败:', error);
      } finally {
        this.saveLoading = false;
      }
    },
    
    // 取消处理
    handleCancel() {
      this.$emit('update:visible', false);
    },
    

    
    // 处理航材选择
    handleProductSelect(rows) {
      console.log('选择航材事件触发', rows);
      // 这个方法可能不会被调用，因为DialogTable组件触发的是input-row事件
    },
    
    // 处理航材输入（这是DialogTable组件实际触发的事件）
    handleProductInput(selectedRow) {
      console.log('航材输入事件触发', selectedRow);
      
      if (selectedRow && (Array.isArray(selectedRow) ? selectedRow.length > 0 : true)) {
        // 如果是数组，取第一个元素；如果不是数组，直接使用
        const selectedProduct = Array.isArray(selectedRow) ? selectedRow[0] : selectedRow;
        
        if (selectedProduct) {
          // 更新表单数据
          this.form.productId = selectedProduct.id;
          this.form.productName = selectedProduct.name;
          this.form.productCode = selectedProduct.code;
          this.form.machineTypeName = selectedProduct.machineTypeName || '';
          this.form.partNumberName = selectedProduct.code || '';
          
          console.log('选择的航材ID:', selectedProduct.id);
          console.log('更新后的表单数据:', JSON.stringify(this.form));
          console.log('isFormValid现在的值:', this.isFormValid);
          
          this.$message.success('已选择航材：' + selectedProduct.name);
        }
      }
    }
  }
});
</script>

<style scoped>
.selected-product-info {
  margin-top: 8px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
}

.form-selector {
  flex: 1;
  margin-right: 20px;
}

.non-replacement-part-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.basic-info-container {
  margin-bottom: 16px;
}

.application-form {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 16px;
}

.form-title {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
}

.form-content {
  width: 100%;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.form-label {
  width: 80px;
  text-align: right;
  margin-right: 10px;
}

.form-input {
  width: 300px;
}

.form-input-number {
  width: 150px;
}

.form-textarea {
  width: calc(100% - 90px);
}

.application-list {
  margin-top: 20px;
}

.list-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
}

.list-header {
  display: flex;
  background-color: #f5f5f5;
  padding: 10px 0;
  margin-bottom: 10px;
  border: 1px solid #f0f0f0;
}

.header-item {
  flex: 1;
  text-align: center;
}

.list-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 10px 0;
}

.footer-left {
  font-weight: bold;
}

.footer-right {
  display: flex;
  gap: 10px;
}
</style>
