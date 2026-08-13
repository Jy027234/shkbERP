<template>
  <PageWrapper>
    <div class="maintenance-dashboard">
      <!-- 模拟数据切换按钮 -->
      <div v-if="false" class="mock-data-switch">
        <a-switch
          v-model:checked="useMockData"
          :checkedChildren="'使用模拟数据'"
          :unCheckedChildren="'使用真实数据'"
          @change="handleMockDataChange"
        />
      </div>    
      <!-- 民航维修工单 -->
      <a-card title="民航维修工单" :bordered="false" class="dashboard-card">
        <a-row :gutter="[24, 24]">
          <a-col :span="5">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="chart-container">
                  <pie-chart 
                    :data="aTypeData.chartData" 
                    :loading="loading"
                    @click="handleChartClick"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="7">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="date-filter-container">
                  <a-range-picker 
                    v-model:value="aTypeData.dateRange" 
                    :allowClear="false"
                    :disabled-date="disabledDate"
                    @change="handleDateRangeChange"
                  />
                </div>
                <div class="stats-container">
                  <a-table
                    size="small"
                    :columns="statsColumns"
                    :data-source="aTypeData.statsData"
                    :pagination="false"
                    :loading="loading"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="12">
            <a-table
              size="small"
              :columns="columns"
              :data-source="aTypeData.tableData"
              :pagination="false"
              :loading="loading"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 返厂WB维修工单 -->
      <a-card
        title="返厂WB维修工单"
        :bordered="false"
        class="dashboard-card"
      >
        <template #extra>
          <a-switch
            size="small"
            v-model:checked="showWB"
            checked-children="显示"
            un-checked-children="隐藏"
          />
        </template>
        <a-row v-if="showWB" :gutter="[24, 24]">
          <a-col :span="5">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="chart-container">
                  <pie-chart 
                    :data="bTypeData.chartData" 
                    :loading="loading"
                    @click="handleChartClick"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="7">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="date-filter-container">
                  <a-range-picker 
                    v-model:value="bTypeData.dateRange" 
                    :allowClear="false"
                    :disabled-date="disabledDate"
                    @change="handleBDateRangeChange"
                  />
                </div>
                <div class="stats-container">
                  <a-table
                    size="small"
                    :columns="statsColumns"
                    :data-source="bTypeData.statsData"
                    :pagination="false"
                    :loading="loading"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="12">
            <a-table
              size="small"
              :columns="columns"
              :data-source="bTypeData.tableData"
              :pagination="false"
              :loading="loading"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 返厂L维修工单 -->
      <a-card
        title="返厂L维修工单"
        :bordered="false"
        class="dashboard-card"
      >
        <template #extra>
          <a-switch
            size="small"
            v-model:checked="showL"
            checked-children="显示"
            un-checked-children="隐藏"
          />
        </template>
        <a-row v-if="showL" :gutter="[24, 24]">
          <a-col :span="5">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="chart-container">
                  <pie-chart 
                    :data="cTypeData.chartData" 
                    :loading="loading"
                    @click="handleChartClick"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="7">
            <a-row :gutter="[0, 16]">
              <a-col :span="24">
                <div class="date-filter-container">
                  <a-range-picker 
                    v-model:value="cTypeData.dateRange" 
                    :allowClear="false"
                    :disabled-date="disabledDate"
                    @change="handleCDateRangeChange"
                  />
                </div>
                <div class="stats-container">
                  <a-table
                    size="small"
                    :columns="statsColumns"
                    :data-source="cTypeData.statsData"
                    :pagination="false"
                    :loading="loading"
                  />
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="12">
            <a-table
              size="small"
              :columns="columns"
              :data-source="cTypeData.tableData"
              :pagination="false"
              :loading="loading"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 工具设备管理模块 -->
      <a-card title="设备与库存管理模块" :bordered="false" class="dashboard-card">
        <a-row :gutter="24">
          <!-- 左侧航空库存提醒 -->
          <a-col :span="12">
            <div class="module-title">航空库存提醒</div>
            <a-table
              size="small"
              :columns="inventoryColumns"
              :data-source="inventoryData"
              :pagination="false"
              :loading="loading"
            />
          </a-col>
          
          <!-- 右侧工具设备管理提醒 -->
          <a-col :span="12">
            <div class="module-title">工具设备管理提醒</div>
            <a-table
              size="small"
              :columns="toolColumns"
              :data-source="toolsData"
              :pagination="false"
              :loading="loading"
            />
          </a-col>
        </a-row>
      </a-card>
    </div>
  </PageWrapper>
