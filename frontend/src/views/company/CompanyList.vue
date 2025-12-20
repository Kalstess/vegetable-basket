<template>
  <div class="company-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业管理</span>
          <el-button 
            v-if="canCreate" 
            type="primary" 
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新增企业
          </el-button>
        </div>
      </template>

      <!-- 搜索栏和状态筛选 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入企业名称或经营范围"
          style="width: 300px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-if="canFilterByStatus"
          v-model="statusFilter"
          placeholder="全部状态"
          style="width: 150px;"
          clearable
          @change="handleStatusFilterChange"
        >
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已停用" value="DISABLED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div v-if="canApprove && selectedCompanies.length > 0" style="margin-left: 10px;">
          <el-button type="success" @click="handleBatchApprove">批量通过 ({{ selectedCompanies.length }})</el-button>
          <el-button type="warning" @click="handleBatchReject">批量驳回 ({{ selectedCompanies.length }})</el-button>
        </div>
        <div v-if="canDelete && selectedCompaniesForDelete.length > 0" style="margin-left: 10px;">
          <el-button type="danger" @click="handleBatchDelete">批量删除 ({{ selectedCompaniesForDelete.length }})</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px;"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column 
          v-if="canApprove"
          type="selection" 
          width="55"
          :selectable="(row) => row.status === 'PENDING'"
        />
        <el-table-column 
          v-if="canDelete"
          type="selection" 
          width="55"
          @selection-change="handleDeleteSelectionChange"
        />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="企业名称" min-width="200" />
        <el-table-column prop="companyType" label="企业性质" width="120" />
        <el-table-column prop="legalPersonName" label="负责人" width="120" />
        <el-table-column prop="legalPersonPhone" label="联系电话" width="150" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column 
          v-if="canFilterByStatus" 
          prop="auditRemark" 
          label="审批备注" 
          min-width="150" 
          show-overflow-tooltip 
        />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="canApprove && row.status === 'PENDING'" 
              type="success" 
              link 
              @click="handleApprove(row)"
            >
              审批通过
            </el-button>
            <el-button 
              v-if="canApprove && row.status === 'PENDING'" 
              type="warning" 
              link 
              @click="handleReject(row)"
            >
              驳回
            </el-button>
            <el-button 
              v-if="canDelete" 
              type="danger" 
              link 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <CompanyDialog
      v-model="dialogVisible"
      :form-data="currentRow"
      @success="handleDialogSuccess"
    />

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approvalDialogVisible"
      :title="approvalAction === 'approve' ? '审批通过' : '审批驳回'"
      width="600px"
    >
      <el-form :model="approvalForm" label-width="120px">
        <el-form-item label="企业名称">
          <el-input v-model="approvalForm.companyName" disabled />
        </el-form-item>
        <el-form-item 
          v-if="approvalAction === 'approve' && existingCompanies.length > 0"
          label="关联已有企业"
        >
          <el-select 
            v-model="approvalForm.existingCompanyId" 
            placeholder="选择已有企业（可选，留空则创建新企业）"
            style="width: 100%;"
            clearable
            filterable
          >
            <el-option
              v-for="company in existingCompanies"
              :key="company.id"
              :label="`${company.name} (${company.address || '无地址'})`"
              :value="company.id"
            />
          </el-select>
          <div style="margin-top: 8px; color: #909399; font-size: 12px;">
            提示：如果选择已有企业，将把注册账号关联到该企业，并删除待审批的企业记录
          </div>
        </el-form-item>
        <el-form-item label="审批备注">
          <el-input
            v-model="approvalForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入审批备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmApproval"
          :loading="approvalLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { companyApi, companyRegistrationApi } from '@/api'
import CompanyDialog from './CompanyDialog.vue'

const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const tableData = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const approvalDialogVisible = ref(false)
const approvalAction = ref('approve') // 'approve' or 'reject'
const approvalForm = ref({
  companyId: null,
  companyName: '',
  remark: '',
  existingCompanyId: null
})
const approvalLoading = ref(false)
const existingCompanies = ref([])
const allCompanies = ref([])
const selectedCompanies = ref([])
const selectedCompaniesForDelete = ref([])

// 获取当前用户角色
const currentRole = ref(localStorage.getItem('role') || '')

// 权限判断
const canCreate = computed(() => {
  return currentRole.value === 'ADMIN'
})

const canDelete = computed(() => {
  return currentRole.value === 'ADMIN'
})

const canApprove = computed(() => {
  return currentRole.value === 'ADMIN' || currentRole.value === 'BUSINESS_COMMISSION'
})

const canFilterByStatus = computed(() => {
  return currentRole.value === 'ADMIN' || currentRole.value === 'BUSINESS_COMMISSION'
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  loadData()
  loadAllCompanies()
})

