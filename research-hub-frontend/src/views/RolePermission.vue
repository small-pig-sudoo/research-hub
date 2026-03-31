<template>
  <div style="padding: 16px;">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card v-loading="loading">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>角色列表</span>
              <el-button type="primary" @click="openCreate">新增角色</el-button>
            </div>
          </template>

          <el-input v-model="keyword" placeholder="搜索角色名称/编码" clearable @clear="loadRoles" @keyup.enter="loadRoles" />

          <el-table :data="roles" style="width:100%;margin-top:12px;" height="520"
                    @row-click="selectRole" highlight-current-row>
            <el-table-column prop="roleId" label="ID" width="70" />
            <el-table-column prop="roleName" label="名称" />
            <el-table-column prop="roleCode" label="编码" width="110" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status===1 ? 'success':'info'">{{ row.status===1?'启用':'禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button size="small" @click.stop="openEdit(row)">编辑</el-button>
                <el-button size="small" type="primary" plain @click.stop="viewRoleUsers(row)">查看用户</el-button>
                <el-dropdown @command="(cmd)=>handleRoleCmd(cmd,row)">
                  <el-button size="small">
                    更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="toggle">{{ row.status===1?'禁用':'启用' }}</el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card v-loading="menuLoading || permissionLoading">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>菜单权限：<b>{{ currentRole?.roleName || '未选择角色' }}</b></span>
              <el-button type="primary" :disabled="!currentRole" @click="saveMenus">保存权限</el-button>
            </div>
          </template>

          <el-empty v-if="menuTree.length === 0" description="菜单数据加载失败，后端服务异常" />
          <el-tree
            v-else
            ref="treeRef"
            :data="menuTree"
            node-key="id"
            show-checkbox
            default-expand-all
            :props="{ label: 'label', children: 'children' }"
            style="height:560px;overflow:auto;"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 角色表单对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="dialogMode==='create'?'新增角色':'编辑角色'" width="520px">
      <el-form :model="roleForm" label-width="90px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="roleForm.roleCode" :disabled="dialogMode==='edit'" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="roleForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看角色用户对话框 -->
    <el-dialog v-model="roleUsersDialog.visible" :title="`拥有【${roleUsersDialog.roleName}】角色的用户`" width="600px">
      <el-table :data="roleUsersDialog.users" style="width:100%" max-height="400">
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
      </el-table>
      <template #footer>
        <el-button @click="roleUsersDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import {
  getRoleList, createRole, updateRole, deleteRole, setRoleStatus,
  getMenuTree, getRoleMenuIds, saveRoleMenus, getRoleUsers
} from '../api/rbac'

const keyword = ref('')
const roles = ref([])
const currentRole = ref(null)
const loading = ref(false)
const menuLoading = ref(false)
const permissionLoading = ref(false)

const menuTree = ref([])
const treeRef = ref(null)

const roleDialogVisible = ref(false)
const dialogMode = ref('create')
const roleForm = ref({ roleName:'', roleCode:'', description:'', status:1 })

// 角色用户对话框
const roleUsersDialog = ref({
  visible: false,
  roleName: '',
  users: []
})

// 错误提示锁，防止重复提示
let errorShown = false

function showError(msg) {
  if (!errorShown) {
    errorShown = true
    ElMessage.error(msg)
    setTimeout(() => { errorShown = false }, 3000)
  }
}

