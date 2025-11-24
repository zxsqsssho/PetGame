<template>
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
import { ref } from 'vue'
const pokedex = ref([
  { id:1, name:'小猫', icon:'🐱', collected:true },
  { id:2, name:'小狗', icon:'🐶', collected:true },
  { id:3, name:'水灵', icon:'🐟', collected:false },
])
</script>

<style scoped>
.page-wrap { max-width:1100px; margin:40px auto; padding:0 20px; }
.page-title { font-size:28px; font-weight:700; margin-bottom:18px; }
.grid { display:grid; grid-template-columns: repeat(4, 1fr); gap:18px; }
.dex-card { background:#fff; padding:12px; border-radius:10px; text-align:center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.dex-icon { font-size:36px; margin-bottom:8px; }
.dex-name { font-weight:700; }
.locked { color:#bbb; margin-top:6px; }
</style>
