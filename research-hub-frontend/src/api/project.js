import request from '../utils/request'

const prefix = '/project'

// 获取项目列表（分页）
export async function getProjects(params = {}) {
    const { page = 1, pageSize = 10, ...otherParams } = params
    return request({
        url: `${prefix}/list`,
        method: 'get',
        params: {
            pageNum: page,
            pageSize,
            ...otherParams
        }
    })
}

// 查询单个项目详情
export async function getProject(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'get'
    })
}

// 创建项目
export async function createProject(projectData) {
    return request({
        url: `${prefix}`,
        method: 'post',
        data: projectData
    })
}

// 更新项目
export async function updateProject(id, projectData) {
    return request({
        url: `${prefix}/${id}`,
        method: 'put',
        data: projectData
    })
}

// 删除项目
export async function deleteProject(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'delete'
    })
}

// 项目统计（如果有）
export async function getProjectStats() {
    return request({
        url: `${prefix}/stats`,
        method: 'get'
    })
}

// 获取项目统计数据
export async function getProjectStatistics(params = {}) {
    return request({
        url: `${prefix}/statistics`,
        method: 'get',
        params
    })
}