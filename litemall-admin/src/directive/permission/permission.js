
import store from '@/store'

export default{
  inserted(el, binding, vnode) {
    const { value } = binding
    const perms = store.getters && store.getters.perms

    if (value && value instanceof Array && value.length > 0) {
      const permissions = value

      var hasPermission = false

      if (perms.indexOf('*') >= 0) {
        hasPermission = true
      } else {
        hasPermission = perms.some(perm => {
          return permissions.includes(perm)
        })
      }

      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`need perms! Like v-permission="['GET /aaa','POST /bbb']"`)
    }
  }
}
