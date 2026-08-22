export type OrderActionState = {
  statusText: string;
  canPay: boolean;
  canCancel: boolean;
  canRefund: boolean;
  canConfirm: boolean;
  canDelete: boolean;
  canAftersale: boolean;
};

export function orderActionState(status: number): OrderActionState {
  switch (status) {
    case 101:
      return state('未付款', { canPay: true, canCancel: true });
    case 102:
      return state('已取消', { canDelete: true });
    case 103:
      return state('已取消(系统)', { canDelete: true });
    case 201:
      return state('已付款', { canRefund: true });
    case 202:
      return state('订单取消，退款中');
    case 203:
      return state('已退款', { canDelete: true });
    case 204:
      return state('已超时团购');
    case 301:
      return state('已发货', { canConfirm: true });
    case 401:
    case 402:
      return state(status === 401 ? '已收货' : '已收货(系统)', {
        canDelete: true,
        canAftersale: true
      });
    default:
      throw new Error(`unsupported litemall order status: ${status}`);
  }
}

export function checkoutSummary(input: {
  goodsTotalPrice: number;
  freightPrice: number;
  couponPrice: number;
  grouponPrice: number;
  actualPrice: number;
}) {
  return {
    goods: input.goodsTotalPrice.toFixed(2),
    freight: input.freightPrice.toFixed(2),
    coupon: input.couponPrice.toFixed(2),
    groupon: input.grouponPrice.toFixed(2),
    payable: input.actualPrice.toFixed(2)
  };
}

function state(statusText: string, enabled: Partial<Omit<OrderActionState, 'statusText'>> = {}): OrderActionState {
  return {
    statusText,
    canPay: false,
    canCancel: false,
    canRefund: false,
    canConfirm: false,
    canDelete: false,
    canAftersale: false,
    ...enabled
  };
}
