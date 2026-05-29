import request from '@/utils/request'

export function getProductPage(params) {
  return request({
    url: '/pos/product/page',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/pos/product/${id}`,
    method: 'get'
  })
}

export function saveProduct(data) {
  return request({
    url: '/pos/product',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/pos/product',
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/pos/product/${id}`,
    method: 'delete'
  })
}
