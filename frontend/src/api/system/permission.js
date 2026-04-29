/**
 * 权限管理 API
 */
import request from '@/utils/request'

export function getPermissionTree() {
  return request.get('/system/permission/tree')
}

export function getPermissionsByRoleId(roleId) {
  return request.get(`/system/permission/role/${roleId}`)
}

export function updateRolePermissions(roleId, permissionIds) {
  return request.put(`/system/permission/role/${roleId}`, { permissionIds })
}

export function addPermission(data) {
  return request.post('/system/permission', data)
}

export function updatePermission(id, data) {
  return request.put(`/system/permission/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/system/permission/${id}`)
}
