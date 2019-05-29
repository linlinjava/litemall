import dayjs from 'dayjs';
import { isNumber } from 'lodash';
export const dateFormat = (value, format = 'YYYY-MM-DD') =>
  value ? dayjs(value * 1000).format(format) : '';

export const yuan = value =>
  isNumber(value) ? `¥${(value / 100).toFixed(2)}` : value;

export default {
  install(Vue) {
    Vue.filter('yuan', yuan);
    Vue.filter('dateFormat', dateFormat);
  }
};
