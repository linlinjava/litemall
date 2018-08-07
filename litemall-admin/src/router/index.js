import Vue from 'vue'
import Router from 'vue-router'
const _import = require('./_import_' + process.env.NODE_ENV)
// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/** note: submenu only apppear when children.length>=1
*   detail see  https://panjiachen.github.io/vue-element-admin-site/#/router-and-nav?id=sidebar
**/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    roles: ['admin','editor']     will control the page roles (you can set multiple roles)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if fasle ,the page will no be cached(default is false)
  }
**/
export const constantRouterMap = [
  { path: '/login', component: _import('login/index'), hidden: true },
  { path: '/404', component: _import('error/404'), hidden: true },
  { path: '/401', component: _import('error/401'), hidden: true },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [{
      path: 'dashboard',
      component: _import('dashboard/index'),
      name: 'dashboard',
      meta: { title: '首页', icon: 'dashboard', noCache: true }
    }]
  }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  {
    path: '/user',
    component: Layout,
    redirect: 'noredirect',
    name: 'userManage',
    meta: {
      title: '用户管理',
      icon: 'chart'
    },
    children: [
      { path: 'user', component: _import('user/user'), name: 'user', meta: { title: '会员管理', noCache: true }},
      { path: 'address', component: _import('user/address'), name: 'address', meta: { title: '收货地址', noCache: true }},
      { path: 'collect', component: _import('user/collect'), name: 'collect', meta: { title: '会员收藏', noCache: true }},
      { path: 'footprint', component: _import('user/footprint'), name: 'footprint', meta: { title: '会员足迹', noCache: true }},
      { path: 'history', component: _import('user/history'), name: 'history', meta: { title: '搜索历史', noCache: true }}
    ]
  },

  {
    path: '/mall',
    component: Layout,
    redirect: 'noredirect',
    name: 'mallManage',
    meta: {
      title: '商场管理',
      icon: 'chart'
    },
    children: [
      { path: 'region', component: _import('mall/region'), name: 'region', meta: { title: '行政区域', noCache: true }},
      { path: 'brand', component: _import('mall/brand'), name: 'brand', meta: { title: '品牌制造商', noCache: true }},
      { path: 'category', component: _import('mall/category'), name: 'category', meta: { title: '商品类目', noCache: true }},
      { path: 'order', component: _import('mall/order'), name: 'order', meta: { title: '订单管理', noCache: true }},
      { path: 'issue', component: _import('mall/issue'), name: 'issue', meta: { title: '通用问题', noCache: true }},
      { path: 'keyword', component: _import('mall/keyword'), name: 'keyword', meta: { title: '关键词', noCache: true }}
    ]
  },

  {
    path: '/goods',
    component: Layout,
    redirect: 'noredirect',
    name: 'goodsManage',
    meta: {
      title: '商品管理',
      icon: 'chart'
    },
    children: [
      { path: 'list', component: _import('goods/list'), name: 'goodsList', meta: { title: '商品列表', noCache: true }},
      { path: 'create', component: _import('goods/create'), name: 'goodsCreate', meta: { title: '商品上架', noCache: true }},
      { path: 'edit', component: _import('goods/edit'), name: 'goodsEdit', meta: { title: '商品编辑', noCache: true }, hidden: true },
      { path: 'comment', component: _import('goods/comment'), name: 'goodsComment', meta: { title: '商品评论', noCache: true }}
    ]
  },

  {
    path: '/groupon',
    component: Layout,
    redirect: 'noredirect',
    name: 'grouponManage',
    meta: {
      title: '团购管理',
      icon: 'chart'
    },
    children: [
      { path: 'list', component: _import('groupon/list'), name: 'list', meta: { title: '团购规则', noCache: true }},
      { path: 'record', component: _import('groupon/record'), name: 'record', meta: { title: '团购活动', noCache: true }}
    ]
  },

  {
    path: '/promotion',
    component: Layout,
    redirect: 'noredirect',
    name: 'promotionManage',
    meta: {
      title: '推广管理',
      icon: 'chart'
    },
    children: [
      { path: 'ad', component: _import('promotion/ad'), name: 'ad', meta: { title: '广告列表', noCache: true }},
      { path: 'topic', component: _import('promotion/topic'), name: 'topic', meta: { title: '专题管理', noCache: true }}
    ]
  },

  {
    path: '/sys',
    component: Layout,
    redirect: 'noredirect',
    name: 'sysManage',
    meta: {
      title: '系统管理',
      icon: 'chart'
    },
    children: [
      { path: 'admin', component: _import('sys/admin'), name: 'admin', meta: { title: '管理员', noCache: true }},
      { path: 'os', component: _import('sys/os'), name: 'os', meta: { title: '对象存储', noCache: true }}
    ]
  },

  {
    path: '/stat',
    component: Layout,
    redirect: 'noredirect',
    name: 'statManage',
    meta: {
      title: '统计',
      icon: 'chart'
    },
    children: [
      { path: 'user', component: _import('stat/user'), name: 'statUser', meta: { title: '用户统计', noCache: true }},
      { path: 'order', component: _import('stat/order'), name: 'statOrder', meta: { title: '订单统计', noCache: true }},
      { path: 'goods', component: _import('stat/goods'), name: 'statGoods', meta: { title: '商品统计', noCache: true }}
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
]
