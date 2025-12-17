<template>
  <div class="company-user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业内部用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%;"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="vehicle" label="关联车辆" width="150">
          <template #default="{ row }">
            {{ row.vehicle?.plateNumber || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
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
      :title="currentRow ? '编辑用户' : '新增用户'"
      width="600px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="!!currentRow" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" :rules="currentRow ? [] : rules.password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%;" @change="handleRoleChange">
            <el-option label="企业普通用户" value="COMPANY_USER" />
            <el-option label="司机用户" value="DRIVER" />
          </el-select>
        </el-form-item>
        <el-form-item
          label="关联车辆"
          prop="vehicleId"
          v-if="formData.role === 'DRIVER'"
          :rules="formData.role === 'DRIVER' ? [{ required: true, message: '司机用户必须选择关联车辆', trigger: 'change' }] : []"
        >
          <el-select v-model="formData.vehicleId" placeholder="请选择车辆" style="width: 100%;" filterable>
            <el-option
              v-for="vehicle in vehicles"
              :key="vehicle.id"
              :label="`${vehicle.plateNumber} - ${vehicle.companyName}`"
              :value="vehicle.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-switch v-model="formData.isActive" />
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
import { userApi, vehicleApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)
const vehicles = ref([])

const formData = ref({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  role: 'COMPANY_USER',
  vehicleId: null,
  isActive: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(() => {
  loadData()
  loadVehicles()
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await userApi.getAll()
    tableData.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadVehicles = async () => {
  try {
    const data = await vehicleApi.getAll()
    vehicles.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (error) {
    console.error('加载车辆列表失败', error)
  }
}

const getRoleName = (role) => {
  const roleMap = {
    COMPANY_USER: '企业普通用户',
    DRIVER: '司机用户'
  }
  return roleMap[role] || role
}

const getRoleType = (role) => {
  const typeMap = {
    COMPANY_USER: 'primary',
    DRIVER: 'success'
  }
  return typeMap[role] || ''
}

const handleAdd = () => {
  currentRow.value = null
  formData.value = {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    role: 'COMPANY_USER',
    vehicleId: null,
    isActive: true
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = {
    username: row.username,
    password: '',
    nickname: row.nickname || '',
    email: row.email || '',
    phone: row.phone || '',
    role: row.role,
    vehicleId: row.vehicle?.id || null,
    isActive: row.isActive
  }
  dialogVisible.value = true
}

const handleRoleChange = () => {
  formData.value.vehicleId = null
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 验证必填项
  if (formData.value.role === 'DRIVER' && !formData.value.vehicleId) {
    ElMessage.warning('司机用户必须选择关联车辆')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          username: formData.value.username,
          password: formData.value.password,
          nickname: formData.value.nickname,
          email: formData.value.email,
          phone: formData.value.phone,
          role: formData.value.role,
          isActive: formData.value.isActive
        }
        
        // 司机用户需要关联车辆
        if (formData.value.role === 'DRIVER' && formData.value.vehicleId) {
          submitData.vehicle = { id: formData.value.vehicleId }
        }

        if (currentRow.value) {
          // 编辑时，如果密码为空则不更新密码
          if (!submitData.password || submitData.password.trim() === '') {
            delete submitData.password
          }
          await userApi.update(currentRow.value.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await userApi.create(submitData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('操作失败:', error)
        const errorMessage = error.response?.data?.message || error.message || '操作失败'
        ElMessage.error(errorMessage)
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await userApi.delete(row.id)
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
.company-user-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>

