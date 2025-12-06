<template>
  <div class="compliance-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>合规管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增合规记录
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
        <el-table-column label="报告年月" width="120">
          <template #default="{ row }">
            {{ row.reportYear }}年{{ row.reportMonth }}月
          </template>
        </el-table-column>
        <el-table-column prop="illegalModification" label="非法改装" width="100">
          <template #default="{ row }">
            <el-tag :type="row.illegalModification ? 'danger' : 'success'">
              {{ row.illegalModification ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="trafficViolations" label="交通违法次数" width="120" />
        <el-table-column prop="trafficAccidents" label="交通事故次数" width="120" />
        <el-table-column prop="violationDetails" label="违法详情" min-width="150" show-overflow-tooltip />
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
      :title="currentRow ? '编辑合规记录' : '新增合规记录'"
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
        <el-form-item label="报告年份" prop="reportYear">
          <el-input-number v-model="formData.reportYear" :min="2020" :max="2099" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="报告月份" prop="reportMonth">
          <el-input-number v-model="formData.reportMonth" :min="1" :max="12" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="非法改装" prop="illegalModification">
          <el-switch v-model="formData.illegalModification" />
        </el-form-item>
        <el-form-item label="交通违法次数" prop="trafficViolations">
          <el-input-number v-model="formData.trafficViolations" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="交通事故次数" prop="trafficAccidents">
          <el-input-number v-model="formData.trafficAccidents" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="违法详情" prop="violationDetails">
          <el-input v-model="formData.violationDetails" type="textarea" :rows="3" placeholder="请输入违法详情" />
        </el-form-item>
        <el-form-item label="事故详情" prop="accidentDetails">
          <el-input v-model="formData.accidentDetails" type="textarea" :rows="3" placeholder="请输入事故详情" />
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
import { complianceApi, companyApi, vehicleApi } from '@/api'

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
  reportYear: new Date().getFullYear(),
  reportMonth: new Date().getMonth() + 1,
  illegalModification: false,
  trafficViolations: 0,
  trafficAccidents: 0,
  violationDetails: '',
  accidentDetails: ''
})

const rules = {
  'vehicle.id': [{ required: true, message: '请选择车辆', trigger: 'change' }],
  reportYear: [{ required: true, message: '请输入报告年份', trigger: 'blur' }],
  reportMonth: [{ required: true, message: '请输入报告月份', trigger: 'blur' }]
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
      data = await complianceApi.getByVehicleId(searchVehicleId.value)
    } else if (searchCompanyId.value) {
      data = await complianceApi.getByCompanyId(searchCompanyId.value)
    } else {
      data = await complianceApi.getAll()
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
    reportYear: new Date().getFullYear(),
    reportMonth: new Date().getMonth() + 1,
    illegalModification: false,
    trafficViolations: 0,
    trafficAccidents: 0,
    violationDetails: '',
    accidentDetails: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = {
    vehicle: { id: row.vehicleId },
    reportYear: row.reportYear,
    reportMonth: row.reportMonth,
    illegalModification: row.illegalModification || false,
    trafficViolations: row.trafficViolations || 0,
    trafficAccidents: row.trafficAccidents || 0,
    violationDetails: row.violationDetails || '',
    accidentDetails: row.accidentDetails || ''
  }
  dialogVisible.value = true
}

const handleVehicleChange = () => {
  // 车辆选择变化时的处理
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentRow.value) {
          await complianceApi.update(currentRow.value.id, formData.value)
          ElMessage.success('更新成功')
        } else {
          await complianceApi.create(formData.value)
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
    await ElMessageBox.confirm('确定要删除该合规记录吗？', '提示', {
      type: 'warning'
    })
    await complianceApi.delete(row.id)
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
.compliance-list {
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
