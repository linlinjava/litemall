import { beforeEach, describe, expect, it } from 'vitest';
import {
  cartCheckout,
  orderCancel,
  orderConfirm,
  orderDelete,
  orderDetail,
  orderRefund,
  orderH5pay,
  orderPrepay,
  orderSubmit
} from '@/api/api.js';
import {
  requestCalls,
  resetRequestCalls
} from '../../test-support/request-spy';

describe('mobile order API frontend integration contract', () => {
  beforeEach(() => {
    resetRequestCalls();
  });

  it('MALL-PAY-AC07-FE-IT-001 sends order submit payload to /order/submit', async () => {
    const payload = {
      addressId: 7,
      cartId: 9,
      couponId: -1,
      userCouponId: -1,
      grouponLinkId: 0,
      grouponRulesId: 0,
      message: 'leave at front desk'
    };

    await orderSubmit(payload);

    expect(requestCalls).toEqual([
      {
        url: '/order/submit',
        method: 'post',
        data: payload
      }
    ]);
  });

  it('MALL-PAY-AC01-FE-IT-001 sends JSAPI prepay request with orderId', async () => {
    await orderPrepay({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/prepay',
      method: 'post',
      data: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC01-FE-IT-002 sends H5 pay request with orderId', async () => {
    await orderH5pay({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/h5pay',
      method: 'post',
      data: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC07-FE-IT-002 sends checkout query parameters without mutating payload', async () => {
    const query = {
      cartId: 9,
      addressId: 7,
      couponId: -1,
      userCouponId: -1,
      grouponRulesId: 0
    };

    await cartCheckout(query);

    expect(requestCalls[0]).toEqual({
      url: '/cart/checkout',
      method: 'get',
      params: query
    });
  });

  it('MALL-PAY-AC08-FE-IT-001 sends order detail query with orderId', async () => {
    await orderDetail({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/detail',
      method: 'get',
      params: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC09-FE-IT-001 sends cancel request with orderId for unpaid compensation', async () => {
    await orderCancel({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/cancel',
      method: 'post',
      data: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC12-FE-IT-001 sends refund request with orderId for paid order', async () => {
    await orderRefund({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/refund',
      method: 'post',
      data: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC13-FE-IT-001 sends confirm request with orderId for shipped order', async () => {
    await orderConfirm({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/confirm',
      method: 'post',
      data: { orderId: 1001 }
    });
  });

  it('MALL-PAY-AC14-FE-IT-001 sends delete request with orderId for closed order', async () => {
    await orderDelete({ orderId: 1001 });

    expect(requestCalls[0]).toEqual({
      url: '/order/delete',
      method: 'post',
      data: { orderId: 1001 }
    });
  });
});
