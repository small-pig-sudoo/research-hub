<template>
  <div class="performance-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">绩效统计</h1>
          <p class="page-subtitle">展示科研项目绩效与成果情况</p>
        </div>
      </div>
    </div>

    <!-- 查询条件区 -->
    <el-card class="filter-section" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="项目名称">
          <el-input v-model="filterForm.projectName" placeholder="请输入项目名称" style="width: 200px" />
        </el-form-item>
        <el-form-item label="项目负责人">
          <el-input v-model="filterForm.leader" placeholder="请输入项目负责人" style="width: 200px" />
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="filterForm.status" placeholder="选择项目状态" style="width: 150px" clearable>
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结题" value="已结题" />
            <el-option label="已暂停" value="已暂停" />
          </el-select>
        </el-form-item>
        <el-form-item label="绩效等级">
          <el-select v-model="filterForm.performanceLevel" placeholder="选择绩效等级" style="width: 150px" clearable>
            <el-option label="优秀" value="优秀" />
            <el-option label="良好" value="良好" />
            <el-option label="合格" value="合格" />
            <el-option label="待提升" value="待提升" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="card-section">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProjects }}</div>
              <div class="stat-label">项目总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="32"><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedProjects }}</div>
              <div class="stat-label">已结题项目数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="32"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAchievements }}</div>
              <div class="stat-label">成果总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="32"><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.highPerformanceProjects }}</div>
              <div class="stat-label">高绩效项目数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 绩效明细表格 -->
    <el-card class="table-section" shadow="never">
      <el-table :data="performanceList" style="width: 100%" v-loading="loading">
        <el-table-column prop="index" label="序号" width="80" type="index" />
        <el-table-column prop="projectName" label="项目名称" />
        <el-table-column prop="leader" label="项目负责人" />
        <el-table-column prop="status" label="项目状态" />
        <el-table-column prop="achievementCount" label="成果数量" width="100" />
        <el-table-column prop="memberCount" label="成员人数" width="100" />
        <el-table-column prop="performanceScore" label="绩效得分" width="100" />
        <el-table-column prop="performanceLevel" label="绩效等级" width="100">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.performanceLevel)">
              {{ scope.row.performanceLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="绩效详情"
      width="500px"
    >
      <el-form :model="currentPerformance" label-width="120px">
        <el-form-item label="项目名称">
          {{ currentPerformance.projectName }}
        </el-form-item>
        <el-form-item label="项目负责人">
          {{ currentPerformance.leader }}
        </el-form-item>
        <el-form-item label="项目状态">
          {{ currentPerformance.status }}
        </el-form-item>
        <el-form-item label="项目成员人数">
          {{ currentPerformance.memberCount }}
        </el-form-item>
        <el-form-item label="论文数量">
          {{ currentPerformance.paperCount }}
        </el-form-item>
        <el-form-item label="专利数量">
          {{ currentPerformance.patentCount }}
        </el-form-item>
        <el-form-item label="获奖数量">
          {{ currentPerformance.awardCount }}
        </el-form-item>
        <el-form-item label="成果总数">
          {{ currentPerformance.achievementCount }}
        </el-form-item>
        <el-form-item label="绩效得分">
          {{ currentPerformance.performanceScore }}
        </el-form-item>
        <el-form-item label="绩效等级">
          <el-tag :type="getLevelType(currentPerformance.performanceLevel)">
            {{ currentPerformance.performanceLevel }}
          </el-tag>
        </el-form-item>
        <el-form-item label="备注说明">
          {{ currentPerformance.remark || '无' }}
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 筛选表单
const filterForm = ref({
  projectName: '',
  leader: '',
  status: '',
  performanceLevel: ''
})

// 统计数据
const statistics = ref({
  totalProjects: 0,
  completedProjects: 0,
  totalAchievements: 0,
  highPerformanceProjects: 0
})

// 绩效列表
const performanceList = ref([])
const loading = ref(false)

// 详情弹窗
const detailDialogVisible = ref(false)
const currentPerformance = ref({})

// 获取绩效等级对应的标签类型
const getLevelType = (level) => {
  const typeMap = {
    '优秀': 'success',
    '良好': 'warning',
    '合格': 'info',
    '待提升': 'danger'
  }
  return typeMap[level] || 'default'
}

// 加载绩效数据
const loadPerformanceData = async () => {
  loading.value = true
  try {
    // 调用后端接口获取项目数据
    const res = await request.get('/project/recent')
    const projects = Array.isArray(res) ? res : res.data || res.records || []
    
    // 处理绩效数据
    const performanceData = projects.map(project => {
      // 模拟绩效数据，实际应该从后端获取
      const paperCount = Math.floor(Math.random() * 10) + 1
      const patentCount = Math.floor(Math.random() * 5)
      const awardCount = Math.floor(Math.random() * 3)
      const achievementCount = paperCount + patentCount + awardCount
      const memberCount = Math.floor(Math.random() * 10) + 1
      const performanceScore = Math.floor(Math.random() * 40) + 60
      
      // 计算绩效等级
      let performanceLevel
      if (performanceScore >= 90) {
        performanceLevel = '优秀'
      } else if (performanceScore >= 80) {
        performanceLevel = '良好'
      } else if (performanceScore >= 60) {
        performanceLevel = '合格'
      } else {
        performanceLevel = '待提升'
      }
      
      return {
        id: project.projectId,
        projectName: project.name,
        leader: `编号：${project.leaderId}`,
        status: project.status || '未知',
        achievementCount: achievementCount,
        memberCount: memberCount,
        performanceScore: performanceScore,
        performanceLevel: performanceLevel,
        paperCount: paperCount,
        patentCount: patentCount,
        awardCount: awardCount,
        remark: '绩效评估正常'
      }
    })
    
    // 应用筛选条件
    const filteredData = performanceData.filter(item => {
      return (
        (!filterForm.value.projectName || item.projectName.includes(filterForm.value.projectName)) &&
        (!filterForm.value.leader || item.leader.includes(filterForm.value.leader)) &&
        (!filterForm.value.status || item.status === filterForm.value.status) &&
        (!filterForm.value.performanceLevel || item.performanceLevel === filterForm.value.performanceLevel)
      )
    })
    
    // 计算统计数据
    statistics.value = {
      totalProjects: filteredData.length,
      completedProjects: filteredData.filter(item => item.status === '已结题' || item.status === 'CONCLUSION').length,
      totalAchievements: filteredData.reduce((sum, item) => sum + item.achievementCount, 0),
      highPerformanceProjects: filteredData.filter(item => item.performanceLevel === '优秀').length
    }
    
    performanceList.value = filteredData
  } catch (error) {
    console.error('加载绩效数据失败', error)
    ElMessage.error('加载绩效数据失败')
    
    // 使用模拟数据
    const mockPerformanceData = [
      {
        id: 1,
        projectName: '基于大数据的创新研发',
        leader: '编号：101',
        status: '已结题',
        achievementCount: 15,
        memberCount: 8,
        performanceScore: 95,
        performanceLevel: '优秀',
        paperCount: 8,
        patentCount: 4,
        awardCount: 3,
        remark: '项目成果显著，获得多项专利和奖项'
      },
      {
        id: 2,
        projectName: '基于多组学分析的疾病预测模型',
        leader: '编号：102',
        status: '进行中',
        achievementCount: 8,
        memberCount: 5,
        performanceScore: 85,
        performanceLevel: '良好',
        paperCount: 5,
        patentCount: 2,
        awardCount: 1,
        remark: '项目进展顺利，已发表多篇论文'
      },
      {
        id: 3,
        projectName: '基于AI的医疗影像分析',
        leader: '编号：103',
        status: '已结题',
        achievementCount: 12,
        memberCount: 6,
        performanceScore: 92,
        performanceLevel: '优秀',
        paperCount: 6,
        patentCount: 3,
        awardCount: 3,
        remark: '项目成果应用于临床实践，效果显著'
      },
      {
        id: 4,
        projectName: '基于区块链的科研数据管理',
        leader: '编号：104',
        status: '进行中',
        achievementCount: 5,
        memberCount: 4,
        performanceScore: 75,
        performanceLevel: '良好',
        paperCount: 3,
        patentCount: 1,
        awardCount: 1,
        remark: '项目处于初期阶段，进展良好'
      },
      {
        id: 5,
        projectName: '基于物联网的环境监测系统',
        leader: '编号：105',
        status: '已结题',
        achievementCount: 10,
        memberCount: 7,
        performanceScore: 88,
        performanceLevel: '良好',
        paperCount: 4,
        patentCount: 3,
        awardCount: 3,
        remark: '项目成果已在多个城市部署应用'
      }
    ]
    
    // 应用筛选条件
    const filteredData = mockPerformanceData.filter(item => {
      return (
        (!filterForm.value.projectName || item.projectName.includes(filterForm.value.projectName)) &&
        (!filterForm.value.leader || item.leader.includes(filterForm.value.leader)) &&
        (!filterForm.value.status || item.status === filterForm.value.status) &&
        (!filterForm.value.performanceLevel || item.performanceLevel === filterForm.value.performanceLevel)
      )
    })
    
    // 计算统计数据
    statistics.value = {
      totalProjects: filteredData.length,
      completedProjects: filteredData.filter(item => item.status === '已结题').length,
      totalAchievements: filteredData.reduce((sum, item) => sum + item.achievementCount, 0),
      highPerformanceProjects: filteredData.filter(item => item.performanceLevel === '优秀').length
    }
    
    performanceList.value = filteredData
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadPerformanceData()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    projectName: '',
    leader: '',
    status: '',
    performanceLevel: ''
  }
  loadPerformanceData()
}

// 查看详情
const handleView = (row) => {
  currentPerformance.value = { ...row }
  detailDialogVisible.value = true
}

// 页面加载时初始化数据
onMounted(() => {
  loadPerformanceData()
})
</script>

<style scoped>
.performance-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.card-section {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

.table-section {
  margin-bottom: 20px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
}

.el-table td {
  vertical-align: middle;
}

.el-dialog {
  border-radius: 4px;
}

.el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.el-dialog__title {
  font-size: 16px;
  font-weight: 600;
}

.el-form-item {
  margin-bottom: 16px;
}
</style>
