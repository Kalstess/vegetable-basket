<template>
  <div class="company-register">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>企业自助注册</h2>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="formData.companyName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入企业地址" />
        </el-form-item>
        <el-form-item label="法定代表人" prop="legalPersonName">
          <el-input v-model="formData.legalPersonName" placeholder="请输入法定代表人姓名" />
        </el-form-item>
        <el-form-item label="法人电话" prop="legalPersonPhone">
          <el-input v-model="formData.legalPersonPhone" placeholder="请输入法人联系电话" />
        </el-form-item>
        <el-form-item label="货运证联系人" prop="freightPassContactName">
          <el-input v-model="formData.freightPassContactName" placeholder="请输入货运车通行证联系人姓名" />
        </el-form-item>
        <el-form-item label="联系人电话" prop="freightPassContactPhone">
          <el-input v-model="formData.freightPassContactPhone" placeholder="请输入联系人电话" />
        </el-form-item>
        <el-form-item label="企业性质" prop="companyType">
          <el-select v-model="formData.companyType" placeholder="请选择企业性质" style="width: 100%;">
            <el-option label="国有" value="国有" />
            <el-option label="民营" value="民营" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="经营范围" prop="businessScope">
          <el-input
            v-model="formData.businessScope"
            type="textarea"
            :rows="3"
            placeholder="请输入企业经营范围"
          />
        </el-form-item>

        <el-divider>企业管理员账号</el-divider>

        <el-form-item label="管理员用户名" prop="adminUsername">
          <el-input v-model="formData.adminUsername" placeholder="用于登录系统的账号名" />
        </el-form-item>
        <el-form-item label="管理员密码" prop="adminPassword">
          <el-input v-model="formData.adminPassword" type="password" show-password placeholder="请设置登录密码" />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="adminNickname">
          <el-input v-model="formData.adminNickname" placeholder="请输入管理员姓名" />
        </el-form-item>
        <el-form-item label="管理员电话" prop="adminPhone">
          <el-input v-model="formData.adminPhone" placeholder="请输入管理员联系电话" />
        </el-form-item>
        <el-form-item label="管理员邮箱" prop="adminEmail">
          <el-input v-model="formData.adminEmail" placeholder="请输入管理员邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button @click="goBack">返回登录</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交注册申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { companyRegistrationApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const formData = ref({
  companyName: '',
  address: '',
  legalPersonName: '',
  legalPersonPhone: '',
  freightPassContactName: '',
  freightPassContactPhone: '',
  companyType: '',
  businessScope: '',
  adminUsername: '',
  adminPassword: '',
  adminNickname: '',
  adminPhone: '',
  adminEmail: ''
})

const rules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  legalPersonName: [{ required: true, message: '请输入法定代表人', trigger: 'blur' }],
  legalPersonPhone: [{ required: true, message: '请输入法人电话', trigger: 'blur' }],
  adminUsername: [{ required: true, message: '请输入管理员用户名', trigger: 'blur' }],
  adminPassword: [
    { required: true, message: '请输入管理员密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await companyRegistrationApi.register(formData.value)
      ElMessage.success('提交成功，待商务委审核')
      router.push('/login').catch(err => {
        // 忽略导航重复的错误
        if (err.name !== 'NavigationDuplicated') {
          console.error('路由跳转失败:', err)
        }
      })
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '提交失败')
    } finally {
      submitting.value = false
    }
  })
}

const goBack = () => {
  router.push('/login').catch(err => {
    // 忽略导航重复的错误
    if (err.name !== 'NavigationDuplicated') {
      console.error('路由跳转失败:', err)
    }
  })
}
</script>

<style scoped lang="scss">
.company-register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 50%, #43e97b 100%);

  .register-card {
    width: 600px;
    max-width: 90%;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    background: rgba(255, 255, 255, 0.98);
    border-radius: 12px;

    .card-header {
      text-align: center;

      h2 {
        margin: 0;
        font-size: 22px;
        color: #303133;
      }
    }
  }
}
</style>


