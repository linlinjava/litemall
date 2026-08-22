import { beforeEach, describe, expect, it } from 'vitest';
import {
  getHome,
  goodsCategory,
  goodsCount,
  goodsDetail,
  goodsList,
  goodsRelated
} from '@/api/api.js';
import {
  requestCalls,
  resetRequestCalls
} from '../../test-support/request-spy';

describe('mobile product browsing API frontend integration contract', () => {
  beforeEach(() => {
    resetRequestCalls();
  });

  it('MALL-BROWSE-AC01-FE-IT-001 requests homepage merchandising blocks', async () => {
    await getHome();

    expect(requestCalls[0]).toEqual({
      url: '/home/index',
      method: 'get'
    });
  });

  it('MALL-BROWSE-AC02-FE-IT-001 sends goods list filters and pagination unchanged', async () => {
    const query = {
      categoryId: 20,
      brandId: 5,
      keyword: '青瓷',
      isNew: true,
      isHot: false,
      page: 3,
      limit: 8,
      sort: 'retail_price',
      order: 'asc'
    };

    await goodsList(query);

    expect(requestCalls[0]).toEqual({
      url: '/goods/list',
      method: 'get',
      params: query
    });
  });

  it('MALL-BROWSE-AC03-FE-IT-001 requests goods detail by id', async () => {
    await goodsDetail({ id: 100 });

    expect(requestCalls[0]).toEqual({
      url: '/goods/detail',
      method: 'get',
      params: { id: 100 }
    });
  });

  it('MALL-BROWSE-AC04-FE-IT-001 requests related goods by id', async () => {
    await goodsRelated({ id: 100 });

    expect(requestCalls[0]).toEqual({
      url: '/goods/related',
      method: 'get',
      params: { id: 100 }
    });
  });

  it('MALL-BROWSE-AC05-FE-IT-001 requests category sibling filter data by id', async () => {
    await goodsCategory({ id: 20 });

    expect(requestCalls[0]).toEqual({
      url: '/goods/category',
      method: 'get',
      params: { id: 20 }
    });
  });

  it('MALL-BROWSE-AC06-FE-IT-001 requests on-sale goods count', async () => {
    await goodsCount();

    expect(requestCalls[0]).toEqual({
      url: '/goods/count',
      method: 'get'
    });
  });
});
