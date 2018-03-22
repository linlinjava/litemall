import request from '@/utils/request'

export function listCart(query) {
  return request({
    url: '/cart/list',
    method: 'get',
    params: query
  })
}

export function createCart(data) {
  return request({
    url: '/cart/create',
    method: 'post',
    data
  })
}

export function readCart(data) {
  return request({
    url: '/cart/read',
    method: 'get',
    data
  })
}

export function updateCart(data) {
  return request({
    url: '/cart/update',
    method: 'post',
    data
  })
}

export function deleteCart(data) {
  return request({
    url: '/cart/delete',
    method: 'post',
    data
  })
}
