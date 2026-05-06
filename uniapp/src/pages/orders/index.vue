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
      <view v-if="orders.length === 0 && !loading" class="empty">
        <text class="empty-icon">📦</text>
        <text class="empty-text">暂无订单</text>
        <text class="empty-hint">快去预约服务吧</text>
      </view>
      
      <view v-else class="order-list">
        <view v-for="order in orders" :key="order.id" class="order-card">
          <view class="order-header">
            <text class="order-no">No.{{ order.orderNo || order.id }}</text>
            <text :class="['order-status', getStatusClass(order.status)]">
              {{ getStatusText(order.status) }}
            </text>
          </view>
          
          <view class="order-services">
            <view 
              v-for="(item, index) in getItems(order)" 
              :key="index" 
              class="order-item"
            >
              <text class="item-name">{{ item.name || item.serviceName || '服务项目' }}</text>
              <text class="item-qty">x{{ item.quantity || 1 }}</text>
              <text class="item-price">¥{{ item.price || item.amount || 0 }}</text>
            </view>
          </view>
          
          <view class="order-footer">
            <view class="order-info">
              <text class="order-date">{{ formatDate(order.createTime || order.orderDate) }}</text>
              <text class="order-total">合计: ¥{{ order.totalAmount || order.amount || 0 }}</text>
            </view>
            
            <view class="order-actions">
              <view v-if="canPay(order)" class="action-btn pay" @click="payOrder(order)">
                去支付
              </view>
              <view v-if="canCancel(order)" class="action-btn cancel" @click="cancelOrder(order)">
                取消
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <view v-if="loadingMore" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="noMore && orders.length > 0" class="loading-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderByCustomerId, cancelOrder as cancelOrderApi } from '@/api/order'
import { useMemberStore } from '@/store'

const memberStore = useMemberStore()

const tabs = [
  { key: 'all', name: '全部' },
  { key: '0', name: '待支付' },
  { key: '1', name: '已支付' },
  { key: '2', name: '已完成' },
  { key: '3', name: '已取消' }
]

const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const page = ref(1)
const limit = ref(10)
const noMore = ref(false)

function getStatusText(status) {
  const map = {
    0: '待支付',
    1: '已支付',
    2: '已完成',
    3: '已取消',
    4: '退款中',
    5: '已退款'
  }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = {
    0: 'pending',
    1: 'paid',
    2: 'completed',
    3: 'cancelled',
    4: 'refunding',
    5: 'refunded'
  }
  return map[status] || 'pending'
}

function getItems(order) {
  if (order.items) return order.items
  if (order.orderItems) return order.orderItems
  return [{ name: order.serviceName || '服务', amount: order.totalAmount || order.amount }]
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function canPay(order) {
  return order.status === 0
}

function canCancel(order) {
  return order.status === 0 || order.status === 1
}

function changeTab(key) {
  activeTab.value = key
  page.value = 1
  noMore.value = false
  orders.value = []
  fetchOrders(true)
}

async function fetchOrders(reset = false) {
  if (loading.value) return
  
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  
  loading.value = true
  
  try {
    const customerId = uni.getStorageSync('customerId')
    
    if (customerId) {
      const res = await getOrderByCustomerId(customerId)
      
      if (res.data) {
        let list = res.data.list || res.data || []
        
        // 根据状态筛选
        if (activeTab.value !== 'all') {
          list = list.filter(o => o.status === parseInt(activeTab.value))
        }
        
        orders.value = list
        noMore.value = true
      }
    }
  } catch (e) {
    console.error('Failed to fetch orders:', e)
    if (reset) {
      loadStaticOrders()
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    loadingMore.value = true
    fetchOrders().finally(() => {
      loadingMore.value = false
    })
  }
}

async function onRefresh() {
  refreshing.value = true
  await fetchOrders(true)
}

async function payOrder(order) {
  uni.showToast({ title: '支付功能开发中', icon: 'none' })
}

async function cancelOrder(order) {
  uni.showModal({
    title: '确认取消',
    content: '确定要取消此订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrderApi(order.id, '用户取消')
          uni.showToast({ title: '已取消', icon: 'success' })
          fetchOrders(true)
        } catch (e) {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    }
  })
}

function loadStaticOrders() {
  orders.value = [
    {
      id: 1,
      orderNo: 'ORD20260502001',
      status: 0,
      totalAmount: 466,
      createTime: '2026-05-02 10:30:00',
      items: [
        { name: '面部深层清洁', quantity: 1, price: 198 },
        { name: '玻尿酸补水', quantity: 1, price: 268 }
      ]
    },
    {
      id: 2,
      orderNo: 'ORD20260501001',
      status: 2,
      totalAmount: 398,
      createTime: '2026-05-01 14:00:00',
      items: [
        { name: '光子嫩肤', quantity: 1, price: 398 }
      ]
    }
  ]
  noMore.value = true
}

onMounted(() => {
  fetchOrders(true)
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
    padding: 12rpx 24rpx;
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.order-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
    
    .order-no {
      font-size: 24rpx;
      color: var(--text-gray);
    }
    
    .order-status {
      font-size: 24rpx;
      padding: 6rpx 20rpx;
      border-radius: 20rpx;
      
      &.pending {
        background: #fff7e6;
        color: #fa8c16;
      }
      
      &.paid {
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
      
      &.refunding {
        background: #fff7e6;
        color: #fa8c16;
      }
      
      &.refunded {
        background: #f5f5f5;
        color: #999;
      }
    }
  }
  
  .order-services {
    border-bottom: 1rpx solid var(--border);
    padding-bottom: 16rpx;
    margin-bottom: 16rpx;
  }
  
  .order-item {
    display: flex;
    align-items: center;
    padding: 8rpx 0;
    
    .item-name {
      flex: 1;
      font-size: 26rpx;
      color: var(--text-dark);
    }
    
    .item-qty {
      font-size: 24rpx;
      color: var(--text-gray);
      margin-right: 20rpx;
    }
    
    .item-price {
      font-size: 26rpx;
      color: var(--text-dark);
    }
  }
  
  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .order-info {
      display: flex;
      flex-direction: column;
      
      .order-date {
        font-size: 22rpx;
        color: var(--text-gray);
      }
      
      .order-total {
        font-size: 28rpx;
        color: var(--text-dark);
        font-weight: 600;
        margin-top: 4rpx;
      }
    }
    
    .order-actions {
      display: flex;
      gap: 16rpx;
      
      .action-btn {
        padding: 12rpx 32rpx;
        border-radius: 30rpx;
        font-size: 26rpx;
        
        &.pay {
          background: var(--primary);
          color: #fff;
        }
        
        &.cancel {
          background: #fff;
          color: var(--text-gray);
          border: 1rpx solid var(--border);
        }
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
