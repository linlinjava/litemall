import request from '@/utils/request'

export function listComment(query) {
  return request({
    url: '/comment/list',
    method: 'get',
    params: query
  })
}

export function createComment(data) {
  return request({
    url: '/comment/create',
    method: 'post',
    data
  })
}

export function readComment(data) {
  return request({
    url: '/comment/read',
    method: 'get',
    data
  })
}

export function updateComment(data) {
  return request({
    url: '/comment/update',
    method: 'post',
    data
  })
}

export function deleteComment(data) {
  return request({
    url: '/comment/delete',
    method: 'post',
    data
  })
}
