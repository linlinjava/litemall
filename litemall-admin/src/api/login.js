import request from '@/utils/request'

export function loginByUsername(username, password, code) {
  const data = {
    username,
    password,
    code
  }
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getUserInfo(token) {
  return request({
    url: '/auth/info',
    method: 'get',
    params: { token }
  })
}

export function getKaptcha() {
  return request({
    url: '/auth/kaptcha',
    method: 'get'
  })
}
