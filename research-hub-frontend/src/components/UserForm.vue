<template>
  <div class="user-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="right"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="form.username"
          placeholder="请输入用户名"
          :disabled="mode === 'edit'"
        />
      </el-form-item>

      <el-form-item label="姓名" prop="realName">
        <el-input
          v-model="form.realName"
          placeholder="请输入真实姓名"
        />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="form.email"
          placeholder="请输入邮箱地址"
          type="email"
        />
      </el-form-item>

      <el-form-item label="角色" prop="role">
        <el-select
          v-model="form.role"
          placeholder="请选择角色"
          style="width: 100%"
        >
          <el-option label="管理员" value="ADMIN" />
          <el-option label="教师" value="TEACHER" />
          <el-option label="学生" value="STUDENT" />
        </el-select>
      </el-form-item>

      <el-form-item label="所属单位" prop="department">
        <el-input
          v-model="form.department"
          placeholder="请输入所属单位"
        />
      </el-form-item>

      <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入联系电话"
        />
      </el-form-item>

      <el-form-item label="研究方向" prop="researchField">
        <el-input
          v-model="form.researchField"
          placeholder="请输入研究方向"
          type="textarea"
          :rows="3"
        />
      </el-form-item>

      <el-form-item v-if="mode === 'create'" label="初始密码" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入初始密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="用户状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="ACTIVE">激活</el-radio>
          <el-radio label="INACTIVE">禁用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ mode === 'create' ? '创建' : '更新' }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, updateUser } from '../api/user'

const props = defineProps({
  user: {
    type: Object,
    default: null
  },
  mode: {
    type: String,
    default: 'create' // 'create' or 'edit'
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formRef = ref()
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  realName: '',
  email: '',
  role: 'STUDENT',
  department: '',
  phone: '',
  researchField: '',
  password: '123456', // 默认密码
  status: 'ACTIVE'
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请输入所属单位', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入初始密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

// 监听props变化，在编辑模式下填充数据
watch(() => props.user, (newUser) => {
  if (newUser && props.mode === 'edit') {
    Object.assign(form, {
      username: newUser.username || '',
      realName: newUser.realName || '',
      email: newUser.email || '',
      role: newUser.role || 'STUDENT',
      department: newUser.department || '',
      phone: newUser.phone || '',
      researchField: newUser.researchField || '',
      status: newUser.status || 'ACTIVE'
    })
  }
}, { immediate: true })

// 重置表单（在创建模式下）
onMounted(() => {
  if (props.mode === 'create' && props.user) {
    Object.assign(form, {
      username: '',
      realName: '',
      email: '',
      role: 'STUDENT',
      department: '',
      phone: '',
      researchField: '',
      password: '123456',
      status: 'ACTIVE'
    })
  }
})

async function handleSubmit() {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true

    if (props.mode === 'create') {
      // 创建用户
      await createUser(form)
      ElMessage.success('用户创建成功')
    } else {
      // 更新用户
      await updateUser(props.user.userId, form)
      ElMessage.success('用户更新成功')
    }

    emit('submit')
  } catch (error) {
    console.error('表单提交错误:', error)
    ElMessage.error(props.mode === 'create' ? '用户创建失败' : '用户更新失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.user-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  border-radius: 6px;
}
</style>