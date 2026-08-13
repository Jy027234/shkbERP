<template>
  <div>
    <div v-permission="['hr:training:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="TrainingCourse"
          ref="grid"
          resizable
          show-overflow
          highlight-hover-row
          keep-source
          row-id="id"
          :proxy-config="proxyConfig"
          :columns="tableColumn"
          :toolbar-config="toolbarConfig"
          :custom-config="{ storage: false }"
          :pager-config="{}"
          :sort-config="{ remote: true }"
          :loading="loading"
          height="530px"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
        >
          <template #form>
            <j-border>
              <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                <j-form-item label="课程名称">
                  <a-input v-model:value="searchFormData.courseName" allow-clear />
                </j-form-item>
                <j-form-item label="课程类型">
                  <a-select
                    v-model:value="searchFormData.courseType"
                    placeholder="请选择"
                    allow-clear
                    style="width: 150px"
                  >
                    <a-select-option value="公共类">公共类</a-select-option>
                    <a-select-option value="技术类">技术类</a-select-option>
                    <a-select-option value="其他">其他</a-select-option>
                  </a-select>
                </j-form-item>
                <j-form-item label="状态">
                  <a-select
                    v-model:value="searchFormData.status"
                    placeholder="请选择"
                    allow-clear
                    style="width: 120px"
                  >
                    <a-select-option :value="1">启用</a-select-option>
                    <a-select-option :value="0">禁用</a-select-option>
                  </a-select>
                </j-form-item>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button :icon="h(SyncOutlined)" @click="reset">重置</a-button>
              <a-button
                v-permission="['hr:training:create']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >新增课程</a-button
              >
              <a-button
                v-permission="['hr:training:query']"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
                >导出Excel</a-button
              >
              <a-button
                v-permission="['hr:training:delete']"
                danger
                :disabled="!selectedRowKeys.length"
                :icon="h(DeleteOutlined)"
                @click="handleBatchDelete"
                >批量删除</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="row.status === 1 ? 'success' : 'default'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <a-space size="small">
              <a-button type="link" @click="handleView(row)">详情</a-button>
              <a-button type="link" @click="handleFiles(row)">文档</a-button>
              <a-button v-permission="['hr:training:update']" type="link" @click="handleEdit(row)">编辑</a-button>
              <a-button v-permission="['hr:training:delete']" type="link" danger @click="handleDelete(row)">删除</a-button>
            </a-space>
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />

    <!-- 文档管理弹窗 -->
    <a-modal
      v-model:open="fileModalVisible"
      title="课程文档管理"
      :footer="null"
      width="1000px"
      :mask-closable="false"
    >
      <div v-loading="loading" class="course-file-list">
        <div class="file-actions">
          <a-upload
            :custom-request="handleFileUpload"
            :show-upload-list="false"
            :multiple="true"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.jpg,.jpeg,.png"
          >
            <a-button type="primary">
              <upload-outlined />
              上传文档
            </a-button>
          </a-upload>
        </div>
        <a-table
          :columns="fileColumns"
          :data-source="fileList"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'fileName'">
              <a @click="handleFilePreview(record)">{{ record.fileName }}</a>
            </template>
            <template v-if="column.dataIndex === 'fileSize'">
              {{ formatFileSize(record.fileSize) }}
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="handleFileDownload(record)">
                  下载
                </a-button>
                <a-popconfirm title="确定要删除该文档吗？" @confirm="handleFileDelete(record)">
                  <a-button type="link" danger size="small">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
        <a-empty v-if="fileList.length === 0" description="暂无课程文档" />
      </div>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import * as api from '@/api/hr/training-course';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    DeleteOutlined,
    DownloadOutlined,
    UploadOutlined
  } from '@ant-design/icons-vue';

  export default defineComponent({
    name: 'HrTrainingCourse',
    components: {
      Add,
      Modify,
      Detail,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        SyncOutlined,
        PlusOutlined,
        DeleteOutlined,
        DownloadOutlined,
        UploadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        id: '',
        selectedRows: [],
        selectedRowKeys: [],
        fileModalVisible: false,
        currentCourseId: '',
        fileList: [],
        searchFormData: {
          courseName: '',
          courseType: undefined,
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        },
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'courseName', title: '课程名称', width: 200 },
          { field: 'courseType', title: '课程类型', width: 120 },
          { field: 'implementationInterval', title: '实施间隔', width: 120 },
          { field: 'initialTrainingHours', title: '初训时长(h)', width: 120 },
          { field: 'retrainingHours', title: '复训时长(h)', width: 120 },
          { field: 'teachingMethod', title: '教学方式', width: 120 },
          { field: 'instructor', title: '教员', width: 120 },
          { field: 'assessmentMethod', title: '考核方式', width: 120 },
          { field: 'status', title: '状态', width: 100, slots: { default: 'status_default' } },
          { field: 'createTime', title: '创建时间', width: 180 },
          { title: '操作', width: 240, fixed: 'right', slots: { default: 'action_default' } },
        ],
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'total',
          },
          ajax: {
            query: ({ page, sorts }) => {
              const params = {
                ...this.$utils.buildSortPageVo(page, sorts),
                ...this.buildSearchFormData(),
              };
              return api.queryTrainingCourses(params);
            },
          },
        },
      };
    },
    mounted() {
      this.search();
    },
    computed: {
      fileColumns() {
        return [
          { title: '文件名称', dataIndex: 'fileName', ellipsis: true, width: 250 },
          { title: '文件类型', dataIndex: 'fileType', width: 100 },
          { title: '文件大小', dataIndex: 'fileSize', width: 100 },
          { title: '上传人', dataIndex: 'createBy', width: 100 },
          { title: '上传时间', dataIndex: 'createTime', width: 150 },
          { title: '操作', key: 'action', width: 150 }
        ];
      }
    },
    methods: {
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      
      buildQueryParams(page, sorts) {
        const params = {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
        return params;
      },
      
      buildSearchFormData() {
        return {
          courseName: this.searchFormData.courseName,
          courseType: this.searchFormData.courseType,
          status: this.searchFormData.status,
        };
      },
      
      reset() {
        this.searchFormData = {
          courseName: '',
          courseType: undefined,
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        };
        if (this.$refs.grid && this.$refs.grid.clearSort) {
          this.$refs.grid.clearSort();
        }
        this.search();
      },
      

      
      handleCheckboxChange({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },

      handleCheckboxAll({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },
      
      handleAdd() {
        this.$nextTick(() => this.$refs.addDialog.openDialog());
      },
      
      handleView(row) {
        this.id = row.id;
        this.$nextTick(() => this.$refs.viewDialog.openDialog());
      },
      
      handleEdit(row) {
        this.id = row.id;
        this.$nextTick(() => this.$refs.updateDialog.openDialog());
      },
      
      handleDelete(record) {
        this.$confirm({
          title: '确定要删除该课程吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.deleteTrainingCourse(record.id);
              this.$message.success('删除成功');
              this.search();
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleBatchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择要删除的课程');
          return;
        }
        
        this.$confirm({
          title: '确定要批量删除所选课程吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.batchDeleteTrainingCourses(this.selectedRowKeys);
              this.$message.success(`成功删除 ${this.selectedRowKeys.length} 条记录`);
              this.selectedRowKeys = [];
              this.search();
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleExport() {
        const params = this.buildSearchFormData();
        api.exportList(params).then((res) => {
          if (res) {
            const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `培训课程_${new Date().toLocaleDateString()}.xlsx`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            this.$message.success('导出成功');
          }
        }).catch(() => {
          // 错误已在拦截器处理
        });
      },
      
      async handleFiles(row) {
        console.log('开始处理文档:', row);
        this.currentCourseId = row.id;
        this.fileModalVisible = true;
        console.log('设置弹窗可见:', this.fileModalVisible);
        this.loading = true;
        try {
          console.log('开始请求文件列表');
          const res = await api.getCourseFileList(row.id);
          console.log('获取文件列表成功:', res);
          this.fileList = res || [];
          console.log('文件列表数据:', this.fileList);
          if (this.fileList.length > 0) {
            console.log('第一个文件的fileSize:', this.fileList[0].fileSize);
          }
        } catch (error) {
          console.error('获取文件列表失败:', error);
          this.fileList = [];
        } finally {
          this.loading = false;
          console.log('加载状态:', this.loading);
        }
      },
      
      async handleFileUpload(options) {
        const { file } = options;
        const actualFile = file.originFileObj || file;
        
        this.loading = true;
        try {
          await api.uploadCourseFile(this.currentCourseId, actualFile);
          this.$message.success('上传成功');
          await this.handleFiles({ id: this.currentCourseId });
        } finally {
          this.loading = false;
        }
      },
      
      handleFilePreview(record) {
        const fileUrl = record.url || record.fileUrl;
        if (fileUrl) {
          const isImage = /\.(jpg|jpeg|png|gif)$/i.test(record.fileName);
          if (isImage) {
            window.open(fileUrl, '_blank');
          } else {
            this.handleFileDownload(record);
          }
        }
      },
      
      handleFileDownload(record) {
        const fileUrl = record.url || record.fileUrl;
        if (fileUrl) {
          const link = document.createElement('a');
          link.href = fileUrl;
          link.download = record.fileName;
          link.target = '_blank';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
        }
      },
      
      async handleFileDelete(record) {
        this.loading = true;
        try {
          await api.deleteCourseFile(record.id);
          this.$message.success('删除成功');
          await this.handleFiles({ id: this.currentCourseId });
        } finally {
          this.loading = false;
        }
      },
      
      formatFileSize(size) {
        console.log('文件大小:', size);
        if (size === undefined || size === null) return '-';
        if (size < 1024) {
          return size + ' B';
        } else if (size < 1024 * 1024) {
          return (size / 1024).toFixed(2) + ' KB';
        } else {
          return (size / (1024 * 1024)).toFixed(2) + ' MB';
        }
      },
    },
  });
</script>

<style scoped>
  .course-file-list {
    .file-actions {
      margin-bottom: 16px;
    }
  }
</style>
