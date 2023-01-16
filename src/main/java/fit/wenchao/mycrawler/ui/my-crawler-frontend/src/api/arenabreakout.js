import request from '@/utils/request'

export function fetch_gun_list(params) {
  return request({
    url: '/arenabreakout/gun',
    method: 'get',
    params
  })
}

export function fetch_gun_sort_list() {
  return request({
    url: '/arenabreakout/gun-sort',
    method: 'get'
  })
}

export function fetch_gun_attr_list() {
  return request({
    url: '/arenabreakout/gun-attr',
    method: 'get'
  })
}