</template>

<script lang="ts" setup>
import { ref, onMounted, h } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { PageWrapper } from '/@/components/Page';
import PieChart from './components/PieChart.vue';
import { getMaintenanceTypeData, getInventoryData, getToolsDeviceData } from '/@/api/dashboard';

// 加载状态
const loading = ref(true);
// 是否使用模拟数据
const useMockData = ref(false);
// 返厂WB维修工单显隐
const showWB = ref(true);
// 返厂L维修工单显隐
const showL = ref(true);
// 消息提示
const { createMessage } = useMessage();

// 表格列定义
const columns = [
  {
    title: '合同号',
    dataIndex: 'contractCode',
    key: 'contractCode',
  },
  {
    title: '件号',
    dataIndex: 'partNumber',
    key: 'partNumber',
  },
  {
    title: '序号',
    dataIndex: 'serialNumber',
    key: 'serialNumber',
  },
  {
    title: '入库时间',
    dataIndex: 'storageTime',
    key: 'storageTime',
  },
  {
    title: '状态节点',
    dataIndex: 'statusNode',
    key: 'statusNode',
  },
  {
    title: '超期天数',
    dataIndex: 'overdueDays',
    key: 'overdueDays',
  },
  {
    title: '原因',
    dataIndex: 'reason',
    key: 'reason',
  },
];

// 航空库存提醒表格列定义
const inventoryColumns = [
  {
    title: '件号',
    dataIndex: 'partNumber',
    key: 'partNumber',
  },
  {
    title: '库存',
    dataIndex: 'inventory',
    key: 'inventory',
  },
  {
    title: '紧急程度',
    dataIndex: 'urgency',
    key: 'urgency',
    customRender: ({ text }) => {
      const color = text === '高' ? 'red' : text === '中' ? 'orange' : 'green';
      return h('span', { style: { color } }, text);
    },
  },
];

// 工具设备管理表格列定义
const toolColumns = [
  {
    title: '管理区域',
    dataIndex: 'area',
    key: 'area',
  },
  {
    title: '设备编号',
    dataIndex: 'equipmentCode',
    key: 'equipmentCode',
  },
  {
    title: '可用天数',
    dataIndex: 'availableDays',
    key: 'availableDays',
    customRender: ({ text }) => {
      const color = text < 10 ? 'red' : text < 30 ? 'orange' : 'green';
      return h('span', { style: { color } }, text);
    },
  },
];

// 统计数据表格列定义
const statsColumns = [
  {
    title: '机型',
    dataIndex: 'aircraftType',
    key: 'aircraftType',
  },
  {
    title: '接收量',
    dataIndex: 'received',
    key: 'received',
  },
  {
    title: '完工量',
    dataIndex: 'completed',
    key: 'completed',
  },
  {
    title: '异常量',
    dataIndex: 'abnormal',
    key: 'abnormal',
  },
];

// 日期禁用函数
const disabledDate = (current: Dayjs) => {
  return current && current > dayjs().endOf('day');
};

// A类维修工单日期范围变更处理
const handleDateRangeChange = (dates: Dayjs[], dateStrings: string[]) => {
  console.log('A类 Selected Date Range:', dateStrings);
  // 数据加载
  loading.value = true;
  
  if (useMockData.value) {
    // 使用模拟数据
    setTimeout(() => {
      updateATypeData(dateStrings[0], dateStrings[1]);
      loading.value = false;
    }, 500);
  } else {
    // 使用真实数据
    loadMaintenanceTypeData(1, dateStrings[0], dateStrings[1], 'a')
      .finally(() => {
        loading.value = false;
      });
  }
};

