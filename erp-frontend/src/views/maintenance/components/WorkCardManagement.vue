<template>
  <a-modal
    :open="visible"
    title="工卡管理"
    :width="800"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="work-card-container">
      <!-- 工具栏 -->
      <div class="work-card-toolbar">
        <a-space>
          <a-button type="primary" @click="handleAddWorkCard">
            <plus-outlined /> 添加工卡
          </a-button>
        </a-space>
      </div>
      
      <!-- 工卡列表 -->
      <a-table
        :dataSource="workCardList"
        :columns="columns"
        :rowKey="record => record.id"
        :pagination="{ pageSize: 10 }"
        :loading="loading"
        bordered
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除此工卡吗?"
                @confirm="confirmDelete(record)"
                ok-text="是"
                cancel-text="否"
              >
                <a>删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
    
    <!-- 工卡选择弹窗 -->
    <a-modal
      v-model:open="workCardSelectVisible"
      title="选择工卡"
      @ok="handleWorkCardSelectSubmit"
      :confirmLoading="workCardSelectLoading"
      :width="800"
    >
      <!-- 搜索区域 -->
      <div class="work-card-search" style="margin-bottom: 16px;">
        <a-form layout="inline">
          <a-form-item label="工卡号">
            <a-input v-model:value="searchForm.code" placeholder="请输入工卡号" allowClear />
          </a-form-item>
          <a-form-item label="工卡名称">
            <a-input v-model:value="searchForm.name" placeholder="请输入工卡名称" allowClear />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="loadWorkCardOptions">搜索</a-button>
            <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
          </a-form-item>
        </a-form>
      </div>
      
      <!-- 工卡选择表格 -->
      <a-table
        :dataSource="workCardOptions"
        :columns="selectColumns"
        :rowKey="record => record.id"
        :pagination="{ pageSize: 10 }"
        :loading="selectLoading"
        :rowSelection="{ selectedRowKeys: selectedWorkCardIds, onChange: onWorkCardSelectChange }"
        bordered
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'code'">
            {{ record.code }}
          </template>
          <template v-if="column.key === 'name'">
            {{ record.name }}
          </template>
          <template v-if="column.key === 'machineTypeName'">
            {{ record.machineTypeName }}
          </template>
          <template v-if="column.key === 'partNumberName'">
            {{ record.partNumberName }}
          </template>
          <template v-if="column.key === 'repairTypeName'">
            {{ record.repairTypeName }}
          </template>
        </template>
      </a-table>
    </a-modal>
  </a-modal>
</template>

<script>
import { defineComponent } from 'vue';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { workCardApi } from '@/api/work-card/index';
import { contractTaskWorkCardApi } from '@/api/contract-task/work-card';

