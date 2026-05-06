/**
 * 优惠券相关 API
 */
import request from '@/utils/request'

// 获取优惠券列表
export function getCouponList(params) {
  return request.get('/coupon/list', params)
}

// 获取优惠券详情
export function getCouponById(id) {
  return request.get(`/coupon/${id}`)
}

// 领取优惠券
export function receiveCoupon(couponId, customerId) {
  return request.post('/coupon/receive', { couponId, customerId })
}

// 获取客户优惠券
export function getCustomerCoupons(customerId, status) {
  return request.get(`/coupon/customer/${customerId}`, { status })
}
