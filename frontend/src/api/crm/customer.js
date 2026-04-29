import request from '@/utils/request'

// 客户相关API

/**
 * 获取客户列表（分页）
 */
export function getCustomerList(params) {
  return request({
    url: '/crm/customer/list',
    method: 'get',
    params
  })
}

/**
 * 获取客户详情
 */
export function getCustomerById(id) {
  return request({
    url: `/crm/customer/${id}`,
    method: 'get'
  })
}

/**
 * 新增客户
 */
export function addCustomer(data) {
  return request({
    url: '/crm/customer',
    method: 'post',
    data
  })
}

/**
 * 更新客户信息
 */
export function updateCustomer(id, data) {
  return request({
    url: `/crm/customer/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除客户
 */
export function deleteCustomer(id) {
  return request({
    url: `/crm/customer/${id}`,
    method: 'delete'
  })
}

/**
 * 调整客户余额
 */
export function adjustCustomerBalance(id, amount, reason) {
  return request({
    url: `/crm/customer/${id}/balance`,
    method: 'put',
    data: { amount, reason }
  })
}

/**
 * 获取客户列表（不分页，用于下拉选择）
 */
export function getCustomerSimpleList() {
  return request({
    url: '/crm/customer/simple-list',
    method: 'get'
  })
}
