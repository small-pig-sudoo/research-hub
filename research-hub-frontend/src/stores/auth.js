import { defineStore } from 'pinia'
import { loginAPI } from '../api/login'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        user: JSON.parse(localStorage.getItem('user') || 'null')
    }),
    actions: {
        async login(username, password) {
            const res = await loginAPI(username, password)
            // res ø…ƒ‹ « { code, message, data }
            const data = res.data
            if (data && data.token) {
                this.token = data.token
                this.user = data.user || null
                localStorage.setItem('token', this.token)
                localStorage.setItem('user', JSON.stringify(this.user))
                return true
            }
            return false
        },
        logout() {
            this.token = ''
            this.user = null
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.hash = '#/login'
        }
    }
})
