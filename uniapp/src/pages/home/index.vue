<template>
  <view class="page">
    <!-- Banner Carousel -->
    <view class="banner-section">
      <swiper
        class="banner-swiper"
        :indicator-dots="true"
        :autoplay="true"
        :interval="4000"
        :circular="true"
        indicator-active-color="#e85d75"
        indicator-color="rgba(255,255,255,0.4)"
      >
        <swiper-item
          v-for="(banner, index) in banners"
          :key="index"
          @click="handleBannerClick(banner)"
        >
          <view :class="['banner-item', banner.bgClass]">
            <view class="banner-badge">{{ banner.tag }}</view>
            <text class="banner-title">{{ banner.title }}</text>
            <text class="banner-subtitle">{{ banner.subtitle }}</text>
            <view class="banner-action">
              <text class="banner-btn">立即查看</text>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- Quick Actions -->
    <view class="quick-actions">
      <view class="quick-action" @click="goTo('/pages/booking/index')">
        <view class="quick-action-icon bg-pink">
          <text class="icon-text">预</text>
        </view>
        <text class="quick-action-label">在线预约</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/member/index')">
        <view class="quick-action-icon bg-blue">
          <text class="icon-text">卡</text>
        </view>
        <text class="quick-action-label">会员码</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/coupons/index')">
        <view class="quick-action-icon bg-gold">
          <text class="icon-text">券</text>
        </view>
        <text class="quick-action-label">优惠券</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/orders/index')">
        <view class="quick-action-icon bg-green">
          <text class="icon-text">单</text>
        </view>
        <text class="quick-action-label">我的订单</text>
      </view>
    </view>

    <!-- Hot Services -->
    <view class="section">
      <view class="section-header">
        <view class="section-title-wrap">
          <view class="title-bar" />
          <text class="section-title-text">热门服务</text>
        </view>
        <view class="section-more" @click="goTo('/pages/booking/index')">
          更多 <text class="arrow">›</text>
        </view>
      </view>
      <scroll-view scroll-x class="service-scroll" show-scrollbar="false">
        <view class="service-row">
          <view
            v-for="service in hotServices"
            :key="service.id"
            class="service-card"
            @click="goTo('/pages/booking/index')"
          >
            <view class="service-img-wrap" :style="{ background: service.bgColor }">
              <text class="service-img-text">{{ service.name.charAt(0) }}</text>
            </view>
            <view class="service-info">
              <view class="service-name">{{ service.name }}</view>
              <view class="service-meta">
                <text class="service-price">¥{{ service.price }}</text>
                <text class="service-duration">{{ service.duration }}分钟</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- Recommended Beauticians -->
    <view class="section">
      <view class="section-header">
        <view class="section-title-wrap">
          <view class="title-bar" />
          <text class="section-title-text">推荐美容师</text>
        </view>
        <view class="section-more" @click="goTo('/pages/booking/beautician/index')">
          更多 <text class="arrow">›</text>
        </view>
      </view>
      <scroll-view scroll-x class="beautician-scroll" show-scrollbar="false">
        <view class="beautician-row">
          <view
            v-for="beautician in topBeauticians"
            :key="beautician.id"
            class="beautician-card"
            @click="goTo('/pages/booking/beautician/index')"
          >
            <view class="beautician-avatar">
              <text class="avatar-text">{{ beautician.name.charAt(0) }}</text>
            </view>
            <view class="beautician-name">{{ beautician.name }}</view>
            <view class="beautician-level">{{ getLevelText(beautician.level) }}</view>
            <view class="beautician-rating">
              <text class="star" v-for="i in 5" :key="i" :class="{ active: i <= beautician.level + 1 }">★</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- Skeleton Screen -->
    <view v-if="loading" class="skeleton-wrapper">
      <view class="skeleton-banner" />
      <view class="skeleton-actions">
        <view class="skeleton-action" v-for="i in 4" :key="i">
          <view class="skeleton-circle" />
          <view class="skeleton-text" />
        </view>
      </view>
      <view class="skeleton-section" v-for="i in 2" :key="i">
        <view class="skeleton-header" />
        <view class="skeleton-row">
          <view class="skeleton-card" v-for="j in 3" :key="j" />
        </view>
      </view>
    </view>

    <!-- Bottom Spacing -->
    <view style="height: 40rpx;" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { getActiveServices } from '@/api/service'
import { getActiveBeauticians } from '@/api/beautician'
import { useMemberStore } from '@/store'

const loading = ref(true)
const services = ref([])
const beauticians = ref([])
const memberStore = useMemberStore()

const needLoginPages = ['/pages/member/index', '/pages/orders/index', '/pages/appointments/index']

// WeChat Share
onShareAppMessage(() => {
  return {
    title: '美悦沙龙 - 您的专业美容管家',
    path: '/pages/home/index',
    imageUrl: '/static/share-cover.png'
  }
})

onShareTimeline(() => {
  return {
    title: '美悦沙龙 - 您的专业美容管家',
    query: 'from=timeline'
  }
})

