<template>
  <div class="auth-page">

    <div class="auth-card">
      <div class="auth-title">注册</div>

      <input v-model="username" type="text" placeholder="账号" class="auth-input" />
      <input v-model="password" type="password" placeholder="密码" class="auth-input" />
      <input v-model="confirm" type="password" placeholder="确认密码" class="auth-input" />

      <button class="auth-btn" @click="register">注册</button>

      <div class="switch-text">
        已有账号？
        <span class="link" @click="goLogin">返回登录</span>
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
const confirm = ref('')

const register = () => {
  if (!username.value || !password.value || !confirm.value)
    return alert("请填写所有字段")

  if (password.value !== confirm.value)
    return alert("两次密码不一致")

  // 预留后端 API
  // await axios.post('/register', { username, password })

  alert("注册成功（示意）")
  router.push('/login')
}

const goLogin = () => router.push('/login')
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
