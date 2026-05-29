import request from '@/utils/request'

/**
 * 门店管理 API
 * 
 * @author BeautySalon Team
 */

/**
 * 分页查询门店列表
 */
export function listStore(params) {
  return request({
    url: '/system/store/list',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取门店详情
 */
export function getStore(id) {
  return request({
    url: `/system/store/${id}`,
    method: 'get'
  })
}

/**
 * 新增门店
 */
export function addStore(data) {
  return request({
    url: '/system/store',
    method: 'post',
    data
  })
}

/**
 * 更新门店信息
 */
export function updateStore(id, data) {
  return request({
    url: `/system/store/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除门店
 */
export function deleteStore(id) {
  return request({
    url: `/system/store/${id}`,
    method: 'delete'
  })
}

/**
 * 获取可用门店简要列表
 */
export function getSimpleStoreList() {
  return request({
    url: '/system/store/simple-list',
    method: 'get'
  })
}
