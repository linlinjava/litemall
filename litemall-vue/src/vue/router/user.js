import asyncLoader from 'core/async-loader';

const tab_user = asyncLoader('user/tabbar-user');
const UserCollect = asyncLoader('user/module-collect');
const UserTeam = asyncLoader('user/module-team');
const UserInvitation = asyncLoader('user/module-invitation');
const UserAddress = asyncLoader('user/module-address');
const UserAddressEdit = asyncLoader('user/module-address-edit');
const UserAutonym = asyncLoader('user/module-autonym');
const UserAutonymEdit = asyncLoader('user/module-autonym-edit');
const UserServer = asyncLoader('user/module-server');

const UserInformation = asyncLoader('user/user-information-set');
const UserInfo_SetBg = asyncLoader('user/user-information-set/set-bg');
const UserInfo_SetMobile = asyncLoader('user/user-information-set/set-mobile');
const UserInfo_SetNickname = asyncLoader(
  'user/user-information-set/set-nickname'
);
const UserInfo_SetPassword = asyncLoader(
  'user/user-information-set/set-password'
);

const UserOrderEntityList = asyncLoader('user/order-entity-list');
const UserOrderEleList = asyncLoader('user/order-ele-list');
const UserRefundList = asyncLoader('user/refund-list');

const Tabbar = () =>
  import(/* webpackChunkName: "Tabbar" */ '@/vue/components/Tabbar/');

export default [
  {
    path: '/user',
    name: 'user',
    meta: {
      keepAlive: true
    },
    components: { default: tab_user, tabbar: Tabbar }
  },
  {
    path: '/user/collect',
    name: 'collect',
    meta: {
      login: true
    },
    component: UserCollect
  },
  {
    path: '/user/team',
    name: 'team',
    meta: {
      login: true
    },
    component: UserTeam
  },
  {
    path: '/user/invitation',
    name: 'invitation',
    meta: {
      login: true
    },
    component: UserInvitation
  },
  {
    path: '/user/address',
    name: 'address',
    meta: {
      login: true
    },
    component: UserAddress
  },
  {
    path: '/user/address/edit/:addressId',
    name: 'address-edit',
    props: true,
    meta: {
      login: true
    },
    component: UserAddressEdit
  },
  {
    path: '/user/autonym',
    name: 'autonym',
    component: UserAutonym
  },
  {
    path: '/user/autonym/edit',
    name: 'autonym-edit',
    component: UserAutonymEdit
  },
  {
    path: '/user/server',
    name: 'user-server',
    component: UserServer
  },
  {
    path: '/user/information',
    name: 'user-information',
    meta: {
      login: true
    },
    component: UserInformation
  },
  {
    path: '/user/information/setbg',
    name: 'user-info-setbg',
    component: UserInfo_SetBg
  },
  {
    path: '/user/information/setMobile',
    name: 'user-info-setMobile',
    component: UserInfo_SetMobile
  },
  {
    path: '/user/information/setNickname',
    name: 'user-info-setNickname',
    component: UserInfo_SetNickname
  },
  {
    path: '/user/information/setPassword',
    name: 'user-info-setPassword',
    component: UserInfo_SetPassword
  },
  {
    path: '/user/order/list/:active',
    name: 'user-order-list',
    props: true,
    component: UserOrderEntityList
  },
  {
    path: '/user/orderEle/list/:active',
    name: 'user-order-ele-list',
    props: true,
    component: UserOrderEleList
  },
  {
    path: '/user/refund/list',
    name: 'user-refund-list',
    component: UserRefundList
  }
];
