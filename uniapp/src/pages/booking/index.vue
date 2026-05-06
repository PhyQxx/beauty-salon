<template>
  <view class="page">
    <!-- Header with back -->
    <view class="page-header">
      <view class="back-btn" @click="goBack">←</view>
      <text class="header-title">选择服务</text>
      <view class="header-placeholder"></view>
    </view>
    
    <!-- Tabs -->
    <view class="tabs">
      <view 
        v-for="cat in categories" 
        :key="cat.key"
        :class="['tab', { active: activeCategory === cat.key }]"
        @click="changeCategory(cat.key)"
      >
        {{ cat.name }}
      </view>
    </view>
    
    <scroll-view 
      scroll-y 
      class="page-content" 
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="service-list">
        <view 
          v-for="service in filteredServices" 
          :key="service.id" 
          :class="['service-item', { selected: bookingStore.isServiceSelected(service.id) }]"
          @click="toggleService(service)"
        >
          <view class="service-icon">{{ getServiceIcon(service.category) }}</view>
          <view class="service-content">
            <view class="service-name">{{ service.name }}</view>
            <view class="service-desc">{{ service.description || '' }}</view>
            <view class="service-meta">
              <text>{{ service.duration }}分钟</text>
              <text class="service-price">¥{{ service.price }}</text>
            </view>
          </view>
          <view class="service-check">
            <text v-if="bookingStore.isServiceSelected(service.id)">✓</text>
          </view>
        </view>
        
        <view v-if="loading" class="loading-more">
          <text>加载中...</text>
        </view>
        <view v-if="noMore && services.length > 0" class="loading-more">
          <text>没有更多了</text>
        </view>
      </view>
    </scroll-view>
    
    <view class="bottom-action">
      <view class="action-info">
        已选: {{ selectedNames }} | 共 {{ totalDuration }}分钟
      </view>
      <button 
        class="action-btn" 
        :disabled="selectedCount === 0"
        @click="goNext"
      >
        下一步：选择美容师
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useBookingStore } from '@/store'
import { getServiceList, getServiceCategories } from '@/api/service'
import { getServiceIcon, categoryNameMap } from '@/utils/service'

const bookingStore = useBookingStore()

// 分类
const categories = ref([
  { key: 'all', name: '全部' }
])
const activeCategory = ref('all')

// 服务列表
const services = ref([])
const loading = ref(false)
const refreshing = ref(false)
const page = ref(1)
const limit = ref(20)
const noMore = ref(false)

// 分类映射 - 使用工具函数
const categoryMap = categoryNameMap

// 计算过滤后的服务
const filteredServices = computed(() => {
  if (activeCategory.value === 'all') {
    return services.value
  }
  return services.value.filter(s => s.category === activeCategory.value)
})

const selectedCount = computed(() => bookingStore.selectedServices.length)

const selectedNames = computed(() => {
  if (selectedCount.value === 0) return '未选择'
  return bookingStore.selectedServices.map(s => s.name).join(' + ')
})

const totalDuration = computed(() => {
  return bookingStore.selectedServices.reduce((sum, s) => sum + (s.duration || 0), 0)
})

function changeCategory(key) {
  activeCategory.value = key
}

function toggleService(service) {
  bookingStore.toggleService(service)
}

function goNext() {
  if (selectedCount.value > 0) {
    uni.navigateTo({ url: '/pages/booking/beautician/index' })
  }
}

function goBack() {
  uni.navigateBack()
}

// 加载分类
async function fetchCategories() {
  try {
    const res = await getServiceCategories()
    if (res.data && Array.isArray(res.data)) {
      const cats = res.data.map(cat => ({
        key: cat,
        name: categoryMap[cat] || cat
      }))
      categories.value = [{ key: 'all', name: '全部' }, ...cats]
    }
  } catch (e) {
    console.error('Failed to fetch categories:', e)
  }
}

// 加载服务列表
async function fetchServices(reset = false) {
  if (loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
    services.value = []
  }
  
  loading.value = true
  
  try {
    const res = await getServiceList({
      page: page.value,
      limit: limit.value,
      category: activeCategory.value === 'all' ? undefined : activeCategory.value,
      isActive: 1
    })
    
    if (res.data) {
      const list = res.data.list || res.data || []
      if (list.length < limit.value) {
        noMore.value = true
      }
      services.value = reset ? list : [...services.value, ...list]
      page.value++
    }
  } catch (e) {
    console.error('Failed to fetch services:', e)
    // 降级使用静态数据
    if (reset) {
      loadStaticServices()
    }
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!noMore.value && !loading.value) {
    fetchServices()
  }
}

async function onRefresh() {
  refreshing.value = true
  await fetchServices(true)
  refreshing.value = false
}

function loadStaticServices() {
  services.value = [
    { id: 1, name: '面部深层清洁', category: 'facial', description: '深层清洁毛孔', price: 198, duration: 60 },
    { id: 2, name: '玻尿酸补水', category: 'facial', description: '补水保湿', price: 268, duration: 45 },
    { id: 3, name: '光子嫩肤', category: 'facial', description: '嫩白肌肤', price: 398, duration: 90 },
    { id: 4, name: '日式美甲', category: 'nail', description: '精致美甲', price: 128, duration: 45 },
    { id: 5, name: '睫毛延长', category: 'eye', description: '睫毛延长', price: 188, duration: 50 },
    { id: 6, name: '身体按摩', category: 'body', description: '放松身心', price: 288, duration: 60 }
  ]
  noMore.value = true
}

onMounted(() => {
  fetchCategories()
  fetchServices(true)
})
</script>

<style lang="scss" scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-light);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background: #fff;
  border-bottom: 1rpx solid var(--border);
  
  .back-btn {
    font-size: 40rpx;
    color: var(--text-dark);
    width: 60rpx;
  }
  
  .header-title {
    font-size: 32rpx;
    font-weight: 600;
    color: var(--text-dark);
  }
  
  .header-placeholder {
    width: 60rpx;
  }
}

.tabs {
  display: flex;
  padding: 20rpx;
  background: #fff;
  gap: 20rpx;
  overflow-x: auto;
  flex-shrink: 0;
  
  .tab {
    padding: 16rpx 32rpx;
    border-radius: 30rpx;
    font-size: 26rpx;
    color: var(--text-gray);
    background: var(--bg-light);
    white-space: nowrap;
    
    &.active {
      background: var(--primary);
      color: #fff;
    }
  }
}

.page-content {
  flex: 1;
  padding: 20rpx;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding-bottom: 200rpx;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  
  &.selected {
    border: 2rpx solid var(--primary);
    background: var(--primary-light);
  }
  
  .service-icon {
    font-size: 48rpx;
    margin-right: 20rpx;
  }
  
  .service-content {
    flex: 1;
    
    .service-name {
      font-size: 28rpx;
      color: var(--text-dark);
      margin-bottom: 8rpx;
    }
    
    .service-desc {
      font-size: 24rpx;
      color: var(--text-gray);
      margin-bottom: 8rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .service-meta {
      font-size: 24rpx;
      color: var(--text-gray);
      
      .service-price {
        color: var(--primary);
        font-weight: 600;
        margin-left: 20rpx;
      }
    }
  }
  
  .service-check {
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    border: 2rpx solid var(--border);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #fff;
    
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
