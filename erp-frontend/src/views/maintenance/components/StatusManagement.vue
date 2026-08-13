<template>
  <a-modal
    :open="visible"
    title="状态管理"
    @ok="handleConfirm"
    @cancel="handleCancel"
    :confirmLoading="confirmLoading"
    width="700px"
  >
    <a-row :gutter="16">
      <!-- 左侧表单 -->
      <a-col :span="12">
        <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="维修状态">
            <a-select v-model:value="form.repairStatus">
              <a-select-option value="待检查">待检查</a-select-option>
              <a-select-option value="检查中">检查中</a-select-option>
              <a-select-option value="维修中">维修中</a-select-option>
              <a-select-option value="等料暂停">等料暂停</a-select-option>
              <a-select-option value="其他暂停">其他暂停</a-select-option>
              <a-select-option value="待装配">待装配</a-select-option>
              <a-select-option value="测试中">测试中</a-select-option>
              <a-select-option value="完工">完工</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="备注">
            <a-textarea v-model:value="form.remark" :rows="4" placeholder="请输入状态变更原因或其他备注信息" />
          </a-form-item>
        </a-form>
        <div v-if="form.selectedTasks.length > 0">
          <div class="selected-tasks-title">选中的任务：</div>
          <div class="selected-tasks-list">
            <div v-for="(task, index) in form.selectedTasks" :key="index" class="selected-task-item">
              {{ task.contractCode }} - {{ task.customerName }}
            </div>
          </div>
        </div>
      </a-col>
      
      <!-- 右侧时间轴 -->
      <a-col :span="12">
        <div class="status-history-title">维修状态历史记录：</div>
        <div class="status-history-container">
          <a-spin :spinning="historyLoading">
            <div v-if="statusRecords.length > 0">
              <a-timeline>
                <a-timeline-item v-for="(record, index) in statusRecords" :key="index" :color="getStatusColor(record.repairStatusName)">
                  <template #dot>
                    <a-badge :status="getStatusBadge(record.repairStatusName)" />
                  </template>
                  <div class="timeline-content">
                    <div class="timeline-header">
                      <span class="status-name">{{ record.repairStatusName }}</span>
                      <span class="status-time">{{ formatTime(record.createTime) }}</span>
                    </div>
                    <div class="status-description" v-if="record.description">{{ record.description }}</div>
                    <div class="status-creator">操作人：{{ record.createBy }}</div>
                  </div>
                </a-timeline-item>
              </a-timeline>
            </div>
            <a-empty v-else description="暂无维修状态记录" />
          </a-spin>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { getRepairStatusRecords } from '@/api/maintenance/contract-task';
import dayjs from 'dayjs';

export default defineComponent({
  name: 'StatusManagement',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    confirmLoading: {
      type: Boolean,
      default: false
    },
    selectedTasks: {
      type: Array,
      default: () => []
    },
    defaultStatus: {
      type: String,
      default: '待检查'
    }
  },
  data() {
    return {
      form: {
        repairStatus: this.defaultStatus,
        remark: '',
        selectedTasks: []
      },
      statusRecords: [], // 维修状态记录列表
      historyLoading: false, // 记录加载状态
      currentTaskId: '' // 当前查看的任务ID
    };
  },
  watch: {
    visible(val) {
      if (val) {
        // 当弹窗显示时，初始化表单数据
        this.form.repairStatus = this.defaultStatus;
        this.form.remark = '';
        this.form.selectedTasks = [...this.selectedTasks];
        
        // 弹窗打开时强制重新加载维修状态记录，即使是同一个任务
        if (this.selectedTasks.length > 0) {
          // 重置当前任务ID，确保重新加载数据
          this.currentTaskId = '';
          this.loadStatusRecords(this.selectedTasks[0].id);
        }
      }
    },
    selectedTasks: {
      handler(val) {
        this.form.selectedTasks = [...val];
        
        // 如果选中了任务且弹窗可见，加载维修状态记录
        if (val.length > 0 && this.visible) {
          this.loadStatusRecords(val[0].id);
        }
      },
      deep: true
    },
    defaultStatus(val) {
      this.form.repairStatus = val;
    }
  },
  methods: {
    // 加载维修状态记录
    async loadStatusRecords(taskId) {
      if (!taskId) {
        return;
      }
      
      this.historyLoading = true;
      this.currentTaskId = taskId;
      
      try {
        // 调用API获取维修状态记录
        const records = await getRepairStatusRecords(taskId);
        this.statusRecords = records || [];
      } catch (error) {
        console.error('获取维修状态记录失败:', error);
        this.$message.error(`获取维修状态记录失败: ${error.message || '未知错误'}`);
        this.statusRecords = [];
      } finally {
        this.historyLoading = false;
      }
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        '待检查': 'blue',
        '检查中': 'processing',
        '待装配': 'processing',
        '维修中': 'processing',
        '等料暂停': 'warning',
        '其他暂停': 'warning',
        '测试中': 'processing',
        '完工': 'success'
      };
      
      return colorMap[status] || 'blue';
    },
    
    // 获取状态徽章类型
    getStatusBadge(status) {
      const badgeMap = {
        '待检查': 'default',
        '检查中': 'processing',
        '待装配': 'processing',
        '维修中': 'processing',
        '等料暂停': 'warning',
        '其他暂停': 'warning',
        '测试中': 'processing',
        '完工': 'success'
      };
      
      return badgeMap[status] || 'default';
    },
    
    // 处理确认
    handleConfirm() {
      if (!this.form.repairStatus) {
        this.$message.warning('请选择执行状态');
        return;
      }
      
      this.$emit('confirm', {
        repairStatus: this.form.repairStatus,
        remark: this.form.remark
      });
    },
    
    // 处理取消
    handleCancel() {
      this.$emit('update:visible', false);
    }
  }
});
</script>

<style scoped>
.selected-tasks-title,
.status-history-title {
  font-weight: bold;
  margin-top: 16px;
  margin-bottom: 8px;
}

.selected-tasks-list {
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 8px;
  background-color: #fafafa;
}

.selected-task-item {
  padding: 4px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.selected-task-item:last-child {
  border-bottom: none;
}

.status-history-container {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 8px 16px;
  background-color: #fafafa;
}

.timeline-content {
  padding: 4px 0;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.status-name {
  font-weight: bold;
  font-size: 14px;
}

.status-time {
  font-size: 12px;
  color: #999;
}

.status-description {
  margin: 4px 0;
  color: #333;
  white-space: pre-line;
}

.status-creator {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
