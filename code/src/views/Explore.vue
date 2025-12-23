<!--code/src/views/Explore.vue-->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">探索</div>

    <div class="locations">
      <div v-for="loc in locations" :key="loc.id" class="loc-card">
        <div class="loc-icon">{{ loc.icon }}</div>
        <div class="loc-name">{{ loc.name }}</div>
        <div class="loc-meta"> 疲劳 +{{ loc.fatigue }}</div>
        <div class="loc-actions">
          <button @click="explore(loc)">开始探索</button>
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
import { ref } from 'vue'
import { api } from '@/api/index.js'
import UserInfoCard from '@/components/UserInfoCard.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const user = ref({ level: 5 })
const goHome = () => {
  router.push('Home')
}

const locations = ref([
  { id: 1, name: '公园', level: 1, fatigue: 10, icon: '🌳' },
  { id: 2, name: '神秘湖泊', level: 5, fatigue: 15, icon: '💧' },
  { id: 3, name: '遗迹', level: 10, fatigue: 20, icon: '🏛️' }
])

const lastResult = ref('')

const explore = async (loc) => {
  const res = await api.explore(loc.id);
  if (res.code === 0) {
    lastResult.value = res.data.message
  } else {
    alert(res.msg)
  }
}
</script>


<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px;  margin-top: 0px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

.locations {
  display: flex;
  gap: 20px;
  justify-content: center; /* 水平居中所有卡片 */
}

.loc-card { background: #fff; padding: 18px; border-radius: 12px; width: 260px; text-align: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.loc-icon { font-size: 40px; margin-bottom: 8px; }
.loc-name { font-size: 18px; font-weight: 700; }
.loc-meta { color: #777; margin: 8px 0; }
.loc-actions button { padding: 8px 12px; border-radius: 8px; border: none; cursor: pointer; }

.result-card { margin-top: 26px; background: #fff; padding: 16px; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.result-title { font-weight: 700; margin-bottom: 8px; }

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
