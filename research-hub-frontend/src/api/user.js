import request from '../utils/request'

const prefix = '/user'

// 分页查询用户（用户列表用这个）
export function getUsers(params) {
    const { page = 1, pageSize = 10, ...otherParams } = params
    return request({
        url: `${prefix}/page`,
        method: 'get',
        params: {
            page,
            size: pageSize,
            ...otherParams
        }
    })
}

// 兼容你旧命名（如果别处还在用 getUserPage）
export function getUserPage(params) {
    return getUsers(params)
}

export function getUser(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'get'
    })
}

export function createUser(userData) {
    return request({
        url: `${prefix}`,
        method: 'post',
        data: userData
    })
}

export function updateUser(id, userData) {
    return request({
        url: `${prefix}/${id}`,
        method: 'put',
        data: userData
    })
}

export function deleteUser(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'delete'
    })
}

export function resetPassword(id, newPassword) {
    return request({
        url: `${prefix}/${id}/reset-password`,
        method: 'post',
        data: { newPassword }
    })
}

export function getUserStats() {
    return request({
        url: `${prefix}/stats`,
        method: 'get'
    })
}
