<template>
  <div class="enterprise-analytics">
    <el-card class="filter-card">
      <template #header>
        <div class="filter-header">
          <span>企业运输与车辆运营分析</span>
          <div class="filter-header-actions">
            <el-button size="small" @click="exportCsv">导出数据 CSV</el-button>
            <el-button size="small" @click="exportCharts">导出图表 PNG</el-button>
          </div>
        </div>
      </template>
      <el-form :inline="true" :model="filters">
        <el-form-item label="企业">
          <el-select v-model="filters.companyId" placeholder="全部企业" clearable filterable style="width: 220px">
            <el-option
              v-for="c in companies"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-select v-model="filters.year" placeholder="选择年份" style="width: 140px">
            <el-option v-for="y in years" :key="y" :label="y" :value="y" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAll">应用筛选</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 企业运输总览 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ summary.cagrLabel }}</div>
          <div class="stat-label">复合年均增长率 (2022-2025)</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ summary.totalGrowthLabel }}</div>
          <div class="stat-label">运输量总增长 (2022→2025)</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ summary.trendType || '—' }}</div>
          <div class="stat-label">趋势类型</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14">
        <el-card>
          <template #header>
            <span>年度运输量趋势（2022-2025）</span>
          </template>
          <div ref="transportTrendRef" style="height: 360px"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>年度同比增长率</span>
          </template>
          <div ref="yoyBarRef" style="height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 车辆年度运营分析 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车辆运输量排行榜</span>
          </template>
          <div ref="vehicleRankRef" style="height: 360px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>装载率与运输量关系散点图</span>
          </template>
          <div ref="loadingScatterRef" style="height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 车辆效率散点图 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>车辆效率散点图（X=年里程, Y=年运输量, 气泡=成本）</span>
          </template>
          <div ref="vehicleScatterRef" style="height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 车辆月度趋势多线图 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="filter-header">
              <span>车辆月度运输量趋势（多车对比）</span>
              <div class="filter-header-actions">
                <el-select
                  v-model="selectedVehicleIds"
                  multiple
                  clearable
                  collapse-tags
                  placeholder="选择车辆（不选则展示年运输量前5名）"
                  style="min-width: 280px"
                  @change="updateMonthlyTrendChart"
                >
                  <el-option
                    v-for="v in vehicleYearStats"
                    :key="v.vehicleId"
                    :label="v.plateNumber"
                    :value="v.vehicleId"
                  />
                </el-select>
              </div>
            </div>
          </template>
          <div ref="monthlyTrendRef" style="height: 380px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { analyticsApi, companyApi, surveyApi, transportApi } from '@/api'

const companies = ref([])
const years = ref([2022, 2023, 2024, 2025])
const filters = ref({
  companyId: null,
  year: 2025
})

const transportTrends = ref([]) // CompanyTransportTrendDTO 列表
const vehicleYearStats = ref([]) // VehicleYearStatsDTO 列表

// 问卷和月度明细，用于交叉分析
const surveyData = ref([])
const transportMonthlyStats = ref([])

const summary = ref({
  cagrLabel: '—',
  totalGrowthLabel: '—',
  trendType: ''
})

const transportTrendRef = ref(null)
const yoyBarRef = ref(null)
const vehicleRankRef = ref(null)
const vehicleScatterRef = ref(null)
const loadingScatterRef = ref(null)
const monthlyTrendRef = ref(null)

let transportTrendChart = null
let yoyBarChart = null
let vehicleRankChart = null
let vehicleScatterChart = null
let loadingScatterChart = null
let monthlyTrendChart = null

// 下钻选中的车辆
const selectedVehicleIds = ref([])

onMounted(() => {
  loadCompanies()
  loadAll()
  nextTick(() => {
    initCharts()
  })
})

const loadCompanies = async () => {
  try {
    companies.value = await companyApi.getAll()
  } catch (e) {
    console.error(e)
  }
}

