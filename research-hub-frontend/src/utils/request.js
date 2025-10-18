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
        // ������ͳһ���� code �ֶ�
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
            // token ���ڻ�δ��Ȩ
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.hash = '#/login'
            ElMessage.error('���ȵ�¼')
        } else {
            ElMessage.error(error.message || '�������')
        }
        return Promise.reject(error)
    }
)

export default service