async function loadRoles() {
  loading.value = true
  try {
    const res = await getRoleList({ keyword: keyword.value })
    if (res.code === 200) {
      roles.value = res.data || []
    } else {
      showError(res.message || '加载角色列表失败')
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
    showError('加载角色列表失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

async function loadMenuTree() {
  menuLoading.value = true
  try {
    const res = await getMenuTree()
    if (res.code === 200) {
      menuTree.value = res.data || []
    } else {
      showError(res.message || '加载菜单树失败')
    }
  } catch (error) {
    console.error('加载菜单树失败:', error)
    // 不显示错误提示，使用空状态显示
  } finally {
    menuLoading.value = false
  }
}

async function selectRole(row) {
  if (!row || !row.roleId) {
    ElMessage.warning('角色信息无效')
    return
  }
  
  currentRole.value = row
  permissionLoading.value = true
  
  try {
    const res = await getRoleMenuIds(row.roleId)
    if (res.code === 200) {
      const menuIds = Array.isArray(res.data) ? res.data : []
      treeRef.value?.setCheckedKeys(menuIds)
    } else {
      showError(res.message || '获取角色菜单权限失败')
    }
  } catch (error) {
    console.error('获取角色菜单权限失败:', error)
    // 不显示错误提示，静默处理
  } finally {
    permissionLoading.value = false
  }
}

// 查看拥有该角色的用户
async function viewRoleUsers(row) {
  if (!row || !row.roleId) {
    ElMessage.warning('角色信息无效')
    return
  }
  
  roleUsersDialog.value.roleName = row.roleName
  roleUsersDialog.value.visible = true
  roleUsersDialog.value.users = []
  
  try {
    const res = await getRoleUsers(row.roleId)
    if (res.code === 200) {
      roleUsersDialog.value.users = res.data || []
    } else {
      ElMessage.warning(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取角色用户失败:', error)
    ElMessage.error('获取用户列表失败，请检查后端服务')
  }
}

function openCreate() {
  dialogMode.value = 'create'
  roleForm.value = { roleName:'', roleCode:'', description:'', status:1 }
  roleDialogVisible.value = true
}

function openEdit(row) {
  if (!row) {
    ElMessage.warning('角色信息无效')
    return
  }
  
  dialogMode.value = 'edit'
  roleForm.value = { 
    roleId: row.roleId, 
    roleName: row.roleName, 
    roleCode: row.roleCode, 
    description: row.description, 
    status: row.status 
  }
  roleDialogVisible.value = true
}

async function submitRole() {
  if (!roleForm.value.roleName || !roleForm.value.roleCode) {
    ElMessage.warning('请填写角色名称与编码')
    return
  }
  
  loading.value = true
  try {
    let res
    if (dialogMode.value === 'create') {
      res = await createRole(roleForm.value)
    } else {
      res = await updateRole(roleForm.value.roleId, roleForm.value)
    }
    if (res.code === 200) {
      ElMessage.success('保存成功')
      roleDialogVisible.value = false
      await loadRoles()
    } else {
      showError(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存角色失败:', error)
    showError('保存角色失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

async function handleRoleCmd(cmd, row) {
  if (!row || !row.roleId) {
    ElMessage.warning('角色信息无效')
    return
  }
  
  loading.value = true
  try {
    if (cmd === 'toggle') {
      const res = await setRoleStatus(row.roleId, row.status === 1 ? 0 : 1)
      if (res.code === 200) {
        ElMessage.success('已更新')
        await loadRoles()
      } else {
        showError(res.message || '更新状态失败')
      }
    } else if (cmd === 'delete') {
      await ElMessageBox.confirm(`确定删除角色【${row.roleName}】？`, '提示', { type: 'warning' })
      const res = await deleteRole(row.roleId)
      if (res.code === 200) {
        ElMessage.success('已删除')
        if (currentRole.value?.roleId === row.roleId) {
          currentRole.value = null
        }
        await loadRoles()
      } else {
        showError(res.message || '删除失败')
      }
    }
  } catch (error) {
    console.error('处理角色操作失败:', error)
    if (error.message !== 'cancel') {
      showError('处理角色操作失败，请检查后端服务')
    }
  } finally {
    loading.value = false
  }
}

async function saveMenus() {
  if (!currentRole.value || !currentRole.value.roleId) {
    ElMessage.warning('请先选择角色')
    return
  }
  
  permissionLoading.value = true
  try {
    const ids = treeRef.value?.getCheckedKeys(true) || []
    const res = await saveRoleMenus(currentRole.value.roleId, ids)
    if (res.code === 200) {
      ElMessage.success('权限已保存')
    } else {
      showError(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存菜单权限失败:', error)
    showError('保存菜单权限失败，请检查后端服务')
  } finally {
    permissionLoading.value = false
  }
}

onMounted(async () => {
  await loadRoles()
  await loadMenuTree()
})
</script>
