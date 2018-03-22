import request from '@/utils/request'

export function listRegion(query) {
  return request({
    url: '/region/list',
    method: 'get',
    params: query
  })
}

export function listSubRegion(query) {
  return request({
    url: '/region/clist',
    method: 'get',
    params: query
  })
}
