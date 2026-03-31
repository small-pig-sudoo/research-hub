import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login as apiLogin, logout as apiLogout, getProfile } from '../api/login'
import { getMyMenus } from '../api/rbac'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token'))
    const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
    const menus = ref(JSON.parse(localStorage.getItem('menus') || '[]'))
    const loading = ref(false)

    const isAuthenticated = computed(() => !!token.value)
    const userInfo = computed(() => user.value)

    async function login(username, password) {
        loading.value = true
        try {
            const response = await apiLogin(username, password)

            console.log('登录响应:', response)

            // 根据您的后端响应格式：{ code: 200, message: "success", data: { token: "...", userId: 1, username: "...", role: "...", name: "..." } }
            if (response.code === 200 && response.data) {
                token.value = response.data.token

                // 构建user对象 - 根据您的后端数据结构
                user.value = {
                    id: response.data.userId,
                    username: response.data.username,
                    name: response.data.name,
                    role: response.data.role
                }

                // 保存到localStorage
                localStorage.setItem('token', response.data.token)
                localStorage.setItem('user', JSON.stringify(user.value))
                // 登录成功后拉取菜单权限
                await fetchMyMenus()


                ElMessage.success(response.message || '登录成功')
                return true
            } else {
                // 处理登录失败
                ElMessage.error(response.message || '登录失败')
                return false
            }
        } catch (error) {
            console.error('Login error:', error)
            // 网络错误已经在request拦截器中处理了
            return false
        } finally {
            loading.value = false
        }
    }

    async function logout() {
        loading.value = true
        try {
            // 调用后端登出接口
            await apiLogout()
            ElMessage.success('退出登录成功')
        } catch (error) {
            console.error('Logout error:', error)
            // 即使后端登出失败，也要清除前端状态
        } finally {
            token.value = null
            user.value = null
            menus.value = []
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            localStorage.removeItem('menus')
            loading.value = false
        }
    }

    async function fetchProfile() {
        if (!token.value) return

        try {
            const response = await getProfile()
            if (response.code === 200 && response.data) {
                // 根据后端profile接口返回结构调整
                user.value = {
                    id: response.data.userId || response.data.id,
                    username: response.data.username,
                    name: response.data.name,
                    role: response.data.role,
                    email: response.data.email,
                    phone: response.data.phone,
                    department: response.data.department,
                    researchField: response.data.researchField
                }
                localStorage.setItem('user', JSON.stringify(user.value))
            }
        } catch (error) {
            console.error('Fetch profile error:', error)
            // 如果获取用户信息失败，可能是token过期
            if (error.response && error.response.status === 401) {
                logout()
            }
        }
    }

    // 初始化认证状态
    function initAuth() {
        if (token.value && user.value) {
            // 可以在这里添加token有效性检查
            console.log('用户已登录:', user.value.username)
        }
    }

    // 获取用户菜单权限
    async function fetchMyMenus() {
        try {
            const res = await getMyMenus()
            if (res.code === 200) {
                menus.value = res.data || []
                localStorage.setItem('menus', JSON.stringify(menus.value))
            }
        } catch (e) {
            console.error('获取菜单权限失败:', e)
            // 忽略错误，菜单加载失败不阻塞登录
        }
    }

    // 权限计算属性
    const userRole = computed(() => user.value?.role)
    const isAdmin = computed(() => user.value?.role === 'admin')
    const isTeacher = computed(() => user.value?.role === 'teacher')
    const isStudent = computed(() => user.value?.role === 'student')

    return {
        // 状态
        token,
        user,
        menus,
        loading,

        // 计算属性
        isAuthenticated,
        userInfo,
        userRole,
        isAdmin,
        isTeacher,
        isStudent,

        // 方法
        login,
        logout,
        fetchProfile,
        fetchMyMenus,
        initAuth
    }
})