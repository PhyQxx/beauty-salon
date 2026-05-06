<template>
  <view class="beautician-card" @click="handleClick">
    <view class="beautician-avatar">
      <image v-if="beautician.avatar" :src="beautician.avatar" mode="aspectFill" />
      <text v-else>{{ getDefaultAvatar(beautician.gender) }}</text>
    </view>
    <view class="beautician-info">
      <view class="beautician-header">
        <text class="beautician-name">{{ beautician.name }}</text>
        <text class="beautician-level">{{ getLevelText(beautician.level) }}</text>
      </view>
      <view class="beautician-rating" v-if="beautician.rating">
        <text class="stars">★★★★★</text>
        <text class="rating-value">{{ beautician.rating }}</text>
      </view>
      <view class="beautician-services" v-if="beautician.specialties">
        {{ beautician.specialties }}
      </view>
      <view class="beautician-stats" v-if="showStats">
        <text>服务 {{ beautician.serviceCount || 0 }} 次</text>
      </view>
    </view>
    <view v-if="showSelect" class="beautician-check">
      <view :class="['check-icon', { selected: selected }]">
        <text v-if="selected">✓</text>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  beautician: {
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
  },
  showStats: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['click'])

function getDefaultAvatar(gender) {
  return gender === 1 ? '👨' : '👩'
}

function getLevelText(level) {
  const levels = { 1: '初级', 2: '中级', 3: '高级', 4: '首席' }
  return levels[level] || '技师'
}

function handleClick() {
  emit('click', props.beautician)
}
</script>

<style lang="scss" scoped>
.beautician-card {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  
  .beautician-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background: var(--bg-light);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 60rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
    overflow: hidden;
    
    image, text {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  .beautician-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .beautician-header {
      display: flex;
      align-items: center;
      margin-bottom: 8rpx;
      
      .beautician-name {
        font-size: 30rpx;
        font-weight: 600;
        color: var(--text-dark);
        margin-right: 12rpx;
      }
      
      .beautician-level {
        font-size: 20rpx;
        padding: 4rpx 12rpx;
        background: var(--primary-light);
        color: var(--primary);
        border-radius: 12rpx;
      }
    }
    
    .beautician-rating {
      display: flex;
      align-items: center;
      margin-bottom: 8rpx;
      
      .stars {
        font-size: 20rpx;
        color: #ffb800;
        margin-right: 8rpx;
      }
      
      .rating-value {
        font-size: 24rpx;
        color: var(--text-dark);
      }
    }
    
    .beautician-services {
      font-size: 22rpx;
      color: var(--text-gray);
      margin-bottom: 8rpx;
    }
    
    .beautician-stats {
      font-size: 22rpx;
      color: var(--text-gray);
    }
  }
  
  .beautician-check {
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
