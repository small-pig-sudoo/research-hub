import request from '../utils/request'

// 获取用户列表
export async function getUsers(params = {}) {
    return request({
        url: '/api/users',
        method: 'get',
        params
    })
}

// 获取用户详情
export async function getUser(id) {
    return request({
        url: `/api/users/${id}`,
        method: 'get'
    })
}

// 创建用户
export async function createUser(userData) {
    return request({
        url: '/api/users',
        method: 'post',
        data: userData
    })
}

// 更新用户
export async function updateUser(id, userData) {
    return request({
        url: `/api/users/${id}`,
        method: 'put',
        data: userData
    })
}

// 删除用户
export async function deleteUser(id) {
    return request({
        url: `/api/users/${id}`,
        method: 'delete'
    })
}

// 重置用户密码
export async function resetPassword(id, newPassword) {
    return request({
        url: `/api/users/${id}/reset-password`,
        method: 'post',
        data: { newPassword }
    })
}

// 获取用户统计
export async function getUserStats() {
    return request({
        url: '/api/users/stats',
        method: 'get'
    })
}