const banners = ref([
  {
    id: 1,
    title: '母亲节特惠',
    subtitle: '全场服务8折起，感恩回馈',
    tag: '热门',
    bgClass: 'banner-pink',
    link: '/pages/booking/index'
  },
  {
    id: 2,
    title: '新客专享',
    subtitle: '首次预约立减50元',
    tag: '新客',
    bgClass: 'banner-blue',
    link: '/pages/login/index'
  },
  {
    id: 3,
    title: '积分兑换',
    subtitle: '100积分抵10元，多充多送',
    tag: '会员',
    bgClass: 'banner-gold',
    link: '/pages/member/index'
  }
])

const hotServices = computed(() => services.value.slice(0, 6))
const topBeauticians = computed(() => beauticians.value.slice(0, 4))

function goTo(path) {
  if (needLoginPages.includes(path) && !memberStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url: path })
}

function getLevelText(level) {
  const levels = { 1: '初级', 2: '中级', 3: '高级', 4: '首席' }
  return levels[level] || '技师'
}

function handleBannerClick(banner) {
  if (banner.link) {
    uni.navigateTo({ url: banner.link })
  }
}

async function fetchServices() {
  try {
    const res = await getActiveServices()
    if (res.data && res.data.length > 0) {
      services.value = res.data
    } else {
      loadStaticServices()
    }
  } catch (e) {
    loadStaticServices()
  }
}

async function fetchBeauticians() {
  try {
    const res = await getActiveBeauticians()
    if (res.data && res.data.length > 0) {
      beauticians.value = res.data
    } else {
      loadStaticBeauticians()
    }
  } catch (e) {
    loadStaticBeauticians()
  }
}

function loadStaticServices() {
  services.value = [
    { id: 1, name: '面部深层清洁', category: 'facial', price: 198, duration: 60, bgColor: 'linear-gradient(135deg, #e85d75, #ff8a9b)' },
    { id: 2, name: '玻尿酸补水', category: 'facial', price: 268, duration: 45, bgColor: 'linear-gradient(135deg, #4a90e2, #7eb3f1)' },
    { id: 3, name: '光子嫩肤', category: 'facial', price: 398, duration: 90, bgColor: 'linear-gradient(135deg, #a855f7, #c084fc)' },
    { id: 4, name: '日式美甲', category: 'nail', price: 128, duration: 45, bgColor: 'linear-gradient(135deg, #f5a623, #f7c948)' },
    { id: 5, name: '睫毛延长', category: 'eye', price: 188, duration: 50, bgColor: 'linear-gradient(135deg, #52c41a, #7dd367)' },
    { id: 6, name: '身体按摩', category: 'body', price: 288, duration: 60, bgColor: 'linear-gradient(135deg, #e85d75, #f08a9d)' }
  ]
}

function loadStaticBeauticians() {
  beauticians.value = [
    { id: 1, name: '小林', gender: 0, level: 3 },
    { id: 2, name: '小王', gender: 0, level: 2 },
    { id: 3, name: '小李', gender: 0, level: 4 },
    { id: 4, name: '小张', gender: 0, level: 3 }
  ]
}

