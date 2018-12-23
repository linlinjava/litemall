import request from '@/utils/request'

export function listRole(query) {
  return request({
    url: '/role/list',
    method: 'get',
    params: query
  })
}

export function createRole(data) {
  return request({
    url: '/role/create',
    method: 'post',
    data
  })
}

export function reroleRole(data) {
  return request({
    url: '/role/rerole',
    method: 'get',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role/update',
    method: 'post',
    data
  })
}

export function deleteRole(data) {
  return request({
    url: '/role/delete',
    method: 'post',
    data
  })
}

export function listSrc(query) {
  return request({
    url: '/role/listSrc',
    method: 'get',
    params: query
  })
}
