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
    path: '/items/search/result',
    name: 'search-result',
    meta: {
      keepAlive: true
    },
    component: () => import('@/views/items/search-result'),
    props: route => route.query
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
  }
];
