<template>
  <div class="member-list-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">成员管理</h1>
          <p class="page-subtitle">管理项目成员和职责分工</p>
        </div>
        <el-button type="primary" class="create-btn" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          添加成员
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="filter-section" shadow="never">
      <div class="filter-header">
        <h3 class="filter-title">筛选条件</h3>
        <div class="filter-actions">
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" @click="handleSearch">
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
                v-model="filterForm.projectName"
                placeholder="输入项目名称"
                clearable
                size="large"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="成员姓名">
              <el-input
                v-model="filterForm.memberName"
                placeholder="输入成员姓名"
                clearable
                size="large"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="职责角色">
              <el-select
                v-model="filterForm.role"
                placeholder="选择职责角色"
                clearable
                size="large"
                style="width: 100%"
              >
                <el-option label="项目负责人" value="LEADER" />
                <el-option label="核心成员" value="CORE" />
                <el-option label="参与成员" value="MEMBER" />
                <el-option label="顾问" value="ADVISOR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="状态">
              <el-select
                v-model="filterForm.status"
                placeholder="选择状态"
                clearable
                size="large"
                style="width: 100%"
              >
                <el-option label="活跃" value="ACTIVE" />
                <el-option label="已退出" value="EXIT" />
                <el-option label="暂停" value="PAUSED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 成员表格 -->
    <el-card class="table-section" shadow="never">
      <div class="table-header">
        <div class="table-info">
          <span class="total-text">共 {{ pagination.total }} 个成员</span>
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
        :data="members"
        v-loading="loading"
        class="member-table"
        style="width: 100%"
        table-layout="auto"
        :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="projectName" label="项目名称" min-width="150" />
        <el-table-column prop="memberName" label="成员姓名" min-width="120" />
        <el-table-column prop="role" label="职责角色" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duty" label="具体职责" min-width="150" />
        <el-table-column prop="joinDate" label="加入时间" min-width="140" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">查看详情</el-button>
            <el-button size="small" type="primary" plain @click="editMember(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialog.visible" :title="'成员详情'" width="600px">
      <div v-if="detailDialog.data" class="member-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="项目名称">{{ detailDialog.data.projectName }}</el-descriptions-item>
          <el-descriptions-item label="成员姓名">{{ detailDialog.data.memberName }}</el-descriptions-item>
          <el-descriptions-item label="职责角色">{{ getRoleLabel(detailDialog.data.role) }}</el-descriptions-item>
          <el-descriptions-item label="具体职责">{{ detailDialog.data.duty || '无' }}</el-descriptions-item>
          <el-descriptions-item label="加入时间">{{ detailDialog.data.joinDate }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusLabel(detailDialog.data.status) }}</el-descriptions-item>
          <el-descriptions-item label="贡献度">{{ detailDialog.data.contribution || '0%' }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ detailDialog.data.contactInfo || '无' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailDialog.data.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialog.visible" :title="editDialog.id ? '编辑成员' : '添加成员'" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="项目名称" prop="projectId">
          <el-select v-model="editForm.projectId" placeholder="选择项目" style="width: 100%">
            <el-option v-for="project in projects" :key="project.project_id" :label="project.name" :value="project.project_id" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员姓名" prop="memberId">
          <el-select v-model="editForm.memberId" placeholder="选择成员" style="width: 100%">
            <el-option v-for="user in users" :key="user.user_id" :label="user.real_name" :value="user.user_id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职责角色" prop="role">
          <el-select v-model="editForm.role" placeholder="选择职责角色" style="width: 100%">
            <el-option label="项目负责人" value="LEADER" />
            <el-option label="核心成员" value="CORE" />
            <el-option label="参与成员" value="MEMBER" />
            <el-option label="顾问" value="ADVISOR" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体职责" prop="duty">
          <el-input v-model="editForm.duty" placeholder="输入具体职责" />
        </el-form-item>
        <el-form-item label="加入时间" prop="joinDate">
          <el-date-picker v-model="editForm.joinDate" type="date" placeholder="选择加入时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="选择状态" style="width: 100%">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="已退出" value="EXIT" />
            <el-option label="暂停" value="PAUSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="贡献度">
          <el-input-number v-model="editForm.contribution" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="editForm.contactInfo" placeholder="输入联系方式" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" placeholder="输入备注信息" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveMember">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/member'
import * as userApi from '@/api/user'
import * as projectApi from '@/api/project'
import { Search, Plus, Download, Refresh } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const members = ref([])
const projects = ref([])
const users = ref([])

// 筛选表单
const filterForm = ref({
  projectName: '',
  memberName: '',
  role: '',
  status: ''
})

// 分页
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

// 详情对话框
const detailDialog = ref({
  visible: false,
  data: null
})

// 编辑对话框
const editDialog = ref({
  visible: false,
  id: null
})

// 编辑表单
const editForm = ref({
  projectId: null,
  memberId: null,
  role: '',
  duty: '',
  joinDate: '',
  status: 'ACTIVE',
  contribution: 0,
  contactInfo: '',
  remark: ''
})

const editFormRef = ref(null)

// 编辑表单规则
const editRules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'blur' }],
  memberId: [{ required: true, message: '请选择成员', trigger: 'blur' }],
  role: [{ required: true, message: '请选择职责角色', trigger: 'blur' }],
  duty: [{ required: true, message: '请输入具体职责', trigger: 'blur' }],
  joinDate: [{ required: true, message: '请选择加入时间', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }]
}

