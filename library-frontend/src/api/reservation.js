import request from '@/utils/request'

export function getReservationPage(params) {
  return request({
    url: '/reservations/page',
    method: 'get',
    params
  })
}

export function getReservation(id) {
  return request({
    url: `/reservations/${id}`,
    method: 'get'
  })
}

export function getReservationsByReaderId(readerId) {
  return request({
    url: `/reservations/reader/${readerId}`,
    method: 'get'
  })
}

export function getReservationsByBookId(bookId) {
  return request({
    url: `/reservations/book/${bookId}`,
    method: 'get'
  })
}

export function reserveBook(readerId, bookId) {
  return request({
    url: '/reservations',
    method: 'post',
    params: { readerId, bookId }
  })
}

export function cancelReservation(id) {
  return request({
    url: `/reservations/${id}/cancel`,
    method: 'put'
  })
}

export function fulfillReservation(id) {
  return request({
    url: `/reservations/${id}/fulfill`,
    method: 'put'
  })
}

export function checkExpiredReservations() {
  return request({
    url: '/reservations/check-expired',
    method: 'post'
  })
}

export function deleteReservation(id) {
  return request({
    url: `/reservations/${id}`,
    method: 'delete'
  })
}
