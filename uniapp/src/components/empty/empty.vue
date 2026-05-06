<template>
  <view class="empty-state">
    <view class="empty-icon">{{ icon }}</view>
    <view class="empty-text">{{ text }}</view>
    <view v-if="actionText" class="empty-action" @click="handleAction">
      {{ actionText }}
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  icon: {
    type: String,
    default: '📭'
  },
  text: {
    type: String,
    default: '暂无数据'
  },
  actionText: String
})

const emit = defineEmits(['action'])

function handleAction() {
  emit('action')
}
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 60rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-gray);
  margin-bottom: 30rpx;
}

.empty-action {
  padding: 16rpx 48rpx;
  background: var(--primary);
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}
</style>
