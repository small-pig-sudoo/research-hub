<template>
  <div class="project-detail-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/projects' }">项目管理</el-breadcrumb-item>
              <el-breadcrumb-item>项目详情</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <h1 class="page-title">{{ project.name }}</h1>
          <div class="project-meta">
            <el-tag :type="getTypeTag(project.type)" size="small">
              {{ getTypeText(project.type) }}
            </el-tag>
            <el-tag :type="getStatusTag(project.status)" size="small">
              {{ getStatusText(project.status) }}
            </el-tag>
            <span class="project-id">项目编号：{{ project.projectId }}</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button @click="$router.back()">返回</el-button>
          <el-button @click="handleEdit">编辑项目</el-button>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出信息
          </el-button>
        </div>
      </div>
    </div>

    <!-- 项目信息 -->
    <div class="content-grid">
      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">基本信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ project.name }}</el-descriptions-item>
          <el-descriptions-item label="项目编号">{{ project.projectId }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">{{ getTypeText(project.type) }}</el-descriptions-item>
          <el-descriptions-item label="项目状态">{{ getStatusText(project.status) }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ project.principal }}</el-descriptions-item>
          <el-descriptions-item label="总经费">{{ formatFunding(project.totalFunding) }} 元</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ project.startDate }}</el-descriptions-item>
          <el-descriptions-item label="预期结题">{{ project.expectedEndDate }}</el-descriptions-item>
          <el-descriptions-item label="实际结题" :span="2">
            {{ project.actualEndDate || '尚未结题' }}
          </el-descriptions-item>
          <el-descriptions-item label="合作单位" :span="2">
            {{ project.externalCollaborator || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 项目描述 -->
      <el-card class="description-card" shadow="never" v-if="project.description">
        <template #header>
          <div class="card-header">
            <span class="card-title">项目描述</span>
          </div>
        </template>
        <div class="description-content">
          {{ project.description }}
        </div>
      </el-card>

      <!-- 项目进度 -->
      <el-card class="progress-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">项目进度</span>
          </div>
        </template>
        <div class="progress-content">
          <el-steps :active="getProgressStep(project.status)" align-center>
            <el-step title="申报" description="项目申报阶段" />
            <el-step title="在研" description="项目研究阶段" />
            <el-step title="结题" description="项目结题阶段" />
          </el-steps>
          
          <div class="progress-stats">
            <div class="stat-item">
              <div class="stat-label">已进行时间</div>
              <div class="stat-value">{{ getElapsedTime() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">剩余时间</div>
              <div class="stat-value">{{ getRemainingTime() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">完成度</div>
              <div class="stat-value">{{ getCompletionRate() }}%</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 项目成员 -->
      <el-card class="members-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">项目成员</span>
            <el-button type="primary" text size="small">管理成员</el-button>
          </div>
        </template>
        <el-table :data="projectMembers" style="width: 100%">
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="role" label="角色">
            <template #default="scope">
              <el-tag :type="getMemberRoleTag(scope.row.role)" size="small">
                {{ scope.row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="department" label="所属单位" />
          <el-table-column prop="workload" label="工作量" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button size="small" text>查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 相关成果 -->
      <el-card class="achievements-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">相关成果</span>
            <el-button type="primary" text size="small">添加成果</el-button>
          </div>
        </template>
        <div class="achievements-content">
          <el-empty description="暂无相关成果" :image-size="100">
            <el-button type="primary">添加论文</el-button>
            <el-button>添加专利</el-button>
          </el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)

// 项目数据
const project = reactive({
  name: '',
  projectId: '',
  type: '',
  status: '',
  principal: '',
  totalFunding: 0,
  startDate: '',
  expectedEndDate: '',
  actualEndDate: '',
  description: '',
  externalCollaborator: ''
})

// 项目成员数据
const projectMembers = ref([
  { name: '张教授', role: '负责人', department: '计算机学院', workload: '30%' },
  { name: '李研究员', role: '核心成员', department: '人工智能研究院', workload: '25%' },
  { name: '王博士', role: '成员', department: '计算机学院', workload: '20%' },
  { name: '赵同学', role: '学生成员', department: '软件工程系', workload: '15%' }
])

onMounted(() => {
  loadProjectDetail()
})

function loadProjectDetail() {
  loading.value = true
  // 模拟加载项目详情
  setTimeout(() => {
    Object.assign(project, {
      name: '人工智能在科研管理中的应用研究',
      projectId: 'RH2024001',
      type: 'NATIONAL',
      status: 'ONGOING',
      principal: '张教授',
      totalFunding: 500000,
      startDate: '2024-01-01',
      expectedEndDate: '2026-12-31',
      description: '本研究旨在探索人工智能技术在高校科研管理中的应用，通过自然语言处理、机器学习等技术，提高科研项目管理的效率和质量。研究内容包括：科研项目智能推荐、科研成果自动分析、科研进度智能监控等。',
      externalCollaborator: '某人工智能研究院'
    })
    loading.value = false
  }, 800)
}

function handleEdit() {
  router.push(`/projects/${route.params.id}/edit`)
}

function handleExport() {
  ElMessage.info('导出功能开发中...')
}

function getTypeTag(type) {
  const typeMap = {
    'NATIONAL': 'danger',
    'PROVINCIAL': 'warning',
    'SCHOOL': 'success',
    'HORIZONTAL': 'info'
  }
  return typeMap[type] || 'info'
}

function getTypeText(type) {
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

function getMemberRoleTag(role) {
  const roleMap = {
    '负责人': 'danger',
    '核心成员': 'warning',
    '成员': 'success',
    '学生成员': 'info'
  }
  return roleMap[role] || 'info'
}

function formatFunding(amount) {
  if (!amount) return '0'
  return amount.toLocaleString('zh-CN')
}

function getProgressStep(status) {
  const stepMap = {
    'APPLICATION': 1,
    'ONGOING': 2,
    'CONCLUSION': 3,
    'TERMINATED': 0
  }
  return stepMap[status] || 0
}

function getElapsedTime() {
  if (!project.startDate) return '--'
  const start = new Date(project.startDate)
  const now = new Date()
  const diffTime = Math.abs(now - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return `${diffDays} 天`
}

function getRemainingTime() {
  if (!project.expectedEndDate) return '--'
  const end = new Date(project.expectedEndDate)
  const now = new Date()
  const diffTime = Math.abs(end - now)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return `${diffDays} 天`
}

function getCompletionRate() {
  if (!project.startDate || !project.expectedEndDate) return 0
  
  const start = new Date(project.startDate)
  const end = new Date(project.expectedEndDate)
  const now = new Date()
  
  const totalDuration = end - start
  const elapsedDuration = now - start
  
  if (elapsedDuration <= 0) return 0
  if (elapsedDuration >= totalDuration) return 100
  
  return Math.round((elapsedDuration / totalDuration) * 100)
}
</script>

<style scoped>
.project-detail-container {
  padding: 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面标题区域 */
.page-header {
  background: white;
  padding: 24px;
  border-bottom: 1px solid #e4e7ed;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 12px;
}

.title-section .page-title {
  margin: 0 0 16px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.project-id {
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 内容网格布局 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 16px;
  max-width: 1200px;
  margin: 16px auto;
  padding: 0 16px;
}

.content-grid > * {
  min-width: 0;
}

.info-card {
  grid-column: 1 / -1;
}

.description-card {
  grid-column: 1 / -1;
}

.progress-card {
  grid-column: 1;
}

.members-card {
  grid-column: 2;
}

.achievements-card {
  grid-column: 1 / -1;
}

/* 卡片样式 */
.info-card,
.description-card,
.progress-card,
.members-card,
.achievements-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 描述内容 */
.description-content {
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
}

/* 进度内容 */
.progress-content {
  padding: 8px;
}

.progress-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
    padding: 0 8px;
  }
  
  .content-grid > * {
    grid-column: 1 !important;
  }
  
  .progress-stats {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>