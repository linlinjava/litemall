import axios from 'axios'
import { Message } from 'element-ui'

// create an axios instance
const service = axios.create({
  baseURL: process.env.OS_API, // api的base_url
  timeout: 5000 // request timeout
})

// respone interceptor
service.interceptors.response.use(
  response => {
    return response
  }, error => {
    console.log('err' + error)// for debug
    Message({
      message: '对象存储服务访问超时，请检查链接是否能够访问。',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  })

export function listStorage(query) {
  return service({
    url: '/storage/list',
    method: 'get',
    params: query
  })
}

export function createStorage(data) {
  return service({
    url: '/storage/create',
    method: 'post',
    data
  })
}

export function readStorage(data) {
  return service({
    url: '/storage/read',
    method: 'get',
    data
  })
}

export function updateStorage(data) {
  return service({
    url: '/storage/update',
    method: 'post',
    data
  })
}

export function deleteStorage(data) {
  return service({
    url: '/storage/delete',
    method: 'post',
    data
  })
}

const uploadPath = process.env.OS_API + '/storage/create'
export { uploadPath }
