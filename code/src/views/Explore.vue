<!-- code/src/views/Explore.vue -->
<template>
  <div class="page-wrap">
    <div class="page-header">
      <button class="back-button" @click="goHome">←</button>
      <div class="page-title">探索</div>
    </div>

    <div class="locations">
      <div v-for="loc in locations" :key="loc.id" class="loc-card">
        <div class="loc-icon">{{ loc.icon || '🌳' }}</div>
        <div class="loc-name">{{ loc.name }}</div>
        <div class="loc-meta">{{ loc.description }}</div>
        <div class="loc-actions">
          <button @click="explore(loc)">开始探索</button>
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
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const locations = ref([])
const lastResult = ref('')

onMounted(async () => {
  try {
    // 使用 api 实例而非 fetch，统一走代理
    const res = await api.instance.get("/explore/locations")
    if (res.data.code === 0) {
      locations.value = res.data.data
    } else {
      loadDefaultLocations()
    }
  } catch (error) {
    console.error("获取地点失败", error)
    loadDefaultLocations()
  }
})

function loadDefaultLocations() {
  locations.value = [
    { id: 1, name: '公园', description: '适合新手的休闲场所，可获得普通宠物与基础奖励', icon: '🌳' },
    { id: 2, name: '神秘湖泊', description: '宁静又危险的湖区，可以钓上稀有生物', icon: '💧' },
    { id: 3, name: '遗迹', description: '充满未知能量的古老遗迹，可挑战强力怪物', icon: '🏛️' }
  ]
}

const explore = async (loc) => {
  const res = await api.explore(loc.id)
  if (res.code === 0) {
    lastResult.value = res.data.message || '探索成功！'
  } else {
    alert(res.msg || '探索失败')
  }
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
.locations {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}
.loc-card {
  background: #fff;
  padding: 18px;
  border-radius: 12px;
  width: 260px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.loc-icon {
  font-size: 40px;
  margin-bottom: 8px;
}
.loc-name {
  font-size: 18px;
  font-weight: 700;
}
.loc-meta {
  color: #777;
  margin: 8px 0;
  font-size: 14px;
}
.loc-actions button {
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #4ecdc4;
  color: white;
}
.result-card {
  margin-top: 26px;
  background: #fff;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.result-title {
  font-weight: 700;
  margin-bottom: 8px;
}
</style>