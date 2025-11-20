<!-- src/components/Layout.vue -->
<template>
  <div class="app-layout">
    <!-- 侧边栏（登录页隐藏） -->
    <el-aside
      v-if="!hideLayout"
      :width="isCollapsed ? '64px' : '240px'"
      class="sidebar"
    >
      <!-- 左上角 Logo + 系统名称 -->
      <div class="sidebar-header">
        <div class="logo" v-if="!isCollapsed">
          <img src="/logo.png.jpg" alt="Logo" class="logo-img" />
          <span class="logo-text">高校科研管理项目系统</span>
        </div>
        <div class="logo-collapsed" v-else>
          <img src="/logo.png.jpg" alt="Logo" class="logo-img" />
        </div>
      </div>

      <!-- 菜单区域可滚动 -->
      <el-scrollbar class="sidebar-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          router
          class="sidebar-menu"
          background-color="transparent"
          text-color="#303133"
          active-text-color="#409EFF"
        >
          <!-- 仪表盘 -->
          <el-menu-item index="/dashboard">
            <el-icon><DataBoard /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <!-- 项目管理 -->
          <el-sub-menu index="projects">
            <template #title>
              <el-icon><Collection /></el-icon>
              <span>项目管理</span>
            </template>
            <el-menu-item index="/projects">
              <template #title>项目列表</template>
            </el-menu-item>
            <el-menu-item index="/projects/create">
              <template #title>创建项目</template>
            </el-menu-item>
            <el-menu-item index="/projects/categories">
              <template #title>项目分类</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 用户管理 -->
          <el-sub-menu index="users">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/users">
              <template #title>用户列表</template>
            </el-menu-item>
            <el-menu-item index="/users/roles">
              <template #title>角色权限</template>
            </el-menu-item>
            <el-menu-item index="/users/departments">
              <template #title>部门管理</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 成员管理 -->
          <el-sub-menu index="members">
            <template #title>
              <el-icon><User /></el-icon>
              <span>成员管理</span>
            </template>
            <el-menu-item index="/members">
              <template #title>成员列表</template>
            </el-menu-item>
            <el-menu-item index="/members/assign">
              <template #title>分配成员</template>
            </el-menu-item>
            <el-menu-item index="/members/roles">
              <template #title>成员角色</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 数据统计 -->
          <el-sub-menu index="statistics">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item index="/statistics/projects">
              <template #title>项目统计</template>
            </el-menu-item>
            <el-menu-item index="/statistics/funding">
              <template #title>经费统计</template>
            </el-menu-item>
            <el-menu-item index="/statistics/performance">
              <template #title>绩效统计</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 系统设置 -->
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/system/basic">
              <template #title>基础设置</template>
            </el-menu-item>
            <el-menu-item index="/system/backup">
              <template #title>数据备份</template>
            </el-menu-item>
            <el-menu-item index="/system/logs">
              <template #title>系统日志</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>

      <!-- 折叠按钮 -->
      <div class="sidebar-footer">
        <el-button
          link
          @click="toggleCollapse"
          class="collapse-btn"
          :title="isCollapsed ? '展开菜单' : '折叠菜单'"
        >
          <el-icon>
            <Expand v-if="isCollapsed" />
            <Fold v-else />
          </el-icon>
        </el-button>
      </div>
    </el-aside>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 顶部导航（登录页隐藏） -->
      <el-header v-if="!hideLayout" class="header">
        <div class="header-left">
          <span class="header-title">{{ currentRouteTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar" />
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  DataBoard,
  Collection,
  User,
  TrendCharts,
  Setting,
  Expand,
  Fold,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)

/** 登录页隐藏侧栏与头部 */
const hideLayout = computed(() => route.path === '/login')

const activeMenu = computed(() => route.path)
const userName = computed(
  () => authStore.user?.realName || authStore.user?.username || '用户'
)

const currentRouteTitle = computed(() => {
  const titles = {
    '/dashboard': '仪表盘',
    '/projects': '项目管理',
    '/projects/create': '创建项目',
    '/users': '用户管理',
    '/members': '成员管理',
    '/statistics': '数据统计',
    '/system/basic': '系统设置'
  }
  return titles[route.path] || '科研管理系统'
})

function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
}

function handleCommand(cmd) {
  if (cmd === 'profile') router.push('/profile')
  if (cmd === 'settings') router.push('/settings')
  if (cmd === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}

onMounted(() => {
  if (window.innerWidth < 768) isCollapsed.value = true
})
</script>

<style scoped>
/* ===================== 全局虚化背景 ===================== */

/* 整个后台外层容器 */
.app-layout {
  display: flex;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 使用登录页背景图做全屏虚化背景（侧边栏、顶部、内容区共享） */
.app-layout::before {
  content: "";
  position: fixed; /* 覆盖整个窗口 */
  inset: 0;
  background-image: url("/login-bg.jpg.png"); /* public 目录下的登录背景 */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  /* 虚化 + 提亮 + 降饱和，让颜色柔和不刺眼 */
  filter: blur(18px) brightness(1.08) saturate(0.7);
  opacity: 0.55; /* 整体透明度，越小越淡 */

  z-index: -1; /* 放在所有内容后面 */
}

/* ===================== 侧边栏 ===================== */

.sidebar {
  background: rgba(255, 255, 255, 0.82); /* 半透明白 */
  backdrop-filter: blur(14px);            /* 毛玻璃，和背景融合 */
  border-right: 1px solid rgba(255, 255, 255, 0.5);
  display: flex;
  flex-direction: column;
  height: 100vh;
  transition: width 0.3s;
}

/* 左上角 Logo 区域 */
.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.6);
}

.logo {
  display: flex;
  align-items: center;
  padding: 0 16px;
}

.logo-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
  border-radius: 4px;
}

.logo-text {
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

/* 菜单可滚动 */
.sidebar-scroll {
  flex: 1 1 auto;
}

/* 菜单整体浅色 */
.sidebar :deep(.el-menu) {
  background-color: transparent;
  border-right: none;
}

.sidebar :deep(.el-menu-item),
.sidebar :deep(.el-sub-menu__title) {
  color: #303133;
  font-weight: 500;
}

/* 激活项 */
.sidebar :deep(.el-menu-item.is-active),
.sidebar :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.12);
}

/* hover 效果 */
.sidebar :deep(.el-menu-item:hover),
.sidebar :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.7);
}

/* 折叠按钮 */
.sidebar-footer {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.6);
}

.collapse-btn {
  color: #606266;
  font-size: 16px;
}

.collapse-btn:hover {
  color: #409eff;
}

/* ===================== 顶部导航 ===================== */

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.header {
  height: 64px;
  background: rgba(255, 255, 255, 0.86); /* 半透明白 + 毛玻璃 */
  backdrop-filter: blur(14px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 1;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.user-avatar {
  margin-right: 8px;
}

.user-name {
  margin-right: 8px;
  color: #303133;
  font-weight: 500;
}

/* ===================== 内容区域 ===================== */

.content {
  flex: 1;
  padding: 24px;
  overflow: auto;
  background: transparent; /* 让下面的虚化背景透出来 */
}

/* 小屏适配 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
  }

  .main-container {
    margin-left: 64px;
  }

  .header {
    padding: 0 16px;
  }

  .content {
    padding: 16px;
  }

  .user-name {
    display: none;
  }
}
</style>
