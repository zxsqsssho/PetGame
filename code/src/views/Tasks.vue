<!--code/src/views/Tasks.vue-->
<template>
  <!-- 使用用户信息卡片组件，并添加ref以便调用组件方法 -->
  <UserInfoCard ref="userInfoRef" />
  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">背包</div>

    <div class="bag-list">
      <div v-for="b in bags" :key="b.itemId" class="bag-card">
        <img class="bag-icon" :src=b.icon>
        <div class="bag-name">{{ b.name }}</div>
        <div class="bag-amount">数量:{{ b.amount }} </div>
        <div><button @click="sale(b.itemId,b.name,b.amount)">出售</button></div>
      </div>
    </div>
    <div class="sale-popup" v-if="sales.is_sale">
      <div class="sale-name">
        <span>{{sales.salename}}</span>
      </div>
      <div class="sale-number">
        <span>出售的数量:</span>
        <input type="number" v-model="sales.salenum" :min="1" :max="sales.amount" class="amount-input">
      </div>
      <div>
        <button class="sale-remove" @click="remove">取消</button>
        <button class="sale-confirm" @click="confirm">确认出售</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'
import {useRouter} from "vue-router";
import UserInfoCard from '@/components/UserInfoCard.vue'


const router = useRouter()
const goHome = () => {
  router.push('Home')
}
const userInfoRef = ref(null) // 添加对UserInfoCard组件的引用

// 删除原来的user定义和onMounted中的获取用户信息逻辑
// 因为UserInfoCard组件会自己处理

const bags = ref([]) // 修改为数组，因为背包物品通常是多个
const sales = ref({
  is_sale: false,
  salename: "",
  itemId: 0,
  amount: 0,
  salenum: 1,
})

onMounted(async () => {
  const res = await api.getBags()
  if (res && res.code === 0) {
    bags.value = res.data
  }
})

const sale = (Id, name, acount) => {
  sales.value.amount = acount
  sales.value.itemId = Id
  sales.value.salename = name
  sales.value.is_sale = true
}

const remove = () => {
  sales.value.is_sale = false
  sales.value.salenum = 1
}

const confirm = async () => {
  if (sales.value.salenum < 1 || sales.value.salenum > sales.value.amount) {
    alert('出售数量无效')
    return
  }

  try {
    const res = await api.saleItem(sales.value.itemId, sales.value.salenum)

    if (res && res.code === 0) {
      // 1. 重新加载背包数据
      const bagsRes = await api.getBags();
      if (bagsRes && bagsRes.code === 0) {
        bags.value = bagsRes.data;
      }

      // 2. 刷新用户信息（金币数量）
      if (userInfoRef.value && userInfoRef.value.refreshUserInfo) {
        await userInfoRef.value.refreshUserInfo();
      }

      alert(res.msg || '出售成功')
    } else {
      alert(res?.msg || '出售失败')
    }
  } catch (error) {
    console.error("出售错误:", error)
    alert('出售失败，请重试')
  } finally {
    // 关闭出售弹窗
    sales.value.is_sale = false
    sales.value.salenum = 1
  }
}
</script>

<style scoped>
.page-wrap {
  max-width: 1100px;
  margin: 80px auto 0;
  padding: 0 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 18px;
}

.bag-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.bag-card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.bag-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.bag-name {
  font-weight: 700;
  margin-bottom: 6px;
}

.bag-amount {
  color: #888;
  margin-bottom: 8px;
}

.bag-card button {
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  width: 75px;
  font-size: 16px;
  background: #4CAF50;
  color: white;
  transition: background 0.2s;
}

.bag-card button:hover {
  background: #45a049;
}

.sale-popup {
  position: fixed;
  background: #ffd2b6;
  border-radius: 8px;
  font-size: 22px;
  width: 36%;
  height: 36%;
  left: 32%;
  top: 32%;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;
  border-color: black;
  border-width: 1px;
  border-style: solid;
  z-index: 1000;
}

.sale-name {
  font-size: 24px;
}

.sale-number span {
  margin-right: 20px;
}

.sale-number input {
  width: 100px;
  font-size: 20px;
  text-align: center;
  padding: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.sale-popup button {
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  width: 150px;
  font-size: 22px;
}

.sale-remove {
  background: #f0f0f0;
  color: #333;
}

.sale-confirm {
  margin-left: 10px;
  background: #ff6b6b;
  color: white;
}

.sale-remove:hover {
  background: #e0e0e0;
}

.sale-confirm:hover {
  background: #ff5252;
}
</style>