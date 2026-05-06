/**
 * 美容师相关 API
 */
import request from '@/utils/request'

// 获取美容师列表
export function getBeauticianList(params) {
  return request.get('/beautician/list', params)
}

// 获取美容师详情
export function getBeauticianById(id) {
  return request.get(`/beautician/${id}`)
}

// 获取在职美容师
export function getActiveBeauticians() {
  return request.get('/beautician/active')
}
