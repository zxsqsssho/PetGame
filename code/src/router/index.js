// code/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Pets from '../views/Pets.vue'
import Explore from '../views/Explore.vue'
import Draw from '../views/Draw.vue'
import Shop from '../views/Shop.vue'
import Tasks from '../views/Tasks.vue'
import Dex from '../views/Dex.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
    //默认跳转页面是注册
    { path: '/', redirect: '/login' },

    { path: '/home', name: 'Home', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },

    { path: '/pets', name: 'Pets', component: Pets },
    { path: '/explore', name: 'Explore', component: Explore },
    { path: '/draw', name: 'Draw', component: Draw },
    { path: '/shop', name: 'Shop', component: Shop },
    { path: '/Tasks', name: 'Tasks', component: Tasks },
    { path: '/dex', name: 'Dex', component: Dex },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router
