<!--code/src/views/Home.vue-->
<template>
  <div class="home-page">

    <!-- 页面标题 -->
    <div class="page-title">主页</div>
    <!-- 玩家信息栏 -->
    <div class="user-card">
      <div class="user-left">
        <img class="avatar" :src="user.avatar"  alt="头像" />
        <div class="user-info">
          <div class="user-name">{{ user.name }}</div>
        </div>
      </div>

      <div class="user-right">
         <div v-if="user.coins < 10000" class="coins">🪙 {{ user.coins }}</div>
        <div v-else-if="user.coins >= 10000&&user.coins <= 100000000" class="coins">🪙 {{ (user.coins-user.coins%100)/10000 }}w+</div>
        <div v-else class="coins">🪙 10000w+</div>
      </div>
    </div>

    <!-- 中间功能菜单 -->
    <div class="menu-grid">

      <div class="menu-card" @click="goPets">
        <div class="menu-icon">🐾</div>
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
        <div class="menu-text">食物商店</div>
      </div>

      <div class="menu-card" @click="goTasks">
        <div class="menu-icon">🎒</div>
        <div class="menu-text">背包</div>
      </div>

      <div class="menu-card" @click="goDex">
        <div class="menu-icon">📘</div>
        <div class="menu-text">图鉴</div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from 'vue-router'
import { api } from '@/api/index.js'

const router = useRouter()

const user = ref({
  name: "",
  avatar:"",
  coins: 0,
})

// onMounted(async () => {
//   const res = await api.getUserInfo()
//
//   if (res.code === 0) {
//     user.value = res.data
//   }
// })

onMounted(async () => {
  try {
    const res = await api.getUserInfo()
    console.log("user info =", res)

    if (res && res.code === 0) {
      user.value = res.data
    } else {
      alert(res?.msg || "登录状态异常，请重新登录")
      router.push('/login')
    }
  } catch (e) {
    console.error("getUserInfo error", e)
    alert("服务器异常，请重新登录")
    router.push('/login')
  }
})





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

/* 金币 + 经验 */
.user-right {
  text-align: right;
}

.coins {
  font-size: 18px;
  font-weight: bold;
  margin-top: 20px;
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
