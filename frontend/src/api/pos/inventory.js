import request from '@/utils/request'

export function getInventoryPage(params) {
  return request({
    url: '/pos/inventory/page',
    method: 'get',
    params
  })
}

export function stockIn(data) {
  return request({
    url: '/pos/inventory/stock-in',
    method: 'post',
    data
  })
}

export function stockOut(data) {
  return request({
    url: '/pos/inventory/stock-out',
    method: 'post',
    data
  })
}

export function adjustment(data) {
  return request({
    url: '/pos/inventory/adjustment',
    method: 'post',
    data
  })
}
