<template>
  <a-modal
    v-model:open="visible"
    title="工卡附件管理"
    width="800px"
    :maskClosable="false"
    @cancel="handleCancel"
  >
    <div v-if="workCard">
      <!-- 工卡基本信息 -->
      <div class="work-card-info">
        <a-descriptions :column="3" size="small" bordered>
          <a-descriptions-item label="工卡编号">{{ workCard.code }}</a-descriptions-item>
          <a-descriptions-item label="工卡名称">{{ workCard.name }}</a-descriptions-item>
          <a-descriptions-item label="客户">{{ workCard.customerName }}</a-descriptions-item>
        </a-descriptions>
      </div>
      
      <!-- 上传附件 -->
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
      
      <!-- 附件列表 -->
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
import { defineComponent, ref, reactive, h } from 'vue';
import { UploadOutlined } from '@ant-design/icons-vue';
import { useMessage } from '@/hooks/web/useMessage';
import { get as getWorkCard } from '@/api/work-card';
import {
  getWorkCardAttachments,
  uploadWorkCardAttachments,
  deleteWorkCardAttachment,
  batchDeleteWorkCardAttachments
} from '@/api/work-card/attachment';

export default defineComponent({
  name: 'WorkCardAttachmentManage',
  setup() {
    const { createMessage } = useMessage();
    
    return {
      h,
      UploadOutlined,
      createMessage
    };
  },
  data() {
    return {
      visible: false,
      loading: false,
      uploading: false,
      workCardId: '',
      workCard: null,
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
    openDialog(workCardId) {
      this.workCardId = workCardId;
      this.visible = true;
      this.fileList = [];
      
      // 加载工卡信息
      this.loadWorkCardInfo();
      
      // 加载附件列表
      this.loadAttachments();
    },
    
    // 加载工卡信息
    loadWorkCardInfo() {
      if (!this.workCardId) return;
      
      getWorkCard(this.workCardId).then(res => {
        this.workCard = res;
      }).catch(err => {
        this.createMessage.error('获取工卡信息失败');
      });
    },
    
    // 加载附件列表
    loadAttachments() {
      if (!this.workCardId) return;
      
      this.loading = true;
      getWorkCardAttachments(this.workCardId)
        .then(res => {
          this.attachments = res || [];
        })
        .catch(err => {
          this.createMessage.error('获取附件列表失败');
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
        this.createMessage.warning('请选择要上传的文件');
        return;
      }
      
      // 准备实际文件对象列表
      const actualFiles = [];
      for (const fileItem of this.fileList) {
        if (fileItem.originFileObj) {
          actualFiles.push(fileItem.originFileObj);
        } else {
          this.createMessage.error(`文件 ${fileItem.name} 无法上传，请重新选择`);
          return;
        }
      }
      
      if (actualFiles.length === 0) {
        this.createMessage.error('没有可上传的文件');
        return;
      }
      
      this.uploading = true;
      
      try {
        // 调用API上传文件
        await uploadWorkCardAttachments(this.workCardId, actualFiles);
        
        // 上传成功
        this.createMessage.success('附件上传成功');
        
        // 清空文件列表
        this.fileList = [];
        
        // 重新加载附件列表
        this.loadAttachments();
      } catch (error) {
        console.error('附件上传失败:', error);
        this.createMessage.error('附件上传失败: ' + (error.message || '未知错误'));
      } finally {
        this.uploading = false;
      }
    },
    
    // 删除附件
    handleDelete(record) {
      this.loading = true;
      deleteWorkCardAttachment(record.id)
        .then(res => {
          this.createMessage.success('删除成功');
          this.loadAttachments(); // 重新加载附件列表
        })
        .catch(err => {
          this.createMessage.error('删除失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 取消/关闭
    handleCancel() {
      this.visible = false;
      this.workCardId = '';
      this.workCard = null;
      this.attachments = [];
      this.fileList = [];
    },
  },
});
</script>

<style scoped>
.work-card-info {
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
