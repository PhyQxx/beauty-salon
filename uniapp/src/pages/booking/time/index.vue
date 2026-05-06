<template>
  <view class="page">
    <!-- Date Selection -->
    <view class="section">
      <view class="section-title">选择日期</view>
      <scroll-view scroll-x class="date-scroll">
        <view class="date-grid">
          <view 
            v-for="date in dateList" 
            :key="date.value"
            :class="['date-item', { selected: bookingStore.selectedDate === date.value }]"
            @click="selectDate(date.value)"
          >
            <view class="date-week">{{ date.week }}</view>
            <view class="date-day">{{ date.day }}</view>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- Time Selection -->
    <view class="section">
      <view class="section-title">选择时间</view>
      <view v-if="!bookingStore.selectedDate" class="time-hint">
        <text>请先选择日期</text>
      </view>
      <view v-else class="time-grid">
        <view 
          v-for="time in timeSlots" 
          :key="time"
          :class="['time-item', { 
            selected: bookingStore.selectedTime === time,
            disabled: !time.available
          }]"
          @click="time.available && selectTime(time.time)"
        >
          <text>{{ time.time }}</text>
          <text v-if="!time.available" class="time-unavailable">已约</text>
        </view>
      </view>
    </view>
    
    <!-- Loading -->
    <view v-if="loadingSlots" class="loading-overlay">
      <text>加载可用时间...</text>
    </view>
    
    <view class="bottom-action">
      <view class="action-info">
        <text v-if="bookingStore.selectedDate && bookingStore.selectedTime">
          {{ bookingStore.selectedDate }} {{ bookingStore.selectedTime }}
        </text>
        <text v-else>请选择日期和时间</text>
      </view>
      <button 
        class="action-btn" 
        :disabled="!bookingStore.selectedDate || !bookingStore.selectedTime"
        @click="goNext"
      >
        下一步：确认预约
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useBookingStore } from '@/store'
import { getAvailableSlots } from '@/api/appointment'
import { getFutureDays } from '@/utils/datetime'

const bookingStore = useBookingStore()

const loadingSlots = ref(false)
const bookedSlots = ref([])

// 生成日期列表（未来14天）
const dateList = getFutureDays(14)

// 基础时间段
const baseTimeSlots = [
  '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
  '13:00', '13:30', '14:00', '14:30', '15:00', '15:30',
  '16:00', '16:30', '17:00', '17:30', '18:00', '18:30', '19:00', '19:30'
]

// 计算时间槽（标记已约）
const timeSlots = computed(() => {
  return baseTimeSlots.map(time => ({
    time,
    available: !bookedSlots.value.includes(time)
  }))
})

function selectDate(date) {
  bookingStore.selectDate(date)
  bookingStore.selectTime('') // 清空已选时间
}

function selectTime(time) {
  bookingStore.selectTime(time)
}

// 监听日期变化，加载可用时间
watch(() => bookingStore.selectedDate, async (newDate) => {
  if (newDate && bookingStore.selectedBeautician) {
    await fetchAvailableSlots(newDate)
  }
})

async function fetchAvailableSlots(date) {
  if (!bookingStore.selectedBeautician?.id) return
  
  loadingSlots.value = true
  bookedSlots.value = []
  
  try {
    const res = await getAvailableSlots({
      beauticianId: bookingStore.selectedBeautician.id,
      date: date,
      serviceDuration: bookingStore.totalDuration || 60
    })
    
    if (res.data && Array.isArray(res.data)) {
      // 提取已约的时间段
      bookedSlots.value = res.data.map(slot => slot.startTime || slot.time)
    }
  } catch (e) {
    console.error('Failed to fetch available slots:', e)
    // 模拟部分已约
    bookedSlots.value = ['10:00', '14:00', '16:00']
  } finally {
    loadingSlots.value = false
  }
}

function goNext() {
  if (bookingStore.selectedDate && bookingStore.selectedTime) {
    uni.navigateTo({ url: '/pages/booking/confirm/index' })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background-color: var(--bg-light);
  padding-bottom: 200rpx;
}

.section {
  padding: 20rpx;
  
  .section-title {
    font-size: 30rpx;
    font-weight: 600;
    color: var(--text-dark);
    margin-bottom: 20rpx;
  }
}

.date-scroll {
  width: 100%;
  white-space: nowrap;
}

.date-grid {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 10rpx;
}

.date-item {
  width: 120rpx;
  padding: 20rpx 0;
  background: #fff;
  border-radius: 12rpx;
  text-align: center;
  
  &.selected {
    background: var(--primary);
    color: #fff;
    
    .date-week {
      color: rgba(255, 255, 255, 0.8);
    }
  }
  
  .date-week {
    font-size: 22rpx;
    color: var(--text-gray);
    margin-bottom: 8rpx;
  }
  
  .date-day {
    font-size: 28rpx;
    font-weight: 600;
  }
}

.time-hint {
  text-align: center;
  padding: 60rpx 0;
  color: var(--text-gray);
  font-size: 26rpx;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.time-item {
  padding: 24rpx 0;
  background: #fff;
  border-radius: 8rpx;
  text-align: center;
  font-size: 26rpx;
  position: relative;
  
  &.selected {
    background: var(--primary);
    color: #fff;
    
    .time-unavailable {
      background: rgba(255, 255, 255, 0.3);
      color: #fff;
    }
  }
  
  &.disabled {
    background: #f5f5f5;
    color: #ccc;
    
    .time-unavailable {
      display: block;
    }
  }
  
  .time-unavailable {
    display: none;
    font-size: 20rpx;
    margin-top: 4rpx;
  }
}

.loading-overlay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 24rpx 48rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  z-index: 999;
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
  
  .action-info {
    font-size: 24rpx;
    color: var(--text-gray);
    text-align: center;
    margin-bottom: 16rpx;
  }
  
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
