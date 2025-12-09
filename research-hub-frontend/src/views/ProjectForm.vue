<template>
  <div class="page-wrapper">
    <!-- 顶部标题卡片 -->
    <el-card class="page-header" shadow="never">
      <div class="header-left">
        <div class="title-row">
          <span class="title">创建项目</span>
          <span class="tag">项目管理模块</span>
        </div>
        <div class="sub-title">填写项目信息并提交，系统会自动同步到项目列表中</div>
      </div>
    </el-card>

    <!-- 表单主体 -->
    <el-card class="form-card" shadow="never">
      <div class="form-layout">
        <!-- 左侧表单 -->
        <div class="form-main">
          <div class="section-header">
            <div class="section-title">项目信息</div>
            <div class="section-sub">带 * 的为必填项</div>
          </div>

          <el-divider />

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            class="project-form"
          >
            <!-- 项目名称 -->
            <el-form-item label="项目名称" prop="name">
              <el-input
                v-model="form.name"
                placeholder="请输入项目名称，例如：基于大数据的科研管理系统"
                clearable
              />
            </el-form-item>

            <!-- 两列：类型 + 状态 -->
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="项目类型" prop="projectType">
                  <el-select
                    v-model="form.projectType"
                    placeholder="请选择项目类型"
                    class="full-width"
                  >
                    <el-option label="国家级" value="国家级" />
                    <el-option label="省部级" value="省部级" />
                    <el-option label="校级" value="校级" />
                    <el-option label="横向项目" value="横向项目" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="项目状态" prop="status">
                  <el-select
                    v-model="form.status"
                    placeholder="请选择状态"
                    class="full-width"
                  >
                    <!-- ⭐ 必须和数据库 enum 完全一致 -->
                    <el-option label="申报中" value="申报中" />
                    <el-option label="在研" value="在研" />
                    <el-option label="已结题" value="已结题" />
                    <el-option label="已终止" value="已终止" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 两列：负责人ID + 经费 -->
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="负责人ID" prop="leaderId">
                  <el-input-number
                    v-model="form.leaderId"
                    :min="1"
                    placeholder="请输入负责人教师ID"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="项目经费" prop="funding">
                  <el-input-number
                    v-model="form.funding"
                    :min="0"
                    :step="10000"
                    :precision="2"
                    placeholder="单位：元"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 两列：开始/结束日期 -->
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="开始日期" prop="startDate">
                  <el-date-picker
                    v-model="form.startDate"
                    type="date"
                    placeholder="选择开始日期"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="结束日期" prop="endDate">
                  <el-date-picker
                    v-model="form.endDate"
                    type="date"
                    placeholder="选择结束日期"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 按钮行 -->
            <el-form-item>
              <div class="btn-group">
                <el-button type="primary" @click="handleSubmit">提交</el-button>
                <el-button @click="handleReset">重置</el-button>
                <el-button text type="info" @click="backToList">返回列表</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>

        <!-- 右侧提示面板 -->
        <div class="form-side">
          <div class="side-card">
            <div class="side-title">填写提示</div>
            <ul class="side-list">
              <li>项目名称尽量简洁明了，体现研究方向和内容。</li>
              <li>项目类型需与立项级别一致，例如国家自然科学基金等。</li>
              <li>项目状态建议按真实进展维护，方便后续统计与管理。</li>
              <li>开始 / 结束日期会用于统计图表与进度计算。</li>
            </ul>
          </div>

          <div class="side-card secondary">
            <div class="side-title">创建完成后</div>
            <ul class="side-list">
              <li>可在「项目列表」中查看、编辑或删除该项目。</li>
              <li>相关统计会同步更新到仪表盘和数据统计模块中。</li>
            </ul>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { createProject } from '../api/project'

export default {
  name: 'ProjectForm',

  data() {
    return {
      form: {
        name: '',
        projectType: '',
        status: '申报中',   // 默认值和数据库默认一致
        leaderId: null,
        funding: 0,
        startDate: null,
        endDate: null
      },
      rules: {
        name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
        status: [{ required: true, message: '请选择项目状态', trigger: 'change' }],
        leaderId: [{ required: true, message: '请输入负责人 ID', trigger: 'blur' }],
        funding: [
          { required: true, message: '请输入项目经费', trigger: 'blur' },
          {
            validator: (_, value, callback) => {
              if (value == null || value === '') return callback(new Error('请输入项目经费'))
              if (value < 0) return callback(new Error('经费不能为负数'))
              callback()
            },
            trigger: 'blur'
          }
        ],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [
          { required: true, message: '请选择结束日期', trigger: 'change' },
          {
            validator: (_, value, callback) => {
              if (!value || !this.form.startDate) return callback()
              if (new Date(value) < new Date(this.form.startDate)) {
                return callback(new Error('结束日期不能早于开始日期'))
              }
              callback()
            },
            trigger: 'change'
          }
        ]
      }
    }
  },

  methods: {
    // Date 对象转 yyyy-MM-dd 字符串
    toDateString(d) {
      if (!d) return null
      const date = new Date(d)
      if (Number.isNaN(date.getTime())) return null
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },

    handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return

        try {
          const payload = {
            ...this.form,
            startDate: this.toDateString(this.form.startDate),
            endDate: this.toDateString(this.form.endDate)
          }

          const res = await createProject(payload)

          if (res && res.success) {
            ElMessage.success('项目创建成功！')
            this.$router.push('/projects')
          } else {
            ElMessage.error(res?.message || '项目创建失败')
          }
        } catch (err) {
          console.error(err)
          ElMessage.error('创建失败，服务器错误')
        }
      })
    },

    handleReset() {
      this.$refs.formRef.resetFields()
    },

    backToList() {
      this.$router.push('/projects')
    }
  }
}
</script>

<style scoped>
.page-wrapper {
  padding: 16px 18px 24px;
  background: #f5f7fb;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

/* 顶部标题卡片 */
.page-header {
  border-radius: 12px;
  margin-bottom: 16px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
}

.header-left .title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left .title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-left .tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  background: #ecf5ff;
  color: #409eff;
}

.header-left .sub-title {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

/* 表单整体卡片 */
.form-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.03);
}

/* 左右布局 */
.form-layout {
  display: flex;
  gap: 32px;
}

/* 左侧表单区域 */
.form-main {
  flex: 1.3;
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-sub {
  font-size: 12px;
  color: #909399;
}

.project-form {
  margin-top: 8px;
}

/* 右侧提示区域 */
.form-side {
  flex: 1;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.side-card {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f5f7fb;
}

.side-card.secondary {
  background: #fef6f0;
}

.side-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 6px;
}

.side-list {
  margin: 0;
  padding-left: 18px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}

.full-width {
  width: 100%;
}

/* 按钮组 */
.btn-group {
  display: flex;
  gap: 8px;
}

/* 响应式：窄屏时上下布局 */
@media (max-width: 1024px) {
  .form-layout {
    flex-direction: column;
  }
  .form-side {
    max-width: 100%;
  }
}
</style>
