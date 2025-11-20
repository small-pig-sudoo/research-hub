<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { ElBadge } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const dash = useDashboardStore()

const items = computed(() => [
  { key: 'total',    label: '项目总数',   value: dash.stats.total,    to: '/projects' },
  { key: 'ongoing',  label: '进行中',     value: dash.stats.ongoing,  to: '/projects?status=ongoing' },
  { key: 'finished', label: '已结题',     value: dash.stats.finished, to: '/projects?status=finished' },
  { key: 'funding',  label: '总经费(元)', value: dash.stats.funding,  to: '/statistics' },
])

const shortcuts = [
  { icon: '?', label: '项目管理', to: '/projects' },
  { icon: '?', label: '成员管理', to: '/members' },
  { icon: '?', label: '数据统计', to: '/statistics' },
  { icon: '??', label: '系统设置', to: '/settings' }
]

const goto = (to) => router.push(to)
</script>

<template>
  <div class="quick-stats">
    <div class="qs-section">
      <div class="qs-title">概览</div>
      <ul class="qs-list">
        <li v-for="it in items" :key="it.key" @click="goto(it.to)" class="qs-item">
          <span class="qs-label">{{ it.label }}</span>
          <ElBadge :value="it.value" class="qs-badge" />
        </li>
      </ul>
    </div>

    <div class="qs-section">
      <div class="qs-title">快捷入口</div>
      <ul class="qs-shortcuts">
        <li v-for="s in shortcuts" :key="s.label" @click="goto(s.to)" class="qs-short">
          <span class="qs-ico">{{ s.icon }}</span>
          <span class="qs-text">{{ s.label }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.quick-stats { 
  padding: 8px 12px; 
  border-top: 1px solid var(--el-border-color-lighter);
}
.qs-section + .qs-section { margin-top: 12px; }
.qs-title { 
  font-size: 12px; color: var(--el-text-color-secondary); 
  letter-spacing: .5px; margin-bottom: 6px;
}
.qs-list, .qs-shortcuts { list-style: none; margin: 0; padding: 0; }
.qs-item, .qs-short {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 10px; border-radius: 10px; cursor: pointer;
}
.qs-item:hover, .qs-short:hover { background: var(--el-fill-color-light); }
.qs-label { font-size: 13px; }
.qs-badge :deep(.el-badge__content){ font-weight: 600; }
.qs-short { gap: 8px; justify-content: flex-start; }
.qs-ico { width: 22px; text-align: center; }
.qs-text { font-size: 13px; }
</style>
