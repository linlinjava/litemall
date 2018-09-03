import request from '@/utils/request'

export function listAddress(query) {
  return request({
    url: '/address/list',
    method: 'get',
    params: query
  })
}
