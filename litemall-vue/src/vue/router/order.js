import asyncLoader from 'core/async-loader';
const tab_cart = asyncLoader('order/tabbar-cart');
const PlaceOrderEntity = asyncLoader('order/place-order-entity');
const orderDetail = asyncLoader('order/orderDetail');
const PlaceOrderVirtual = asyncLoader('order/place-order-virtual');
const Payment = asyncLoader('order/payment');
const PaymentStatus = asyncLoader('order/payment-status');

const Tabbar = () =>
  import(/* webpackChunkName: "Tabbar" */ '@/vue/components/Tabbar/');

export default [
  {
    path: '/order',
    name: 'cart',
    meta: {
      login: true
    },
    components: { default: tab_cart, tabbar: Tabbar }
  },
  {
    path: '/order/placeOrderEntity',
    name: 'placeOrderEntity',
    component: PlaceOrderEntity
  },
  {
    path: '/order/orderDetail',
    name: 'orderDetail',
    component: orderDetail
  },
  {
    path: '/order/placeOrderVirtual',
    name: 'placeOrderVirtual',
    component: PlaceOrderVirtual
  },
  {
    path: '/order/payment',
    name: 'payment',
    component: Payment
  },
  {
    path: '/order/payment/:status',
    name: 'paymentStatus',
    component: PaymentStatus,
    props: true
  }
];
