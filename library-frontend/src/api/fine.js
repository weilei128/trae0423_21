import request from '@/utils/request'

export function getFinePage(params) {
  return request({
    url: '/fine-records/page',
    method: 'get',
    params
  })
}

export function getFineRecord(id) {
  return request({
    url: `/fine-records/${id}`,
    method: 'get'
  })
}

export function getFinesByReaderId(readerId) {
  return request({
    url: `/fine-records/reader/${readerId}`,
    method: 'get'
  })
}

export function getUnpaidFines(readerId) {
  return request({
    url: `/fine-records/reader/${readerId}/unpaid`,
    method: 'get'
  })
}

export function getTotalUnpaidAmount(readerId) {
  return request({
    url: `/fine-records/reader/${readerId}/unpaid/total`,
    method: 'get'
  })
}

export function createFineRecord(data) {
  return request({
    url: '/fine-records',
    method: 'post',
    data
  })
}

export function updateFineRecord(id, data) {
  return request({
    url: `/fine-records/${id}`,
    method: 'put',
    data
  })
}

export function payFine(id, amount) {
  return request({
    url: `/fine-records/${id}/pay`,
    method: 'post',
    params: { amount }
  })
}

export function checkOverdueFines() {
  return request({
    url: '/fine-records/check-overdue',
    method: 'post'
  })
}

export function deleteFineRecord(id) {
  return request({
    url: `/fine-records/${id}`,
    method: 'delete'
  })
}
