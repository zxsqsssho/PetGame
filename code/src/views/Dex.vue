<!-- Dex.vue -->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">宠物图鉴</div>
    <div class="dex-section">
      <div class="section-title">
        宠物图鉴（{{ petsCollectedCount }}/{{ totalPetsCount }}）
      </div>
      <div class="grid">
        <div v-for="entry in pokedex" :key="entry.id" class="dex-card">
          <img v-if="entry.icon && entry.icon.endsWith('.png')" :src="entry.icon" class="dex-icon-img" alt="Pet Icon"/>
          <div class="dex-name">{{ entry.name }}</div>
          <div class="dex-rarity">{{ getRarityName(entry.rarity) }}</div>
          <div class="dex-desc">{{ entry.description }}</div>
          <div v-if="!entry.collected" class="locked">未收集</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from "@/api/index.js"
import UserInfoCard from '@/components/UserInfoCard.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const pokedex = ref([])
const totalPetsCount = ref(0)

onMounted(async () => {
  try {
    const petRes = await api.getDexPets()
    if (petRes.code === 0) {
      pokedex.value = petRes.data
      totalPetsCount.value = petRes.data.length
    } else {
      console.error('加载宠物图鉴失败', petRes.msg)
    }
  } catch (e) {
    console.error('加载图鉴失败', e)
  }
})

const goHome = () => {
  router.push('Home')
}
const petsCollectedCount = computed(() =>
    pokedex.value.filter(p => p.collected).length
)

const getRarityName = (rarity) => {
  switch (rarity) {
    case 1: return '普通'
    case 2: return '稀有'
    case 3: return '史诗'
    default: return '未知'
  }
}
</script>

<style scoped>
.page-wrap { max-width:1100px; margin:40px auto; padding:0 20px;  margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size:28px; font-weight:700; margin-bottom:18px; }
.grid { display:grid; grid-template-columns: repeat(4, 1fr); gap:18px; }
.dex-card { background:#fff; padding:12px; border-radius:10px; text-align:center;align-items: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.dex-icon-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
}
.dex-name { font-weight:700; }
.locked { color:#bbb; margin-top:6px; }
.page-wrap {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: center;
}
.dex-section {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}
.grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 18px;
}
.dex-card {
  background: #fff;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #f0f0f0;
  transition: transform 0.2s;
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.dex-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
}
.dex-icon {
  font-size: 32px;
  margin-bottom: 6px;
}
.dex-name {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 2px;
}
.dex-rarity {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}
.dex-desc {
  font-size: 11px;
  color: #888;
  margin-top: 4px;
}
.locked {
  color: #aaa;
  font-size: 11px;
  margin-top: 2px;
}

@media (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
@media (max-width: 768px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .page-wrap {
    margin: 20px auto;
    padding: 0 10px;
  }
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