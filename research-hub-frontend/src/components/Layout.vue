<!-- src/components/Layout.vue -->
<template>
  <div class="app-layout">
    <!-- 侧边栏（登录页隐藏） -->
    <el-aside
      v-if="!hideLayout"
      :width="isCollapsed ? '64px' : '240px'"
      class="sidebar"
    >
      <div class="sidebar-header">
        <div class="logo" v-if="!isCollapsed">
          <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
          <span class="logo-text">科研管理系统</span>
        </div>
        <div class="logo-collapsed" v-else>
          <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
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
          background-color="#001529"
          text-color="#bfcbd9"
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
            <el-menu-item index="/projects"><template #title>项目列表</template></el-menu-item>
            <el-menu-item index="/projects/create"><template #title>创建项目</template></el-menu-item>
            <el-menu-item index="/projects/categories"><template #title>项目分类</template></el-menu-item>
          </el-sub-menu>

          <!-- 用户管理 -->
          <el-sub-menu index="users">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/users"><template #title>用户列表</template></el-menu-item>
            <el-menu-item index="/users/roles"><template #title>角色权限</template></el-menu-item>
            <el-menu-item index="/users/departments"><template #title>部门管理</template></el-menu-item>
          </el-sub-menu>

          <!-- 成员管理 -->
          <el-sub-menu index="members">
            <template #title>
              <el-icon><Team /></el-icon>
              <span>成员管理</span>
            </template>
            <el-menu-item index="/members"><template #title>成员列表</template></el-menu-item>
            <el-menu-item index="/members/assign"><template #title>分配成员</template></el-menu-item>
            <el-menu-item index="/members/roles"><template #title>成员角色</template></el-menu-item>
          </el-sub-menu>

          <!-- 数据统计 -->
          <el-sub-menu index="statistics">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item index="/statistics/projects"><template #title>项目统计</template></el-menu-item>
            <el-menu-item index="/statistics/funding"><template #title>经费统计</template></el-menu-item>
            <el-menu-item index="/statistics/performance"><template #title>绩效统计</template></el-menu-item>
          </el-sub-menu>

          <!-- 系统设置 -->
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/system/basic"><template #title>基础设置</template></el-menu-item>
            <el-menu-item index="/system/backup"><template #title>数据备份</template></el-menu-item>
            <el-menu-item index="/system/logs"><template #title>系统日志</template></el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>

      <!-- 侧边栏统计/快捷入口（折叠时隐藏） -->
      <div v-show="!isCollapsed" class="aside-stats-wrap">
        <QuickStats />
      </div>

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
              <el-avatar :size="32" :src="userAvatar" class="user-avatar" />
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人资料</el-dropdown-item>
                <el-dropdown-item command="settings"><el-icon><Setting /></el-icon>账户设置</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useDashboardStore } from '@/stores/dashboard'
import QuickStats from '@/components/QuickStats.vue'
import {
  DataBoard, Collection, User, Team, TrendCharts, Setting,
  Expand, Fold, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const dash = useDashboardStore()

const isCollapsed = ref(false)

/** 登录页隐藏侧栏与头部 */
const hideLayout = computed(() => route.path === '/login')

const activeMenu = computed(() => route.path)
const userName = computed(() => authStore.user?.realName || authStore.user?.username || '用户')
const userAvatar = computed(() => authStore.user?.avatar || '')
const currentRouteTitle = computed(() => {
  const titles = {
    '/dashboard': '仪表盘',
    '/projects': '项目管理',
    '/projects/create': '创建项目',
    '/users': '用户管理',
    '/members': '成员管理',
    '/statistics': '数据统计'
  }
  return titles[route.path] || '科研管理系统'
})

function toggleCollapse() { isCollapsed.value = !isCollapsed.value }
function handleCommand(cmd) {
  if (cmd === 'profile') router.push('/profile')
  if (cmd === 'settings') router.push('/settings')
  if (cmd === 'logout') { authStore.logout(); router.push('/login') }
}

onMounted(() => {
  if (window.innerWidth < 768) isCollapsed.value = true
  // 侧栏统计数据
  dash.fetchStats()
})
</script>

<style scoped>
.app-layout { display: flex; height: 100vh; background-color: #f0f2f5; }

/* 侧边栏整体 */
.sidebar {
  background-color: #001529;
  display: flex; flex-direction: column;
  transition: width .3s;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  overflow: hidden;
  height: 100vh;
}
.sidebar-header {
  height: 64px; display: flex; align-items: center; justify-content: center;
  border-bottom: 1px solid #002140;
}
.logo { display: flex; align-items: center; padding: 0 16px; }
.logo-collapsed { display: flex; align-items: center; justify-content: center; padding: 0 8px; }
.logo-img { width: 32px; height: 32px; margin-right: 8px; }
.logo-text { color: #fff; font-size: 18px; font-weight: 600; white-space: nowrap; }

/* 菜单区：可滚动、占满中间空间 */
.sidebar-scroll { flex: 1 1 auto; }

/* 深色菜单覆盖 */
.sidebar :deep(.el-menu) { background-color: #001529; border-right: none; }
.sidebar :deep(.el-sub-menu__title),
.sidebar :deep(.el-menu-item) { color: #bfcbd9; }
.sidebar :deep(.el-menu-item.is-active),
.sidebar :deep(.el-sub-menu.is-active > .el-sub-menu__title) { color: #409EFF; }
.sidebar :deep(.el-menu-item:hover),
.sidebar :deep(.el-sub-menu__title:hover) { background-color: #0a2740; }

/* 统计区（不挤占菜单高度） */
.aside-stats-wrap {
  flex: 0 0 auto;
  background: #001a2a;
  border-top: 1px solid #002140;
  padding-bottom: 8px;
}

/* 底部折叠按钮 */
.sidebar-footer {
  height: 48px; display: flex; align-items: center; justify-content: center;
  border-top: 1px solid #002140;
}
.collapse-btn { color: #bfcbd9; font-size: 16px; }
.collapse-btn:hover { color: #409EFF; }

/* 主内容区域 */
.main-container { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.header {
  height: 64px; background: #fff; display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px; box-shadow: 0 1px 4px rgba(0,21,41,.08); z-index: 10;
}
.header-title { font-size: 20px; font-weight: 600; color: #303133; }
.user-info { display: flex; align-items: center; cursor: pointer; padding: 8px 12px; border-radius: 6px; transition: background-color .3s; }
.user-info:hover { background-color: #f5f7fa; }
.user-avatar { margin-right: 8px; }
.user-name { margin-right: 8px; color: #303133; font-weight: 500; }

.content { flex: 1; padding: 24px; overflow: auto; background-color: #f0f2f5; }

/* 小屏默认折叠 */
@media (max-width: 768px) {
  .sidebar { position: fixed; top: 0; left: 0; height: 100vh; z-index: 1000; }
  .main-container { margin-left: 64px; }
  .header { padding: 0 16px; }
  .content { padding: 16px; }
  .user-name { display: none; }
}
</style>
