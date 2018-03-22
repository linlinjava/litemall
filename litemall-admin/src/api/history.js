import request from '@/utils/request'

export function listHistory(query) {
  return request({
    url: '/history/list',
    method: 'get',
    params: query
  })
}

export function createHistory(data) {
  return request({
    url: '/history/create',
    method: 'post',
    data
  })
}

export function readHistory(data) {
  return request({
    url: '/history/read',
    method: 'get',
    data
  })
}

export function updateHistory(data) {
  return request({
    url: '/history/update',
    method: 'post',
    data
  })
}

export function deleteHistory(data) {
  return request({
    url: '/history/delete',
    method: 'post',
    data
  })
}
