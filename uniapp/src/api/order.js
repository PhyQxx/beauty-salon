/**
 * 订单相关 API
 */
import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request.get('/pos/order/list', params)
}

// 获取订单详情
export function getOrderById(id) {
  return request.get(`/pos/order/${id}`)
}

// 根据客户ID获取订单列表
export function getOrderByCustomerId(customerId) {
  return request.get(`/pos/order/customer/${customerId}`)
}

// 支付订单
export function payOrder(id, payType, operatorId) {
  return request.put(`/pos/order/${id}/pay`, { payType, operatorId })
}

// 取消订单
export function cancelOrder(id, reason) {
  return request.put(`/pos/order/${id}/cancel`, { reason })
}

// 退款
export function refundOrder(id, refundAmount, reason) {
  return request.put(`/pos/order/${id}/refund`, { refundAmount, reason })
}
