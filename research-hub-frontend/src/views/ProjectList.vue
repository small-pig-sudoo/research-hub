<template>
  <div class="page-wrapper">
    <!-- 顶部标题 -->
    <el-card class="page-header" shadow="never">
      <div class="header-left">
        <div class="title">科研项目管理</div>
        <div class="sub-title">管理您的科研项目信息</div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="$router.push('/projects/create')">
          新增项目
        </el-button>
      </div>
    </el-card>

    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-header">
        <span class="filter-title">筛选条件</span>
      </div>
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="项目名称">
          <el-input
            v-model="query.name"
            placeholder="输入项目名称"
            clearable
            style="width: 220px"
          />
        </el-form-item>

        <el-form-item label="项目类型">
          <el-select
            v-model="query.projectType"
            placeholder="选择类型"
            clearable
            style="width: 180px"
          >
            <el-option label="国家级" value="国家级" />
            <el-option label="省部级" value="省部级" />
            <el-option label="校级" value="校级" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="项目状态">
          <el-select
            v-model="query.status"
            placeholder="选择状态"
            clearable
            style="width: 160px"
          >
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结项" value="已结项" />
            <el-option label="暂停" value="暂停" />
          </el-select>
        </el-form-item>

        <el-form-item class="filter-actions">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 + 分页 -->
    <el-card class="table-card" shadow="never">
      <div class="table-header-row">
        <div class="table-title">
          共 <span class="table-count">{{ pagination.total }}</span> 个项目
        </div>
        <div class="table-actions">
          <el-button text @click="handleRefresh">刷新</el-button>
          <el-button text>导出</el-button>
        </div>
      </div>

      <div class="table-wrapper">
        <el-table
          v-loading="loading"
          :data="tableData"
          class="project-table"
          :border="false"
          stripe
          header-cell-class-name="table-header-cell"
        >
          <el-table-column
            type="index"
            label="序号"
            width="70"
            align="center"
          />

          <el-table-column
            prop="projectId"
            label="项目编号"
            width="110"
            align="center"
          />

          <el-table-column
            prop="name"
            label="项目名称"
            min-width="260"
            show-overflow-tooltip
          />

          <el-table-column
            prop="projectType"
            label="类型"
            width="100"
            align="center"
          >
            <template #default="{ row }">
              <el-tag
                v-if="row.projectType"
                :type="typeTagType(row.projectType)"
                effect="light"
              >
                {{ row.projectType }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="status"
            label="状态"
            width="100"
            align="center"
          >
            <template #default="{ row }">
              <el-tag
                v-if="row.status"
                :type="statusTagType(row.status)"
                effect="plain"
              >
                {{ row.status }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="leaderId"
            label="负责人ID"
            width="110"
            align="center"
          />

          <el-table-column
            prop="funding"
            label="经费(万)"
            width="110"
            align="right"
          >
            <template #default="{ row }">
              <span v-if="row.funding != null">
                {{ Number(row.funding).toFixed(1) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="startDate"
            label="开始日期"
            width="130"
            align="center"
          >
            <template #default="{ row }">
              {{ formatDate(row.startDate) }}
            </template>
          </el-table-column>

          <el-table-column
            prop="endDate"
            label="结束日期"
            width="130"
            align="center"
          >
            <template #default="{ row }">
              {{ formatDate(row.endDate) }}
            </template>
          </el-table-column>

<el-table-column
  label="操作"
  width="180"
  fixed="right"
  align="center"
>
  <template #default="{ row }">
    <div class="action-cell">
      <el-button size="small" link type="primary">
        查看
      </el-button>
      <el-button size="small" link type="primary">
        编辑
      </el-button>
      <el-button size="small" link type="danger">
        删除
      </el-button>
    </div>
  </template>
</el-table-column>
        </el-table>
      </div>

      <div class="table-footer">
        <div class="footer-left"></div>
        <div class="footer-right">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="pagination.page"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </el-card>

  </div>
</template>

<script>
import { getProjects } from '../api/project'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProjectList',

  data() {
    return {
      loading: false,
      // 查询条件
      query: {
        name: '',
        projectType: '',
        status: ''
      },
      // 当前页表格数据
      tableData: [],
      // 分页信息（与后端保持一致）
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      }
    }
  },

  created() {
    this.fetchProjects()
  },

  methods: {
    // 从后端加载项目列表（带分页和筛选条件）
    async fetchProjects() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.page,
          pageSize: this.pagination.pageSize,
          name: this.query.name || undefined,
          projectType: this.query.projectType || undefined,
          status: this.query.status || undefined
        }

        const res = await getProjects(params)

        let data = res
        // 兼容 { success, data: {...} } 这种结果
        if (res && typeof res === 'object' && 'success' in res) {
          data = res.data
        }

        if (Array.isArray(data)) {
          this.tableData = data
          this.pagination.total = data.length
        } else if (data && typeof data === 'object') {
          this.tableData = data.records || data.list || []
          this.pagination.total = data.total || this.tableData.length
        } else {
          this.tableData = []
          this.pagination.total = 0
        }
      } catch (err) {
        console.error('加载项目列表失败:', err)
        ElMessage.error('加载项目列表失败，请检查后端服务')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.pagination.page = 1
      this.fetchProjects()
    },

    handleReset() {
      this.query = {
        name: '',
        projectType: '',
        status: ''
      }
      this.pagination.page = 1
      this.fetchProjects()
    },

    handlePageChange(page) {
      this.pagination.page = page
      this.fetchProjects()
    },

    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.page = 1
      this.fetchProjects()
    },

    handleRefresh() {
      this.fetchProjects()
    },

    // 类型颜色
    typeTagType(type) {
      switch (type) {
        case '国家级':
          return 'danger'
        case '省部级':
          return 'warning'
        case '校级':
          return 'success'
        default:
          return ''
      }
    },

    // 状态颜色
    statusTagType(status) {
      switch (status) {
        case '进行中':
          return 'success'
        case '未开始':
          return 'info'
        case '已结项':
          return ''
        case '暂停':
          return 'warning'
        default:
          return 'info'
      }
    },

    // 简单日期格式化：只取 yyyy-MM-dd
    formatDate(val) {
      if (!val) return '-'
      const d = new Date(val)
      if (Number.isNaN(d.getTime())) return '-'
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
}

.header-left .title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-left .sub-title {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.02);
}

.filter-header {
  margin-bottom: 8px;
}

.filter-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.filter-form :deep(.el-form-item) {
  margin-right: 16px;
  margin-bottom: 8px;
}

.filter-actions {
  margin-left: auto;
}

/* 表格卡片整体 */
.table-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.03);
}

.table-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.table-title {
  font-size: 14px;
  color: #606266;
}

.table-count {
  font-weight: 600;
  color: #303133;
}

.table-actions {
  display: flex;
  gap: 4px;
}

/* 表格本体 */
.table-wrapper {
  border-radius: 12px;
  overflow: hidden;
}

.project-table {
  width: 100%;
}

/* 表头更柔和的背景 */
:deep(.table-header-cell) {
  background: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
}

/* 去掉表格外层多余边框，只保留行分隔线 */
:deep(.el-table) {
  border-radius: 12px;
}

:deep(.el-table__inner-wrapper::before) {
  display: none;
}

/* 行 hover 高亮 */
:deep(.el-table__body-wrapper tr:hover > td) {
  background-color: #f0f5ff !important;
}

/* 分页区 */
.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-right {
  display: flex;
  justify-content: flex-end;
  flex: 1;
}
.action-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

</style>
