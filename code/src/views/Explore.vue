<!-- Explore.vue -->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <div class="page-title">探索</div>
    <div class="locations">
      <div v-for="loc in locations" :key="loc.id" class="loc-card">
        <div class="loc-icon">{{ loc.icon }}</div>
        <div class="loc-name">{{ loc.name }}</div>
        <div class="loc-meta">疲劳 +{{ loc.fatigueCost }}</div>
        <div class="loc-desc">{{ loc.description }}</div>
        <div class="loc-actions">
          <button @click="startExplore(loc)">开始探索</button>
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
import UserInfoCard from '@/components/UserInfoCard.vue'

const locations = ref([])
const lastResult = ref('')

onMounted(async () => {
  try {
    const res = await api.getExploreLocations()
    if (res.code === 0) {
      locations.value = res.data
    } else {
      alert(res.msg || '加载探索地点失败')
    }
  } catch (e) {
    console.error('加载探索地点出错', e)
    alert('网络错误，请重试')
  }
})

const startExplore = async (loc) => {
  try {
    const res = await api.explore(loc.id)
    if (res.code === 0) {
      lastResult.value = res.data.message || '探索完成'
      alert(res.msg)
    } else {
      alert(res.msg)
    }
  } catch (e) {
    alert('探索失败')
  }
}
</script>

<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px;  margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }
.locations { display: flex; gap: 20px; flex-wrap: wrap; }
.loc-card {
  background: #fff;
  padding: 18px;
  border-radius: 12px;
  width: 260px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.loc-icon { font-size: 40px; margin-bottom: 8px; }
.loc-name { font-size: 18px; font-weight: 700; }
.loc-meta { color: #777; margin: 8px 0; }
.loc-desc { color: #555; font-size: 14px; margin: 10px 0; }
.loc-actions button {
  padding: 10px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #4ecdc4;
  color: white;
  font-weight: 600;
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