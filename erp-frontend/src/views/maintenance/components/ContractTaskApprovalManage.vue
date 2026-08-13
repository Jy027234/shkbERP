<template>
  <a-modal
    v-model:open="visible"
    title="放行文件管理"
    width="800px"
    :maskClosable="false"
    @cancel="handleCancel"
  >
    <div v-if="task">
      <!-- 任务基本信息 -->
      <div class="contract-info">
        <a-descriptions :column="3" size="small" bordered>
          <a-descriptions-item label="合同编号">{{ task.contractCode }}</a-descriptions-item>
          <a-descriptions-item label="客户">{{ task.customerName }}</a-descriptions-item>
          <a-descriptions-item label="机型">{{ task.machineTypeName }}</a-descriptions-item>
          <a-descriptions-item label="件号">{{ task.partNumberCode || '-' }}</a-descriptions-item>
          <a-descriptions-item label="序号">{{ task.serialNumber || '-' }}</a-descriptions-item>
        </a-descriptions>
      </div>

      <!-- 放行文件编号编辑 -->
      <div class="approval-number-section">
        <span class="label">放行文件编号：</span>
        <a-input
          v-model:value="approvalFileNumber"
          placeholder="请输入放行文件编号"
          style="width: 260px; margin-right: 8px;"
        />
        <a-button
          type="primary"
          size="small"
          :loading="savingApprovalNumber"
          @click="handleSaveApprovalFileNumber"
        >
          保存
        </a-button>
      </div>

      <!-- 上传放行文件 -->
      <div class="upload-section">
        <a-upload
          :file-list="fileList"
          :before-upload="beforeUpload"
          :multiple="true"
          :action="''"
          :custom-request="customUploadRequest"
          @change="handleUploadChange"
          @remove="handleRemoveUpload"
        >
          <a-button type="primary">
            <template #icon><CloudUploadOutlined /></template>
            选择文件
          </a-button>
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

      <!-- 放行文件列表 -->
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
                title="确定删除此文件吗？"
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
import { CloudUploadOutlined } from '@ant-design/icons-vue';
import { useMessage } from '@/hooks/web/useMessage';
import { getApprovalFiles, uploadApprovalFiles, deleteApprovalFile } from '@/api/maintenance/contract-task-approval-file';
import { updateApprovalFileNumber } from '@/api/maintenance/contract-task';

export default defineComponent({
  name: 'ContractTaskApprovalManage',
  setup() {
    const { createMessage } = useMessage();
    return { h, CloudUploadOutlined, createMessage };
  },
  data() {
    return {
      visible: false,
      loading: false,
      uploading: false,
      savingApprovalNumber: false,
      taskId: '',
      task: null,
      approvalFileNumber: '',
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
    // 打开弹窗
    openDialog(task) {
      this.taskId = task?.id || '';
      this.task = task || null;
      this.approvalFileNumber = task?.approvalFileNumber || '';
      this.visible = true;
      this.fileList = [];
      this.loadAttachments();
    },

    // 加载放行文件列表
    loadAttachments() {
      if (!this.taskId) return;
      this.loading = true;
      getApprovalFiles(this.taskId)
        .then((res) => {
          this.attachments = res || [];
        })
        .catch(() => {
          this.createMessage.error('获取放行文件失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 上传前校验
    beforeUpload() {
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
      // 提取原始文件
      const actualFiles = [];
      for (const fileItem of this.fileList) {
        if (fileItem.originFileObj) actualFiles.push(fileItem.originFileObj);
      }
      if (actualFiles.length === 0) {
        this.createMessage.error('没有可上传的文件');
        return;
      }
      this.uploading = true;
      try {
        await uploadApprovalFiles(this.taskId, actualFiles);
        this.createMessage.success('上传成功');
        this.fileList = [];
        this.loadAttachments();
      } catch (e) {
        this.createMessage.error('上传失败: ' + (e.message || '未知错误'));
      } finally {
        this.uploading = false;
      }
    },

    // 删除放行文件
    handleDelete(record) {
      this.loading = true;
      deleteApprovalFile(record.id)
        .then(() => {
          this.createMessage.success('删除成功');
          this.loadAttachments();
        })
        .catch(() => {
          this.createMessage.error('删除失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 阻止 a-upload 默认发请求（采用手动上传）
    customUploadRequest() {
      return Promise.resolve();
    },

    // 取消/关闭
    handleCancel() {
      this.visible = false;
      this.taskId = '';
      this.task = null;
      this.attachments = [];
      this.fileList = [];
      this.approvalFileNumber = '';
    },

    // 保存放行文件编号
    async handleSaveApprovalFileNumber() {
      if (!this.taskId) {
        this.createMessage.error('任务信息缺失，无法保存放行文件编号');
        return;
      }
      if (!this.approvalFileNumber) {
        this.createMessage.warning('请输入放行文件编号');
        return;
      }

      this.savingApprovalNumber = true;
      try {
        await updateApprovalFileNumber({
          id: this.taskId,
          approvalFileNumber: this.approvalFileNumber,
        });
        if (this.task) {
          this.task.approvalFileNumber = this.approvalFileNumber;
        }
        this.createMessage.success('放行文件编号已更新');
      } catch (e) {
        this.createMessage.error('更新放行文件编号失败');
      } finally {
        this.savingApprovalNumber = false;
      }
    },
  },
});
</script>

<style scoped>
.contract-info { margin-bottom: 20px; }
.upload-section { margin-bottom: 20px; display: flex; align-items: center; }
.attachment-list { max-height: 300px; overflow-y: auto; }
.approval-number-section { margin-bottom: 20px; display: flex; align-items: center; }
.approval-number-section .label { margin-right: 8px; }
</style>
