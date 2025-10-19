<template>
  <div class="project-list-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">科研项目管理</h1>
          <p class="page-subtitle">管理您的科研项目信息</p>
        </div>
        <el-button type="primary" class="create-btn" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增项目
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="filter-section" shadow="never">
      <div class="filter-header">
        <h3 class="filter-title">筛选条件</h3>
        <div class="filter-actions">
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="loadProjects">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>
      
      <el-form :model="filterForm" class="filter-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="项目名称">
              <el-input 
                v-model="filterForm.name" 
                placeholder="输入项目名称" 
                clearable
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="项目类型">
              <el-select 
                v-model="filterForm.type" 
                placeholder="选择类型" 
                clearable
                size="large"
                style="width: 100%"
              >
                <el-option label="国家级" value="NATIONAL" />
                <el-option label="省部级" value="PROVINCIAL" />
                <el-option label="校级" value="SCHOOL" />
                <el-option label="横向项目" value="HORIZONTAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="项目状态">
              <el-select 
                v-model="filterForm.status" 
                placeholder="选择状态" 
                clearable
                size="large"
                style="width: 100%"
              >
                <el-option label="申报中" value="APPLICATION" />
                <el-option label="在研" value="ONGOING" />
                <el-option label="已结题" value="CONCLUSION" />
                <el-option label="已终止" value="TERMINATED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 项目表格 -->
    <el-card class="table-section" shadow="never">
      <div class="table-header">
        <div class="table-info">
          <span class="total-text">共 {{ pagination.total }} 个项目</span>
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
        :data="projects" 
        v-loading="loading"
        class="project-table"
        :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="projectId" label="项目编号" width="130" />
        <el-table-column prop="name" label="项目名称" min-width="240">
          <template #default="scope">
            <div class="project-name-cell">
              <span class="project-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="getTypeTag(scope.row.type)" 
              size="small"
              class="type-tag"
            >
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="principal" label="负责人" width="120" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="getStatusTag(scope.row.status)" 
              size="small"
              class="status-tag"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalFunding" label="经费(元)" width="140" align="right">
          <template #default="scope">
            <span class="funding-amount">{{ formatFunding(scope.row.totalFunding) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" align="center" />
        <el-table-column prop="expectedEndDate" label="预期结题" width="120" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-tooltip content="查看详情" placement="top">
                <el-button 
                  size="small" 
                  text 
                  class="action-btn view-btn"
                  @click="handleView(scope.row)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑项目" placement="top">
                <el-button 
                  size="small" 
                  text 
                  class="action-btn edit-btn"
                  @click="handleEdit(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除项目" placement="top">
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
      <div v-if="projects.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无项目数据" :image-size="200">
          <el-button type="primary" @click="handleCreate">创建第一个项目</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="projects.length > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProjects"
          @current-change="loadProjects"
          class="custom-pagination"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Download, Refresh, View, Edit, Delete 
} from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const projects = ref([])

const filterForm = reactive({
  name: '',
  type: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  loadProjects()
})

async function loadProjects() {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 模拟数据
    projects.value = [
      {
        id: 1,
        projectId: 'RH2024001',
        name: '人工智能在科研管理中的应用研究',
        type: 'NATIONAL',
        principal: '张教授',
        status: 'ONGOING',
        totalFunding: 500000,
        startDate: '2024-01-01',
        expectedEndDate: '2026-12-31'
      },
      {
        id: 2,
        projectId: 'RH2024002',
        name: '高校科研成果转化机制研究',
        type: 'PROVINCIAL',
        principal: '李教授',
        status: 'ONGOING',
        totalFunding: 300000,
        startDate: '2024-03-01',
        expectedEndDate: '2025-12-31'
      },
      {
        id: 3,
        projectId: 'RH2024003',
        name: '跨学科科研团队建设研究',
        type: 'SCHOOL',
        principal: '王教授',
        status: 'APPLICATION',
        totalFunding: 100000,
        startDate: '2024-06-01',
        expectedEndDate: '2026-05-31'
      }
    ]
    pagination.total = 3
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  Object.assign(filterForm, {
    name: '',
    type: '',
    status: ''
  })
  loadProjects()
}

function handleCreate() {
  router.push('/projects/create')
}

function handleView(project) {
  router.push(`/projects/${project.id}`)
}

function handleEdit(project) {
  router.push(`/projects/${project.id}/edit`)
}

async function handleDelete(project) {
  try {
    await ElMessageBox.confirm(
      `确定删除项目 "${project.name}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'delete-confirm-btn',
        customClass: 'delete-confirm-dialog'
      }
    )
    
    // 模拟删除
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('删除成功')
    loadProjects()
  } catch (error) {
    // 用户取消删除
  }
}

function exportData() {
  ElMessage.info('导出功能开发中...')
}

function refreshData() {
  loadProjects()
  ElMessage.success('数据已刷新')
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

function formatFunding(amount) {
  if (!amount) return '0'
  return amount.toLocaleString('zh-CN')
}
</script>

<style scoped>
.project-list-container {
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
.project-table {
  width: 100%;
}

.project-table :deep(.el-table__header-wrapper) {
  border-radius: 8px 8px 0 0;
}

.project-table :deep(.el-table__row) {
  transition: background-color 0.2s;
}

.project-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.project-name-cell {
  display: flex;
  align-items: center;
}

.project-name {
  font-weight: 500;
  color: #303133;
  line-height: 1.4;
}

.type-tag,
.status-tag {
  font-weight: 500;
  border: none;
  border-radius: 12px;
  padding: 4px 8px;
}

.funding-amount {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-weight: 600;
  color: #e6a23c;
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

.view-btn {
  color: #409eff;
}

.view-btn:hover {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.edit-btn {
  color: #67c23a;
}

.edit-btn:hover {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
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

/* 删除确认对话框样式 */
:deep(.delete-confirm-dialog) {
  border-radius: 12px;
}

:deep(.delete-confirm-btn) {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

:deep(.delete-confirm-btn:hover) {
  background-color: #f78989;
  border-color: #f78989;
}
</style>