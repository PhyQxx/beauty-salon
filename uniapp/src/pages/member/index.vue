<template>
  <view class="page">
    <scroll-view scroll-y class="page-content">
      <!-- Member Card -->
      <view class="member-card">
        <view class="member-card-bg" />
        <view class="member-info">
          <view class="member-avatar">
            <image v-if="memberData.avatar" :src="memberData.avatar" mode="aspectFill" />
            <text v-else class="avatar-fallback">{{ getAvatarText(memberData.name) }}</text>
          </view>
          <view class="member-detail">
            <text class="member-name">{{ memberData.name || '游客用户' }}</text>
            <text class="member-phone">{{ memberData.phone || '点击登录享受更多权益' }}</text>
          </view>
        </view>
        <view class="member-level">
          <view class="level-badge">
            <text>{{ getLevelText(memberData.memberLevel) }}</text>
          </view>
          <text class="level-name">{{ getLevelName(memberData.memberLevel) }}</text>
        </view>
      </view>

      <!-- Balance & Points -->
      <view class="stats-card">
        <view class="stat-item" @click="goToPoints">
          <text class="stat-value">{{ formatNumber(memberData.points) }}</text>
          <text class="stat-label">积分</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item" @click="goToBalance">
          <text class="stat-value">¥{{ formatMoney(memberData.balance) }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item" @click="goToCoupons">
          <text class="stat-value">{{ couponCount }}</text>
          <text class="stat-label">优惠券</text>
        </view>
      </view>

      <!-- Member Benefits -->
      <view class="card benefits-card">
        <view class="card-header">
          <view class="header-bar" />
          <text class="card-title">会员权益</text>
        </view>
        <view class="benefit-grid">
          <view class="benefit-item">
            <view class="benefit-icon icon-discount">
              <text>折</text>
            </view>
            <text class="benefit-text">享受{{ getDiscount(memberData.memberLevel) }}折优惠</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon icon-gift">
              <text>礼</text>
            </view>
            <text class="benefit-text">生日专属礼包</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon icon-vip">
              <text>V</text>
            </view>
            <text class="benefit-text">优先预约通道</text>
          </view>
          <view class="benefit-item">
            <view class="benefit-icon icon-star">
              <text>星</text>
            </view>
            <text class="benefit-text">专属活动邀请</text>
          </view>
        </view>
      </view>

      <!-- Member Code -->
      <view class="card code-card">
        <view class="card-header">
          <view class="header-bar" />
          <text class="card-title">会员码</text>
        </view>
        <view class="qr-code" @click="showMemberCode">
          <view class="qr-visual">
            <view class="qr-pattern">
              <view class="qr-dot" v-for="i in 9" :key="i" />
            </view>
            <text class="qr-hint">点击查看会员码</text>
          </view>
        </view>
      </view>

      <!-- Menu List -->
      <view class="card menu-card">
        <view class="menu-list">
          <view class="menu-item" @click="goToEdit">
            <view class="menu-icon-wrap icon-user">
              <text>我</text>
            </view>
            <text class="menu-text">个人信息</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToAppointments">
            <view class="menu-icon-wrap icon-calendar">
              <text>预</text>
            </view>
            <text class="menu-text">预约记录</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToOrders">
            <view class="menu-icon-wrap icon-order">
              <text>单</text>
            </view>
            <text class="menu-text">我的订单</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToPointsDetail">
            <view class="menu-icon-wrap icon-points">
              <text>分</text>
            </view>
            <text class="menu-text">积分明细</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="showRecharge">
            <view class="menu-icon-wrap icon-recharge">
              <text>充</text>
            </view>
            <text class="menu-text">立即充值</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item logout" @click="handleLogout">
            <view class="menu-icon-wrap icon-logout">
              <text>退</text>
            </view>
            <text class="menu-text">退出登录</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
      </view>

      <!-- Bottom Spacing -->
      <view style="height: 40rpx;" />
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
            <view class="qr-mock">
              <view class="qr-grid">
                <view class="qr-cell" v-for="i in 49" :key="i" :class="{ filled: getRandomQr(i) }" />
              </view>
            </view>
            <text class="qr-number">{{ memberData.phone || '138****8888' }}</text>
          </view>
        </view>
        <text class="qr-tip">出示给工作人员扫描</text>
      </view>
    </view>

    <!-- Recharge Modal -->
    <view v-if="showRechargeModal" class="modal-overlay" @click="showRechargeModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">会员充值</text>
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
            <text v-if="amount >= 500" class="recharge-bonus">送¥{{ Math.floor(amount * 0.1) }}</text>
          </view>
        </view>
        <view class="recharge-custom">
          <text class="recharge-label">自定义金额</text>
          <input
            type="number"
            class="recharge-input"
            placeholder="请输入充值金额"
            v-model.number="customRechargeAmount"
            @focus="rechargeAmount = 0"
          />
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
const customRechargeAmount = ref('')
const rechargeAmounts = [100, 200, 300, 500, 1000]

function getAvatarText(name) {
  if (!name) return '客'
  return name.charAt(0)
}

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

function getRandomQr(i) {
  const pattern = [1, 2, 3, 4, 5, 8, 10, 12, 15, 16, 17, 18, 19, 22, 24, 26, 29, 30, 31, 32, 33, 36, 38, 40, 43, 44, 45, 46, 47]
  return pattern.includes(i)
}

function formatNumber(num) {
  if (!num) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  return num.toString()
}

function formatMoney(num) {
  if (!num) return '0.00'
  return parseFloat(num).toFixed(2)
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
    confirmColor: '#e85d75',
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
  couponCount.value = 3
}

async function doRecharge() {
  const amount = rechargeAmount.value || parseFloat(customRechargeAmount.value) || 0
  if (!amount || amount <= 0) {
    uni.showToast({ title: '请选择或输入充值金额', icon: 'none' })
    return
  }
  if (!memberData.value.id) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }

  try {
    uni.showLoading({ title: '充值中...', mask: true })
    await recharge(memberData.value.id, amount, '会员充值')
    uni.hideLoading()
    uni.showToast({ title: '充值成功', icon: 'success' })
    memberData.value.balance = parseFloat(memberData.value.balance || 0) + amount
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

/* Member Card */
.member-card {
  background: var(--primary-gradient);
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-pink);

  &::after {
    content: '';
    position: absolute;
    right: -60rpx;
    top: -60rpx;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }

  &::before {
    content: '';
    position: absolute;
    left: -40rpx;
    bottom: -40rpx;
    width: 140rpx;
    height: 140rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.06);
  }

  .member-info {
    display: flex;
    align-items: center;
    position: relative;
    z-index: 1;

    .member-avatar {
      width: 108rpx;
      height: 108rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;
      overflow: hidden;
      border: 2rpx solid rgba(255, 255, 255, 0.3);

      image, .avatar-fallback {
        width: 100%;
        height: 100%;
        font-size: 48rpx;
        font-weight: 700;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .member-detail {
      display: flex;
      flex-direction: column;

      .member-name {
        font-size: 36rpx;
        font-weight: 700;
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
    position: relative;
    z-index: 1;

    .level-badge {
      background: #fff;
      padding: 8rpx 24rpx;
      border-radius: 24rpx;
      font-size: 26rpx;
      font-weight: 700;
      color: var(--primary);
      margin-bottom: 10rpx;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
    }

    .level-name {
      font-size: 22rpx;
      color: rgba(255, 255, 255, 0.9);
    }
  }
}

/* Stats Card */
.stats-card {
  display: flex;
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx 20rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--shadow-sm);

  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    transition: all 0.25s ease;

    &:active {
      transform: scale(0.96);
    }

    .stat-value {
      font-size: 38rpx;
      font-weight: 700;
      color: var(--text-dark);
      margin-bottom: 8rpx;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 24rpx;
      color: var(--text-gray);
    }
  }

  .stat-divider {
    width: 1rpx;
    background: var(--border);
    margin: 8rpx 0;
  }
}

/* Cards */
.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--shadow-sm);

  .card-header {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 24rpx;

    .header-bar {
      width: 6rpx;
      height: 28rpx;
      background: var(--primary-gradient);
      border-radius: 4rpx;
    }

    .card-title {
      font-size: 30rpx;
      font-weight: 700;
      color: var(--text-dark);
    }
  }
}

