import request from '@/utils/request'

export function listTopic(query) {
  return request({
    url: '/topic/list',
    method: 'get',
    params: query
  })
}

export function createTopic(data) {
  return request({
    url: '/topic/create',
    method: 'post',
    data
  })
}

export function readTopic(query) {
  return request({
    url: '/topic/read',
    method: 'get',
    params: query
  })
}

export function updateTopic(data) {
  return request({
    url: '/topic/update',
    method: 'post',
    data
  })
}

export function deleteTopic(data) {
  return request({
    url: '/topic/delete',
    method: 'post',
    data
  })
}

export function batchDeleteTopic(data) {
  return request({
    url: '/topic/batch-delete',
    method: 'post',
    data
  })
}
