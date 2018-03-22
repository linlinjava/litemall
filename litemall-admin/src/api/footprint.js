import request from '@/utils/request'

export function listFootprint(query) {
  return request({
    url: '/footprint/list',
    method: 'get',
    params: query
  })
}

export function createFootprint(data) {
  return request({
    url: '/footprint/create',
    method: 'post',
    data
  })
}

export function readFootprint(data) {
  return request({
    url: '/footprint/read',
    method: 'get',
    data
  })
}

export function updateFootprint(data) {
  return request({
    url: '/footprint/update',
    method: 'post',
    data
  })
}

export function deleteFootprint(data) {
  return request({
    url: '/footprint/delete',
    method: 'post',
    data
  })
}
