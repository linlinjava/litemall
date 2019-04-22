import request from '@/utils/request'

// 登录
export const USER_LOGIN = '/wx/auth/login';
export const USER_LOGOUT = '';

// 用户信息
export const USER_PROFILE = '/user-profile';
export const USER_MODIFY_PASSWORD = '';
export const USER_CHANGE_MOBILE = '';

// 验证码
export const USER_SENDCODE = '';

// 地址
export const ADDRESS = '/address';
export const ADDRESS_DEFAULT = '/address-default';

// 收藏
export const GOODS_COLLECT_LIST = '/moreGoods';

export function loginByUsername(data) {
  return request({
    url: '/wx/auth/login',
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