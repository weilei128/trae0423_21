import request from '@/utils/request'

export function getCollectionPage(params) {
  return request({
    url: '/collections/page',
    method: 'get',
    params
  })
}

export function getCollection(id) {
  return request({
    url: `/collections/${id}`,
    method: 'get'
  })
}

export function getCollectionByBarcode(barcode) {
  return request({
    url: `/collections/barcode/${barcode}`,
    method: 'get'
  })
}

export function createCollection(data) {
  return request({
    url: '/collections',
    method: 'post',
    data
  })
}

export function updateCollection(id, data) {
  return request({
    url: `/collections/${id}`,
    method: 'put',
    data
  })
}

export function deleteCollection(id) {
  return request({
    url: `/collections/${id}`,
    method: 'delete'
  })
}

export function batchDeleteCollections(ids) {
  return request({
    url: '/collections/batch',
    method: 'post',
    data: ids
  })
}

export function updateCollectionStatus(id, status) {
  return request({
    url: `/collections/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function getCollectionsByBookId(bookId) {
  return request({
    url: `/collections/book/${bookId}`,
    method: 'get'
  })
}

export function getAvailableCount(bookId) {
  return request({
    url: `/collections/available/count/${bookId}`,
    method: 'get'
  })
}
