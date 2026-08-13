<template>
  <div ref="chartRef" class="pie-chart"></div>
</template>

<script lang="ts" setup>
import { ref, onBeforeUnmount, onMounted, watch, PropType } from 'vue';
import * as echarts from 'echarts/core';
import { PieChart as EChartsPie } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

// 注册必要的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  EChartsPie,
  CanvasRenderer,
]);

// 定义数据类型
interface ChartDataItem {
  name: string;
  value: number;
  color: string;
}

const props = defineProps({
  data: {
    type: Array as PropType<ChartDataItem[]>,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['click']);

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return;
  
  chartInstance = echarts.init(chartRef.value);
  
  // 监听点击事件
  chartInstance.on('click', (params) => {
    emit('click', params);
  });
  
  updateChart();
  
  // 响应窗口大小变化
  window.addEventListener('resize', handleResize);
};

// 更新图表数据
const updateChart = () => {
  if (!chartInstance) return;
  
  // 设置图表配置
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    legend: {
      show: false,
    },
    series: [
      {
        name: '机型',
        type: 'pie',
        radius: '65%',
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold',
          },
        },
        labelLine: {
          show: false,
        },
        data: props.data.map(item => ({
          name: item.name,
          value: item.value,
          itemStyle: {
            color: item.color,
          },
        })),
      },
    ],
  };
  
  // 设置加载状态
  if (props.loading) {
    chartInstance.showLoading({
      text: '加载中...',
      maskColor: 'rgba(255, 255, 255, 0.8)',
      fontSize: 14,
    });
  } else {
    chartInstance.hideLoading();
  }
  
  chartInstance.setOption(option);
};

// 监听数据变化
watch(
  () => props.data,
  () => {
    updateChart();
  },
  { deep: true }
);

// 监听加载状态变化
watch(
  () => props.loading,
  (val) => {
    if (chartInstance) {
      if (val) {
        chartInstance.showLoading({
          text: '加载中...',
          maskColor: 'rgba(255, 255, 255, 0.8)',
          fontSize: 14,
        });
      } else {
        chartInstance.hideLoading();
      }
    }
  }
);

// 组件挂载时初始化图表
onMounted(() => {
  initChart();
});

// 组件卸载时销毁图表实例
function handleResize() {
  chartInstance?.resize();
}

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.pie-chart {
  width: 100%;
  height: 100%;
}
</style>
