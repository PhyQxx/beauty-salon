/**
 * 订单管理 API 模块
 * 
 * @author BeautySalon Team
 */
import request from '@/utils/request'

/**
 * 分页查询订单列表
 * @param {object} params - 查询参数
 */
export function getOrderList(params) {
  return request({
    url: '/pos/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 * @param {number} id - 订单ID
 */
export function getOrderById(id) {
  return request({
    url: `/pos/order/${id}`,
    method: 'get'
  })
}

/**
 * 创建服务订单
 * @param {object} data - 订单数据
 */
export function createServiceOrder(data) {
  return request({
    url: '/pos/order/service',
    method: 'post',
    data
  })
}

/**
 * 创建商品订单
 * @param {object} data - 订单数据
 */
export function createProductOrder(data) {
  return request({
    url: '/pos/order/product',
    method: 'post',
    data
  })
}

/**
 * 创建充值订单
 * @param {number} customerId - 客户ID
 * @param {number} amount - 充值金额
 * @param {number} payType - 支付方式
 */
export function createRechargeOrder(customerId, amount, payType) {
  return request({
    url: '/pos/order/recharge',
    method: 'post',
    params: { customerId, amount, payType }
  })
}

/**
 * 创建套餐订单
 * @param {object} data - 订单数据
 */
export function createPackageOrder(data) {
  return request({
    url: '/pos/order/package',
    method: 'post',
    data
  })
}

/**
 * 支付订单
 * @param {number} id - 订单ID
 * @param {number} payType - 支付方式
 */
export function payOrder(id, payType) {
  return request({
    url: `/pos/order/${id}/pay`,
    method: 'put',
    params: { payType }
  })
}

/**
 * 退款
 * @param {number} id - 订单ID
 * @param {number} refundAmount - 退款金额
 * @param {string} reason - 退款原因
 */
export function refundOrder(id, refundAmount, reason) {
  return request({
    url: `/pos/order/${id}/refund`,
    method: 'put',
    params: { refundAmount, reason }
  })
}

/**
 * 取消订单
 * @param {number} id - 订单ID
 * @param {string} reason - 取消原因
 */
export function cancelOrder(id, reason) {
  return request({
    url: `/pos/order/${id}/cancel`,
    method: 'put',
    params: { reason }
  })
}

/**
 * 完成订单
 * @param {number} id - 订单ID
 */
export function completeOrder(id) {
  return request({
    url: `/pos/order/${id}/complete`,
    method: 'put'
  })
}

/**
 * 获取日结报表
 * @param {string} date - 日期
 */
export function getDailyReport(date) {
  return request({
    url: '/pos/order/daily-report',
    method: 'get',
    params: { date }
  })
}

/**
 * 获取营业统计
 * @param {string} startDate - 开始日期
 * @param {string} endDate - 结束日期
 */
export function getOrderStatistics(startDate, endDate) {
  return request({
    url: '/pos/order/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}