// B类维修工单日期范围变更处理
const handleBDateRangeChange = (dates: Dayjs[], dateStrings: string[]) => {
  console.log('B类 Selected Date Range:', dateStrings);
  // 数据加载
  loading.value = true;
  
  if (useMockData.value) {
    // 使用模拟数据
    setTimeout(() => {
      updateBTypeData(dateStrings[0], dateStrings[1]);
      loading.value = false;
    }, 500);
  } else {
    // 使用真实数据
    loadMaintenanceTypeData(2, dateStrings[0], dateStrings[1], 'b')
      .finally(() => {
        loading.value = false;
      });
  }
};

// C类维修工单日期范围变更处理
const handleCDateRangeChange = (dates: Dayjs[], dateStrings: string[]) => {
  console.log('C类 Selected Date Range:', dateStrings);
  // 数据加载
  loading.value = true;
  
  if (useMockData.value) {
    // 使用模拟数据
    setTimeout(() => {
      updateCTypeData(dateStrings[0], dateStrings[1]);
      loading.value = false;
    }, 500);
  } else {
    // 使用真实数据
    loadMaintenanceTypeData(3, dateStrings[0], dateStrings[1], 'c')
      .finally(() => {
        loading.value = false;
      });
  }
};

// 更新A类维修工单数据
const updateATypeData = (startDate: string, endDate: string) => {
  console.log(`Updating A类 data for date range: ${startDate} to ${endDate}`);
  
  // 模拟按机型更新图表数据
  // 实际应用中，这里应该是从API获取数据
  aTypeData.value.chartData = [
    { name: 'B737', value: Math.floor(Math.random() * 30) + 10, color: '#36cfc9' },
    { name: 'A320', value: Math.floor(Math.random() * 40) + 20, color: '#5b8ff9' },
    { name: 'B777', value: Math.floor(Math.random() * 20) + 5, color: '#f6bd16' },
    { name: 'A330', value: Math.floor(Math.random() * 15) + 5, color: '#ff7a45' },
    { name: 'B787', value: Math.floor(Math.random() * 10) + 5, color: '#bfbfbf' },
  ];
  
  // 更新统计数据
  aTypeData.value.statsData = [
    { key: '1', aircraftType: 'B737', received: 15, completed: 10, abnormal: 2 },
    { key: '2', aircraftType: 'A320', received: 22, completed: 18, abnormal: 1 },
    { key: '3', aircraftType: 'B777', received: 8, completed: 5, abnormal: 0 },
    { key: '4', aircraftType: 'A330', received: 12, completed: 9, abnormal: 1 },
    { key: '5', aircraftType: 'B787', received: 7, completed: 4, abnormal: 1 },
  ];
};

// 更新B类维修工单数据
const updateBTypeData = (startDate: string, endDate: string) => {
  console.log(`Updating B类 data for date range: ${startDate} to ${endDate}`);
  
  // 模拟按机型更新图表数据
  bTypeData.value.chartData = [
    { name: 'B737', value: Math.floor(Math.random() * 35) + 10, color: '#36cfc9' },
    { name: 'A320', value: Math.floor(Math.random() * 30) + 15, color: '#5b8ff9' },
    { name: 'B777', value: Math.floor(Math.random() * 25) + 5, color: '#f6bd16' },
    { name: 'A330', value: Math.floor(Math.random() * 15) + 5, color: '#ff7a45' },
    { name: 'B787', value: Math.floor(Math.random() * 10) + 2, color: '#bfbfbf' },
  ];
  
  // 更新统计数据
  bTypeData.value.statsData = [
    { key: '1', aircraftType: 'B737', received: Math.floor(Math.random() * 10) + 10, completed: Math.floor(Math.random() * 8) + 8, abnormal: Math.floor(Math.random() * 3) },
    { key: '2', aircraftType: 'A320', received: Math.floor(Math.random() * 10) + 15, completed: Math.floor(Math.random() * 8) + 10, abnormal: Math.floor(Math.random() * 3) },
    { key: '3', aircraftType: 'B777', received: Math.floor(Math.random() * 5) + 8, completed: Math.floor(Math.random() * 5) + 5, abnormal: Math.floor(Math.random() * 2) },
    { key: '4', aircraftType: 'A330', received: Math.floor(Math.random() * 5) + 5, completed: Math.floor(Math.random() * 4) + 3, abnormal: Math.floor(Math.random() * 2) },
    { key: '5', aircraftType: 'B787', received: Math.floor(Math.random() * 4) + 3, completed: Math.floor(Math.random() * 3) + 2, abnormal: Math.floor(Math.random() * 2) },
  ];
};

