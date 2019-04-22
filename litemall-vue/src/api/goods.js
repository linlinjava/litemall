import request from '@/utils/request'

// export const GOODS_CATEGORY = '/category';
export const GOODS_CATEGORY = '/wx/catalog/index';
export const GOODS_CHANNGE_CATEGORY = '/wx/catalog/current?id=';
export const GOODS_SEARCH = '/moreGoods';
export const GOODS_DETAIL = '/details';

export function goodsCategory(query) {
    return request({
      url: '/wx/catalog/index',
      method: 'get',
      params: query
    })
}

export function goodsChannelCategory(query) {
    return request({
      url: '/wx/catalog/index',
      method: 'get',
      params: query
    })
}
  
export function goodsSearch(query) {
    return request({
      url: '/wx/catalog/current?id=',
      method: 'get',
      params: query
    })
}
  
export function goodsDetail(query) {
    return request({
      url: '/wx/catalog/index',
      method: 'get',
      params: query
    })
}
    