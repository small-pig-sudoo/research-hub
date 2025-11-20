import request from '../utils/request'

// 登录
export async function login(username, password) {
    return request({
        // ⚠ 这里不要再写 /api 了，让 baseURL=/api 去拼接
        url: '/auth/login',
        method: 'post',
        data: {
            username,
            password
        }
    })
}

// 退出登录
export async function logout() {
    return request({
        url: '/auth/logout',  // 对应后端 /api/auth/logout（如果你有实现的话）
        method: 'post'
    })
}

// 获取个人信息
export async function getProfile() {
    return request({
        url: '/user/profile', // 对应后端 /api/user/profile（按你的 UserController 实现来）
        method: 'get'
    })
}

// 注册（如果后端还没实现，这个现在调用会 404，是正常的）
export async function register(userData) {
    return request({
        url: '/auth/register',
        method: 'post',
        data: userData
    })
}

// 找回密码（同上，没有对应接口会 404）
export async function forgotPassword(email) {
    return request({
        url: '/auth/forgot-password',
        method: 'post',
        data: { email }
    })
}

// 测试后端连接（需要你后端写 /api/auth/test 才会通）
export async function testConnection() {
    return request({
        url: '/auth/test',
        method: 'get'
    })
}
