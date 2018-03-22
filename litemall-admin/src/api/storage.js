import axios from 'axios'

// create an axios instance
const service = axios.create({
  baseURL: process.env.OS_API, // api的base_url
  timeout: 5000 // request timeout
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