const loadAllCompanies = async () => {
  try {
    const data = await companyApi.getAll()
    allCompanies.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    let data = []
    if (searchKeyword.value) {
      data = await companyApi.search(searchKeyword.value)
    } else {
      // 如果有状态筛选，传递状态参数
      const status = statusFilter.value || undefined
      data = await companyApi.getAll(status)
    }
    
    // 确保 data 是数组
    if (Array.isArray(data)) {
      tableData.value = data
      pagination.value.total = data.length
    } else if (data && Array.isArray(data.data)) {
      tableData.value = data.data
      pagination.value.total = data.data.length
    } else {
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error(error.response?.data?.message || '加载数据失败')
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadData()
}

const handleReset = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  handleSearch()
}

const handleStatusFilterChange = () => {
  pagination.value.page = 1
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该企业吗？', '提示', {
      type: 'warning'
    })
    await companyApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const handleApprove = async (row) => {
  approvalAction.value = 'approve'
  approvalForm.value = {
    companyId: row.id,
    companyName: row.name,
    remark: '',
    existingCompanyId: null
  }
  
  // 根据企业名称匹配已有企业（排除待审批和已驳回的企业）
  const matchedCompanies = allCompanies.value.filter(c => 
    c.name === row.name && 
    c.status !== 'PENDING' && 
    c.status !== 'REJECTED' &&
    c.id !== row.id
  )
  existingCompanies.value = matchedCompanies
  
  approvalDialogVisible.value = true
}

const handleReject = (row) => {
  approvalAction.value = 'reject'
  approvalForm.value = {
    companyId: row.id,
    companyName: row.name,
    remark: ''
  }
  approvalDialogVisible.value = true
}

const confirmApproval = async () => {
  approvalLoading.value = true
  try {
    if (approvalAction.value === 'approve') {
      await companyRegistrationApi.approve(
        approvalForm.value.companyId, 
        approvalForm.value.remark,
        approvalForm.value.existingCompanyId || undefined
      )
      ElMessage.success('审批通过')
    } else {
      await companyRegistrationApi.reject(approvalForm.value.companyId, approvalForm.value.remark)
      ElMessage.success('已驳回')
    }
    approvalDialogVisible.value = false
    loadData()
    loadAllCompanies() // 重新加载企业列表
  } catch (error) {
    console.error('审批操作失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    approvalLoading.value = false
  }
}

const handleDialogSuccess = () => {
  dialogVisible.value = false
  loadData()
}

const handleSizeChange = () => {
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedCompanies.value = selection.filter(row => row.status === 'PENDING')
}

const handleDeleteSelectionChange = (selection) => {
  selectedCompaniesForDelete.value = selection
}

const handleBatchApprove = async () => {
  if (selectedCompanies.value.length === 0) {
    ElMessage.warning('请选择要审批的企业')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量通过 ${selectedCompanies.value.length} 个企业吗？`,
      '批量审批通过',
      {
        type: 'warning'
      }
    )
    
    approvalLoading.value = true
    const promises = selectedCompanies.value.map(company => 
      companyRegistrationApi.approve(company.id, '批量审批通过', null)
    )
    
    await Promise.all(promises)
    ElMessage.success(`成功通过 ${selectedCompanies.value.length} 个企业`)
    selectedCompanies.value = []
    loadData()
    loadAllCompanies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量审批失败:', error)
      ElMessage.error(error.response?.data?.message || '批量审批失败')
    }
  } finally {
    approvalLoading.value = false
  }
}

const handleBatchReject = async () => {
  if (selectedCompanies.value.length === 0) {
    ElMessage.warning('请选择要驳回的企业')
    return
  }
  
  try {
    await ElMessageBox.prompt(
      `确定要批量驳回 ${selectedCompanies.value.length} 个企业吗？请输入驳回原因：`,
      '批量审批驳回',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入驳回原因（可选）'
      }
    ).then(({ value }) => {
      approvalLoading.value = true
      const promises = selectedCompanies.value.map(company => 
        companyRegistrationApi.reject(company.id, value || '批量审批驳回')
      )
      
      return Promise.all(promises).then(() => {
        ElMessage.success(`成功驳回 ${selectedCompanies.value.length} 个企业`)
        selectedCompanies.value = []
        loadData()
        loadAllCompanies()
      })
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量驳回失败:', error)
      ElMessage.error(error.response?.data?.message || '批量驳回失败')
    }
  } finally {
    approvalLoading.value = false
  }
}

const handleBatchDelete = async () => {
  if (selectedCompaniesForDelete.value.length === 0) {
    ElMessage.warning('请选择要删除的企业')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量删除 ${selectedCompaniesForDelete.value.length} 个企业吗？此操作不可恢复！`,
      '批量删除',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )
    
    const promises = selectedCompaniesForDelete.value.map(company => 
      companyApi.delete(company.id)
    )
    
    await Promise.all(promises)
    ElMessage.success(`成功删除 ${selectedCompaniesForDelete.value.length} 个企业`)
    selectedCompaniesForDelete.value = []
    loadData()
    loadAllCompanies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.response?.data?.message || '批量删除失败')
    }
  }
}

const getStatusName = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回',
    'DISABLED': '已停用'
  }
  return statusMap[status] || status || '-'
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'DISABLED': 'info'
  }
  return typeMap[status] || ''
}
</script>

<style scoped lang="scss">
.company-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    align-items: center;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
