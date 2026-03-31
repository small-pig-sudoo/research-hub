import { createRouter, createWebHashHistory } from 'vue-router'

import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ProjectList from '../views/ProjectList.vue'
import ProjectForm from '../views/ProjectForm.vue' // ? 新增：创建项目页面
import UserList from '../views/UserList.vue'
import RolePermission from '../views/RolePermission.vue'
import MemberList from '../views/MemberList.vue'
import MemberAssign from '../views/MemberAssign.vue' // ? 新增：成员分配页面
import MemberDuty from '../views/MemberDuty.vue' // ? 新增：职责分工页面
import ProjectStatistics from '../views/ProjectStatistics.vue' // ? 新增：项目统计页面
import FundingStatistics from '../views/FundingStatistics.vue' // ? 新增：经费统计页面
import PerformanceStatistics from '../views/PerformanceStatistics.vue' // ? 新增：绩效统计页面

/**
 * 占位页面组件：先让所有菜单可以打开页面，后面你需要哪个模块，
 * 我们再把对应 path 的 component 换成真正的页面组件即可。
 */
const Placeholder = {
  template: '<div style="padding:20px;">功能开发中...</div>'
}

const routes = [
  // 登录
  { path: '/login', component: Login },

  // 默认跳转到仪表盘
  { path: '/', redirect: '/dashboard' },

  // 仪表盘
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },

  // ===== 项目管理 =====
  { path: '/projects', component: ProjectList, meta: { requiresAuth: true } },
  // ? 这里改成真正的 ProjectForm 组件
  { path: '/projects/create', component: ProjectForm, meta: { requiresAuth: true } },
  { path: '/projects/categories', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 用户管理 =====
  { path: '/users', component: UserList, meta: { requiresAuth: true } },
  { path: '/users/roles', component: RolePermission, meta: { requiresAuth: true } },
  { path: '/users/departments', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 成员管理 =====
  { path: '/members', component: MemberList, meta: { requiresAuth: true } },
  { path: '/members/assign', component: MemberAssign, meta: { requiresAuth: true } },
  { path: '/members/roles', component: MemberDuty, meta: { requiresAuth: true } },

  // ===== 数据统计 =====
  { path: '/statistics/projects', component: ProjectStatistics, meta: { requiresAuth: true } },
  { path: '/statistics/funding', component: FundingStatistics, meta: { requiresAuth: true } },
  { path: '/statistics/performance', component: PerformanceStatistics, meta: { requiresAuth: true } },

  // ===== 系统设置 =====
  { path: '/system/basic', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/system/backup', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/system/logs', component: Placeholder, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 登录校验 + 菜单权限拦截
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  // 有token时，如果已经有 menus，则做权限拦截
  const menus = JSON.parse(localStorage.getItem('menus') || '[]')
  if (token && menus.length > 0) {
    // dashboard 永远允许
    if (to.path !== '/dashboard' && !menus.includes(to.path)) {
      return next('/dashboard')
    }
  }
  next()
})

export default router
