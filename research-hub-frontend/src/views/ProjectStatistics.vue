<template>
  <div class="statistics-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">项目统计</h1>
          <p class="page-subtitle">展示科研项目整体情况</p>
        </div>
      </div>
    </div>

    <!-- 筛选区 -->
    <el-card class="filter-section" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="年份">
          <el-select v-model="filterForm.year" placeholder="选择年份" style="width: 150px" clearable>
            <el-option v-for="year in years" :key="year" :label="year" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="filterForm.projectType" placeholder="选择项目类型" style="width: 150px" clearable>
            <el-option label="国家级项目" value="国家级" />
            <el-option label="省级项目" value="省级" />
            <el-option label="校级项目" value="校级" />
            <el-option label="横向项目" value="横向" />
            <el-option label="其他项目" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="filterForm.status" placeholder="选择项目状态" style="width: 150px" clearable>
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结题" value="已结题" />
            <el-option label="已暂停" value="已暂停" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">导出统计</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="card-section">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total }}</div>
              <div class="stat-label">项目总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.ongoing }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completed }}</div>
              <div class="stat-label">已结题</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="32"><Plus /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.newThisYear }}</div>
              <div class="stat-label">本年新增</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20" class="chart-section">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <h3>项目类型分布</h3>
          </template>
          <div ref="typeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <h3>项目状态分布</h3>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="chart-section">
      <el-col :span="24">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <h3>年度项目数量趋势</h3>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Clock, CircleCheck, Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '../utils/request'

// 响应式数据
const years = ref([2025, 2024, 2023, 2022, 2021])
const filterForm = ref({
  year: null,
  projectType: null,
  status: null
})

const statistics = ref({
  total: 0,
  ongoing: 0,
  completed: 0,
  newThisYear: 0
})

const chartData = ref({
  typeDistribution: [],
  statusDistribution: [],
  yearTrend: []
})

// 图表引用
const typeChartRef = ref(null)
const statusChartRef = ref(null)
const trendChartRef = ref(null)

// 图表实例
let typeChart = null
let statusChart = null
let trendChart = null

// 项目类型映射
function getProjectTypeText(type) {
  const map = {
    NATIONAL: '国家级',
    PROVINCIAL: '省部级',
    SCHOOL: '校级',
    HORIZONTAL: '横向',
    '国家级': '国家级',
    '省部级': '省部级',
    '校级': '校级',
    '横向': '横向',
    '其他': '其他'
  }
  return map[type] || type || '其他'
}

// 项目状态映射
function getStatusText(status) {
  const map = {
    APPLICATION: '申报中',
    ONGOING: '进行中',
    CONCLUSION: '已结题',
    TERMINATED: '已暂停',
    '申报中': '申报中',
    '进行中': '进行中',
    '已结题': '已结题',
    '已暂停': '已暂停'
  }
  return map[status] || status || '未知'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 调用与仪表盘相同的接口获取项目数据
    const res = await request.get('/project/recent')
    const projects = Array.isArray(res) ? res : res.data || res.records || []
    
    // 计算统计数据
    statistics.value = {
      total: projects.length,
      ongoing: projects.filter(p => p.status === 'ONGOING' || p.status === '进行中').length,
      completed: projects.filter(p => p.status === 'CONCLUSION' || p.status === '已结题').length,
      newThisYear: projects.filter(p => {
        if (!p.startDate) return false
        const startDate = new Date(p.startDate)
        return startDate.getFullYear() === new Date().getFullYear()
      }).length
    }
    
    // 处理项目类型分布
    const typeCountMap = {}
    projects.forEach(p => {
      const type = p.projectType || '其他'
      typeCountMap[type] = (typeCountMap[type] || 0) + 1
    })
    
    const typeDistribution = []
    Object.keys(typeCountMap).forEach(type => {
      typeDistribution.push({
        name: getProjectTypeText(type),
        value: typeCountMap[type]
      })
    })
    
    // 处理项目状态分布
    const statusCountMap = {}
    projects.forEach(p => {
      const status = p.status || '未知'
      statusCountMap[status] = (statusCountMap[status] || 0) + 1
    })
    
    const statusDistribution = []
    Object.keys(statusCountMap).forEach(status => {
      statusDistribution.push({
        name: getStatusText(status),
        value: statusCountMap[status]
      })
    })
    
    // 处理年度趋势
    const yearCountMap = {}
    projects.forEach(p => {
      if (p.startDate) {
        const year = new Date(p.startDate).getFullYear()
        yearCountMap[year] = (yearCountMap[year] || 0) + 1
      }
    })
    
    const yearTrend = []
    for (let i = 4; i >= 0; i--) {
      const year = new Date().getFullYear() - i
      yearTrend.push({
        year: String(year),
        count: yearCountMap[year] || 0
      })
    }
    
    // 更新图表数据
    chartData.value = {
      typeDistribution,
      statusDistribution,
      yearTrend
    }
    
    updateCharts()
  } catch (error) {
    console.error('加载统计数据失败', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 加载模拟数据
const loadMockData = () => {
  statistics.value = {
    total: 128,
    ongoing: 76,
    completed: 39,
    newThisYear: 13
  }
  
  chartData.value = {
    typeDistribution: [
      { value: 45, name: '国家级项目' },
      { value: 38, name: '省级项目' },
      { value: 25, name: '校级项目' },
      { value: 15, name: '横向项目' },
      { value: 5, name: '其他项目' }
    ],
    statusDistribution: [
      { name: '未开始', value: 8 },
      { name: '进行中', value: 76 },
      { name: '已结题', value: 39 },
      { name: '已暂停', value: 5 }
    ],
    yearTrend: [
      { year: '2021', count: 12 },
      { year: '2022', count: 18 },
      { year: '2023', count: 25 },
      { year: '2024', count: 31 },
      { year: '2025', count: 42 }
    ]
  }
  
  // 确保图表已初始化
  if (!typeChart || !statusChart || !trendChart) {
    initCharts()
  }
  
  // 使用 nextTick 确保 DOM 更新后再更新图表
  setTimeout(() => {
    updateCharts()
  }, 100)
}

// 初始化图表
const initCharts = () => {
  // 项目类型分布饼图
  if (typeChartRef.value) {
    typeChart = echarts.init(typeChartRef.value)
  }

  // 项目状态分布柱状图
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
  }

  // 年度项目数量趋势折线图
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
  }
}

