import Vue from 'vue'
import VueI18n from 'vue-i18n' // translations

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import Element from 'element-ui'
import ElementLocale from 'element-ui/lib/locale'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import './icons' // icon
import './permission' // permission control

import * as filters from './filters' // global filters

import permission from '@/directive/permission/index.js' // 权限判断指令

import Print from '@/utils/print' // 打印

Vue.use(VueI18n)

// Load all locales and remember context
// https://kazupon.github.io/vue-i18n/guide/hot-reload.html
function loadMessages() {
  const context = require.context('./locales', false, /[a-z0-9-_]+\.js$/i)

  const messages = context
    .keys()
    .sort()
    .map((key) => ({ key, locale: key.match(/[a-z0-9-_]+/i)[0] }))
    .reduce(
      (messages, { key, locale }) => ({
        ...messages,
        [locale]: context(key).default
      }),
      {}
    )

  return { context, messages }
}

const { context, messages } = loadMessages()

// VueI18n instance
const i18n = new VueI18n({
  locale: 'zh-Hans',
  messages
})

// Update Element UI locale when Vue locale is changed
// https://element.eleme.cn/#/en-US/component/i18n
ElementLocale.i18n((key, value) => i18n.t(key, value))

Vue.use(Print)

Vue.use(Element, {
  size: Cookies.get('size') || 'mini' // set element-ui default size
})

Vue.directive('permission', permission)

// register global utility filters.
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})

// Hot updates
// https://kazupon.github.io/vue-i18n/guide/hot-reload.html
if (module.hot) {
  module.hot.accept(context.id, () => {
    const { messages: newMessages } = loadMessages()

    Object.keys(newMessages)
      .filter((locale) => messages[locale] !== newMessages[locale])
      .forEach((locale) => {
        messages[locale] = newMessages[locale]
        i18n.setLocaleMessage(locale, messages[locale])
      })
  })
}
