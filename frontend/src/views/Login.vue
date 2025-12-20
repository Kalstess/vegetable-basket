<template>
  <div class="login-container">
    <ParticleText text="菜篮子工程车管理系统" :fontSize="100" :particleSize="2" :particleDistance="80" :particleSpeed="0.5" />
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>菜篮子工程车管理系统</h2>
        </div>
      </template>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">
        <p class="register-link">
          尚未入驻？<a href="javascript:;" @click.prevent="goToRegister">企业自助注册</a>
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import ParticleText from '@/components/ParticleText.vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = await authApi.login(loginForm.username, loginForm.password)
        
        console.log('登录响应数据:', data)
        
        // 检查响应数据格式
        if (!data || !data.token) {
          console.error('登录响应数据格式错误:', data)
          ElMessage.error('登录响应数据格式错误')
          return
        }
        
        // 保存token和用户信息
        localStorage.setItem('token', data.token)
        localStorage.setItem('username', data.username)
        localStorage.setItem('nickname', data.nickname || data.username)
        localStorage.setItem('role', data.role)
        if (data.companyId) {
          localStorage.setItem('companyId', data.companyId.toString())
        }
        
        console.log('Token已保存:', data.token.substring(0, 20) + '...')

        ElMessage.success('登录成功')
        
        // 延迟一下再跳转，确保token已保存
        setTimeout(() => {
          // 根据角色跳转到不同首页
          let targetPath = '/companies' // 默认
          if (data.role === 'ADMIN' || data.role === 'BUSINESS_COMMISSION') {
            targetPath = '/dashboard'
          } else if (data.role === 'COMPANY' || data.role === 'COMPANY_ADMIN' || data.role === 'COMPANY_USER') {
            targetPath = '/companies'
          } else if (data.role === 'DRIVER') {
            targetPath = '/vehicles'
          }
          router.push(targetPath).catch(err => {
            // 忽略导航重复的错误
            if (err.name !== 'NavigationDuplicated') {
              console.error('路由跳转失败:', err)
            }
          })
        }, 100)
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push({ path: '/company-register' })
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 50%, #43e97b 100%);
  position: relative;
  overflow: hidden;

  .login-card {
    width: 400px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    position: relative;
    z-index: 10;
    margin-top: 120px; // 下移登录框，为粒子文字留出空间
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 12px;

    .card-header {
      text-align: center;

      h2 {
        margin: 0;
        color: #303133;
        font-size: 24px;
      }
    }

    .login-tip {
      margin-top: 20px;
      text-align: center;
      color: #909399;
      font-size: 12px;

      p {
        margin: 5px 0;
      }

      .register-link a {
        color: #409eff;
        text-decoration: underline;
        cursor: pointer;
      }
    }
  }
}
</style>

