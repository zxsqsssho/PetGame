<!-- code/src/views/Shop.vue -->
<template>
  <UserInfoCard />

  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">商店</div>
    <div class="page-subtitle">购买道具以帮助你的宠物缓解疲劳</div>

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
import { useRouter } from 'vue-router'
const router = useRouter()

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
const goHome = () => {
  router.push('Home')
}

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

/* 仅修改原有.back-arrow类的样式，保持模板不变 */
.back-arrow {
  /* 基础样式 - 与卡片风格统一 */
  display: inline-flex;
  align-items: center;
  gap: 6px; /* 图标与文字间距 */
  padding: 8px 16px;
  background-color: #f5f7fa; /* 浅灰背景，呼应输入框/卡片风格 */
  color: #333; /* 文字颜色 */
  border: 1px solid #eee; /* 细边框，与卡片边框呼应 */
  border-radius: 8px; /* 圆角，接近卡片卡片的10px稍小，保持层次 */
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease; /* 统一过渡动画 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04); /* 轻微阴影，增强立体感 */
  margin-bottom: 20px; /* 与下方标题保持距离 */
}

/* 图标样式强化 */
.back-arrow::before {
  content: "←"; /* 保持原箭头图标 */
  font-size: 16px; /* 图标稍大于文字 */
}

/* hover状态 - 与卡片hover效果呼应 */
.back-arrow:hover {
  background-color: #e8ebf0; /* 背景加深 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.06); /* 阴影增强 */
  transform: translateY(-1px); /* 轻微上浮，与卡片hover动效一致 */
}

/* 点击状态 */
.back-arrow:active {
  transform: translateY(0); /* 恢复原位 */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04); /* 阴影减弱 */
}

/* 响应式适配 - 小屏幕调整 */
@media (max-width: 768px) {
  .back-arrow {
    padding: 6px 12px;
    font-size: 13px;
    margin-bottom: 15px;
  }

  .back-arrow::before {
    font-size: 14px;
  }
}
</style>
