import request from '@/utils/request'

export function changePassword(data) {
  return request({
    url: '/profile/password',
    method: 'post',
    data
  })
}
