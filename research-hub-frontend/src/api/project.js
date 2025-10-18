import request from '../utils/request'

export function fetchProjects() {
    return request.get('/projects')
}
export function createProject(payload) {
    return request.post('/projects', payload)
}
export function deleteProject(id) {
    return request.delete(`/projects/${id}`)
}