const loadAll = async () => {
  try {
    const trendParams = {}
    if (filters.value.companyId) {
      trendParams.companyId = filters.value.companyId
    }
    const yearParams = {
      year: filters.value.year
    }
    if (filters.value.companyId) {
      yearParams.companyId = filters.value.companyId
    }

    const surveyParams = {}
    if (filters.value.year) {
      surveyParams.year = filters.value.year
    }

    const [trendResp, vehicleResp, surveyResp, transportResp] = await Promise.all([
      analyticsApi.getCompanyTransportTrends(trendParams),
      analyticsApi.getVehicleYearStats(yearParams),
      filters.value.year ? surveyApi.getByYear(filters.value.year) : surveyApi.getAll(),
      filters.value.companyId ? transportApi.getByCompanyId(filters.value.companyId) : transportApi.getAll()
    ])

    // 统一做一层解包，兼容直接数组或 { data: [] } 结构
    const unwrapList = (resp) => {
      if (Array.isArray(resp)) return resp
      if (resp && Array.isArray(resp.data)) return resp.data
      return []
    }

    transportTrends.value = unwrapList(trendResp)
    vehicleYearStats.value = unwrapList(vehicleResp)
    surveyData.value = unwrapList(surveyResp)
    transportMonthlyStats.value = unwrapList(transportResp)

    updateSummary()
    nextTick(() => {
      updateTransportTrendChart()
      updateYoyBarChart()
      updateVehicleRankChart()
      updateVehicleScatterChart()
      updateLoadingScatterChart()
      updateMonthlyTrendChart()
    })
  } catch (e) {
    console.error(e)
    ElMessage.error('加载分析数据失败')
  }
}

const initCharts = () => {
  if (transportTrendRef.value) {
    transportTrendChart = echarts.init(transportTrendRef.value)
  }
  if (yoyBarRef.value) {
    yoyBarChart = echarts.init(yoyBarRef.value)
  }
  if (vehicleRankRef.value) {
    vehicleRankChart = echarts.init(vehicleRankRef.value)
  }
  if (vehicleScatterRef.value) {
    vehicleScatterChart = echarts.init(vehicleScatterRef.value)
  }

  if (loadingScatterRef.value) {
    loadingScatterChart = echarts.init(loadingScatterRef.value)
  }
  if (monthlyTrendRef.value) {
    monthlyTrendChart = echarts.init(monthlyTrendRef.value)
  }

  // 下钻交互：点击排行榜或散点图的车辆，同步到月度趋势选择
  if (vehicleRankChart) {
    vehicleRankChart.on('click', params => {
      handleVehicleClickByName(params.name)
    })
  }
  if (vehicleScatterChart) {
    vehicleScatterChart.on('click', params => {
      handleVehicleClickByName(params.name)
    })
  }
}

const updateSummary = () => {
  if (!transportTrends.value.length) {
    summary.value = {
      cagrLabel: '—',
      totalGrowthLabel: '—',
      trendType: ''
    }
    return
  }
  // 只针对当前公司（接口已按 companyId 过滤），取第一条记录
  const first = transportTrends.value[0]
  const cagrVal = first.cagr ?? null
  const t2022 = Number(first.transport2022 || 0)
  const t2025 = Number(first.transport2025 || 0)
  const growth = t2022 > 0 ? ((t2025 - t2022) / t2022) * 100 : 0

  summary.value.cagrLabel = cagrVal == null || isNaN(cagrVal)
    ? '—'
    : (Number(cagrVal) * 100).toFixed(1) + '%'
  summary.value.totalGrowthLabel = isNaN(growth) ? '—' : growth.toFixed(1) + '%'
  summary.value.trendType = first.trendType || '—'
}

const updateTransportTrendChart = () => {
  if (!transportTrendChart) return
  if (!transportTrends.value.length) {
    transportTrendChart.clear()
    return
  }
  const yearsAxis = ['2022', '2023', '2024', '2025']
  // 只显示当前筛选企业（接口已按 companyId 过滤，这里再取第一条做保护）
  const item = transportTrends.value[0]
  const series = [{
    name: item.companyName,
    type: 'line',
    data: [
      item.transport2022 || 0,
      item.transport2023 || 0,
      item.transport2024 || 0,
      item.transport2025 || 0
    ]
  }]
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { show: true, data: [item.companyName] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: yearsAxis },
    yAxis: { type: 'value', name: '运输量(吨)' },
    series
  }
  transportTrendChart.setOption(option)
}

const updateYoyBarChart = () => {
  if (!yoyBarChart) return
  if (!transportTrends.value.length) {
    yoyBarChart.clear()
    return
  }
  const item = transportTrends.value[0]
  const x = ['2023', '2024', '2025']
  const data = [
    (item.yoy2023 ?? 0) * 100,
    (item.yoy2024 ?? 0) * 100,
    (item.yoy2025 ?? 0) * 100
  ]
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: params => {
        const p = params[0]
        return `${p.name} 年同比：${p.value.toFixed(2)}%`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: x },
    yAxis: { type: 'value', name: '同比增长率(%)' },
    series: [
      {
        name: '同比增长率',
        type: 'bar',
        data,
        label: {
          show: true,
          position: 'top',
          formatter: p => `${p.value.toFixed(1)}%`
        }
      }
    ]
  }
  yoyBarChart.setOption(option)
}

