import request from '../utils/request'

const prefix = '/project'

// 获取项目列表（分页）
export async function getProjects(params = {}) {
    return request({
        url: `${prefix}/list`,
        method: 'get',
        params
    })
}

// 查询单个项目详情
export async function getProject(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'get'
    })
}

// 新建项目
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

// 仪表盘统计（如果有）
export async function getProjectStats() {
    return request({
        url: `${prefix}/stats`,
        method: 'get'
    })
}
