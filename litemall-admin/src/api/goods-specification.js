import request from '@/utils/request'

export function listGoodsSpecification(query) {
  return request({
    url: '/goods-specification/list',
    method: 'get',
    params: query
  })
}

export function createGoodsSpecification(data) {
  return request({
    url: '/goods-specification/create',
    method: 'post',
    data
  })
}

export function readGoodsSpecification(data) {
  return request({
    url: '/goods-specification/read',
    method: 'get',
    data
  })
}

export function updateGoodsSpecification(data) {
  return request({
    url: '/goods-specification/update',
    method: 'post',
    data
  })
}

export function deleteGoodsSpecification(data) {
  return request({
    url: '/goods-specification/delete',
    method: 'post',
    data
  })
}

export function listGoodsSpecificationVo(query) {
  return request({
    url: '/goods-specification/volist',
    method: 'get',
    params: query
  })
}
