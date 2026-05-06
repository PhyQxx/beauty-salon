<template>
  <view :class="['coupon-card', statusClass]">
    <view class="coupon-left">
      <view class="coupon-value">
        <text class="coupon-unit">¥</text>
        <text class="coupon-amount">{{ coupon.discountAmount || coupon.discount || 0 }}</text>
      </view>
      <view class="coupon-condition">{{ conditionText }}</view>
    </view>
    
    <view class="coupon-right">
      <view class="coupon-header">
        <text class="coupon-name">{{ coupon.name || '优惠券' }}</text>
        <text :class="['coupon-status', statusClass]">{{ statusText }}</text>
      </view>
      <view class="coupon-desc">{{ coupon.description || '全场通用优惠券' }}</view>
      <view class="coupon-footer">
        <text class="coupon-time">{{ timeText }}</text>
        <view v-if="canUse" class="coupon-action" @click="handleUse">
          立即使用
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  coupon: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['use'])

const statusText = computed(() => {
  const map = { 0: '未使用', 1: '已使用', 2: '已过期' }
  return map[props.coupon.status] || '未知'
})

const statusClass = computed(() => {
  if (props.coupon.status === 2) return 'expired'
  if (props.coupon.status === 1) return 'used'
  return 'available'
})

const conditionText = computed(() => {
  if (!props.coupon.minAmount || props.coupon.minAmount <= 0) {
    return '无门槛'
  }
  return `满${props.coupon.minAmount}可用`
})

const timeText = computed(() => {
  if (props.coupon.expireTime) {
    return `有效期至 ${props.coupon.expireTime}`
  }
  if (props.coupon.startTime && props.coupon.endTime) {
    return `${props.coupon.startTime} - ${props.coupon.endTime}`
  }
  return '永久有效'
})

const canUse = computed(() => props.coupon.status === 0)

function handleUse() {
  emit('use', props.coupon)
}
</script>

<style lang="scss" scoped>
.coupon-card {
  display: flex;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
  margin-bottom: 20rpx;
  
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
</style>
