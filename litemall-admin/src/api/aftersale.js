import request from '@/utils/request'

export function listAftersale(query) {
  return request({
    url: '/aftersale/list',
    method: 'get',
    params: query
  })
}

export function deleteAftersale(data) {
  return request({
    url: '/aftersale/delete',
    method: 'post',
    data
  })
}

export function batchDeleteAftersale(data) {
  return request({
    url: '/aftersale/batch-delete',
    method: 'post',
    data
  })
}
