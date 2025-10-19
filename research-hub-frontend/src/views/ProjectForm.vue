<template>
  <div class="project-form-container">
    <!-- 固定头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">{{ isEdit ? '编辑项目' : '创建新项目' }}</h1>
          <p class="page-subtitle">{{ isEdit ? '修改项目信息' : '填写项目基本信息' }}</p>
        </div>
        <div class="header-actions">
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEdit ? '更新项目' : '创建项目' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 可滚动内容区域 -->
    <div class="scrollable-content">
      <div class="form-content-wrapper">
        <el-card class="form-section" shadow="never">
          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="rules" 
            label-width="120px"
            label-position="left"
            class="project-form"
          >
            <!-- 基本信息部分 -->
            <div class="form-section-title">
              <el-icon><Document /></el-icon>
              <span>基本信息</span>
            </div>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="项目名称" prop="name">
                  <el-input 
                    v-model="form.name" 
                    placeholder="请输入项目名称"
                    size="large"
                    maxlength="100"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="项目编号" prop="projectId">
                  <el-input 
                    v-model="form.projectId" 
                    placeholder="系统自动生成"
                    size="large"
                    disabled
                  >
                    <template #append>
                      <el-button @click="generateProjectId" :disabled="isEdit">生成</el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="项目类型" prop="type">
                  <el-select 
                    v-model="form.type" 
                    placeholder="请选择项目类型"
                    size="large"
                    style="width: 100%"
                  >
                    <el-option label="国家级" value="NATIONAL" />
                    <el-option label="省部级" value="PROVINCIAL" />
                    <el-option label="校级" value="SCHOOL" />
                    <el-option label="横向项目" value="HORIZONTAL" />
                    <el-option label="其他" value="OTHER" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="项目状态" prop="status">
                  <el-select 
                    v-model="form.status" 
                    placeholder="请选择项目状态"
                    size="large"
                    style="width: 100%"
                  >
                    <el-option label="申报中" value="APPLICATION" />
                    <el-option label="在研" value="ONGOING" />
                    <el-option label="已结题" value="CONCLUSION" />
                    <el-option label="已终止" value="TERMINATED" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="负责人" prop="principal">
                  <el-input 
                    v-model="form.principal" 
                    placeholder="请输入负责人姓名"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="总经费" prop="totalFunding">
                  <el-input 
                    v-model="form.totalFunding" 
                    placeholder="请输入项目经费"
                    size="large"
                    type="number"
                  >
                    <template #append>元</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 时间信息部分 -->
            <div class="form-section-title">
              <el-icon><Calendar /></el-icon>
              <span>时间信息</span>
            </div>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="开始日期" prop="startDate">
                  <el-date-picker
                    v-model="form.startDate"
                    type="date"
                    placeholder="选择开始日期"
                    size="large"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="预期结题" prop="expectedEndDate">
                  <el-date-picker
                    v-model="form.expectedEndDate"
                    type="date"
                    placeholder="选择预期结题日期"
                    size="large"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="实际结题" prop="actualEndDate">
                  <el-date-picker
                    v-model="form.actualEndDate"
                    type="date"
                    placeholder="选择实际结题日期"
                    size="large"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                    :disabled="form.status !== 'CONCLUSION'"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="项目周期" prop="duration">
                  <el-input 
                    :value="calculateDuration" 
                    placeholder="自动计算"
                    size="large"
                    disabled
                  >
                    <template #append>个月</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 详细信息部分 -->
            <div class="form-section-title">
              <el-icon><EditPen /></el-icon>
              <span>详细信息</span>
            </div>

            <el-form-item label="项目描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                placeholder="请输入项目描述、研究内容、技术路线、创新点等详细信息"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>

            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="合作单位" prop="externalCollaborator">
                  <el-input
                    v-model="form.externalCollaborator"
                    placeholder="请输入校外合作单位"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input
                    v-model="form.contactPhone"
                    placeholder="请输入联系电话"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 项目成员部分 -->
            <div class="form-section-title">
              <el-icon><User /></el-icon>
              <span>项目成员</span>
            </div>

            <el-form-item label="项目成员" prop="teamMembers">
              <div class="team-members-section">
                <div class="members-list">
                  <div 
                    v-for="(member, index) in form.teamMembers" 
                    :key="index" 
                    class="member-item"
                  >
                    <el-input
                      v-model="member.name"
                      placeholder="成员姓名"
                      style="width: 150px; margin-right: 10px;"
                    />
                    <el-select
                      v-model="member.role"
                      placeholder="角色"
                      style="width: 120px; margin-right: 10px;"
                    >
                      <el-option label="负责人" value="leader" />
                      <el-option label="主要成员" value="main" />
                      <el-option label="参与人员" value="participant" />
                    </el-select>
                    <el-input
                      v-model="member.department"
                      placeholder="所在单位"
                      style="width: 150px; margin-right: 10px;"
                    />
                    <el-button
                      type="danger"
                      text
                      :icon="Delete"
                      @click="removeMember(index)"
                    />
                  </div>
                </div>
                <el-button 
                  type="primary" 
                  text 
                  :icon="Plus" 
                  @click="addMember"
                  class="add-member-btn"
                >
                  添加成员
                </el-button>
              </div>
            </el-form-item>

            <!-- 附件上传部分 -->
            <div class="form-section-title">
              <el-icon><Upload /></el-icon>
              <span>附件上传</span>
            </div>

            <el-form-item label="项目附件">
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                :file-list="fileList"
                multiple
                :limit="10"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  选择文件
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持上传项目申请书、合同、任务书等文件，单个文件不超过 10MB
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <!-- 表单操作按钮（底部） -->
            <div class="form-actions-bottom">
              <el-button @click="$router.back()" size="large">取消</el-button>
              <el-button 
                type="primary" 
                :loading="loading" 
                @click="handleSubmit"
                size="large"
              >
                {{ isEdit ? '更新项目' : '创建项目' }}
              </el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Document, Calendar, EditPen, Upload, User, Plus, Delete
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref()
const fileList = ref([])

