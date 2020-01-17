import request from '@/utils/request'

export function listKeyword(query) {
  return request({
    url: '/keyword/list',
    method: 'get',
    params: query
  })
}

export function createKeyword(data) {
  return request({
    url: '/keyword/create',
    method: 'post',
    data
  })
}

export function readKeyword(data) {
  return request({
    url: '/keyword/read',
    method: 'get',
    data
  })
}

export function updateKeyword(data) {
  return request({
    url: '/keyword/update',
    method: 'post',
    data
  })
}

export function deleteKeyword(data) {
  return request({
    url: '/keyword/delete',
    method: 'post',
    data
  })
}
