// src/stores/usePlayerStore.js
import { defineStore } from 'pinia'

// 宠物模板（掉落池）- ✅ 已按图鉴内容完整定义
const PET_TEMPLATES = {
  普通: [
    { name: '小猫', icon: '🐱', preferredFood: 'fish' },
    { name: '小狗', icon: '🐶', preferredFood: 'bone' },
    { name: '麻雀', icon: '🐦', preferredFood: 'seed' },
    { name: '松鼠', icon: '🐿️', preferredFood: 'nut' },
    { name: '白兔', icon: '🐇', preferredFood: 'carrot' }
  ],
  稀有: [
    { name: '锦鲤', icon: '🐟', preferredFood: 'fish_food' },
    { name: '电鳗', icon: '⚡', preferredFood: 'fish_food' },
    { name:'发光水母', icon:'🪼',preferredFood:'plankton' },
    { name:'深海章鱼', icon:'🐙',preferredFood:'fish_food' },
    { name:'水晶虾', icon:'🦐',preferredFood:'fish_food' }
  ],
  史诗: [
    { name: '石像守卫', icon: '🗿', preferredFood: 'relic_core' },
    { name: '遗迹灵魂', icon: '👻', preferredFood: 'relic_core' },
    { name:'时光蜥蜴', icon:'🦎',preferredFood:'spirit_essence' },
    { name:'符文猫', icon:'🐈‍⬛',preferredFood:'spirit_essence' },
    { name:'星尘龟', icon:'🐢',preferredFood:'spirit_essence' }
  ]
}

// 食物名称映射（用于显示）
export const FOOD_NAMES = {
  fish: '鱼干',
  bone: '骨头',
  seed: '种子',
  nut: '坚果',
  carrot: '胡萝卜',
  fish_food: '鱼食',
  plankton: '浮游生物',
  relic_core: '遗迹核心',
  spirit_essence: '灵魂精华',
  golden: '高级食物'
}

