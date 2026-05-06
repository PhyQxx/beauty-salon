/**
 * 服务相关 API
 */
import request from '@/utils/request'

// 获取服务列表
export function getServiceList(params) {
  return request.get('/pos/service/list', params)
}

// 获取服务分类
export function getServiceCategories() {
  return request.get('/pos/service/categories')
}

// 获取服务详情
export function getServiceById(id) {
  return request.get(`/pos/service/${id}`)
}

// 获取上架服务
export function getActiveServices() {
  return request.get('/pos/service/active')
}
