<template>
  <div class="page-wrap">
    <div class="page-title">宠物</div>

    <!-- 显示当前携带的宠物（可选提示） -->
    <div v-if="carriedPet" class="carried-banner">
      当前携带：{{ carriedPet.icon }} {{ carriedPet.name }}
    </div>
    <div v-else class="carried-banner">
      当前未携带宠物，快去携带一只吧！
    </div>

    <div class="grid">
      <div v-for="pet in pets" :key="pet.id" class="pet-card">
        <div class="pet-icon">{{ pet.icon }}</div>
        <div class="pet-name">{{ pet.name }}</div>
        <div class="pet-info">等级: {{ pet.level }} · {{ pet.rarity }}</div>
        <div class="pet-actions">
          <button @click="openDetail(pet)">详情</button>
        </div>
      </div>
    </div>

    <!-- 宠物详情模态框 -->
    <div v-if="selectedPet" class="modal-overlay" @click="closeDetail">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">{{ selectedPet.name }}</h3>
        <div class="pet-icon">{{ selectedPet.icon }}</div>
        <div class="detail-info">
          <p>等级: {{ selectedPet.level }}</p>
          <p>稀有度: {{ selectedPet.rarity }}</p>
          <p>疲劳度: 
            <span :class="{ 'high-fatigue': selectedPet.fatigue > getMaxFatigue(selectedPet.rarity) * 0.8 }">
              {{ selectedPet.fatigue }} / {{ getMaxFatigue(selectedPet.rarity) }}
            </span>
          </p>
          <p>偏好食物: {{ getPreferredFoodName(selectedPet) }}</p>
          <p>状态: 
            <span :class="{ 'carried-status': isCarried(selectedPet.id) }">
              {{ isCarried(selectedPet.id) ? '✅ 已携带' : '未携带' }}
            </span>
          </p>
        </div>
        <div class="modal-actions">
          <button @click="feedSpecific(selectedPet, 'normal')" :disabled="selectedPet.fatigue <= 0">
            喂 {{ getPreferredFoodName(selectedPet) }}（-10）
          </button>
          <button @click="feedSpecific(selectedPet, 'golden')" :disabled="selectedPet.fatigue <= 0">
            喂高级食物（-20）
          </button>
          <button  v-if="isCarried(selectedPet.id)" @click="toggleCarry(selectedPet)" >
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
import { ref, computed, watch, onMounted } from 'vue'

const pets = ref([
  { id: 1, name: '小猫', level: 1, rarity: '普通', icon: '🐱', fatigue: 10, preferredFood: 'fish' },
  { id: 2, name: '小狗', level: 2, rarity: '普通', icon: '🐶', fatigue: 5, preferredFood: 'bone' },
  { id: 3, name: '水灵', level: 5, rarity: '稀有', icon: '🐟', fatigue: 20, preferredFood: 'fish_food' },
])

// ✅ 模拟玩家拥有的食物库存（key: 食物类型, value: 数量）
const foodInventory = {
  fish: 2,        // 有 2 个鱼干
  bone: 0,        // 没有骨头
  fish_food: 1,   // 有 1 份鱼食
  golden: 1       // 有 1 份高级食物
}

//  持久化：每次 pets 变更，自动保存到 localStorage
watch(pets, (newVal) => {
  localStorage.setItem('pets', JSON.stringify(newVal))
}, { deep: true })

//  初始化：从 localStorage 恢复宠物数据（如果存在）
onMounted(() => {
  const saved = localStorage.getItem('pets')
  if (saved) {
    try {
      pets.value = JSON.parse(saved)
    } catch (e) {
      console.warn('宠物数据解析失败，使用默认数据', e)
      // 可选：清空错误数据
      localStorage.removeItem('pets')
    }
  }
})

const selectedPet = ref(null)

// === 携带宠物逻辑 ===
const carriedPetId = ref(localStorage.getItem('carriedPetId'))

const carriedPet = computed(() => {
  return pets.value.find(p => String(p.id) === carriedPetId.value) || null
})

function isCarried(petId) {
  return String(petId) === carriedPetId.value
}

function toggleCarry(pet) {
  if (isCarried(pet.id)) {
    // 取消携带
    carriedPetId.value = null
    localStorage.removeItem('carriedPetId')
  } else {
    // 携带该宠物（自动替换）
    carriedPetId.value = String(pet.id)
    localStorage.setItem('carriedPetId', carriedPetId.value)
  }
}

// 食物名称映射
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

function getPreferredFoodName(pet) {
  return foodNames[pet.preferredFood] || pet.preferredFood
}

function getMaxFatigue(rarity) {
  if (rarity === '普通') return 10
  if (rarity === '稀有') return 50
  return 100 // 史诗
}

// 打开详情
const openDetail = (pet) => {
  selectedPet.value = { ...pet }
}

// 关闭详情
const closeDetail = () => {
  selectedPet.value = null
}

// 专属喂食逻辑
const feedSpecific = (pet, type) => {
  // 疲劳为 0 时不能喂
  if (pet.fatigue <= 0) {
    alert('宠物不疲劳，无需喂食！')
    return
  }
  if (type === 'normal') {
    const foodKey = pet.preferredFood
    const foodName = getPreferredFoodName(pet)
    const hasFood = foodInventory[foodKey] > 0
    if (!hasFood) {
      alert(`暂无该食物：${foodName}`)
      return
    }
    // 扣除食物（可选：后续可加动画或更新 UI）
    foodInventory[foodKey] -= 1
    pet.fatigue = Math.max(0, pet.fatigue - 10)
    alert(`成功喂食 ${foodName}！${pet.name} 疲劳减少 10 点`)
  } else if (type === 'golden') {
    const hasGolden = foodInventory.golden > 0
    if (!hasGolden) {
      alert('暂无该食物：高级食物')
      return
    }
    foodInventory.golden -= 1
    pet.fatigue = Math.max(0, pet.fatigue - 20)
    alert('成功喂食高级食物！疲劳减少 20 点')
  }
  
  // 同步更新原始列表中的宠物状态
  const original = pets.value.find(p => p.id === pet.id)
  if (original) {
    original.fatigue = pet.fatigue
  }
}
</script>

<style scoped>
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }

/* 携带提示横幅 */
.carried-banner {
  background: #e6f7ff;
  color: #1890ff;
  padding: 10px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
  width:300px
}

.grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 24px; }
.pet-card {
  background: #fff; padding: 18px 44px;  border-radius: 12px;
  text-align: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04);
}
.pet-icon { font-size: 60px; margin-bottom: 8px; }
.pet-name { font-size: 24px; font-weight: 700; }
.pet-info { color: #777; margin: 8px 0; }
.pet-actions {
  margin-top: 12px;
}
.pet-actions button {
  margin: 4px;
  padding: 6px 10px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 13px;
}
/* 模态框样式 */
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
.modal-title {
  font-size: 24px;
  margin-bottom: 16px;
}
.detail-info p {
  font-size: 18px;
  margin: 10px 0;
  color: #333;
}
.carried-status {
  color: #52c41a;
  font-weight: bold;
}
.high-fatigue {
  color: #ff4d4f;
  font-weight: bold;
}
.modal-actions {
  margin: 20px 0;
}
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
.btn-carried-small {
  background: #ffe58f;
  color: #b26a00;
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