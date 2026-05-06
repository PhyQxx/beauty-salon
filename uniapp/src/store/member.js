/**
 * 会员/用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useMemberStore = defineStore('member', () => {
  // Token
  const token = ref('')
  
  // 用户信息
  const userInfo = ref(null)
  
  // 会员信息
  const memberInfo = ref(null)
  
  // 客户ID
  const customerId = ref(null)
  
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)
  
  // 初始化 - 从本地存储恢复
  function init() {
    try {
      const storedToken = uni.getStorageSync('token')
      const storedUser = uni.getStorageSync('userInfo')
      const storedMember = uni.getStorageSync('memberInfo')
      const storedCustomerId = uni.getStorageSync('customerId')
      
      if (storedToken) token.value = storedToken
      if (storedUser) userInfo.value = JSON.parse(storedUser)
      if (storedMember) memberInfo.value = JSON.parse(storedMember)
      if (storedCustomerId) customerId.value = storedCustomerId
    } catch (e) {
      console.error('Failed to init member store:', e)
    }
  }
  
  // 设置Token
  function setToken(newToken) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }
  
  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info
    uni.setStorageSync('userInfo', JSON.stringify(info))
  }
  
  // 设置会员信息
  function setMemberInfo(info) {
    memberInfo.value = info
    uni.setStorageSync('memberInfo', JSON.stringify(info))
  }
  
  // 设置客户ID
  function setCustomerId(id) {
    customerId.value = id
    uni.setStorageSync('customerId', id)
  }
  
  // 登录
  function login(data) {
    setToken(data.token)
    setUserInfo(data.userInfo)
    if (data.memberInfo) {
      setMemberInfo(data.memberInfo)
    }
    if (data.customerId) {
      setCustomerId(data.customerId)
    }
  }
  
  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    memberInfo.value = null
    customerId.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('memberInfo')
    uni.removeStorageSync('customerId')
  }
  
  // 自动初始化
  init()
  
  return {
    token,
    userInfo,
    memberInfo,
    customerId,
    isLoggedIn,
    init,
    setToken,
    setUserInfo,
    setMemberInfo,
    setCustomerId,
    login,
    logout
  }
})
