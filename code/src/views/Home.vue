<!--code/src/views/Home.vue-->
<template>
  <button @click="debugLogin" style="position:fixed;top:10px;left:10px;z-index:9999;">
    测试登录
  </button>

  <div class="home-page">

    <!-- 页面标题 -->
    <div class="page-title">主页</div>

    <!-- 玩家信息栏 -->
    <div class="user-card">
      <div class="user-left">
        <img class="avatar" src="../assets/avatar.jpg" alt="头像" />

        <div class="user-info">
          <div class="user-name">{{ user.name }}</div>
          <div class="user-level">等级 Lv.{{ user.level }}</div>
        </div>
      </div>

      <div class="user-right">
        <div class="coins">🪙 {{ user.coins }}</div>

        <div class="exp-bar">
          <div class="exp-fill" :style="{ width: expPercent + '%' }"></div>
        </div>
        <div class="exp-text">{{ user.exp }} / {{ user.expMax }}</div>
      </div>
    </div>

    <!-- 中间功能菜单 -->
    <div class="menu-grid">

      <div class="menu-card" @click="goPets">
        <div class="menu-icon">🏠</div>
        <div class="menu-text">宠物</div>
      </div>

      <div class="menu-card" @click="goExplore">
        <div class="menu-icon">🌳</div>
        <div class="menu-text">探索</div>
      </div>

      <div class="menu-card" @click="goDraw">
        <div class="menu-icon">🧧</div>
        <div class="menu-text">抽奖</div>
      </div>

      <div class="menu-card" @click="goShop">
        <div class="menu-icon">🛒</div>
        <div class="menu-text">商店</div>
      </div>

      <div class="menu-card" @click="goTasks">
        <div class="menu-icon">📜</div>
        <div class="menu-text">任务</div>
      </div>

      <div class="menu-card" @click="goDex">
        <div class="menu-icon">📘</div>
        <div class="menu-text">图鉴</div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router'
// import { api } from '@/api/index.js'

import { api } from "@/api";

async function debugLogin() {
  console.log("开始测试登录…");

  try {
    const loginRes = await api.login("admin", "admin");
    console.log("LOGIN RESULT:", loginRes);

    const infoRes = await api.getUserInfo();
    console.log("USER INFO:", infoRes);
  } catch (e) {
    console.error("TEST ERROR:", e);
  }
}

const router = useRouter()

const user = ref({
  name: "",
  level: 0,
  coins: 0,
  exp: 0,
  expMax: 400,
})

onMounted(async () => {
  const res = await api.getUserInfo()
  if (res.code === 0) {
    user.value = res.data
    user.value.expMax = 400  // 如果你后端有 exp_max 就可以删掉
  }
})

const expPercent = computed(() => (user.value.exp / user.value.expMax) * 100)

const goPets = () => router.push('/pets')
const goExplore = () => router.push('/explore')
const goDraw = () => router.push('/draw')
const goShop = () => router.push('/shop')
const goTasks = () => router.push('/tasks')
const goDex = () => router.push('/dex')
</script>


<style scoped>
.home-page {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
  background: #f9f1e5;
  min-height: 100vh;
}

.page-title {
  font-size: 32px;
  text-align: center;
  margin-bottom: 30px;
  font-weight: bold;
}

/* 玩家信息卡片 */
.user-card {
  background: white;
  padding: 20px 28px;
  display: flex;
  justify-content: space-between;
  border-radius: 14px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  margin-bottom: 40px;
}

.user-left {
  display: flex;
  align-items: center;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  margin-right: 16px;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
}

.user-level {
  font-size: 14px;
  color: #666;
}

/* 金币 + 经验 */
.user-right {
  text-align: right;
}

.coins {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.exp-bar {
  width: 180px;
  height: 10px;
  background: #ddd;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 4px;
}

.exp-fill {
  height: 100%;
  background: #80d468;
}

.exp-text {
  font-size: 14px;
  color: #555;
}

/* 六宫格菜单 */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 35px;
  margin-top: 20px;
}

.menu-card {
  background: white;
  border-radius: 14px;
  padding: 30px 150px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: 0.15s;
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.menu-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.menu-text {
  font-size: 18px;
  font-weight: 600;
}
</style>
