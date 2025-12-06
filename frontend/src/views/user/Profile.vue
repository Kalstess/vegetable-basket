<template>
  <div class="profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" style="max-width: 600px;">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="getRoleType(formData.role)">{{ getRoleName(formData.role) }}</el-tag>
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
        <el-form-item label="关联企业" v-if="formData.company">
          <el-input :value="formData.company.name" disabled />
        </el-form-item>
        <el-form-item label="关联车辆" v-if="formData.vehicle">
          <el-input :value="formData.vehicle.plateNumber" disabled />
        </el-form-item>
        <el-divider>修改密码</el-divider>
        <el-form-item label="新密码" prop="password" :rules="formData.password ? rules.password : []">
          <el-input v-model="formData.password" type="password" placeholder="留空则不修改密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'

const formRef = ref(null)
const submitting = ref(false)
const formData = ref({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  role: '',
  password: '',
  company: null,
  vehicle: null
})

const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadProfile()
})

const loadProfile = async () => {
  try {
    const data = await userApi.getCurrentUser()
    formData.value = {
      username: data.username || '',
      nickname: data.nickname || '',
      email: data.email || '',
      phone: data.phone || '',
      role: data.role || '',
      password: '',
      company: data.company || null,
      vehicle: data.vehicle || null
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
  }
}

const getRoleName = (role) => {
  const roleMap = {
    ADMIN: '管理员',
    COMPANY: '企业用户',
    DRIVER: '司机用户'
  }
  return roleMap[role] || role
}

const getRoleType = (role) => {
  const typeMap = {
    ADMIN: 'danger',
    COMPANY: 'primary',
    DRIVER: 'success'
  }
  return typeMap[role] || ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const submitData = {
          nickname: formData.value.nickname,
          email: formData.value.email,
          phone: formData.value.phone
        }
        // 只有填写了新密码才提交
        if (formData.value.password && formData.value.password.trim()) {
          submitData.password = formData.value.password
        }
        
        await userApi.updateProfile(submitData)
        ElMessage.success('保存成功')
        // 更新本地存储的昵称
        if (submitData.nickname) {
          localStorage.setItem('nickname', submitData.nickname)
        }
        handleReset()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleReset = () => {
  loadProfile()
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.profile {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>