// 加载项目成员列表
const loadMembers = async () => {
  loading.value = true
  try {
    const params = {
      projectId: filterForm.value.projectId
    }
    const res = await memberApi.getProjectMembers(params)
    members.value = res.data || []
    pagination.value.total = members.value.length
  } catch (error) {
    ElMessage.error('加载成员列表失败')
  } finally {
    loading.value = false
  }
}

// 加载项目列表
const loadProjects = async () => {
  try {
    const res = await projectApi.listProjects({ page: 1, pageSize: 100 })
    projects.value = res.data.list || []
  } catch (error) {
    console.error('加载项目列表失败', error)
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await userApi.getUsers({ page: 1, pageSize: 100 })
    users.value = res.data.records || []
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadMembers()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    projectName: '',
    memberName: '',
    role: '',
    status: ''
  }
  handleSearch()
}

// 刷新数据
const refreshData = () => {
  loadMembers()
}

// 分页
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  loadMembers()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  loadMembers()
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const res = await memberApi.getMemberDetail(row.id)
    detailDialog.value.data = res.data
    detailDialog.value.visible = true
  } catch (error) {
    ElMessage.error('获取成员详情失败')
  }
}

// 打开编辑
const editMember = (row) => {
  editDialog.value.id = row.id
  editForm.value = {
    projectId: row.projectId,
    memberId: row.memberId,
    role: row.role,
    contribution: row.contribution || 0,
    contactInfo: row.contactInfo || '',
    remark: row.remark || ''
  }
  editDialog.value.visible = true
}

// 打开添加
const handleCreate = () => {
  editDialog.value.id = null
  editForm.value = {
    projectId: null,
    memberId: null,
    role: '',
    duty: '',
    joinDate: '',
    status: 'ACTIVE',
    contribution: 0,
    contactInfo: '',
    remark: ''
  }
  editDialog.value.visible = true
}

// 保存成员
const saveMember = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 重复校验：检查是否已存在相同的项目和成员组合
        const isDuplicate = members.value.some(member => 
          member.projectId == editForm.value.projectId && 
          member.memberId == editForm.value.memberId &&
          member.id != editDialog.value.id
        )
        
        if (isDuplicate) {
          ElMessage.error('该成员已经在该项目中存在')
          return
        }
        
        if (editDialog.value.id) {
          // 编辑成员：先移除旧成员，再添加新成员
          await memberApi.removeMember(editForm.value.projectId, editForm.value.memberId)
          await memberApi.addMember({
            projectId: editForm.value.projectId,
            userId: editForm.value.memberId,
            role: editForm.value.role,
            duty: editForm.value.duty,
            joinDate: editForm.value.joinDate,
            status: editForm.value.status,
            contribution: editForm.value.contribution,
            contactInfo: editForm.value.contactInfo,
            remark: editForm.value.remark
          })
          ElMessage.success('编辑成功')
        } else {
          // 添加成员
          await memberApi.addMember({
            projectId: editForm.value.projectId,
            userId: editForm.value.memberId,
            role: editForm.value.role,
            duty: editForm.value.duty,
            joinDate: editForm.value.joinDate,
            status: editForm.value.status,
            contribution: editForm.value.contribution,
            contactInfo: editForm.value.contactInfo,
            remark: editForm.value.remark
          })
          ElMessage.success('添加成功')
        }
        editDialog.value.visible = false
        loadMembers()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 移除成员
const removeMember = (row) => {
  ElMessageBox.confirm(`确定要移除成员 ${row.memberName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await memberApi.removeMember(row.projectId, row.memberId)
      ElMessage.success('移除成功')
      loadMembers()
    } catch (error) {
      ElMessage.error('移除失败')
    }
  })
}

// 导出数据
const exportData = async () => {
  try {
    ElMessage.info('导出功能暂未实现')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 获取角色标签类型
const getRoleType = (role) => {
  const typeMap = {
    LEADER: 'primary',
    CORE: 'success',
    MEMBER: 'info',
    ADVISOR: 'warning'
  }
  return typeMap[role] || 'info'
}

// 获取角色标签文本
const getRoleLabel = (role) => {
  const labelMap = {
    LEADER: '项目负责人',
    CORE: '核心成员',
    MEMBER: '参与成员',
    ADVISOR: '顾问'
  }
  return labelMap[role] || role
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    ACTIVE: 'success',
    EXIT: 'danger',
    PAUSED: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  const labelMap = {
    ACTIVE: '活跃',
    EXIT: '已退出',
    PAUSED: '暂停'
  }
  return labelMap[status] || status
}

// 初始化
onMounted(() => {
  loadMembers()
  loadProjects()
  loadUsers()
})
</script>

<style scoped>
.member-list-container {
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

.create-btn {
  white-space: nowrap;
}

.filter-section {
  margin-bottom: 20px;
  border-radius: 8px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.filter-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.filter-form {
  margin: 0;
}

.table-section {
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.table-info {
  font-size: 14px;
  color: #606266;
}

.total-text {
  font-weight: 500;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.member-table {
  border-radius: 4px;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.member-detail {
  padding: 10px 0;
}

/* 为下拉框添加样式，让下拉菜单可以根据内容自动调整宽度 */
:deep(.el-select-dropdown) {
  min-width: 200px;
  width: auto !important;
  max-width: 400px;
}

:deep(.el-select-dropdown__item) {
  white-space: normal;
  word-break: break-all;
  padding: 8px 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .member-list-container {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-actions {
    align-self: stretch;
    justify-content: flex-end;
  }
  
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .table-actions {
    align-self: stretch;
    justify-content: flex-end;
  }
  
  .pagination-container {
    justify-content: center;
  }
}
</style>