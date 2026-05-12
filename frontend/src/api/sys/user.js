/**
 * 系统用户 API 模块
 * 
 * @author BeautySalon Team
 */
import request from '@/utils/request'

/**
 * 用户登录
 * @param {string} username - 用户名
 * @param {string} password - 密码
 */
export function login(username, password) {
  return request({
    url: '/sys/user/login',
    method: 'post',
    data: { username, password }
  })
}

/**
 * 刷新 Token
 * @param {string} refreshToken
 */
export function refreshToken(refreshToken) {
  return request({
    url: '/sys/user/refresh',
    method: 'post',
    data: { refreshToken }
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/sys/user/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/sys/user/info',
    method: 'get'
  })
}

/**
 * 分页查询用户列表
 * @param {object} params - 查询参数
 */
export function getUserList(params) {
  return request({
    url: '/sys/user/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 */
export function getUserById(id) {
  return request({
    url: `/sys/user/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {object} data - 用户数据
 */
export function addUser(data) {
  return request({
    url: '/sys/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {number} id - 用户ID
 * @param {object} data - 用户数据
 */
export function updateUser(id, data) {
  return request({
    url: `/sys/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export function deleteUser(id) {
  return request({
    url: `/sys/user/${id}`,
    method: 'delete'
  })
}

/**
 * 修改密码
 * @param {string} oldPassword - 旧密码
 * @param {string} newPassword - 新密码
 */
export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/sys/user/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}
