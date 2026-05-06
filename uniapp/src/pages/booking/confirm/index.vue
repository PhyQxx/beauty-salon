<template>
  <view class="page">
    <scroll-view scroll-y class="page-content">
      <!-- Services -->
      <view class="card">
        <view class="card-title">服务项目</view>
        <view v-for="service in bookingStore.selectedServices" :key="service.id" class="service-row">
          <view class="service-info">
            <text class="service-name">{{ service.name }}</text>
            <text class="service-duration">{{ service.duration }}分钟</text>
          </view>
          <text class="service-price">¥{{ service.price }}</text>
        </view>
        <view class="service-row total">
          <text>合计</text>
          <text class="price">¥{{ bookingStore.totalPrice }}</text>
        </view>
      </view>
      
      <!-- Beautician -->
      <view class="card">
        <view class="card-title">美容师</view>
        <view class="info-row">
          <view class="beautician-info">
            <text class="beautician-name">{{ bookingStore.selectedBeautician?.name }}</text>
            <text class="beautician-level">{{ getLevelText(bookingStore.selectedBeautician?.level) }}</text>
          </view>
          <text class="beautician-rating">★ {{ bookingStore.selectedBeautician?.rating || '5.0' }}</text>
        </view>
      </view>
      
      <!-- Time -->
      <view class="card">
        <view class="card-title">预约时间</view>
        <view class="time-row">
          <view class="time-item">
            <text class="time-label">日期</text>
            <text class="time-value">{{ bookingStore.selectedDate }}</text>
          </view>
          <view class="time-item">
            <text class="time-label">时间</text>
            <text class="time-value">{{ bookingStore.selectedTime }}</text>
          </view>
          <view class="time-item">
            <text class="time-label">时长</text>
            <text class="time-value">约{{ bookingStore.totalDuration }}分钟</text>
          </view>
        </view>
      </view>
      
      <!-- Customer Info -->
      <view class="card">
        <view class="card-title">客户信息</view>
        <view class="input-row">
          <text class="input-label">姓名</text>
          <input 
            v-model="customerName" 
            class="input-field" 
            placeholder="请输入您的姓名"
          />
        </view>
        <view class="input-row">
          <text class="input-label">手机号</text>
          <input 
            v-model="customerPhone" 
            class="input-field" 
            type="number"
            maxlength="11"
            placeholder="请输入手机号"
          />
        </view>
      </view>
      
      <!-- Remark -->
      <view class="card">
        <view class="card-title">备注</view>
        <textarea 
          v-model="remark" 
          class="remark-input"
          placeholder="如有特殊需求请备注..."
          maxlength="200"
        />
      </view>
    </scroll-view>
    
    <view class="bottom-action">
      <view class="action-info">
        <text>应付金额: </text>
        <text class="total-price">¥{{ bookingStore.totalPrice }}</text>
      </view>
      <button class="action-btn" :disabled="submitting" @click="submitBooking">
        {{ submitting ? '提交中...' : '确认预约' }}
      </button>
    </view>
    
    <!-- Success Modal -->
    <view v-if="showSuccess" class="modal-overlay">
      <view class="modal-content">
        <view class="modal-icon">✓</view>
        <view class="modal-title">预约成功</view>
        <view class="modal-desc">我们已收到您的预约请求</view>
        <view class="modal-desc">稍后会有工作人员与您联系确认</view>
        <button class="modal-btn" @click="goToAppointments">查看预约记录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useBookingStore } from '@/store'
import { createAppointment } from '@/api/appointment'
import { getLevelText } from '@/utils/service'

const bookingStore = useBookingStore()

const customerName = ref('')
const customerPhone = ref('')
const remark = ref('')
const submitting = ref(false)
const showSuccess = ref(false)

// 计算结束时间
function calculateEndTime() {
  const start = bookingStore.selectedTime
  const duration = bookingStore.totalDuration
  
  if (!start || !duration) return ''
  
  const [hours, minutes] = start.split(':').map(Number)
  const totalMinutes = hours * 60 + minutes + duration
  
  const endHours = Math.floor(totalMinutes / 60)
  const endMinutes = totalMinutes % 60
  
  return `${String(endHours).padStart(2, '0')}:${String(endMinutes).padStart(2, '0')}`
}

