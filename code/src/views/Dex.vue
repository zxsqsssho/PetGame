<!-- code/src/views/Dex.vue -->
<template>
  <div class="page-wrap">
    <div class="page-header">
      <button class="back-button" @click="goHome">←</button>
      <div class="page-title">图鉴</div>
    </div>

    <div class="stats-card" v-if="stats">
      <div class="stat-item">
        <div class="stat-number">{{ stats.collected }}</div>
        <div class="stat-label">已收集</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ stats.total }}</div>
        <div class="stat-label">总数</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ stats.percentage }}%</div>
        <div class="stat-label">完成度</div>
      </div>
    </div>

    <div class="grid">
      <div v-for="entry in pokedex" :key="entry.id" class="dex-card">
        <div class="dex-icon">{{ entry.icon || '🐾' }}</div>
        <div class="dex-name">{{ entry.name }}</div>
        <div class="dex-rarity" :class="'rarity-' + entry.rarity">稀有度: {{ entry.rarity }}</div>
        <div v-if="!entry.collected" class="locked">未收集</div>
        <div v-else class="collected">✓ 已收集</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from "@/api/index.js"
import { useRouter } from 'vue-router'

const router = useRouter()
const pokedex = ref([])
const stats = ref(null)

onMounted(async () => {
  try {
    const dexRes = await api.getDex()
    if (dexRes.code === 0) {
      pokedex.value = dexRes.data
    }

    // 获取统计（假设后端有 /api/dex/stats）
    const statsRes = await api.instance.get("/dex/stats")
    if (statsRes.data.code === 0) {
      stats.value = statsRes.data.data
    }
  } catch (error) {
    console.error('获取图鉴数据失败:', error)
    alert('加载图鉴失败，请稍后重试')
  }
})

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
  text-align: center;
  flex: 1;
}
.stats-card {
  display: flex;
  justify-content: space-around;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
}
.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #4ecdc4;
}
.stat-label {
  font-size: 14px;
  color: #777;
  margin-top: 5px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}
.dex-card {
  background: #fff;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  position: relative;
}
.dex-icon {
  font-size: 36px;
  margin-bottom: 8px;
}
.dex-name {
  font-weight: 700;
  margin-bottom: 5px;
}
.dex-rarity {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 5px;
}
.rarity-1 { background: #d0f0ff; color: #0077b6; }
.rarity-2 { background: #ffeaa7; color: #fdcb6e; }
.rarity-3 { background: #fab1a0; color: #e17055; }
.locked { color: #bbb; margin-top: 6px; font-size: 12px; }
.collected { color: #4ecdc4; margin-top: 6px; font-size: 12px; }

@media (max-width: 768px) {
  .grid { grid-template-columns: repeat(2, 1fr); }
  .stats-card { flex-direction: column; gap: 15px; }
}
</style>