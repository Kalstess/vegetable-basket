<template>
  <div class="transport-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运输统计</span>
          <div>
            <el-button @click="handleExportTemplate">
              <el-icon><Download /></el-icon>
              下载模板
            </el-button>
            <el-button @click="handleImport">
              <el-icon><Upload /></el-icon>
              导入Excel
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增运输统计
            </el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchCompanyId" placeholder="选择企业" clearable style="width: 200px;">
          <el-option
            v-for="company in companies"
            :key="company.id"
            :label="company.name"
            :value="company.id"
          />
        </el-select>
        <el-select v-model="searchVehicleId" placeholder="选择车辆" clearable style="width: 200px;" :disabled="!searchCompanyId">
          <el-option
            v-for="vehicle in vehicles"
            :key="vehicle.id"
            :label="vehicle.plateNumber"
            :value="vehicle.id"
          />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-alert
          type="info"
          :closable="false"
          style="margin-left: 20px; flex: 1;"
        >
          <template #title>
            <span style="font-size: 12px;">提示：支持Excel批量导入，请先下载模板。产品类型用逗号分隔（如：蔬菜,肉类,水果）</span>
          </template>
        </el-alert>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px;"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="companyName" label="所属企业" min-width="150" />
        <el-table-column label="统计年月" width="120">
          <template #default="{ row }">
            {{ row.statYear }}年{{ row.statMonth }}月
          </template>
        </el-table-column>
        <!-- 日相关字段 -->
        <el-table-column prop="dailyDeliveryTimes" label="日均配送次数" width="120">
          <template #default="{ row }">
            {{ row.dailyDeliveryTimes || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="dailyDeliveryPoints" label="日配送网点数" width="140">
          <template #default="{ row }">
            {{ row.dailyDeliveryPoints !== null && row.dailyDeliveryPoints !== undefined ? Math.round(row.dailyDeliveryPoints) + ' 个/天' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="peakSeasonDailyIncreaseTimes" label="旺季增加次数" width="140">
          <template #default="{ row }">
            {{ row.peakSeasonDailyIncreaseTimes !== null && row.peakSeasonDailyIncreaseTimes !== undefined ? row.peakSeasonDailyIncreaseTimes + ' 次/天' : '-' }}
          </template>
        </el-table-column>
        <!-- 月相关字段 -->
        <el-table-column prop="monthProductTon" label="月产品吨数" width="120">
          <template #default="{ row }">
            {{ row.monthProductTon || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="monthKilometers" label="月公里数" width="120">
          <template #default="{ row }">
            {{ row.monthKilometers || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="monthDeliveryCost" label="月配送费用" width="130">
          <template #default="{ row }">
            {{ row.monthDeliveryCost ? row.monthDeliveryCost + ' 元/月' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="productTypes" label="产品类型" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="(type, index) in row.productTypes" :key="index" style="margin-right: 5px;">
              {{ type }}
            </el-tag>
            <span v-if="!row.productTypes || row.productTypes.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentRow ? '编辑运输统计' : '新增运输统计'"
      width="800px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="车辆" prop="vehicleId">
          <el-select v-model="formData.vehicleId" placeholder="请选择车辆" style="width: 100%;" @change="handleVehicleChange">
            <el-option
              v-for="vehicle in allVehicles"
              :key="vehicle.id"
              :label="`${vehicle.plateNumber} - ${vehicle.companyName}`"
              :value="vehicle.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="统计年份" prop="statYear">
          <el-input-number v-model="formData.statYear" :min="2020" :max="2099" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-input-number v-model="formData.statMonth" :min="1" :max="12" style="width: 100%;" />
        </el-form-item>
        
        <!-- 日相关字段 -->
        <el-divider content-position="left">日相关数据</el-divider>
        <el-form-item label="日均配送次数" prop="dailyDeliveryTimes">
          <el-input-number v-model="formData.dailyDeliveryTimes" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="日配送网点数" prop="dailyDeliveryPoints">
          <el-input-number 
            v-model="formData.dailyDeliveryPoints" 
            :min="0" 
            :precision="0"
            :step="1"
            style="width: calc(100% - 80px);" 
            placeholder="请输入网点数"
          />
          <span style="margin-left: 10px; color: #909399;">个/天</span>
        </el-form-item>
        <el-form-item label="旺季增加次数" prop="peakSeasonDailyIncreaseTimes">
          <el-input-number 
            v-model="formData.peakSeasonDailyIncreaseTimes" 
            :min="0" 
            :precision="0"
            :step="1"
            style="width: calc(100% - 80px);" 
            placeholder="请输入增加次数"
          />
          <span style="margin-left: 10px; color: #909399;">次/天</span>
        </el-form-item>
        
        <!-- 月相关字段 -->
        <el-divider content-position="left">月相关数据</el-divider>
        <el-form-item label="月产品吨数" prop="monthProductTon">
          <el-input-number v-model="formData.monthProductTon" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="月公里数" prop="monthKilometers">
          <el-input-number v-model="formData.monthKilometers" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="月配送费用" prop="monthDeliveryCost">
          <el-input-number 
            v-model="formData.monthDeliveryCost" 
            :min="0" 
            :precision="2" 
            style="width: calc(100% - 80px);" 
            placeholder="请输入费用"
          />
          <span style="margin-left: 10px; color: #909399;">元/月</span>
        </el-form-item>
        <el-form-item label="产品类型" prop="productTypes">
          <el-select
            v-model="formData.productTypes"
            multiple
            placeholder="请选择产品类型"
            style="width: 100%;"
            filterable
            @change="handleProductTypeChange"
          >
            <el-option
              v-for="type in productTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
          <el-input
            v-if="showOtherInput"
            v-model="otherProductName"
            placeholder="请输入其他产品名称"
            style="margin-top: 10px;"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Upload } from '@element-plus/icons-vue'
import { transportApi, companyApi, vehicleApi, productTypeApi } from '@/api'
import { exportToExcel, importFromExcel, createExcelTemplate } from '@/utils/excel'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const vehicles = ref([])
const allVehicles = ref([])
const searchCompanyId = ref(null)
const searchVehicleId = ref(null)
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)

const productTypes = ref([])
const otherProductName = ref('')
const showOtherInput = computed(() => {
  return formData.value.productTypes && formData.value.productTypes.includes('其他')
})

const formData = ref({
  vehicleId: null,
  statYear: new Date().getFullYear(),
  statMonth: new Date().getMonth() + 1,
  dailyDeliveryTimes: 0,
  monthProductTon: 0,
  monthKilometers: 0,
  monthDeliveryCost: null,
  dailyDeliveryPoints: null,
  peakSeasonDailyIncreaseTimes: null,
  productTypes: []
})

const rules = {
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  statYear: [{ required: true, message: '请输入统计年份', trigger: 'blur' }],
  statMonth: [{ required: true, message: '请输入统计月份', trigger: 'blur' }]
}

// 监听企业选择变化，加载对应车辆
watch(searchCompanyId, (val) => {
  if (val) {
    loadVehicles(val)
  } else {
    vehicles.value = []
    searchVehicleId.value = null
  }
})

onMounted(() => {
  loadCompanies()
  loadAllVehicles()
  loadProductTypes()
  loadData()
})

const loadCompanies = async () => {
  try {
    companies.value = await companyApi.getAll()
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadVehicles = async (companyId) => {
  try {
    vehicles.value = await vehicleApi.getByCompanyId(companyId)
  } catch (error) {
    console.error('加载车辆列表失败', error)
  }
}

const loadAllVehicles = async () => {
  try {
    allVehicles.value = await vehicleApi.getAll()
  } catch (error) {
    console.error('加载车辆列表失败', error)
  }
}

const loadProductTypes = async () => {
  try {
    const data = await productTypeApi.getActive()
    productTypes.value = Array.isArray(data) ? data.map(item => item.typeName) : []
    // 确保"其他"选项存在
    if (!productTypes.value.includes('其他')) {
      productTypes.value.push('其他')
    }
  } catch (error) {
    console.error('加载产品类型失败', error)
    // 如果加载失败，使用默认值
    productTypes.value = ['蔬菜', '肉类', '禽蛋', '水产', '水果', '饮料', '炒货', '其他']
  }
}

const loadData = async () => {
  loading.value = true
  try {
    let data = []
    if (searchVehicleId.value) {
      data = await transportApi.getByVehicleId(searchVehicleId.value)
    } else if (searchCompanyId.value) {
      data = await transportApi.getByCompanyId(searchCompanyId.value)
    } else {
      data = await transportApi.getAll()
    }
    tableData.value = data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchCompanyId.value = null
  searchVehicleId.value = null
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  otherProductName.value = ''
  formData.value = {
    vehicleId: null,
    statYear: new Date().getFullYear(),
    statMonth: new Date().getMonth() + 1,
    dailyDeliveryTimes: 0,
    monthProductTon: 0,
    monthKilometers: 0,
    monthDeliveryCost: null,
    dailyDeliveryPoints: null,
    peakSeasonDailyIncreaseTimes: null,
    productTypes: []
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  const productTypesList = row.productTypes || []
  // 检查是否有"其他"类型，如果有，提取其他产品名称
  const otherIndex = productTypesList.findIndex(type => type.startsWith('其他'))
  if (otherIndex >= 0) {
    const otherType = productTypesList[otherIndex]
    if (otherType.includes('：') || otherType.includes(':')) {
      otherProductName.value = otherType.split(/[：:]/)[1] || ''
      productTypesList[otherIndex] = '其他'
    } else {
      otherProductName.value = ''
    }
  } else {
    otherProductName.value = ''
  }
  formData.value = {
    vehicleId: row.vehicleId,
    statYear: row.statYear,
    statMonth: row.statMonth,
    dailyDeliveryTimes: row.dailyDeliveryTimes || 0,
    monthProductTon: row.monthProductTon || 0,
    monthKilometers: row.monthKilometers || 0,
    monthDeliveryCost: row.monthDeliveryCost || null,
    dailyDeliveryPoints: row.dailyDeliveryPoints || null,
    peakSeasonDailyIncreaseTimes: row.peakSeasonDailyIncreaseTimes || null,
    productTypes: productTypesList
  }
  dialogVisible.value = true
}

const handleVehicleChange = () => {
  // 车辆选择变化时的处理
}

const handleProductTypeChange = (value) => {
  // 如果移除了"其他"选项，清空其他产品名称
  if (!value || !value.includes('其他')) {
    otherProductName.value = ''
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 处理产品类型，如果选择了"其他"且有输入，则合并
        const submitData = { ...formData.value }
        if (submitData.productTypes && submitData.productTypes.includes('其他')) {
          const otherIndex = submitData.productTypes.indexOf('其他')
          if (otherProductName.value && otherProductName.value.trim()) {
            submitData.productTypes[otherIndex] = `其他：${otherProductName.value.trim()}`
          }
        }
        
        // 确保新字段被包含在提交数据中（即使为null也要提交）
        submitData.monthDeliveryCost = submitData.monthDeliveryCost !== undefined ? submitData.monthDeliveryCost : null
        submitData.dailyDeliveryPoints = submitData.dailyDeliveryPoints !== undefined ? submitData.dailyDeliveryPoints : null
        submitData.peakSeasonDailyIncreaseTimes = submitData.peakSeasonDailyIncreaseTimes !== undefined ? submitData.peakSeasonDailyIncreaseTimes : null
        
        console.log('提交数据:', submitData)
        
        if (currentRow.value) {
          await transportApi.update(currentRow.value.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await transportApi.create(submitData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        otherProductName.value = ''
        loadData()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该运输统计记录吗？', '提示', {
      type: 'warning'
    })
    await transportApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// Excel导入导出功能
const handleExportTemplate = () => {
  const columns = [
    { key: 'plateNumber', label: '车牌号', required: true, example: '京A12345' },
    { key: 'statYear', label: '统计年份', required: true, example: '2025' },
    { key: 'statMonth', label: '统计月份', required: true, example: '1' },
    { key: 'dailyDeliveryTimes', label: '日均配送次数', required: false, example: '5.50' },
    { key: 'dailyDeliveryPoints', label: '日配送网点数', required: false, example: '10' },
    { key: 'peakSeasonDailyIncreaseTimes', label: '旺季增加次数', required: false, example: '2' },
    { key: 'monthProductTon', label: '月产品吨数', required: false, example: '100.50' },
    { key: 'monthKilometers', label: '月公里数', required: false, example: '5000.00' },
    { key: 'monthDeliveryCost', label: '月配送费用', required: false, example: '50000.00' },
    { key: 'productTypes', label: '产品类型', required: false, example: '蔬菜,肉类,水果' }
  ]
  createExcelTemplate(columns, '运输统计模板.xlsx')
  ElMessage.success('模板下载成功')
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    try {
      ElMessage.info('正在解析Excel文件...')
      const excelData = await importFromExcel(file)
      
      if (excelData.length === 0) {
        ElMessage.warning('Excel文件中没有数据')
        return
      }
      
      // 验证和转换数据
      const errors = []
      const successData = []
      
      for (let i = 0; i < excelData.length; i++) {
        const row = excelData[i]
        const rowNum = i + 3 // Excel行号（从3开始，因为第1行是表头，第2行是必填/可选标记）
        const errorsInRow = []
        
        // 验证必填项
        if (!row['车牌号']) errorsInRow.push('车牌号不能为空')
        if (!row['统计年份']) errorsInRow.push('统计年份不能为空')
        if (!row['统计月份']) errorsInRow.push('统计月份不能为空')
        
        if (errorsInRow.length > 0) {
          errors.push(`第${rowNum}行: ${errorsInRow.join('; ')}`)
          continue
        }
        
        // 查找车辆ID
        const vehicle = allVehicles.value.find(v => v.plateNumber === String(row['车牌号']).trim())
        if (!vehicle) {
          errors.push(`第${rowNum}行: 找不到车牌号"${row['车牌号']}"`)
          continue
        }
        
        // 验证年份和月份
        const statYear = parseInt(row['统计年份'])
        const statMonth = parseInt(row['统计月份'])
        
        if (isNaN(statYear) || statYear < 2020 || statYear > 2099) {
          errors.push(`第${rowNum}行: 统计年份格式错误，应为2020-2099之间的数字`)
          continue
        }
        
        if (isNaN(statMonth) || statMonth < 1 || statMonth > 12) {
          errors.push(`第${rowNum}行: 统计月份格式错误，应为1-12之间的数字`)
          continue
        }
        
        // 处理产品类型（可能是逗号分隔的字符串）
        let productTypesArray = []
        if (row['产品类型']) {
          const productTypesStr = String(row['产品类型']).trim()
          if (productTypesStr) {
            // 支持逗号、分号、空格分隔
            productTypesArray = productTypesStr.split(/[,，;；\s]+/).filter(t => t.trim())
          }
        }
        
        // 构建数据对象
        const transportData = {
          vehicleId: vehicle.id,
          statYear: statYear,
          statMonth: statMonth,
          dailyDeliveryTimes: row['日均配送次数'] ? parseFloat(row['日均配送次数']) : 0,
          dailyDeliveryPoints: row['日配送网点数'] ? parseInt(row['日配送网点数']) : null,
          peakSeasonDailyIncreaseTimes: row['旺季增加次数'] ? parseInt(row['旺季增加次数']) : null,
          monthProductTon: row['月产品吨数'] ? parseFloat(row['月产品吨数']) : 0,
          monthKilometers: row['月公里数'] ? parseFloat(row['月公里数']) : 0,
          monthDeliveryCost: row['月配送费用'] ? parseFloat(row['月配送费用']) : null,
          productTypes: productTypesArray.length > 0 ? productTypesArray : []
        }
        
        successData.push(transportData)
      }
      
      // 显示错误信息
      if (errors.length > 0) {
        ElMessage.warning(`导入完成，但有${errors.length}条错误：\n${errors.slice(0, 5).join('\n')}${errors.length > 5 ? '\n...' : ''}`)
      }
      
      // 批量导入数据
      if (successData.length > 0) {
        ElMessage.info(`正在导入${successData.length}条数据...`)
        let successCount = 0
        let failCount = 0
        
        for (const data of successData) {
          try {
            await transportApi.create(data)
            successCount++
          } catch (error) {
            failCount++
            console.error('导入失败:', data, error)
          }
        }
        
        ElMessage.success(`导入完成：成功${successCount}条，失败${failCount}条`)
        loadData()
      }
    } catch (error) {
      ElMessage.error('导入失败: ' + error.message)
    }
  }
  input.click()
}

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  const columns = [
    { key: 'plateNumber', label: '车牌号', formatter: (val) => val || '' },
    { key: 'companyName', label: '所属企业', formatter: (val) => val || '' },
    { key: 'statYear', label: '统计年份', formatter: (val) => val ?? '' },
    { key: 'statMonth', label: '统计月份', formatter: (val) => val ?? '' },
    { key: 'dailyDeliveryTimes', label: '日均配送次数', formatter: (val) => val ?? 0 },
    { key: 'dailyDeliveryPoints', label: '日配送网点数', formatter: (val) => val ?? '' },
    { key: 'peakSeasonDailyIncreaseTimes', label: '旺季增加次数', formatter: (val) => val ?? '' },
    { key: 'monthProductTon', label: '月产品吨数', formatter: (val) => val ?? 0 },
    { key: 'monthKilometers', label: '月公里数', formatter: (val) => val ?? 0 },
    { key: 'monthDeliveryCost', label: '月配送费用', formatter: (val) => val ?? '' },
    { key: 'productTypes', label: '产品类型', formatter: (val) => Array.isArray(val) ? val.join(',') : '' }
  ]
  
  const filename = `运输统计_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
  exportToExcel(tableData.value, columns, filename)
  ElMessage.success('导出成功')
}
</script>

<style scoped lang="scss">
.transport-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
}
</style>
