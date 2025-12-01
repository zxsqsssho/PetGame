<template>
  <div class="page-wrap">
    <div class="page-title">图鉴</div>
    
    <div class="dual-grid">
      <!-- 左侧：宠物图鉴 -->
      <div class="dex-section">
        <div class="section-title">宠物图鉴（{{ petsCollectedCount }}/15）</div>
        <div class="grid">
          <div v-for="entry in pokedex" :key="entry.id" class="dex-card">
            <div class="dex-icon">{{ entry.icon }}</div>
            <div class="dex-name">{{ entry.name }}</div>
            <div class="dex-rarity">{{ entry.rarity }}</div>
            <div v-if="!entry.collected" class="locked">未收集</div>
          </div>
        </div>
      </div>

      <!-- 右侧：食物图鉴 -->
      <div class="dex-section">
        <div class="section-title">食物图鉴（{{ foodsCollectedCount }}/10）</div>
        <div class="grid">
          <div v-for="food in foodDex" :key="food.id" class="dex-card">
            <div class="dex-icon">{{ food.icon }}</div>
            <div class="dex-name">{{ food.name }}</div>
            <div class="dex-type">{{ food.type }}</div>
            <div v-if="!food.collected" class="locked">未获得</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { usePlayerStore } from '@/stores/usePlayerStore'
import { computed } from 'vue'

const store = usePlayerStore()

// 从 store 获取图鉴数据（保持原有结构）
const pokedex = computed(() => store.petDex)
const foodDex = computed(() => store.foodDex)

// 计算已收集数量（保持原有逻辑）
const petsCollectedCount = computed(() => store.petsCollectedCount)
const foodsCollectedCount = computed(() => store.foodsCollectedCount)
</script>

<style scoped>
/* 你的样式保持不变 */
.page-wrap { 
  max-width: 1400px; 
  margin: 40px auto; 
  padding: 0 20px; 
}
.page-title { 
  font-size: 28px; 
  font-weight: 700; 
  margin-bottom: 24px; 
  text-align: center;
}

.dual-grid {
  display: flex;
  gap: 30px;
}

.dex-section {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
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
  box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #f0f0f0;
  transition: transform 0.2s;
}

.dex-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
}

.dex-icon { 
  font-size: 52px; 
  margin-bottom: 6px; 
}
.dex-name { 
  font-weight: 600; 
  font-size: 14px;
  margin-bottom: 2px;
}
.dex-rarity, .dex-type { 
  font-size: 12px; 
  color: #666; 
  margin-bottom: 4px;
}
.locked { 
  color: #aaa; 
  font-size: 11px; 
  margin-top: 2px;
}

/* 响应式设计 */
/*@media (max-width: 1024px) {
  .dual-grid {
    flex-direction: column;
  }
  .grid { 
    grid-template-columns: repeat(4, 1fr); 
  }
}

@media (max-width: 768px) {
  .grid { 
    grid-template-columns: repeat(3, 1fr); 
  }
  .page-wrap {
    margin: 20px auto;
    padding: 0 10px;
  }
}*/
</style>