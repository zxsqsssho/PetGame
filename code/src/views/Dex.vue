<!-- Dex.vue -->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <div class="page-title">宠物图鉴</div>
    <div class="dex-section">
      <div class="section-title">
        宠物图鉴（{{ petsCollectedCount }}/{{ totalPetsCount }}）
      </div>
      <div class="grid">
        <div v-for="entry in pokedex" :key="entry.id" class="dex-card">
          <img v-if="entry.icon && entry.icon.endsWith('.png')" :src="entry.icon" class="dex-icon-img" alt="Pet Icon"/>
          <div class="dex-name">{{ entry.name }}</div>
          <div class="dex-rarity">{{ getRarityName(entry.rarity) }}</div>
          <div class="dex-desc">{{ entry.description }}</div>
          <div v-if="!entry.collected" class="locked">未收集</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from "@/api/index.js"
import UserInfoCard from '@/components/UserInfoCard.vue'

const pokedex = ref([])
const totalPetsCount = ref(0)

onMounted(async () => {
  try {
    const petRes = await api.getDexPets()
    if (petRes.code === 0) {
      pokedex.value = petRes.data
      totalPetsCount.value = petRes.data.length
    } else {
      console.error('加载宠物图鉴失败', petRes.msg)
    }
  } catch (e) {
    console.error('加载图鉴失败', e)
  }
})

const petsCollectedCount = computed(() =>
    pokedex.value.filter(p => p.collected).length
)

const getRarityName = (rarity) => {
  switch (rarity) {
    case 1: return '普通'
    case 2: return '稀有'
    case 3: return '史诗'
    default: return '未知'
  }
}
</script>

<style scoped>
.page-wrap { max-width:1100px; margin:40px auto; padding:0 20px;  margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size:28px; font-weight:700; margin-bottom:18px; }
.grid { display:grid; grid-template-columns: repeat(4, 1fr); gap:18px; }
.dex-card { background:#fff; padding:12px; border-radius:10px; text-align:center;align-items: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.dex-icon-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
}
.dex-name { font-weight:700; }
.locked { color:#bbb; margin-top:6px; }
.page-wrap {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: center;
}
.dex-section {
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
  grid-template-columns: repeat(5, 1fr);
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
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.dex-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
}
.dex-icon {
  font-size: 32px;
  margin-bottom: 6px;
}
.dex-name {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 2px;
}
.dex-rarity {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}
.dex-desc {
  font-size: 11px;
  color: #888;
  margin-top: 4px;
}
.locked {
  color: #aaa;
  font-size: 11px;
  margin-top: 2px;
}

@media (max-width: 1024px) {
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
}
</style>