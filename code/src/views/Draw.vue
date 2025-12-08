<!--code/src/views/Draw.vue-->
<template>
  <div class="page-wrap">
    <div class="page-title">抽奖</div>

    <div class="draw-panel">
      <div class="pool-card">
        <div class="pool-title">普通抽奖（100 金币）</div>
        <div class="pool-info">可能获得：普通宠物 / 稀有宠物 / 食物 / 金币</div>
        <button @click="drawOnce">抽 一 次</button>
        <button @click="drawTen">抽 十 次</button>
      </div>

      <div v-if="results.length" class="result-list">
        <div v-for="(r,i) in results" :key="i" class="result-item">{{ r }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { api } from "@/api/index.js"

const results = ref([])

const drawOnce = async () => {
  const res = await api.drawNormal()
  results.value.unshift("获得：" + JSON.stringify(res.data))
}

const drawTen = () => {
  for (let i = 0; i < 10; i++) drawOnce()
}
</script>


<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

.draw-panel { display:flex; gap:24px; align-items:flex-start; }
.pool-card { background:#fff; padding:18px; border-radius:12px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); width:320px; text-align:center; }
.pool-card button { margin-top:12px; padding:8px 12px; border-radius:8px; border:none; cursor:pointer; }

.result-list { flex:1; background:#fff; padding:16px; border-radius:12px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.result-item { padding:8px 0; border-bottom:1px dashed #eee; }
</style>
