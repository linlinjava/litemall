const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/items',
    name: 'class',
    meta: {
      keepAlive: true
    },
    components: {
      default:  () => import('@/views/items/tabbar-catalog'),
      tabbar: Tabbar
    }
  },
  {
    path: '/items/search',
    name: 'search',
    meta: {
      keepAlive: true
    },
    component: () => import('@/views/items/search')
  },
  {
    path: '/items/detail/:itemId',
    name: 'detail',
    props: true,
    component: () => import('@/views/items/detail')
  },
  {
    path: '/items/list',
    name: 'list',
    component: () => import('@/views/items/list'),
    props: route => route.query
  },
  {
    path: '/items/hot',
    name: 'hot',
    component: () => import('@/views/items/hot'),
    props: route => route.query
  },
  {
    path: '/items/new',
    name: 'new',
    component: () => import('@/views/items/new'),
    props: route => route.query
  },
  {
    path: '/items/groupon',
    name: 'groupon',
    component: () => import('@/views/items/groupon'),
    props: route => route.query
  }
];
