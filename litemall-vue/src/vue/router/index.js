import Vue from 'vue';
import Router from 'vue-router';
import { getLocalStorage } from '@/core/utils/local-storage';

import home from './home';
import items from './items';
import user from './user';
import order from './order';
import login from './login';

Vue.use(Router);

const RouterModel = new Router({
  routes: [...home, ...items, ...user, ...order, ...login]
});

RouterModel.beforeEach((to, from, next) => {
  const { Authorization, user_id } = getLocalStorage(
    'Authorization',
    'user_id'
  );
  if (!Authorization && !user_id) {
    if (to.meta.login) {
      next({ name: 'login', query: { redirect: to.name } });
      return;
    }
  }
  next();
});

export default RouterModel;
