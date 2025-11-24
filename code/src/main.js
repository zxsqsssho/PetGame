import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import './assets/main.css' // 如果需要可放全局样式

const app = createApp(App)
app.use(router)
app.mount('#app')
