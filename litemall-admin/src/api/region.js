import request from '@/utils/request'

export function listRegion() {
  return request({
    url: '/region/list',
    method: 'get'
  })
}

export function listSubRegion(query) {
  return request({
    url: '/region/clist',
    method: 'get',
    params: query
  })
}
