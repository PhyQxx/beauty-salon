<script>
export default {
  onLaunch() {
    console.log('Beauty Salon App Launch')
    this.checkLoginStatus()
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  },
  methods: {
    checkLoginStatus() {
      const token = uni.getStorageSync('token')
      if (token) {
        console.log('User is logged in')
      }
    }
  }
}
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}

.container {
  padding: 24rpx;
}

/* 颜色变量 */
:root {
  --primary: #e85d75;
  --primary-light: #f8e4e7;
  --primary-gradient: linear-gradient(135deg, #e85d75, #ff8a9b);
  --primary-gradient-soft: linear-gradient(135deg, #f08a9d, #e85d75);
  --success: #52c41a;
  --warning: #faad14;
  --danger: #ff4d4f;
  --info: #4a90e2;
  --text-dark: #1f2937;
  --text-regular: #4b5563;
  --text-gray: #9ca3af;
  --text-light: #d1d5db;
  --bg-light: #f5f5f5;
  --bg-white: #ffffff;
  --border: #e5e7eb;
  --shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  --shadow-pink: 0 4rpx 16rpx rgba(232, 93, 117, 0.2);
}

/* 安全区域 */
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

.safe-area-top {
  padding-top: env(safe-area-inset-top);
}

/* 通用卡片 */
.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--shadow-sm);
  transition: all 0.25s ease;
}

/* 通用标题 */
.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--text-dark);
  margin-bottom: 20rpx;
}

/* 通用按钮样式 */
.btn-primary {
  background: var(--primary-gradient);
  color: #fff;
  border: none;
  border-radius: 44rpx;
  font-weight: 600;
  box-shadow: var(--shadow-pink);
  transition: all 0.25s ease;

  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }
}

.btn-default {
  background: #f5f5f5;
  color: var(--text-dark);
  border: none;
}

/* 清除按钮默认样式 */
button::after {
  border: none;
}

/* 禁止选中 */
.no-select {
  user-select: none;
}

/* 骨架屏动画 */
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 8rpx;
}
</style>
