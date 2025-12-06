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
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'companies',
        name: 'Companies',
        component: () => import('@/views/company/CompanyList.vue'),
        meta: { title: '企业管理', icon: 'OfficeBuilding', requiresAuth: true, roles: ['ADMIN', 'COMPANY'] }
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
        meta: { title: '车辆统计', icon: 'DataBoard', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'survey',
        name: 'Survey',
        component: () => import('@/views/survey/SurveyList.vue'),
        meta: { title: '问卷调查', icon: 'Document', requiresAuth: true, roles: ['ADMIN', 'COMPANY'] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true, roles: ['ADMIN'] }
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

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.meta.requiresAuth !== false
  const role = localStorage.getItem('role')

  // 个人中心页面：所有已登录用户都可以访问
  if (to.path === '/profile') {
    if (token) {
      next()
    } else {
      next({ name: 'Login', query: { redirect: to.fullPath } })
    }
    return
  }

  if (requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    // 已登录访问登录页，跳转到首页
    // 根据角色跳转到不同首页
    if (role === 'ADMIN') {
      next('/dashboard')
    } else if (role === 'COMPANY') {
      next('/companies')
    } else if (role === 'DRIVER') {
      next('/vehicles')
    } else {
      next('/companies')
    }
  } else if (requiresAuth && to.meta.roles) {
    // 检查角色权限
    if (!role || !to.meta.roles.includes(role)) {
      // 无权限，跳转到有权限的首页
      if (role === 'ADMIN') {
        next('/dashboard')
      } else if (role === 'COMPANY') {
        next('/companies')
      } else if (role === 'DRIVER') {
        next('/vehicles')
      } else {
        next('/companies')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

