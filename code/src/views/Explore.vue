<template>
  <div class="page-wrap">
    <div class="page-title">探索</div>

    <div class="locations">
      <div v-for="loc in locations" :key="loc.id" class="loc-card">
        <div class="loc-icon">{{ loc.icon }}</div>
        <div class="loc-name">{{ loc.name }}</div>
        <div class="loc-meta">等级要求: Lv.{{ loc.level }} · 疲劳 +{{ loc.fatigue }}</div>
        <div class="loc-actions">
          <button :disabled="user.level < loc.level" @click="explore(loc)">
            {{ user.level >= loc.level ? '开始探索' : '等级不足' }}
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
const user = ref({ level: 5 })
const locations = ref([
  { id: 1, name: '公园', level: 1, fatigue: 10, icon: '🌳' },
  { id: 2, name: '神秘湖泊', level: 5, fatigue: 15, icon: '💧' },
  { id: 3, name: '遗迹', level: 10, fatigue: 20, icon: '🏛️' }
])

const lastResult = ref('')

const explore = (loc) => {
  // 模拟探索结果
  const rnd = Math.random()
  if (rnd < 0.5) lastResult.value = `在 ${loc.name} 找到金币  ${Math.floor(Math.random()*50)+10}！`
  else if (rnd < 0.85) lastResult.value = `在 ${loc.name} 发现了食物！`
  else lastResult.value = `在 ${loc.name} 遇到了一只宠物（概率示意）！`
}
</script>

<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

.locations { display: flex; gap: 20px; }
.loc-card { background: #fff; padding: 18px; border-radius: 12px; width: 260px; text-align: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.loc-icon { font-size: 40px; margin-bottom: 8px; }
.loc-name { font-size: 18px; font-weight: 700; }
.loc-meta { color: #777; margin: 8px 0; }
.loc-actions button { padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; }

.result-card { margin-top: 26px; background: #fff; padding: 16px; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.result-title { font-weight: 700; margin-bottom: 8px; }
</style>
