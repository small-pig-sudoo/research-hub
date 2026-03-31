<template>
  <div class="member-assign-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">成员分配</h1>
          <p class="page-subtitle">为项目分配成员并设置角色职责</p>
        </div>
      </div>
    </div>

    <!-- 分配表单 -->
    <el-card class="assign-form-section" shadow="never">
      <el-form :model="assignForm" :rules="assignRules" ref="assignFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="assignForm.projectName" placeholder="输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="选择成员" prop="memberId">
              <el-select v-model="assignForm.memberId" placeholder="选择成员" style="width: 100%">
                <el-option v-for="user in users" :key="user.user_id" :label="user.real_name" :value="user.user_id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="职责角色" prop="role">
              <el-select v-model="assignForm.role" placeholder="选择职责角色" style="width: 100%">
                <el-option label="项目负责人" value="LEADER" />
                <el-option label="核心成员" value="CORE" />
                <el-option label="参与成员" value="MEMBER" />
                <el-option label="顾问" value="ADVISOR" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="具体职责" prop="duty">
              <el-input v-model="assignForm.duty" placeholder="输入具体职责" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="加入时间" prop="joinDate">
              <el-date-picker v-model="assignForm.joinDate" type="date" placeholder="选择加入时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="assignForm.status" placeholder="选择状态" style="width: 100%">
                <el-option label="活跃" value="ACTIVE" />
                <el-option label="已退出" value="EXIT" />
                <el-option label="暂停" value="PAUSED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="贡献度">
              <el-input-number v-model="assignForm.contribution" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="16">
            <el-form-item label="联系方式">
              <el-input v-model="assignForm.contactInfo" placeholder="输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24">
            <el-form-item label="备注">
              <el-input v-model="assignForm.remark" type="textarea" placeholder="输入备注信息" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" class="form-actions">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="saveAssign">保存分配</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 已分配成员列表 -->
    <el-card class="assigned-list-section" shadow="never" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>已分配成员列表</h3>
        </div>
      </template>

      <el-table
        :data="assignedMembers"
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
        <el-table-column label="操作" min-width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editMember(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/member'
import * as userApi from '@/api/user'
import * as projectApi from '@/api/project'

// 响应式数据
const loading = ref(false)
const projects = ref([])
const users = ref([])
const assignedMembers = ref([])

// 分配表单
const assignForm = ref({
  projectName: '',
  memberId: null,
  role: '',
  duty: '',
  joinDate: '',
  status: 'ACTIVE',
  contribution: 0,
  contactInfo: '',
  remark: ''
})

const assignFormRef = ref(null)

// 分配表单规则
const assignRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  memberId: [{ required: true, message: '请选择成员', trigger: 'blur' }],
  role: [{ required: true, message: '请选择职责角色', trigger: 'blur' }],
  duty: [{ required: true, message: '请输入具体职责', trigger: 'blur' }],
  joinDate: [{ required: true, message: '请选择加入时间', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }]
}

// 加载项目列表
const loadProjects = async () => {
  try {
    const res = await projectApi.getProjects({ page: 1, pageSize: 100 })
    projects.value = res.data.records || []
  } catch (error) {
    console.error('加载项目列表失败', error)
  }
}

// 根据项目名称查找项目ID
const getProjectIdByName = (projectName) => {
  const project = projects.value.find(p => p.name === projectName)
  return project ? project.project_id : null
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

// 加载已分配成员列表
const loadAssignedMembers = async () => {
  loading.value = true
  try {
    // 这里暂时不传递 projectId，获取所有成员
    const res = await memberApi.getProjectMembers({})
    assignedMembers.value = res.data || []
  } catch (error) {
    ElMessage.error('加载已分配成员列表失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (assignFormRef.value) {
    assignFormRef.value.resetFields()
  }
  assignForm.value = {
    projectName: '',
    memberId: null,
    role: '',
    duty: '',
    joinDate: '',
    status: 'ACTIVE',
    contribution: 0,
    contactInfo: '',
    remark: ''
  }
}

// 保存分配
const saveAssign = async () => {
  if (!assignFormRef.value) return
  await assignFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 根据项目名称查找项目ID
        const projectId = getProjectIdByName(assignForm.value.projectName)
        if (!projectId) {
          ElMessage.error('项目名称不存在，请检查输入')
          return
        }
        
        // 重复校验：检查是否已存在相同的项目和成员组合
        const isDuplicate = assignedMembers.value.some(member => 
          member.projectId == projectId && 
          member.memberId == assignForm.value.memberId
        )
        
        if (isDuplicate) {
          ElMessage.error('该成员已经在该项目中存在')
          return
        }
        
        // 调用添加成员的API
        await memberApi.addMember({
          projectId: projectId,
          userId: assignForm.value.memberId,
          role: assignForm.value.role,
          duty: assignForm.value.duty,
          joinDate: assignForm.value.joinDate,
          status: assignForm.value.status,
          contribution: assignForm.value.contribution,
          contactInfo: assignForm.value.contactInfo,
          remark: assignForm.value.remark
        })
        
        ElMessage.success('分配成功')
        resetForm()
        loadAssignedMembers()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 编辑成员
const editMember = (row) => {
  assignForm.value = {
    projectId: row.projectId,
    memberId: row.memberId,
    role: row.role,
    duty: row.duty || '',
    joinDate: row.joinDate || '',
    status: row.status || 'ACTIVE',
    contribution: row.contribution || 0,
    contactInfo: row.contactInfo || '',
    remark: row.remark || ''
  }
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
      loadAssignedMembers()
    } catch (error) {
      ElMessage.error('移除失败')
    }
  })
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
  loadProjects()
  loadUsers()
  loadAssignedMembers()
})
</script>

<style scoped>
.member-assign-container {
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

.assign-form-section {
  border-radius: 8px;
  margin-bottom: 20px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.assigned-list-section {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.member-table {
  border-radius: 4px;
  overflow: hidden;
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
  .member-assign-container {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>