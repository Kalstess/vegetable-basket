import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layouts/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/company-register',
    name: 'CompanyRegister',
    component: () => import('@/views/company/CompanyRegister.vue'),
    meta: { title: '企业注册', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/companies', // 默认重定向，路由守卫会根据角色再次调整
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer', requiresAuth: true, roles: ['ADMIN', 'BUSINESS_COMMISSION'] }
      },
      {
        path: 'analytics',
        name: 'EnterpriseAnalytics',
        component: () => import('@/views/analytics/EnterpriseTransportDashboard.vue').catch(err => {
          console.error('加载运营分析组件失败:', err)
          // 返回一个错误提示组件
          return {
            template: '<div style="padding: 20px; text-align: center;"><h3>组件加载失败</h3><p>请检查控制台错误信息</p></div>'
          }
        }),
        meta: { title: '运营分析', icon: 'DataAnalysis', requiresAuth: true, roles: ['ADMIN', 'BUSINESS_COMMISSION', 'COMPANY', 'COMPANY_ADMIN', 'COMPANY_USER'] }
      },
      {
        path: 'companies',
        name: 'Companies',
        component: () => import('@/views/company/CompanyList.vue'),
        meta: { title: '企业管理', icon: 'OfficeBuilding', requiresAuth: true, roles: ['ADMIN', 'BUSINESS_COMMISSION', 'COMPANY', 'COMPANY_ADMIN'] }
      },
      {
        path: 'vehicles',
        name: 'Vehicles',
        component: () => import('@/views/vehicle/VehicleList.vue'),
        meta: { title: '车辆管理', icon: 'Van', requiresAuth: true }
      },
      {
        path: 'transport',
        name: 'Transport',
        component: () => import('@/views/transport/TransportList.vue'),
        meta: { title: '运输统计', icon: 'DataAnalysis', requiresAuth: true }
      },
      {
        path: 'compliance',
        name: 'Compliance',
        component: () => import('@/views/compliance/ComplianceList.vue'),
        meta: { title: '合规管理', icon: 'DocumentChecked', requiresAuth: true }
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('@/views/maintenance/MaintenanceList.vue'),
        meta: { title: '维护保养', icon: 'Tools', requiresAuth: true }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/feedback/FeedbackList.vue'),
        meta: { title: '反馈管理', icon: 'ChatDotRound', requiresAuth: true }
      },
      {
        path: 'routes',
        name: 'Routes',
        component: () => import('@/views/route/RouteList.vue'),
        meta: { title: '车辆路线', icon: 'MapLocation', requiresAuth: true }
      },
      {
        path: 'routes/form',
        name: 'RouteForm',
        component: () => import('@/views/route/RouteForm.vue'),
        meta: { title: '路线填报', requiresAuth: true }
      },
      {
        path: 'vehicle-statistics',
        name: 'VehicleStatistics',
        component: () => import('@/views/vehicleStatistics/VehicleStatisticsList.vue'),
        meta: { title: '车辆统计', icon: 'DataBoard', requiresAuth: true, roles: ['ADMIN', 'BUSINESS_COMMISSION'] }
      },
      {
        path: 'survey',
        name: 'Survey',
        component: () => import('@/views/survey/SurveyList.vue'),
        meta: { title: '问卷调查', icon: 'Document', requiresAuth: true, roles: ['ADMIN', 'COMPANY', 'COMPANY_ADMIN', 'COMPANY_USER'] }
      },
      {
        path: 'survey/analysis',
        name: 'SurveyAnalysis',
        component: () => import('@/views/survey/SurveyAnalysis.vue'),
        meta: { title: '问卷数据分析', icon: 'DataAnalysis', requiresAuth: true, roles: ['ADMIN', 'BUSINESS_COMMISSION', 'COMPANY', 'COMPANY_ADMIN'] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'company-users',
        name: 'CompanyUsers',
        component: () => import('@/views/user/CompanyUserList.vue'),
        meta: { title: '企业内部用户', icon: 'UserFilled', requiresAuth: true, roles: ['COMPANY', 'COMPANY_ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'UserFilled', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 根据角色获取默认首页路径
const getDefaultPath = (role) => {
  if (role === 'ADMIN' || role === 'BUSINESS_COMMISSION') {
    return '/dashboard'
  } else if (role === 'COMPANY' || role === 'COMPANY_ADMIN' || role === 'COMPANY_USER') {
    return '/companies'
  } else if (role === 'DRIVER') {
    return '/vehicles'
  }
  return '/companies' // 默认
}

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth !== false
  const role = localStorage.getItem('role')

  // 添加调试日志
  console.log('路由守卫:', { path: to.path, name: to.name, role, hasToken: !!token })

  // 公开页面（登录页、注册页）不需要认证
  if (to.path === '/login' || to.path === '/company-register') {
    if (token) {
      // 已登录访问登录/注册页，跳转到首页
      const defaultPath = getDefaultPath(role)
      if (to.path === defaultPath) {
        next()
      } else {
        next(defaultPath)
      }
    } else {
      next()
    }
    return
  }

  // 个人中心页面：所有已登录用户都可以访问
  if (to.path === '/profile') {
    if (token) {
      next()
    } else {
      next({ name: 'Login', query: { redirect: to.fullPath } })
    }
    return
  }

  // 需要登录但未登录
  if (requiresAuth && !token) {
    console.warn('路由守卫: 未登录，跳转到登录页')
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 处理根路径重定向
  if (to.path === '/') {
    const defaultPath = getDefaultPath(role)
    next(defaultPath)
    return
  }

  // 已登录用户访问需要权限的页面
  if (requiresAuth && to.meta.roles) {
    // 检查角色权限
    if (!role || !to.meta.roles.includes(role)) {
      console.warn('路由守卫: 权限不足', { role, requiredRoles: to.meta.roles, path: to.path })
      // 无权限，跳转到有权限的首页（避免循环重定向）
      const defaultPath = getDefaultPath(role)
      // 如果目标路径就是默认路径，说明已经在正确的页面，直接放行
      if (to.path === defaultPath || to.path === '/') {
        next()
      } else {
        next(defaultPath)
      }
      return
    }
  }

  // 其他情况正常放行
  console.log('路由守卫: 放行', to.path)
  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadError = error.message && error.message.match(pattern)
  if (isChunkLoadError) {
    console.error('组件加载失败，可能是网络问题或构建问题')
    // 可以在这里添加重试逻辑或错误提示
  }
})

export default router

