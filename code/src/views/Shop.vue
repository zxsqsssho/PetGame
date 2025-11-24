<template>
  <div class="page-wrap">
    <div class="page-title">商店</div>

    <div class="grid">
      <div v-for="item in items" :key="item.id" class="shop-card">
        <div class="shop-icon">{{ item.icon }}</div>
        <div class="shop-name">{{ item.name }}</div>
        <div class="shop-price">{{ item.price }} 金币</div>
        <div><button @click="buy(item)">购买</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const items = ref([
  { id:1, name:'普通食物', price: 30, icon: '🍗' },
  { id:2, name:'高级食物', price: 120, icon: '🥩' },
  { id:3, name:'抽奖券', price: 200, icon: '🎟️' },
])

const buy = (item) => {
  alert(`购买 ${item.name}（仅前端占位）`)
}
</script>

<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

.grid { display:grid; grid-template-columns: repeat(3,1fr); gap:20px; }
.shop-card { background:#fff; padding:16px; border-radius:12px; text-align:center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.shop-icon { font-size:36px; margin-bottom:8px; }
.shop-name { font-weight:700; margin-bottom:6px; }
.shop-price { color:#888; margin-bottom:8px; }
.shop-card button { padding:8px 12px; border-radius:8px; border:none; cursor:pointer; }
</style>
