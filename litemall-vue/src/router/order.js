const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/order',
    name: 'cart',
    meta: {
      login: true
    },
    components: { 
      default: () => import('@/views/order/tabbar-cart'), 
      tabbar: Tabbar 
    }
  },
  {
    path: '/order/placeOrderEntity',
    name: 'placeOrderEntity',
    component: () => import('@/views/order/place-order-entity')
  },
  {
    path: '/order/orderDetail',
    name: 'orderDetail',
    component: () => import('@/views/order/orderDetail')
  },
  {
    path: '/order/payment',
    name: 'payment',
    component: () => import('@/views/order/payment')
  },
  {
    path: '/order/payment/:status',
    name: 'paymentStatus',
    component: () => import('@/views/order/payment-status'),
    props: true
  }
];
