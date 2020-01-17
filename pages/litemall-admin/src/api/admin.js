import request from '@/utils/request'

export function listAdmin(query) {
  return request({
    url: '/admin/list',
    method: 'get',
    params: query
  })
}

export function createAdmin(data) {
  return request({
    url: '/admin/create',
    method: 'post',
    data
  })
}

export function readminAdmin(data) {
  return request({
    url: '/admin/readmin',
    method: 'get',
    data
  })
}

export function updateAdmin(data) {
  return request({
    url: '/admin/update',
    method: 'post',
    data
  })
}

export function deleteAdmin(data) {
  return request({
    url: '/admin/delete',
    method: 'post',
    data
  })
}
