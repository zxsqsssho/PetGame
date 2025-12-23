<!-- components/UserInfoCard.vue -->
<template>
  <div class="player-info-card">
    <div class="player-avatar">
      <img :src="user.avatar" alt="头像" />
    </div>
    <div class="player-details">
      <div class="player-name">{{ user.name }}</div>

      <div class="player-coins">
        <div v-if="user.coins < 10000" class="coins">
          🪙 {{ user.coins }}
        </div>
        <div
            v-else-if="user.coins >= 10000 && user.coins <= 100000000"
            class="coins"
        >
          🪙 {{ Math.floor(user.coins / 10000) }}w+
        </div>
        <div v-else class="coins">🪙 10000w+</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { api } from '@/api/index.js'
import { useRouter } from 'vue-router'

const router = useRouter()

const user = ref({
  name: '',
  avatar: '',
  coins: 0,
})

/**
 * ⭐ 刷新用户信息（金币、头像等）
 */
const refreshUserInfo = async () => {
  try {
    const res = await api.getUserInfo()

    if (res && res.code === 0 && res.data) {
      user.value = res.data
      return true
    } else {
      alert(res?.msg || '登录状态异常，请重新登录')
      router.push('/login')
      return false
    }
  } catch (e) {
    console.error('getUserInfo error', e)
    alert('服务器异常，请重新登录')
    router.push('/login')
    return false
  }
}

/**
 * ⭐ 监听全局刷新事件
 */
const handleRefreshEvent = () => {
  refreshUserInfo()
}

// 初次加载 + 事件监听
onMounted(() => {
  refreshUserInfo()
  window.addEventListener('refresh-user-info', handleRefreshEvent)
})

// 组件卸载时移除监听（好习惯）
onUnmounted(() => {
  window.removeEventListener('refresh-user-info', handleRefreshEvent)
})

// 保留：如果以后父组件想直接调用
defineExpose({
  user,
  refreshUserInfo,
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
  max-width: 200px;
  z-index: 100;
}

.player-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
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
}

.player-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.player-coins {
  font-size: 13px;
  color: #666;
}
</style>
