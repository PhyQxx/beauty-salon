import request from '@/utils/request'

/**
 * 获取美容师列表（分页）
 */
export function getBeauticianList(params) {
  return request({
    url: '/beautician',
    method: 'get',
    params
  })
}

/**
 * 获取美容师详情
 */
export function getBeauticianById(id) {
  return request({
    url: `/beautician/${id}`,
    method: 'get'
  })
}

/**
 * 新增美容师
 */
export function addBeautician(data) {
  return request({
    url: '/beautician',
    method: 'post',
    data
  })
}

/**
 * 更新美容师信息
 */
export function updateBeautician(id, data) {
  return request({
    url: `/beautician/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除美容师
 */
export function deleteBeautician(id) {
  return request({
    url: `/beautician/${id}`,
    method: 'delete'
  })
}

/**
 * 获取美容师列表（不分页，用于下拉选择）
 */
export function getBeauticianSimpleList() {
  return request({
    url: '/beautician/simple-list',
    method: 'get'
  })
}