export const usePlayerStore = defineStore('player', {
  // 状态
  state: () => ({
    level: 5,
    gold: 350,
    pets: [
      { id: 1, name: '小猫', rarity: '普通', icon: '🐱', fatigue: 10, preferredFood: 'fish' },
      { id: 2, name: '小狗', rarity: '普通', icon: '🐶', fatigue: 5, preferredFood: 'bone' },
      { id: 3, name: '锦鲤', rarity: '稀有', icon: '🐟', fatigue: 20, preferredFood: 'fish_food' }
    ],
    carriedPetId: null, // 携带的宠物 ID
    nextPetId: 4,       // 用于生成唯一 ID
    inventory: {        // 背包
      fish: 2,
      bone: 0,
      golden: 1
    }
  }),

  // 计算属性
  getters: {
    carriedPet() {
      if (!this.carriedPetId) return null
      return this.pets.find(p => p.id === this.carriedPetId) || null
    },
    maxFatigue() {
      return (rarity) => {
        if (rarity === '普通') return 10
        if (rarity === '稀有') return 50
        return 100 // 史诗
      }
    },
    
    // 宠物图鉴（根据玩家拥有的宠物自动标记）
    petDex() {
      const ownedPetNames = new Set(this.pets.map(p => p.name))
      return [
        { id:1, name:'小猫', icon:'🐱', rarity:'普通' },
        { id:2, name:'小狗', icon:'🐶', rarity:'普通' },
        { id:3, name:'麻雀', icon:'🐦', rarity:'普通' },
        { id:4, name:'松鼠', icon:'🐿️', rarity:'普通' },
        { id:5, name:'白兔', icon:'🐇', rarity:'普通' },
        { id:6, name:'锦鲤', icon:'🐟', rarity:'稀有' },
        { id:7, name:'电鳗', icon:'⚡', rarity:'稀有' },
        { id:8, name:'发光水母', icon:'🪼', rarity:'稀有' },
        { id:9, name:'深海章鱼', icon:'🐙', rarity:'稀有' },
        { id:10, name:'水晶虾', icon:'🦐', rarity:'稀有' },
        { id:11, name:'石像守卫', icon:'🗿', rarity:'史诗' },
        { id:12, name:'遗迹灵魂', icon:'👻', rarity:'史诗' },
        { id:13, name:'时光蜥蜴', icon:'🦎', rarity:'史诗' },
        { id:14, name:'符文猫', icon:'🐈‍⬛', rarity:'史诗' },
        { id:15, name:'星尘龟', icon:'🐢', rarity:'史诗' }
      ].map(p => ({ ...p, collected: ownedPetNames.has(p.name) }))
    },
    
    // 食物图鉴（根据背包自动标记）
    foodDex() {
      const ownedFoodKeys = new Set(Object.keys(this.inventory).filter(key => this.inventory[key] > 0))
      return [
        { id:1, name:'鱼干', icon:'🐟', type:'普通食物' },
        { id:2, name:'骨头', icon:'🦴', type:'普通食物' },
        { id:3, name:'种子', icon:'🌱', type:'普通食物' },
        { id:4, name:'坚果', icon:'🥜', type:'普通食物' },
        { id:5, name:'胡萝卜', icon:'🥕', type:'普通食物' },
        { id:6, name:'鱼食', icon:'🐠', type:'稀有食物' },
        { id:7, name:'浮游生物', icon:'🦠', type:'稀有食物' },
        { id:8, name:'遗迹核心', icon:'🔮', type:'史诗食物' },
        { id:9, name:'灵魂精华', icon:'✨', type:'史诗食物' },
        { id:10, name:'高级食物', icon:'🌟', type:'通用食物' }
      ].map(f => ({ ...f, collected: ownedFoodKeys.has(this.foodKeyMap[f.name]) }))
    },
    
    // 食物名称映射（用于图鉴）- ✅ 已按图鉴内容完整定义
    foodKeyMap() {
      return {
        '鱼干': 'fish',
        '骨头': 'bone',
        '种子': 'seed',
        '坚果': 'nut',
        '胡萝卜': 'carrot',
        '鱼食': 'fish_food',
        '浮游生物': 'plankton',
        '遗迹核心': 'relic_core',
        '灵魂精华': 'spirit_essence',
        '高级食物': 'golden'
      }
    },
    
    // 已收集数量
    petsCollectedCount() {
      return this.petDex.filter(p => p.collected).length
    },
    foodsCollectedCount() {
      return this.foodDex.filter(f => f.collected).length
    }
  },

  // 动作
  actions: {
    // 携带宠物
    setCarry(petId) {
      this.carriedPetId = petId
    },
    clearCarry() {
      this.carriedPetId = null
    },

    // 喂食宠物
    feedPet(petId, type = 'normal') {
      const pet = this.pets.find(p => p.id === petId)
      if (!pet || pet.fatigue <= 0) return false

      let foodKey = type === 'golden' ? 'golden' : pet.preferredFood
      if (this.inventory[foodKey] <= 0) return false

      const reduce = type === 'golden' ? 20 : 10
      pet.fatigue = Math.max(0, pet.fatigue - reduce)
      this.inventory[foodKey]--

      return true
    },

    // 探索（更新疲劳 + 掉落）
    explore(location) {
      if (this.level < location.level) return '等级不足'

      // 检查携带宠物疲劳
      if (this.carriedPet) {
        const max = this.maxFatigue(this.carriedPet.rarity)
        if (this.carriedPet.fatigue + location.fatigue > max) {
          return '宠物太累'
        }
      }

      const results = []

      // 1. 更新疲劳
      if (this.carriedPet) {
        this.carriedPet.fatigue += location.fatigue
        results.push(`宠物疲劳 +${location.fatigue}`)
      }

      // 2. 金币
      const gold = Math.floor(Math.random() * 50) + 10
      this.gold += gold
      results.push(`获得金币 ${gold}`)

      // 3. 食物
      if (Math.random() < 0.85) {
        const foods = {
          普通: ['fish', 'bone', 'seed', 'nut', 'carrot'],
          稀有: ['fish_food', 'plankton'],  
          史诗: ['golden', 'relic_core', 'spirit_essence'] // ✅ 史诗地点可掉落史诗食物
        }[location.rarity] || ['fish']

        const foodKey = foods[Math.floor(Math.random() * foods.length)]
        this.inventory[foodKey] = (this.inventory[foodKey] || 0) + 1
        
        // ✅ 返回具体食物名称
        const foodName = FOOD_NAMES[foodKey] || foodKey
        results.push(`获得食物：${foodName}`)
      }

      // 4. 新宠物
      if (Math.random() < 0.3) {
        const pool = PET_TEMPLATES[location.rarity]
        if (pool) {
          const template = pool[Math.floor(Math.random() * pool.length)]
          const newPet = {
            id: this.nextPetId++,
            name: template.name,
            icon: template.icon,
            rarity: location.rarity,
            preferredFood: template.preferredFood,
            fatigue: 0
          }
          this.pets.push(newPet)
          results.push(`获得新宠物：${template.name}`)
        }
      }

      // 5. 抽奖券
      if (location.rarity === '稀有' && Math.random() < 0.4) {
        results.push('获得普通抽奖券')
      }
      if (location.rarity === '史诗' && Math.random() < 0.4) {
        results.push('获得高级抽奖券')
      }

      return results.join('； ')
    }
  }
})