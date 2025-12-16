<template>
  <div class="vehicle-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增车辆
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-select v-model="searchCompanyId" placeholder="选择企业" clearable style="width: 200px;">
          <el-option
              v-for="company in companies"
              :key="company.id"
              :label="company.name"
              :value="company.id"
          />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 数据表 -->
      <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%; margin-top: 20px;"
          border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="companyName" label="所属企业" min-width="150" />
        <el-table-column prop="vehicleCategory" label="车辆类别" width="140" />
        <el-table-column prop="vehicleType" label="车辆类型" width="100" />
        <el-table-column prop="colorPlate" label="车牌颜色" width="100" />
        <el-table-column prop="emissionStandard" label="排放标准" width="100" />
        <el-table-column prop="approvedLoad" label="核定载质量(吨)" width="130" />
        <el-table-column prop="purchaseDate" label="购置时间" width="120" />
        <el-table-column prop="isActive" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '有效' : '无效' }}
            </el-tag>
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
        :title="currentRow ? '编辑车辆' : '新增车辆'"
        width="700px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="所属企业" prop="companyId">
          <el-select v-model="formData.companyId" placeholder="请选择企业" style="width: 100%;">
            <el-option
                v-for="company in companies"
                :key="company.id"
                :label="company.name"
                :value="company.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="车牌号" prop="plateNumber">
          <el-input v-model="formData.plateNumber" placeholder="请输入车牌号" />
        </el-form-item>

        <el-form-item label="车辆类别" prop="vehicleCategory">
          <el-select v-model="formData.vehicleCategory" placeholder="请选择类别" style="width: 100%;">
            <el-option label="菜篮子工程车" value="菜篮子工程车" />
            <el-option label="非菜篮子工程车" value="非菜篮子工程车" />
          </el-select>
        </el-form-item>

        <el-form-item label="车辆类型" prop="vehicleType">
          <el-select v-model="formData.vehicleType" placeholder="请选择类型" style="width: 100%;">
            <el-option label="普通" value="普通" />
            <el-option label="冷藏" value="冷藏" />
          </el-select>
        </el-form-item>

        <el-form-item label="车牌颜色" prop="colorPlate">
          <el-select v-model="formData.colorPlate" placeholder="请选择颜色" style="width: 100%;">
            <el-option label="蓝牌" value="蓝牌" />
            <el-option label="黄牌" value="黄牌" />
            <el-option label="绿牌" value="绿牌" />
          </el-select>
        </el-form-item>

        <el-form-item label="排放标准" prop="emissionStandard">
          <el-input v-model="formData.emissionStandard" placeholder="如：国五 / 国六 / 新能源" />
        </el-form-item>

        <el-form-item label="核定载质量(吨)" prop="approvedLoad">
          <el-input-number v-model="formData.approvedLoad" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="购置时间" prop="purchaseDate">
          <el-date-picker
              v-model="formData.purchaseDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="车架号(VIN)" prop="vin">
          <el-input v-model="formData.vin" placeholder="输入车架号" />
        </el-form-item>

        <el-form-item label="发动机号" prop="engineNo">
          <el-input v-model="formData.engineNo" placeholder="输入发动机号" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { vehicleApi, companyApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const searchCompanyId = ref(null)
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)

const formData = ref({
  companyId: null,
  plateNumber: '',
  vehicleCategory: '菜篮子工程车',
  vehicleType: '普通',
  colorPlate: '蓝牌',
  emissionStandard: '',
  approvedLoad: 0,
  purchaseDate: '',
  vin: '',
  engineNo: ''
})

const rules = {
  companyId: [{ required: true, message: '请选择所属企业', trigger: 'change' }],
  plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  vehicleCategory: [{ required: true, message: '请选择车辆类别', trigger: 'change' }],
  vehicleType: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
  colorPlate: [{ required: true, message: '请选择车牌颜色', trigger: 'change' }]
}

onMounted(() => {
  loadCompanies()
  loadData()
})

const loadCompanies = async () => {
  try {
    companies.value = await companyApi.getAll()
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    if (searchCompanyId.value) {
      tableData.value = await vehicleApi.getByCompanyId(searchCompanyId.value)
    } else {
      tableData.value = await vehicleApi.getAll()
    }
  } catch (error) {
    ElMessage.error('加载车辆数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchCompanyId.value = null
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  formData.value = {
    companyId: null,
    plateNumber: '',
    vehicleCategory: '菜篮子工程车',
    vehicleType: '普通',
    colorPlate: '蓝牌',
    emissionStandard: '',
    approvedLoad: 0,
    purchaseDate: '',
    vin: '',
    engineNo: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentRow.value) {
          await vehicleApi.update(currentRow.value.id, formData.value)
          ElMessage.success('更新成功')
        } else {
          await vehicleApi.create(formData.value)
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
    await ElMessageBox.confirm(`确定要删除车辆 [${row.plateNumber}] 吗？`, '提示', { type: 'warning' })
    await vehicleApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}
</script>

<style scoped lang="scss">
.vehicle-list {
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
