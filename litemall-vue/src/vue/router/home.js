const Tabbar = () =>
  import(/* webpackChunkName: "Tabbar" */ '@/vue/components/Tabbar/');
import asyncLoader from 'core/async-loader';

export default [
  {
    path: '/',
    name: 'home',
    components: {
      default: asyncLoader('home/tabbar-home'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: true
    }
  },
  {
    path: '*',
    redirect: {
      name: 'home'
    }
  }
];
