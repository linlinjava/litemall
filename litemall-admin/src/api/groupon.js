import request from '@/utils/request'

export function listRecord(query) {
  return request({
    url: '/groupon/listRecord',
    method: 'get',
    params: query
  })
}

export function listGroupon(query) {
  return request({
    url: '/groupon/list',
    method: 'get',
    params: query
  })
}

export function deleteGroupon(data) {
  return request({
    url: '/groupon/delete',
    method: 'post',
    data
  })
}

export function publishGroupon(data) {
  return request({
    url: '/groupon/create',
    method: 'post',
    data
  })
}

export function editGroupon(data) {
  return request({
    url: '/groupon/update',
    method: 'post',
    data
  })
}
