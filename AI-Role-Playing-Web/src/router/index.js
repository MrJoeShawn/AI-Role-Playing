// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue' // 导入Home组件

const routes = [
  {
    path: '/', // 根路径（默认首页）
    name: 'home',
    component: HomeView, // 对应Home.vue组件
  },
  // 其他路由...
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