const isEdit = computed(() => route.name === 'ProjectEdit')

// 表单数据
const form = reactive({
  name: '',
  projectId: '',
  type: '',
  status: 'APPLICATION',
  principal: '',
  totalFunding: '',
  startDate: '',
  expectedEndDate: '',
  actualEndDate: '',
  description: '',
  externalCollaborator: '',
  contactPhone: '',
  teamMembers: [
    { name: '', role: 'participant', department: '' }
  ]
})

// 计算项目周期
const calculateDuration = computed(() => {
  if (!form.startDate || !form.expectedEndDate) return '0'
  
  const start = new Date(form.startDate)
  const end = new Date(form.expectedEndDate)
  const months = (end.getFullYear() - start.getFullYear()) * 12 + 
                (end.getMonth() - start.getMonth())
  return Math.max(0, months)
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 100, message: '项目名称长度为 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择项目类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ],
  principal: [
    { required: true, message: '请输入负责人', trigger: 'blur' }
  ],
  totalFunding: [
    { required: true, message: '请输入项目经费', trigger: 'blur' },
    { pattern: /^\d+$/, message: '经费必须为数字', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  expectedEndDate: [
    { required: true, message: '请选择预期结题日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (!form.startDate || !value) {
          callback()
          return
        }
        const start = new Date(form.startDate)
        const end = new Date(value)
        if (end <= start) {
          callback(new Error('预期结题日期必须晚于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 监听状态变化，自动设置实际结题日期
watch(() => form.status, (newStatus) => {
  if (newStatus === 'CONCLUSION' && !form.actualEndDate) {
    form.actualEndDate = new Date().toISOString().split('T')[0]
  }
})

onMounted(() => {
  if (isEdit.value) {
    loadProjectData()
  } else {
    generateProjectId()
  }
})

function loadProjectData() {
  // 模拟加载项目数据
  const projectId = route.params.id
  // 这里应该是从API加载数据，现在使用模拟数据
  Object.assign(form, {
    name: '人工智能在科研管理中的应用研究',
    projectId: 'RH2024001',
    type: 'NATIONAL',
    status: 'ONGOING',
    principal: '张教授',
    totalFunding: '500000',
    startDate: '2024-01-01',
    expectedEndDate: '2026-12-31',
    description: '本研究旨在探索人工智能技术在高校科研管理中的应用，通过自然语言处理、机器学习等技术，提高科研项目管理的效率和质量。研究内容包括：科研项目智能推荐、科研成果自动分析、科研进度智能监控等。',
    externalCollaborator: '某人工智能研究院',
    contactPhone: '13800138000',
    teamMembers: [
      { name: '张教授', role: 'leader', department: '计算机学院' },
      { name: '李研究员', role: 'main', department: '人工智能研究院' },
      { name: '王工程师', role: 'main', department: '软件学院' }
    ]
  })
}

function generateProjectId() {
  const timestamp = new Date().getTime().toString().slice(-6)
  const random = Math.random().toString(36).substr(2, 4).toUpperCase()
  form.projectId = `RH${new Date().getFullYear()}${timestamp}${random}`
}

function addMember() {
  form.teamMembers.push({ name: '', role: 'participant', department: '' })
}

function removeMember(index) {
  if (form.teamMembers.length > 1) {
    form.teamMembers.splice(index, 1)
  } else {
    ElMessage.warning('至少需要保留一个项目成员')
  }
}

function handleFileChange(file, files) {
  // 文件大小验证
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  fileList.value = files
}

function handleFileRemove(file, files) {
  fileList.value = files
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(isEdit.value ? '项目更新成功' : '项目创建成功')
    router.push('/projects')
  } catch (error) {
    console.error('表单提交错误:', error)
    ElMessage.error(isEdit.value ? '项目更新失败' : '项目创建失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.project-form-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", "Helvetica Neue", Helvetica, Arial, sans-serif;
  overflow: hidden;
}

/* 固定头部 */
.page-header {
  background: white;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.title-section .page-title {
  margin: 0 0 8px 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.title-section .page-subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 可滚动内容区域 */
.scrollable-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.form-content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

/* 表单区域 */
.form-section {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  min-height: min-content;
}

.project-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.form-section-title {
  display: flex;
  align-items: center;
  margin: 32px 0 24px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.form-section-title:first-child {
  margin-top: 0;
}

.form-section-title .el-icon {
  margin-right: 8px;
  color: #409eff;
}

/* 团队成员样式 */
.team-members-section {
  width: 100%;
}

.member-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.member-item:hover {
  background: #f0f2f5;
}

.add-member-btn {
  margin-top: 8px;
  width: 100%;
  justify-content: center;
}

/* 底部操作按钮 */
.form-actions-bottom {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .scrollable-content {
    padding: 12px;
  }
  
  .project-form {
    padding: 16px;
  }
  
  .member-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .member-item .el-input,
  .member-item .el-select {
    width: 100% !important;
    margin-right: 0 !important;
  }
  
  .form-actions-bottom {
    flex-direction: column;
  }
  
  .form-actions-bottom .el-button {
    width: 100%;
  }
}

/* 自定义滚动条 */
.scrollable-content::-webkit-scrollbar {
  width: 6px;
}

.scrollable-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.scrollable-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.scrollable-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 全局字体设置 */
:deep(*) {
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", "Helvetica Neue", Helvetica, Arial, sans-serif !important;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner),
:deep(.el-select .el-input__inner) {
  border-radius: 6px;
}

:deep(.el-textarea__inner) {
  resize: vertical;
  min-height: 80px;
}
</style>