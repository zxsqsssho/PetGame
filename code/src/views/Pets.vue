<!--code/src/views/Pets.vue-->
<template>
  <!-- 使用用户信息卡片组件 -->
  <UserInfoCard />
  <div class="page-wrap">
    <button @click="goHome" class="back-arrow">返回</button>
    <div class="page-title">宠物</div>
    <div v-if="carriedPet" class="carried-banner">
      当前携带：{{ carriedPet.nickname }}
    </div>
    <div v-else class="carried-banner">
      当前未携带宠物，快去携带一只吧！
    </div>
    <div class="grid">
      <div v-for="pet in pets" :key="pet.id" class="pet-card">
        <img v-if="pet.icon && pet.icon.endsWith('.png')" :src="pet.icon" class="pet-icon-img" alt="Pet Icon"/>
        <div class="pet-name">{{ pet.nickname }}</div>
        <div class="pet-info">疲劳: {{ pet.fatigue }}/{{ pet.fatigueMax }}</div>
        <div class="pet-actions">
          <button @click="openDetail(pet)">详情</button>
        </div>
      </div>
    </div>

    <!-- 宠物详情模态框 -->
    <div v-if="selectedPet" class="modal-overlay" @click="closeDetail">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">{{ selectedPet.nickname }}</h3>
        <div class="pet-icon">{{ selectedPet.icon }}</div>
        <div class="detail-info">
          <p>稀有度: {{ getRarityName(selectedPet.rarity) }}</p>
          <p>疲劳度:
            <span>
              {{ selectedPet.fatigue }} / {{ selectedPet.fatigueMax }}
            </span>
          </p>
          <p>偏好食物: {{ getPreferredFoodName(selectedPet.preferredFood) }}</p>
          <p>状态:
            <span :class="{ 'carried-status': selectedPet.carried }">
              {{ selectedPet.carried ? '✅ 已携带' : '未携带' }}
            </span>
          </p>
        </div>
        <div class="modal-actions">
          <button @click="feedPet(selectedPet.id, 'normal')" :disabled="selectedPet.fatigue <= 0">
            喂 {{ getPreferredFoodName(selectedPet.preferredFood) }}（-10疲劳）
          </button>
          <button @click="feedPet(selectedPet.id, 'golden')" :disabled="selectedPet.fatigue <= 0">
            喂高级食物（-20疲劳）
          </button>
          <button v-if="selectedPet.carried" @click="toggleCarry(selectedPet)" class="btn-uncarry">
            取消携带
          </button>
          <button v-else @click="toggleCarry(selectedPet)" class="btn-carry">
            设为携带
          </button>
        </div>
        <button class="btn-close" @click="closeDetail">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '@/api/index.js'
import UserInfoCard from "@/components/UserInfoCard.vue";
import { useRouter } from 'vue-router'
const router = useRouter()

const pets = ref([])
const selectedPet = ref(null)

// 从后端加载宠物列表
onMounted(async () => {
  try {
    const res = await api.getPets()
    if (res.code === 0) {
      pets.value = res.data
    } else {
      alert(res.msg || '加载宠物失败')
    }
  } catch (e) {
    console.error('加载宠物出错', e)
    alert('网络错误，请重试')
  }
})
const goHome = () => {
  router.push('Home')
}

// 计算当前携带的宠物
const carriedPet = computed(() => {
  return pets.value.find(pet => pet.carried)
})

// 打开详情（深拷贝避免直接修改响应数据）
const openDetail = (pet) => {
  selectedPet.value = { ...pet }
}

const closeDetail = () => {
  selectedPet.value = null
}

// 切换携带状态
const toggleCarry = async (pet) => {
  try {
    const res = await api.carryPet(pet.id, !pet.carried)
    if (res.code === 0) {
      // 更新本地状态
      const updated = pets.value.find(p => p.id === pet.id)
      if (updated) {
        updated.carried = !pet.carried
        // 自动取消其他宠物的携带（后端已处理，前端同步）
        pets.value.forEach(p => {
          if (p.id !== pet.id) p.carried = false
        })
        selectedPet.value.carried = updated.carried
      }
      alert(res.msg)
    } else {
      alert(res.msg)
    }
  } catch (e) {
    alert('操作失败')
  }
}

// 喂食
const feedPet = async (petId, foodType) => {
  try {
    const res = await api.feedPet(petId, foodType)
    if (res.code === 0) {
      // 更新疲劳值
      const pet = pets.value.find(p => p.id === petId)
      const detailPet = selectedPet.value
      if (pet) {
        pet.fatigue = res.data.newFatigue
        // 更新携带宠物疲劳值
        if (pet.carried && carriedPet.value) {
          carriedPet.value.fatigue = res.data.newFatigue
        }
      }
      if (detailPet && detailPet.id === petId) {
        detailPet.fatigue = res.data.newFatigue
      }
      alert(res.msg)
    } else {
      alert(res.msg)
    }
  } catch (e) {
    alert('喂食失败')
  }
}

// 工具函数
const getRarityName = (rarity) => {
  switch (rarity) {
    case 1: return '普通'
    case 2: return '稀有'
    case 3: return '史诗'
    default: return '未知'
  }
}

const foodNames = {
  fish: '鱼干',
  bone: '骨头',
  seed: '种子',
  nut: '坚果',
  carrot: '胡萝卜',
  fish_food: '鱼食',
  plankton: '浮游生物',
  golden: '高级食物'
}

const getPreferredFoodName = (key) => {
  return foodNames[key] || key
}
</script>


<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px;  margin-top: 80px; /* 为固定定位的用户信息卡片留出空间 */padding: 20px;}
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }
.carried-banner {
  background: #e6f7ff;
  color: #1890ff;
  padding: 10px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
  width: 300px;
}
.grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 24px; }
.pet-card {
  background: #fff;
  padding: 18px 44px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.pet-icon-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
}
.pet-name { font-size: 24px; font-weight: 700; }
.pet-info { color: #777; margin: 8px 0; }
.pet-actions button {
  margin: 4px;
  padding: 6px 10px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 13px;
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  padding: 30px;
  border-radius: 16px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
}
.modal-title { font-size: 24px; margin-bottom: 16px; }
.detail-info p {
  font-size: 18px;
  margin: 10px 0;
  color: #333;
}
.carried-status { color: #52c41a; font-weight: bold; }
.modal-actions button {
  display: block;
  width: 80%;
  margin: 8px auto;
  padding: 10px;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background: #e6f7ff;
  color: #1890ff;
}
.modal-actions button:disabled {
  background: #ccc;
  color: #666;
  cursor: not-allowed;
}
.btn-uncarry {
  background: #fff2e8;
  color: #fa8c16;
}
.btn-carry {
  background: #f6ffed;
  color: #52c41a;
}
.btn-close {
  margin-top: 20px;
  padding: 8px 16px;
  background: #f0f0f0;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>