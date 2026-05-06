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
      <view v-if="coupons.length === 0 && !loading" class="empty">
        <text class="empty-icon">🎫</text>
        <text class="empty-text">暂无优惠券</text>
        <text class="empty-hint">快去领取优惠券吧</text>
      </view>
      
      <view v-else class="coupon-list">
        <view 
          v-for="coupon in coupons" 
          :key="coupon.id" 
          :class="['coupon-card', getCouponClass(coupon)]"
        >
          <view class="coupon-left">
            <view class="coupon-value">
              <text class="coupon-unit">¥</text>
              <text class="coupon-amount">{{ coupon.discountAmount || coupon.discount || 0 }}</text>
            </view>
            <view class="coupon-condition">{{ getConditionText(coupon) }}</view>
          </view>
          
          <view class="coupon-right">
            <view class="coupon-header">
              <text class="coupon-name">{{ coupon.name || '优惠券' }}</text>
              <text :class="['coupon-status', getStatusClass(coupon.status)]">
                {{ getStatusText(coupon.status) }}
              </text>
            </view>
            <view class="coupon-desc">{{ coupon.description || '全场通用优惠券' }}</view>
            <view class="coupon-footer">
              <text class="coupon-time">{{ formatTime(coupon) }}</text>
              <view v-if="canUse(coupon)" class="coupon-action" @click="useCoupon(coupon)">
                立即使用
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <view v-if="loadingMore" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="noMore && coupons.length > 0" class="loading-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCouponList, getCustomerCoupons, receiveCoupon } from '@/api/coupon'

const tabs = [
  { key: 'available', name: '可用' },
  { key: 'used', name: '已用' },
  { key: 'expired', name: '已过期' }
]

const activeTab = ref('available')
const coupons = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const refreshing = ref(false)
const page = ref(1)
const limit = ref(10)
const noMore = ref(false)

function getCouponClass(coupon) {
  if (coupon.status === 2) return 'expired'
  if (coupon.status === 1) return 'used'
  return 'available'
}

function getStatusText(status) {
  const map = { 0: '未使用', 1: '已使用', 2: '已过期' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = { 0: 'available', 1: 'used', 2: 'expired' }
  return map[status] || 'available'
}

function getConditionText(coupon) {
  if (!coupon.minAmount || coupon.minAmount <= 0) {
    return '无门槛'
  }
  return `满${coupon.minAmount}可用`
}

function formatTime(coupon) {
  if (coupon.expireTime) {
    return `有效期至 ${coupon.expireTime}`
  }
  if (coupon.startTime && coupon.endTime) {
    return `${coupon.startTime} - ${coupon.endTime}`
  }
  return '永久有效'
}

function canUse(coupon) {
  return coupon.status === 0
}

function changeTab(key) {
  activeTab.value = key
  page.value = 1
  noMore.value = false
  coupons.value = []
  fetchCoupons(true)
}

async function fetchCoupons(reset = false) {
  if (loading.value) return
  
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  
  loading.value = true
  
  try {
    const customerId = uni.getStorageSync('customerId')
    
    if (customerId) {
      // 客户优惠券列表
      const statusMap = { available: 0, used: 1, expired: 2 }
      const status = statusMap[activeTab.value]
      
      const res = await getCustomerCoupons(customerId, status)
      
      if (res.data) {
        coupons.value = res.data.list || res.data || []
        noMore.value = true
      }
    } else {
      // 公开优惠券列表
      const res = await getCouponList({
        page: page.value,
        limit: limit.value,
        status: activeTab.value === 'available' ? 0 : undefined
      })
      
      if (res.data) {
        const list = res.data.list || res.data || []
        if (list.length < limit.value) {
          noMore.value = true
        }
        coupons.value = reset ? list : [...coupons.value, ...list]
        page.value++
      }
    }
  } catch (e) {
    console.error('Failed to fetch coupons:', e)
    if (reset) {
      loadStaticCoupons()
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    loadingMore.value = true
    fetchCoupons().finally(() => {
      loadingMore.value = false
    })
  }
}

async function onRefresh() {
  refreshing.value = true
  await fetchCoupons(true)
}

async function useCoupon(coupon) {
  uni.showToast({ title: '请到预约流程中使用', icon: 'none' })
}

function loadStaticCoupons() {
  coupons.value = [
    {
      id: 1,
      name: '新人专享券',
      discountAmount: 50,
      minAmount: 200,
      description: '限首次预约使用',
      status: 0,
      expireTime: '2026-05-31'
    },
    {
      id: 2,
      name: '满减券',
      discountAmount: 30,
      minAmount: 100,
      description: '全场通用',
      status: 0,
      expireTime: '2026-06-15'
    },
    {
      id: 3,
      name: '折扣券',
      discountAmount: 20,
      minAmount: 0,
      description: '指定服务项目',
      status: 1,
      expireTime: '2026-04-30'
    }
  ]
  noMore.value = true
}

onMounted(() => {
  fetchCoupons(true)
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
  flex-shrink: 0;
  
  .tab {
    flex: 1;
    padding: 12rpx 0;
    text-align: center;
    border-radius: 30rpx;
    font-size: 28rpx;
    color: var(--text-gray);
    background: var(--bg-light);
    
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

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.coupon-card {
  display: flex;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
  
  &.expired {
    opacity: 0.6;
    
    .coupon-left {
      background: #999;
    }
  }
  
  &.used {
    opacity: 0.6;
    
    .coupon-left {
      background: #ccc;
    }
  }
  
  .coupon-left {
    width: 220rpx;
    background: var(--primary);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30rpx 20rpx;
    
    .coupon-value {
      color: #fff;
      display: flex;
      align-items: baseline;
      
      .coupon-unit {
        font-size: 28rpx;
        margin-right: 4rpx;
      }
      
      .coupon-amount {
        font-size: 56rpx;
        font-weight: 700;
      }
    }
    
    .coupon-condition {
      font-size: 22rpx;
      color: rgba(255, 255, 255, 0.8);
      margin-top: 8rpx;
    }
  }
  
  .coupon-right {
    flex: 1;
    padding: 20rpx 24rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    
    .coupon-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .coupon-name {
        font-size: 28rpx;
        font-weight: 600;
        color: var(--text-dark);
      }
      
      .coupon-status {
        font-size: 22rpx;
        padding: 4rpx 12rpx;
        border-radius: 12rpx;
        
        &.available {
          background: var(--primary-light);
          color: var(--primary);
        }
        
        &.used {
          background: #f5f5f5;
          color: #999;
        }
        
        &.expired {
          background: #fff1f0;
          color: #ff4d4f;
        }
      }
    }
    
    .coupon-desc {
      font-size: 24rpx;
      color: var(--text-gray);
      margin-top: 8rpx;
    }
    
    .coupon-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12rpx;
      
      .coupon-time {
        font-size: 22rpx;
        color: var(--text-gray);
      }
      
      .coupon-action {
        padding: 8rpx 24rpx;
        background: var(--primary);
        color: #fff;
        border-radius: 20rpx;
        font-size: 24rpx;
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
