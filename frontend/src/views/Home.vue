<template>
  <div class="home-container">
    <!-- 侧边栏 -->
    <aside class="bs-sidebar">
      <div class="bs-sidebar-logo">
        <div class="logo-icon">美</div>
        <span class="logo-text">美容沙龙</span>
      </div>

      <div class="bs-sidebar-menu">
        <el-menu :default-active="activeMenu" class="el-menu-vertical" :router="true" :collapse="false">
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/customer">
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </el-menu-item>
          <el-menu-item index="/appointment">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/service">
            <el-icon><FirstAidKit /></el-icon>
            <span>服务项目</span>
          </el-menu-item>
          <el-menu-item index="/membership-card">
            <el-icon><CreditCard /></el-icon>
            <span>会员卡</span>
          </el-menu-item>
          <el-menu-item index="/recharge">
            <el-icon><Money /></el-icon>
            <span>充值管理</span>
          </el-menu-item>
          <el-menu-item index="/order">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/beautician">
            <el-icon><UserFilled /></el-icon>
            <span>美容师</span>
          </el-menu-item>
          <el-menu-item index="/schedule">
            <el-icon><Timer /></el-icon>
            <span>排班管理</span>
          </el-menu-item>
          <el-menu-item index="/beautician-timeline">
            <el-icon><View /></el-icon>
            <span>预约看板</span>
          </el-menu-item>
          <el-menu-item index="/campaign">
            <el-icon><Present /></el-icon>
            <span>活动管理</span>
          </el-menu-item>
          <el-menu-item index="/coupon">
            <el-icon><Ticket /></el-icon>
            <span>优惠券管理</span>
          </el-menu-item>

          <div class="bs-sidebar-divider" />

          <el-menu-item index="/system/permission">
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
          <el-menu-item index="/system/log">
            <el-icon><Document /></el-icon>
            <span>日志管理</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="bs-sidebar-footer">
        Beauty Salon v1.0
      </div>
    </aside>

    <!-- 主区域 -->
    <div style="flex: 1; display: flex; flex-direction: column; overflow: hidden;">
      <!-- 头部 -->
      <header class="bs-header">
        <div class="bs-header-breadcrumb">
          <el-icon style="margin-right: 4px; vertical-align: middle;"><Location /></el-icon>
          {{ pageTitle }}
        </div>
        <div class="bs-header-actions">
          <button class="bs-header-icon-btn" title="通知">
            <el-icon><Bell /></el-icon>
          </button>
          <button class="bs-header-icon-btn" title="设置">
            <el-icon><Setting /></el-icon>
          </button>
          <el-dropdown>
            <div class="bs-header-user">
              <div class="user-avatar">管</div>
              <span class="user-name">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="handleChangePassword">
                  <el-icon><Key /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 主内容区 -->
      <main class="bs-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Cookies from 'js-cookie'
import {
  Odometer, User, Calendar, FirstAidKit, CreditCard,
  Money, ShoppingCart, UserFilled, Timer, Present,
  Ticket, Lock, Document, Bell, Setting, ArrowDown,
  Location, Key, SwitchButton, View
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': '首页概览',
    '/customer': '客户管理',
    '/appointment': '预约管理',
    '/service': '服务项目',
    '/membership-card': '会员卡管理',
    '/recharge': '充值管理',
    '/order': '订单管理',
    '/beautician': '美容师管理',
    '/schedule': '排班管理',
    '/beautician-timeline': '预约看板',
    '/campaign': '活动管理',
    '/coupon': '优惠券管理',
    '/system/permission': '权限管理',
    '/system/log': '日志管理'
  }
  return titles[route.path] || '美容沙龙管理系统'
})

const handleProfile = () => {
  ElMessage.info('个人中心功能开发中')
}

const handleChangePassword = () => {
  ElMessage.info('修改密码功能开发中')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    Cookies.remove('beauty_salon_token', { path: '/' })
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
}
</style>
