import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
    baseURL: '/api',
    timeout: 10000
})

service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) config.headers['Authorization'] = `Bearer ${token}`
        return config
    },
    error => Promise.reject(error)
)

service.interceptors.response.use(
    response => {
        const res = response.data
        // 如果后端统一返回 code 字段
        if (res && typeof res.code !== 'undefined') {
            if (res.code !== 200) {
                ElMessage.error(res.message || 'Error')
                return Promise.reject(res)
            }
            return res
        }
        return res
    },
    error => {
        if (error.response && error.response.status === 401) {
            // token 过期或未授权
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.hash = '#/login'
            ElMessage.error('请先登录')
        } else {
            ElMessage.error(error.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

export default service
