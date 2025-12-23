<!-- components/UserInfoCard.vue -->
<template>
  <div class="player-info-card">
    <div class="player-avatar">
      <img :src="user.avatar" alt="头像" />
    </div>
    <div class="player-details">
      <div class="player-name">{{ user.name }}</div>
      <div class="player-coins">
        <div v-if="user.coins < 10000" class="coins">🪙 {{ user.coins }}</div>
        <div v-else-if="user.coins >= 10000 && user.coins <= 100000000" class="coins">
          🪙 {{ (user.coins - user.coins % 100) / 10000 }}w+
        </div>
        <div v-else class="coins">🪙 10000w+</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'
import { useRouter } from "vue-router"

const router = useRouter()
const user = ref({
  name: "",
  avatar: "",
  coins: 0,
})

// 暴露一个刷新方法，供父组件调用
const refreshUserInfo = async () => {
  try {
    const res = await api.getUserInfo()
    console.log("user info =", res)

    if (res && res.code === 0) {
      user.value = res.data
      return true
    } else {
      alert(res?.msg || "登录状态异常，请重新登录")
      router.push('/login')
      return false
    }
  } catch (e) {
    console.error("getUserInfo error", e)
    alert("服务器异常，请重新登录")
    router.push('/login')
    return false
  }
}

// 暴露用户数据和刷新方法给父组件
defineExpose({
  user,
  refreshUserInfo
})

// 组件挂载时自动获取用户信息
onMounted(async () => {
  await refreshUserInfo()
})
</script>

<style scoped>
.player-info-card {
  position: fixed;
  top: 20px;
  right: 20px;
  background: white;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
  width: auto;
  max-width: 200px;
  z-index: 100;
}

.player-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.player-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.player-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.player-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.player-coins {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.coin-icon {
  font-size: 12px;
}

.coin-amount {
  font-weight: 500;
  color: #e6a23c;
}
</style>