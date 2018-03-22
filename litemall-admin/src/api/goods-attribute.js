import request from '@/utils/request'

export function listGoodsAttribute(query) {
  return request({
    url: '/goods-attribute/list',
    method: 'get',
    params: query
  })
}

export function createGoodsAttribute(data) {
  return request({
    url: '/goods-attribute/create',
    method: 'post',
    data
  })
}

export function readGoodsAttribute(data) {
  return request({
    url: '/goods-attribute/read',
    method: 'get',
    data
  })
}

export function updateGoodsAttribute(data) {
  return request({
    url: '/goods-attribute/update',
    method: 'post',
    data
  })
}

export function deleteGoodsAttribute(data) {
  return request({
    url: '/goods-attribute/delete',
    method: 'post',
    data
  })
}
