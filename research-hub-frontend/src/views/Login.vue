<template>
  <div class="login-container">
    <el-card class="login-card">
      <!-- 标签页切换 -->
      <div class="tabs-container">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >
          登录
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >
          注册
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'forgot' }"
          @click="activeTab = 'forgot'"
        >
          找回密码
        </div>
      </div>

      <!-- 登录表单 -->
      <div v-if="activeTab === 'login'" class="form-container">
        <div class="login-header">
          <h2>ResearchHub 登录</h2>
          <div class="login-subtitle">高级科研管理系统</div>
        </div>

        <el-form :model="loginForm" ref="loginFormRef" label-position="top" class="login-form">
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              autocomplete="off"
              size="large"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input 
              type="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码"
              show-password
              size="large"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              class="login-btn" 
              :loading="loading"
              @click="handleLogin"
              size="large"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 注册表单 -->
      <div v-if="activeTab === 'register'" class="form-container">
        <div class="login-header">
          <h2>创建新账户</h2>
          <div class="login-subtitle">加入 ResearchHub 科研管理系统</div>
        </div>

        <el-form :model="registerForm" ref="registerFormRef" label-position="top" class="login-form">
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              autocomplete="off"
              size="large"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱地址"
              autocomplete="off"
              size="large"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input 
              type="password" 
              v-model="registerForm.password" 
              placeholder="请输入密码"
              show-password
              size="large"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              type="password" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码"
              show-password
              size="large"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              class="login-btn" 
              :loading="loading"
              @click="handleRegister"
              size="large"
            >
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 找回密码表单 -->
      <div v-if="activeTab === 'forgot'" class="form-container">
        <div class="login-header">
          <h2>找回密码</h2>
          <div class="login-subtitle">我们将向您的邮箱发送重置链接</div>
        </div>

        <el-form :model="forgotForm" ref="forgotFormRef" label-position="top" class="login-form">
          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="forgotForm.email" 
              placeholder="请输入注册时使用的邮箱"
              autocomplete="off"
              size="large"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              class="login-btn" 
              :loading="loading"
              @click="handleForgotPassword"
              size="large"
            >
              {{ loading ? '发送中...' : '发送重置链接' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

// 标签页状态
const activeTab = ref('login')

// 表单数据
const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ 
  username: '', 
  email: '', 
  password: '', 
  confirmPassword: '' 
})
const forgotForm = reactive({ email: '' })

// 表单引用
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const forgotFormRef = ref(null)

const loading = ref(false)

// 登录处理
async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.error('用户名和密码不能为空')
    return
  }
  
  loading.value = true
  try {
    const ok = await auth.login(loginForm.username, loginForm.password)
    if (ok) {
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error('登录失败：请检查用户名或密码')
    }
  } catch (e) {
    console.error('登录错误:', e)
    ElMessage.error('登录请求失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 注册处理
async function handleRegister() {
  if (!registerForm.username || !registerForm.email || !registerForm.password) {
    ElMessage.error('请填写所有必填字段')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    // 这里调用注册API
    ElMessage.success('注册成功，请查收验证邮件')
    activeTab.value = 'login'
  } catch (e) {
    console.error('注册错误:', e)
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 找回密码处理
async function handleForgotPassword() {
  if (!forgotForm.email) {
    ElMessage.error('请输入邮箱地址')
    return
  }
  
  loading.value = true
  try {
    // 这里调用找回密码API
    ElMessage.success('重置链接已发送到您的邮箱，请查收')
    activeTab.value = 'login'
  } catch (e) {
    console.error('发送重置链接错误:', e)
    ElMessage.error('发送失败，请检查邮箱地址是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>

/* 强制重置所有可能的滚动和边距 */
:global(html),
:global(body),
:global(#app) {
  margin: 0 !important;
  padding: 0 !important;
  height: 100% !important;
  width: 100% !important;
  overflow: hidden !important;
  position: fixed !important;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 登录容器：绝对全屏，无滚动 */
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
  
  /* 背景图片配置 - 只使用纯图片，无任何颜色叠加 */
  background-image: url('/login-bg.jpg.png');
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  background-attachment: fixed;
  
  /* 居中布局 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 登录卡片 */
.login-card {
  width: 460px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  padding: 0;
  box-sizing: border-box;
  border: none;
  overflow: hidden;
  
  max-width: 90vw;
  max-height: 90vh;
}

/* 标签页容器 */
.tabs-container {
  display: flex;
  background-color: rgba(255, 255, 255, 0.5);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16px 0;
  cursor: pointer;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
  position: relative;
}

.tab-item:hover {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.tab-item.active {
  color: #409eff;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  width: 60%;
  height: 3px;
  background: #409eff;
  border-radius: 3px;
}

/* 表单容器 */
.form-container {
  padding: 32px 40px 40px;
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 32px;
  width: 100%;
  box-sizing: border-box;
}

.login-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  width: 100%;
}

.login-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  letter-spacing: 1px;
  width: 100%;
}

/* 登录表单 */
.login-form {
  margin-top: 8px;
  width: 100%;
}

.login-form :deep(.el-form-item) {
  width: 100%;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #5a6c7d;
  margin-bottom: 6px;
  width: 100%;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  width: 100%;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  border-radius: 10px;
  font-weight: 500;
  letter-spacing: 1px;
  margin-top: 8px;
  background: #409eff;
  border: none;
  height: 44px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3);
}

/* 移动端适配 */
@media (max-width: 480px) {
  .login-card {
    width: calc(100% - 40px);
    max-width: calc(100% - 40px);
  }
  
  .form-container {
    padding: 24px 24px 32px;
  }
  
  .login-header h2 {
    font-size: 20px;
  }
  
  .tabs-container {
    flex-direction: column;
  }
  
  .tab-item {
    padding: 12px 0;
  }
}

/* 超小屏幕适配 */
@media (max-width: 320px) {
  .login-card {
    width: calc(100% - 32px);
    max-width: calc(100% - 32px);
  }
  
  .form-container {
    padding: 20px 20px 28px;
  }
}
</style>