// 更新C类维修工单数据
const updateCTypeData = (startDate: string, endDate: string) => {
  console.log(`Updating C类 data for date range: ${startDate} to ${endDate}`);
  
  // 模拟按机型更新图表数据
  cTypeData.value.chartData = [
    { name: 'B737', value: Math.floor(Math.random() * 20) + 5, color: '#36cfc9' },
    { name: 'A320', value: Math.floor(Math.random() * 25) + 15, color: '#5b8ff9' },
    { name: 'B777', value: Math.floor(Math.random() * 30) + 20, color: '#f6bd16' },
    { name: 'A330', value: Math.floor(Math.random() * 25) + 10, color: '#ff7a45' },
    { name: 'B787', value: Math.floor(Math.random() * 15) + 5, color: '#bfbfbf' },
  ];
  
  // 更新统计数据
  cTypeData.value.statsData = [
    { key: '1', aircraftType: 'B737', received: Math.floor(Math.random() * 5) + 8, completed: Math.floor(Math.random() * 4) + 6, abnormal: Math.floor(Math.random() * 2) },
    { key: '2', aircraftType: 'A320', received: Math.floor(Math.random() * 5) + 12, completed: Math.floor(Math.random() * 5) + 8, abnormal: Math.floor(Math.random() * 2) },
    { key: '3', aircraftType: 'B777', received: Math.floor(Math.random() * 5) + 15, completed: Math.floor(Math.random() * 5) + 12, abnormal: Math.floor(Math.random() * 3) },
    { key: '4', aircraftType: 'A330', received: Math.floor(Math.random() * 5) + 10, completed: Math.floor(Math.random() * 4) + 8, abnormal: Math.floor(Math.random() * 2) },
    { key: '5', aircraftType: 'B787', received: Math.floor(Math.random() * 4) + 5, completed: Math.floor(Math.random() * 3) + 3, abnormal: Math.floor(Math.random() * 2) },
  ];
};

// A类维修工单数据
const aTypeData = ref({
  dateRange: [dayjs().subtract(30, 'day'), dayjs()] as [Dayjs, Dayjs],
  chartData: [
    { name: 'B737', value: 25, color: '#36cfc9' },
    { name: 'A320', value: 45, color: '#5b8ff9' },
    { name: 'B777', value: 15, color: '#f6bd16' },
    { name: 'A330', value: 10, color: '#ff7a45' },
    { name: 'B787', value: 5, color: '#bfbfbf' },
  ],
  statsData: [
    { key: '1', aircraftType: 'B737', received: 15, completed: 10, abnormal: 2 },
    { key: '2', aircraftType: 'A320', received: 22, completed: 18, abnormal: 1 },
    { key: '3', aircraftType: 'B777', received: 8, completed: 5, abnormal: 0 },
    { key: '4', aircraftType: 'A330', received: 12, completed: 9, abnormal: 1 },
    { key: '5', aircraftType: 'B787', received: 7, completed: 4, abnormal: 1 },
  ],
  tableData: [
    {
      key: '1',
      contractCode: 'HT2023001',
      partNumber: 'PN-A001',
      serialNumber: 'SN001',
      storageTime: '2023-05-08',
      statusNode: '待检',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '2',
      contractCode: 'HT2023002',
      partNumber: 'PN-A002',
      serialNumber: 'SN002',
      storageTime: '2023-05-10',
      statusNode: '检验中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '3',
      contractCode: 'HT2023003',
      partNumber: 'PN-A003',
      serialNumber: 'SN003',
      storageTime: '2023-04-15',
      statusNode: '等料暂停',
      overdueDays: 5,
      reason: '缺少零件',
    },
    {
      key: '4',
      contractCode: 'HT2023004',
      partNumber: 'PN-A004',
      serialNumber: 'SN004',
      storageTime: '2023-05-01',
      statusNode: '维修中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '5',
      contractCode: 'HT2023005',
      partNumber: 'PN-A005',
      serialNumber: 'SN005',
      storageTime: '2023-04-20',
      statusNode: '完工',
      overdueDays: 0,
      reason: '-',
    },
  ],
});

