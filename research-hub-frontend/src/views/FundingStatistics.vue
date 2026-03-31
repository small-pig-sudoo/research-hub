<template>
  <div class="funding-statistics-container">
    <!-- 页面标题区 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">经费统计</h1>
          <p class="page-subtitle">展示科研项目经费使用情况</p>
        </div>
      </div>
    </div>

    <!-- 筛选查询区 -->
    <el-card class="filter-section" shadow="never">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="项目名称">
          <el-input v-model="filterForm.projectName" placeholder="请输入项目名称" style="width: 200px" clearable />
        </el-form-item>
        <el-form-item label="项目负责人">
          <el-input v-model="filterForm.leader" placeholder="请输入负责人" style="width: 150px" clearable />
        </el-form-item>
        <el-form-item label="经费状态">
          <el-select v-model="filterForm.status" placeholder="选择经费状态" style="width: 150px" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="超支" value="over" />
            <el-option label="未使用" value="unused" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片区 -->
    <el-row :gutter="20" class="card-section">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFunding }}</div>
              <div class="stat-label">经费总额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="32"><ArrowDown /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.usedFunding }}</div>
              <div class="stat-label">已使用经费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="32"><ArrowUp /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.remainingFunding }}</div>
              <div class="stat-label">剩余经费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.projectCount }}</div>
              <div class="stat-label">项目数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区 -->
    <el-card class="chart-section" shadow="never">
      <template #header>
        <h3>项目经费使用情况</h3>
      </template>
      <div ref="fundingChartRef" class="chart-container"></div>
    </el-card>

    <!-- 经费明细表格区 -->
    <el-card class="table-section" shadow="never">
      <template #header>
        <h3>经费明细</h3>
      </template>
      <el-table
        v-loading="loading"
        :data="fundingList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column label="项目ID" width="120">
          <template #default="scope">
            {{ scope.row.projectId }}
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="项目负责人" width="150" />
        <el-table-column label="总经费" width="120">
          <template #default="scope">
            {{ scope.row.totalFundingFormatted }}
          </template>
        </el-table-column>
        <el-table-column label="已使用经费" width="120">
          <template #default="scope">
            {{ scope.row.usedFundingFormatted }}
          </template>
        </el-table-column>
        <el-table-column label="剩余经费" width="120">
          <template #default="scope">
            {{ scope.row.remainingFundingFormatted }}
          </template>
        </el-table-column>
        <el-table-column prop="usageRate" label="经费使用率" width="120">
          <template #default="scope">
            <el-progress :percentage="scope.row.usageRate" :color="getUsageRateColor(scope.row.usageRate)" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="openDetailDialog(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="经费详情"
      width="500px"
    >
      <el-form :model="currentFunding" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="currentFunding.projectName" disabled />
        </el-form-item>
        <el-form-item label="项目负责人">
          <el-input v-model="currentFunding.leader" disabled />
        </el-form-item>
        <el-form-item label="总经费">
          <el-input v-model="currentFunding.totalFunding" disabled />
        </el-form-item>
        <el-form-item label="已使用经费">
          <el-input v-model="currentFunding.usedFunding" disabled />
        </el-form-item>
        <el-form-item label="剩余经费">
          <el-input v-model="currentFunding.remainingFunding" disabled />
        </el-form-item>
        <el-form-item label="经费使用率">
          <el-progress :percentage="currentFunding.usageRate" :color="getUsageRateColor(currentFunding.usageRate)" />
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusTag(currentFunding.status)">
            {{ currentFunding.status }}
          </el-tag>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="currentFunding.remark" type="textarea" disabled />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElProgress, ElTag } from 'element-plus'
