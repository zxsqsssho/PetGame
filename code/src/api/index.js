// code/src/api/index.js
import axios from "axios";

// 让 axios 自动携带 Cookie（用于 Session 登录）
axios.defaults.withCredentials = true;

// 创建一个 axios 实例，统一处理 baseURL 和 cookie
const instance = axios.create({
    baseURL: "/api",
    withCredentials: true
});

// 统一的 API 函数
export const api = {
    // 探索
    explore: (locationId) =>
        instance.post("/explore", { locationId }).then(r => r.data),

    // 抽奖
    drawNormal: () =>
        instance.post("/draw/normal").then(r => r.data),

    drawAdvanced: () =>
        instance.post("/draw/advanced").then(r => r.data),

    // 图鉴
    getDex: () =>
        instance.get("/dex/list").then(r => r.data),

    // 宠物
    getPets: () =>
        instance.get("/pets/list").then(r => r.data),

    feedPet: (petId) =>
        instance.post("/pets/feed", { petId }).then(r => r.data),

    // 商店
    getShopItems: () =>
        instance.get("/shop/items").then(r => r.data),

    buyItem: (itemId, quantity = 1) =>
        instance.post("/shop/buy", { itemId, quantity }).then(r => r.data),

    // 任务
    getTasks: () =>
        instance.get("/tasks/list").then(r => r.data),

    claimTask: (taskId) =>
        instance.post("/tasks/claim", { taskId }).then(r => r.data),

    // 用户系统
    login: (username, password) =>
        instance.post("/user/login", { username, password }).then(r => r.data),

    register: (username, password) =>
        instance.post("/user/register", { username, password }).then(r => r.data),

    getUserInfo: () =>
        instance.get("/user/info").then(r => r.data),
};
