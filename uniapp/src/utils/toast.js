// Toast 提示 - 全局调用
let toastTimer = null

export function showToast(options) {
  const { title, icon = 'none', duration = 2000 } = options
  
  // 使用 uni.showToast
  uni.showToast({
    title,
    icon,
    duration
  })
}

export function showSuccess(title = '操作成功') {
  showToast({ title, icon: 'success' })
}

export function showError(title = '操作失败') {
  showToast({ title, icon: 'error' })
}

export function showLoading(title = '加载中...') {
  uni.showLoading({ title })
}

export function hideLoading() {
  uni.hideLoading()
}

export function showConfirm(options) {
  return new Promise((resolve, reject) => {
    uni.showModal({
      title: options.title || '提示',
      content: options.content || '',
      success: (res) => {
        if (res.confirm) {
          resolve()
        } else {
          reject(new Error('cancel'))
        }
      },
      fail: reject
    })
  })
}
