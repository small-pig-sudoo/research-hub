<template>
  <!-- 主容器添加背景图样式 -->
  <div class="app-wrapper">
    <!-- 半透明遮罩层 -->
    <div class="bg-mask"></div>

    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <img src="/logo.png.jpg" alt="logo" class="logo" />
          <div class="header-title">
            <div class="title-main">ResearchHub</div>
            <div class="title-sub">高校科研管理系统</div>
          </div>
        </div>

        <div class="header-right">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-icon><UserFilled /></el-icon>
              {{ userDisplay }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click.native="goProfile">个人信息</el-dropdown-item>
                <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体布局 -->
    <div class="main-layout">
      <!-- 侧边栏 -->
      <aside class="app-sidebar" v-if="isAuthenticated">
        <el-menu 
          :default-active="$route.path" 
          router
          class="side-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/projects">
            <el-icon><Document /></el-icon>
            <span>科研项目</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区域 -->
      <main class="app-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'
import { UserFilled, ArrowDown, House, Document } from '@element-plus/icons-vue'

const auth = useAuthStore()
const router = useRouter()

const isAuthenticated = computed(() => !!auth.token)
const userDisplay = computed(() =>
  auth.user ? (auth.user.realName || auth.user.username) : '未登录'
)

function logout() {
  auth.logout()
}
function goProfile() {
  router.push('/dashboard')
}

// 强制设置全宽度的函数
const forceFullWidth = () => {
  const elements = document.querySelectorAll('body, html, #app, .app-wrapper, .main-layout, .app-main');
  elements.forEach(el => {
    if (el) {
      el.style.width = '100%';
      el.style.maxWidth = 'none';
      el.style.margin = '0';
      el.style.padding = '0';
    }
  });
};

onMounted(() => {
  forceFullWidth();
  setTimeout(forceFullWidth, 100);
});
</script>

<style>
/* === 全局强制样式 === */
html, body, #app {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  height: 100% !important;
  overflow-x: hidden !important;
}

/* === 应用包装器 === */
.app-wrapper {
  min-height: 100vh;
  background: url('/login-bg.jpg.png') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  flex-direction: column;
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* === 顶部导航栏 === */
.app-header {
  width: 100% !important;
  height: 60px;
  background: linear-gradient(90deg, #2c3e50, #34495e);
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  flex-shrink: 0;
  margin: 0 !important;
  padding: 0 !important;
}

.header-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  margin: 0 auto;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo {
  height: 36px;
  border-radius: 8px;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title-main {
  font-weight: 700;
  font-size: 18px;
}

.title-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.el-dropdown-link {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* === 主体布局 === */
.main-layout {
  flex: 1;
  display: flex;
  width: 100% !important;
  min-height: calc(100vh - 60px);
  margin: 0 !important;
  padding: 0 !important;
}

/* === 侧边栏 === */
.app-sidebar {
  width: 220px;
  background: rgba(255, 255, 255, 0.9);
  border-right: 1px solid #e0e0e0;
  flex-shrink: 0;
}

.side-menu {
  border: none;
  background: transparent;
  padding-top: 16px;
}

/* === 主内容区域 === */
.app-main {
  flex: 1;
  padding: 24px;
  background: rgba(255, 255, 255, 0.9);
  overflow-y: auto;
  width: 100% !important;
  min-width: 0;
  box-sizing: border-box;
}

/* === 半透明遮罩层 === */
.bg-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: -1;
}

/* === 强制 Element Plus 组件全宽度 === */
:deep(.el-container) {
  width: 100% !important;
}

:deep(.el-main) {
  width: 100% !important;
}

:deep(.el-header) {
  width: 100% !important;
}

/* === 响应式设计 === */
@media (max-width: 768px) {
  .app-sidebar {
    width: 180px;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .app-main {
    padding: 16px;
  }
}
</style>