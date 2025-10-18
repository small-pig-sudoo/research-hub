<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
      <h3>科研项目</h3>
      <el-button type="primary" @click="openAdd = true">新增项目</el-button>
    </div>

    <el-table :data="projects" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="startDate" label="开始" width="120" />
      <el-table-column prop="endDate" label="结束" width="120" />
      <el-table-column prop="funding" label="经费" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button size="mini" @click="edit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog title="新增项目" v-model:visible="openAdd">
      <el-form :model="form">
        <el-form-item label="项目名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="form.type"></el-input>
        </el-form-item>
        <el-form-item label="开始">
          <el-date-picker v-model="form.startDate" type="date" />
        </el-form-item>
        <el-form-item label="结束">
          <el-date-picker v-model="form.endDate" type="date" />
        </el-form-item>
        <el-form-item label="经费">
          <el-input v-model="form.funding" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="openAdd = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchProjects, createProject, deleteProject } from '../api/project'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const projects = ref([])
const loading = ref(false)
const openAdd = ref(false)
const form = ref({
  name: '',
  type: '',
  startDate: null,
  endDate: null,
  funding: 0
})

async function load() {
  loading.value = true
  try {
    const res = await fetchProjects()
    projects.value = (res.data || []).map(item => ({
      ...item,
      startDate: item.startDate ? dayjs(item.startDate).format('YYYY-MM-DD') : '',
      endDate: item.endDate ? dayjs(item.endDate).format('YYYY-MM-DD') : ''
    }))
  } catch (e) {
    projects.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function submitAdd() {
  try {
    const payload = {
      ...form.value,
      startDate: form.value.startDate ? dayjs(form.value.startDate).format('YYYY-MM-DD') : null,
      endDate: form.value.endDate ? dayjs(form.value.endDate).format('YYYY-MM-DD') : null
    }
    await createProject(payload)
    ElMessage.success('新增成功')
    openAdd.value = false
    load()
  } catch (e) {
    ElMessage.error('新增失败')
  }
}

async function del(row) {
  try {
    await ElMessageBox.confirm('确认删除？', '提示')
    await deleteProject(row.id)
    ElMessage.success('删除成功')
    load()
  } catch (e) {}
}

function edit(row) {
  ElMessage.info('编辑功能可按需求完善')
}
</script>
