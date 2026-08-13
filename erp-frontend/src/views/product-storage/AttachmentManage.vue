<template>
  <a-modal
    v-model:open="visible"
    title="接收单附件管理"
    width="800px"
    :maskClosable="false"
    @cancel="handleCancel"
  >
    <div v-if="storage">
      <div class="basic-info">
        <a-descriptions :column="3" size="small" bordered>
          <a-descriptions-item label="客户名称">{{ storage.clientName }}</a-descriptions-item>
          <a-descriptions-item label="产品名称">{{ storage.productName }}</a-descriptions-item>
          <a-descriptions-item label="件号">{{ storage.productCode }}</a-descriptions-item>
        </a-descriptions>
      </div>

      <div class="upload-section">
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

      <div class="attachment-list">
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
              {{ record.fileSize }}
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
import { useMessage } from '@/hooks/web/useMessage';
import { getProductStorageAttachments, uploadProductStorageAttachments, deleteProductStorageAttachment } from '@/api/shkb/product-storage/attachment';
import { get as getProductStorage } from '@/api/shkb/product-storage';

export default defineComponent({
  name: 'ProductStorageAttachmentManage',
  setup() {
    const { createMessage } = useMessage();
    return { h, UploadOutlined, createMessage };
  },
  data() {
    return {
      visible: false,
      loading: false,
      uploading: false,
      productStorageId: '',
      storage: null,
      attachments: [],
      fileList: [],
      columns: [
        { title: '文件名', dataIndex: 'fileName', key: 'fileName', ellipsis: true },
        { title: '大小', dataIndex: 'fileSize', key: 'fileSize', width: 100 },
        { title: '上传时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
        { title: '操作', key: 'action', width: 100, align: 'center' },
      ],
    };
  },
  methods: {
    openDialog(productStorageId) {
      this.productStorageId = productStorageId;
      this.visible = true;
      this.fileList = [];
      this.loadStorageInfo();
      this.loadAttachments();
    },
    loadStorageInfo() {
      if (!this.productStorageId) return;
      getProductStorage(this.productStorageId)
        .then((res) => {
          this.storage = res;
        })
        .catch(() => this.createMessage.error('获取接收单信息失败'));
    },
    loadAttachments() {
      if (!this.productStorageId) return;
      this.loading = true;
      getProductStorageAttachments(this.productStorageId)
        .then((res) => {
          this.attachments = res || [];
        })
        .catch(() => this.createMessage.error('获取附件列表失败'))
        .finally(() => (this.loading = false));
    },
    beforeUpload() {
      return false;
    },
    handleUploadChange({ fileList }) {
      this.fileList = fileList;
    },
    handleRemoveUpload(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },
    async handleUpload() {
      if (this.fileList.length === 0) {
        this.createMessage.warning('请选择要上传的文件');
        return;
      }
      const actualFiles = [];
      for (const item of this.fileList) {
        if (item.originFileObj) actualFiles.push(item.originFileObj);
        else {
          this.createMessage.error(`文件 ${item.name} 无法上传，请重新选择`);
          return;
        }
      }
      if (!actualFiles.length) {
        this.createMessage.error('没有可上传的文件');
        return;
      }
      this.uploading = true;
      try {
        await uploadProductStorageAttachments(this.productStorageId, actualFiles);
        this.createMessage.success('附件上传成功');
        this.fileList = [];
        this.loadAttachments();
      } catch (e) {
        this.createMessage.error('附件上传失败: ' + (e.message || '未知错误'));
      } finally {
        this.uploading = false;
      }
    },
    handleDelete(record) {
      this.loading = true;
      deleteProductStorageAttachment(record.id)
        .then(() => {
          this.createMessage.success('删除成功');
          this.loadAttachments();
        })
        .catch(() => this.createMessage.error('删除失败'))
        .finally(() => (this.loading = false));
    },
    handleCancel() {
      this.visible = false;
      this.productStorageId = '';
      this.storage = null;
      this.attachments = [];
      this.fileList = [];
    },
  },
});
</script>

<style scoped>
.basic-info {
  margin-bottom: 20px;
}
.upload-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
.attachment-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
