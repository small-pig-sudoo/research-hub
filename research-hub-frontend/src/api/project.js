import request from '../utils/request'

export async function getProjects(params = {}) {
    return request({
        url: '/api/projects',
        method: 'get',
        params
    })
}

export async function getProject(id) {
    return request({
        url: `/api/projects/${id}`,
        method: 'get'
    })
}

export async function createProject(projectData) {
    return request({
        url: '/api/projects',
        method: 'post',
        data: projectData
    })
}

export async function updateProject(id, projectData) {
    return request({
        url: `/api/projects/${id}`,
        method: 'put',
        data: projectData
    })
}

export async function deleteProject(id) {
    return request({
        url: `/api/projects/${id}`,
        method: 'delete'
    })
}

export async function getProjectStats() {
    return request({
        url: '/api/projects/stats',
        method: 'get'
    })
}