// B类维修工单数据
const bTypeData = ref({
  dateRange: [dayjs().subtract(30, 'day'), dayjs()] as [Dayjs, Dayjs],
  chartData: [
    { name: 'B737', value: 30, color: '#36cfc9' },
    { name: 'A320', value: 35, color: '#5b8ff9' },
    { name: 'B777', value: 20, color: '#f6bd16' },
    { name: 'A330', value: 10, color: '#ff7a45' },
    { name: 'B787', value: 5, color: '#bfbfbf' },
  ],
  statsData: [
    { key: '1', aircraftType: 'B737', received: 18, completed: 12, abnormal: 1 },
    { key: '2', aircraftType: 'A320', received: 20, completed: 15, abnormal: 2 },
    { key: '3', aircraftType: 'B777', received: 10, completed: 8, abnormal: 0 },
    { key: '4', aircraftType: 'A330', received: 8, completed: 6, abnormal: 1 },
    { key: '5', aircraftType: 'B787', received: 6, completed: 3, abnormal: 0 },
  ],
  tableData: [
    {
      key: '1',
      contractCode: 'HT20230011',
      partNumber: 'PN-B001',
      serialNumber: 'SN001',
      storageTime: '2023-05-10',
      statusNode: '待检',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '2',
      contractCode: 'HT2023007',
      partNumber: 'PN-B002',
      serialNumber: 'SN007',
      storageTime: '2023-05-05',
      statusNode: '检验中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '3',
      contractCode: 'HT2023008',
      partNumber: 'PN-B003',
      serialNumber: 'SN008',
      storageTime: '2023-04-10',
      statusNode: '等料暂停',
      overdueDays: 8,
      reason: '缺少零件',
    },
    {
      key: '4',
      contractCode: 'HT2023009',
      partNumber: 'PN-B004',
      serialNumber: 'SN009',
      storageTime: '2023-05-03',
      statusNode: '维修中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '5',
      contractCode: 'HT2023010',
      partNumber: 'PN-B005',
      serialNumber: 'SN010',
      storageTime: '2023-04-25',
      statusNode: '完工',
      overdueDays: 0,
      reason: '-',
    },
  ],
});

