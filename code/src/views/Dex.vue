<!--code/src/views/Dex.vue-->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <div class="page-title">图鉴</div>

    <div class="grid">
      <div v-for="entry in pokedex" :key="entry.id" class="dex-card">
        <div class="dex-icon">{{ entry.icon }}</div>
        <div class="dex-name">{{ entry.name }}</div>
        <div v-if="!entry.collected" class="locked">未收集</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from "@/api/index.js"
import UserInfoCard from '@/components/UserInfoCard.vue'

const pokedex = ref([])

onMounted(async () => {
  const res = await api.getDex()
  if (res.code === 0) {
    pokedex.value = res.data
  }
})
</script>


<style scoped>
.page-wrap { max-width:1100px; margin:40px auto; padding:0 20px;  margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size:28px; font-weight:700; margin-bottom:18px; }
.grid { display:grid; grid-template-columns: repeat(4, 1fr); gap:18px; }
.dex-card { background:#fff; padding:12px; border-radius:10px; text-align:center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.dex-icon { font-size:36px; margin-bottom:8px; }
.dex-name { font-weight:700; }
.locked { color:#bbb; margin-top:6px; }
</style>
