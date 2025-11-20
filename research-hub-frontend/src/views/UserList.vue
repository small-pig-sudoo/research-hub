<template>
  <div class="user-list-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">用户管理</h1>
          <p class="page-subtitle">管理系统用户和权限</p>
        </div>
        <el-button type="primary" class="create-btn" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="filter-section" shadow="never">
      <div class="filter-header">
        <h3 class="filter-title">筛选条件</h3>
        <div class="filter-actions">
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="loadUsers">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>
      
      <el-form :model="filterForm" class="filter-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="用户名">
              <el-input 
                v-model="filterForm.username" 
                placeholder="输入用户名" 
                clearable
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="姓名">
              <el-input 
                v-model="filterForm.realName" 
                placeholder="输入姓名" 
                clearable
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="角色">
              <el-select 
                v-model="filterForm.role" 
                placeholder="选择角色" 
                clearable
                size="large"
                style="width: 100%"
              >
                <el-option label="管理员" value="ADMIN" />
                <el-option label="教师" value="TEACHER" />
                <el-option label="学生" value="STUDENT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 用户表格 -->
    <el-card class="table-section" shadow="never">
      <div class="table-header">
        <div class="table-info">
          <span class="total-text">共 {{ pagination.total }} 个用户</span>
        </div>
        <div class="table-actions">
          <el-button text @click="exportData">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
          <el-button text @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <el-table 
        :data="users" 
        v-loading="loading"
        class="user-table"
        :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="getRoleTag(scope.row.role)" 
              size="small"
              class="role-tag"
            >
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所属单位" width="150" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="researchField" label="研究方向" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="150" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'" 
              size="small"
            >
              {{ scope.row.status === 'ACTIVE' ? '激活' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-tooltip content="编辑" placement="top">
                <el-button 
                  size="small" 
                  text 
                  class="action-btn edit-btn"
                  @click="handleEdit(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="重置密码" placement="top">
                <el-button 
                  size="small" 
                  text 
                  class="action-btn reset-btn"
                  @click="handleResetPassword(scope.row)"
                >
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button 
                  size="small" 
                  text 
                  class="action-btn delete-btn"
                  @click="handleDelete(scope.row)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <div v-if="users.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无用户数据" :image-size="200">
          <el-button type="primary" @click="handleCreate">创建第一个用户</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="users.length > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUsers"
          @current-change="loadUsers"
          class="custom-pagination"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="formDialog.title"
      v-model="formDialog.visible"
      width="600px"
      :before-close="handleDialogClose"
    >
      <UserForm
        v-if="formDialog.visible"
        :user="formDialog.user"
        :mode="formDialog.mode"
        @submit="handleFormSubmit"
        @cancel="formDialog.visible = false"
      />
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      title="重置密码"
      v-model="resetDialog.visible"
      width="400px"
    >
      <el-form :model="resetForm" label-width="100px">
        <el-form-item label="新密码">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="resetDialog.loading" @click="handleResetSubmit">
          确认重置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Download, Refresh, Edit, Delete 
} from '@element-plus/icons-vue'
import { getUsers, deleteUser, resetPassword } from '../api/user'
import UserForm from '../components/UserForm.vue'

const loading = ref(false)
const users = ref([])

const filterForm = reactive({
  username: '',
  realName: '',
  role: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formDialog = reactive({
  visible: false,
  title: '',
  mode: 'create', // 'create' or 'edit'
  user: null
})

const resetDialog = reactive({
  visible: false,
  loading: false
})

const resetForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...filterForm
    }
    
    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '') {
        delete params[key]
      }
    })

    const response = await getUsers(params)
    if (response.code === 200 && response.data) {
      users.value = response.data.list || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  Object.assign(filterForm, {
    username: '',
    realName: '',
    role: ''
  })
  loadUsers()
}

function handleCreate() {
  formDialog.title = '创建用户'
  formDialog.mode = 'create'
  formDialog.user = null
  formDialog.visible = true
}

function handleEdit(user) {
  formDialog.title = '编辑用户'
  formDialog.mode = 'edit'
  formDialog.user = { ...user }
  formDialog.visible = true
}

function handleResetPassword(user) {
  resetDialog.user = user
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
  resetDialog.visible = true
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(
      `确定删除用户 "${user.realName}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteUser(user.userId)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    // 用户取消删除
  }
}

async function handleResetSubmit() {
  if (!resetForm.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  
  if (resetForm.newPassword !== resetForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  if (resetForm.newPassword.length < 6) {
    ElMessage.error('密码长度至少6位')
    return
  }
  
  resetDialog.loading = true
  try {
    await resetPassword(resetDialog.user.userId, resetForm.newPassword)
    ElMessage.success('密码重置成功')
    resetDialog.visible = false
  } catch (error) {
    ElMessage.error('密码重置失败')
  } finally {
    resetDialog.loading = false
  }
}

function handleFormSubmit() {
  formDialog.visible = false
  loadUsers()
}

function handleDialogClose(done) {
  ElMessageBox.confirm('确定要关闭吗？未保存的内容将丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    done()
  }).catch(() => {
    // 取消关闭
  })
}

function exportData() {
  ElMessage.info('导出功能开发中...')
}

function refreshData() {
  loadUsers()
  ElMessage.success('数据已刷新')
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
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleTextMap[role] || role
}
</script>

<style scoped>
.user-list-container {
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

.create-btn {
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
}

/* 筛选区域 */
.filter-section {
  margin: 16px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.filter-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.filter-form {
  margin-top: 8px;
}

/* 表格区域 */
.table-section {
  margin: 16px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 4px;
}

.total-text {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.table-actions {
  display: flex;
  gap: 8px;
}

/* 表格样式 */
.user-table {
  width: 100%;
}

.user-table :deep(.el-table__header-wrapper) {
  border-radius: 8px 8px 0 0;
}

.user-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.role-tag {
  font-weight: 500;
  border: none;
  border-radius: 12px;
  padding: 4px 8px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 4px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  transition: all 0.2s;
}

.edit-btn {
  color: #409eff;
}

.edit-btn:hover {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.reset-btn {
  color: #e6a23c;
}

.reset-btn:hover {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
}

/* 分页 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.custom-pagination {
  padding: 16px 0;
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
  
  .filter-section,
  .table-section {
    margin: 8px;
  }
  
  .filter-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .action-buttons {
    justify-content: flex-start;
  }
}
</style>