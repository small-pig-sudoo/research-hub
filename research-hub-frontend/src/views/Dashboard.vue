<template>
  <div class="card">
    <div style="display:flex;gap:18px;flex-wrap:wrap">
      <el-card style="flex:1;min-width:220px">
        <div style="font-size:14px;color:var(--muted)">当前用户</div>
        <div style="font-weight:700;font-size:18px">{{ userName }}</div>
        <div style="color:var(--muted);margin-top:8px">{{ roleName }}</div>
      </el-card>

      <el-card style="flex:1;min-width:220px">
        <div style="font-size:14px;color:var(--muted)">项目总数</div>
        <div style="font-weight:700;font-size:28px">{{ projectCount }}</div>
      </el-card>
    </div>

    <div style="margin-top:20px">
      <h3>近期项目</h3>
      <project-list-mini />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { fetchProjects } from '../api/project'
import ProjectListMini from './ProjectList.vue' // 复用

const auth = useAuthStore()
const userName = computed(() => auth.user ? (auth.user.realName || auth.user.username) : '游客')
const roleName = computed(() => auth.user ? auth.user.role : '未登录')

const projectCount = ref(0)

async function load() {
  try {
    const res = await fetchProjects()
    const list = res.data || []
    projectCount.value = list.length
  } catch (e) {
    projectCount.value = 0
  }
}

onMounted(load)
</script>
