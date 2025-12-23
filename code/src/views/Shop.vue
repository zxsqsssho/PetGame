<!-- code/src/views/Shop.vue -->
<template>
  <UserInfoCard />

  <div class="page-wrap">
    <div class="page-title">商店</div>
    <div class="page-subtitle">购买道具以帮助你的宠物成长</div>

    <div class="shop-panel">
      <div v-if="items.length === 0" class="empty-tip">
        暂无商品
      </div>

      <div
          v-for="item in items"
          :key="item.id"
          class="shop-row"
      >
        <!-- 图标 -->
        <img
            :src="item.icon"
            class="item-icon"
            :alt="item.name"
            @error="e => e.target.src='/shop/default.png'"
        />

        <!-- 名称 + 描述 -->
        <div class="item-info">
          <div class="item-name">{{ item.name }}</div>
          <div class="item-desc">
            {{ item.description || '暂无描述' }}
          </div>
        </div>

        <!-- 价格 -->
        <div class="item-price">
          {{ item.price }} 金币
        </div>

        <!-- 操作 -->
        <button class="buy-btn" @click="buy(item)">
          购买
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api'
import UserInfoCard from '@/components/UserInfoCard.vue'

const items = ref([])

onMounted(async () => {
  try {
    const res = await api.getShopItems()
    if (res && Array.isArray(res.data)) {
      // ⭐ 原样使用数据库返回的数据
      items.value = res.data
    }
  } catch (e) {
    console.error('获取商店失败', e)
    alert('加载商店失败')
  }
})

const buy = async (item) => {
  try {
    const res = await api.buyItem(item.id, 1)
    alert(res.msg || '购买成功')

    // ⭐ 通知用户信息卡片刷新金币
    window.dispatchEvent(new Event('refresh-user-info'))
    // 1. 添加对UserInfoCard组件的引用
    const userInfoRef = ref(null)
    // 刷新用户信息栏的金币数量
    if (userInfoRef.value && userInfoRef.value.refreshUserInfo) {
      await userInfoRef.value.refreshUserInfo()
    }
  } catch (e) {
    console.error(e)
    alert('金币不足或购买失败')
  }
}

</script>

<style scoped>
.page-wrap {
  max-width: 1000px;
  margin: 40px auto;
  padding: 20px;
  margin-top: 0px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
}

.page-subtitle {
  margin-top: 6px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #888;
}

/* 商店整体 */
.shop-panel {
  background: #fff;
  border-radius: 16px;
  padding: 10px 16px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

/* 单行商品 */
.shop-row {
  display: flex;
  align-items: center;
  padding: 14px 6px;
  border-bottom: 1px dashed #eee;
}

.shop-row:last-child {
  border-bottom: none;
}

/* 图标 */
.item-icon {
  width: 56px;
  height: 56px;
  object-fit: contain;
  margin-right: 16px;
}

/* 信息 */
.item-info {
  flex: 1;
}

.item-name {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 4px;
}

.item-desc {
  font-size: 13px;
  color: #888;
}

/* 价格 */
.item-price {
  width: 100px;
  text-align: right;
  margin-right: 16px;
  font-weight: 700;
  color: #f5a623;
}

/* 按钮 */
.buy-btn {
  padding: 6px 14px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #409eff;
  color: #fff;
  font-weight: 600;
}

.buy-btn:hover {
  background: #337ecc;
}

/* 空状态 */
.empty-tip {
  text-align: center;
  color: #aaa;
  padding: 40px 0;
}
</style>
