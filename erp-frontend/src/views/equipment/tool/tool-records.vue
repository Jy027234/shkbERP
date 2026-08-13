<template>
  <div>
    <a-modal
      v-model:open="visible"
      :title="title"
      width="1000px"
      :footer="null"
      :maskClosable="false"
      :destroyOnClose="true"
    >
      <div v-if="visible">
        <!-- 工具信息 -->
        <a-card title="工具信息" style="margin-bottom: 16px">
          <a-descriptions :column="3" bordered>
            <template v-if="toolInfo">
              <a-descriptions-item label="管理编号">{{ toolInfo.code || '-' }}</a-descriptions-item>
              <a-descriptions-item label="设备名称">{{ toolInfo.name || '-' }}</a-descriptions-item>
              <a-descriptions-item label="管理区域">{{ toolInfo.managementArea || '-' }}</a-descriptions-item>
              <a-descriptions-item label="规格">{{ toolInfo.specification || '-' }}</a-descriptions-item>
              <a-descriptions-item label="计量周期">{{ toolInfo.calibrationPeriod ? `${toolInfo.calibrationPeriod}` : '-' }}</a-descriptions-item>
              <a-descriptions-item label="存放位置">{{ toolInfo.storageLocation || '-' }}</a-descriptions-item>
            </template>
            <template v-else>
              <a-descriptions-item label="管理编号">-</a-descriptions-item>
              <a-descriptions-item label="设备名称">-</a-descriptions-item>
              <a-descriptions-item label="管理区域">-</a-descriptions-item>
              <a-descriptions-item label="规格">-</a-descriptions-item>
              <a-descriptions-item label="计量周期">-</a-descriptions-item>
              <a-descriptions-item label="存放位置">-</a-descriptions-item>
            </template>
          </a-descriptions>
        </a-card>

        <!-- 工具栏 -->
        <div style="margin-bottom: 16px">
          <a-space>
            <a-button
              v-permission="['equipment:tool']"
              type="primary"
              :icon="h(PlusOutlined)"
              @click="openAddDialog"
            >
              新增计量记录
            </a-button>
          </a-space>
        </div>

        <!-- 计量记录列表 -->
        <a-table
          :loading="loading"
          :columns="columns"
          :data-source="records"
          :pagination="pagination"
          @change="handleTableChange"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'attachments'">
              <div v-if="record.attachments && record.attachments.length > 0">
                <div v-for="(file, index) in record.attachments" :key="index" style="margin-bottom: 4px; display: flex; align-items: center;">
                  <a :href="file.url" target="_blank" style="flex: 1;">
                    <paper-clip-outlined /> {{ file.fileName }}
                  </a>
                  <a @click.stop="handleDeleteAttachment(file.id, record)" style="color: #ff4d4f; cursor: pointer; margin-left: 8px;">
                    <delete-outlined />
                  </a>
                </div>
              </div>
              <span v-else>-</span>
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a v-permission="['equipment:tool']" @click="openEditDialog(record)">修改</a>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="确定要删除该计量记录吗？"
                  @confirm="handleDelete(record.id)"
                  ok-text="确定"
                  cancel-text="取消"
                >
                  <a v-permission="['equipment:tool']" class="danger-text">删除</a>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>

    <!-- 新增计量记录对话框 -->
    <a-modal
      v-model:open="addDialogVisible"
      title="新增计量记录"
      :maskClosable="false"
      :destroyOnClose="true"
      @ok="handleAddSubmit"
      :confirmLoading="submitLoading"
    >
      <a-form
        ref="addForm"
        :model="formData"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="工具" name="toolId">
          <a-select v-model:value="formData.toolId" placeholder="请选择工具" disabled>
            <a-select-option :value="formData.toolId">
              {{ toolInfo && toolInfo.code ? `${toolInfo.code} - ${toolInfo.name}` : '加载中...' }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维保人" name="maintenancenUser">
          <a-input v-model:value="formData.maintenancenUser" placeholder="请输入维保人" />
        </a-form-item>
        <a-form-item label="计量时间" name="maintenanceTime">
          <a-date-picker
            v-model:value="formData.maintenanceTime"
            placeholder="请选择计量时间"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="计量证书编号" name="certificateNumber">
          <a-input v-model:value="formData.certificateNumber" placeholder="请输入计量证书编号" />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入备注" />
        </a-form-item>
        <a-form-item label="附件上传">
          <a-upload
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            @remove="handleRemove"
          >
            <a-button>
              <UploadOutlined /> 上传附件
            </a-button>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改计量记录对话框 -->
    <a-modal
      v-model:open="editDialogVisible"
      title="修改计量记录"
      :maskClosable="false"
      :destroyOnClose="true"
      @ok="handleEditSubmit"
      :confirmLoading="submitLoading"
    >
      <a-form
        ref="editForm"
        :model="formData"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="工具" name="toolId">
          <a-select v-model:value="formData.toolId" placeholder="请选择工具" disabled>
            <a-select-option :value="formData.toolId">
              {{ toolInfo && toolInfo.code ? `${toolInfo.code} - ${toolInfo.name}` : '加载中...' }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维保人" name="maintenancenUser">
          <a-input v-model:value="formData.maintenancenUser" placeholder="请输入维保人" />
        </a-form-item>
        <a-form-item label="计量时间" name="maintenanceTime">
          <a-date-picker
            v-model:value="formData.maintenanceTime"
            placeholder="请选择计量时间"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="计量证书编号" name="certificateNumber">
          <a-input v-model:value="formData.certificateNumber" placeholder="请输入计量证书编号" />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入备注" />
        </a-form-item>
        <a-form-item label="附件上传">
          <a-upload
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            @remove="handleRemove"
          >
            <a-button>
              <UploadOutlined /> 上传附件
            </a-button>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, h, ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined, UploadOutlined, PaperClipOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import * as toolApi from '@/api/equipment/tool';
import * as toolRecordApi from '@/api/equipment/tool-record';

export default defineComponent({
  name: 'ToolRecords',
  props: {
    toolIds: {
      type: Array,
      required: true
    }
  },
  components: {
    UploadOutlined,
    PaperClipOutlined,
    DeleteOutlined
  },
  setup(props, { emit }) {
    const visible = ref(false);
    const title = ref('工具计量记录管理');
    const loading = ref(false);
    const records = ref([]);
    const toolInfo = ref(null);
    const addDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const submitLoading = ref(false);

    return {
      visible,
      title,
      loading,
      records,
      toolInfo,
      addDialogVisible,
      editDialogVisible,
      submitLoading,
      h,
      PlusOutlined
    };
  },
  data() {
    return {
      // 表格列定义
      columns: [
        { title: '维保人', dataIndex: 'maintenancenUser', key: 'maintenancenUser' },
        { title: '计量时间', dataIndex: 'maintenanceTime', key: 'maintenanceTime', sorter: true },
        { title: '计量证书编号', dataIndex: 'certificateNumber', key: 'certificateNumber' },
        { title: '备注', dataIndex: 'description', key: 'description' },
        { title: '创建人', dataIndex: 'createBy', key: 'createBy' },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', sorter: true },
        { 
          title: '附件', 
          dataIndex: 'attachments', 
          key: 'attachments'
        },
        { title: '操作', key: 'action', width: 150 }
      ],
      // 分页配置
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 条记录`
      },
      // 查询参数
      queryParams: {
        pageIndex: 1,
        pageSize: 10,
        sortField: null,
        sortOrder: null,
        toolId: null
      },
      // 表单数据
      formData: {
        toolId: '',
        maintenancenUser: '',
        maintenanceTime: '',
        certificateNumber: '',
        description: ''
      },
      // 表单验证规则
      rules: {
        maintenancenUser: [{ required: true, message: '请输入维保人', trigger: 'blur' }],
        maintenanceTime: [{ required: true, message: '请选择计量时间', trigger: 'change' }],
        certificateNumber: [{ required: true, message: '请输入计量证书编号', trigger: 'blur' }]
      },
      // 附件列表
      fileList: []
    };
  },
  methods: {
    // 打开对话框
    openDialog() {
      if (!this.toolIds || this.toolIds.length === 0) {
        message.warning('请先选择工具');
        return;
      }

      this.visible = true;
      
      // 重置分页
      this.queryParams.pageIndex = 1;
      this.queryParams.pageSize = 10;
      
      // 如果只选择了一个工具，加载工具信息
      if (this.toolIds.length === 1) {
        this.loadToolInfo(this.toolIds[0]);
      } else {
        this.toolInfo = null;
        this.title = `工具计量记录管理 (已选择 ${this.toolIds.length} 个工具)`;
      }
      
      // 加载计量记录
      this.loadRecords();
    },
    
    // 加载工具信息
    async loadToolInfo(toolId) {
      try {
        const res = await toolApi.get(toolId);
        if (res) {
          this.toolInfo = res;
          this.title = `工具计量记录管理 - ${res.name || res.code || ''}`;
        }
      } catch (error) {
        console.error('加载工具信息失败', error);
        message.error('加载工具信息失败');
        this.toolInfo = null;
      }
    },
    
    // 加载计量记录
    async loadRecords() {
      if (!this.toolIds || this.toolIds.length === 0) {
        return;
      }
      
      this.loading = true;
      try {
        const params = {
          ...this.queryParams,
          toolId: this.toolIds.length === 1 ? this.toolIds[0] : this.toolIds[0] // 如果多选，默认显示第一个工具的记录
        };
        const res = await toolRecordApi.query(params);
        if (res) {
          this.records = res.datas || [];
          this.pagination.total = res.totalCount || 0;
          this.pagination.current = params.pageIndex;
          this.pagination.pageSize = params.pageSize;
        }
      } catch (error) {
        console.error('加载计量记录失败', error);
        message.error('加载计量记录失败');
        this.records = [];
      } finally {
        this.loading = false;
      }
    },

    // 表格分页、排序、筛选变化时触发
    handleTableChange(pagination, filters, sorter) {
      this.queryParams.pageIndex = pagination.current;
      this.queryParams.pageSize = pagination.pageSize;
      
      // 处理排序
      if (sorter && sorter.field) {
        this.queryParams.sortField = sorter.field;
        this.queryParams.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc';
      } else {
        this.queryParams.sortField = null;
        this.queryParams.sortOrder = null;
      }
      
      this.loadRecords();
    },

    // 打开新增对话框
    openAddDialog() {
      if (!this.toolInfo || !this.toolInfo.id) {
        message.warning('请先选择工具');
        return;
      }
      
      this.formData = {
        toolId: this.toolInfo.id,
        maintenancenUser: '',
        maintenanceTime: '',
        certificateNumber: '',
        description: ''
      };
      this.fileList = [];
      this.addDialogVisible = true;
    },
    
    // 文件上传前处理
    beforeUpload(file) {
      // 限制文件大小为10MB
      const isLt50M = file.size / 1024 / 1024 < 50;
      if (!isLt50M) {
        message.error('文件大小不能超过50MB!');
        return false;
      }
      return false; // 阻止自动上传，由我们手动控制
    },
    
    // 文件变更处理
    handleFileChange(info) {
      // 保留所有文件
      this.fileList = [...info.fileList];
    },
    
    // 移除文件
    handleRemove(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },

    // 打开编辑对话框
    openEditDialog(record) {
      if (!record) return;
      
      this.formData = {
        id: record.id,
        toolId: record.toolId,
        maintenancenUser: record.maintenancenUser,
        maintenanceTime: record.maintenanceTime,
        certificateNumber: record.certificateNumber,
        description: record.description
      };
      
      // 设置附件列表
      this.fileList = [];
      this.editDialogVisible = true;
    },

    // 提交新增
    handleAddSubmit() {
      this.$refs.addForm.validate().then(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          try {
            // 创建表单数据的副本，避免修改原始数据
            const formData = new FormData();
            
            // 添加基本表单数据
            formData.append('toolId', this.formData.toolId);
            formData.append('maintenancenUser', this.formData.maintenancenUser);
            formData.append('maintenanceTime', this.formData.maintenanceTime);
            formData.append('certificateNumber', this.formData.certificateNumber);
            if (this.formData.description) {
              formData.append('description', this.formData.description);
            }
            
            // 添加文件
            if (this.fileList && this.fileList.length > 0) {
              this.fileList.forEach(file => {
                if (file.originFileObj) {
                  formData.append('files', file.originFileObj);
                }
              });
            }
            
            await toolRecordApi.create(formData);
            message.success('新增计量记录成功');
            this.addDialogVisible = false;
            this.loadRecords();
            this.$emit('confirm');
          } catch (error) {
            console.error('新增计量记录失败', error);
            message.error('新增计量记录失败');
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },

    // 提交编辑
    handleEditSubmit() {
      this.$refs.editForm.validate().then(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          try {
            // 创建表单数据的副本，避免修改原始数据
            const formData = new FormData();
            
            // 添加基本表单数据
            formData.append('id', this.formData.id);
            formData.append('toolId', this.formData.toolId);
            formData.append('maintenancenUser', this.formData.maintenancenUser);
            formData.append('maintenanceTime', this.formData.maintenanceTime);
            formData.append('certificateNumber', this.formData.certificateNumber);
            if (this.formData.description) {
              formData.append('description', this.formData.description);
            }
            
            // 添加文件
            if (this.fileList && this.fileList.length > 0) {
              this.fileList.forEach(file => {
                if (file.originFileObj) {
                  formData.append('files', file.originFileObj);
                }
              });
            }
            
            await toolRecordApi.update(formData);
            message.success('修改计量记录成功');
            this.editDialogVisible = false;
            this.loadRecords();
            this.$emit('confirm');
          } catch (error) {
            console.error('修改计量记录失败', error);
            message.error('修改计量记录失败');
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },

    // 删除计量记录
    async handleDelete(id) {
      if (!id) return;
      
      try {
        await toolRecordApi.deleteById(id);
        message.success('删除计量记录成功');
        this.loadRecords();
        this.$emit('confirm');
      } catch (error) {
        console.error('删除计量记录失败', error);
        message.error('删除计量记录失败');
      }
    },
    
    // 删除附件
    async handleDeleteAttachment(attachmentId, record) {
      if (!attachmentId) return;
      
      this.$confirm({
        title: '提示',
        content: '确定要删除此附件吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: async () => {
          try {
            await toolRecordApi.deleteAttachment(attachmentId);
            message.success('删除附件成功');
            // 重新加载记录列表
            this.loadRecords();
          } catch (error) {
            console.error('删除附件失败', error);
            message.error('删除附件失败');
          }
        }
      });
    },
  }
});
</script>

<style scoped>
.danger-text {
  color: #ff4d4f;
}
</style>
