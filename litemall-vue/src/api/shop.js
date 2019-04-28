import request from '@/utils/request'

export const HOME_module = '/home';
export const ALL_GOODS = '/moreGoods';

export const SHOPINFO = '/shop-info';

// 运费模板
export const POST_FEE = '';

export function getHome(query) {
    return request({
      url: '/wx/home/index',
      method: 'get',
      params: query
    })
}