// C类维修工单数据
const cTypeData = ref({
  dateRange: [dayjs().subtract(30, 'day'), dayjs()] as [Dayjs, Dayjs],
  chartData: [
    { name: 'B737', value: 15, color: '#36cfc9' },
    { name: 'A320', value: 25, color: '#5b8ff9' },
    { name: 'B777', value: 30, color: '#f6bd16' },
    { name: 'A330', value: 20, color: '#ff7a45' },
    { name: 'B787', value: 10, color: '#bfbfbf' },
  ],
  statsData: [
    { key: '1', aircraftType: 'B737', received: 10, completed: 8, abnormal: 0 },
    { key: '2', aircraftType: 'A320', received: 15, completed: 12, abnormal: 1 },
    { key: '3', aircraftType: 'B777', received: 18, completed: 15, abnormal: 2 },
    { key: '4', aircraftType: 'A330', received: 12, completed: 10, abnormal: 1 },
    { key: '5', aircraftType: 'B787', received: 8, completed: 5, abnormal: 0 },
  ],
  tableData: [
    {
      key: '1',
      contractCode: 'HT2023011',
      partNumber: 'PN-C001',
      serialNumber: 'SN011',
      storageTime: '2023-05-15',
      statusNode: '已完成',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '2',
      contractCode: 'HT2023012',
      partNumber: 'PN-C002',
      serialNumber: 'SN012',
      storageTime: '2023-05-08',
      statusNode: '检验中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '3',
      contractCode: 'HT2023013',
      partNumber: 'PN-C003',
      serialNumber: 'SN013',
      storageTime: '2023-04-05',
      statusNode: '等料暂停',
      overdueDays: 12,
      reason: '缺少零件',
    },
    {
      key: '4',
      contractCode: 'HT2023014',
      partNumber: 'PN-C004',
      serialNumber: 'SN014',
      storageTime: '2023-05-02',
      statusNode: '维修中',
      overdueDays: 0,
      reason: '-',
    },
    {
      key: '5',
      contractCode: 'HT2023015',
      partNumber: 'PN-C005',
      serialNumber: 'SN015',
      storageTime: '2023-04-28',
      statusNode: '完工',
      overdueDays: 0,
      reason: '-',
    },
  ],
});

// 航空库存提醒数据
const inventoryData = ref([
  {
    key: '1',
    partNumber: 'PN-A001',
    inventory: 5,
    urgency: '高',
  },
  {
    key: '2',
    partNumber: 'PN-B002',
    inventory: 12,
    urgency: '中',
  },
  {
    key: '3',
    partNumber: 'PN-C003',
    inventory: 8,
    urgency: '高',
  },
  {
    key: '4',
    partNumber: 'PN-A004',
    inventory: 20,
    urgency: '低',
  },
  {
    key: '5',
    partNumber: 'PN-B005',
    inventory: 3,
    urgency: '高',
  },
]);

// 工具设备管理数据
const toolsData = ref([
  {
    key: '1',
    area: '管理区域A',
    equipmentCode: 'EQ-001',
    availableDays: 45,
  },
  {
    key: '2',
    area: '管理区域A',
    equipmentCode: 'EQ-002',
    availableDays: 30,
  },
  {
    key: '3',
    area: '管理区域B',
    equipmentCode: 'EQ-003',
    availableDays: 5,
  },
  {
    key: '4',
    area: '管理区域B',
    equipmentCode: 'EQ-004',
    availableDays: 15,
  },
  {
    key: '5',
    area: '管理区域C',
    equipmentCode: 'EQ-005',
    availableDays: 60,
  },
]);

// 图表点击处理
const handleChartClick = (params) => {
  console.log('Chart clicked:', params);
  // 可以在这里添加点击图表后的处理逻辑
};

// 模拟数据切换处理
const handleMockDataChange = (checked) => {
  loading.value = true;
  useMockData.value = checked;
  
  // 重新加载所有数据
  loadAllData();
  
  createMessage.success(`已切换到${checked ? '模拟' : '真实'}数据模式`);
};

