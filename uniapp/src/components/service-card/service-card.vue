<template>
  <view class="service-card" @click="handleClick">
    <view class="service-img">{{ getServiceIcon(service.category) }}</view>
    <view class="service-info">
      <view class="service-name">{{ service.name }}</view>
      <view class="service-desc" v-if="service.description">{{ service.description }}</view>
      <view class="service-meta">
        <text class="service-price">¥{{ service.price }}</text>
        <text class="service-duration">{{ service.duration }}分钟</text>
      </view>
    </view>
    <view v-if="showSelect" class="service-check">
      <view :class="['check-icon', { selected: selected }]">
        <text v-if="selected">✓</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { getServiceIcon } from '@/utils/service'

const props = defineProps({
  service: {
    type: Object,
    required: true
  },
  selected: {
    type: Boolean,
    default: false
  },
  showSelect: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

function handleClick() {
  emit('click', props.service)
}
</script>

<style lang="scss" scoped>
.service-card {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  
  .service-img {
    width: 120rpx;
    height: 120rpx;
    background: var(--bg-light);
    border-radius: 12rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 60rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
  }
  
  .service-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .service-name {
      font-size: 28rpx;
      font-weight: 600;
      color: var(--text-dark);
      margin-bottom: 8rpx;
    }
    
    .service-desc {
      font-size: 22rpx;
      color: var(--text-gray);
      margin-bottom: 8rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .service-meta {
      display: flex;
      align-items: center;
      
      .service-price {
        font-size: 28rpx;
        color: var(--primary);
        font-weight: 600;
        margin-right: 20rpx;
      }
      
      .service-duration {
        font-size: 22rpx;
        color: var(--text-gray);
      }
    }
  }
  
  .service-check {
    display: flex;
    align-items: center;
    margin-left: 16rpx;
    
    .check-icon {
      width: 44rpx;
      height: 44rpx;
      border: 2rpx solid var(--border);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;
      color: #fff;
      
      &.selected {
        background: var(--primary);
        border-color: var(--primary);
      }
    }
  }
}
</style>
