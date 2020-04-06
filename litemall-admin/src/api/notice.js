import request from '@/utils/request'

export function listNotice(query) {
  return request({
    url: '/notice/list',
    method: 'get',
    params: query
  })
}

export function createNotice(data) {
  return request({
    url: '/notice/create',
    method: 'post',
    data
  })
}

export function readNotice(query) {
  return request({
    url: '/notice/read',
    method: 'get',
    params: query
  })
}

export function updateNotice(data) {
  return request({
    url: '/notice/update',
    method: 'post',
    data
  })
}

export function deleteNotice(data) {
  return request({
    url: '/notice/delete',
    method: 'post',
    data
  })
}

export function batchDeleteNotice(data) {
  return request({
    url: '/notice/batch-delete',
    method: 'post',
    data
  })
}
