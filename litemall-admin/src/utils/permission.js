import store from '@/store'

/**
 * @param {Array} value
 * @returns {Boolean}
 * @example see @/views/permission/directive.vue
 */
export default function checkPermission(value) {
  if (value && value instanceof Array && value.length > 0) {
    const perms = store.getters && store.getters.perms
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
      return false
    }
    return true
  } else {
    console.error(`need perms! Like v-permission="['GET /aaa','POST /bbb']"`)
    return false
  }
}
