<!--code/src/views/Tasks.vue-->
<template>
  <div class="page-wrap">
    <div class="page-title">任务</div>

    <div class="task-list">
      <div v-for="t in tasks" :key="t.id" class="task-card">
        <div class="task-title">{{ t.title }}</div>
        <div class="task-desc">{{ t.desc }}</div>
        <div class="task-progress">{{ t.progress }} / {{ t.target }}</div>
        <div><button :disabled="t.progress < t.target" @click="claim(t)">{{ t.progress >= t.target ? '领取' : '进行中' }}</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api/index.js'

const tasks = ref([])

onMounted(async () => {
  const res = await api.getTasks()
  tasks.value = res.data
})

const claim = async (t) => {
  const res = await api.claimTask(t.id)
  if (res.code === 0) {
    alert(`奖励：金币 ${res.data.coinsGained}, 经验 ${res.data.expGained}`)
  }
}
</script>


<style scoped>
.page-wrap { max-width:1100px; margin:40px auto; padding:0 20px; }
.page-title { font-size:28px; font-weight:700; margin-bottom:18px; }

.task-list { display:flex; flex-direction:column; gap:14px; }
.task-card { background:#fff; padding:14px; border-radius:10px; box-shadow: 0 6px 18px rgba(0,0,0,0.04); }
.task-title { font-weight:700; }
.task-desc { color:#666; margin:8px 0; }
.task-progress { color:#888; margin-bottom:8px; }
.task-card button { padding:8px 12px; border-radius:8px; border:none; cursor:pointer; }
</style>
