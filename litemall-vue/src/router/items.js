const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/items',
    name: 'class',
    meta: {
      keepAlive: true,
      showHeader:false
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
      keepAlive: true,
      title:"搜索",
      showHeader:false
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
    path: '/items/category',
    name: 'category',
    component: () => import('@/views/items/category'),
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
  },
  {
    path: '/items/brand/:brandId',
    name: 'brand',
    props: true,
    component: () => import('@/views/items/brand')
  },
  {
    path: '/items/brand-list',
    name: 'brandList',
    component: () => import('@/views/items/brand-list'),
    props: route => route.query
  },
  {
    path: '/items/topic/:topicId',
    name: 'topic',
    props: true,
    component: () => import('@/views/items/topic')
  },
  {
    path: '/items/topic-list',
    name: 'topicList',
    component: () => import('@/views/items/topic-list'),
    props: route => route.query
  }
];
