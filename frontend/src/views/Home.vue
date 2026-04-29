<template>
  <div class="home-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">美容沙龙</div>
        <el-menu default-active="1" class="el-menu-vertical" :router="true">
          <el-menu-item index="/home">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/customer">
            <span>客户管理</span>
          </el-menu-item>
          <el-menu-item index="/appointment">
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/service">
            <span>服务项目</span>
          </el-menu-item>
          <el-menu-item index="/membership-card">
            <span>会员卡</span>
          </el-menu-item>
          <el-menu-item index="/recharge">
            <span>充值管理</span>
          </el-menu-item>
          <el-menu-item index="/beautician">
            <span>美容师</span>
          </el-menu-item>
          <el-menu-item index="/campaign">
            <span>活动管理</span>
          </el-menu-item>
          <el-menu-item index="/coupon">
            <span>优惠券管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <!-- 头部 -->
        <el-header>
          <span>欢迎使用美容沙龙管理系统</span>
          <el-dropdown>
            <span class="el-dropdown-link">
              管理员<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-header>

        <!-- 主内容区 -->
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/dashboard'

const stats = ref({
  todayAppointments: 0,
  todayOrders: 0,
  totalCustomers: 0,
  todayRevenue: 0
})

const loading = ref(false)

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      stats.value = {
        todayAppointments: res.data.todayAppointments || 0,
        todayOrders: res.data.todayOrders || 0,
        totalCustomers: res.data.totalCustomers || 0,
        todayRevenue: res.data.todayRevenue || 0
      }
    }
  } catch (error) {
    console.error('获取首页数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
