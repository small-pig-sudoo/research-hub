import request from '../utils/request'

export function loginAPI(username, password) {
    // ���ݺ��ʵ�ʽӿڵ���·�� /user/login �� /auth/login
    return request.post('/api/auth/login', { username, password })
}
