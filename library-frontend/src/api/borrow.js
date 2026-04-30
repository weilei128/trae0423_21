import request from '@/utils/request'

export function getBorrowPage(params) {
  return request({
    url: '/borrow-records/page',
    method: 'get',
    params
  })
}

export function getBorrowRecord(id) {
  return request({
    url: `/borrow-records/${id}`,
    method: 'get'
  })
}

export function getRecordsByReaderId(readerId) {
  return request({
    url: `/borrow-records/reader/${readerId}`,
    method: 'get'
  })
}

export function borrowBook(readerId, collectionId) {
  return request({
    url: '/borrow-records/borrow',
    method: 'post',
    params: { readerId, collectionId }
  })
}

export function returnBook(id) {
  return request({
    url: `/borrow-records/${id}/return`,
    method: 'put'
  })
}

export function renewBook(id) {
  return request({
    url: `/borrow-records/${id}/renew`,
    method: 'put'
  })
}

export function getOverdueRecords() {
  return request({
    url: '/borrow-records/overdue',
    method: 'get'
  })
}

export function createBorrowRecord(data) {
  return request({
    url: '/borrow-records',
    method: 'post',
    data
  })
}

export function updateBorrowRecord(id, data) {
  return request({
    url: `/borrow-records/${id}`,
    method: 'put',
    data
  })
}

export function deleteBorrowRecord(id) {
  return request({
    url: `/borrow-records/${id}`,
    method: 'delete'
  })
}
