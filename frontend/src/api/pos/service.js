/**
 * 服务项目 API 模块
 * 
 * @author BeautySalon Team
 */
import request from '@/utils/request'

/**
 * 分页查询服务项目列表
 * @param {object} params - 查询参数
 */
export function getServiceList(params) {
  return request({
    url: '/pos/service/list',
    method: 'get',
    params
  })
}

/**
 * 获取服务分类列表
 */
export function getCategories() {
  return request({
    url: '/pos/service/categories',
    method: 'get'
  })
}

/**
 * 获取服务项目详情
 * @param {number} id - 项目ID
 */
export function getServiceById(id) {
  return request({
    url: `/pos/service/${id}`,
    method: 'get'
  })
}

/**
 * 新增服务项目
 * @param {object} data - 项目数据
 */
export function addService(data) {
  return request({
    url: '/pos/service',
    method: 'post',
    data
  })
}

/**
 * 更新服务项目
 * @param {number} id - 项目ID
 * @param {object} data - 项目数据
 */
export function updateService(id, data) {
  return request({
    url: `/pos/service/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除服务项目
 * @param {number} id - 项目ID
 */
export function deleteService(id) {
  return request({
    url: `/pos/service/${id}`,
    method: 'delete'
  })
}

/**
 * 上下架服务项目
 * @param {number} id - 项目ID
 * @param {number} isActive - 是否上架
 */
export function updateServiceStatus(id, isActive) {
  return request({
    url: `/pos/service/${id}/status`,
    method: 'put',
    params: { isActive }
  })
}

/**
 * 获取上架的服务项目（下拉框用）
 */
export function getActiveServices() {
  return request({
    url: '/pos/service/active',
    method: 'get'
  })
}
