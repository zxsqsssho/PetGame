<!--code/src/views/Draw.vue-->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">抽奖</div>
    <div class="draw-panel">

      <!-- 左侧抽奖区 -->
      <div class="gacha-panel">
        <!-- 普通抽奖 -->
        <div class="pool-card">
          <div class="pool-title">普通抽奖</div>
          <div class="pool-cost">100 金币</div>
          <div class="pool-desc">
            普通 / 稀有宠物 · 食物 · 金币
          </div>

          <button @click="drawOne('normal')">抽 一 次</button>
          <button @click="drawTen('normal')">十 连 抽</button>
        </div>

        <!-- 高级抽奖 -->
        <div class="pool-card advanced">
          <div class="pool-title">高级抽奖</div>
          <div class="pool-cost">500 金币</div>
          <div class="pool-desc">
            稀有 / 史诗宠物 · 高级食物 · 金币
          </div>

          <button @click="drawOne('advanced')">抽 一 次</button>
          <button @click="drawTen('advanced')">十 连 抽</button>

        </div>
      </div>

      <!-- 抽奖结果 -->
      <div class="result-list">
        <div class="result-title">抽奖结果</div>

        <div class="result-hint" v-if="results.length === 0">
          还没有抽奖记录
        </div>

        <div
            v-for="(r, i) in results"
            :key="i"
            class="result-item"
            :class="r.rarity"
        >
          🎉 {{ r.text }}
        </div>
      </div>


    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive, onBeforeUnmount } from 'vue'
import { api } from '@/api'
import UserInfoCard from '@/components/UserInfoCard.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const results = ref([])
const goHome = () => {
  router.push('Home')
}

onMounted(() => {
  // 锁死页面滚动
  document.body.style.overflow = 'hidden'
})

onBeforeUnmount(() => {
  // 恢复页面滚动
  document.body.style.overflow = ''
})

const draw = async (type) => {
  try {
    const res = await api.gachaDraw({ type })

    if (!res || res.code !== 0 || !res.data) {
      // ❌ 失败：不 alert，交给外面
      return { ok: false, msg: res?.msg || '抽奖失败' }
    }

    const d = res.data

    let rarityText = ''
    if (d.rarity === 'epic') rarityText = '【史诗】'
    else if (d.rarity === 'rare') rarityText = '【稀有】'
    else rarityText = '【普通】'

    // 展示抽奖结果
    results.value.unshift({
      rarity: d.rarity,
      text: `${rarityText} ${d.rewardName}`
    })

    // ⭐ 抽奖成功 → 统一刷新金币
    window.dispatchEvent(new Event('refresh-user-info'))
    // 1. 添加对UserInfoCard组件的引用
    const userInfoRef = ref(null)
    // 刷新用户信息栏的金币数量
    if (userInfoRef.value && userInfoRef.value.refreshUserInfo) {
      await userInfoRef.value.refreshUserInfo()
    }

    // ✅ 成功一定要 return
    return { ok: true }

  } catch (e) {
    console.error(e)
    return { ok: false, msg: '网络或服务器错误' }
  }
}

const drawOne = async (type) => {
  const result = await draw(type)
  if (!result.ok) {
    alert(result.msg || '金币不足')
  }
}


const drawTen = async (type) => {
  for (let i = 0; i < 10; i++) {
    const result = await draw(type)

    if (!result.ok) {
      alert(result.msg || '金币不足')
      break   // ⭐ 中断十连
    }
  }
}


</script>

<style scoped>


.page-wrap {
  max-width: 1100px;
  margin: 40px auto;
  padding: 0 20px;
  margin-top: 30px; /* 为固定定位的用户信息卡片留出空间 */
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
}

.draw-panel {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.pool-card {
  width: 260px;
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
  text-align: center;
}

.pool-card.advanced {
  border: 2px solid #ffd36a;
}

.pool-title {
  font-size: 20px;
  font-weight: 700;
}

.pool-cost {
  margin-top: 6px;
  font-size: 14px;
  color: #666;
}

.pool-desc {
  margin: 12px 0;
  font-size: 13px;
  color: #888;
}

.pool-card button {
  width: 100%;
  margin-top: 10px;
  padding: 8px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: #409eff;
  color: #fff;
}

.pool-card.advanced button {
  background: #f5a623;
}

.pool-card button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.lock-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #d9534f;
}

.result-list {
  flex: 1;
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
  max-height: 420px;
  overflow-y: auto;
}

.result-item {
  padding: 10px 6px;
  border-bottom: 1px dashed #eee;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;

  animation: popIn 0.35s ease;
}

@keyframes popIn {
  from {
    opacity: 0;
    transform: translateY(-8px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}


/* 稀有度颜色 */
.result-item.normal {
  color: #333;
  background: #f7f7f7;
}

.result-item.rare {
  color: #409eff;
  background: #eaf3ff;
}

.result-item.epic {
  color: #d9534f;
  background: #fff0f0;
}


.result-hint {
  color: #aaa;
  font-size: 14px;
  text-align: center;
  padding: 40px 0;
}

.result-item.epic {
  box-shadow: 0 0 0 1px rgba(217,83,79,0.3);
}

.gacha-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;

  /* ⭐ 让抽奖卡整体“往中间靠” */
  padding-top: 40px;
}

.result-title {
  font-weight: 700;
  margin-bottom: 12px;
  color: #555;
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
