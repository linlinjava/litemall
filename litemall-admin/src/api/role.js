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

export function readRole(data) {
  return request({
    url: '/role/read',
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

export function getPermission(query) {
  return request({
    url: '/role/permissions',
    method: 'get',
    params: query
  })
}

export function updatePermission(data) {
  return request({
    url: '/role/permissions',
    method: 'post',
    data
  })
}

export function roleOptions(query) {
  return request({
    url: '/role/options',
    method: 'get',
    params: query
  })
}
