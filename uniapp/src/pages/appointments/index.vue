<template>
  <view class="page">
    <!-- Tabs -->
    <view class="tabs">
      <view 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['tab', { active: activeTab === tab.key }]"
        @click="changeTab(tab.key)"
      >
        {{ tab.name }}
      </view>
    </view>
    
    <!-- List -->
    <scroll-view 
      scroll-y 
      class="page-content"
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="appointments.length === 0 && !loading" class="empty">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无预约记录</text>
        <text class="empty-hint">快去预约服务吧</text>
      </view>
      
      <view v-else class="appointment-list">
        <view v-for="item in appointments" :key="item.id" class="appointment-card">
          <view class="appointment-header">
            <text class="appointment-no">No.{{ item.appointmentNo || item.id }}</text>
            <text :class="['appointment-status', getStatusClass(item.status)]">
              {{ getStatusText(item.status) }}
            </text>
          </view>
          
          <view class="appointment-services">
            <text v-for="(s, index) in getServices(item)" :key="index" class="service-tag">
              {{ s.serviceName || s.name }}
            </text>
          </view>
          
          <view class="appointment-details">
            <view class="detail-item">
              <text class="detail-icon">🧑</text>
              <text>{{ item.beauticianName || item.beautician?.name || '待分配' }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-icon">📅</text>
              <text>{{ item.appointmentDate || item.date }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-icon">🕐</text>
              <text>{{ item.startTime || item.time }}</text>
            </view>
          </view>
          
          <view v-if="item.status === 0 || item.status === 'pending'" class="appointment-actions">
            <button class="action-btn cancel" @click="cancelAppointment(item)">取消预约</button>
          </view>
        </view>
      </view>
      
      <view v-if="loadingMore" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="noMore && appointments.length > 0" class="loading-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAppointmentList, cancelAppointment as cancelApi } from '@/api/appointment'

const tabs = [
  { key: 'all', name: '全部' },
  { key: '0', name: '待服务' },
  { key: '1', name: '服务中' },
  { key: '2', name: '已完成' },
  { key: '3', name: '已取消' }
]

const activeTab = ref('all')
const appointments = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const page = ref(1)
const limit = ref(10)
const noMore = ref(false)

function getStatusText(status) {
  const map = {
    0: '待确认',
    1: '服务中',
    2: '已完成',
    3: '已取消',
    4: '失约'
  }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = {
    0: 'pending',
    1: 'processing',
    2: 'completed',
    3: 'cancelled',
    4: 'no-show'
  }
  return map[status] || 'pending'
}

function getServices(item) {
  // 支持多种数据格式
  if (item.services) return item.services
  if (item.serviceItems) return item.serviceItems
  if (item.serviceName) return [item]
  return []
}

function changeTab(key) {
  activeTab.value = key
  page.value = 1
  noMore.value = false
  appointments.value = []
  fetchAppointments(true)
}

async function fetchAppointments(reset = false) {
  if (loading.value) return
  
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  
  loading.value = true
  
  try {
    const params = {
      page: page.value,
      limit: limit.value
    }
    
    // 根据状态筛选
    if (activeTab.value !== 'all') {
      params.status = parseInt(activeTab.value)
    }
    
    const res = await getAppointmentList(params)
    
    if (res.data) {
      const list = res.data.list || res.data || []
      if (list.length < limit.value) {
        noMore.value = true
      }
      appointments.value = reset ? list : [...appointments.value, ...list]
      page.value++
    }
  } catch (e) {
    console.error('Failed to fetch appointments:', e)
    // 降级使用静态数据
    if (reset) {
      loadStaticAppointments()
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    loadingMore.value = true
    fetchAppointments().finally(() => {
      loadingMore.value = false
    })
  }
}

async function onRefresh() {
  refreshing.value = true
  await fetchAppointments(true)
}

async function cancelAppointment(item) {
  uni.showModal({
    title: '确认取消',
    content: '确定要取消此预约吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelApi(item.id, '用户取消')
          uni.showToast({ title: '已取消预约', icon: 'success' })
          fetchAppointments(true)
        } catch (e) {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    }
  })
}

function loadStaticAppointments() {
  appointments.value = [
    {
      id: 1,
      appointmentNo: 'BK20260502001',
      status: 0,
      beauticianName: '小林',
      appointmentDate: '2026-05-03',
      startTime: '14:00',
      services: [
        { serviceName: '面部深层清洁' },
        { serviceName: '玻尿酸补水' }
      ]
    },
    {
      id: 2,
      appointmentNo: 'BK20260501001',
      status: 2,
      beauticianName: '小王',
      appointmentDate: '2026-05-01',
      startTime: '10:00',
      services: [
        { serviceName: '日式美甲' }
      ]
    }
  ]
  noMore.value = true
}

onMounted(() => {
  fetchAppointments(true)
})
</script>

<style lang="scss" scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-light);
}

.tabs {
  display: flex;
  padding: 20rpx;
  background: #fff;
  gap: 16rpx;
  overflow-x: auto;
  flex-shrink: 0;
  
  .tab {
    padding: 12rpx 28rpx;
    border-radius: 30rpx;
    font-size: 26rpx;
    color: var(--text-gray);
    background: var(--bg-light);
    white-space: nowrap;
    
    &.active {
      background: var(--primary);
      color: #fff;
    }
  }
}

.page-content {
  flex: 1;
  padding: 20rpx;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
  
  .empty-icon {
    font-size: 100rpx;
    margin-bottom: 24rpx;
  }
  
  .empty-text {
    font-size: 30rpx;
    color: var(--text-dark);
    margin-bottom: 12rpx;
  }
  
  .empty-hint {
    font-size: 26rpx;
    color: var(--text-gray);
  }
}

.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.appointment-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  
  .appointment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
    
    .appointment-no {
      font-size: 24rpx;
      color: var(--text-gray);
    }
    
    .appointment-status {
      font-size: 24rpx;
      padding: 6rpx 20rpx;
      border-radius: 20rpx;
      
      &.pending {
        background: #fff7e6;
        color: #fa8c16;
      }
      
      &.processing {
        background: #e6f7ff;
        color: #1890ff;
      }
      
      &.completed {
        background: #e6f7e6;
        color: #52c41a;
      }
      
      &.cancelled {
        background: #f5f5f5;
        color: #999;
      }
      
      &.no-show {
        background: #fff1f0;
        color: #ff4d4f;
      }
    }
  }
  
  .appointment-services {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 16rpx;
    
    .service-tag {
      padding: 8rpx 20rpx;
      background: var(--bg-light);
      border-radius: 8rpx;
      font-size: 24rpx;
      color: var(--text-dark);
    }
  }
  
  .appointment-details {
    display: flex;
    flex-wrap: wrap;
    gap: 24rpx;
    
    .detail-item {
      display: flex;
      align-items: center;
      font-size: 24rpx;
      color: var(--text-gray);
      
      .detail-icon {
        margin-right: 8rpx;
      }
    }
  }
  
  .appointment-actions {
    margin-top: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid var(--border);
    display: flex;
    justify-content: flex-end;
    
    .action-btn {
      padding: 12rpx 32rpx;
      border-radius: 30rpx;
      font-size: 26rpx;
      
      &.cancel {
        background: #fff;
        color: #ff4d4f;
        border: 1rpx solid #ff4d4f;
      }
    }
  }
}

.loading-more {
  text-align: center;
  padding: 30rpx;
  color: var(--text-gray);
  font-size: 24rpx;
}
</style>
