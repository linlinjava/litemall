import axios from 'axios';
import _ from 'lodash';
import qs from 'qs';
import { Dialog, Toast } from 'vant';
import Vue from 'vue';
Vue.use(Toast);
const instance = axios.create({
  timeout: 5000,
  baseURL: ''
});

instance.interceptors.request.use(
  config => {
    if (!config.headers['X-Litemall-Token']) {
      config.headers['X-Litemall-Token'] = `${window.localStorage.getItem(
        'Authorization'
      ) || ''}`;
    }
    return config;
  },
  err => Promise.reject(err)
);

instance.interceptors.response.use(
  res => {
    let litemall = _.has(res.data, 'errno') && res.data.errno !== 0;
    let oldmall = _.has(res.data, 'success') && !res.data.success;
    if (litemall || oldmall) {
      switch (res.data.code || res.data.errno) {
        case 422: {
          const flag = Array.isArray(res.data.data) && res.data.data.length;
          Dialog.alert({
            message: flag ? res.data.data[0].message : res.data.message
          });
          break;
        }
        case 401:
          break;
        case 404:
          break;
        case 740: {
          Toast.fail('优惠券已经领取过');
          break;
        }
        case 501: {
          Toast.fail('请登录');
          setTimeout(() => {
            window.location = '#/login/'
          }, 1500)
          break;
        }
        default:
          Toast.fail(res.data.errmsg)
      }
      return Promise.reject(res);
    }
    return res;
  },
  error => {
    Dialog.alert({
      title: '警告',
      message: error.message
    });
    return Promise.reject(error);
  }
);

const post = (url, data, config = {}) => instance.post(url, data, config);

const put = (url, data, config = {}) => instance.put(url, data, config);

const get = (url, params, config = {}) =>
  instance.get(url, {
    params,
    ...config
  });

const deleteMethod = (url, config = {}) =>
  instance({
    url,
    method: 'delete',
    ...config
  });

export default {
  install(Vue) {
    Vue.prototype.$reqGet = get;
    Vue.prototype.$reqPost = post;
    Vue.prototype.$reqPut = put;
    Vue.prototype.$reqDel = deleteMethod;
  }
};
