<template>
  <view class="page">
    <scroll-view scroll-y class="page-content">
      <!-- Member Card -->
      <view class="member-card">
        <view class="member-info">
          <view class="member-avatar">
            <image v-if="memberData.avatar" :src="memberData.avatar" mode="aspectFill" />
            <text v-else>👤</text>
          </view>
          <view class="member-detail">
            <text class="member-name">{{ memberData.name || '游客用户' }}</text>
            <text class="member-phone">{{ memberData.phone || '未登录' }}</text>
          </view>
        </view>
        <view class="member-level">
          <text class="level-badge">{{ getLevelText(memberData.memberLevel) }}</text>
          <text class="level-name">{{ getLevelName(memberData.memberLevel) }}</text>
        </view>
      </view>
      
      <!-- Balance & Points -->
      <view class="stats-row">
        <view class="stat-item" @click="goToPoints">
          <text class="stat-value">{{ memberData.points || 0 }}</text>
          <text class="stat-label">积分</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="goToBalance">
          <text class="stat-value">¥{{ memberData.balance || '0.00' }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="goToCoupons">
          <text class="stat-value">{{ couponCount }}</text>
          <text class="stat-label">优惠券</text>
        </view>
      </view>
      
      <!-- Member Benefits -->
      <view class="card">
        <view class="card-title">会员权益</view>
        <view class="benefit-grid">
          <view class="benefit-item">
            <view class="benefit-icon">💰</view>
            <text class="benefit-text">享受{{ getDiscount(memberData.memberLevel) }}折优惠</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon">🎁</view>
            <text class="benefit-text">生日专属礼包</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon">📞</view>
            <text class="benefit-text">优先预约</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon">🎀</view>
            <text class="benefit-text">专属活动</text>
          </view>
        </view>
      </view>
      
      <!-- Member Code -->
      <view class="card">
        <view class="card-title">会员码</view>
        <view class="qr-code" @click="showMemberCode">
          <view class="qr-placeholder">
            <text class="qr-icon">📱</text>
            <text class="qr-text">点击查看会员码</text>
          </view>
        </view>
      </view>
      
      <!-- Menu List -->
      <view class="card">
        <view class="menu-list">
          <view class="menu-item" @click="goToEdit">
            <text class="menu-icon">✏️</text>
            <text class="menu-text">个人信息</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToAppointments">
            <text class="menu-icon">📋</text>
            <text class="menu-text">预约记录</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToOrders">
            <text class="menu-icon">📦</text>
            <text class="menu-text">我的订单</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToPointsDetail">
            <text class="menu-icon">⭐</text>
            <text class="menu-text">积分明细</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item logout" @click="handleLogout">
            <text class="menu-icon">🚪</text>
            <text class="menu-text">退出登录</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="showRecharge">
            <text class="menu-icon">💳</text>
            <text class="menu-text">充值</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- Member Code Modal -->
    <view v-if="showCode" class="modal-overlay" @click="showCode = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">会员码</text>
          <text class="modal-close" @click="showCode = false">×</text>
        </view>
        <view class="qr-display">
          <image v-if="memberData.qrCode" :src="memberData.qrCode" class="qr-image" />
          <view v-else class="qr-placeholder-modal">
            <text class="qr-number">{{ memberData.phone || '***********' }}</text>
          </view>
        </view>
        <text class="qr-hint">出示给工作人员扫描</text>
      </view>
    </view>
    
    <!-- Recharge Modal -->
    <view v-if="showRechargeModal" class="modal-overlay" @click="showRechargeModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">充值</text>
          <text class="modal-close" @click="showRechargeModal = false">×</text>
        </view>
        <view class="recharge-amounts">
          <view 
            v-for="amount in rechargeAmounts" 
            :key="amount"
            :class="['recharge-item', { selected: rechargeAmount === amount }]"
            @click="rechargeAmount = amount"
          >
            <text class="recharge-value">¥{{ amount }}</text>
            <text v-if="amount >= 500" class="recharge-bonus">送{{ Math.floor(amount * 0.1) }}</text>
          </view>
        </view>
        <button class="recharge-btn" @click="doRecharge">立即充值</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCustomerById, recharge } from '@/api/member'
import { useMemberStore } from '@/store'

const memberStore = useMemberStore()

const memberData = ref({
  id: null,
  name: '',
  phone: '',
  avatar: '',
  memberLevel: 1,
  points: 0,
  balance: 0,
  qrCode: ''
})

const couponCount = ref(0)
const showCode = ref(false)
const showRechargeModal = ref(false)
const rechargeAmount = ref(100)
const rechargeAmounts = [100, 200, 300, 500, 1000]

function getLevelText(level) {
  const levels = { 1: 'V1', 2: 'V2', 3: 'V3', 4: 'V4' }
  return levels[level] || 'V1'
}

function getLevelName(level) {
  const names = { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }
  return names[level] || '普通会员'
}

function getDiscount(level) {
  const discounts = { 1: 9.5, 2: 9, 3: 8.5, 4: 8 }
  return discounts[level] || 9.5
}

function showMemberCode() {
  showCode.value = true
}

function showRecharge() {
  showRechargeModal.value = true
}

function goToEdit() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function goToAppointments() {
  uni.switchTab({ url: '/pages/appointments/index' })
}

function goToOrders() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function goToPoints() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function goToBalance() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function goToCoupons() {
  uni.switchTab({ url: '/pages/coupons/index' })
}

