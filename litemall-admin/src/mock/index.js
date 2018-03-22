import Mock from 'mockjs'
import userAPI from './user'

// 用户相关
Mock.mock(/\/user\/list/, 'get', userAPI.getList)
Mock.mock(/\/user\/create/, 'post', userAPI.createUser)
Mock.mock(/\/user\/update/, 'post', userAPI.updateUser)
Mock.mock(/\/user\/read/, 'get', userAPI.readUser)
Mock.mock(/\/user\/delete/, 'post', userAPI.deleteUser)

export default Mock
