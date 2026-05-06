<template>
  <button 
    :class="['btn', `btn-${type}`, `btn-${size}`, { disabled, block, plain, round }]"
    :disabled="disabled"
    @click="handleClick"
  >
    <slot></slot>
  </button>
</template>

<script setup>
const props = defineProps({
  type: {
    type: String,
    default: 'default' // default, primary, success, warning, danger
  },
  size: {
    type: String,
    default: 'medium' // small, medium, large
  },
  disabled: {
    type: Boolean,
    default: false
  },
  block: {
    type: Boolean,
    default: false
  },
  plain: {
    type: Boolean,
    default: false
  },
  round: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

function handleClick(e) {
  if (!props.disabled) {
    emit('click', e)
  }
}
</script>

<style lang="scss" scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 32rpx;
  font-size: 28rpx;
  border: none;
  border-radius: 8rpx;
  transition: all 0.2s;
  
  &.block {
    display: flex;
    width: 100%;
  }
  
  &.round {
    border-radius: 44rpx;
  }
  
  &.disabled {
    opacity: 0.6;
  }
  
  // Types
  &.btn-default {
    background: #f5f5f5;
    color: var(--text-dark);
  }
  
  &.btn-primary {
    background: var(--primary);
    color: #fff;
    
    &.plain {
      background: var(--primary-light);
      color: var(--primary);
    }
  }
  
  &.btn-success {
    background: #52c41a;
    color: #fff;
    
    &.plain {
      background: #e6f7e6;
      color: #52c41a;
    }
  }
  
  &.btn-warning {
    background: #faad14;
    color: #fff;
    
    &.plain {
      background: #fffbe6;
      color: #faad14;
    }
  }
  
  &.btn-danger {
    background: #ff4d4f;
    color: #fff;
    
    &.plain {
      background: #fff1f0;
      color: #ff4d4f;
    }
  }
  
  // Sizes
  &.btn-small {
    height: 56rpx;
    font-size: 24rpx;
    padding: 0 24rpx;
  }
  
  &.btn-medium {
    height: 72rpx;
    font-size: 28rpx;
  }
  
  &.btn-large {
    height: 88rpx;
    font-size: 32rpx;
    padding: 0 40rpx;
  }
}
</style>