/* Benefits */
.benefit-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: #f8fafc;
  border-radius: 16rpx;

  .benefit-icon {
    width: 56rpx;
    height: 56rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    text {
      font-size: 24rpx;
      font-weight: 700;
      color: #fff;
    }

    &.icon-discount {
      background: linear-gradient(135deg, #e85d75, #ff8a9b);
    }
    &.icon-gift {
      background: linear-gradient(135deg, #f5a623, #f7c948);
    }
    &.icon-vip {
      background: linear-gradient(135deg, #4a90e2, #7eb3f1);
    }
    &.icon-star {
      background: linear-gradient(135deg, #a855f7, #c084fc);
    }
  }

  .benefit-text {
    font-size: 24rpx;
    color: var(--text-regular);
    line-height: 1.4;
  }
}

/* QR Code */
.qr-code {
  display: flex;
  justify-content: center;

  .qr-visual {
    width: 280rpx;
    height: 280rpx;
    background: #f8fafc;
    border-radius: 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 20rpx;
    border: 2rpx dashed var(--border);
    transition: all 0.25s ease;

    &:active {
      background: #fef1f3;
      border-color: var(--primary-light);
    }

    .qr-pattern {
      width: 120rpx;
      height: 120rpx;
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 8rpx;

      .qr-dot {
        width: 100%;
        height: 100%;
        background: var(--primary);
        border-radius: 4rpx;
        opacity: 0.8;

        &:nth-child(2), &:nth-child(4), &:nth-child(6), &:nth-child(8) {
          opacity: 0.3;
        }
      }
    }

    .qr-hint {
      font-size: 24rpx;
      color: var(--text-gray);
    }
  }
}

/* Menu List */
.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.2s ease;

  &:active {
    opacity: 0.7;
  }

  &:last-child {
    border-bottom: none;
  }

  &.logout {
    margin-top: 8rpx;

    .menu-text {
      color: var(--danger);
    }
  }

  .menu-icon-wrap {
    width: 52rpx;
    height: 52rpx;
    border-radius: 14rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    flex-shrink: 0;

    text {
      font-size: 22rpx;
      font-weight: 700;
      color: #fff;
    }

    &.icon-user {
      background: linear-gradient(135deg, #e85d75, #ff8a9b);
    }
    &.icon-calendar {
      background: linear-gradient(135deg, #4a90e2, #7eb3f1);
    }
    &.icon-order {
      background: linear-gradient(135deg, #f5a623, #f7c948);
    }
    &.icon-points {
      background: linear-gradient(135deg, #52c41a, #7dd367);
    }
    &.icon-recharge {
      background: linear-gradient(135deg, #a855f7, #c084fc);
    }
    &.icon-logout {
      background: linear-gradient(135deg, #9ca3af, #d1d5db);
    }
  }

  .menu-text {
    flex: 1;
    font-size: 28rpx;
    color: var(--text-dark);
    font-weight: 500;
  }

  .menu-arrow {
    font-size: 28rpx;
    color: var(--text-light);
    line-height: 1;
  }
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease;
}

.modal-content {
  width: 640rpx;
  background: #fff;
  border-radius: 28rpx;
  padding: 36rpx;
  box-shadow: var(--shadow-lg);
  animation: slideUp 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;

  .modal-title {
    font-size: 32rpx;
    font-weight: 700;
    color: var(--text-dark);
  }

  .modal-close {
    font-size: 44rpx;
    color: var(--text-gray);
    line-height: 1;
    padding: 8rpx;
  }
}

.qr-display {
  display: flex;
  justify-content: center;
  margin-bottom: 20rpx;

  .qr-placeholder-modal {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20rpx;

    .qr-mock {
      width: 340rpx;
      height: 340rpx;
      background: #f8fafc;
      border-radius: 16rpx;
      padding: 30rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .qr-grid {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        gap: 8rpx;
        width: 100%;
        height: 100%;

        .qr-cell {
          background: #e5e7eb;
          border-radius: 2rpx;

          &.filled {
            background: var(--text-dark);
          }
        }
      }
    }

    .qr-number {
      font-size: 28rpx;
      color: var(--text-gray);
      letter-spacing: 4rpx;
      font-weight: 500;
    }
  }
}

.qr-tip {
  display: block;
  text-align: center;
  font-size: 24rpx;
  color: var(--text-gray);
}

/* Recharge */
.recharge-amounts {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.recharge-item {
  padding: 24rpx 12rpx;
  background: #f8fafc;
  border-radius: 16rpx;
  text-align: center;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;

  &.selected {
    border-color: var(--primary);
    background: var(--primary-light);
  }

  &:active {
    transform: scale(0.97);
  }

  .recharge-value {
    font-size: 32rpx;
    font-weight: 700;
    color: var(--text-dark);
    display: block;
  }

  .recharge-bonus {
    font-size: 20rpx;
    color: var(--primary);
    display: block;
    margin-top: 6rpx;
    font-weight: 600;
  }
}

.recharge-custom {
  margin-bottom: 24rpx;

  .recharge-label {
    font-size: 24rpx;
    color: var(--text-gray);
    display: block;
    margin-bottom: 12rpx;
  }

  .recharge-input {
    width: 100%;
    height: 80rpx;
    background: #f8fafc;
    border-radius: 16rpx;
    padding: 0 24rpx;
    font-size: 28rpx;
    color: var(--text-dark);
    border: 2rpx solid transparent;
    transition: all 0.2s ease;

    &:focus {
      border-color: var(--primary-light);
      background: #fff;
    }
  }
}

.recharge-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: var(--primary-gradient);
  color: #fff;
  border-radius: 44rpx;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: var(--shadow-pink);
  border: none;
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(40rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
