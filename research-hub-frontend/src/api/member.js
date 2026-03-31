// src/api/member.js
import request from '../utils/request'

// 成员管理API
export const memberApi = {
  // 获取项目成员列表
  getProjectMembers: (params) => {
    const { projectId, ...otherParams } = params
    if (projectId) {
      return request({
        url: `/api/project-member/project/${projectId}`,
        method: 'get'
      })
    } else {
      // 如果没有projectId，返回空列表
      return Promise.resolve({ success: true, data: [] })
    }
  },
  
  // 新增项目成员
  addMember: (data) => {
    return request({
      url: '/api/project-member',
      method: 'post',
      data: {
        projectId: data.projectId,
        userId: data.userId,
        roleInProject: data.role
      }
    })
  },
  
  // 修改成员角色
  updateMember: (projectId, userId, role) => {
    return request({
      url: '/api/project-member/role',
      method: 'put',
      params: {
        projectId,
        userId,
        newRole: role
      }
    })
  },
  
  // 移除项目成员
  removeMember: (projectId, userId) => {
    return request({
      url: '/api/project-member',
      method: 'delete',
      params: {
        projectId,
        userId
      }
    })
  },
  
  // 校验用户是否为项目成员
  checkMember: (projectId, userId) => {
    return request({
      url: '/api/project-member/check',
      method: 'get',
      params: {
        projectId,
        userId
      }
    })
  }
}