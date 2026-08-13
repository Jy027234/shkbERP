<template>
  <div>
    <a-modal
      v-model:open="visible"
      :title="title"
      :maskClosable="false"
      :destroyOnClose="true"
      :footer="null"
      width="800px"
    >
      <div v-if="visible">
        <!-- 工具栏 -->
        <div style="margin-bottom: 16px">
          <a-space>
            <a-upload
              :file-list="fileList"
              :multiple="true"
              :before-upload="beforeUpload"
              @remove="handleRemove"
            >
              <a-button type="primary" :icon="h(UploadOutlined)">
                上传附件
              </a-button>
            </a-upload>
            <a-button
              v-if="selectedRowKeys.length > 0"
              type="danger"
              :icon="h(DeleteOutlined)"
              @click="handleBatchDelete"
            >
              批量删除
            </a-button>
          </a-space>
        </div>

        <!-- 附件列表 -->
        <a-table
          :loading="loading"
          :columns="columns"
          :data-source="files"
          :pagination="pagination"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          @change="handleTableChange"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'fileName'">
              <a :href="record.url" target="_blank">{{ record.fileName }}</a>
            </template>
            <template v-if="column.key === 'fileSize'">
              {{ formatFileSize(record.fileSize) }}
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a :href="record.url" target="_blank">下载</a>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="确定要删除该附件吗？"
                  @confirm="handleDelete(record.id)"
                  ok-text="确定"
                  cancel-text="取消"
                >
                  <a class="danger-text">删除</a>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, h, ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { UploadOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import * as toolRecordFileApi from '@/api/equipment/tool-record-file';

export default defineComponent({
  name: 'ToolRecordFiles',
  props: {
    recordId: {
      type: String,
      required: true
    }
  },
  setup(props, { emit }) {
    const visible = ref(false);
    const title = ref('计量记录附件');

    return {
      visible,
      title
    };
  },
  data() {
    return {
      loading: false,
      files: [],
      fileList: [],
      selectedRowKeys: [],
      columns: [
        { title: '文件名', dataIndex: 'fileName', key: 'fileName' },
        { title: '文件类型', dataIndex: 'contentType', key: 'contentType' },
        { title: '文件大小', dataIndex: 'fileSize', key: 'fileSize' },
        { title: '上传时间', dataIndex: 'createTime', key: 'createTime' },
        { title: '操作', key: 'action', width: 150 }
      ],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 条`
      }
    };
  },
  methods: {
    h,
    // 打开对话框
    openDialog() {
      this.visible = true;
      this.loadFiles();
    },

    // 加载附件列表
    async loadFiles() {
      if (!this.recordId) return;

      this.loading = true;
      try {
        const res = await toolRecordFileApi.getToolRecordFiles(this.recordId);
        if (res) {
          this.files = res || [];
        }
      } catch (error) {
        console.error('加载附件列表失败', error);
        message.error('加载附件列表失败');
        this.files = [];
      } finally {
        this.loading = false;
      }
    },

    // 表格分页、排序、筛选变化时触发
    handleTableChange(pagination) {
      this.pagination.current = pagination.current;
      this.pagination.pageSize = pagination.pageSize;
    },

    // 选择行变化
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },

    // 上传前处理
    beforeUpload(file) {
      this.fileList = [...this.fileList, file];
      return false;
    },

    // 移除文件
    handleRemove(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },

    // 上传附件
    async uploadFiles() {
      if (!this.recordId || this.fileList.length === 0) return;

      try {
        await toolRecordFileApi.uploadToolRecordFiles(this.recordId, this.fileList);
        message.success('上传附件成功');
        this.fileList = [];
        this.loadFiles();
        this.$emit('uploaded');
      } catch (error) {
        console.error('上传附件失败', error);
        message.error('上传附件失败');
      }
    },

    // 删除附件
    async handleDelete(id) {
      if (!id) return;

      try {
        await toolRecordFileApi.deleteToolRecordFile(id);
        message.success('删除附件成功');
        this.loadFiles();
        this.$emit('deleted');
      } catch (error) {
        console.error('删除附件失败', error);
        message.error('删除附件失败');
      }
    },

    // 批量删除附件
    async handleBatchDelete() {
      if (this.selectedRowKeys.length === 0) return;

      try {
        await toolRecordFileApi.batchDeleteToolRecordFiles(this.selectedRowKeys);
        message.success('批量删除附件成功');
        this.selectedRowKeys = [];
        this.loadFiles();
        this.$emit('deleted');
      } catch (error) {
        console.error('批量删除附件失败', error);
        message.error('批量删除附件失败');
      }
    },

    // 格式化文件大小
    formatFileSize(size) {
      if (!size) return '0 B';
      
      const units = ['B', 'KB', 'MB', 'GB', 'TB'];
      let i = 0;
      while (size >= 1024 && i < units.length - 1) {
        size /= 1024;
        i++;
      }
      
      return `${size.toFixed(2)} ${units[i]}`;
    }
  }
});
</script>

<style scoped>
.danger-text {
  color: #ff4d4f;
}
</style>
