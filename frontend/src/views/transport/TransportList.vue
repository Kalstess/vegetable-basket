<template>
  <div class="transport-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运输统计</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增运输统计
          </el-button>
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
import { Plus } from '@element-plus/icons-vue'
import { transportApi, companyApi, vehicleApi, productTypeApi } from '@/api'

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
