import Vue from 'vue';
import Router from 'vue-router';
import { getLocalStorage } from '@/utils/local-storage';

import home from './home';
import items from './items';
import user from './user';
import order from './order';
import login from './login';
import store from '../store/index';

Vue.use(Router);

const RouterModel = new Router({
  routes: [...home, ...items, ...user, ...order, ...login]
});

RouterModel.beforeEach((to, from, next) => {
  const { Authorization } = getLocalStorage(
    'Authorization'
  );
  if (!Authorization) {
    if (to.meta.login) {
      console.log("login");
      next({ name: 'login', query: { redirect: to.name } });
      return;
    }
  }
  console.log(to.meta,"meta");
  //页面顶部菜单拦截
  let emptyObj=JSON.stringify(to.meta) == "{}";
  let undefinedObj=typeof(to.meta.showHeader)=="undefined";
  if(!emptyObj&&!undefinedObj){
    store.commit("CHANGE_HEADER",to.meta);
  }else{
    store.commit("CHANGE_HEADER",{showHeader:true,title:""});
  }
  next();
});

export default RouterModel;