// 加载所有维修看板数据
const loadAllData = async () => {
  try {
    // 获取日期范围
    const aStartDate = aTypeData.value.dateRange[0].format('YYYY-MM-DD');
    const aEndDate = aTypeData.value.dateRange[1].format('YYYY-MM-DD');
    const bStartDate = bTypeData.value.dateRange[0].format('YYYY-MM-DD');
    const bEndDate = bTypeData.value.dateRange[1].format('YYYY-MM-DD');
    const cStartDate = cTypeData.value.dateRange[0].format('YYYY-MM-DD');
    const cEndDate = cTypeData.value.dateRange[1].format('YYYY-MM-DD');
    
    // 如果使用模拟数据，则直接更新本地数据
    if (useMockData.value) {
      updateATypeData(aStartDate, aEndDate);
      updateBTypeData(bStartDate, bEndDate);
      updateCTypeData(cStartDate, cEndDate);
      loadMockInventoryData();
      loadMockToolsData();
    } else {
      // 使用真实数据，调用后端API
      await Promise.all([
        loadMaintenanceTypeData(1, aStartDate, aEndDate, 'a'),
        loadMaintenanceTypeData(2, bStartDate, bEndDate, 'b'),
        loadMaintenanceTypeData(3, cStartDate, cEndDate, 'c'),
        loadInventoryData(),
        loadToolsData()
      ]);
    }
  } catch (error) {
    console.error('加载维修看板数据失败:', error);
    createMessage.error('加载数据失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 加载维修类型数据
const loadMaintenanceTypeData = async (contractType, startDate, endDate, dataType) => {
  try {
    const result = await getMaintenanceTypeData(contractType, startDate, endDate);
    
    if (result) {
      // 根据数据类型更新对应的数据
      if (dataType === 'a') {
        aTypeData.value.chartData = result.chartData || [];
        aTypeData.value.statsData = result.statisticsData || [];
        aTypeData.value.tableData = result.tableData || [];
      } else if (dataType === 'b') {
        bTypeData.value.chartData = result.chartData || [];
        bTypeData.value.statsData = result.statisticsData || [];
        bTypeData.value.tableData = result.tableData || [];
      } else if (dataType === 'c') {
        cTypeData.value.chartData = result.chartData || [];
        cTypeData.value.statsData = result.statisticsData || [];
        cTypeData.value.tableData = result.tableData || [];
      }
    }
  } catch (error) {
    console.error(`加载${dataType}类维修数据失败:`, error);
    throw error;
  }
};

// 加载库存数据
const loadInventoryData = async () => {
  try {
    const result = await getInventoryData();
    if (result) {
      inventoryData.value = result;
    }
  } catch (error) {
    console.error('加载库存数据失败:', error);
    throw error;
  }
};

// 加载工具设备数据
const loadToolsData = async () => {
  try {
    const result = await getToolsDeviceData();
    if (result) {
      toolsData.value = result;
    }
  } catch (error) {
    console.error('加载工具设备数据失败:', error);
    throw error;
  }
};

// 加载模拟库存数据
const loadMockInventoryData = () => {
  inventoryData.value = [
    {
      key: '1',
      partNumber: 'PN-A001',
      inventory: 5,
      urgency: '高',
    },
    {
      key: '2',
      partNumber: 'PN-B002',
      inventory: 12,
      urgency: '中',
    },
    {
      key: '3',
      partNumber: 'PN-C003',
      inventory: 8,
      urgency: '高',
    },
    {
      key: '4',
      partNumber: 'PN-A004',
      inventory: 20,
      urgency: '低',
    },
    {
      key: '5',
      partNumber: 'PN-B005',
      inventory: 3,
      urgency: '高',
    },
  ];
};

// 加载模拟工具设备数据
const loadMockToolsData = () => {
  toolsData.value = [
    {
      key: '1',
      area: '管理区域A',
      equipmentCode: 'EQ-001',
      availableDays: 45,
    },
    {
      key: '2',
      area: '管理区域A',
      equipmentCode: 'EQ-002',
      availableDays: 30,
    },
    {
      key: '3',
      area: '管理区域B',
      equipmentCode: 'EQ-003',
      availableDays: 5,
    },
    {
      key: '4',
      area: '管理区域B',
      equipmentCode: 'EQ-004',
      availableDays: 15,
    },
    {
      key: '5',
      area: '管理区域C',
      equipmentCode: 'EQ-005',
      availableDays: 60,
    },
  ];
};

// 初始加载数据
onMounted(() => {
  loadAllData();
});
</script>

<style lang="less" scoped>
.maintenance-dashboard {
  .mock-data-switch {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
  }
  .dashboard-card {
    margin-bottom: 24px;
    
    .chart-container {
      height: 220px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    
    .date-filter-container {
      margin-bottom: 16px;
      display: flex;
      justify-content: center;
    }
    
    .stats-container {
      height: 180px;
      overflow-y: auto;
    }
    
    .module-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 12px;
      padding-left: 8px;
      border-left: 3px solid #1890ff;
      color: #262626;
    }
  }
}
</style>