export default defineComponent({
  name: 'WorkCardManagement',
  components: {
    PlusOutlined,
    DeleteOutlined
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    tasks: {
      type: Array,
      default: () => []
    },
    taskId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // 是否使用模拟数据
      useMockData: false,
      loading: false,
      // 工卡列表
      workCardList: [],
      // 表格列定义
      columns: [
        {
          title: '工卡号',
          dataIndex: 'workCardNumber',
          key: 'workCardNumber',
          width: 120
        },
        {
          title: '工卡名称',
          dataIndex: 'workCardName',
          key: 'workCardName',
          width: 150
        },
        {
          title: '机型',
          dataIndex: 'machineTypeName',
          key: 'machineTypeName',
          width: 100
        },
        {
          title: '件号',
          dataIndex: 'partNumberName',
          key: 'partNumberName',
          width: 120
        },
        {
          title: '维修类型',
          dataIndex: 'repairTypeName',
          key: 'repairTypeName',
          width: 100
        },
        {
          title: '备注',
          dataIndex: 'remark',
          key: 'remark',
          ellipsis: true
        },
        {
          title: '操作',
          key: 'action',
          width: 120,
          fixed: 'right'
        }
      ],
      // 选中的行
      selectedRowKeys: [],
      // 工卡表单
      workCardFormVisible: false,
      workCardFormLoading: false,
      isEdit: false,
      workCardForm: {
        id: '',
        workCardNumber: '',
        workCardName: '',
        machineTypeId: undefined,
        partNumberId: undefined,
        repairTypeId: undefined,
        remark: ''
      },
      // 选项数据
      machineTypeOptions: [],
      partNumberOptions: [],
      repairTypeOptions: [],
      
      // 工卡选择相关
      workCardSelectVisible: false,
      workCardSelectLoading: false,
      workCardOptions: [],
      selectedWorkCardIds: [],
      selectLoading: false,
      // 工卡选择表格列定义
      selectColumns: [
        {
          title: '工卡号',
          dataIndex: 'code',
          key: 'code',
          width: 120
        },
        {
          title: '工卡名称',
          dataIndex: 'name',
          key: 'name',
          width: 150
        },
        {
          title: '机型',
          dataIndex: 'machineTypeName',
          key: 'machineTypeName',
          width: 100
        },
        {
          title: '件号名称',
          dataIndex: 'partNumberName',
          key: 'partNumberName',
          width: 120
        },
        {
          title: '维修类型',
          dataIndex: 'repairTypeName',
          key: 'repairTypeName',
          width: 100
        }
      ],
      // 搜索表单
      searchForm: {
        code: '',
        name: '',
        pageIndex: 1,
        pageSize: 10
      }
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.loadWorkCards();
        this.loadOptions();
      }
    }
  },
  methods: {
    // 加载工卡列表
    loadWorkCards() {
      if (!this.taskId) {
        this.$message.warning('任务ID不能为空');
        return;
      }
      
      this.loading = true;
      
      if (this.useMockData) {
        // 使用模拟数据
        setTimeout(() => {
          // 根据任务生成模拟工卡数据
          this.workCardList = this.generateMockWorkCards();
          this.loading = false;
        }, 500);
      } else {
        // 使用真实接口数据
        contractTaskWorkCardApi.getWorkCards(this.taskId)
          .then(res => {
            // 转换接口返回的数据格式
            this.workCardList = (res || []).map(item => ({
              id: item.workCardId,
              workCardNumber: item.workCardCode,
              workCardName: item.workCardName,
              machineTypeId: item.machineTypeId,
              machineTypeName: item.machineTypeName,
              partNumberId: item.partNumberId,
              partNumberName: item.partNumberName,
              repairTypeId: item.repairTypeId,
              repairTypeName: item.repairTypeName
            }));
            
            this.loading = false;
          })
          .catch(() => {
            this.loading = false;
          });
      }
    },
    
    // 生成模拟工卡数据
    generateMockWorkCards() {
      const workCards = [];
      
      // 为每个任务生成1-3个工卡
      this.tasks.forEach(task => {
        const count = Math.floor(Math.random() * 3) + 1;
        
        for (let i = 1; i <= count; i++) {
          workCards.push({
            id: `wc-${task.id}-${i}`,
            workCardNumber: `WC-${task.contractCode}-${i}`,
            workCardName: `${task.repairTypesLabel}工卡${i}`,
            machineTypeName: task.machineTypeName,
            machineTypeId: `mt-${task.machineTypeCode}`,
            partNumberName: task.partNumberName,
            partNumberId: `pn-${task.partNumberCode}`,
            repairTypeName: task.repairTypesLabel.split(',')[0],
            repairTypeId: `rt-${task.repairTypesLabel.split(',')[0]}`,
            taskId: task.id,
            remark: `${task.contractCode}的工卡${i}`
          });
        }
      });
      
      return workCards;
    },
    
    // 加载选项数据
    loadOptions() {
      // 模拟机型选项
      this.machineTypeOptions = Array.from(new Set(this.tasks.map(task => task.aircraftType)))
        .map(type => ({
          label: type,
          value: `mt-${type}`
        }));
      
      // 模拟件号选项
      this.partNumberOptions = Array.from(new Set(this.tasks.map(task => task.partNumber)))
        .map(part => ({
          label: part,
          value: `pn-${part}`
        }));
      
      // 模拟维修类型选项
      const repairTypes = [];
      this.tasks.forEach(task => {
        if (task.repairType) {
          task.repairType.split(',').forEach(type => {
            repairTypes.push(type.trim());
          });
        }
      });
      
      this.repairTypeOptions = Array.from(new Set(repairTypes))
        .map(type => ({
          label: type,
          value: `rt-${type}`
        }));
    },
    
    // 选择变更
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },
    
    // 添加工卡
    handleAddWorkCard() {
      this.selectedWorkCardIds = [];
      this.searchForm = {
        code: '',
        name: '',
        pageIndex: 1,
        pageSize: 10
      };
      this.loadWorkCardOptions();
      this.workCardSelectVisible = true;
    },
    
    // 加载工卡选项
    loadWorkCardOptions() {
      this.selectLoading = true;
      
      if (this.useMockData) {
        // 使用模拟数据
        setTimeout(() => {
          // 生成模拟工卡数据
          const mockData = this.generateMockWorkCardOptions();
          
          // 根据搜索条件筛选
          this.workCardOptions = mockData.filter(item => {
            const codeMatch = !this.searchForm.code || item.code.toLowerCase().includes(this.searchForm.code.toLowerCase());
            const nameMatch = !this.searchForm.name || item.name.toLowerCase().includes(this.searchForm.name.toLowerCase());
            return codeMatch && nameMatch;
          });
          
          this.selectLoading = false;
        }, 500);
      } else {
        // 使用真实接口数据
        const params = {
          code: this.searchForm.code || undefined,
          name: this.searchForm.name || undefined,
          pageIndex: 1,
          pageSize: 20
        };
        
        workCardApi.query(params).then(res => {
          // 转换接口返回的数据格式
          this.workCardOptions = (res.datas || []).map(item => ({
            id: item.id,
            code: item.code,
            name: item.name,
            machineTypeId: item.machineTypeId,
            machineTypeName: item.machineTypeName,
            partNumberId: item.partNumberId,
            partNumberName: item.partNumberName,
            repairTypeId: item.repairTypeId,
            repairTypeName: item.repairTypeName,
            description: item.description
          }));
          
          this.selectLoading = false;
        }).catch(() => {
          this.selectLoading = false;
        });
      }
    },
    
    // 生成模拟工卡选项数据
    generateMockWorkCardOptions() {
      const machineTypes = ['波音737', '空客A320', '波音777', '空客A350', '庞巴迪CRJ'];
      const repairTypes = ['定检', '故障修复', '改装', '大修', '日常维护'];
      
      return Array.from({ length: 20 }, (_, i) => {
        const id = `wc-${i + 1}`;
        const machineTypeIndex = Math.floor(Math.random() * machineTypes.length);
        const repairTypeIndex = Math.floor(Math.random() * repairTypes.length);
        
        return {
          id,
          code: `WC${String(i + 1).padStart(4, '0')}`,
          name: `${machineTypes[machineTypeIndex]}${repairTypes[repairTypeIndex]}工卡`,
          machineTypeId: `mt-${machineTypeIndex + 1}`,
          machineTypeName: machineTypes[machineTypeIndex],
          partNumberId: `pn-${i + 1}`,
          partNumberName: `PN-${String(i + 1).padStart(4, '0')}`,
          repairTypeId: `rt-${repairTypeIndex + 1}`,
          repairTypeName: repairTypes[repairTypeIndex],
          description: `这是工卡${i + 1}的描述信息`
        };
      });
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        code: '',
        name: '',
        pageIndex: 1,
        pageSize: 10
      };
      this.loadWorkCardOptions();
    },
    
    // 工卡选择变更
    onWorkCardSelectChange(selectedRowKeys) {
      this.selectedWorkCardIds = selectedRowKeys;
    },
    
    // 提交工卡选择
    handleWorkCardSelectSubmit() {
      if (this.selectedWorkCardIds.length === 0) {
        this.$message.warning('请至少选择一个工卡');
        return;
      }
      
      if (!this.taskId) {
        this.$message.warning('任务ID不能为空');
        return;
      }
      
      this.workCardSelectLoading = true;
      
      if (this.useMockData) {
        // 使用模拟数据
        setTimeout(() => {
          // 从选项中获取选中的工卡
          const selectedWorkCards = this.workCardOptions.filter(item => 
            this.selectedWorkCardIds.includes(item.id)
          );
          
          // 将选中的工卡数据转换为表格所需格式
          const newWorkCards = selectedWorkCards.map(item => ({
            id: item.id,
            workCardNumber: item.code,
            workCardName: item.name,
            machineTypeId: item.machineTypeId,
            machineTypeName: item.machineTypeName,
            partNumberId: item.partNumberId,
            partNumberName: item.partNumberName,
            repairTypeId: item.repairTypeId,
            repairTypeName: item.repairTypeName,
            remark: item.description
          }));
          
          // 检查是否有重复添加的工卡
          const existingIds = this.workCardList.map(item => item.id);
          const uniqueWorkCards = newWorkCards.filter(item => !existingIds.includes(item.id));
          
          if (uniqueWorkCards.length === 0) {
            this.$message.warning('所选工卡已全部添加');
          } else {
            // 添加到工卡列表
            this.workCardList = [...uniqueWorkCards, ...this.workCardList];
            this.$message.success(`成功添加${uniqueWorkCards.length}个工卡`);
          }
          
          this.workCardSelectLoading = false;
          this.workCardSelectVisible = false;
        }, 500);
      } else {
        // 使用真实接口数据
        // 构造请求参数
        const params = {
          taskId: this.taskId,
          workCardIds: this.selectedWorkCardIds
        };
        
        // 调用批量添加接口
        contractTaskWorkCardApi.batchAddWorkCards(params)
          .then(() => {
            this.$message.success('工卡添加成功');
            // 重新加载工卡列表
            this.loadWorkCards();
            this.workCardSelectLoading = false;
            this.workCardSelectVisible = false;
          })
          .catch(() => {
            this.workCardSelectLoading = false;
          });
      }
    },
    
    // 编辑工卡
    handleEditWorkCard(record) {
      this.isEdit = true;
      this.workCardForm = { ...record };
      this.workCardFormVisible = true;
    },
    
    // 删除工卡
    handleDeleteWorkCard() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要删除的工卡');
        return;
      }
      
      this.$confirm({
        title: '确定要删除选中的工卡吗?',
        content: '删除后无法恢复',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          // 模拟删除
          this.workCardList = this.workCardList.filter(
            item => !this.selectedRowKeys.includes(item.id)
          );
          this.selectedRowKeys = [];
          this.$message.success('删除成功');
        }
      });
    },
    
    // 确认删除单个工卡
    confirmDelete(record) {
      if (!this.taskId) {
        this.$message.warning('任务ID不能为空');
        return;
      }
      
      if (this.useMockData) {
        // 使用模拟数据
        this.workCardList = this.workCardList.filter(item => item.id !== record.id);
        this.$message.success('删除成功');
      } else {
        // 使用真实接口数据
        // 构造请求参数
        const params = {
          taskId: this.taskId,
          workCardIds: [record.id]
        };
        
        // 调用批量删除接口
        contractTaskWorkCardApi.batchDeleteWorkCards(params)
          .then(() => {
            this.$message.success('工卡删除成功');
            // 重新加载工卡列表
            this.loadWorkCards();
          });
      }
    },
    
    // 提交工卡表单
    handleWorkCardFormSubmit() {
      // 表单验证
      if (!this.workCardForm.workCardNumber) {
        this.$message.warning('请输入工卡号');
        return;
      }
      
      if (!this.workCardForm.workCardName) {
        this.$message.warning('请输入工卡名称');
        return;
      }
      
      this.workCardFormLoading = true;
      
      // 模拟提交
      setTimeout(() => {
        if (this.isEdit) {
          // 编辑现有工卡
          const index = this.workCardList.findIndex(item => item.id === this.workCardForm.id);
          if (index !== -1) {
            // 获取选项的显示名称
            const machineType = this.machineTypeOptions.find(item => item.value === this.workCardForm.machineTypeId);
            const partNumber = this.partNumberOptions.find(item => item.value === this.workCardForm.partNumberId);
            const repairType = this.repairTypeOptions.find(item => item.value === this.workCardForm.repairTypeId);
            
            this.workCardList[index] = {
              ...this.workCardForm,
              machineTypeName: machineType ? machineType.label : '',
              partNumberName: partNumber ? partNumber.label : '',
              repairTypeName: repairType ? repairType.label : ''
            };
          }
        } else {
          // 添加新工卡
          // 获取选项的显示名称
          const machineType = this.machineTypeOptions.find(item => item.value === this.workCardForm.machineTypeId);
          const partNumber = this.partNumberOptions.find(item => item.value === this.workCardForm.partNumberId);
          const repairType = this.repairTypeOptions.find(item => item.value === this.workCardForm.repairTypeId);
          
          const newWorkCard = {
            ...this.workCardForm,
            id: `wc-new-${Date.now()}`,
            machineTypeName: machineType ? machineType.label : '',
            partNumberName: partNumber ? partNumber.label : '',
            repairTypeName: repairType ? repairType.label : ''
          };
          
          this.workCardList.unshift(newWorkCard);
        }
        
        this.workCardFormLoading = false;
        this.workCardFormVisible = false;
        this.$message.success(this.isEdit ? '工卡更新成功' : '工卡添加成功');
      }, 500);
    },
    
    // 关闭并提交结果
    close() {
      this.$emit('update:visible', false);
      this.$emit('confirm', this.workCardList);
    },
    
    // 处理取消
    handleCancel() {
      this.$emit('update:visible', false);
    }
  }
});
</script>

<style scoped>
.work-card-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.work-card-toolbar {
  margin-bottom: 16px;
}
</style>
