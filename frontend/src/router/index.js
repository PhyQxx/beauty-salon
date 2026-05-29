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
import Order from '@/views/Order.vue'
import Product from '@/views/Product.vue'
import Inventory from '@/views/Inventory.vue'
import Beautician from '@/views/Beautician.vue'
import Schedule from '@/views/Schedule.vue'
import BeauticianTimeline from '@/views/BeauticianTimeline.vue'
import Permission from '@/views/system/Permission.vue'
import SystemLog from '@/views/system/SystemLog.vue'
import Store from '@/views/system/Store.vue'

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
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Home,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: '/appointment',
        name: 'Appointment',
        component: Appointment
      },
      {
        path: '/customer',
        name: 'Customer',
        component: Customer
      },
      {
        path: '/campaign',
        name: 'Campaign',
        component: Campaign
      },
      {
        path: '/coupon',
        name: 'Coupon',
        component: Coupon
      },
      {
        path: '/service',
        name: 'Service',
        component: Service
      },
      {
        path: '/membership-card',
        name: 'MembershipCard',
        component: MembershipCard
      },
      {
        path: '/recharge',
        name: 'Recharge',
        component: Recharge
      },
      {
        path: '/order',
        name: 'Order',
        component: Order
      },
      {
        path: '/product',
        name: 'Product',
        component: Product
      },
      {
        path: '/inventory',
        name: 'Inventory',
        component: Inventory
      },
      {
        path: '/beautician',
        name: 'Beautician',
        component: Beautician
      },
      {
        path: '/schedule',
        name: 'Schedule',
        component: Schedule
      },
      {
        path: '/beautician-timeline',
        name: 'BeauticianTimeline',
        component: BeauticianTimeline
      },
      {
        path: '/system/permission',
        name: 'Permission',
        component: Permission
      },
      {
        path: '/system/log',
        name: 'SystemLog',
        component: SystemLog
      },
      {
        path: '/system/store',
        name: 'Store',
        component: Store
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(authGuard)

export default router
