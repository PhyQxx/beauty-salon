<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="bs-page-header">
      <h2>首页概览</h2>
      <p class="bs-page-desc">欢迎回来，今日营业数据一览</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="bs-stat-row" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="bs-stat-card">
          <div class="bs-stat-icon pink">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="bs-stat-info">
            <div class="bs-stat-value">{{ stats.todayAppointments }}</div>
            <div class="bs-stat-label">今日预约</div>
          </div>
          <div class="bs-stat-trend up">+12%</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="bs-stat-card">
          <div class="bs-stat-icon blue">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="bs-stat-info">
            <div class="bs-stat-value">{{ stats.todayOrders }}</div>
            <div class="bs-stat-label">今日订单</div>
          </div>
          <div class="bs-stat-trend up">+8%</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="bs-stat-card">
          <div class="bs-stat-icon green">
            <el-icon><User /></el-icon>
          </div>
          <div class="bs-stat-info">
            <div class="bs-stat-value">{{ stats.totalCustomers }}</div>
            <div class="bs-stat-label">客户总数</div>
          </div>
          <div class="bs-stat-trend up">+5%</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="bs-stat-card">
          <div class="bs-stat-icon orange">
            <el-icon><Money /></el-icon>
          </div>
          <div class="bs-stat-info">
            <div class="bs-stat-value">¥{{ formatNumber(stats.todayRevenue) }}</div>
            <div class="bs-stat-label">今日营收</div>
          </div>
          <div class="bs-stat-trend down">-3%</div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="20" style="margin-bottom: 24px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px; font-weight: 600;">
              <el-icon><Grid /></el-icon>
              快捷入口
            </div>
          </template>
          <div class="quick-entry-grid">
            <div
              v-for="item in quickEntries"
              :key="item.path"
              class="quick-entry-item"
              @click="$router.push(item.path)"
            >
              <div class="quick-entry-icon" :style="{ background: item.bgColor, color: item.iconColor }">
                <el-icon :size="24"><component :is="item.icon" /></el-icon>
              </div>
              <span class="quick-entry-label">{{ item.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 营业趋势图 -->
    <el-row :gutter="20" style="margin-bottom: 24px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div style="display: flex; align-items: center; gap: 8px; font-weight: 600;">
                <el-icon><Histogram /></el-icon>
                最近7日营收趋势
              </div>
              <el-radio-group v-model="chartTimeRange" size="small">
                <el-radio-button label="7d">最近7天</el-radio-button>
                <el-radio-button label="30d">最近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="revenue-chart-container">
            <div class="chart-y-axis">
              <span>¥3,000</span>
              <span>¥2,000</span>
              <span>¥1,000</span>
              <span>0</span>
            </div>
            <div class="chart-content">
              <div v-for="(item, index) in revenueData" :key="index" class="chart-bar-wrapper">
                <div class="chart-bar-group">
                  <div class="chart-bar revenue" :style="{ height: (item.revenue / 3000 * 100) + '%' }">
                    <div class="chart-bar-tooltip">营收: ¥{{ item.revenue }}</div>
                  </div>
                  <div class="chart-bar order" :style="{ height: (item.orders * 50 / 3000 * 100) + '%' }">
                    <div class="chart-bar-tooltip">订单: {{ item.orders }}</div>
                  </div>
                </div>
                <div class="chart-label">{{ item.date }}</div>
              </div>
            </div>
            <div class="chart-legend">
              <div class="legend-item"><span class="dot revenue" /> 营收金额</div>
              <div class="legend-item"><span class="dot order" /> 订单数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日预约列表 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div style="display: flex; align-items: center; gap: 8px; font-weight: 600;">
                <el-icon><List /></el-icon>
                今日预约
              </div>
              <el-button text type="primary" @click="$router.push('/appointment')">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="todayAppointments" style="width: 100%" size="small">
            <el-table-column prop="time" label="时间" width="100" />
            <el-table-column prop="customer" label="客户" />
            <el-table-column prop="service" label="服务项目" />
            <el-table-column prop="beautician" label="美容师" width="100" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.statusType" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px; font-weight: 600;">
              <el-icon><Bell /></el-icon>
              通知公告
            </div>
          </template>
          <div class="notice-list">
            <div v-for="(notice, index) in notices" :key="index" class="notice-item">
              <div class="notice-dot" />
              <div class="notice-content">
                <p class="notice-title">{{ notice.title }}</p>
                <p class="notice-time">{{ notice.time }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Calendar, ShoppingCart, User, Money, Grid,
  List, ArrowRight, Bell, Plus, Ticket, Timer,
  Present, FirstAidKit, Histogram, TrendCharts
} from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'

const loading = ref(false)
const chartTimeRange = ref('7d')
const stats = ref({
  todayAppointments: 0,
  todayOrders: 0,
  totalCustomers: 0,
  todayRevenue: 0
})

const revenueData = ref([
  { date: '05-23', revenue: 2100, orders: 12 },
  { date: '05-24', revenue: 1850, orders: 10 },
  { date: '05-25', revenue: 2400, orders: 15 },
  { date: '05-26', revenue: 1200, orders: 8 },
  { date: '05-27', revenue: 2800, orders: 18 },
  { date: '05-28', revenue: 1500, orders: 9 },
  { date: '今日', revenue: 2200, orders: 14 }
])

const quickEntries = [
  { label: '新建预约', path: '/appointment', icon: Plus, bgColor: '#fef1f3', iconColor: '#e85d75' },
  { label: '新增客户', path: '/customer', icon: User, bgColor: '#eff6ff', iconColor: '#4a90e2' },
  { label: '发放优惠券', path: '/coupon', icon: Ticket, bgColor: '#f0fdf4', iconColor: '#52c41a' },
  { label: '排班调整', path: '/schedule', icon: Timer, bgColor: '#fffbeb', iconColor: '#f5a623' },
  { label: '发布活动', path: '/campaign', icon: Present, bgColor: '#fef1f3', iconColor: '#e85d75' },
  { label: '添加服务', path: '/service', icon: FirstAidKit, bgColor: '#f3f4f6', iconColor: '#6b7280' }
]

const todayAppointments = ref([
  { time: '09:30', customer: '王女士', service: '面部深层清洁', beautician: '小林', status: '已完成', statusType: 'success' },
  { time: '10:00', customer: '李女士', service: '玻尿酸补水', beautician: '小王', status: '进行中', statusType: 'primary' },
  { time: '11:30', customer: '张女士', service: '光子嫩肤', beautician: '小李', status: '待服务', statusType: 'warning' },
  { time: '14:00', customer: '陈女士', service: '日式美甲', beautician: '小张', status: '待服务', statusType: 'warning' },
  { time: '15:30', customer: '赵女士', service: '身体按摩', beautician: '小林', status: '待服务', statusType: 'warning' }
])

const notices = ref([
  { title: '母亲节特惠活动将于本周五开始', time: '2小时前' },
  { title: '新美容师小李已入职，请完善档案', time: '5小时前' },
  { title: '系统将于今晚22:00进行例行维护', time: '1天前' },
  { title: '本月会员充值优惠活动还剩3天', time: '2天前' }
])

const formatNumber = (num) => {
  if (!num) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    const data = res.data || res
    stats.value = {
      todayAppointments: data.todayAppointments || 0,
      todayOrders: data.todayOrders || 0,
      totalCustomers: data.totalCustomers || 0,
      todayRevenue: data.todayRevenue || 0
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
.dashboard-container {
  padding: 0;
}

/* Revenue Chart */
.revenue-chart-container {
  height: 300px;
  display: flex;
  position: relative;
  padding: 20px 10px 40px 60px;
}

.chart-y-axis {
  position: absolute;
  left: 0;
  top: 20px;
  bottom: 40px;
  width: 50px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #9ca3af;
  font-size: 12px;
  text-align: right;
  border-right: 1px solid #e5e7eb;
  padding-right: 10px;
}

.chart-content {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
}

.chart-bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  position: relative;
}

.chart-bar-group {
  width: 40px;
  height: calc(100% - 20px);
  display: flex;
  align-items: flex-end;
  gap: 4px;
}

.chart-bar {
  flex: 1;
  border-radius: 4px 4px 0 0;
  position: relative;
  transition: all 0.3s ease;
  min-height: 2px;
}

.chart-bar.revenue {
  background: linear-gradient(180deg, #e85d75 0%, #ff8a9b 100%);
}

.chart-bar.order {
  background: linear-gradient(180deg, #4a90e2 0%, #7eb3f1 100%);
}

.chart-bar:hover {
  filter: brightness(1.1);
  transform: scaleX(1.1);
}

.chart-bar-tooltip {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  white-space: nowrap;
  display: none;
  z-index: 10;
}

.chart-bar:hover .chart-bar-tooltip {
  display: block;
}

.chart-label {
  margin-top: 10px;
  font-size: 12px;
  color: #6b7280;
}

.chart-legend {
  position: absolute;
  bottom: 0;
  right: 20px;
  display: flex;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #4b5563;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.dot.revenue { background: #e85d75; }
.dot.order { background: #4a90e2; }

.quick-entry-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.quick-entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.quick-entry-item:hover {
  background: #f8fafc;
  transform: translateY(-2px);
}

.quick-entry-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.quick-entry-item:hover .quick-entry-icon {
  transform: scale(1.05);
}

.quick-entry-label {
  font-size: 13px;
  color: #4b5563;
  font-weight: 500;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e85d75;
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-size: 14px;
  color: #1f2937;
  margin: 0 0 4px 0;
  line-height: 1.5;
}

.notice-time {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

@media (max-width: 1200px) {
  .quick-entry-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
