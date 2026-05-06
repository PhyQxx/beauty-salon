/**
 * 预约相关 API
 */
import request from '@/utils/request'

// 获取预约列表
export function getAppointmentList(params) {
  return request.get('/appointment/list', params)
}

// 获取预约详情
export function getAppointmentById(id) {
  return request.get(`/appointment/${id}`)
}

// 创建预约
export function createAppointment(data) {
  return request.post('/appointment', data)
}

// 更新预约
export function updateAppointment(id, data) {
  return request.put(`/appointment/${id}`, data)
}

// 取消预约
export function cancelAppointment(id, reason) {
  return request.put(`/appointment/${id}/cancel`, { reason })
}

// 确认预约
export function confirmAppointment(id) {
  return request.put(`/appointment/${id}/confirm`)
}

// 获取可用时间段
export function getAvailableSlots(params) {
  return request.get('/appointment/available-slots', params)
}

// 检测时间段冲突
export function checkConflict(params) {
  return request.get('/appointment/check-conflict', params)
}
