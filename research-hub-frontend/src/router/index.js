import { createRouter, createWebHashHistory } from 'vue-router'

import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ProjectList from '../views/ProjectList.vue'

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
  { path: '/projects/create', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/projects/categories', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 用户管理 =====
  { path: '/users', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/users/roles', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/users/departments', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 成员管理 =====
  { path: '/members', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/members/assign', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/members/roles', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 数据统计 =====
  { path: '/statistics/projects', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/statistics/funding', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/statistics/performance', component: Placeholder, meta: { requiresAuth: true } },

  // ===== 系统设置 =====
  { path: '/system/basic', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/system/backup', component: Placeholder, meta: { requiresAuth: true } },
  { path: '/system/logs', component: Placeholder, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 登录校验
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
