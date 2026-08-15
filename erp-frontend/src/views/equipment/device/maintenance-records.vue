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
        <!-- 设备信息 -->
        <a-card title="设备信息" style="margin-bottom: 16px">
          <a-descriptions :column="3" bordered>
            <template v-if="deviceInfo">
              <a-descriptions-item label="设备编号">{{ deviceInfo.code || '-' }}</a-descriptions-item>
              <a-descriptions-item label="设备名称">{{ deviceInfo.name || '-' }}</a-descriptions-item>
              <a-descriptions-item label="管理区域">{{ deviceInfo.managementArea || '-' }}</a-descriptions-item>
              <a-descriptions-item label="维保项目">{{ deviceInfo.maintenanceProject || '-' }}</a-descriptions-item>
              <a-descriptions-item label="维保间隔">{{ deviceInfo.maintenanceInterval ? `${deviceInfo.maintenanceInterval}天` : '-' }}</a-descriptions-item>
              <a-descriptions-item label="维保工卡">{{ deviceInfo.maintenanceCard || '-' }}</a-descriptions-item>
            </template>
            <template v-else>
              <a-descriptions-item label="设备编号">-</a-descriptions-item>
              <a-descriptions-item label="设备名称">-</a-descriptions-item>
              <a-descriptions-item label="管理区域">-</a-descriptions-item>
              <a-descriptions-item label="维保项目">-</a-descriptions-item>
              <a-descriptions-item label="维保间隔">-</a-descriptions-item>
              <a-descriptions-item label="维保工卡">-</a-descriptions-item>
            </template>
          </a-descriptions>
        </a-card>

        <!-- 工具栏 -->
        <div style="margin-bottom: 16px">
          <a-space>
            <a-button
              v-permission="['equipment:device']"
              type="primary"
              :icon="h(PlusOutlined)"
              @click="openAddDialog"
            >
              新增维保记录
            </a-button>
          </a-space>
        </div>

        <!-- 维保记录列表 -->
        <a-table
          :loading="loading"
          :columns="columns"
          :data-source="records"
          :pagination="pagination"
          @change="handleTableChange"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
              <a-space>
                <a v-permission="['equipment:device']" @click="openEditDialog(record)">修改</a>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="确定要删除该维保记录吗？"
                  @confirm="handleDelete(record.id)"
                  ok-text="确定"
                  cancel-text="取消"
                >
                  <a v-permission="['equipment:device']" class="danger-text">删除</a>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>

    <!-- 新增维保记录对话框 -->
    <a-modal
      v-model:open="addDialogVisible"
      title="新增维保记录"
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
        <a-form-item label="设备" name="deviceId">
          <a-select v-model:value="formData.deviceId" placeholder="请选择设备" disabled>
            <a-select-option :value="formData.deviceId">
              {{ deviceInfo && deviceInfo.code ? `${deviceInfo.code} - ${deviceInfo.name}` : '加载中...' }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维保人" name="maintenancenUser">
          <a-input v-model:value="formData.maintenancenUser" placeholder="请输入维保人" />
        </a-form-item>
        <a-form-item label="维保时间" name="maintenanceTime">
          <a-date-picker
            v-model:value="formData.maintenanceTime"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改维保记录对话框 -->
    <a-modal
      v-model:open="editDialogVisible"
      title="修改维保记录"
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
        <a-form-item label="设备" name="deviceId">
          <a-select v-model:value="formData.deviceId" placeholder="请选择设备" disabled>
            <a-select-option :value="formData.deviceId">
              {{ deviceInfo && deviceInfo.code ? `${deviceInfo.code} - ${deviceInfo.name}` : '加载中...' }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维保人" name="maintenancenUser">
          <a-input v-model:value="formData.maintenancenUser" placeholder="请输入维保人" />
        </a-form-item>
        <a-form-item label="维保时间" name="maintenanceTime">
          <a-date-picker
            v-model:value="formData.maintenanceTime"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="formData.description" :rows="4" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, h, ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import * as api from '@/api/equipment';

export default defineComponent({
  name: 'MaintenanceRecords',
  props: {
    deviceIds: {
      type: Array,
      required: true
    }
  },
  components: {},
  setup(props, { emit }) {
    // 表格列定义
    const columns = [
      {
        title: '维保人',
        dataIndex: 'maintenancenUser',
        key: 'maintenancenUser',
      },
      {
        title: '维保时间',
        dataIndex: 'maintenanceTime',
        key: 'maintenanceTime',
        sorter: true
      },
      {
        title: '备注',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: '操作',
        key: 'action'
      }
    ];

    // 表单验证规则
    const rules = {
      maintenancenUser: [
        { required: true, message: '请输入维保人', trigger: 'blur' }
      ],
      maintenanceTime: [
        { required: true, message: '请选择维保时间', trigger: 'change' }
      ]
    };

    return {
      h,
      PlusOutlined,
      columns,
      rules
    };
  },
  data() {
    return {
      visible: false,
      loading: false,
      submitLoading: false,
      addDialogVisible: false,
      editDialogVisible: false,
      title: '设备维保记录管理',
      deviceInfo: {
        id: '',
        code: '',
        name: '',
        managementArea: '',
        maintenanceProject: '',
        maintenanceInterval: '',
        maintenanceCard: ''
      }, // 当前设备信息
      records: [], // 维保记录列表
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 条`
      },
      formData: {
        id: '',
        deviceId: '',
        maintenancenUser: '',
        maintenanceTime: '',
        description: ''
      },
      queryParams: {
        deviceId: '',
        pageIndex: 1,
        pageSize: 10
      }
    };
  },
  methods: {
    // 打开对话框
    openDialog() {
      this.visible = true;
      
      // 初始化设备信息
      this.deviceInfo = {
        id: '',
        code: '',
        name: '',
        managementArea: '',
        maintenanceProject: '',
        maintenanceInterval: '',
        maintenanceCard: ''
      };
      
      if (this.deviceIds && this.deviceIds.length === 1) {
        this.loadDeviceInfo(this.deviceIds[0]);
      } else if (this.deviceIds && this.deviceIds.length > 1) {
        this.deviceInfo = {
          id: this.deviceIds[0], // 使用第一个设备ID
          code: `已选择 ${this.deviceIds.length} 个设备`,
          name: '批量管理',
          managementArea: '-',
          maintenanceProject: '-',
          maintenanceInterval: '-',
          maintenanceCard: '-'
        };
      }
      
      this.loadRecords();
    },

    // 加载设备信息
    async loadDeviceInfo(deviceId) {
      if (!deviceId) return;
      
      try {
        const data = await api.get(deviceId);
        if (data) {
          this.deviceInfo = data;
        }
      } catch (error) {
        console.error('加载设备信息失败', error);
        message.error('加载设备信息失败');
      }
    },

    // 加载维保记录
    async loadRecords() {
      if (!this.deviceIds || this.deviceIds.length === 0) {
        this.records = [];
        return;
      }
      
      this.loading = true;
      try {
        const params = {
          ...this.queryParams,
          deviceId: this.deviceIds.length === 1 ? this.deviceIds[0] : this.deviceIds[0] // 如果多选，默认显示第一个设备的记录
        };
        const res = await api.queryDeviceRecords(params);
        if (res) {
          this.records = res.datas || [];
          this.pagination.total = res.totalCount || 0;
          this.pagination.current = params.pageIndex;
          this.pagination.pageSize = params.pageSize;
        }
      } catch (error) {
        console.error('加载维保记录失败', error);
        message.error('加载维保记录失败');
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
      if (!this.deviceInfo || !this.deviceInfo.id) {
        message.warning('请先选择设备');
        return;
      }
      
      this.formData = {
        deviceId: this.deviceInfo.id,
        maintenancenUser: '',
        maintenanceTime: '',
        description: ''
      };
      this.addDialogVisible = true;
    },

    // 打开编辑对话框
    openEditDialog(record) {
      if (!record) return;
      
      this.formData = {
        id: record.id,
        deviceId: record.deviceId,
        maintenancenUser: record.maintenancenUser,
        maintenanceTime: record.maintenanceTime,
        description: record.description
      };
      this.editDialogVisible = true;
    },

    // 提交新增
    handleAddSubmit() {
      this.$refs.addForm.validate().then(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          try {
            // 创建表单数据的副本，避免修改原始数据
            const formData = { ...this.formData };
            
            await api.createDeviceRecord(formData);
            message.success('新增维保记录成功');
            this.addDialogVisible = false;
            this.loadRecords();
            this.$emit('confirm');
          } catch (error) {
            console.error('新增维保记录失败', error);
            message.error('新增维保记录失败');
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
            const formData = { ...this.formData };
            
            await api.updateDeviceRecord(formData);
            message.success('修改维保记录成功');
            this.editDialogVisible = false;
            this.loadRecords();
            this.$emit('confirm');
          } catch (error) {
            console.error('修改维保记录失败', error);
            message.error('修改维保记录失败');
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },

    // 删除维保记录
    async handleDelete(id) {
      if (!id) return;
      
      try {
        await api.deleteDeviceRecord(id);
        message.success('删除维保记录成功');
        this.loadRecords();
        this.$emit('confirm');
      } catch (error) {
        console.error('删除维保记录失败', error);
        message.error('删除维保记录失败');
      }
    }
  }
});
</script>

<style scoped>
.danger-text {
  color: #ff4d4f;
}
</style>
