import { describe, expect, it, beforeEach } from 'vitest';
import { yuan } from '@/filter/index.js';
import {
  getLocalStorage,
  removeLocalStorage,
  setLocalStorage
} from '@/utils/local-storage.js';
import {
  checkoutSummary,
  orderActionState
} from '../../src/domain/litemallPaymentRules';

describe('mobile payment frontend unit rules', () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it('MALL-PAY-AC01-FE-UT-001 formats cents into yuan display text', () => {
    expect(yuan(0)).toBe('¥0.00');
    expect(yuan(10000)).toBe('¥100.00');
    expect(yuan(105)).toBe('¥1.05');
    expect(yuan(1)).toBe('¥0.01');
    expect(yuan('10000')).toBe('10000');
  });

  it('MALL-PAY-AC07-FE-UT-001 persists checkout identifiers in localStorage', () => {
    setLocalStorage({
      AddressId: 7,
      CartId: 9,
      CouponId: -1,
      UserCouponId: -1
    });

    expect(getLocalStorage('AddressId', 'CartId', 'CouponId', 'UserCouponId')).toEqual({
      AddressId: '7',
      CartId: '9',
      CouponId: '-1',
      UserCouponId: '-1'
    });

    removeLocalStorage('CouponId', 'UserCouponId');

    expect(getLocalStorage('CouponId', 'UserCouponId')).toEqual({
      CouponId: null,
      UserCouponId: null
    });
  });

  it('MALL-PAY-AC11-FE-UT-001 maps backend order status into visible actions', () => {
    expect(orderActionState(101)).toEqual({
      statusText: '未付款',
      canPay: true,
      canCancel: true,
      canRefund: false,
      canConfirm: false,
      canDelete: false,
      canAftersale: false
    });
    expect(orderActionState(102)).toEqual({
      statusText: '已取消',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: true,
      canAftersale: false
    });
    expect(orderActionState(103)).toEqual({
      statusText: '已取消(系统)',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: true,
      canAftersale: false
    });
    expect(orderActionState(201)).toEqual({
      statusText: '已付款',
      canPay: false,
      canCancel: false,
      canRefund: true,
      canConfirm: false,
      canDelete: false,
      canAftersale: false
    });
    expect(orderActionState(202)).toEqual({
      statusText: '订单取消，退款中',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: false,
      canAftersale: false
    });
    expect(orderActionState(203)).toEqual({
      statusText: '已退款',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: true,
      canAftersale: false
    });
    expect(orderActionState(204)).toEqual({
      statusText: '已超时团购',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: false,
      canAftersale: false
    });
    expect(orderActionState(301)).toEqual({
      statusText: '已发货',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: true,
      canDelete: false,
      canAftersale: false
    });
    expect(orderActionState(401)).toEqual({
      statusText: '已收货',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: true,
      canAftersale: true
    });
    expect(orderActionState(402)).toEqual({
      statusText: '已收货(系统)',
      canPay: false,
      canCancel: false,
      canRefund: false,
      canConfirm: false,
      canDelete: true,
      canAftersale: true
    });
  });

  it('MALL-PAY-AC03-FE-UT-001 summarizes payable amount with coupon and groupon discounts', () => {
    expect(checkoutSummary({
      goodsTotalPrice: 120,
      freightPrice: 10,
      couponPrice: 15,
      grouponPrice: 20,
      actualPrice: 95
    })).toEqual({
      goods: '120.00',
      freight: '10.00',
      coupon: '15.00',
      groupon: '20.00',
      payable: '95.00'
    });
  });
});
