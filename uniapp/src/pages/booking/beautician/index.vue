<template>
  <view class="page">
    <scroll-view scroll-y class="page-content" @scrolltolower="loadMore">
      <view class="beautician-list">
        <view 
          v-for="beautician in beauticians" 
          :key="beautician.id" 
          :class="['beautician-item', { selected: bookingStore.selectedBeautician?.id === beautician.id }]"
          @click="selectBeautician(beautician)"
        >
          <view class="beautician-avatar">
            <image v-if="beautician.avatar" :src="beautician.avatar" mode="aspectFill" />
            <text v-else>{{ getDefaultAvatar(beautician.gender) }}</text>
          </view>
          <view class="beautician-info">
            <view class="beautician-header">
              <view class="beautician-name">{{ beautician.name }}</view>
              <view class="beautician-level">{{ getLevelText(beautician.level) }}</view>
            </view>
            <view class="beautician-rating">
              <text class="stars">{{ getStars(beautician.rating) }}</text>
              <text>{{ beautician.rating || '5.0' }}</text>
              <text class="divider">|</text>
              <text>已服务 {{ beautician.serviceCount || 0 }} 次</text>
            </view>
            <view class="beautician-specialty">
              <text v-for="(tag, index) in getSpecialties(beautician.specialty)" :key="index" class="tag">
                {{ tag }}
              </text>
            </view>
            <view v-if="beautician.introduction" class="beautician-intro">
              {{ beautician.introduction }}
            </view>
          </view>
          <view class="beautician-check">
            <text v-if="bookingStore.selectedBeautician?.id === beautician.id">✓</text>
          </view>
        </view>
        
        <view v-if="loading" class="loading-more">
          <text>加载中...</text>
        </view>
        <view v-if="noMore && beauticians.length > 0" class="loading-more">
          <text>没有更多了</text>
        </view>
      </view>
    </scroll-view>
    
    <view class="bottom-action">
      <button 
        class="action-btn" 
        :disabled="!bookingStore.selectedBeautician"
        @click="goNext"
      >
        下一步：选择时间
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBookingStore } from '@/store'
import { getBeauticianList, getActiveBeauticians } from '@/api/beautician'
import { getLevelText, getStars, getDefaultAvatar } from '@/utils/service'

const bookingStore = useBookingStore()

const beauticians = ref([])
const loading = ref(false)
const page = ref(1)
const limit = ref(20)
const noMore = ref(false)

function getDefaultAvatar(gender) {
  if (gender === 1) return '👨'
  return '👩'
}

function getStars(rating) {
  const r = parseFloat(rating) || 5
  const full = Math.floor(r)
  return '★'.repeat(full) + '☆'.repeat(5 - full)
}

function getSpecialties(specialty) {
  if (!specialty) return []
  if (typeof specialty === 'string') {
    return specialty.split(',').map(s => s.trim()).filter(Boolean)
  }
  return specialty
}

function selectBeautician(beautician) {
  bookingStore.selectBeautician({
    id: beautician.id,
    name: beautician.name,
    avatar: beautician.avatar,
    rating: beautician.rating,
    serviceCount: beautician.serviceCount,
    specialty: beautician.specialty,
    level: beautician.level
  })
}

function goNext() {
  if (bookingStore.selectedBeautician) {
    uni.navigateTo({ url: '/pages/booking/time/index' })
  }
}

async function fetchBeauticians(reset = false) {
  if (loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
    beauticians.value = []
  }
  
  loading.value = true
  
  try {
    // 优先获取在职美容师
    const res = await getActiveBeauticians()
    if (res.data && Array.isArray(res.data)) {
      beauticians.value = res.data
      noMore.value = true
    }
  } catch (e) {
    console.error('Failed to fetch beauticians:', e)
    loadStaticBeauticians()
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    page.value++
    fetchBeauticians()
  }
}

function loadStaticBeauticians() {
  beauticians.value = [
    { id: 1, name: '小林', gender: 0, level: 3, rating: 4.9, serviceCount: 328, specialty: '面部护理,身体护理', introduction: '资深美容师，擅长各类面部护理' },
    { id: 2, name: '小王', gender: 0, level: 2, rating: 4.8, serviceCount: 256, specialty: '美甲,美睫', introduction: '美甲美睫专家' },
    { id: 3, name: '小李', gender: 0, level: 4, rating: 4.7, serviceCount: 189, specialty: '面部护理,抗衰', introduction: '首席美容师' },
    { id: 4, name: '小张', gender: 0, level: 3, rating: 4.9, serviceCount: 412, specialty: '身体理疗,按摩', introduction: '理疗按摩专家' }
  ]
  noMore.value = true
}

onMounted(() => {
  fetchBeauticians(true)
})
</script>

<style lang="scss" scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-light);
}

.page-content {
  flex: 1;
  padding: 20rpx;
}

.beautician-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding-bottom: 200rpx;
}

.beautician-item {
  display: flex;
  align-items: flex-start;
  padding: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  
  &.selected {
    border: 2rpx solid var(--primary);
    background: var(--primary-light);
  }
  
  .beautician-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    overflow: hidden;
    background: var(--bg-light);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    flex-shrink: 0;
    
    image {
      width: 100%;
      height: 100%;
    }
    
    text {
      font-size: 50rpx;
    }
  }
  
  .beautician-info {
    flex: 1;
    
    .beautician-header {
      display: flex;
      align-items: center;
      margin-bottom: 8rpx;
    }
    
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
    
    .beautician-rating {
      font-size: 24rpx;
      color: var(--text-gray);
      margin-bottom: 12rpx;
      
      .stars {
        color: #ffc107;
        margin-right: 8rpx;
      }
      
      .divider {
        margin: 0 8rpx;
      }
    }
    
    .beautician-specialty {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-bottom: 8rpx;
      
      .tag {
        padding: 4rpx 16rpx;
        background: var(--bg-light);
        border-radius: 20rpx;
        font-size: 22rpx;
        color: var(--text-gray);
      }
    }
    
    .beautician-intro {
      font-size: 24rpx;
      color: var(--text-gray);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  .beautician-check {
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    border: 2rpx solid var(--border);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #fff;
    flex-shrink: 0;
    
    .selected & {
      background: var(--primary);
      border-color: var(--primary);
    }
  }
}

.loading-more {
  text-align: center;
  padding: 30rpx;
  color: var(--text-gray);
  font-size: 24rpx;
}

.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid var(--border);
  
  .action-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: var(--primary);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    
    &[disabled] {
      background: #ccc;
    }
  }
}
</style>
