<template>
  <view class="page">
    <!-- Banner Carousel -->
    <view class="banner-section">
      <swiper 
        class="banner-swiper" 
        :indicator-dots="true" 
        :autoplay="true" 
        :interval="3000" 
        :circular="true"
        indicator-active-color="#e85d75"
      >
        <swiper-item 
          v-for="(banner, index) in banners" 
          :key="index"
          @click="handleBannerClick(banner)"
        >
          <view :class="['banner-item', banner.bgClass]">
            <text class="banner-title">{{ banner.title }}</text>
            <text class="banner-subtitle">{{ banner.subtitle }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>
    
    <!-- Quick Actions -->
    <view class="quick-actions">
      <view class="quick-action" @click="goTo('/pages/booking/index')">
        <view class="quick-action-icon">⚡</view>
        <text>在线预约</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/member/index')">
        <view class="quick-action-icon">📱</view>
        <text>会员码</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/coupons/index')">
        <view class="quick-action-icon">🎫</view>
        <text>优惠券</text>
      </view>
      <view class="quick-action" @click="goTo('/pages/orders/index')">
        <view class="quick-action-icon">📦</view>
        <text>我的订单</text>
      </view>
    </view>
    
    <!-- Hot Services -->
    <view class="section">
      <view class="section-header">
        <view class="section-title">热门服务</view>
        <view class="section-more" @click="goTo('/pages/booking/index')">更多 ></view>
      </view>
      <scroll-view scroll-x class="service-scroll">
        <view class="service-row">
          <view 
            v-for="service in hotServices" 
            :key="service.id" 
            class="service-card"
            @click="goTo('/pages/booking/index')"
          >
            <view class="service-img">{{ getServiceIcon(service.category) }}</view>
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
        <view class="section-title">推荐美容师</view>
        <view class="section-more" @click="goTo('/pages/booking/beautician/index')">更多 ></view>
      </view>
      <scroll-view scroll-x class="beautician-scroll">
        <view class="beautician-row">
          <view 
            v-for="beautician in topBeauticians" 
            :key="beautician.id" 
            class="beautician-card"
            @click="goTo('/pages/booking/beautician/index')"
          >
            <view class="beautician-avatar">
              <text>{{ getDefaultAvatar(beautician.gender) }}</text>
            </view>
            <view class="beautician-name">{{ beautician.name }}</view>
            <view class="beautician-level">{{ getLevelText(beautician.level) }}</view>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- Loading -->
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getActiveServices } from '@/api/service'
import { getActiveBeauticians } from '@/api/beautician'
import { getServiceIcon } from '@/utils/service'
import { useMemberStore } from '@/store'

const loading = ref(false)
const services = ref([])
const beauticians = ref([])

const memberStore = useMemberStore()

// Banner 轮播图
const banners = ref([
  {
    id: 1,
    title: '母亲节特惠',
    subtitle: '全场服务8折起',
    bgClass: 'banner-pink',
    link: '/pages/booking/index'
  },
  {
    id: 2,
    title: '新客专享',
    subtitle: '首次预约立减50元',
    bgClass: 'banner-blue',
    link: '/pages/login/index'
  },
  {
    id: 3,
    title: '积分兑换',
    subtitle: '100积分抵10元',
    bgClass: 'banner-gold',
    link: '/pages/member/index'
  }
])

// 取前6个热门服务
const hotServices = computed(() => services.value.slice(0, 6))

// 取前4个推荐美容师
const topBeauticians = computed(() => beauticians.value.slice(0, 4))

// 需要登录的页面
const needLoginPages = ['/pages/member/index', '/pages/orders/index', '/pages/appointments/index']

function goTo(path) {
  // 检查是否需要登录
  if (needLoginPages.includes(path) && !memberStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url: path })
}

function getDefaultAvatar(gender) {
  return gender === 1 ? '👨' : '👩'
}

function getLevelText(level) {
  const levels = { 1: '初级', 2: '中级', 3: '高级', 4: '首席' }
  return levels[level] || '技师'
}

function goTo(path) {
  // 检查是否需要登录
  if (needLoginPages.includes(path) && !memberStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url: path })
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
    console.error('Failed to fetch services:', e)
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
    console.error('Failed to fetch beauticians:', e)
    loadStaticBeauticians()
  }
}

function loadStaticServices() {
  services.value = [
    { id: 1, name: '面部深层清洁', category: 'facial', price: 198, duration: 60 },
    { id: 2, name: '玻尿酸补水', category: 'facial', price: 268, duration: 45 },
    { id: 3, name: '光子嫩肤', category: 'facial', price: 398, duration: 90 },
    { id: 4, name: '日式美甲', category: 'nail', price: 128, duration: 45 },
    { id: 5, name: '睫毛延长', category: 'eye', price: 188, duration: 50 },
    { id: 6, name: '身体按摩', category: 'body', price: 288, duration: 60 }
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

onMounted(() => {
  fetchServices()
  fetchBeauticians()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background-color: var(--bg-light);
  padding-bottom: 120rpx;
}

.banner-section {
  padding: 20rpx;
}

.banner-swiper {
  width: 100%;
  height: 280rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40rpx;
  
  &.banner-pink {
    background: linear-gradient(135deg, #e85d75, #ff8a9b);
  }
  
  &.banner-blue {
    background: linear-gradient(135deg, #4a90e2, #7eb3f1);
  }
  
  &.banner-gold {
    background: linear-gradient(135deg, #f5a623, #f7c948);
  }
  
  .banner-title {
    font-size: 44rpx;
    font-weight: 700;
    color: #fff;
    margin-bottom: 12rpx;
  }
  
  .banner-subtitle {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.9);
  }
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 30rpx 20rpx;
  background: #fff;
  margin: 0 20rpx;
  border-radius: 16rpx;
  
  .quick-action {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .quick-action-icon {
      font-size: 48rpx;
      margin-bottom: 12rpx;
    }
    
    text {
      font-size: 24rpx;
      color: var(--text-dark);
    }
  }
}

.section {
  margin-top: 30rpx;
  padding: 0 20rpx;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
  }
  
  .section-title {
    font-size: 32rpx;
    font-weight: 600;
    color: var(--text-dark);
  }
  
  .section-more {
    font-size: 24rpx;
    color: var(--text-gray);
  }
}

.service-scroll {
  width: 100%;
  white-space: nowrap;
}

.service-row {
  display: inline-flex;
  gap: 20rpx;
}

.service-card {
  width: 240rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  
  .service-img {
    font-size: 60rpx;
    margin-bottom: 16rpx;
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
    }
    
    .service-meta {
      display: flex;
      justify-content: space-between;
      font-size: 22rpx;
      
      .service-price {
        color: var(--primary);
        font-weight: 600;
      }
      
      .service-duration {
        color: var(--text-gray);
      }
    }
  }
}

.beautician-scroll {
  width: 100%;
  white-space: nowrap;
}

.beautician-row {
  display: inline-flex;
  gap: 20rpx;
}

.beautician-card {
  width: 160rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx 16rpx;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  
  .beautician-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: var(--bg-light);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;
    
    text {
      font-size: 50rpx;
    }
  }
  
  .beautician-name {
    font-size: 26rpx;
    color: var(--text-dark);
    margin-bottom: 8rpx;
  }
  
  .beautician-level {
    font-size: 20rpx;
    padding: 4rpx 16rpx;
    background: var(--primary-light);
    color: var(--primary);
    border-radius: 12rpx;
  }
}

.loading {
  text-align: center;
  padding: 40rpx;
  color: var(--text-gray);
  font-size: 26rpx;
}
</style>
