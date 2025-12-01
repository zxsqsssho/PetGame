<template>
  <div class="page-wrap">
    <div class="page-title">探索</div>

    <div v-if="store.carriedPet" class="carried-info">
      🐾 携带宠物：{{ store.carriedPet.icon }} {{ store.carriedPet.name }}
      （疲劳：{{ store.carriedPet.fatigue }} / {{ store.maxFatigue(store.carriedPet.rarity) }}）
    </div>
    <div v-else class="carried-info warn">
      ⚠️ 未携带宠物，无法获得宠物奖励！
    </div>

    <div class="locations">
      <div v-for="loc in locations" :key="loc.id" class="loc-card">
        <div class="loc-icon">{{ loc.icon }}</div>
        <div class="loc-name">{{ loc.name }}</div>
        <div class="loc-meta">等级要求: Lv.{{ loc.level }} · 疲劳 +{{ loc.fatigue }}</div>
        <div class="loc-actions">
          <button
            :disabled="!canExplore(loc)"
            @click="startExplore(loc)"
          >
            {{ getExploreButtonText(loc) }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="lastResult" class="result-card">
      <div class="result-title">探索结果</div>
      <div>{{ lastResult }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { usePlayerStore } from '@/stores/usePlayerStore'

const store = usePlayerStore()

const locations = [
  { id: 1, name: '公园', level: 1, fatigue: 5, icon: '🌳', rarity: '普通' },
  { id: 2, name: '神秘湖泊', level: 5, fatigue: 10, icon: '💧', rarity: '稀有' },
  { id: 3, name: '遗迹', level: 10, fatigue: 15, icon: '🏛️', rarity: '史诗' }
]

const lastResult = ref('')

function canExplore(loc) {
  return store.level >= loc.level && (
    !store.carriedPet ||
    (store.carriedPet.fatigue + loc.fatigue <= store.maxFatigue(store.carriedPet.rarity))
  )
}

function getExploreButtonText(loc) {
  if (store.level < loc.level) return '等级不足'
  if (store.carriedPet) {
    const max = store.maxFatigue(store.carriedPet.rarity)
    const future = store.carriedPet.fatigue + loc.fatigue
    if (future > max) return '宠物太累'
  }
  return '开始探索'
}

function startExplore(loc) {
  const result = store.explore(loc)
  lastResult.value = typeof result === 'string' ? result : '探索失败'
}
</script>

<style scoped>
/* 与原样式相同 */
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }
.carried-info { background: #f0f9ff; color: #0c6; padding: 10px 20px; border-radius: 8px; margin-bottom: 20px; text-align: center; font-size: 16px; }
.carried-info.warn { background: #fffbe6; color: #d9a700; }
.locations { display: flex; gap: 20px; }
.loc-card { background: #fff; padding: 18px; border-radius: 12px; width: 260px; text-align: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.loc-icon { font-size: 40px; margin-bottom: 8px; }
.loc-name { font-size: 18px; font-weight: 700; }
.loc-meta { color: #777; margin: 8px 0; }
.loc-actions button { padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; width: 100%; }
.result-card { margin-top: 26px; background: #fff; padding: 16px; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.result-title { font-weight: 700; margin-bottom: 8px; }
</style>