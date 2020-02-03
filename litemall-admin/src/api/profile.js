import request from '@/utils/request'

export function changePassword(data) {
  return request({
    url: '/profile/password',
    method: 'post',
    data
  })
}

export function nNotice() {
  return request({
    url: '/profile/nnotice',
    method: 'get'
  })
}

export function listNotice(query) {
  return request({
    url: '/profile/lsnotice',
    method: 'get',
    params: query
  })
}

export function catNotice(data) {
  return request({
    url: '/profile/catnotice',
    method: 'post',
    data
  })
}

export function bcatNotice(data) {
  return request({
    url: '/profile/bcatnotice',
    method: 'post',
    data
  })
}

export function rmotice(data) {
  return request({
    url: '/profile/rmnotice',
    method: 'post',
    data
  })
}

export function brmNotice(data) {
  return request({
    url: '/profile/brmnotice',
    method: 'post',
    data
  })
}

