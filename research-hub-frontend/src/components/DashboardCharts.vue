<!-- src/components/DashboardCharts.vue -->
<template>
  <div class="dashboard-charts">
    <!-- 图表标题和操作 -->
    <div class="charts-header">
      <h3 class="charts-title">科研数据统计</h3>
      <div class="charts-actions">
        <el-select 
          v-model="chartType" 
          placeholder="选择图表类型" 
          size="small"
          style="width: 140px; margin-right: 12px;"
        >
          <el-option label="柱状图" value="bar" />
          <el-option label="折线图" value="line" />
          <el-option label="饼图" value="pie" />
        </el-select>
        <el-button 
          size="small" 
          :icon="Refresh" 
          @click="refreshCharts"
          :loading="loading"
        >
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 图表容器 -->
    <div class="charts-grid">
      <!-- 项目状态分布 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">项目状态分布</span>
            <el-tag type="primary" size="small">实时</el-tag>
          </div>
        </template>
        <v-chart 
          :option="projectStatusOption" 
          :loading="loading"
          class="chart-container"
          autoresize
        />
      </el-card>

      <!-- 经费统计 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">项目经费统计</span>
            <el-tag type="success" size="small">万元</el-tag>
          </div>
        </template>
        <v-chart 
          :option="fundingOption" 
          :loading="loading"
          class="chart-container"
          autoresize
        />
      </el-card>

      <!-- 月度趋势 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">月度项目趋势</span>
            <el-tag type="warning" size="small">趋势</el-tag>
          </div>
        </template>
        <v-chart 
          :option="monthlyTrendOption" 
          :loading="loading"
          class="chart-container"
          autoresize
        />
      </el-card>

      <!-- 类型分布 -->
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">项目类型分布</span>
            <el-tag type="info" size="small">分类</el-tag>
          </div>
        </template>
        <v-chart 
          :option="projectTypeOption" 
          :loading="loading"
          class="chart-container"
          autoresize
        />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const loading = ref(false)
const chartType = ref('bar')

// 图表数据
const chartData = reactive({
  projectStatus: [],
  funding: [],
  monthlyTrend: [],
  projectType: []
})

// 获取图表数据
async function fetchChartData() {
  loading.value = true
  try {
    // 这里调用后端接口
    const response = await fetch('/api/dashboard/statistics')
    const data = await response.json()
    
    if (data.success) {
      // 假设后端返回的数据结构
      chartData.projectStatus = data.data.projectStatus || []
      chartData.funding = data.data.funding || []
      chartData.monthlyTrend = data.data.monthlyTrend || []
      chartData.projectType = data.data.projectType || []
    } else {
      throw new Error(data.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取图表数据失败:', error)
    ElMessage.error('获取图表数据失败')
    // 使用模拟数据
    useMockData()
  } finally {
    loading.value = false
  }
}

// 模拟数据
function useMockData() {
  chartData.projectStatus = [
    { name: '进行中', value: 5 },
    { name: '已结题', value: 2 },
    { name: '未开始', value: 1 },
    { name: '暂停', value: 1 }
  ]

  chartData.funding = [
    { name: '国家级', value: 80 },
    { name: '省部级', value: 30 },
    { name: '校级', value: 10 },
    { name: '横向项目', value: 5 }
  ]

  chartData.monthlyTrend = [
    { month: '1月', projects: 2, funding: 20 },
    { month: '2月', projects: 3, funding: 35 },
    { month: '3月', projects: 1, funding: 15 },
    { month: '4月', projects: 4, funding: 50 },
    { month: '5月', projects: 2, funding: 25 },
    { month: '6月', projects: 3, funding: 40 }
  ]

  chartData.projectType = [
    { name: '国家级', value: 2 },
    { name: '省部级', value: 3 },
    { name: '校级', value: 2 },
    { name: '横向项目', value: 1 }
  ]
}

// 项目状态分布图表配置
const projectStatusOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center',
    data: chartData.projectStatus.map(item => item.name)
  },
  series: [
    {
      name: '项目状态',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: chartData.projectStatus
    }
  ],
  color: ['#67C23A', '#E6A23C', '#409EFF', '#F56C6C']
}))

// 经费统计图表配置
const fundingOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: chartData.funding.map(item => item.name),
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    name: '经费(万元)'
  },
  series: [
    {
      name: '项目经费',
      type: chartType.value,
      data: chartData.funding.map(item => item.value),
      itemStyle: {
        color: '#409EFF'
      },
      label: {
        show: true,
        position: 'top'
      }
    }
  ]
}))

// 月度趋势图表配置
const monthlyTrendOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      crossStyle: {
        color: '#999'
      }
    }
  },
  legend: {
    data: ['项目数量', '经费(万)']
  },
  xAxis: {
    type: 'category',
    data: chartData.monthlyTrend.map(item => item.month),
    axisPointer: {
      type: 'shadow'
    }
  },
  yAxis: [
    {
      type: 'value',
      name: '项目数量',
      min: 0,
      axisLabel: {
        formatter: '{value} 个'
      }
    },
    {
      type: 'value',
      name: '经费(万)',
      min: 0,
      axisLabel: {
        formatter: '{value} 万'
      }
    }
  ],
  series: [
    {
      name: '项目数量',
      type: 'bar',
      data: chartData.monthlyTrend.map(item => item.projects),
      itemStyle: {
        color: '#67C23A'
      }
    },
    {
      name: '经费(万)',
      type: 'line',
      yAxisIndex: 1,
      data: chartData.monthlyTrend.map(item => item.funding),
      itemStyle: {
        color: '#E6A23C'
      },
      lineStyle: {
        width: 3
      }
    }
  ]
}))

// 项目类型分布图表配置
const projectTypeOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    bottom: 10,
    left: 'center',
    data: chartData.projectType.map(item => item.name)
  },
  series: [
    {
      name: '项目类型',
      type: 'pie',
      radius: '50%',
      center: ['50%', '45%'],
      data: chartData.projectType,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ],
  color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
}))

// 刷新图表数据
function refreshCharts() {
  fetchChartData()
}

// 监听图表类型变化
watch(chartType, () => {
  // 图表类型变化时，可以重新渲染或调整配置
})

onMounted(() => {
  fetchChartData()
})
</script>

<style scoped>
.dashboard-charts {
  margin-bottom: 20px;
}

.charts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 4px;
}

.charts-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.charts-actions {
  display: flex;
  align-items: center;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  transition: all 0.3s;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .charts-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .charts-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style>