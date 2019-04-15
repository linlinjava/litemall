import asyncLoader from 'core/async-loader';
const login = asyncLoader('login/login');
const registerGetCode = asyncLoader('login/register-getCode');
const registerSubmit = asyncLoader('login/register-submit');
const registerStatus = asyncLoader('login/register-status');
const forget = asyncLoader('login/forget');
const forgetReset = asyncLoader('login/forget-reset');
const forgetStatus = asyncLoader('login/forget-status');

export default [
  {
    path: '/login',
    name: 'login',
    component: login
  },
  {
    path: '/login/registerGetCode',
    name: 'registerGetCode',
    component: registerGetCode
  },
  {
    path: '/login/registerSubmit',
    name: 'registerSubmit',
    component: registerSubmit
  },
  {
    path: '/login/registerStatus/:status',
    name: 'registerStatus',
    props: true,
    component: registerStatus
  },
  {
    path: '/login/forget',
    name: 'forget',
    component: forget
  },
  {
    path: '/login/forget/reset',
    name: 'forgetReset',
    component: forgetReset
  },
  {
    path: '/login/forget/reset/:status',
    name: 'forgetStatus',
    props: true,
    component: forgetStatus
  }
];
