<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.companyCount }}</div>
              <div class="stat-label">企业总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon size="30"><Van /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.vehicleCount }}</div>
              <div class="stat-label">车辆总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon size="30"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.transportCount }}</div>
              <div class="stat-label">运输记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon size="30"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.feedbackCount }}</div>
              <div class="stat-label">待处理反馈</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 企业类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>企业类型分布</span>
          </template>
          <div ref="companyTypeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 车辆类别分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车辆类别分布</span>
          </template>
          <div ref="vehicleCategoryChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 车辆类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车辆类型分布（普通/冷藏）</span>
          </template>
          <div ref="vehicleTypeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 车牌颜色分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车牌颜色分布</span>
          </template>
          <div ref="colorPlateChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 反馈词云图 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>主要运营困难词云图</span>
          </template>
          <FeedbackWordCloud />
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计表格 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>企业车辆统计详情</span>
          </template>
          <el-table :data="companyVehicleStats" stripe v-loading="loading">
            <el-table-column prop="companyName" label="企业名称" min-width="200" />
            <el-table-column prop="vehicleCount" label="车辆总数" width="100" align="center" />
            <el-table-column prop="basketCount" label="菜篮子工程车" width="140" align="center" />
            <el-table-column prop="freightCount" label="非菜篮子工程车" width="160" align="center" />
            <el-table-column prop="regularCount" label="普通车辆" width="100" align="center" />
            <el-table-column prop="coldCount" label="冷藏车辆" width="100" align="center" />
            <el-table-column prop="blueCount" label="蓝牌" width="80" align="center" />
            <el-table-column prop="yellowCount" label="黄牌" width="80" align="center" />
            <el-table-column prop="greenCount" label="绿牌" width="80" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { OfficeBuilding, Van, DataAnalysis, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { statisticsApi, companyApi, vehicleApi } from '@/api'
import * as echarts from 'echarts'
import FeedbackWordCloud from '@/components/FeedbackWordCloud.vue'

const stats = ref({
  companyCount: 0,
  vehicleCount: 0,
  transportCount: 0,
  feedbackCount: 0
})

const loading = ref(false)
const companyVehicleStats = ref([])

const companyTypeChart = ref(null)
const vehicleCategoryChart = ref(null)
const vehicleTypeChart = ref(null)
const colorPlateChart = ref(null)

let companyTypeChartInstance = null
let vehicleCategoryChartInstance = null
let vehicleTypeChartInstance = null
let colorPlateChartInstance = null

onMounted(() => {
  loadStats()
  nextTick(() => {
    initCharts()
  })
})

const loadStats = async () => {
  try {
    loading.value = true
    // 使用 Promise.allSettled 而不是 Promise.all，这样即使部分请求失败也能继续
    const [statsResult, companiesResult, vehiclesResult] = await Promise.allSettled([
      statisticsApi.getStatistics(),
      companyApi.getAll(),
      vehicleApi.getAll()
    ])
    
    // 处理统计数据
    const statsData = statsResult.status === 'fulfilled' ? statsResult.value : {}
    const companiesData = companiesResult.status === 'fulfilled' ? companiesResult.value : []
    const vehiclesData = vehiclesResult.status === 'fulfilled' ? vehiclesResult.value : []
    
    // 如果有请求失败，记录但不阻止页面显示
    if (statsResult.status === 'rejected') {
      console.error('加载统计数据失败:', statsResult.reason)
      ElMessage.warning('加载统计数据失败，部分数据可能无法显示')
    }
    if (companiesResult.status === 'rejected') {
      console.error('加载企业数据失败:', companiesResult.reason)
    }
    if (vehiclesResult.status === 'rejected') {
      console.error('加载车辆数据失败:', vehiclesResult.reason)
    }

    stats.value = {
      companyCount: statsData.companyCount || 0,
      vehicleCount: statsData.vehicleCount || 0,
      transportCount: statsData.transportCount || 0,
      feedbackCount: statsData.feedbackCount || 0
    }

    // 处理企业类型分布
    const companyTypes = {}
    if (Array.isArray(companiesData)) {
      companiesData.forEach(company => {
        const type = company.companyType || '其他'
        companyTypes[type] = (companyTypes[type] || 0) + 1
      })
    }

    // 处理车辆统计
    const vehicleStats = {}
    const categoryStats = { '菜篮子工程车': 0, '非菜篮子工程车': 0 }
    const typeStats = { '普通': 0, '冷藏': 0 }
    const colorStats = { '蓝牌': 0, '黄牌': 0, '绿牌': 0 }

    if (Array.isArray(vehiclesData)) {
      vehiclesData.forEach(vehicle => {
        // 按类别统计
        if (vehicle.vehicleCategory) {
          categoryStats[vehicle.vehicleCategory] = (categoryStats[vehicle.vehicleCategory] || 0) + 1
        }
        // 按类型统计
        if (vehicle.vehicleType) {
          typeStats[vehicle.vehicleType] = (typeStats[vehicle.vehicleType] || 0) + 1
        }
        // 按颜色统计
        if (vehicle.colorPlate) {
          colorStats[vehicle.colorPlate] = (colorStats[vehicle.colorPlate] || 0) + 1
        }

        // 按企业统计
        if (vehicle.companyId) {
          if (!vehicleStats[vehicle.companyId]) {
            vehicleStats[vehicle.companyId] = {
              companyId: vehicle.companyId,
              companyName: vehicle.companyName || '未知企业',
              vehicleCount: 0,
              basketCount: 0,
              freightCount: 0,
              regularCount: 0,
              coldCount: 0,
              blueCount: 0,
              yellowCount: 0,
              greenCount: 0
            }
          }
          vehicleStats[vehicle.companyId].vehicleCount++
          if (vehicle.vehicleCategory === '菜篮子工程车') {
            vehicleStats[vehicle.companyId].basketCount++
          } else if (vehicle.vehicleCategory === '非菜篮子工程车') {
            vehicleStats[vehicle.companyId].freightCount++
          }
          if (vehicle.vehicleType === '普通') {
            vehicleStats[vehicle.companyId].regularCount++
          } else if (vehicle.vehicleType === '冷藏') {
            vehicleStats[vehicle.companyId].coldCount++
          }
          if (vehicle.colorPlate === '蓝牌') {
            vehicleStats[vehicle.companyId].blueCount++
          } else if (vehicle.colorPlate === '黄牌') {
            vehicleStats[vehicle.companyId].yellowCount++
          } else if (vehicle.colorPlate === '绿牌') {
            vehicleStats[vehicle.companyId].greenCount++
          }
        }
      })
    }

    companyVehicleStats.value = Object.values(vehicleStats)

    // 更新图表
    updateCompanyTypeChart(companyTypes)
    updateVehicleCategoryChart(categoryStats)
    updateVehicleTypeChart(typeStats)
    updateColorPlateChart(colorStats)
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  if (companyTypeChart.value) {
    companyTypeChartInstance = echarts.init(companyTypeChart.value)
  }
  if (vehicleCategoryChart.value) {
    vehicleCategoryChartInstance = echarts.init(vehicleCategoryChart.value)
  }
  if (vehicleTypeChart.value) {
    vehicleTypeChartInstance = echarts.init(vehicleTypeChart.value)
  }
  if (colorPlateChart.value) {
    colorPlateChartInstance = echarts.init(colorPlateChart.value)
  }
}

const updateCompanyTypeChart = (data) => {
  if (!companyTypeChartInstance) return
  const total = Object.values(data).reduce((sum, val) => sum + val, 0)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '企业类型',
        type: 'pie',
        radius: '50%',
        data: Object.keys(data).map(key => ({
          value: data[key],
          name: key
        })),
        label: {
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  companyTypeChartInstance.setOption(option)
}

const updateVehicleCategoryChart = (data) => {
  if (!vehicleCategoryChartInstance) return
  const total = Object.values(data).reduce((sum, val) => sum + val, 0)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '车辆类别',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)',
          position: 'outside'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true
        },
        data: Object.keys(data).map(key => ({
          value: data[key],
          name: key
        }))
      }
    ]
  }
  vehicleCategoryChartInstance.setOption(option)
}

