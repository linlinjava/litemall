import request from '@/utils/request'

export function listFeedback(query) {
  return request({
    url: '/feedback/list',
    method: 'get',
    params: query
  })
}

export function createFeedback(data) {
  return request({
    url: '/feedback/create',
    method: 'post',
    data
  })
}

export function readFeedback(data) {
  return request({
    url: '/feedback/read',
    method: 'get',
    data
  })
}

export function updateFeedback(data) {
  return request({
    url: '/feedback/update',
    method: 'post',
    data
  })
}

export function deleteFeedback(data) {
  return request({
    url: '/feedback/delete',
    method: 'post',
    data
  })
}
