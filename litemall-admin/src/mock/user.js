import Mock from 'mockjs'
import { param2Obj } from '@/utils'
import resAPI from './res'

const List = []
const count = 100

for (let i = 0; i < count; i++) {
  List.push(Mock.mock({
    'id': '@increment',
    'username': '@cname',
    'mobile': '@string("number", 11)',
    'age|10-30': 0,
    'birthday': '@date("yyyy-MM-dd")',
    'gender': '@integer(0, 2)',
    'status|1': ['可用', '禁用', '删除']
  }))
}

export default {
  getList: config => {
    const { username, mobile, sort, page = 1, limit = 20 } = param2Obj(config.url)

    let mockList = List.filter(item => {
      if (username && item.username.indexOf(username) < 0) return false
      if (mobile && (item.mobile !== mobile)) return false
      return true
    })

    if (sort === '-id') {
      mockList = mockList.reverse()
    }

    const pageList = mockList.filter((item, index) => index < limit * page && index >= limit * (page - 1))

    return resAPI.ok({
      total: mockList.length,
      items: pageList
    })
  },
  createUser: () => {
    return resAPI.ok()
  },
  readUser: () => {
    return resAPI.ok()
  },
  updateUser: () => {
    return resAPI.ok()
  },
  deleteUser: () => {
    return resAPI.ok()
  }
}
