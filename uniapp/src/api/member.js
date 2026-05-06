/**
 * 会员/客户相关 API
 */
import request from '@/utils/request'

// 获取客户详情
export function getCustomerById(id) {
  return request.get(`/crm/customer/${id}`)
}

// 获取客户预约记录
export function getCustomerAppointments(customerId) {
  return request.get(`/crm/customer/${customerId}/appointments`)
}

// 获取客户订单记录
export function getCustomerOrders(customerId) {
  return request.get(`/crm/customer/${customerId}/orders`)
}

// 账户充值
export function recharge(customerId, amount, reason) {
  return request.post(`/crm/customer/${customerId}/recharge`, { amount, reason })
}

// 消费扣款
export function consume(customerId, amount, reason) {
  return request.post(`/crm/customer/${customerId}/consume`, { amount, reason })
}

// 检查手机号是否存在
export function checkPhone(phone) {
  return request.get('/crm/customer/check-phone', { phone })
}
