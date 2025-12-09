<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎卡片 -->
    <el-card class="welcome-banner">
      <div class="welcome-content">
        <div class="welcome-text">
          <div class="welcome-title-row">
            <h2>欢迎回来，{{ userName }}！</h2>
            <span class="welcome-tag">高校科研管理项目</span>
          </div>
          <p>今天是 {{ currentDate }}，祝您科研顺利、项目顺利推进。</p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" @click="$router.push('/projects/create')">
            <el-icon><Plus /></el-icon>
            创建新项目
          </el-button>
          <el-button @click="$router.push('/projects')">
            <el-icon><View /></el-icon>
            查看所有项目
          </el-button>
        </div>
      </div>
    </el-card>

    

    <!-- 最近项目 + 统计图 -->
    <el-card class="recent-projects">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <span class="card-title">最近项目</span>
            <span class="card-subtitle">展示最近创建或更新的项目概览</span>
          </div>
          <el-button type="primary" text @click="$router.push('/projects')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>

      <!-- 图表区域 -->
      <div class="chart-wrapper">
        <div class="chart-header">
          <div class="chart-title">项目类型分布</div>
          <div class="chart-subtitle">基于最近项目的类型统计</div>
        </div>
        <div ref="projectChartRef" class="chart-container"></div>
      </div>

      <el-divider></el-divider>

      <!-- 最近项目表格（数据来自后端） -->
      <div class="table-header-row">
        <div class="table-title">最近项目列表</div>
        <div class="table-subtitle">点击右侧“查看”可进入项目详情页</div>
      </div>

      <el-table
        v-loading="loading"
        :data="recentProjects"
        border
        stripe
        style="width: 100%"
        class="project-table"
      >
        <el-table-column prop="projectId" label="项目编号" width="120" />
        <el-table-column prop="name" label="项目名称" min-width="260" show-overflow-tooltip />

        <el-table-column label="类型" width="110">
          <template #default="scope">
            <el-tag :type="getProjectTypeTag(scope.row.projectType)" effect="light">
              {{ getProjectTypeText(scope.row.projectType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="leaderId" label="负责人" width="140">
          <template #default="scope">
            <span class="leader-cell">编号：{{ scope.row.leaderId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="startDate" label="开始日期" width="130">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="endDate" label="结束日期" width="130">
          <template #default="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewProject(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="recentProjects.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无项目数据">
          <el-button type="primary" @click="$router.push('/projects/create')">
            创建第一个项目
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, View, ArrowRight } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '../utils/request'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const recentProjects = ref([])

// 当前登录用户名
const userName = computed(
  () => authStore.user?.realName || authStore.user?.username || 'admin'
)

// 当前日期字符串
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})


/** ====== ECharts 相关 ====== */
const projectChartRef = ref(null)
let chartInstance = null

onMounted(() => {
  loadDashboardData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', handleResize)
})

// 加载仪表盘数据：调用 /api/project/recent
async function loadDashboardData() {
  loading.value = true
  try {
    const res = await request.get('/project/recent')
    const data = Array.isArray(res) ? res : res.data || res.records || []

    recentProjects.value = data
    initChart()
  } catch (e) {
    console.error('加载最近项目失败:', e)
    recentProjects.value = []
  } finally {
    loading.value = false
  }
}

// 初始化 / 更新图表：按项目类型统计数量
function initChart() {
  if (!projectChartRef.value) return

  if (!chartInstance) {
    chartInstance = echarts.init(projectChartRef.value)
  }

  const typeCountMap = {}

  recentProjects.value.forEach((p) => {
    const t = p.projectType || 'UNKNOWN'
    typeCountMap[t] = (typeCountMap[t] || 0) + 1
  })

  const typeKeys = Object.keys(typeCountMap)
  const xData = typeKeys.map((k) => getProjectTypeText(k))
  const yData = typeKeys.map((k) => typeCountMap[k])

const option = {
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    top: 40,
    left: 50,
    right: 30,
    bottom: 30
  },
  xAxis: {
    type: 'category',
    data: xData,
    axisLine: {
      lineStyle: {
        color: '#999',
        width: 1
      }
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: '#333',
      fontSize: 13
    }
  },
  yAxis: {
    type: 'value',
    name: '数量',
    nameTextStyle: {
      color: '#444',
      fontSize: 14,
      padding: [0, 30, 0, 0]
    },
    axisLine: {
      lineStyle: {
        color: '#999',
        width: 1
      }
    },
    axisLabel: {
      color: '#333',    // 纵坐标文字变深色
      fontSize: 13
    },
    splitLine: {
      show: true,
      lineStyle: {
        color: '#e0e0e0',  // 网格线灰色更清晰
        width: 1
      }
    }
  },
  series: [
    {
      name: '项目数量',
      type: 'bar',
      data: yData,
      barWidth: '45%',
      itemStyle: {
        color: '#4a72f5',  // 更高级的蓝色
        borderRadius: [4, 4, 0, 0]
      }
    }
  ]
}

  chartInstance.setOption(option)
}

function handleResize() {
  if (chartInstance) {
    chartInstance.resize()
  }
}

/** ====== 工具函数 ====== */

function getProjectTypeTag(type) {
  const map = {
    NATIONAL: 'danger',
    PROVINCIAL: 'warning',
    SCHOOL: 'success',
    HORIZONTAL: 'info'
  }
  return map[type] || 'info'
}

function getProjectTypeText(type) {
  const map = {
    NATIONAL: '国家级',
    PROVINCIAL: '省部级',
    SCHOOL: '校级',
    HORIZONTAL: '横向'
  }
  return map[type] || type || '其他'
}

function getStatusTag(status) {
  const map = {
    APPLICATION: 'info',
    ONGOING: 'success',
    CONCLUSION: 'warning',
    TERMINATED: 'danger'
  }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = {
    APPLICATION: '申报中',
    ONGOING: '在研',
    CONCLUSION: '已结题',
    TERMINATED: '已终止'
  }
  return map[status] || status || '未知'
}

function formatDate(val) {
  if (!val) return ''
  const d = typeof val === 'string' || typeof val === 'number' ? new Date(val) : val
  if (Number.isNaN(d.getTime())) return ''
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function viewProject(row) {
  router.push(`/projects/${row.projectId}`)
}
</script>

<style scoped>
.dashboard-container {
  padding: 16px 20px 24px;
  background: #f5f7fb;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

/* 顶部欢迎卡片 */
.welcome-banner {
  margin-bottom: 16px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  background: linear-gradient(120deg, #4f8cf5, #7f7cf5);
  color: #fff;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-actions > * + * {
  margin-left: 8px;
}

.welcome-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.welcome-tag {
  padding: 2px 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

/* 统计卡片区域 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 14px 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f8;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.1;
}

.stat-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #a0a3b1;
}

/* 最近项目卡片 */
.recent-projects {
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-left {
  display: flex;
  flex-direction: column;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.card-subtitle {
  font-size: 12px;
  color: #a0a3b1;
  margin-top: 2px;
}

/* 图表区域 */
.chart-wrapper {
  width: 100%;
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 8px;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-subtitle {
  font-size: 12px;
  color: #a0a3b1;
}

.chart-container {
  width: 100%;
  height: 260px;
}

/* 表格标题 */
.table-header-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 8px;
}

.table-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.table-subtitle {
  font-size: 12px;
  color: #a0a3b1;
}

/* 表格样式微调 */
.project-table {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  background-color: #f5f7fb !important;
  color: #606266;
  font-weight: 500;
}

:deep(.el-table__body-wrapper tr:hover > td) {
  background-color: #f0f5ff !important;
}

.leader-cell {
  white-space: nowrap;
}

/* 空状态 */
.empty-state {
  margin-top: 16px;
}
</style>
