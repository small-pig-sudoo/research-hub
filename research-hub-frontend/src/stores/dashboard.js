import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useDashboardStore = defineStore('dashboard', {
    state: () => ({
        stats: {
            total: 0,        // 项目总数
            ongoing: 0,      // 进行中
            finished: 0,     // 已结题
            funding: 0       // 总经费（元）
        }
    }),
    actions: {
        async fetchStats() {
            // 这里用你原来获取 8 个卡片数据的接口；下面是示例
            const res = await request.get('/dashboard/overview')
            // 期望后端返回：{ code:200, data:{ total, ongoing, finished, funding } }
            if (res?.code === 200 && res?.data) {
                this.stats = {
                    total: res.data.total ?? 0,
                    ongoing: res.data.ongoing ?? 0,
                    finished: res.data.finished ?? 0,
                    funding: res.data.funding ?? 0
                }
            }
        }
    }
})
