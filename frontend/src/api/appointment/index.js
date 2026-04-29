/**
 * 预约管理 API 模块
 * 
 * @author BeautySalon Team
 */
import request from '@/utils/request'

/**
 * 分页查询预约列表
 * @param {object} params - 查询参数
 */
export function getAppointmentList(params) {
  return request({
    url: '/appointment/list',
    method: 'get',
    params
  })
}

/**
 * 获取预约详情
 * @param {number} id - 预约ID
 */
export function getAppointmentById(id) {
  return request({
    url: `/appointment/${id}`,
    method: 'get'
  })
}

/**
 * 创建预约
 * @param {object} data - 预约数据
 */
export function createAppointment(data) {
  return request({
    url: '/appointment',
    method: 'post',
    data
  })
}

/**
 * 更新预约
 * @param {number} id - 预约ID
 * @param {object} data - 预约数据
 */
export function updateAppointment(id, data) {
  return request({
    url: `/appointment/${id}`,
    method: 'put',
    data
  })
}

/**
 * 取消预约
 * @param {number} id - 预约ID
 * @param {string} reason - 取消原因
 */
export function cancelAppointment(id, reason) {
  return request({
    url: `/appointment/${id}/cancel`,
    method: 'put',
    params: { reason }
  })
}

/**
 * 确认预约
 * @param {number} id - 预约ID
 */
export function confirmAppointment(id) {
  return request({
    url: `/appointment/${id}/confirm`,
    method: 'put'
  })
}

/**
 * 客户到店
 * @param {number} id - 预约ID
 */
export function arriveAppointment(id) {
  return request({
    url: `/appointment/${id}/arrived`,
    method: 'put'
  })
}

/**
 * 开始服务
 * @param {number} id - 预约ID
 */
export function startAppointmentService(id) {
  return request({
    url: `/appointment/${id}/start`,
    method: 'put'
  })
}

/**
 * 完成服务
 * @param {number} id - 预约ID
 */
export function completeAppointment(id) {
  return request({
    url: `/appointment/${id}/complete`,
    method: 'put'
  })
}

/**
 * 查询技师可用时间段
 * @param {number} beauticianId - 技师ID
 * @param {string} date - 日期
 */
export function getAvailableSlots(beauticianId, date) {
  return request({
    url: '/appointment/available-slots',
    method: 'get',
    params: { beauticianId, date }
  })
}

/**
 * 获取今日预约概览
 */
export function getTodayOverview() {
  return request({
    url: '/appointment/today-overview',
    method: 'get'
  })
}
