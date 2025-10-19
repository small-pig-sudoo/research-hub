<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-banner" v-if="auth.user">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ auth.user.realName }}！</h2>
          <p>今天是 {{ currentDate }}，祝您科研顺利！</p>
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

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-row :gutter="20" class="quick-actions-row">
      <el-col :xs="12" :sm="6" v-for="action in quickActions" :key="action.title">
        <el-card class="quick-action-card" shadow="hover" @click="handleQuickAction(action)">
          <div class="action-content">
            <div class="action-icon" :style="{ color: action.color }">
              <el-icon><component :is="action.icon" /></el-icon>
            </div>
            <div class="action-text">
              <div class="action-title">{{ action.title }}</div>
              <div class="action-desc">{{ action.description }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近项目 -->
    <el-card class="recent-projects">
      <template #header>
        <div class="card-header">
          <span>最近项目</span>
          <el-button type="primary" text @click="$router.push('/projects')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      
      <el-table :data="recentProjects" v-loading="loading" style="width: 100%">
        <el-table-column prop="projectId" label="项目编号" width="120" />
        <el-table-column prop="name" label="项目名称" min-width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getProjectTypeTag(scope.row.type)">{{ getProjectTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="principal" label="负责人" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewProject(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="recentProjects.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无项目数据">
          <el-button type="primary" @click="$router.push('/projects/create')">创建第一个项目</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus, View, ArrowRight, Document, Clock, Check, Money,
  Collection, User, Setting, DataBoard
} from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 统计数据
const stats = ref([
  { title: '项目总数', value: 0, icon: Document, color: '#409EFF' },
  { title: '进行中', value: 0, icon: Clock, color: '#67C23A' },
  { title: '已结题', value: 0, icon: Check, color: '#E6A23C' },
  { title: '总经费', value: '0元', icon: Money, color: '#F56C6C' }
])

// 快速操作
const quickActions = ref([
  { 
    title: '项目管理', 
    description: '创建和管理科研项目',
    icon: Collection, 
    color: '#409EFF',
    action: () => router.push('/projects')
  },
  { 
    title: '成员管理', 
    description: '管理项目成员和权限',
    icon: User, 
    color: '#67C23A',
    action: () => ElMessage.info('功能开发中...')
  },
  { 
    title: '数据统计', 
    description: '查看科研数据统计',
    icon: DataBoard, 
    color: '#E6A23C',
    action: () => ElMessage.info('功能开发中...')
  },
  { 
    title: '系统设置', 
    description: '配置系统参数',
    icon: Setting, 
    color: '#F56C6C',
    action: () => ElMessage.info('功能开发中...')
  }
])

// 最近项目数据
const recentProjects = ref([])

onMounted(() => {
  loadDashboardData()
})

function loadDashboardData() {
  loading.value = true
  // 模拟加载数据
  setTimeout(() => {
    // 模拟数据
    stats.value = [
      { title: '项目总数', value: 8, icon: Document, color: '#409EFF' },
      { title: '进行中', value: 5, icon: Clock, color: '#67C23A' },
      { title: '已结题', value: 2, icon: Check, color: '#E6A23C' },
      { title: '总经费', value: '1,250,000元', icon: Money, color: '#F56C6C' }
    ]

    recentProjects.value = [
      {
        id: 1,
        projectId: 'RH2024001',
        name: '人工智能在科研管理中的应用研究',
        type: 'NATIONAL',
        status: 'ONGOING',
        principal: '张教授',
        startDate: '2024-01-15',
        endDate: '2026-12-31'
      },
      {
        id: 2,
        projectId: 'RH2024002',
        name: '高校科研成果转化机制研究',
        type: 'PROVINCIAL',
        status: 'ONGOING',
        principal: '李教授',
        startDate: '2024-03-01',
        endDate: '2025-12-31'
      },
      {
        id: 3,
        projectId: 'RH2024003',
        name: '跨学科科研团队建设研究',
        type: 'SCHOOL',
        status: 'APPLICATION',
        principal: '王教授',
        startDate: '2024-06-01',
        endDate: '2026-05-31'
      }
    ]
    loading.value = false
  }, 1000)
}

function handleQuickAction(action) {
  action.action()
}

function viewProject(project) {
  router.push(`/projects/${project.id}`)
}

function getProjectTypeTag(type) {
  const typeMap = {
    'NATIONAL': 'danger',
    'PROVINCIAL': 'warning', 
    'SCHOOL': 'success',
    'HORIZONTAL': 'info'
  }
  return typeMap[type] || 'info'
}

function getProjectTypeText(type) {
  const typeTextMap = {
    'NATIONAL': '国家级',
    'PROVINCIAL': '省部级',
    'SCHOOL': '校级',
    'HORIZONTAL': '横向项目'
  }
  return typeTextMap[type] || type
}

function getStatusTag(status) {
  const statusMap = {
    'APPLICATION': 'info',
    'ONGOING': 'success',
    'CONCLUSION': 'warning',
    'TERMINATED': 'danger'
  }
  return statusMap[status] || 'info'
}

function getStatusText(status) {
  const statusTextMap = {
    'APPLICATION': '申报中',
    'ONGOING': '在研',
    'CONCLUSION': '已结题',
    'TERMINATED': '已终止'
  }
  return statusTextMap[status] || status
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-banner {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-banner :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.welcome-actions {
  display: flex;
  gap: 12px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.quick-actions-row {
  margin-bottom: 20px;
}

.quick-action-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
}

.quick-action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.action-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.action-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  background-color: #f5f7fa;
}

.action-icon .el-icon {
  font-size: 28px;
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.recent-projects {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .welcome-actions {
    justify-content: center;
  }
  
  .stat-card,
  .quick-action-card {
    height: 100px;
  }
  
  .stat-icon,
  .action-icon {
    width: 50px;
    height: 50px;
    margin-right: 12px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}
</style>