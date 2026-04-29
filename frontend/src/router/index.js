/**
 * 路由配置
 * 
 * @author BeautySalon Team
 */
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import Dashboard from '@/views/Dashboard.vue'
import Appointment from '@/views/Appointment.vue'
import Customer from '@/views/Customer.vue'
import Campaign from '@/views/Campaign.vue'
import Coupon from '@/views/Coupon.vue'
import Service from '@/views/Service.vue'
import MembershipCard from '@/views/MembershipCard.vue'
import Recharge from '@/views/Recharge.vue'
import Beautician from '@/views/Beautician.vue'
import Permission from '@/views/system/Permission.vue'
import SystemLog from '@/views/system/SystemLog.vue'

// 路由守卫：简单检查是否已登录
const authGuard = (to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
}

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/appointment',
    name: 'Appointment',
    component: Appointment,
    meta: { requiresAuth: true }
  },
  {
    path: '/customer',
    name: 'Customer',
    component: Customer,
    meta: { requiresAuth: true }
  },
  {
    path: '/campaign',
    name: 'Campaign',
    component: Campaign,
    meta: { requiresAuth: true }
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: Coupon,
    meta: { requiresAuth: true }
  },
  {
    path: '/service',
    name: 'Service',
    component: Service,
    meta: { requiresAuth: true }
  },
  {
    path: '/membership-card',
    name: 'MembershipCard',
    component: MembershipCard,
    meta: { requiresAuth: true }
  },
  {
    path: '/recharge',
    name: 'Recharge',
    component: Recharge,
    meta: { requiresAuth: true }
  },
  {
    path: '/beautician',
    name: 'Beautician',
    component: Beautician,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/permission',
    name: 'Permission',
    component: Permission,
    meta: { requiresAuth: true }
  },
  {
    path: '/system/log',
    name: 'SystemLog',
    component: SystemLog,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(authGuard)

export default router