onMounted(async () => {
  loading.value = true
  await Promise.all([fetchServices(), fetchBeauticians()])
  setTimeout(() => {
    loading.value = false
  }, 600)
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background-color: var(--bg-light);
  padding-bottom: 120rpx;
}

/* Banner */
.banner-section {
  padding: 20rpx 24rpx 0;
}

.banner-swiper {
  width: 100%;
  height: 320rpx;
  border-radius: 24rpx;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40rpx 48rpx;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    right: -40rpx;
    top: -40rpx;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }

  &::before {
    content: '';
    position: absolute;
    right: 60rpx;
    bottom: -60rpx;
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.06);
  }

  &.banner-pink {
    background: linear-gradient(135deg, #e85d75, #c94a61);
  }

  &.banner-blue {
    background: linear-gradient(135deg, #4a90e2, #357abd);
  }

  &.banner-gold {
    background: linear-gradient(135deg, #f5a623, #d4891a);
  }

  .banner-badge {
    display: inline-block;
    align-self: flex-start;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 20rpx;
    padding: 6rpx 16rpx;
    border-radius: 20rpx;
    margin-bottom: 16rpx;
    backdrop-filter: blur(4px);
  }

  .banner-title {
    font-size: 40rpx;
    font-weight: 700;
    color: #fff;
    margin-bottom: 12rpx;
    letter-spacing: 2rpx;
  }

  .banner-subtitle {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.85);
    margin-bottom: 24rpx;
  }

  .banner-action {
    .banner-btn {
      display: inline-block;
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
      font-size: 24rpx;
      padding: 10rpx 28rpx;
      border-radius: 28rpx;
      border: 1rpx solid rgba(255, 255, 255, 0.3);
      backdrop-filter: blur(4px);
    }
  }
}

/* Quick Actions */
.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 32rpx 24rpx;
  background: #fff;
  margin: 20rpx 24rpx;
  border-radius: 24rpx;
  box-shadow: var(--shadow-sm);

  .quick-action {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 14rpx;
    transition: all 0.25s ease;

    &:active {
      transform: scale(0.95);
    }

    .quick-action-icon {
      width: 96rpx;
      height: 96rpx;
      border-radius: 28rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: var(--shadow-sm);

      &.bg-pink {
        background: linear-gradient(135deg, #fef1f3, #f8e4e7);
      }
      &.bg-blue {
        background: linear-gradient(135deg, #eff6ff, #dbeafe);
      }
      &.bg-gold {
        background: linear-gradient(135deg, #fffbeb, #fef3c7);
      }
      &.bg-green {
        background: linear-gradient(135deg, #f0fdf4, #dcfce7);
      }

      .icon-text {
        font-size: 36rpx;
        font-weight: 700;
        color: var(--primary);
      }
    }

    .quick-action-label {
      font-size: 24rpx;
      color: var(--text-regular);
      font-weight: 500;
    }
  }
}

/* Section */
.section {
  margin-top: 30rpx;
  padding: 0 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;

  .section-title-wrap {
    display: flex;
    align-items: center;
    gap: 12rpx;

    .title-bar {
      width: 6rpx;
      height: 30rpx;
      background: var(--primary-gradient);
      border-radius: 4rpx;
    }

    .section-title-text {
      font-size: 32rpx;
      font-weight: 700;
      color: var(--text-dark);
    }
  }

  .section-more {
    font-size: 24rpx;
    color: var(--text-gray);
    display: flex;
    align-items: center;
    gap: 4rpx;

    .arrow {
      font-size: 28rpx;
      line-height: 1;
    }
  }
}

/* Service Cards */
.service-scroll {
  width: 100%;
  white-space: nowrap;
}

.service-row {
  display: inline-flex;
  gap: 20rpx;
}

.service-card {
  width: 220rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.97);
  }

  .service-img-wrap {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20rpx;
    box-shadow: var(--shadow-sm);

    .service-img-text {
      font-size: 44rpx;
      font-weight: 700;
      color: #fff;
    }
  }

  .service-info {
    width: 100%;

    .service-name {
      font-size: 26rpx;
      color: var(--text-dark);
      text-align: center;
      margin-bottom: 12rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-weight: 600;
    }

    .service-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 22rpx;

      .service-price {
        color: var(--primary);
        font-weight: 700;
        font-size: 26rpx;
      }

      .service-duration {
        color: var(--text-gray);
        font-size: 20rpx;
        background: #f5f5f5;
        padding: 4rpx 10rpx;
        border-radius: 8rpx;
      }
    }
  }
}

/* Beautician Cards */
.beautician-scroll {
  width: 100%;
  white-space: nowrap;
}

.beautician-row {
  display: inline-flex;
  gap: 20rpx;
}

.beautician-card {
  width: 180rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx 16rpx;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.97);
  }

  .beautician-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: var(--primary-gradient);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16rpx;
    box-shadow: var(--shadow-pink);

    .avatar-text {
      font-size: 40rpx;
      font-weight: 700;
      color: #fff;
    }
  }

  .beautician-name {
    font-size: 28rpx;
    color: var(--text-dark);
    margin-bottom: 8rpx;
    font-weight: 600;
  }

  .beautician-level {
    font-size: 20rpx;
    padding: 4rpx 16rpx;
    background: var(--primary-light);
    color: var(--primary);
    border-radius: 12rpx;
    font-weight: 500;
    margin-bottom: 8rpx;
  }

  .beautician-rating {
    .star {
      font-size: 20rpx;
      color: #e5e7eb;

      &.active {
        color: #f5a623;
      }
    }
  }
}

/* Loading */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.85);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  z-index: 9999;
  color: var(--text-gray);
  font-size: 26rpx;
}

.loading-spinner {
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid var(--primary-light);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Skeleton Screen */
.skeleton-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #fff;
  z-index: 100;
  padding: 20rpx 24rpx;
  box-sizing: border-box;
}

.skeleton-banner {
  width: 100%;
  height: 320rpx;
  background: #f2f2f2;
  border-radius: 24rpx;
  margin-bottom: 32rpx;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-actions {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40rpx;
}

.skeleton-action {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.skeleton-circle {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  background: #f2f2f2;
  margin-bottom: 16rpx;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-text {
  width: 80rpx;
  height: 24rpx;
  background: #f2f2f2;
  border-radius: 4rpx;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-section {
  margin-bottom: 40rpx;
}

.skeleton-header {
  width: 200rpx;
  height: 32rpx;
  background: #f2f2f2;
  margin-bottom: 24rpx;
  border-radius: 4rpx;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-row {
  display: flex;
  gap: 20rpx;
}

.skeleton-card {
  width: 220rpx;
  height: 280rpx;
  background: #f2f2f2;
  border-radius: 20rpx;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}
</style>
