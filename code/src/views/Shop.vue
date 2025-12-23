<!--code/src/views/Shop.vue-->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <div class="page-title">商店</div>

    <!-- 加空值判断：避免接口返回空/undefined导致渲染报错 -->
    <div class="grid" v-if="items.length > 0">
      <div v-for="item in items" :key="item.id" class="shop-card">
        <div class="shop-icon">
          <!-- 直接绑定绝对路径，无需require -->
          <img
              :src="item.icon"
              :alt="item.name"
              class="item-icon"
              @error="(e) => e.target.src = '/shop/default.png'"
          >
        </div>
        <div class="shop-name">{{ item.name }}</div>
        <div class="shop-price">{{ item.price }} 金币</div>
        <div><button @click="buy(item)">购买</button></div>
      </div>
    </div>
    <!-- 无数据时的兜底 -->
    <div v-else class="empty-tip">暂无商品</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'
import UserInfoCard from '@/components/UserInfoCard.vue'

const items = ref([])

onMounted(async () => {
  try { // 加try-catch捕获接口错误
    const res = await api.getShopItems()
    // 校验接口返回数据格式
    if (res && res.data && Array.isArray(res.data)) {
      items.value = res.data
    }
  } catch (error) {
    console.error('获取商店物品失败：', error)
    alert('加载商品失败，请重试')
  }
})

const buy = async (item) => {
  if (!item || !item.id) return alert('商品信息异常')
  try {
    const res = await api.buyItem(item.id, 1)
    alert(res.msg || '购买成功')
  } catch (error) {
    console.error('购买商品失败：', error)
    alert('购买失败，请重试')
  }
}
</script>

<style scoped>
/* 原有样式不变，补充空数据提示和图片样式 */
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

.grid { display:grid; grid-template-columns: repeat(3,1fr); gap:20px; }
.shop-card { background:#fff; padding:16px; border-radius:12px; text-align:center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.shop-icon { font-size:36px; margin-bottom:8px; }
.shop-name { font-weight:700; margin-bottom:6px; }
.shop-price { color:#888; margin-bottom:8px; }
.shop-card button { padding:8px 12px; border-radius:8px; border:none; cursor:pointer; }

/* 新增：图片样式 + 空数据提示 */
.item-icon {
  width: 60px;
  height: 60px;
  object-fit: contain;
}
.empty-tip {
  text-align: center;
  color: #999;
  font-size: 16px;
  margin-top: 40px;
}
</style>