import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据概览', icon: 'DataAnalysis' }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/views/book/index.vue'),
        meta: { title: '书目管理', icon: 'Reading' }
      },
      {
        path: 'collections',
        name: 'Collections',
        component: () => import('@/views/collection/index.vue'),
        meta: { title: '馆藏管理', icon: 'Box' }
      },
      {
        path: 'readers',
        name: 'Readers',
        component: () => import('@/views/reader/index.vue'),
        meta: { title: '读者管理', icon: 'User' }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/views/borrow/index.vue'),
        meta: { title: '借阅服务', icon: 'Tickets' }
      },
      {
        path: 'reservation',
        name: 'Reservation',
        component: () => import('@/views/reservation/index.vue'),
        meta: { title: '预约续借', icon: 'Calendar' }
      },
      {
        path: 'fine',
        name: 'Fine',
        component: () => import('@/views/fine/index.vue'),
        meta: { title: '逾期罚款', icon: 'Money' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/stats/index.vue'),
        meta: { title: '统计报表', icon: 'TrendCharts' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