import { Money, ArrowDown, ArrowUp, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '../utils/request'

// 筛选表单
const filterForm = ref({
  projectName: '',
  leader: '',
  status: ''
})

// 统计数据
const statistics = ref({
  totalFunding: 0,
  usedFunding: 0,
  remainingFunding: 0,
  projectCount: 0
})

// 经费列表
const fundingList = ref([])
const loading = ref(false)

// 图表引用
const fundingChartRef = ref(null)
let fundingChart = null

// 详情弹窗
const detailDialogVisible = ref(false)
const currentFunding = ref({})

// 数字格式化函数
const formatNumber = (num) => {
  if (num >= 100000000) {
    return (num / 100000000).toFixed(2) + '亿'
  } else if (num >= 10000) {
    return (num / 10000).toFixed(2) + '万'
  } else {
    return num.toFixed(2)
  }
}

// 加载经费数据
const loadFundingData = async () => {
  loading.value = true
  try {
    // 调用与项目统计相同的接口获取项目数据
    const res = await request.get('/project/recent')
    const projects = Array.isArray(res) ? res : res.data || res.records || []
    
    // 处理经费数据
    const fundingData = projects.map(project => {
      // 模拟经费数据，实际应该从后端获取
      const totalFunding = parseFloat(project.funding) || 0
      const usedFunding = totalFunding * 0.3 // 假设使用了30%
      const remainingFunding = totalFunding - usedFunding
      const usageRate = totalFunding > 0 ? (usedFunding / totalFunding) * 100 : 0
      
      // 确定状态
      let status = '正常'
      if (usageRate === 0) {
        status = '未使用'
      } else if (usageRate > 100) {
        status = '超支'
      }
      
      return {
        id: project.projectId,
        projectName: project.name,
        projectId: project.projectId,
        leader: `编号：${project.leaderId}`,
        totalFunding: totalFunding.toFixed(2),
        usedFunding: usedFunding.toFixed(2),
        remainingFunding: remainingFunding.toFixed(2),
        totalFundingFormatted: formatNumber(totalFunding),
        usedFundingFormatted: formatNumber(usedFunding),
        remainingFundingFormatted: formatNumber(remainingFunding),
        usageRate: Math.round(usageRate),
        status: status,
        remark: '经费使用正常'
      }
    })
    
    // 计算统计数据
    const totalFunding = fundingData.reduce((sum, item) => sum + parseFloat(item.totalFunding), 0)
    const usedFunding = fundingData.reduce((sum, item) => sum + parseFloat(item.usedFunding), 0)
    const remainingFunding = totalFunding - usedFunding
    
    statistics.value = {
      totalFunding: formatNumber(totalFunding),
      usedFunding: formatNumber(usedFunding),
      remainingFunding: formatNumber(remainingFunding),
      projectCount: fundingData.length
    }
    
    fundingList.value = fundingData
    updateFundingChart(fundingData)
  } catch (error) {
    console.error('加载经费数据失败，使用模拟数据', error)
    ElMessage.warning('无法连接后端服务，使用模拟数据')
    
    // 使用模拟数据
    const mockFundingData = [
      {
        id: 1,
        projectId: 1,
        projectName: '基于大数据的创新研发',
        leaderId: 101,
        totalFunding: '500000.00',
        usedFunding: '150000.00',
        remainingFunding: '350000.00',
        totalFundingFormatted: '50.00万',
        usedFundingFormatted: '15.00万',
        remainingFundingFormatted: '35.00万',
        usageRate: 30,
        status: '正常',
        remark: '经费使用正常'
      },
      {
        id: 2,
        projectId: 2,
        projectName: '基于多组学分析的疾病预测模型',
        leaderId: 102,
        totalFunding: '111000.00',
        usedFunding: '33300.00',
        remainingFunding: '77700.00',
        totalFundingFormatted: '11.10万',
        usedFundingFormatted: '3.33万',
        remainingFundingFormatted: '7.77万',
        usageRate: 30,
        status: '正常',
        remark: '经费使用正常'
      },
      {
        id: 3,
        projectId: 3,
        projectName: '基于AI的医疗影像分析',
        leaderId: 103,
        totalFunding: '10000.00',
        usedFunding: '3000.00',
        remainingFunding: '7000.00',
        totalFundingFormatted: '1.00万',
        usedFundingFormatted: '3000.00',
        remainingFundingFormatted: '7000.00',
        usageRate: 30,
        status: '正常',
        remark: '经费使用正常'
      },
      {
        id: 4,
        projectId: 4,
        projectName: '基于区块链的科研数据管理',
        leaderId: 104,
        totalFunding: '1000.00',
        usedFunding: '300.00',
        remainingFunding: '700.00',
        totalFundingFormatted: '1000.00',
        usedFundingFormatted: '300.00',
        remainingFundingFormatted: '700.00',
        usageRate: 30,
        status: '正常',
        remark: '经费使用正常'
      }
    ]
    
    // 计算统计数据
    const totalFunding = mockFundingData.reduce((sum, item) => sum + parseFloat(item.totalFunding), 0)
    const usedFunding = mockFundingData.reduce((sum, item) => sum + parseFloat(item.usedFunding), 0)
    const remainingFunding = totalFunding - usedFunding
    
    statistics.value = {
      totalFunding: formatNumber(totalFunding),
      usedFunding: formatNumber(usedFunding),
      remainingFunding: formatNumber(remainingFunding),
      projectCount: mockFundingData.length
    }
    
    fundingList.value = mockFundingData
    updateFundingChart(mockFundingData)
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initFundingChart = () => {
  if (fundingChartRef.value) {
    fundingChart = echarts.init(fundingChartRef.value)
  }
}

// 更新经费图表
const updateFundingChart = (fundingData) => {
  if (!fundingChart) {
    initFundingChart()
  }
  
  if (fundingChart) {
    const projectIds = fundingData.map(item => `项目 ${item.projectId}`)
    const totalFunding = fundingData.map(item => parseFloat(item.totalFunding))
    const usedFunding = fundingData.map(item => parseFloat(item.usedFunding))
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: function(params) {
          let result = params[0].name + '<br/>'
          params.forEach(item => {
            result += item.marker + item.seriesName + ': ' + item.data + ' 元<br/>'
          })
          return result
        }
      },
      legend: {
        data: ['总经费', '已使用经费'],
        top: 'bottom',
        bottom: '10%'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: projectIds,
        axisLabel: {
          rotate: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '金额'
      },
      series: [
        {
          name: '总经费',
          type: 'bar',
          data: totalFunding,
          itemStyle: {
            color: '#409eff'
          }
        },
        {
          name: '已使用经费',
          type: 'bar',
          data: usedFunding,
          itemStyle: {
            color: '#67c23a'
          }
        }
      ]
    }
    
    fundingChart.setOption(option)
  }
}

// 获取使用率颜色
const getUsageRateColor = (rate) => {
  if (rate >= 90) {
    return '#f56c6c'
  } else if (rate >= 60) {
    return '#e6a23c'
  } else {
    return '#67c23a'
  }
}

// 获取状态标签类型
const getStatusTag = (status) => {
  const statusMap = {
    '正常': 'success',
    '超支': 'danger',
    '未使用': 'info'
  }
  return statusMap[status] || 'info'
}

// 搜索
const handleSearch = () => {
  loadFundingData()
}

// 重置
const handleReset = () => {
  filterForm.value = {
    projectName: '',
    leader: '',
    status: ''
  }
  loadFundingData()
}

// 打开详情弹窗
const openDetailDialog = (funding) => {
  currentFunding.value = { ...funding }
  detailDialogVisible.value = true
}

// 处理窗口大小变化
const handleResize = () => {
  if (fundingChart) {
    fundingChart.resize()
  }
}

// 初始化
onMounted(() => {
  loadFundingData()
  window.addEventListener('resize', handleResize)
})

// 销毁
onUnmounted(() => {
  if (fundingChart) {
    fundingChart.dispose()
    fundingChart = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.funding-statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 10px;
}

.title-section {
  flex: 1;
  min-width: 300px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.card-section {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-section {
  margin-bottom: 20px;
}

.chart-section h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.chart-container {
  width: 100%;
  height: 400px;
  font-family: "Microsoft YaHei", "PingFang SC", "Hiragino Sans GB", "WenQuanYi Micro Hei", sans-serif;
}

.table-section {
  margin-bottom: 20px;
}

.table-section h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .funding-statistics-container {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-form .el-form-item {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .filter-form .el-input,
  .filter-form .el-select {
    width: 100% !important;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>
