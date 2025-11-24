<template>
  <div class="page-wrap">
    <div class="page-title">宠物</div>

    <div class="grid">
      <div v-for="pet in pets" :key="pet.id" class="pet-card">
        <div class="pet-avatar">{{ pet.icon }}</div>
        <div class="pet-name">{{ pet.name }}</div>
        <div class="pet-info">等级: {{ pet.level }} · 稀有度: {{ pet.rarity }}</div>
        <div class="pet-actions">
          <button @click="feed(pet)">喂食</button>
          <button @click="detail(pet)">详情</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const pets = ref([
  { id: 1, name: '小猫', level: 1, rarity: '普通', icon: '🐱', fatigue: 10 },
  { id: 2, name: '小狗', level: 2, rarity: '普通', icon: '🐶', fatigue: 5 },
  { id: 3, name: '水灵', level: 5, rarity: '稀有', icon: '🐟', fatigue: 20 },
])

const feed = (pet) => {
  alert(`${pet.name} 被喂食，疲劳减少（示意）`)
  // 触发后端接口：/api/pet/feed
}

const detail = (pet) => {
  // 可以跳转到一个宠物详情页（若有）
  alert(`查看 ${pet.name} 详情（可扩展）`)
}
</script>

<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

/* 宫格 */
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.pet-card {
  background: #fff; padding: 18px; border-radius: 12px; text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.pet-avatar { font-size: 44px; margin-bottom: 8px; }
.pet-name { font-size: 18px; font-weight: 700; }
.pet-info { color: #777; margin: 8px 0; }
.pet-actions button { margin: 6px; padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; }
</style>
