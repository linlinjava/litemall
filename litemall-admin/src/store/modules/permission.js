import { asyncRoutes, constantRoutes } from '@/router'

/**
 * 通过meta.perms判断是否与当前用户权限匹配
 * @param perms
 * @param route
 */
function hasPermission(perms, route) {
  if (route.meta && route.meta.perms) {
    return perms.some(perm => route.meta.perms.includes(perm))
  } else {
    return true
  }
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param routes asyncRoutes
 * @param perms
 */
function filterAsyncRoutes(routes, perms) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (tmp.children) {
      tmp.children = filterAsyncRoutes(tmp.children, perms)
      if (tmp.children && tmp.children.length > 0) {
        res.push(tmp)
      }
    } else {
      if (hasPermission(perms, tmp)) {
        res.push(tmp)
      }
    }
  })

  return res
}

const permission = {
  state: {
    routes: constantRoutes,
    addRoutes: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        const { perms } = data
        let accessedRoutes
        if (perms.includes('*')) {
          accessedRoutes = asyncRoutes
        } else {
          accessedRoutes = filterAsyncRoutes(asyncRoutes, perms)
        }
        commit('SET_ROUTES', accessedRoutes)
        resolve()
      })
    }
  }
}

export default permission
