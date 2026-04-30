import request from '@/utils/request'

export function getReaderPage(params) {
  return request({
    url: '/readers/page',
    method: 'get',
    params
  })
}

export function getReader(id) {
  return request({
    url: `/readers/${id}`,
    method: 'get'
  })
}

export function getReaderByCard(readerCard) {
  return request({
    url: `/readers/card/${readerCard}`,
    method: 'get'
  })
}

export function createReader(data) {
  return request({
    url: '/readers',
    method: 'post',
    data
  })
}

export function updateReader(id, data) {
  return request({
    url: `/readers/${id}`,
    method: 'put',
    data
  })
}

export function deleteReader(id) {
  return request({
    url: `/readers/${id}`,
    method: 'delete'
  })
}

export function batchDeleteReaders(ids) {
  return request({
    url: '/readers/batch',
    method: 'post',
    data: ids
  })
}

export function addToBlacklist(id, reason) {
  return request({
    url: `/readers/${id}/blacklist/add`,
    method: 'put',
    params: { reason }
  })
}

export function removeFromBlacklist(id) {
  return request({
    url: `/readers/${id}/blacklist/remove`,
    method: 'put'
  })
}

export function canBorrow(id) {
  return request({
    url: `/readers/${id}/canBorrow`,
    method: 'get'
  })
}

export function getBorrowCount(id) {
  return request({
    url: `/readers/${id}/borrowCount`,
    method: 'get'
  })
}
