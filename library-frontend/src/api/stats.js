import request from '@/utils/request'

export function getOverviewStats() {
  return request({
    url: '/stats/overview',
    method: 'get'
  })
}

export function getBorrowRanking(limit) {
  return request({
    url: '/stats/borrow-ranking',
    method: 'get',
    params: { limit }
  })
}

export function getPopularBooks(limit) {
  return request({
    url: '/stats/popular-books',
    method: 'get',
    params: { limit }
  })
}

export function getMonthlyStats(months) {
  return request({
    url: '/stats/monthly',
    method: 'get',
    params: { months }
  })
}

export function getBorrowCount(startDate, endDate) {
  return request({
    url: '/stats/borrow-count',
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getReturnCount(startDate, endDate) {
  return request({
    url: '/stats/return-count',
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getVisitCount(startDate, endDate) {
  return request({
    url: '/stats/visit-count',
    method: 'get',
    params: { startDate, endDate }
  })
}
