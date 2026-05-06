/**
 * API 请求封装
 */

const BASE_URL = 'http://localhost:8080/api'

// 获取 token
function getToken() {
  try {
    return uni.getStorageSync('token') || ''
  } catch (e) {
    return ''
  }
}

// 请求封装
function request(options) {
  return new Promise((resolve, reject) => {
    const hideLoading = options.hideLoading || false
    !hideLoading && uni.showLoading({ title: '加载中...', mask: true })

    const token = getToken()
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        !hideLoading && uni.hideLoading()
        const data = res.data
        if (data.code === 200 || data.success) {
          resolve(data)
        } else if (data.code === 401) {
          // 未登录，跳转登录页
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/index' })
          }, 1500)
          reject(data)
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err) => {
        !hideLoading && uni.hideLoading()
        console.error('Request failed:', err)
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// GET 请求
export function get(url, data, options = {}) {
  return request({ url, method: 'GET', data, ...options })
}

// POST 请求
export function post(url, data, options = {}) {
  return request({ url, method: 'POST', data, ...options })
}

// PUT 请求
export function put(url, data, options = {}) {
  return request({ url, method: 'PUT', data, ...options })
}

// DELETE 请求
export function del(url, data, options = {}) {
  return request({ url, method: 'DELETE', data, ...options })
}

export default { get, post, put, del }
