import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

// 创建axios实例
const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const authStore = useAuthStore()

        // 添加token到请求头
        if (authStore.token) {
            config.headers.Authorization = `Bearer ${authStore.token}`
        }

        console.log('🚀 发送请求:', {
            method: config.method?.toUpperCase(),
            url: config.url,
            data: config.data,
            headers: config.headers
        })
        return config
    },
    (error) => {
        console.error('❌ 请求配置错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        console.log('✅ 收到响应:', {
            status: response.status,
            url: response.config.url,
            data: response.data
        })
        return response.data
    },
    (error) => {
        const { response, code, message } = error

        console.error('❌ 请求错误详情:', {
            message: error.message,
            code: error.code,
            response: error.response,
            config: error.config
        })

        if (!response) {
            // 网络错误或服务器无响应
            if (code === 'ECONNABORTED') {
                ElMessage.error('请求超时，请检查网络连接')
            } else if (message === 'Network Error') {
                ElMessage.error(`无法连接到后端服务，请检查：
        1. Spring Boot应用是否在8080端口运行
        2. 后端是否配置了CORS跨域
        3. 网络连接是否正常`)
            } else {
                ElMessage.error(`网络错误: ${message}`)
            }
        } else {
            // 服务器有响应但状态码不是2xx
            const { status, data } = response

            switch (status) {
                case 401:
                    if (data?.message) {
                        ElMessage.error(data.message)
                    } else {
                        ElMessage.error('认证失败，请重新登录')
                    }
                    // 清除本地认证信息
                    const authStore = useAuthStore()
                    authStore.logout()
                    break
                case 403:
                    ElMessage.error('没有权限访问该资源')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器内部错误: ' + (data?.message || '请联系管理员'))
                    break
                default:
                    ElMessage.error(data?.message || `请求失败 (${status})`)
            }
        }

        return Promise.reject(error)
    }
)

export default request