// 文件：code/src/main.js
// 作用：创建 axios 实例，前端统一调用 /api 开头的接口
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import {createPinia} from 'pinia'

const api = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' }
})
const pinia = createPinia()

const app = createApp(App)
app.config.globalProperties.$api = api
app.use(pinia)
app.use(router) // <-- 注册路由
app.mount('#app')