const updateVehicleRankChart = () => {
  if (!vehicleRankChart) return
  const sorted = [...vehicleYearStats.value].sort(
    (a, b) => Number(b.yearTransportTon || 0) - Number(a.yearTransportTon || 0)
  ).slice(0, 20)
  const names = sorted.map(v => v.plateNumber)
  const values = sorted.map(v => v.yearTransportTon || 0)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '年运输量(吨)' },
    yAxis: { type: 'category', data: names, inverse: true },
    series: [
      {
        type: 'bar',
        data: values,
        label: {
          show: true,
          position: 'right'
        }
      }
    ]
  }
  vehicleRankChart.setOption(option)
}

const updateVehicleScatterChart = () => {
  if (!vehicleScatterChart) return
  const data = vehicleYearStats.value.map(v => ({
    name: v.plateNumber,
    value: [
      Number(v.yearKilometers || 0),
      Number(v.yearTransportTon || 0),
      Number(v.yearDeliveryCost || 0)
    ]
  }))
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: p => {
        const [km, ton, cost] = p.value
        return `${p.name}<br/>里程: ${km.toFixed(1)} km<br/>运输量: ${ton.toFixed(
          1
        )} 吨<br/>配送成本: ${cost.toFixed(1)} 元`
      }
    },
    xAxis: { type: 'value', name: '年里程(公里)' },
    yAxis: { type: 'value', name: '年运输量(吨)' },
    series: [
      {
        type: 'scatter',
        symbolSize: val => {
          const cost = val[2] || 0
          // 控制气泡大小范围
          return 10 + Math.min(40, cost / 1000)
        },
        data
      }
    ]
  }
  vehicleScatterChart.setOption(option)
}

// 装载率与运输量关系散点图
const updateLoadingScatterChart = () => {
  if (!loadingScatterChart) return
  if (!surveyData.value.length) {
    loadingScatterChart.clear()
    return
  }

  const rateMap = {
    '≤50%': 25,
    '51-70%': 60,
    '71-90%': 80,
    '91%以上': 95
  }

  let points = surveyData.value
    .filter(item => item.avgLoadingRate && item.annualTransport2025)
    .map(item => {
      const x = rateMap[item.avgLoadingRate] ?? null
      if (x == null) return null
      return {
        name: item.companyName,
        value: [x, Number(item.annualTransport2025 || 0)],
        rateLabel: item.avgLoadingRate
      }
    })
    .filter(Boolean)

  // 使用简单 IQR 过滤极端值（按运输量维度）
  if (points.length >= 4) {
    const ys = points.map(p => p.value[1]).sort((a, b) => a - b)
    const q1 = ys[Math.floor((ys.length - 1) * 0.25)]
    const q3 = ys[Math.floor((ys.length - 1) * 0.75)]
    const iqr = q3 - q1
    if (iqr > 0) {
      const lower = q1 - 1.5 * iqr
      const upper = q3 + 1.5 * iqr
      points = points.filter(p => p.value[1] >= lower && p.value[1] <= upper)
    }
  }

  if (!points.length) {
    loadingScatterChart.clear()
    return
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: p => {
        const [rate, ton] = p.value
        return `${p.name}<br/>装载率: ${p.data.rateLabel}<br/>2025年运输量: ${ton.toFixed(1)} 吨`
      }
    },
    xAxis: {
      type: 'value',
      name: '平均装载率(%)',
      min: 0,
      max: 100
    },
    yAxis: {
      type: 'value',
      name: '2025年运输量(吨)'
    },
    series: [
      {
        type: 'scatter',
        data: points
      }
    ]
  }
  loadingScatterChart.setOption(option)
}

