/**
 * Axios 请求封装
 * 基于 Element Plus 风格封装，统一处理请求和响应
 * 
 * @author BeautySalon Team
 */
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import Cookies from 'js-cookie'
import { refreshToken as refreshTokenApi } from '@/api/sys/user'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 是否正在刷新 token
let isRefreshing = false
// 等待刷新 token 的请求队列
let refreshSubscribers = []

function onTokenRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken))
  refreshSubscribers = []
}

function addRefreshSubscriber(cb) {
  refreshSubscribers.push(cb)
}

function doLogout() {
  Cookies.remove('beauty_salon_token')
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  location.href = '/login'
}

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 Cookie 获取 Token
    const token = Cookies.get('beauty_salon_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 处理 success 格式的接口（如 PosCampaignController）
    if (Object.prototype.hasOwnProperty.call(res, 'success')) {
      if (res.success === false || res.success === 'false') {
        ElMessage.error(res.message || '请求失败')
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data !== undefined ? res.data : res
    }

    // 没有 code 字段时直接返回（如 CrmCustomerController.list 平铺返回）
    if (!Object.prototype.hasOwnProperty.call(res, 'code')) {
      return res
    }

    // 根据业务状态码处理
    if (res.code !== 200 && res.code !== '200') {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 标准 Result 格式：返回 data；平铺格式：去掉 code/message 后返回
    if (Object.prototype.hasOwnProperty.call(res, 'data')) {
      return res.data
    }

    const { code, message, ...rest } = res
    return rest
  },
  error => {
    const { response } = error
    if (response) {
      // Token 过期，尝试无感续期
      if (response.status === 401) {
        const refreshTokenValue = localStorage.getItem('refreshToken')
        if (!refreshTokenValue) {
          ElMessage.error('登录已过期，请重新登录')
          doLogout()
          return Promise.reject(error)
        }

        const originalRequest = error.config

        if (!isRefreshing) {
          isRefreshing = true
          refreshTokenApi(refreshTokenValue)
            .then(data => {
              const newToken = data.token
              const newRefreshToken = data.refreshToken
              if (newToken) {
                Cookies.set('beauty_salon_token', newToken, { path: '/' })
                localStorage.setItem('token', newToken)
                localStorage.setItem('refreshToken', newRefreshToken || '')
                onTokenRefreshed(newToken)
              } else {
                throw new Error('刷新 Token 失败')
              }
            })
            .catch(() => {
              ElMessage.error('登录已过期，请重新登录')
              doLogout()
            })
            .finally(() => {
              isRefreshing = false
            })
        }

        // 将当前请求加入队列，等待刷新完成后重试
        return new Promise(resolve => {
          addRefreshSubscriber(newToken => {
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`
            resolve(service(originalRequest))
          })
        })
      }

      switch (response.status) {
        case 403:
          ElMessage.error(response.data?.message || '没有权限访问')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default service
