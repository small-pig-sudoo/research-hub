<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">个人中心</h1>
          <p class="page-subtitle">管理您的个人信息和账户设置</p>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <!-- 左侧：个人信息 -->
      <el-card class="profile-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">个人信息</span>
            <el-button type="primary" text size="small" @click="editProfile">编辑</el-button>
          </div>
        </template>

        <div class="profile-info">
          <div class="avatar-section">
            <el-avatar :size="80" :src="user.avatar" class="user-avatar">
              {{ user.realName?.charAt(0) }}
            </el-avatar>
            <div class="avatar-actions">
              <el-button size="small">更换头像</el-button>
            </div>
          </div>

          <el-descriptions :column="1" class="user-details">
            <el-descriptions-item label="姓名">{{ user.realName || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ user.username || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag :type="getRoleTag(user.role)">{{ getRoleText(user.role) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ user.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="所属单位">{{ user.department || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ user.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="研究方向">{{ user.researchField || '未设置' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 右侧：统计信息 -->
      <div class="stats-sidebar">
        <el-card class="stats-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">项目统计</span>
            </div>
          </template>
          
          <div class="stats-list">
            <div class="stat-item" v-for="stat in userStats" :key="stat.label">
              <div class="stat-icon" :style="{ backgroundColor: stat.color }">
                <el-icon><component :is="stat.icon" /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="quick-actions-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷操作</span>
            </div>
          </template>
          
          <div class="actions-list">
            <el-button class="action-btn" @click="$router.push('/projects/create')">
              <el-icon><Plus /></el-icon>
              创建新项目
            </el-button>
            <el-button class="action-btn" @click="changePassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-button>
            <el-button class="action-btn" @click="exportData">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { Document, Clock, Check, Money, Plus, Lock, Download } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 确保用户数据有默认值
const user = computed(() => {
  return authStore.user || {
    realName: '',
    username: '',
    role: '',
    email: '',
    department: '',
    phone: '',
    researchField: ''
  }
})

// 用户统计信息
const userStats = ref([
  { label: '总项目数', value: 8, icon: Document, color: '#409EFF' },
  { label: '进行中', value: 5, icon: Clock, color: '#67C23A' },
  { label: '已结题', value: 2, icon: Check, color: '#E6A23C' },
  { label: '总经费', value: '125万', icon: Money, color: '#F56C6C' }
])

function editProfile() {
  ElMessage.info('编辑个人信息功能开发中...')
}

function changePassword() {
  ElMessage.info('修改密码功能开发中...')
}

function exportData() {
  ElMessage.info('导出数据功能开发中...')
}

function getRoleTag(role) {
  const roleMap = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return roleMap[role] || 'info'
}

function getRoleText(role) {
  const roleTextMap = {
    'ADMIN': '系统管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleTextMap[role] || role
}
</script>

<style scoped>
.profile-container {
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
  max-width: 1200px;
  margin: 0 auto;
}

.title-section .page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.title-section .page-subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 主要内容区域 */
.profile-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  max-width: 1200px;
  margin: 16px auto;
  padding: 0 16px;
}

/* 个人信息卡片 */
.profile-card {
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

.profile-info {
  padding: 8px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f2f5;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 32px;
  font-weight: bold;
}

.avatar-actions {
  flex: 1;
}

.user-details {
  margin-top: 16px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

/* 统计侧边栏 */
.stats-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-card,
.quick-actions-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f8f9fa;
  transition: all 0.3s;
}

.stat-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 20px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.actions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  justify-content: flex-start;
  height: 44px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }
  
  .profile-content {
    grid-template-columns: 1fr;
    padding: 0 8px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .avatar-actions {
    width: 100%;
    display: flex;
    justify-content: center;
  }
  
  .stats-list {
    grid-template-columns: 1fr;
  }
}
</style>