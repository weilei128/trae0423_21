import request from '@/utils/request'

export function getBookPage(params) {
  return request({
    url: '/books/page',
    method: 'get',
    params
  })
}

export function getBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

export function createBook(data) {
  return request({
    url: '/books',
    method: 'post',
    data
  })
}

export function updateBook(id, data) {
  return request({
    url: `/books/${id}`,
    method: 'put',
    data
  })
}

export function deleteBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'delete'
  })
}

export function batchDeleteBooks(ids) {
  return request({
    url: '/books/batch',
    method: 'post',
    data: ids
  })
}

export function importBooks(data) {
  return request({
    url: '/books/import',
    method: 'post',
    data
  })
}

export function getBookList() {
  return request({
    url: '/books/list',
    method: 'get'
  })
}
