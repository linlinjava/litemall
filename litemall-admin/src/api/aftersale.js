import request from '@/utils/request'

export function listAftersale(query) {
  return request({
    url: '/aftersale/list',
    method: 'get',
    params: query
  })
}

export function receptAftersale(data) {
  return request({
    url: '/aftersale/recept',
    method: 'post',
    data
  })
}

export function batchReceptAftersale(data) {
  return request({
    url: '/aftersale/batch-recept',
    method: 'post',
    data
  })
}

export function rejectAftersale(data) {
  return request({
    url: '/aftersale/reject',
    method: 'post',
    data
  })
}

export function batchRejectAftersale(data) {
  return request({
    url: '/aftersale/batch-reject',
    method: 'post',
    data
  })
}

export function refundAftersale(data) {
  return request({
    url: '/aftersale/refund',
    method: 'post',
    data
  })
}
