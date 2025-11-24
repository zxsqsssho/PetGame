<template>
  <div class="auth-page">

    <div class="auth-card">
      <div class="auth-title">登录</div>

      <input v-model="username" type="text" placeholder="账号" class="auth-input" />
      <input v-model="password" type="password" placeholder="密码" class="auth-input" />

      <button class="auth-btn" @click="login">登录</button>

      <div class="switch-text">
        还没有账号？
        <span class="link" @click="goRegister">去注册</span>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('')
const password = ref('')

const login = () => {
  if (!username.value || !password.value) return alert("请输入账号和密码")

  // 此处预留后端 API
  // await axios.post('/login', { username, password })

  alert("登录成功（示意）")
  router.push('/') // 回主页
}

const goRegister = () => router.push('/register')
</script>

<style scoped>
.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f9f1e5;
}

.auth-card {
  width: 360px;
  background: white;
  padding: 28px;
  border-radius: 14px;
  text-align: center;
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
}

.auth-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
}

.auth-input {
  width: 100%;
  padding: 12px;
  margin-bottom: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
}

.auth-btn {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
  background: #6dcf7a;
  border-radius: 8px;
  border: none;
  color: white;
  cursor: pointer;
}

.auth-btn:hover {
  background: #51c462;
}

.switch-text {
  margin-top: 14px;
  color: #666;
}

.link {
  color: #3b82f6;
  cursor: pointer;
}
</style>
