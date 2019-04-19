import asyncLoader from 'core/async-loader';

const Tabbar = () =>
  import(/* webpackChunkName: "Tabbar" */ '@/vue/components/Tabbar/');

export default [
  {
    path: '/items',
    name: 'class',
    meta: {
      keepAlive: true
    },
    components: {
      default: asyncLoader('items/tabbar-class'),
      tabbar: Tabbar
    }
  },
  {
    path: '/items/search',
    name: 'search',
    meta: {
      keepAlive: true
    },
    component: asyncLoader('items/search')
  },
  {
    path: '/items/search/result',
    name: 'search-result',
    meta: {
      keepAlive: true
    },
    component: asyncLoader('items/search-result'),
    props: route => route.query
  },
  {
    path: '/items/detail/:itemId',
    name: 'detail',
    props: true,
    component: asyncLoader('items/detail')
  },
  {
    path: '/items/list',
    name: 'list',
    component: asyncLoader('items/list'),
    props: route => ({
      itemClass: +route.query.itemClass
    })
  }
];
