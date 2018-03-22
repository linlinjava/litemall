import request from '@/utils/request'

export function listCollect(query) {
  return request({
    url: '/collect/list',
    method: 'get',
    params: query
  })
}

export function createCollect(data) {
  return request({
    url: '/collect/create',
    method: 'post',
    data
  })
}

export function readCollect(data) {
  return request({
    url: '/collect/read',
    method: 'get',
    data
  })
}

export function updateCollect(data) {
  return request({
    url: '/collect/update',
    method: 'post',
    data
  })
}

export function deleteCollect(data) {
  return request({
    url: '/collect/delete',
    method: 'post',
    data
  })
}
