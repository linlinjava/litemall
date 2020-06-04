const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/',
    name: 'home',
    components: {
      default: () => import('@/views/home/tabbar-home'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: true,
      showHeader:false
    },
  },
  {
    path: '*',
    redirect: {
      name: 'home'
    }
  }
];
