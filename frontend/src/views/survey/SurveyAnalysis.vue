<template>
  <div class="survey-analysis">
    <!-- 筛选工具栏 -->
    <el-card style="margin-bottom: 20px;">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="调查年份">
          <el-select v-model="filters.year" placeholder="全部年份" clearable style="width: 150px;">
            <el-option
              v-for="year in years"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select 
            v-model="filters.companyId" 
            :placeholder="(currentRole === 'COMPANY' || currentRole === 'COMPANY_ADMIN' || currentRole === 'COMPANY_USER') ? '我的企业' : '全部企业'" 
            :clearable="currentRole !== 'COMPANY' && currentRole !== 'COMPANY_ADMIN' && currentRole !== 'COMPANY_USER'"
            :disabled="currentRole === 'COMPANY' || currentRole === 'COMPANY_ADMIN' || currentRole === 'COMPANY_USER'"
            filterable 
            style="width: 200px;"
          >
            <el-option
              v-for="company in companies"
              :key="company.id"
              :label="company.name"
              :value="company.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="提交状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 150px;">
            <el-option label="草稿" value="草稿" />
            <el-option label="已提交" value="已提交" />
            <el-option label="已审核" value="已审核" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
          <el-button @click="showConfigDialog = true">图表配置</el-button>
          <el-button @click="exportData">导出数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">问卷总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon size="30"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.submittedCount }}</div>
              <div class="stat-label">已提交</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon size="30"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.avgTransport2024 || 0 }}</div>
              <div class="stat-label">平均2024年运输量(吨)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.avgRevenue || 0 }}</div>
              <div class="stat-label">平均2025年营业收入(万元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 年度运输量趋势 -->
      <el-col :span="12" v-if="chartConfig.showTransportTrend">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>年度运输量趋势</span>
              <el-button text @click="editChartConfig('transportTrend')">配置</el-button>
            </div>
          </template>
          <div ref="transportTrendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 未来车辆计划分布 -->
      <el-col :span="12" v-if="chartConfig.showVehiclePlan">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>未来车辆计划分布</span>
              <el-button text @click="editChartConfig('vehiclePlan')">配置</el-button>
            </div>
          </template>
          <div ref="vehiclePlanChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 主要产品类型分布 -->
      <el-col :span="12" v-if="chartConfig.showMainProduct">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>主要产品类型分布</span>
              <el-button text @click="editChartConfig('mainProduct')">配置</el-button>
            </div>
          </template>
          <div ref="mainProductChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 次要产品类型分布 -->
      <el-col :span="12" v-if="chartConfig.showSecondaryProduct">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>次要产品类型分布</span>
              <el-button text @click="editChartConfig('secondaryProduct')">配置</el-button>
            </div>
          </template>
          <div ref="secondaryProductChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 配送客户类型分布 -->
      <el-col :span="12" v-if="chartConfig.showDeliveryCustomer">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>配送客户类型分布</span>
              <el-button text @click="editChartConfig('deliveryCustomer')">配置</el-button>
            </div>
          </template>
          <div ref="deliveryCustomerChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 标准化载具分布 -->
      <el-col :span="12" v-if="chartConfig.showStandardEquipment">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>标准化载具分布</span>
              <el-button text @click="editChartConfig('standardEquipment')">配置</el-button>
            </div>
          </template>
          <div ref="standardEquipmentChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- GPS系统类型分布 -->
      <el-col :span="12" v-if="chartConfig.showGpsSystem">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>GPS系统类型分布</span>
              <el-button text @click="editChartConfig('gpsSystem')">配置</el-button>
            </div>
          </template>
          <div ref="gpsSystemChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- GPS平台分布 -->
      <el-col :span="12" v-if="chartConfig.showGpsPlatform">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>GPS平台分布</span>
              <el-button text @click="editChartConfig('gpsPlatform')">配置</el-button>
            </div>
          </template>
          <div ref="gpsPlatformChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 菜篮子车出车次数对比 -->
      <el-col :span="12" v-if="chartConfig.showBasketTripComparison">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>菜篮子车出车次数对比</span>
              <el-button text @click="editChartConfig('basketTripComparison')">配置</el-button>
            </div>
          </template>
          <div ref="basketTripComparisonChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 平均装载率分布 -->
      <el-col :span="12" v-if="chartConfig.showAvgLoadingRate">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>平均装载率分布</span>
              <el-button text @click="editChartConfig('avgLoadingRate')">配置</el-button>
            </div>
          </template>
          <div ref="avgLoadingRateChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 菜篮子车重要性因素 -->
      <el-col :span="12" v-if="chartConfig.showBasketVehicleImportance">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>菜篮子车重要性因素</span>
              <el-button text @click="editChartConfig('basketVehicleImportance')">配置</el-button>
            </div>
          </template>
          <div ref="basketVehicleImportanceChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 运营问题重要性排序 -->
      <el-col :span="12" v-if="chartConfig.showOperationalProblems">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>运营问题重要性排序（Top 10）</span>
              <el-button text @click="editChartConfig('operationalProblems')">配置</el-button>
            </div>
          </template>
          <div ref="operationalProblemsChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 营业收入分布 -->
      <el-col :span="12" v-if="chartConfig.showRevenue">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>2025年营业收入分布</span>
              <div style="display: flex; align-items: center; gap: 10px;">
                <el-checkbox v-model="revenueRange.enabled" @change="updateAllCharts(tableData)">区间过滤</el-checkbox>
                <el-input-number 
                  v-model="revenueRange.min" 
                  :min="0" 
                  :precision="0"
                  :disabled="!revenueRange.enabled"
                  placeholder="最小值"
                  style="width: 120px;"
                  @change="updateAllCharts(tableData)"
                />
                <span v-if="revenueRange.enabled">至</span>
                <el-input-number 
                  v-model="revenueRange.max" 
                  :min="0" 
                  :precision="0"
                  :disabled="!revenueRange.enabled"
                  placeholder="最大值"
                  style="width: 120px;"
                  @change="updateAllCharts(tableData)"
                />
                <el-button text @click="editChartConfig('revenue')">配置</el-button>
              </div>
            </div>
          </template>
          <div ref="revenueChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 冷藏车相关信息 -->
      <el-col :span="12" v-if="chartConfig.showColdVehicle">
        <el-card>
          <template #header>
            <div class="chart-header">
              <span>冷藏车相关信息</span>
              <el-button text @click="editChartConfig('coldVehicle')">配置</el-button>
            </div>
          </template>
          <div ref="coldVehicleChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>详细数据</span>
      </template>
      <el-table :data="tableData" stripe v-loading="loading" border style="width: 100%">
        <el-table-column prop="companyName" label="企业名称" min-width="150" fixed="left" />
        <el-table-column prop="surveyYear" label="调查年份" width="100" align="center" />
        <el-table-column prop="submitStatus" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.submitStatus)">{{ row.submitStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="annualTransport2022" label="2022年运输量(吨)" width="140" align="right" />
        <el-table-column prop="annualTransport2023" label="2023年运输量(吨)" width="140" align="right" />
        <el-table-column prop="annualTransport2024" label="2024年运输量(吨)" width="140" align="right" />
        <el-table-column prop="annualTransport2025" label="2025年运输量(吨)" width="140" align="right" />
        <el-table-column prop="futureVehiclePlan" label="未来车辆计划" width="120" />
        <el-table-column prop="revenue2025" label="2025年营业收入(万元)" width="160" align="right" />
        <el-table-column prop="basketTripComparison" label="出车次数对比" width="120" />
        <el-table-column prop="mainProductType" label="主要产品类型" width="140" />
        <el-table-column prop="secondaryProductType" label="次要产品类型" width="140" />
        <el-table-column prop="gpsSystemType" label="GPS系统类型" width="180" />
        <el-table-column prop="hasColdBasketVehicle" label="有冷藏车" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hasColdBasketVehicle ? 'success' : 'info'">
              {{ row.hasColdBasketVehicle ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="avgLoadingRate" label="平均装载率" width="120" />
        <el-table-column prop="basketVehicleImportance" label="重要性因素" width="200" />
        <el-table-column prop="deliveryCustomerTypes" label="配送客户类型" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="type in row.deliveryCustomerTypes" :key="type" size="small" style="margin-right: 5px;">
              {{ type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="standardEquipmentTypes" label="标准化载具" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="type in row.standardEquipmentTypes" :key="type" size="small" style="margin-right: 5px;">
              {{ type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gpsPlatformTypes" label="GPS平台" min-width="250">
          <template #default="{ row }">
            <el-tag v-for="type in row.gpsPlatformTypes" :key="type" size="small" style="margin-right: 5px; margin-bottom: 5px;">
              {{ type }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 图表配置对话框 -->
    <el-dialog v-model="showConfigDialog" title="图表配置" width="600px">
      <el-form label-width="150px">
        <el-form-item label="显示图表">
          <el-checkbox-group v-model="chartConfigList">
            <el-checkbox label="showTransportTrend">年度运输量趋势</el-checkbox>
            <el-checkbox label="showVehiclePlan">未来车辆计划分布</el-checkbox>
            <el-checkbox label="showMainProduct">主要产品类型分布</el-checkbox>
            <el-checkbox label="showSecondaryProduct">次要产品类型分布</el-checkbox>
            <el-checkbox label="showDeliveryCustomer">配送客户类型分布</el-checkbox>
            <el-checkbox label="showStandardEquipment">标准化载具分布</el-checkbox>
            <el-checkbox label="showGpsSystem">GPS系统类型分布</el-checkbox>
            <el-checkbox label="showGpsPlatform">GPS平台分布</el-checkbox>
            <el-checkbox label="showBasketTripComparison">菜篮子车出车次数对比</el-checkbox>
            <el-checkbox label="showAvgLoadingRate">平均装载率分布</el-checkbox>
            <el-checkbox label="showBasketVehicleImportance">菜篮子车重要性因素</el-checkbox>
            <el-checkbox label="showOperationalProblems">运营问题重要性排序</el-checkbox>
            <el-checkbox label="showRevenue">营业收入分布</el-checkbox>
            <el-checkbox label="showColdVehicle">冷藏车相关信息</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showConfigDialog = false">取消</el-button>
        <el-button type="primary" @click="saveChartConfig">保存</el-button>
      </template>
    </el-dialog>

    <!-- 单个图表配置对话框 -->
    <el-dialog v-model="showSingleConfigDialog" :title="currentChartConfig.title" width="500px">
      <el-form :model="currentChartConfig" label-width="120px">
        <el-form-item label="图表类型">
          <el-select v-model="currentChartConfig.type" style="width: 100%;">
            <el-option label="柱状图" value="bar" />
            <el-option label="折线图" value="line" />
            <el-option label="饼图" value="pie" />
            <el-option label="环形图" value="doughnut" />
          </el-select>
        </el-form-item>
        <el-form-item label="图表高度">
          <el-input-number v-model="currentChartConfig.height" :min="200" :max="600" :step="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSingleConfigDialog = false">取消</el-button>
        <el-button type="primary" @click="applyChartConfig">应用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, DataAnalysis, TrendCharts, Money } from '@element-plus/icons-vue'
import { surveyApi, companyApi } from '@/api'
import * as echarts from 'echarts'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const years = ref([])
for (let i = 2020; i <= new Date().getFullYear() + 1; i++) {
  years.value.push(i)
}

const filters = ref({
  year: null,
  companyId: null,
  status: null
})

const stats = ref({
  totalCount: 0,
  submittedCount: 0,
  avgTransport2024: 0,
  avgRevenue: 0
})

// 图表配置
const chartConfig = ref({
  showTransportTrend: true,
  showVehiclePlan: true,
  showMainProduct: true,
  showSecondaryProduct: true,
  showDeliveryCustomer: true,
  showStandardEquipment: true,
  showGpsSystem: true,
  showGpsPlatform: true,
  showBasketTripComparison: true,
  showAvgLoadingRate: true,
  showBasketVehicleImportance: true,
  showOperationalProblems: true,
  showRevenue: true,
  showColdVehicle: true
})

const showConfigDialog = ref(false)
const showSingleConfigDialog = ref(false)
const currentChartConfig = ref({})

// 营业收入区间过滤配置
const revenueRange = ref({
  enabled: false,
  min: null,
  max: null
})

// 图表配置列表（用于checkbox-group）
const chartConfigList = ref([])

const updateChartConfigList = () => {
  chartConfigList.value = Object.keys(chartConfig.value).filter(key => chartConfig.value[key])
}

// 图表引用
const transportTrendChart = ref(null)
const vehiclePlanChart = ref(null)
const mainProductChart = ref(null)
const secondaryProductChart = ref(null)
const deliveryCustomerChart = ref(null)
const standardEquipmentChart = ref(null)
const gpsSystemChart = ref(null)
const gpsPlatformChart = ref(null)
const basketTripComparisonChart = ref(null)
const avgLoadingRateChart = ref(null)
const basketVehicleImportanceChart = ref(null)
const operationalProblemsChart = ref(null)
const revenueChart = ref(null)
const coldVehicleChart = ref(null)

// 图表实例
let chartInstances = {}

onMounted(() => {
  loadCompanies()
  loadData()
  nextTick(() => {
    initCharts()
  })
  // 从本地存储加载图表配置
  const savedConfig = localStorage.getItem('surveyChartConfig')
  if (savedConfig) {
    try {
      chartConfig.value = { ...chartConfig.value, ...JSON.parse(savedConfig) }
      updateChartConfigList()
    } catch (e) {
      console.error('加载图表配置失败', e)
    }
  } else {
    updateChartConfigList()
  }
})

const currentRole = ref(localStorage.getItem('role') || '')
// 修复：使用普通 ref 而不是函数式 ref
const getCurrentCompanyId = () => {
  const id = localStorage.getItem('companyId')
  return id ? parseInt(id) : null
}
const currentCompanyId = ref(getCurrentCompanyId())

const loadCompanies = async () => {
  try {
    const data = await companyApi.getAll()
    let allCompanies = Array.isArray(data) ? data : (data?.data || [])
    
    // 企业用户（包括企业管理员和普通用户）只能看到自己企业
    if (currentRole.value === 'COMPANY' || currentRole.value === 'COMPANY_ADMIN' || currentRole.value === 'COMPANY_USER') {
      // 重新获取 companyId（可能登录后更新了）
      const companyId = getCurrentCompanyId()
      currentCompanyId.value = companyId
      
      if (companyId) {
        // 确保企业列表包含当前用户的企业
        allCompanies = allCompanies.filter(c => {
          const cid = typeof c.id === 'string' ? parseInt(c.id) : c.id
          return cid === companyId
        })
        // 自动设置筛选条件为企业自己的ID
        filters.value.companyId = companyId
        console.log('企业管理员 - 自动设置企业筛选:', companyId, '企业列表:', allCompanies)
      } else {
        console.warn('企业管理员 - 未找到 companyId')
        allCompanies = []
      }
    }
    
    companies.value = allCompanies
    console.log('加载的企业列表:', companies.value)
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    let data = []
    if (filters.value.year) {
      data = await surveyApi.getByYear(filters.value.year)
    } else {
      data = await surveyApi.getAll()
    }
    
    // 企业用户（包括企业管理员和普通用户）只能看到自己企业的问卷
    // 重新获取 companyId（可能登录后更新了）
    const companyId = getCurrentCompanyId()
    currentCompanyId.value = companyId
    
    if (currentRole.value === 'COMPANY' || currentRole.value === 'COMPANY_ADMIN' || currentRole.value === 'COMPANY_USER') {
      if (companyId) {
        console.log('企业管理员 - 筛选问卷数据，companyId:', companyId, '原始数据量:', data.length)
        data = data.filter(item => {
          const itemCompanyId = typeof item.companyId === 'string' ? parseInt(item.companyId) : item.companyId
          return itemCompanyId === companyId
        })
        console.log('企业管理员 - 筛选后数据量:', data.length)
      } else {
        console.warn('企业管理员 - 未找到 companyId，返回空数据')
        data = []
      }
    }
    
    // 应用筛选
    if (filters.value.companyId) {
      const filterCompanyId = typeof filters.value.companyId === 'string' ? parseInt(filters.value.companyId) : filters.value.companyId
      data = data.filter(item => {
        const itemCompanyId = typeof item.companyId === 'string' ? parseInt(item.companyId) : item.companyId
        return itemCompanyId === filterCompanyId
      })
    }
    if (filters.value.status) {
      data = data.filter(item => item.submitStatus === filters.value.status)
    }
    
    tableData.value = data || []
    
    // 计算统计数据
    calculateStats(data)
    
    // 更新图表
    nextTick(() => {
      updateAllCharts(data)
    })
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const calculateStats = (data) => {
  stats.value.totalCount = data.length
  stats.value.submittedCount = data.filter(item => item.submitStatus === '已提交' || item.submitStatus === '已审核').length
  
  const transport2024 = data.filter(item => item.annualTransport2024).map(item => item.annualTransport2024)
  stats.value.avgTransport2024 = transport2024.length > 0 
    ? (transport2024.reduce((a, b) => a + b, 0) / transport2024.length).toFixed(2)
    : 0
    
  const revenue = data.filter(item => item.revenue2025).map(item => item.revenue2025)
  stats.value.avgRevenue = revenue.length > 0
    ? (revenue.reduce((a, b) => a + b, 0) / revenue.length).toFixed(2)
    : 0
}

const resetFilters = () => {
  // 如果是企业管理员，重置时保留企业筛选
  const companyId = getCurrentCompanyId()
  if (currentRole.value === 'COMPANY' || currentRole.value === 'COMPANY_ADMIN' || currentRole.value === 'COMPANY_USER') {
    filters.value = {
      year: null,
      companyId: companyId,  // 保留企业筛选
      status: null
    }
  } else {
    filters.value = {
      year: null,
      companyId: null,
      status: null
    }
  }
  loadData()
}

const initCharts = () => {
  const chartRefs = {
    transportTrend: transportTrendChart,
    vehiclePlan: vehiclePlanChart,
    mainProduct: mainProductChart,
    secondaryProduct: secondaryProductChart,
    deliveryCustomer: deliveryCustomerChart,
    standardEquipment: standardEquipmentChart,
    gpsSystem: gpsSystemChart,
    gpsPlatform: gpsPlatformChart,
    basketTripComparison: basketTripComparisonChart,
    avgLoadingRate: avgLoadingRateChart,
    basketVehicleImportance: basketVehicleImportanceChart,
    operationalProblems: operationalProblemsChart,
    revenue: revenueChart,
    coldVehicle: coldVehicleChart
  }
  
  Object.keys(chartRefs).forEach(key => {
    if (chartRefs[key].value) {
      chartInstances[key] = echarts.init(chartRefs[key].value)
    }
  })
}

const updateAllCharts = (data) => {
  updateTransportTrendChart(data)
  updateVehiclePlanChart(data)
  updateMainProductChart(data)
  updateSecondaryProductChart(data)
  updateDeliveryCustomerChart(data)
  updateStandardEquipmentChart(data)
  updateGpsSystemChart(data)
  updateGpsPlatformChart(data)
  updateBasketTripComparisonChart(data)
  updateAvgLoadingRateChart(data)
  updateBasketVehicleImportanceChart(data)
  updateOperationalProblemsChart(data)
  updateRevenueChart(data)
  updateColdVehicleChart(data)
}

// 年度运输量趋势
const updateTransportTrendChart = (data) => {
  if (!chartInstances.transportTrend || !chartConfig.value.showTransportTrend) return
  
  const years = ['2022', '2023', '2024', '2025']
  const series = []
  
  data.forEach(item => {
    series.push({
      name: item.companyName,
      type: 'line',
      data: [
        item.annualTransport2022 || 0,
        item.annualTransport2023 || 0,
        item.annualTransport2024 || 0,
        item.annualTransport2025 || 0
      ]
    })
  })
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: data.map(item => item.companyName), type: 'scroll' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: years },
    yAxis: { type: 'value', name: '运输量(吨)' },
    series: series.slice(0, 10) // 最多显示10条线
  }
  chartInstances.transportTrend.setOption(option)
}

// 未来车辆计划分布
const updateVehiclePlanChart = (data) => {
  if (!chartInstances.vehiclePlan || !chartConfig.value.showVehiclePlan) return
  
  const planStats = {}
  data.forEach(item => {
    if (item.futureVehiclePlan) {
      planStats[item.futureVehiclePlan] = (planStats[item.futureVehiclePlan] || 0) + 1
    }
  })
  
  const total = Object.values(planStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '未来车辆计划',
      type: 'pie',
      radius: '50%',
      data: Object.keys(planStats).map(key => ({
        value: planStats[key],
        name: key
      })),
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  }
  chartInstances.vehiclePlan.setOption(option)
}

// 主要产品类型分布
const updateMainProductChart = (data) => {
  if (!chartInstances.mainProduct || !chartConfig.value.showMainProduct) return
  
  const productStats = {}
  data.forEach(item => {
    if (item.mainProductType) {
      const type = item.mainProductType.replace(/^[A-H]/, '').replace('类', '')
      productStats[type] = (productStats[type] || 0) + 1
    }
  })
  
  const total = Object.values(productStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '主要产品类型',
      type: 'pie',
      radius: ['40%', '70%'],
      data: Object.keys(productStats).map(key => ({
        value: productStats[key],
        name: key
      })),
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  }
  chartInstances.mainProduct.setOption(option)
}

// 次要产品类型分布
const updateSecondaryProductChart = (data) => {
  if (!chartInstances.secondaryProduct || !chartConfig.value.showSecondaryProduct) return
  
  const productStats = {}
  data.forEach(item => {
    if (item.secondaryProductType) {
      const type = item.secondaryProductType.replace(/^[A-H]/, '').replace('类', '')
      productStats[type] = (productStats[type] || 0) + 1
    }
  })
  
  const total = Object.values(productStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '次要产品类型',
      type: 'pie',
      radius: ['40%', '70%'],
      data: Object.keys(productStats).map(key => ({
        value: productStats[key],
        name: key
      })),
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  }
  chartInstances.secondaryProduct.setOption(option)
}

// 配送客户类型分布
const updateDeliveryCustomerChart = (data) => {
  if (!chartInstances.deliveryCustomer || !chartConfig.value.showDeliveryCustomer) return
  
  const customerStats = {}
  data.forEach(item => {
    if (item.deliveryCustomerTypes && Array.isArray(item.deliveryCustomerTypes)) {
      item.deliveryCustomerTypes.forEach(type => {
        customerStats[type] = (customerStats[type] || 0) + 1
      })
    }
  })
  
  const total = Object.values(customerStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(customerStats),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: Object.values(customerStats),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.deliveryCustomer.setOption(option)
}

// 标准化载具分布
const updateStandardEquipmentChart = (data) => {
  if (!chartInstances.standardEquipment || !chartConfig.value.showStandardEquipment) return
  
  const equipmentStats = {}
  data.forEach(item => {
    if (item.standardEquipmentTypes && Array.isArray(item.standardEquipmentTypes)) {
      item.standardEquipmentTypes.forEach(type => {
        equipmentStats[type] = (equipmentStats[type] || 0) + 1
      })
    }
  })
  
  const total = Object.values(equipmentStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(equipmentStats),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: Object.values(equipmentStats),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.standardEquipment.setOption(option)
}

// GPS系统类型分布
const updateGpsSystemChart = (data) => {
  if (!chartInstances.gpsSystem || !chartConfig.value.showGpsSystem) return
  
  const gpsStats = {}
  data.forEach(item => {
    if (item.gpsSystemType) {
      const type = item.gpsSystemType.length > 20 
        ? item.gpsSystemType.substring(0, 20) + '...'
        : item.gpsSystemType
      gpsStats[type] = (gpsStats[type] || 0) + 1
    }
  })
  
  const total = Object.values(gpsStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: 'GPS系统类型',
      type: 'pie',
      radius: '50%',
      data: Object.keys(gpsStats).map(key => ({
        value: gpsStats[key],
        name: key
      })),
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  }
  chartInstances.gpsSystem.setOption(option)
}

// GPS平台分布
const updateGpsPlatformChart = (data) => {
  if (!chartInstances.gpsPlatform || !chartConfig.value.showGpsPlatform) return
  
  const platformStats = {}
  data.forEach(item => {
    if (item.gpsPlatformTypes && Array.isArray(item.gpsPlatformTypes)) {
      item.gpsPlatformTypes.forEach(type => {
        const shortType = type.length > 15 ? type.substring(0, 15) + '...' : type
        platformStats[shortType] = (platformStats[shortType] || 0) + 1
      })
    }
  })
  
  const total = Object.values(platformStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(platformStats),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: Object.values(platformStats),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.gpsPlatform.setOption(option)
}

// 菜篮子车出车次数对比
const updateBasketTripComparisonChart = (data) => {
  if (!chartInstances.basketTripComparison || !chartConfig.value.showBasketTripComparison) return
  
  const comparisonStats = {}
  data.forEach(item => {
    if (item.basketTripComparison) {
      comparisonStats[item.basketTripComparison] = (comparisonStats[item.basketTripComparison] || 0) + 1
    }
  })
  
  const total = Object.values(comparisonStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(comparisonStats)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: Object.values(comparisonStats),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.basketTripComparison.setOption(option)
}

// 平均装载率分布
const updateAvgLoadingRateChart = (data) => {
  if (!chartInstances.avgLoadingRate || !chartConfig.value.showAvgLoadingRate) return
  
  const loadingRateStats = {}
  data.forEach(item => {
    if (item.avgLoadingRate) {
      loadingRateStats[item.avgLoadingRate] = (loadingRateStats[item.avgLoadingRate] || 0) + 1
    }
  })
  
  const total = Object.values(loadingRateStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '平均装载率',
      type: 'pie',
      radius: '50%',
      data: Object.keys(loadingRateStats).map(key => ({
        value: loadingRateStats[key],
        name: key
      })),
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  }
  chartInstances.avgLoadingRate.setOption(option)
}

// 菜篮子车重要性因素
const updateBasketVehicleImportanceChart = (data) => {
  if (!chartInstances.basketVehicleImportance || !chartConfig.value.showBasketVehicleImportance) return
  
  const importanceStats = {}
  data.forEach(item => {
    if (item.basketVehicleImportance) {
      const importance = item.basketVehicleImportance.length > 15
        ? item.basketVehicleImportance.substring(0, 15) + '...'
        : item.basketVehicleImportance
      importanceStats[importance] = (importanceStats[importance] || 0) + 1
    }
  })
  
  const total = Object.values(importanceStats).reduce((sum, val) => sum + val, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(importanceStats),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: Object.values(importanceStats),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.basketVehicleImportance.setOption(option)
}

// 运营问题重要性排序
const updateOperationalProblemsChart = (data) => {
  if (!chartInstances.operationalProblems || !chartConfig.value.showOperationalProblems) return
  
  const problemScores = {}
  data.forEach(item => {
    if (item.operationalProblems && Array.isArray(item.operationalProblems)) {
      item.operationalProblems.forEach((problem, index) => {
        const problemType = problem.problemType
        if (!problemScores[problemType]) {
          problemScores[problemType] = { count: 0, totalScore: 0 }
        }
        problemScores[problemType].count++
        problemScores[problemType].totalScore += (item.operationalProblems.length - index)
      })
    }
  })
  
  const problemList = Object.keys(problemScores).map(key => ({
    name: key.length > 20 ? key.substring(0, 20) + '...' : key,
    value: problemScores[key].totalScore / problemScores[key].count
  })).sort((a, b) => b.value - a.value).slice(0, 10)
  
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '平均重要性得分'
    },
    yAxis: {
      type: 'category',
      data: problemList.map(p => p.name),
      axisLabel: { interval: 0 }
    },
    series: [{
      name: '重要性得分',
      type: 'bar',
      data: problemList.map(p => p.value.toFixed(2))
    }]
  }
  chartInstances.operationalProblems.setOption(option)
}

// 使用 IQR 方法排除极端值
const removeOutliers = (values) => {
  if (!values || values.length < 4) {
    // 数据太少时不做极值处理，避免全部被过滤
    return values
  }
  const sorted = [...values].sort((a, b) => a - b)
  const q1Index = Math.floor((sorted.length - 1) * 0.25)
  const q3Index = Math.floor((sorted.length - 1) * 0.75)
  const q1 = sorted[q1Index]
  const q3 = sorted[q3Index]
  const iqr = q3 - q1
  if (iqr === 0) {
    // 所有值几乎一样时不排除
    return values
  }
  const lowerBound = q1 - 1.5 * iqr
  const upperBound = q3 + 1.5 * iqr
  return values.filter(v => v >= lowerBound && v <= upperBound)
}

// 营业收入分布
const updateRevenueChart = (data) => {
  if (!chartInstances.revenue || !chartConfig.value.showRevenue) return
  
  let revenues = data.filter(item => item.revenue2025).map(item => item.revenue2025)
  if (revenues.length === 0) return
  
  // 如果启用区间过滤，使用配置的最小值和最大值过滤
  if (revenueRange.value.enabled) {
    const min = revenueRange.value.min !== null ? revenueRange.value.min : -Infinity
    const max = revenueRange.value.max !== null ? revenueRange.value.max : Infinity
    revenues = revenues.filter(v => v >= min && v <= max)
  }
  
  if (revenues.length === 0) return
  
  const min = Math.min(...revenues)
  const max = Math.max(...revenues)
  
  // 处理所有值相同的情况
  if (min === max) {
    const ranges = [{
      label: `${min.toFixed(0)}`,
      count: revenues.length
    }]
    const option = {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ranges.map(r => r.label)
      },
      yAxis: { type: 'value', name: '企业数量' },
      series: [{
        name: '企业数量',
        type: 'bar',
        data: ranges.map(r => r.count),
        itemStyle: { color: '#409EFF' }
      }]
    }
    chartInstances.revenue.setOption(option)
    return
  }
  
  const step = (max - min) / 5
  const ranges = []
  for (let i = 0; i < 5; i++) {
    ranges.push({
      label: `${(min + i * step).toFixed(0)}-${(min + (i + 1) * step).toFixed(0)}`,
      count: 0
    })
  }
  
  revenues.forEach(revenue => {
    // 计算索引，确保在有效范围内
    let index = Math.floor((revenue - min) / step)
    // 处理边界情况：当 revenue === max 时，index 可能是 5，需要限制为 4
    index = Math.min(Math.max(0, index), 4)
    // 确保 ranges[index] 存在
    if (ranges[index]) {
      ranges[index].count++
    } else {
      console.warn('收入分布计算错误 - revenue:', revenue, 'index:', index, 'ranges length:', ranges.length)
    }
  })
  
  const total = ranges.reduce((sum, r) => sum + r.count, 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const param = params[0]
        const percentage = total > 0 ? ((param.value / total) * 100).toFixed(2) : 0
        return `${param.name}<br/>${param.seriesName}: ${param.value} (${percentage}%)`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ranges.map(r => r.label)
    },
    yAxis: { type: 'value', name: '企业数量' },
    series: [{
      name: '企业数量',
      type: 'bar',
      data: ranges.map(r => r.count),
      label: {
        show: true,
        position: 'top',
        formatter: (params) => {
          const percentage = total > 0 ? ((params.value / total) * 100).toFixed(1) : 0
          return `${params.value} (${percentage}%)`
        }
      }
    }]
  }
  chartInstances.revenue.setOption(option)
}

// 冷藏车相关信息
const updateColdVehicleChart = (data) => {
  if (!chartInstances.coldVehicle || !chartConfig.value.showColdVehicle) return
  
  const hasCold = data.filter(item => item.hasColdBasketVehicle).length
  const noCold = data.length - hasCold
  
  const multiTemp = data.filter(item => item.hasColdBasketVehicle && item.coldVehicleMultiTemp === true).length
  const noMultiTemp = hasCold - multiTemp
  
  const total1 = hasCold + noCold
  const total2 = multiTemp + noMultiTemp
  
  const option = {
    tooltip: { 
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '是否有冷藏车',
        type: 'pie',
        radius: ['0%', '40%'],
        data: [
          { value: hasCold, name: '有冷藏车' },
          { value: noCold, name: '无冷藏车' }
        ],
        label: {
          formatter: '{b}: {c} ({d}%)'
        }
      },
      {
        name: '多温区设计',
        type: 'pie',
        radius: ['50%', '70%'],
        data: [
          { value: multiTemp, name: '有多温区' },
          { value: noMultiTemp, name: '无多温区' }
        ],
        label: {
          formatter: '{b}: {c} ({d}%)'
        }
      }
    ]
  }
  chartInstances.coldVehicle.setOption(option)
}

const editChartConfig = (chartKey) => {
  currentChartConfig.value = {
    key: chartKey,
    title: getChartTitle(chartKey),
    type: 'bar',
    height: 350
  }
  showSingleConfigDialog.value = true
}

const getChartTitle = (key) => {
  const titles = {
    transportTrend: '年度运输量趋势',
    vehiclePlan: '未来车辆计划分布',
    mainProduct: '主要产品类型分布',
    secondaryProduct: '次要产品类型分布',
    deliveryCustomer: '配送客户类型分布',
    standardEquipment: '标准化载具分布',
    gpsSystem: 'GPS系统类型分布',
    gpsPlatform: 'GPS平台分布',
    basketTripComparison: '菜篮子车出车次数对比',
    avgLoadingRate: '平均装载率分布',
    basketVehicleImportance: '菜篮子车重要性因素',
    operationalProblems: '运营问题重要性排序',
    revenue: '营业收入分布',
    coldVehicle: '冷藏车相关信息'
  }
  return titles[key] || key
}

const applyChartConfig = () => {
  // 这里可以实现图表类型的切换
  showSingleConfigDialog.value = false
  ElMessage.success('配置已应用')
}

const saveChartConfig = () => {
  // 根据选中的列表更新配置
  Object.keys(chartConfig.value).forEach(key => {
    chartConfig.value[key] = chartConfigList.value.includes(key)
  })
  localStorage.setItem('surveyChartConfig', JSON.stringify(chartConfig.value))
  showConfigDialog.value = false
  ElMessage.success('配置已保存')
  // 重新渲染图表
  nextTick(() => {
    updateAllCharts(tableData.value)
  })
}

// 打开配置对话框时更新列表
watch(showConfigDialog, (val) => {
  if (val) {
    updateChartConfigList()
  }
})

const exportData = () => {
  // 导出CSV
  const headers = ['企业名称', '调查年份', '状态', '2022年运输量(吨)', '2023年运输量(吨)', '2024年运输量(吨)', '2025年运输量(吨)', '未来车辆计划', '2025年营业收入(万元)', '出车次数对比', '主要产品类型', '次要产品类型', 'GPS系统类型', '有冷藏车', '平均装载率', '重要性因素', '配送客户类型', '标准化载具', 'GPS平台']
  const rows = tableData.value.map(item => [
    item.companyName,
    item.surveyYear,
    item.submitStatus,
    item.annualTransport2022 || '',
    item.annualTransport2023 || '',
    item.annualTransport2024 || '',
    item.annualTransport2025 || '',
    item.futureVehiclePlan || '',
    item.revenue2025 || '',
    item.basketTripComparison || '',
    item.mainProductType || '',
    item.secondaryProductType || '',
    item.gpsSystemType || '',
    item.hasColdBasketVehicle ? '是' : '否',
    item.avgLoadingRate || '',
    item.basketVehicleImportance || '',
    (item.deliveryCustomerTypes || []).join(';'),
    (item.standardEquipmentTypes || []).join(';'),
    (item.gpsPlatformTypes || []).join(';')
  ])
  
  const csv = [headers.join(','), ...rows.map(row => row.map(cell => `"${cell}"`).join(','))].join('\n')
  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `问卷调查数据_${new Date().getTime()}.csv`
  link.click()
  ElMessage.success('数据导出成功')
}

const getStatusType = (status) => {
  const statusMap = {
    '草稿': 'info',
    '已提交': 'warning',
    '已审核': 'success',
    '已驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

// 监听图表配置变化，重新渲染图表
watch(() => chartConfig.value, () => {
  nextTick(() => {
    updateAllCharts(tableData.value)
  })
}, { deep: true })
</script>

<style scoped lang="scss">
.survey-analysis {
  .filter-form {
    margin-bottom: 0;
  }

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

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    > div {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }
}
</style>

