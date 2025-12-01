<template>
  <div class="page-wrap">
    <div class="page-title">宠物</div>

    <!-- 显示当前携带的宠物（可选提示） -->
    <div v-if="store.carriedPet" class="carried-banner">
      当前携带：{{ store.carriedPet.icon }} {{ store.carriedPet.name }}
    </div>
    <div v-else class="carried-banner">
      当前未携带宠物，快去携带一只吧！
    </div>

    <div class="grid">
      <div v-for="pet in store.pets" :key="pet.id" class="pet-card">
        <div class="pet-icon">{{ pet.icon }}</div>
        <div class="pet-name">{{ pet.name }}</div>
        <div class="pet-info">等级: {{ pet.level }} · {{ pet.rarity }}</div>
        <div class="pet-actions">
          <button @click="openDetail(pet)">详情</button>
          <button 
            v-if="store.carriedPetId === pet.id" 
            class="btn-carried"
            @click="store.clearCarry()"
          >
            已携带
          </button>
          <button 
            v-else 
            class="btn-carry"
            @click="store.setCarry(pet.id)"
          >
            携带
          </button>
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
            <span :class="{ 'high-fatigue': selectedPet.fatigue > store.maxFatigue(selectedPet.rarity) * 0.8 }">
              {{ selectedPet.fatigue }} / {{ store.maxFatigue(selectedPet.rarity) }}
            </span>
          </p>
          <p>偏好食物: {{ foodNames[selectedPet.preferredFood] || selectedPet.preferredFood }}</p>
          <p>状态: 
            <span :class="{ 'carried-status': store.carriedPetId === selectedPet.id }">
              {{ store.carriedPetId === selectedPet.id ? '✅ 已携带' : '未携带' }}
            </span>
          </p>
        </div>
        <div class="modal-actions">
          <button 
            @click="feedPet(selectedPet.id, 'normal')" 
            :disabled="selectedPet.fatigue <= 0 || !hasFood(selectedPet.preferredFood)"
          >
            喂 {{ foodNames[selectedPet.preferredFood] }}（-10）
          </button>
          <button 
            @click="feedPet(selectedPet.id, 'golden')" 
            :disabled="selectedPet.fatigue <= 0 || !hasFood('golden')"
          >
            喂高级食物（-20）
          </button>
          <button v-if="store.carriedPetId === selectedPet.id" @click="store.clearCarry()">
            取消携带
          </button>
          <button v-else @click="store.setCarry(selectedPet.id)" class="btn-carry">
            设为携带
          </button>
        </div>
        <button class="btn-close" @click="closeDetail">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { usePlayerStore, FOOD_NAMES } from '@/stores/usePlayerStore'

const store = usePlayerStore()

const selectedPet = ref(null)
// const foodNames = {
//   fish: '鱼干',
//   bone: '骨头',
//   seed: '种子',
//   nut: '坚果',
//   carrot: '胡萝卜',
//   fish_food: '鱼食',
//   plankton: '浮游生物',
//   golden: '高级食物'
// }
const foodNames = FOOD_NAMES

function hasFood(foodKey) {
  return (store.inventory[foodKey] || 0) > 0
}

function openDetail(pet) {
  selectedPet.value = pet
}

function closeDetail() {
  selectedPet.value = null
}

function feedPet(petId, type) {
  const success = store.feedPet(petId, type)
  if (!success) {
    alert('暂无该食物')
  }
}
</script>

<style scoped>
/* 与原样式相同 */
.page-wrap { max-width: 1100px; margin: 40px auto; padding: 0 20px; }
.page-title { font-size: 28px; font-weight: 700; margin-bottom: 18px; }
.carried-banner { background: #e6f7ff; color: #1890ff; padding: 10px 20px; border-radius: 8px; margin-bottom: 20px; text-align: center; font-weight: 600; width:300px }
.grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 24px; }
.pet-card { background: #fff; padding: 18px 44px; border-radius: 12px; text-align: center; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.pet-icon { font-size: 55px; margin-bottom: 8px; }
.pet-name { font-size: 18px; font-weight: 700; }
.pet-info { color: #777; margin: 8px 0; }
.pet-actions { margin-top: 12px; }
.pet-actions button { margin: 4px; padding: 6px 10px; border-radius: 6px; border: none; cursor: pointer; font-size: 13px; }
.btn-carry { background: #e6f7ff; color: #1890ff; border: 1px solid #91d5ff; }
.btn-carried { background: #ffe58f; color: #b26a00; border: 1px solid #ffd666; cursor: not-allowed; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 30px; border-radius: 16px; text-align: center; max-width: 400px; width: 90%; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }
.modal-title { font-size: 24px; margin-bottom: 16px; }
.detail-info p { font-size: 18px; margin: 10px 0; color: #333; }
.carried-status { color: #52c41a; font-weight: bold; }
.high-fatigue { color: #ff4d4f; font-weight: bold; }
.modal-actions { margin: 20px 0; }
.modal-actions button { display: block; width: 80%; margin: 8px auto; padding: 10px; font-size: 16px; border: none; border-radius: 8px; cursor: pointer; background: #e6f7ff; color: #1890ff; }
.modal-actions button:disabled { background: #ccc; color: #666; cursor: not-allowed; }
.btn-close { margin-top: 20px; padding: 8px 16px; background: #f0f0f0; border: none; border-radius: 6px; cursor: pointer; }
</style>