import request from '../utils/request'

// roles
export const getRoleList = (params) => request.get('/rbac/roles', { params })
export const createRole = (data) => request.post('/rbac/roles', data)
export const updateRole = (id, data) => request.put(`/rbac/roles/${id}`, data)
export const deleteRole = (id) => request.delete(`/rbac/roles/${id}`)
export const setRoleStatus = (id, status) => request.post(`/rbac/roles/${id}/status`, null, { params: { status } })

// menus
export const getMenuTree = () => request.get('/rbac/menus/tree')
export const getRoleMenuIds = (roleId) => request.get(`/rbac/roles/${roleId}/menus`)
export const saveRoleMenus = (roleId, menuIds) => request.post(`/rbac/roles/${roleId}/menus`, menuIds)

// user roles
export const getUserRoleIds = (userId) => request.get(`/rbac/users/${userId}/roles`)
export const saveUserRoles = (userId, roleIds) => request.post(`/rbac/users/${userId}/roles`, roleIds)

// role users - 获取拥有该角色的用户列表
export const getRoleUsers = (roleId) => request.get(`/rbac/roles/${roleId}/users`)

// my menus
export const getMyMenus = () => request.get('/rbac/menus/my')