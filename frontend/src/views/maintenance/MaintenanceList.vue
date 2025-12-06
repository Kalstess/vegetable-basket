<template>
  <div class="maintenance-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维护保养</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增维护记录
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
        <el-table-column prop="maintDate" label="维护日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.maintDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="maintType" label="维护类型" width="120" />
        <el-table-column prop="cost" label="费用" width="100">
          <template #default="{ row }">
            {{ row.cost ? `¥${row.cost}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="serviceProvider" label="服务提供商" width="150" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="nextMaintDate" label="下次维护日期" width="120">
          <template #default="{ row }">
            {{ row.nextMaintDate ? formatDate(row.nextMaintDate) : '-' }}
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
      :title="currentRow ? '编辑维护记录' : '新增维护记录'"
      width="800px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="车辆" prop="vehicle.id">
          <el-select v-model="formData.vehicle.id" placeholder="请选择车辆" style="width: 100%;" @change="handleVehicleChange">
            <el-option
              v-for="vehicle in allVehicles"
              :key="vehicle.id"
              :label="`${vehicle.plateNumber} - ${vehicle.companyName}`"
              :value="vehicle.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="维护日期" prop="maintDate">
          <el-date-picker
            v-model="formData.maintDate"
            type="date"
            placeholder="请选择维护日期"
            style="width: 100%;"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="维护类型" prop="maintType">
          <el-select v-model="formData.maintType" placeholder="请选择维护类型" style="width: 100%;">
            <el-option label="日常保养" value="日常保养" />
            <el-option label="定期维护" value="定期维护" />
            <el-option label="故障维修" value="故障维修" />
            <el-option label="年度检测" value="年度检测" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用" prop="cost">
          <el-input-number v-model="formData.cost" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="服务提供商" prop="serviceProvider">
          <el-input v-model="formData.serviceProvider" placeholder="请输入服务提供商" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="下次维护日期" prop="nextMaintDate">
          <el-date-picker
            v-model="formData.nextMaintDate"
            type="date"
            placeholder="请选择下次维护日期"
            style="width: 100%;"
            value-format="YYYY-MM-DD"
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
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { maintenanceApi, companyApi, vehicleApi } from '@/api'

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

const formData = ref({
  vehicle: { id: null },
  maintDate: null,
  maintType: null,
  cost: 0,
  serviceProvider: '',
  description: '',
  nextMaintDate: null
})

const rules = {
  'vehicle.id': [{ required: true, message: '请选择车辆', trigger: 'change' }],
  maintDate: [{ required: true, message: '请选择维护日期', trigger: 'change' }],
  maintType: [{ required: true, message: '请选择维护类型', trigger: 'change' }]
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

const loadData = async () => {
  loading.value = true
  try {
    let data = []
    if (searchVehicleId.value) {
      data = await maintenanceApi.getByVehicleId(searchVehicleId.value)
    } else if (searchCompanyId.value) {
      data = await maintenanceApi.getByCompanyId(searchCompanyId.value)
    } else {
      data = await maintenanceApi.getAll()
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
  formData.value = {
    vehicle: { id: null },
    maintDate: null,
    maintType: null,
    cost: 0,
    serviceProvider: '',
    description: '',
    nextMaintDate: null
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = {
    vehicle: { id: row.vehicleId },
    maintDate: row.maintDate,
    maintType: row.maintType,
    cost: row.cost || 0,
    serviceProvider: row.serviceProvider || '',
    description: row.description || '',
    nextMaintDate: row.nextMaintDate || null
  }
  dialogVisible.value = true
}

const handleVehicleChange = () => {
  // 车辆选择变化时的处理
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentRow.value) {
          await maintenanceApi.update(currentRow.value.id, formData.value)
          ElMessage.success('更新成功')
        } else {
          await maintenanceApi.create(formData.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该维护记录吗？', '提示', {
      type: 'warning'
    })
    await maintenanceApi.delete(row.id)
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
.maintenance-list {
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
