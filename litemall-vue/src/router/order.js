const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/order',
    name: 'cart',
    meta: {
      login: true,
      showHeader:false,
      title:"购物车"
    },
    components: { 
      default: () => import('@/views/order/tabbar-cart'), 
      tabbar: Tabbar 
    }
  },
  {
    path: '/order/checkout',
    name: 'orderCheckout',
    component: () => import('@/views/order/checkout')
  },
  {
    path: '/order/order-detail',
    name: 'orderDetail',
    component: () => import('@/views/order/order-detail')
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
