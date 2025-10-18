import request from '../utils/request'

export function loginAPI(username, password) {
    // 根据后端实际接口调整路径 /user/login 或 /auth/login
    return request.post('/api/auth/login', { username, password })
}
