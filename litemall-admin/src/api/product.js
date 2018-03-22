import request from '@/utils/request'

export function listProduct(query) {
  return request({
    url: '/product/list',
    method: 'get',
    params: query
  })
}

export function createProduct(data) {
  return request({
    url: '/product/create',
    method: 'post',
    data
  })
}

export function readProduct(data) {
  return request({
    url: '/product/read',
    method: 'get',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/product/update',
    method: 'post',
    data
  })
}

export function deleteProduct(data) {
  return request({
    url: '/product/delete',
    method: 'post',
    data
  })
}
