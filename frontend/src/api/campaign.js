import request from '@/utils/request'

// 活动相关API

/**
 * 获取活动列表（分页）
 */
export function getCampaignList(params) {
  return request({
    url: '/pos/campaign/list',
    method: 'get',
    params
  })
}

/**
 * 获取活动详情
 */
export function getCampaignById(id) {
  return request({
    url: `/pos/campaign/${id}`,
    method: 'get'
  })
}

/**
 * 新增活动
 */
export function addCampaign(data) {
  return request({
    url: '/pos/campaign',
    method: 'post',
    data
  })
}

/**
 * 更新活动信息
 */
export function updateCampaign(id, data) {
  return request({
    url: `/pos/campaign/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除活动
 */
export function deleteCampaign(id) {
  return request({
    url: `/pos/campaign/${id}`,
    method: 'delete'
  })
}

/**
 * 获取优惠券列表（分页）
 */
export function getCouponList(params) {
  return request({
    url: '/marketing/coupon/list',
    method: 'get',
    params
  })
}

/**
 * 获取优惠券详情
 */
export function getCouponById(id) {
  return request({
    url: `/marketing/coupon/${id}`,
    method: 'get'
  })
}

/**
 * 新增优惠券
 */
export function addCoupon(data) {
  return request({
    url: '/marketing/coupon',
    method: 'post',
    data
  })
}

/**
 * 更新优惠券信息
 */
export function updateCoupon(id, data) {
  return request({
    url: `/marketing/coupon/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除优惠券
 */
export function deleteCoupon(id) {
  return request({
    url: `/marketing/coupon/${id}`,
    method: 'delete'
  })
}

/**
 * 发放优惠券给客户
 */
export function distributeCoupon(couponId, customerIds) {
  return request({
    url: `/marketing/coupon/${couponId}/distribute`,
    method: 'post',
    data: { customerIds }
  })
}

/**
 * 批量发放优惠券
 */
export function batchDistributeCoupon(data) {
  return request({
    url: '/marketing/coupon/batch-distribute',
    method: 'post',
    data
  })
}
