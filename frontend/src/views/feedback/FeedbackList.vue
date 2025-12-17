<template>
  <div class="feedback-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>反馈管理</span>
          <!-- 所有角色均可新增反馈 -->
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增反馈
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
        <el-select v-model="searchStatus" placeholder="选择状态" clearable style="width: 150px;">
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="已处理" value="已处理" />
          <el-option label="已关闭" value="已关闭" />
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
        <el-table-column prop="companyName" label="企业名称" min-width="150" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column label="报告年月" width="120">
          <template #default="{ row }">
            {{ row.reportYear }}年{{ row.reportMonth }}月
          </template>
        </el-table-column>
        <el-table-column prop="mainOperationalDifficulties" label="主要运营困难" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="isAdmin" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentRow ? '编辑反馈' : '新增反馈'"
      width="800px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="企业" prop="company.id">
          <el-select v-model="formData.company.id" placeholder="请选择企业" style="width: 100%;" @change="handleCompanyChange">
            <el-option
              v-for="company in companies"
              :key="company.id"
              :label="company.name"
              :value="company.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆" prop="vehicle.id">
          <el-select v-model="formData.vehicle.id" placeholder="请选择车辆（可选）" clearable style="width: 100%;">
            <el-option
              v-for="vehicle in vehicles"
              :key="vehicle.id"
              :label="vehicle.plateNumber"
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
        <el-form-item label="主要运营困难" prop="mainOperationalDifficulties">
          <el-input v-model="formData.mainOperationalDifficulties" type="textarea" :rows="3" placeholder="请输入主要运营困难" />
        </el-form-item>
        <el-form-item 
          label="政策建议" 
          prop="policySuggestions"
        >
          <el-input
            v-model="formData.policySuggestions"
            type="textarea"
            :rows="3"
            :readonly="!isAdmin"
            :disabled="!isAdmin"
            placeholder="仅管理员可编辑"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item 
          label="状态" 
          prop="status"
        >
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            style="width: 100%;"
            :disabled="!isAdmin"
          >
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已处理" value="已处理" />
            <el-option label="已关闭" value="已关闭" />
          </el-select>
        </el-form-item>
        <el-form-item 
          label="回复" 
          prop="response"
        >
          <el-input
            v-model="formData.response"
            type="textarea"
            :rows="3"
            :readonly="!isAdmin"
            :disabled="!isAdmin"
            placeholder="仅管理员可编辑"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <!-- 管理员可编辑全部记录；企业/司机仅能提交“新增反馈”，不能修改已有记录 -->
        <el-button
          v-if="isAdmin || !currentRow"
          type="primary"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { feedbackApi, companyApi, vehicleApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const vehicles = ref([])
const searchCompanyId = ref(null)
const searchStatus = ref(null)
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)

// 角色判断
const role = ref(localStorage.getItem('role') || '')
const isAdmin = computed(() => role.value === 'ADMIN' || role.value === 'BUSINESS_COMMISSION')

const formData = ref({
  company: { id: null },
  vehicle: { id: null },
  reportYear: new Date().getFullYear(),
  reportMonth: new Date().getMonth() + 1,
  mainOperationalDifficulties: '',
  policySuggestions: '',
  contactPerson: '',
  contactPhone: '',
  status: '待处理',
  response: ''
})

const rules = {
  'company.id': [{ required: true, message: '请选择企业', trigger: 'change' }],
  reportYear: [{ required: true, message: '请输入报告年份', trigger: 'blur' }],
  reportMonth: [{ required: true, message: '请输入报告月份', trigger: 'blur' }]
}

// 监听企业选择变化，加载对应车辆
watch(() => formData.value.company.id, (val) => {
  if (val) {
    loadVehicles(val)
  } else {
    vehicles.value = []
    formData.value.vehicle.id = null
  }
})

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

const loadVehicles = async (companyId) => {
  try {
    vehicles.value = await vehicleApi.getByCompanyId(companyId)
  } catch (error) {
    console.error('加载车辆列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    let data = []
    if (searchCompanyId.value) {
      data = await feedbackApi.getByCompanyId(searchCompanyId.value)
    } else {
      data = await feedbackApi.getAll()
    }
    // 非管理员和非商务委用户默认只关注已处理/已关闭的反馈（便于查看政策建议与回复）
    if (!isAdmin.value) {
      data = (data || []).filter(item => item.status === '已处理' || item.status === '已关闭')
    }
    // 根据状态筛选
    if (searchStatus.value) {
      data = (data || []).filter(item => item.status === searchStatus.value)
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
  searchStatus.value = null
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  formData.value = {
    company: { id: null },
    vehicle: { id: null },
    reportYear: new Date().getFullYear(),
    reportMonth: new Date().getMonth() + 1,
    mainOperationalDifficulties: '',
    policySuggestions: '',
    contactPerson: '',
    contactPhone: '',
    status: '待处理',
    response: ''
  }
  dialogVisible.value = true
}

// 查看详情（企业/司机用户用于查看已处理的政策建议和回复）
const handleView = (row) => {
  currentRow.value = row
  formData.value = {
    company: { id: row.companyId },
    vehicle: { id: row.vehicleId || null },
    reportYear: row.reportYear,
    reportMonth: row.reportMonth,
    mainOperationalDifficulties: row.mainOperationalDifficulties || '',
    policySuggestions: row.policySuggestions || '',
    contactPerson: row.contactPerson || '',
    contactPhone: row.contactPhone || '',
    status: row.status || '待处理',
    response: row.response || ''
  }
  if (row.companyId) {
    loadVehicles(row.companyId)
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = {
    company: { id: row.companyId },
    vehicle: { id: row.vehicleId || null },
    reportYear: row.reportYear,
    reportMonth: row.reportMonth,
    mainOperationalDifficulties: row.mainOperationalDifficulties || '',
    policySuggestions: row.policySuggestions || '',
    contactPerson: row.contactPerson || '',
    contactPhone: row.contactPhone || '',
    status: row.status || '待处理',
    response: row.response || ''
  }
  if (row.companyId) {
    loadVehicles(row.companyId)
  }
  dialogVisible.value = true
}

const handleCompanyChange = () => {
  formData.value.vehicle.id = null
}

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'info',
    '处理中': 'warning',
    '已处理': 'success',
    '已关闭': ''
  }
  return statusMap[status] || ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...formData.value,
          vehicle: formData.value.vehicle.id ? { id: formData.value.vehicle.id } : null
        }
        if (currentRow.value) {
          await feedbackApi.update(currentRow.value.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await feedbackApi.create(submitData)
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
    await ElMessageBox.confirm('确定要删除该反馈记录吗？', '提示', {
      type: 'warning'
    })
    await feedbackApi.delete(row.id)
    ElMessage.success('删除成功')
    // 重新加载数据并更新词云图
    await loadData()
    // 确保词云图更新
    nextTick(() => {
    })
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped lang="scss">
.feedback-list {
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
