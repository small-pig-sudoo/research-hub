import request from '../utils/request'

export async function getProjects(params = {}) {
    return request({
        url: '/project/list',    // => 实际请求 /api/project/list
        method: 'get',
        params
    })
}

// 查询单个项目详情
export async function getProject(id) {
    return request({
        url: `${prefix}/${id}`,   // => /api/project/{id}
        method: 'get'
    })
}

// 新建项目
export async function createProject(projectData) {
    return request({
        url: `${prefix}`,         // => /api/project
        method: 'post',
        data: projectData
    })
}

// 更新项目
export async function updateProject(id, projectData) {
    return request({
        url: `${prefix}/${id}`,   // => /api/project/{id}
        method: 'put',
        data: projectData
    })
}

// 删除项目
export async function deleteProject(id) {
    return request({
        url: `${prefix}/${id}`,   // => /api/project/{id}
        method: 'delete'
    })
}

// 仪表盘/统计用（如果后端有这个接口）
export async function getProjectStats() {
    return request({
        url: `${prefix}/stats`,   // => /api/project/stats
        method: 'get'
    })
}