function goToPointsDetail() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        memberStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/home/index' })
        }, 1000)
      }
    }
  })
}

async function fetchMemberInfo() {
  try {
    // 先从本地存储获取客户ID
    const customerId = uni.getStorageSync('customerId')
    if (customerId) {
      const res = await getCustomerById(customerId)
      if (res.data) {
        memberData.value = {
          ...memberData.value,
          ...res.data
        }
        memberStore.setMemberInfo(res.data)
      }
    }
  } catch (e) {
    console.error('Failed to fetch member info:', e)
    loadStaticData()
  }
}

function loadStaticData() {
  memberData.value = {
    id: 1,
    name: '李女士',
    phone: '138****8888',
    avatar: '',
    memberLevel: 2,
    points: 1280,
    balance: 588.00,
    qrCode: ''
  }
}

async function doRecharge() {
  if (!memberData.value.id) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '充值中...' })
    await recharge(memberData.value.id, rechargeAmount.value, '会员充值')
    uni.hideLoading()
    uni.showToast({ title: '充值成功', icon: 'success' })
    memberData.value.balance += rechargeAmount.value
    showRechargeModal.value = false
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '充值失败', icon: 'none' })
  }
}

onMounted(() => {
  fetchMemberInfo()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background-color: var(--bg-light);
}

.page-content {
  padding: 20rpx;
}

.member-card {
  background: linear-gradient(135deg, #e85d75, #ff8a9b);
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .member-info {
    display: flex;
    align-items: center;
    
    .member-avatar {
      width: 100rpx;
      height: 100rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;
      overflow: hidden;
      
      image, text {
        width: 100%;
        height: 100%;
        font-size: 60rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    
    .member-detail {
      display: flex;
      flex-direction: column;
      
      .member-name {
        font-size: 34rpx;
        font-weight: 600;
        color: #fff;
        margin-bottom: 8rpx;
      }
      
      .member-phone {
        font-size: 24rpx;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
  
  .member-level {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .level-badge {
      background: #fff;
      color: var(--primary);
      padding: 8rpx 24rpx;
      border-radius: 20rpx;
      font-size: 26rpx;
      font-weight: 600;
      margin-bottom: 8rpx;
    }
    
    .level-name {
      font-size: 22rpx;
      color: rgba(255, 255, 255, 0.9);
    }
  }
}

.stats-row {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;
  margin-bottom: 20rpx;
  
  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .stat-value {
      font-size: 36rpx;
      font-weight: 700;
      color: var(--text-dark);
      margin-bottom: 8rpx;
    }
    
    .stat-label {
      font-size: 24rpx;
      color: var(--text-gray);
    }
  }
  
  .stat-divider {
    width: 1rpx;
    background: var(--border);
    margin: 0 20rpx;
  }
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

.benefit-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
}

.benefit-item {
  display: flex;
  align-items: center;
  
  .benefit-icon {
    font-size: 32rpx;
    margin-right: 12rpx;
  }
  
  .benefit-text {
    font-size: 24rpx;
    color: var(--text-dark);
  }
}

.qr-code {
  display: flex;
  justify-content: center;
  
  .qr-placeholder {
    width: 300rpx;
    height: 300rpx;
    background: var(--bg-light);
    border-radius: 16rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .qr-icon {
      font-size: 80rpx;
      margin-bottom: 16rpx;
    }
    
    .qr-text {
      font-size: 24rpx;
      color: var(--text-gray);
    }
  }
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid var(--border);
  
  &:last-child {
    border-bottom: none;
  }
  
  &.logout {
    margin-top: 20rpx;
    
    .menu-text {
      color: var(--danger);
    }
  }
  
  .menu-icon {
    font-size: 32rpx;
    margin-right: 16rpx;
  }
  
  .menu-text {
    flex: 1;
    font-size: 28rpx;
    color: var(--text-dark);
  }
  
  .menu-arrow {
    font-size: 28rpx;
    color: var(--text-gray);
  }
}

// Modal
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
  padding: 40rpx;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  
  .modal-title {
    font-size: 32rpx;
    font-weight: 600;
    color: var(--text-dark);
  }
  
  .modal-close {
    font-size: 48rpx;
    color: var(--text-gray);
    line-height: 1;
  }
}

.qr-display {
  display: flex;
  justify-content: center;
  margin-bottom: 20rpx;
  
  .qr-placeholder-modal {
    width: 400rpx;
    height: 400rpx;
    background: var(--bg-light);
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .qr-number {
      font-size: 28rpx;
      color: var(--text-gray);
      letter-spacing: 4rpx;
    }
  }
}

.qr-hint {
  display: block;
  text-align: center;
  font-size: 24rpx;
  color: var(--text-gray);
}

.recharge-amounts {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.recharge-item {
  padding: 24rpx;
  background: var(--bg-light);
  border-radius: 12rpx;
  text-align: center;
  border: 2rpx solid transparent;
  
  &.selected {
    border-color: var(--primary);
    background: var(--primary-light);
  }
  
  .recharge-value {
    font-size: 32rpx;
    font-weight: 600;
    color: var(--text-dark);
    display: block;
  }
  
  .recharge-bonus {
    font-size: 20rpx;
    color: var(--primary);
    display: block;
    margin-top: 4rpx;
  }
}

.recharge-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: var(--primary);
  color: #fff;
  border-radius: 44rpx;
  font-size: 30rpx;
}
</style>
