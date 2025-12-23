<!-- code/src/views/Pets.vue -->
<template>
  <div class="page-wrap">
    <!-- 标题栏带返回按钮 -->
    <div class="page-header">
      <button class="back-button" @click="goHome">←</button>
      <div class="page-title">宠物</div>
    </div>

    <div class="grid">
      <div v-for="pet in pets" :key="pet.id" class="pet-card">
        <div class="pet-avatar">{{ pet.icon || '🐾' }}</div>
        <div class="pet-name">{{ pet.nickname || pet.name }}</div>
        <div class="pet-info">稀有度: {{ pet.rarity }}</div>
        <div class="pet-actions">
          <button @click="feed(pet)">喂食</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const pets = ref([])

onMounted(async () => {
  const res = await api.getPets()
  if (res.code === 0) pets.value = res.data
})

const feed = async (pet) => {
  const res = await api.feedPet(pet.id)
  alert(res.msg || res.data?.message || '操作完成')
}

const goHome = () => router.push('/home')
</script>

<style scoped>
.page-wrap {
  max-width: 1100px;
  margin: 40px auto;
  padding: 0 20px;
}
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.back-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  margin-right: 12px;
  color: #4ecdc4;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
}
/* 宫格 */
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
.pet-card {
  background: #fff;
  padding: 18px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.pet-avatar {
  font-size: 44px;
  margin-bottom: 8px;
}
.pet-name {
  font-size: 18px;
  font-weight: 700;
}
.pet-info {
  color: #777;
  margin: 8px 0;
}
.pet-actions button {
  margin: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #4ecdc4;
  color: white;
}
</style>