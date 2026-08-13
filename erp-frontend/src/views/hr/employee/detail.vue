<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="1000px"
    title="员工详情"
    :footer="null"
  >
    <div v-if="visible" v-loading="loading">
      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="basic" tab="基本信息">
          <a-row :gutter="24">
            <a-col :span="4">
              <div class="avatar-section">
                <a-avatar :size="120" :src="formData.photoUrl">
                  <template #icon>
                    <user-outlined />
                  </template>
                </a-avatar>
                <a-upload
                  :custom-request="handleAvatarUpload"
                  :show-upload-list="false"
                  accept=".jpg,.jpeg,.png"
                >
                  <a-button size="small" class="avatar-upload-btn">
                    <camera-outlined />
                    更换照片
                  </a-button>
                </a-upload>
              </div>
            </a-col>
            <a-col :span="20">
              <div class="info-header">
                <div class="employee-name">{{ formData.name || '-' }}</div>
                <div class="employee-info">
                  <a-tag :color="getStatusColor(formData.status)">
                    {{ getStatusText(formData.status) }}
                  </a-tag>
                  <span class="dept-position">{{ formData.deptName || '-' }} / {{ formData.position || '-' }}</span>
                </div>
                <a-space class="action-buttons">
                  <a-button v-if="formData.status === 1" type="primary" danger @click="handleLeaveRegister">
                    离职登记
                  </a-button>
                  <a-button type="primary" @click="handleEdit">
                    编辑资料
                  </a-button>
                </a-space>
              </div>
            </a-col>
          </a-row>
          <a-row :gutter="24" style="margin-top: 24px">
            <a-col :span="24">
              <a-descriptions :column="2" bordered>
                <a-descriptions-item label="员工工号">{{ formData.code || '-' }}</a-descriptions-item>
                <a-descriptions-item label="姓名">{{ formData.name || '-' }}</a-descriptions-item>
                <a-descriptions-item label="性别">{{ getGenderText(formData.gender) }}</a-descriptions-item>
                <a-descriptions-item label="身份证号">{{ formData.idCard || '-' }}</a-descriptions-item>
                <a-descriptions-item label="出生日期">{{ formData.birthday || '-' }}</a-descriptions-item>
                <a-descriptions-item label="民族">{{ formData.nation || '-' }}</a-descriptions-item>
                <a-descriptions-item label="籍贯">{{ formData.nativePlace || '-' }}</a-descriptions-item>
                <a-descriptions-item label="政治面貌">{{ formData.politicalStatus || '-' }}</a-descriptions-item>
                <a-descriptions-item label="学历">{{ formData.education || '-' }}</a-descriptions-item>
                <a-descriptions-item label="专业">{{ formData.major || '-' }}</a-descriptions-item>
                <a-descriptions-item label="毕业院校">{{ formData.graduateSchool || '-' }}</a-descriptions-item>
                <a-descriptions-item label="毕业日期">{{ formData.graduateDate || '-' }}</a-descriptions-item>
                <a-descriptions-item label="联系电话">{{ formData.phone || '-' }}</a-descriptions-item>
                <a-descriptions-item label="电子邮箱">{{ formData.email || '-' }}</a-descriptions-item>
                <a-descriptions-item label="部门">{{ formData.deptName || '-' }}</a-descriptions-item>
                <a-descriptions-item label="职位">{{ formData.position || '-' }}</a-descriptions-item>
                <a-descriptions-item label="入职日期">{{ formData.entryDate || '-' }}</a-descriptions-item>
                <a-descriptions-item label="转正日期">{{ formData.regularDate || '-' }}</a-descriptions-item>
                <a-descriptions-item label="离职日期">{{ formData.leaveDate || '-' }}</a-descriptions-item>
                <a-descriptions-item label="离职原因">{{ formData.leaveReason || '-' }}</a-descriptions-item>
                <a-descriptions-item label="现居住地址" :span="2">{{ formData.address || '-' }}</a-descriptions-item>
                <a-descriptions-item label="紧急联系人">{{ formData.emergencyContact || '-' }}</a-descriptions-item>
                <a-descriptions-item label="紧急联系电话">{{ formData.emergencyPhone || '-' }}</a-descriptions-item>
                <a-descriptions-item label="备注" :span="2">{{ formData.description || '-' }}</a-descriptions-item>
              </a-descriptions>
            </a-col>
          </a-row>
        </a-tab-pane>

        <a-tab-pane key="certificate" tab="资质证书">
          <div class="table-operations">
            <a-button type="primary" @click="handleAddCertificate">
              <plus-outlined />
              新增证书
            </a-button>
          </div>
          <a-table
            :columns="certificateColumns"
            :data-source="certificateList"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 1 ? 'success' : 'default'">
                  {{ record.status === 1 ? '有效' : '无效' }}
                </a-tag>
                <a-tag v-if="record.expiring" color="warning">即将过期</a-tag>
              </template>
              <template v-if="column.key === 'issueDate'">
                <span>{{ record.issueDate || '-' }}</span>
              </template>
              <template v-if="column.key === 'validPeriod'">
                <span v-if="record.validEndDate">{{ record.validEndDate }}</span>
                <span v-else-if="record.validStartDate">{{ record.validStartDate }} 起长期有效</span>
                <span v-else>长期有效</span>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="handleEditCertificate(record)">编辑</a-button>
                  <a-popconfirm title="确定要删除该证书吗？" @confirm="handleDeleteCertificate(record)">
                    <a-button type="link" danger size="small">删除</a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="training" tab="培训记录">
          <a-table
            :columns="trainingColumns"
            :data-source="trainingList"
            :pagination="false"
            size="small"
          >
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="files" tab="附件资料">
          <div class="table-operations">
            <a-upload
              :custom-request="handleFileUpload"
              :show-upload-list="false"
              :multiple="true"
            >
              <a-button type="primary">
                <upload-outlined />
                上传附件
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
              <template v-if="column.key === 'fileSize'">
                <span>{{ formatFileSize(record.fileSize) }}</span>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="handleDownloadFile(record)">下载</a-button>
                  <a-popconfirm title="确定要删除该附件吗？" @confirm="handleDeleteFile(record)">
                    <a-button type="link" danger size="small">删除</a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>

      <div class="modal-footer">
        <a-button @click="handleCancel">关闭</a-button>
      </div>
    </div>

    <!-- 证书弹窗 -->
    <a-modal
      v-model:open="certificateModalVisible"
      :title="certificateModalTitle"
      @ok="handleCertificateSubmit"
      @cancel="handleCertificateCancel"
      width="700px"
    >
      <a-form :model="certificateForm" :rules="certificateRules" ref="certificateFormRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="证书类型" field="certificateType">
              <a-input v-model:value="certificateForm.certificateType" placeholder="请输入证书类型" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="证书名称" field="certificateName">
              <a-input v-model:value="certificateForm.certificateName" placeholder="请输入证书名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="证书编号" field="certificateNo">
              <a-input v-model:value="certificateForm.certificateNo" placeholder="请输入证书编号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发证机构" field="issueOrg">
              <a-input v-model:value="certificateForm.issueOrg" placeholder="请输入发证机构" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="发证日期" field="issueDate">
              <a-date-picker v-model:value="certificateForm.issueDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="有效期至" field="validEndDate">
              <a-date-picker v-model:value="certificateForm.validEndDate" style="width: 100%" value-format="YYYY-MM-DD" placeholder="长期有效则不填" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 离职登记弹窗 -->
    <a-modal
      v-model:open="leaveModalVisible"
      title="离职登记"
      @ok="handleLeaveSubmit"
      @cancel="handleLeaveCancel"
      width="600px"
    >
      <a-form :model="leaveForm" :rules="leaveRules" ref="leaveFormRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="离职日期" field="leaveDate">
          <a-date-picker v-model:value="leaveForm.leaveDate" style="width: 100%" value-format="YYYY-MM-DD" />
        </a-form-item>
        <a-form-item label="离职原因" field="leaveReason">
          <a-textarea v-model:value="leaveForm.leaveReason" :rows="4" placeholder="请输入离职原因" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import { PlusOutlined, UploadOutlined, UserOutlined, CameraOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/hr/employee';

  export default defineComponent({
    name: 'HrEmployeeDetail',
    components: {
      PlusOutlined,
      UploadOutlined,
      UserOutlined,
      CameraOutlined
    },
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    emits: ['edit', 'confirm'],
    data() {
      return {
        visible: false,
        loading: false,
        activeKey: 'basic',
        formData: {},
        certificateList: [],
        trainingList: [],
        fileList: [],
        certificateModalVisible: false,
        leaveModalVisible: false,
        leaveForm: {
          leaveDate: null,
          leaveReason: ''
        },
        leaveRules: {
          leaveDate: [{ required: true, message: '请选择离职日期', trigger: 'change' }],
          leaveReason: [{ required: true, message: '请输入离职原因', trigger: 'blur' }]
        },
        certificateModalTitle: '新增证书',
        certificateForm: {
          id: '',
          certificateType: '',
          certificateName: '',
          certificateNo: '',
          issueOrg: '',
          issueDate: null,
          validEndDate: null
        },
        certificateRules: {
          certificateType: [{ required: true, message: '请输入证书类型', trigger: 'blur' }],
          certificateName: [{ required: true, message: '请输入证书名称', trigger: 'blur' }]
        },
        certificateColumns: [
          { title: '证书类型', dataIndex: 'certificateType', key: 'certificateType', width: 120 },
          { title: '证书名称', dataIndex: 'certificateName', key: 'certificateName', width: 150 },
          { title: '证书编号', dataIndex: 'certificateNo', key: 'certificateNo', width: 150 },
          { title: '发证机构', dataIndex: 'issueOrg', key: 'issueOrg', width: 150 },
          { title: '发证日期', key: 'issueDate', width: 180 },
          { title: '有效期', key: 'validPeriod', width: 180 },
          { title: '状态', key: 'status', width: 120 },
          { title: '操作', key: 'action', width: 120 }
        ],
        trainingColumns: [
          { title: '培训名称', dataIndex: 'trainingName', key: 'trainingName', width: 150 },
          { title: '培训类型', dataIndex: 'trainingType', key: 'trainingType', width: 100 },
          { title: '培训机构', dataIndex: 'trainingOrg', key: 'trainingOrg', width: 120 },
          { title: '开始日期', dataIndex: 'startDate', key: 'startDate', width: 100 },
          { title: '结束日期', dataIndex: 'endDate', key: 'endDate', width: 100 },
          { title: '学时', dataIndex: 'trainingHours', key: 'trainingHours', width: 80 },
          { title: '培训结果', dataIndex: 'trainingResult', key: 'trainingResult', width: 80 }
        ],
        fileColumns: [
          { title: '文件名称', dataIndex: 'fileName', key: 'fileName', width: 250 },
          { title: '文件大小', dataIndex: 'fileSize', key: 'fileSize', width: 100 },
          { title: '上传人', dataIndex: 'createBy', key: 'createBy', width: 100 },
          { title: '上传时间', dataIndex: 'createTime', key: 'createTime', width: 150 },
          { title: '操作', key: 'action', width: 120 }
        ]
      };
    },
    watch: {
      id: {
        handler(newVal) {
          if (newVal && this.visible) {
            this.loadFormData();
          }
        },
        immediate: false,
      },
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.activeKey = 'basic';
        this.$nextTick(() => this.loadFormData());
      },
      handleCancel() {
        this.visible = false;
      },
      async loadFormData() {
        if (!this.id) return;

        this.loading = true;
        try {
          // 获取基本信息
          const res = await api.get(this.id);
          this.formData = res || {};
          
          // 获取资质证书
          const certificates = await api.getCertificates(this.id);
          this.certificateList = certificates || [];
          
          // 获取培训记录
          const trainings = await api.getTrainings(this.id);
          this.trainingList = trainings || [];
          
          // 获取附件资料
          const files = await api.getFiles(this.id);
          this.fileList = files || [];
        } catch (error) {
          this.$message.error('获取员工详情失败');
        } finally {
          this.loading = false;
        }
      },
      getStatusColor(status) {
        const colors = { 0: 'default', 1: 'success', 2: 'processing' };
        return colors[status] || 'default';
      },
      getStatusText(status) {
        const texts = { 0: '离职', 1: '在职', 2: '试用期' };
        return texts[status] || '';
      },
      getGenderText(gender) {
        const texts = { 1: '男', 2: '女' };
        return texts[gender] || '-';
      },
      formatFileSize(size) {
        if (!size) return '-';
        if (size < 1024) return size + ' B';
        if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB';
        return (size / (1024 * 1024)).toFixed(2) + ' MB';
      },
      handleAddCertificate() {
        this.certificateModalTitle = '新增证书';
        this.certificateForm = { id: '', certificateType: '', certificateName: '', certificateNo: '', issueOrg: '', issueDate: null, validEndDate: null };
        this.certificateModalVisible = true;
      },
      handleEditCertificate(record) {
        this.certificateModalTitle = '编辑证书';
        this.certificateForm = { ...record };
        this.certificateModalVisible = true;
      },
      async handleCertificateSubmit() {
        const valid = await this.$refs.certificateFormRef?.validate();
        if (!valid) return;
        
        try {
          const formData = { ...this.certificateForm, employeeId: this.id };
          if (formData.id) {
            // 更新证书
            await api.updateCertificate(formData);
          } else {
            // 新增证书
            await api.addCertificate(formData);
          }
          this.certificateModalVisible = false;
          this.$message.success('保存成功');
          // 重新加载证书列表
          this.loadFormData();
        } catch (error) {
          this.$message.error('保存失败');
        }
      },
      handleCertificateCancel() {
        this.certificateModalVisible = false;
      },
      async handleDeleteCertificate(record) {
        try {
          await api.deleteCertificate(record.id);
          this.$message.success('删除成功');
          // 重新加载证书列表
          this.loadFormData();
        } catch (error) {
          this.$message.error('删除失败');
        }
      },
      async handleFileUpload(options) {
        const { file, onSuccess, onError } = options;
        
        try {
          // 获取真实的文件对象
          const realFile = file.originFileObj || file;
          await api.uploadFile(this.id, realFile);
          this.$message.success('上传成功');
          // 重新加载文件列表
          this.loadFormData();
          onSuccess?.();
        } catch (error) {
          this.$message.error('上传失败');
          onError?.(error);
        }
      },
      handleDownloadFile(record) {
        if (record.fileUrl) {
          window.open(record.fileUrl, '_blank');
        }
      },
      async handleDeleteFile(record) {
        try {
          await api.deleteFile(record.id);
          this.$message.success('删除成功');
          // 重新加载文件列表
          this.loadFormData();
        } catch (error) {
          this.$message.error('删除失败');
        }
      },
      async handleAvatarUpload(options) {
        const { file, onSuccess, onError } = options;
        
        try {
          // 调试：打印 file 对象结构
          console.log('File object:', file);
          console.log('Has originFileObj:', !!file.originFileObj);
          
          // 获取真实的文件对象
          const realFile = file.originFileObj || file;
          console.log('Real file:', realFile);
          
          const photoUrl = await api.uploadPhoto(this.id, realFile);
          this.formData.photoUrl = photoUrl;
          this.$message.success('头像上传成功');
          onSuccess?.(photoUrl);
        } catch (error) {
          console.error('Upload error:', error);
          this.$message.error('头像上传失败');
          onError?.(error);
        }
      },
      handleLeaveRegister() {
        this.leaveForm = { leaveDate: null, leaveReason: '' };
        this.leaveModalVisible = true;
      },
      async handleLeaveSubmit() {
        const valid = await this.$refs.leaveFormRef?.validate();
        if (!valid) return;
        
        try {
          await api.updateLeaveInfo({
            id: this.id,
            leaveDate: this.leaveForm.leaveDate,
            leaveReason: this.leaveForm.leaveReason
          });
          this.formData.leaveDate = this.leaveForm.leaveDate;
          this.formData.leaveReason = this.leaveForm.leaveReason;
          this.leaveModalVisible = false;
          this.$message.success('离职登记成功');
        } catch (error) {
          // 错误已在拦截器处理
        }
      },
      handleLeaveCancel() {
        this.leaveModalVisible = false;
      },
      handleEdit() {
        this.$emit('edit', this.formData);
      },
      // 刷新数据
      refresh() {
        this.loadFormData();
      }
    },
  });
</script>

<style scoped>
  .modal-footer {
    margin-top: 24px;
    text-align: right;
  }

  .table-operations {
    margin-bottom: 16px;
  }

  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 16px;
  }

  .avatar-upload-btn {
    margin-top: 8px;
  }

  .info-header {
    padding: 16px 0;
  }

  .employee-name {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  .employee-info {
    margin-bottom: 16px;
  }

  .dept-position {
    margin-left: 8px;
    color: #666;
  }

  .action-buttons {
    margin-top: 16px;
  }
</style>
