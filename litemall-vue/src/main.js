import Vue from 'vue';
import App from './App.vue';
import router from './vue/router';
import './assets/scss/global.scss';
import '@/assets/scss/iconfont/iconfont.css';

import VeeValidate, { Validator } from 'vee-validate';
import VueCountdown from '@/vue/plugins/vue-countdown';
import zhCN from 'vee-validate/dist/locale/zh_CN';

import axios from '@/vue/plugins/axios';
import filters from '@/vue/filter';

import psm from '@/core/utils/psm'
import {
  procJumpToken
} from "@/core/utils/auth";

Vue.use(VueCountdown);
Vue.use(axios);
Vue.use(filters);

Validator.localize('zh-CN', zhCN);
Vue.use(VeeValidate, {
  locale: 'zh-CN'
});

import { Lazyload, Icon, Cell, CellGroup, loading, Button, Toast } from 'vant';
Vue.use(Icon);
Vue.use(Cell);
Vue.use(CellGroup);
Vue.use(loading);
Vue.use(Button);
Vue.use(Toast);
Vue.use(Lazyload, {
  preLoad: 1.3,
  error: require('@/assets/images/goods_default.png'),
  loading: require('@/assets/images/goods_default.png'),
  attempt: 1,
  listenEvents: ['scroll'],
  lazyComponent: true
});

Vue.prototype.PSMU = psm.psmu
PSMU.setup()

procJumpToken()

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App)
}).$mount('#app');