// 车辆月度趋势多线图
const updateMonthlyTrendChart = () => {
  if (!monthlyTrendChart) return

  const year = filters.value.year
  const list = year
    ? transportMonthlyStats.value.filter(item => item.statYear === year)
    : transportMonthlyStats.value

  if (!list.length) {
    monthlyTrendChart.clear()
    return
  }

  // 汇总每辆车每月运输量
  const vehicleMonthMap = new Map() // vehicleId -> { plateNumber, months: {1: ton, ...} }
  list.forEach(item => {
    const id = item.vehicleId
    const month = item.statMonth
    const ton = Number(item.monthProductTon || 0)
    if (!vehicleMonthMap.has(id)) {
      vehicleMonthMap.set(id, {
        plateNumber: item.plateNumber || `车辆${id}`,
        months: {}
      })
    }
    const v = vehicleMonthMap.get(id)
    v.months[month] = (v.months[month] || 0) + ton
  })

  let vehiclesArr = Array.from(vehicleMonthMap.entries()).map(([id, v]) => {
    const yearTotal = Object.values(v.months).reduce((a, b) => a + b, 0)
    return { vehicleId: id, plateNumber: v.plateNumber, months: v.months, yearTotal }
  })

  // 如果选中了车辆，则只展示选中车辆；否则展示前 5 名
  if (selectedVehicleIds.value && selectedVehicleIds.value.length) {
    vehiclesArr = vehiclesArr.filter(v => selectedVehicleIds.value.includes(v.vehicleId))
  } else {
    vehiclesArr = vehiclesArr.sort((a, b) => b.yearTotal - a.yearTotal).slice(0, 5)
  }

  const months = Array.from({ length: 12 }, (_, i) => i + 1)
  const series = vehiclesArr.map(v => ({
    name: v.plateNumber,
    type: 'line',
    data: months.map(m => Number((v.months[m] || 0).toFixed(2)))
  }))

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      type: 'scroll'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months.map(m => `${m}月`)
    },
    yAxis: {
      type: 'value',
      name: '月运输量(吨)'
    },
    series
  }
  monthlyTrendChart.setOption(option)
}

// 通过车牌名进行下钻选择
const handleVehicleClickByName = (plateNumber) => {
  const target = vehicleYearStats.value.find(v => v.plateNumber === plateNumber)
  if (!target) return
  selectedVehicleIds.value = [target.vehicleId]
  updateMonthlyTrendChart()
}

// 导出数据 CSV（车辆年度统计）
const exportCsv = () => {
  if (!vehicleYearStats.value.length) {
    ElMessage.warning('暂无可导出的车辆年度统计数据')
    return
  }
  const headers = [
    '车牌号',
    '企业名称',
    '车辆类型',
    '年运输量(吨)',
    '年里程(公里)',
    '年配送成本',
    '吨公里效率',
    '运输量占比',
    '里程占比',
    '效率排名'
  ]
  const rows = vehicleYearStats.value.map(v => [
    v.plateNumber || '',
    v.companyName || '',
    v.vehicleType || '',
    v.yearTransportTon || 0,
    v.yearKilometers || 0,
    v.yearDeliveryCost || 0,
    v.tonKilometerEfficiency || 0,
    v.transportShare != null ? (Number(v.transportShare) * 100).toFixed(2) + '%' : '',
    v.kilometerShare != null ? (Number(v.kilometerShare) * 100).toFixed(2) + '%' : '',
    v.efficiencyRank || ''
  ])
  const csv = [headers.join(','), ...rows.map(r => r.map(cell => `"${cell}"`).join(','))].join('\n')
  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `车辆年度统计_${Date.now()}.csv`
  link.click()
  ElMessage.success('数据导出成功')
}

// 导出主要图表为 PNG
const exportCharts = () => {
  if (!transportTrendChart && !vehicleScatterChart) {
    ElMessage.warning('暂无可导出的图表')
    return
  }
  const charts = [
    { inst: transportTrendChart, name: '企业运输量趋势' },
    { inst: vehicleScatterChart, name: '车辆效率散点图' }
  ]
  charts.forEach(c => {
    if (!c.inst) return
    const url = c.inst.getDataURL({
      type: 'png',
      pixelRatio: 2,
      backgroundColor: '#fff'
    })
    const a = document.createElement('a')
    a.href = url
    a.download = `${c.name}_${Date.now()}.png`
    a.click()
  })
  ElMessage.success('图表导出任务已触发（浏览器将下载 PNG 文件）')
}
</script>

<style scoped lang="scss">
.enterprise-analytics {
  .filter-card {
    margin-bottom: 10px;
  }

  .filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filter-header-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }

  .stat-card {
    .stat-value {
      font-size: 26px;
      font-weight: bold;
      margin-bottom: 4px;
    }
    .stat-label {
      color: #909399;
      font-size: 13px;
    }
  }
}
</style>


