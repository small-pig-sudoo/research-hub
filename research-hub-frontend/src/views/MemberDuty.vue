<template>
  <div class="duty-list-container">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">职责分工</h1>
          <p class="page-subtitle">管理项目职责列表</p>
        </div>
        <div class="action-section">
          <el-button type="primary" @click="handleCreate">新增职责</el-button>
        </div>
      </div>
    </div>

    <!-- 职责列表 -->
    <el-card class="list-section" shadow="never">
      <template #header>
        <div class="card-header">
          <h3>职责列表</h3>
          <div class="filter-section">
            <el-input
              v-model="filterForm.name"
              placeholder="输入职责名称"
              style="width: 200px"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button @click="resetFilter" style="margin-left: 10px">重置</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="duties"
        v-loading="loading"
        class="duty-table"
        style="width: 100%"
        table-layout="auto"
        :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="职责名称" min-width="150" />
        <el-table-column prop="description" label="职责描述" min-width="200" />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160" />
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">
              删除
            </el-button>
            <el-button size="small" @click="handleToggleStatus(row)">
              {{ row.status === 'ENABLED' ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="dutyForm" :rules="dutyRules" ref="dutyFormRef" label-width="100px">
        <el-form-item label="职责名称" prop="name">
          <el-input v-model="dutyForm.name" placeholder="输入职责名称" />
        </el-form-item>
        <el-form-item label="职责描述" prop="description">
          <el-input v-model="dutyForm.description" type="textarea" placeholder="输入职责描述" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="dutyForm.status" placeholder="选择状态" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const duties = ref([])
const filterForm = ref({
  name: ''
})
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增职责')
const dutyForm = ref({
  id: null,
  name: '',
  description: '',
  status: 'ENABLED'
})
const dutyFormRef = ref(null)

// 表单验证规则
const dutyRules = {
  name: [{ required: true, message: '请输入职责名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入职责描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }]
}

// 加载职责列表
const loadDuties = async () => {
  loading.value = true
  try {
    // 这里应该调用后端API获取职责列表
    // 暂时使用模拟数据
    duties.value = [
      {
        id: 1,
        name: '项目负责人',
        description: '负责项目的整体规划、协调和管理',
        status: 'ENABLED',
        createdAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        name: '技术负责人',
        description: '负责项目的技术架构和技术实现',
        status: 'ENABLED',
        createdAt: '2024-01-02 10:00:00'
      },
      {
        id: 3,
        name: '数据分析师',
        description: '负责项目的数据收集、分析和处理',
        status: 'DISABLED',
        createdAt: '2024-01-03 10:00:00'
      }
    ]
    pagination.value.total = duties.value.length
  } catch (error) {
    ElMessage.error('加载职责列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadDuties()
}

// 重置搜索
const resetFilter = () => {
  filterForm.value = {
    name: ''
  }
  pagination.value.current = 1
  loadDuties()
}

// 分页
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  loadDuties()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  loadDuties()
}

// 新增职责
const handleCreate = () => {
  dutyForm.value = {
    id: null,
    name: '',
    description: '',
    status: 'ENABLED'
  }
  dialogTitle.value = '新增职责'
  dialogVisible.value = true
}

// 编辑职责
const handleEdit = (row) => {
  dutyForm.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    status: row.status
  }
  dialogTitle.value = '编辑职责'
  dialogVisible.value = true
}

// 保存职责
const handleSave = async () => {
  if (!dutyFormRef.value) return
  await dutyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dutyForm.value.id) {
          // 编辑职责
          const index = duties.value.findIndex(d => d.id === dutyForm.value.id)
          if (index !== -1) {
            duties.value[index] = { ...dutyForm.value }
          }
          ElMessage.success('编辑成功')
        } else {
          // 新增职责
          const newDuty = {
            id: Date.now(),
            ...dutyForm.value,
            createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
          }
          duties.value.unshift(newDuty)
          pagination.value.total = duties.value.length
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 删除职责
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除职责 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const index = duties.value.findIndex(d => d.id === row.id)
      if (index !== -1) {
        duties.value.splice(index, 1)
        pagination.value.total = duties.value.length
      }
      ElMessage.success('删除成功')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 切换职责状态
const handleToggleStatus = (row) => {
  const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  ElMessageBox.confirm(`确定要${newStatus === 'ENABLED' ? '启用' : '禁用'}职责 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const index = duties.value.findIndex(d => d.id === row.id)
      if (index !== -1) {
        duties.value[index].status = newStatus
      }
      ElMessage.success(`${newStatus === 'ENABLED' ? '启用' : '禁用'}成功`)
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 初始化
onMounted(() => {
  loadDuties()
})
</script>

<style scoped>
.duty-list-container {
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

.action-section {
  display: flex;
  gap: 10px;
}

.list-section {
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.filter-section {
  display: flex;
  gap: 10px;
  align-items: center;
}

.duty-table {
  border-radius: 4px;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .duty-list-container {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-section .el-input {
    width: 100% !important;
  }
  
  .filter-section .el-button {
    margin-left: 0 !important;
  }
  
  .pagination-container {
    justify-content: center;
  }
}
</style>