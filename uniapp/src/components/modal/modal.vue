<template>
  <view 
    :class="['modal', { show: modelValue }]"
    @click="handleOverlayClick"
  >
    <view class="modal-content" :class="[`modal-${position}`]" @click.stop>
      <view v-if="title" class="modal-header">
        <text class="modal-title">{{ title }}</text>
        <text v-if="closeable" class="modal-close" @click="close">×</text>
      </view>
      <view class="modal-body">
        <slot></slot>
      </view>
      <view v-if="$slots.footer" class="modal-footer">
        <slot name="footer"></slot>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: String,
  closeable: {
    type: Boolean,
    default: true
  },
  position: {
    type: String,
    default: 'center' // center, bottom
  },
  overlayCloseable: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'close'])

function close() {
  emit('update:modelValue', false)
  emit('close')
}

function handleOverlayClick() {
  if (props.overlayCloseable) {
    close()
  }
}
</script>

<style lang="scss" scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 9999;
  pointer-events: none;
  transition: background 0.3s;
  
  &.show {
    background: rgba(0, 0, 0, 0.6);
    pointer-events: auto;
  }
}

.modal-content {
  position: absolute;
  background: #fff;
  transition: transform 0.3s;
  
  &.modal-center {
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) scale(0.9);
    width: 600rpx;
    border-radius: 24rpx;
    padding: 40rpx;
    
    .show & {
      transform: translate(-50%, -50%) scale(1);
    }
  }
  
  &.modal-bottom {
    bottom: 0;
    left: 0;
    right: 0;
    transform: translateY(100%);
    border-radius: 24rpx 24rpx 0 0;
    max-height: 80vh;
    overflow-y: auto;
    
    .show & {
      transform: translateY(0);
    }
  }
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

.modal-body {
  font-size: 28rpx;
  color: var(--text-dark);
}

.modal-footer {
  margin-top: 30rpx;
  display: flex;
  gap: 20rpx;
}
</style>