async function submitBooking() {
  // 表单验证
  if (!customerName.value.trim()) {
    uni.showToast({ title: '请输入姓名', icon: 'none' })
    return
  }
  if (!customerPhone.value.trim() || !/^1\d{10}$/.test(customerPhone.value)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  
  submitting.value = true
  
  try {
    // 准备预约数据
    // 如果选了多个服务，只传第一个（后端单服务设计）
    const firstService = bookingStore.selectedServices[0]
    
    const appointmentData = {
      customerName: customerName.value,
      customerPhone: customerPhone.value,
      beauticianId: bookingStore.selectedBeautician?.id,
      serviceItemId: firstService?.id,
      appointmentDate: bookingStore.selectedDate,
      startTime: bookingStore.selectedTime,
      endTime: calculateEndTime(),
      duration: bookingStore.totalDuration,
      amount: bookingStore.totalPrice,
      remark: remark.value
    }
    
    const res = await createAppointment(appointmentData)
    
    if (res.success || res.code === 200) {
      bookingStore.setAppointmentId(res.data?.id)
      showSuccess.value = true
    } else {
      throw new Error(res.message || '预约失败')
    }
  } catch (e) {
    console.error('Failed to create appointment:', e)
    uni.showToast({ title: e.message || '预约失败，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function goToAppointments() {
  showSuccess.value = false
  bookingStore.resetBooking()
  uni.reLaunch({ url: '/pages/appointments/index' })
}
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
  padding-bottom: 200rpx;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  
  .card-title {
    font-size: 28rpx;
    font-weight: 600;
    color: var(--text-dark);
    margin-bottom: 20rpx;
  }
}

.service-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid var(--border);
  
  &:last-child {
    border-bottom: none;
  }
  
  .service-info {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }
  
  .service-name {
    font-size: 26rpx;
    color: var(--text-dark);
  }
  
  .service-duration {
    font-size: 22rpx;
    color: var(--text-gray);
  }
  
  .service-price {
    font-size: 26rpx;
    color: var(--text-dark);
  }
  
  &.total {
    margin-top: 12rpx;
    padding-top: 20rpx;
    border-top: 2rpx solid var(--border);
    font-weight: 600;
    
    .price {
      color: var(--primary);
      font-size: 32rpx;
    }
  }
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .beautician-info {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }
  
  .beautician-name {
    font-size: 28rpx;
    color: var(--text-dark);
  }
  
  .beautician-level {
    font-size: 20rpx;
    padding: 4rpx 12rpx;
    background: var(--primary-light);
    color: var(--primary);
    border-radius: 12rpx;
  }
  
  .beautician-rating {
    font-size: 26rpx;
    color: #ffc107;
  }
}

.time-row {
  display: flex;
  gap: 30rpx;
}

.time-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  
  .time-label {
    font-size: 22rpx;
    color: var(--text-gray);
  }
  
  .time-value {
    font-size: 26rpx;
    color: var(--text-dark);
  }
}

.input-row {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid var(--border);
  
  &:last-child {
    border-bottom: none;
  }
  
  .input-label {
    width: 120rpx;
    font-size: 26rpx;
    color: var(--text-gray);
  }
  
  .input-field {
    flex: 1;
    font-size: 28rpx;
    color: var(--text-dark);
  }
}

.remark-input {
  width: 100%;
  min-height: 160rpx;
  padding: 16rpx;
  background: var(--bg-light);
  border-radius: 8rpx;
  font-size: 26rpx;
  box-sizing: border-box;
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
  display: flex;
  align-items: center;
  gap: 20rpx;
  
  .action-info {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    color: var(--text-gray);
    
    .total-price {
      font-size: 36rpx;
      font-weight: 700;
      color: var(--primary);
    }
  }
  
  .action-btn {
    flex: 1;
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

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal-content {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  text-align: center;
  
  .modal-icon {
    width: 120rpx;
    height: 120rpx;
    background: var(--primary);
    color: #fff;
    font-size: 60rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 30rpx;
  }
  
  .modal-title {
    font-size: 36rpx;
    font-weight: 600;
    color: var(--text-dark);
    margin-bottom: 20rpx;
  }
  
  .modal-desc {
    font-size: 26rpx;
    color: var(--text-gray);
    margin-bottom: 10rpx;
  }
  
  .modal-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: var(--primary);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    margin-top: 40rpx;
  }
}
</style>
