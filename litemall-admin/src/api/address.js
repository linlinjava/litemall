import request from '@/utils/request'

export function listAddress(query) {
  return request({
    url: '/address/list',
    method: 'get',
    params: query
  })
}

export function createAddress(data) {
  return request({
    url: '/address/create',
    method: 'post',
    data
  })
}

export function readAddress(data) {
  return request({
    url: '/address/read',
    method: 'get',
    data
  })
}

export function updateAddress(data) {
  return request({
    url: '/address/update',
    method: 'post',
    data
  })
}

export function deleteAddress(data) {
  return request({
    url: '/address/delete',
    method: 'post',
    data
  })
}
