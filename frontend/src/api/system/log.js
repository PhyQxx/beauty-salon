/**
 * 系统日志 API
 */
import request from '@/utils/request'

export function queryOperLog(params) {
  return request.get('/system/log/oper', { params })
}

export function queryLoginLog(params) {
  return request.get('/system/log/login', { params })
}

export function getRecentLogin(userId) {
  return request.get(`/system/log/login/recent/${userId}`)
}
