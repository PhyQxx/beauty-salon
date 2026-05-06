<script>
export default {
  onLaunch() {
    console.log('Beauty Salon App Launch')
    // 检查登录状态
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
      // 简单检查 token 是否存在
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
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.container {
  padding: 24rpx;
}

/* 颜色变量 */
:root {
  --primary: #e85d75;
  --primary-light: #f8e4e7;
  --success: #52c41a;
  --warning: #faad14;
  --danger: #ff4d4f;
  --text-dark: #333;
  --text-gray: #999;
  --bg-light: #f5f5f5;
  --border: #eee;
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
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

/* 通用标题 */
.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 20rpx;
}

/* 通用按钮样式 */
.btn-primary {
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 44rpx;
  
  &:active {
    opacity: 0.8;
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
</style>
