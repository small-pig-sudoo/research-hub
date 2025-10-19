import request from '../utils/request'

export async function login(username, password) {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data: {
            username,
            password
        }
    })
}

export async function logout() {
    return request({
        url: '/api/auth/logout',
        method: 'post'
    })
}

export async function getProfile() {
    return request({
        url: '/api/user/profile', // 根据您的UserController路径调整
        method: 'get'
    })
}

export async function register(userData) {
    return request({
        url: '/api/auth/register',
        method: 'post',
        data: userData
    })
}

export async function forgotPassword(email) {
    return request({
        url: '/api/auth/forgot-password',
        method: 'post',
        data: { email }
    })
}

// 测试后端连接
export async function testConnection() {
    return request({
        url: '/api/auth/test', // 需要在后端添加测试接口
        method: 'get'
    })
}