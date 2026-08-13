<template>
  <a-modal
    v-model:open="visible"
    title="批次库存附件管理"
    width="800px"
    :maskClosable="false"
    @cancel="handleCancel"
  >
    <div v-if="batchInfo">
      <!-- 批次库存基础信息 -->
      <div class="batch-info">
        <a-descriptions :column="3" size="small" bordered>
          <a-descriptions-item label="仓库">{{ batchInfo.scName }}</a-descriptions-item>
          <a-descriptions-item label="航材名称">{{ batchInfo.productName }}</a-descriptions-item>
          <a-descriptions-item label="批次号">{{ batchInfo.batchNumber }}</a-descriptions-item>
          <a-descriptions-item label="库存数量">{{ batchInfo.quantity }}</a-descriptions-item>
          <a-descriptions-item label="生产日期">{{ batchInfo.productionDate }}</a-descriptions-item>
          <a-descriptions-item label="失效日期">{{ batchInfo.expiryDate }}</a-descriptions-item>
        </a-descriptions>
      </div>
      
      <!-- 上传附件 -->
      <div class="upload-section" style="margin-top: 15px;">
        <a-upload
          :file-list="fileList"
          :before-upload="beforeUpload"
          :multiple="true"
          @change="handleUploadChange"
          @remove="handleRemoveUpload"
        >
          <a-button type="primary" :icon="h(UploadOutlined)">选择文件</a-button>
        </a-upload>
        <a-button
          type="primary"
          :disabled="fileList.length === 0"
          :loading="uploading"
          style="margin-left: 10px;"
          @click="handleUpload"
        >
          {{ uploading ? '上传中' : '上传' }}
        </a-button>
      </div>
      
      <!-- 附件列表 -->
      <div class="attachment-list" style="margin-top: 15px;">
        <a-table
          :columns="columns"
          :data-source="attachments"
          :pagination="false"
          :loading="loading"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'fileName'">
              <a :href="record.url" target="_blank">{{ record.fileName }}</a>
            </template>
            <template v-else-if="column.key === 'fileSize'">
              {{ formatFileSize(record.fileSize) }}
            </template>
            <template v-else-if="column.key === 'createTime'">
              {{ record.createTime }}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-popconfirm
                title="确定删除此附件吗？"
                @confirm="handleDelete(record)"
                ok-text="确定"
                cancel-text="取消"
              >
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </div>
    </div>
    
    <template #footer>
      <a-button @click="handleCancel">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>
import { defineComponent, h } from 'vue';
import { UploadOutlined } from '@ant-design/icons-vue';
import { get } from '@/api/sc/stock/product-stock-batch';
import * as batchFileApi from '@/api/sc/stock/product-stock-batch-file/index';

export default defineComponent({
  name: 'ProductBatchAttachment',
  setup() {
    return {
      h,
      UploadOutlined
    };
  },
  data() {
    return {
      visible: false,
      loading: false,
      uploading: false,
      batchId: '',
      batchInfo: null,
      attachments: [],
      fileList: [],
      columns: [
        {
          title: '文件名',
          dataIndex: 'fileName',
          key: 'fileName',
          ellipsis: true,
        },
        {
          title: '大小',
          dataIndex: 'fileSize',
          key: 'fileSize',
          width: 100,
        },
        {
          title: '上传时间',
          dataIndex: 'createTime',
          key: 'createTime',
          width: 180,
        },
        {
          title: '操作',
          key: 'action',
          width: 100,
          align: 'center',
        },
      ],
    };
  },
  methods: {
    // 打开弹窗
    openDialog(batchId) {
      this.batchId = batchId;
      this.visible = true;
      this.fileList = [];
      
      // 加载批次库存信息
      this.loadBatchInfo();
      
      // 加载附件列表
      this.loadAttachments();
    },
    
    // 加载批次库存信息
    loadBatchInfo() {
      if (!this.batchId) return;
      
      get(this.batchId).then(res => {
        this.batchInfo = res;
      }).catch(err => {
        this.$msg.createError('获取批次库存信息失败');
      });
    },
    
    // 加载附件列表
    loadAttachments() {
      if (!this.batchId) return;
      
      this.loading = true;
      batchFileApi.query(this.batchId)
        .then(res => {
          this.attachments = res || [];
        })
        .catch(err => {
          this.$msg.createError('获取附件列表失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 上传前校验
    beforeUpload(file) {
      // 这里可以添加文件类型、大小等限制
      // 返回 false 会阻止文件自动上传
      return false;
    },
    
    // 处理上传文件变化
    handleUploadChange({ fileList }) {
      this.fileList = fileList;
    },
    
    // 移除上传文件
    handleRemoveUpload(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },
    
    // 执行上传
    async handleUpload() {
      if (this.fileList.length === 0) {
        this.$msg.createWarning('请选择要上传的文件');
        return;
      }
      
      // 准备实际文件对象列表
      const actualFiles = [];
      for (const fileItem of this.fileList) {
        if (fileItem.originFileObj) {
          actualFiles.push(fileItem.originFileObj);
        } else {
          this.$msg.createError(`文件 ${fileItem.name} 无法上传，请重新选择`);
          return;
        }
      }
      
      if (actualFiles.length === 0) {
        this.$msg.createError('没有可上传的文件');
        return;
      }
      
      this.uploading = true;
      
      try {
        // 调用API上传文件
        await batchFileApi.upload(this.batchId, actualFiles);
        
        // 上传成功
        this.$msg.createSuccessTip('附件上传成功');
        
        // 清空文件列表
        this.fileList = [];
        
        // 重新加载附件列表
        this.loadAttachments();
      } catch (error) {
        console.error('附件上传失败:', error);
        this.$msg.createError('上传失败：' + (error.message || '未知错误'));
      } finally {
        this.uploading = false;
      }
    },
    
    // 删除附件
    handleDelete(record) {
      batchFileApi.remove(record.id)
        .then(res => {
          this.$msg.createSuccessTip('删除成功！');
          // 重新加载附件列表
          this.loadAttachments();
        })
        .catch(err => {
          this.$msg.createError('删除失败：' + (err.message || '未知错误'));
        });
    },
    
    // 取消/关闭
    handleCancel() {
      this.visible = false;
      this.fileList = [];
    },
    
    // 格式化文件大小
    formatFileSize(size) {
      // 后端已经返回格式化后的字符串，直接返回
      return size || '-';
    },
  },
});
</script>

<style scoped>
.batch-info {
  margin-bottom: 15px;
}

.upload-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.attachment-list {
  margin-top: 15px;
}
</style>