const updateVehicleTypeChart = (data) => {
  if (!vehicleTypeChartInstance) return
  const total = Object.values(data).reduce((sum, val) => sum + val, 0)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Object.keys(data)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '车辆数量',
        type: 'bar',
        data: Object.values(data),
        label: {
          show: true,
          position: 'top',
          formatter: (params) => {
            const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
            return `${params.value} (${percentage}%)`
          }
        },
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        }
      }
    ]
  }
  vehicleTypeChartInstance.setOption(option)
}

const updateColorPlateChart = (data) => {
  if (!colorPlateChartInstance) return
  const total = Object.values(data).reduce((sum, val) => sum + val, 0)
  
  // 定义车牌颜色映射（修复颜色对调问题）
  const colorMap = {
    '蓝牌': '#409EFF',  // 蓝色
    '黄牌': '#E6A23C',  // 黄色
    '绿牌': '#67C23A'   // 绿色
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '车牌颜色',
        type: 'pie',
        radius: '60%',
        data: Object.keys(data).map(key => ({
          value: data[key],
          name: key,
          itemStyle: {
            color: colorMap[key] || '#909399'
          }
        })),
        label: {
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  colorPlateChartInstance.setOption(option)
}
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    margin-bottom: 20px;

    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        margin-right: 20px;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}
</style>