// 更新图表数据
const updateCharts = () => {
  // 更新项目类型分布饼图
  if (typeChart && chartData.value.typeDistribution) {
    typeChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '项目类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
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
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: chartData.value.typeDistribution
        }
      ]
    })
  }

  // 更新项目状态分布柱状图
  if (statusChart && chartData.value.statusDistribution) {
    const statusData = chartData.value.statusDistribution
    statusChart.setOption({
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
        data: statusData.map(item => item.name)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '项目数量',
          type: 'bar',
          barWidth: '60%',
          data: statusData.map(item => item.value),
          itemStyle: {
            color: function (params) {
              // 根据状态名称分配颜色
              const statusColorMap = {
                '申报中': '#409eff',  // 蓝色
                '进行中': '#67c23a',  // 绿色
                '已结题': '#e6a23c',  // 橙色
                '已暂停': '#f56c6c',  // 红色
                '未开始': '#909399',  // 灰色
                '其他': '#c0c4cc'     // 浅灰色
              }
              return statusColorMap[params.name] || '#909399'
            }
          }
        }
      ]
    })
  }

  // 更新年度项目数量趋势折线图
  if (trendChart && chartData.value.yearTrend) {
    const trendData = chartData.value.yearTrend
    trendChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendData.map(item => item.year)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '项目数量',
          type: 'line',
          stack: 'Total',
          areaStyle: {
            opacity: 0.3
          },
          emphasis: {
            focus: 'series'
          },
          data: trendData.map(item => item.count),
          smooth: true,
          itemStyle: {
            color: '#409eff'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          }
        }
      ]
    })
  }
}

// 搜索
const handleSearch = () => {
  loadStatistics()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    year: null,
    projectType: null,
    status: null
  }
  loadStatistics()
}

// 导出统计
const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  if (typeChart) typeChart.resize()
  if (statusChart) statusChart.resize()
  if (trendChart) trendChart.resize()
}

// 初始化
onMounted(() => {
  initCharts()
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

// 清理
onUnmounted(() => {
  if (typeChart) typeChart.dispose()
  if (statusChart) statusChart.dispose()
  if (trendChart) trendChart.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 10px;
}

.title-section {
  flex: 1;
  min-width: 300px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.card-section {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-section {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-card h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.chart-container {
  width: 100%;
  height: 320px;
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", "WenQuanYi Micro Hei", sans-serif;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-form .el-form-item {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .filter-form .el-select {
    width: 100% !important;
  }
  
  .chart-card {
    height: 350px;
  }
  
  .chart-container {
    height: 270px;
  }
}
</style>