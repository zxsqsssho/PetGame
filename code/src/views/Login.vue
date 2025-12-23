<!--code/src/views/Login.vue-->
<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">🐾</div>
        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">登录你的宠物世界账号</p>
      </div>

      <div class="form-container">
        <div class="input-group">
          <input
              v-model="username"
              type="text"
              placeholder="请输入账号"
              class="auth-input"
              @keyup.enter="login"
          />
          <span class="input-icon">👤</span>
        </div>

        <div class="input-group">
          <input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              class="auth-input"
              @keyup.enter="login"
          />
          <span class="input-icon">🔒</span>
        </div>

        <button class="auth-btn" @click="login" :disabled="loading">
          <span v-if="loading">登录中...</span>
          <span v-else>登录</span>
        </button>

        <div class="auth-footer">
          <p class="switch-text">
            还没有账号？
            <span class="link" @click="goRegister">立即注册</span>
          </p>
          <div class="divider">
            <span>或</span>
          </div>
          <p class="hint-text">登录即可开始你的宠物冒险之旅</p>
        </div>
      </div>
    </div>

    <div class="bg-decoration">
      <div class="decoration pet1">🐱</div>
      <div class="decoration pet2">🐶</div>
      <div class="decoration pet3">🐰</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api/index.js'

const router = useRouter()

const username = ref("")
const password = ref("")
const loading = ref(false)

const login = async () => {
  if (!username.value.trim() || !password.value.trim()) {
    alert("请填写账号和密码")
    return
  }

  loading.value = true
  try {
    const res = await api.login(username.value, password.value)
    if (res.code === 0) {
      // 存储用户信息
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      localStorage.setItem('token', 'user_' + res.data.id) // 模拟token

      alert("登录成功")
      router.push('/home')
    } else {
      alert(res.msg || "登录失败")
    }
  } catch (error) {
    alert("网络错误，请稍后重试")
    console.error("登录错误:", error)
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f9f1e5 0%, #f5e7d3 100%);
  position: relative;
  overflow: hidden;
}

.auth-card {
  width: 420px;
  background: white;
  padding: 40px 35px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  position: relative;
  z-index: 10;
  animation: fadeIn 0.6s ease-out;
}

.auth-header {
  margin-bottom: 30px;
}

.auth-logo {
  font-size: 48px;
  margin-bottom: 15px;
  animation: bounce 2s infinite;
}

.auth-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  color: #333;
  background: linear-gradient(45deg, #6dcf7a, #4ecdc4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.auth-subtitle {
  color: #777;
  font-size: 14px;
  margin-bottom: 10px;
}

.form-container {
  width: 100%;
}

.input-group {
  position: relative;
  margin-bottom: 20px;
  width: 100%;
}

.auth-input {
  width: 100%;
  padding: 15px 50px 15px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #fafafa;
  box-sizing: border-box;
}

.auth-input:focus {
  outline: none;
  border-color: #6dcf7a;
  background: white;
  box-shadow: 0 0 0 3px rgba(109, 207, 122, 0.1);
}

.auth-input::placeholder {
  color: #aaa;
}

.input-icon {
  position: absolute;
  right: 18px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  font-size: 18px;
  pointer-events: none;
}

.auth-btn {
  width: 100%;
  padding: 16px;
  margin-top: 10px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(45deg, #6dcf7a, #4ecdc4);
  border-radius: 12px;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(109, 207, 122, 0.3);
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(109, 207, 122, 0.4);
}

.auth-btn:active:not(:disabled) {
  transform: translateY(0);
}

.auth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 30px;
}

.switch-text {
  color: #666;
  font-size: 15px;
  margin-bottom: 20px;
}

.link {
  color: #4ecdc4;
  cursor: pointer;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.3s;
}

.link:hover {
  color: #3bb4ac;
  text-decoration: underline;
}

.divider {
  position: relative;
  margin: 25px 0;
  text-align: center;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: #eee;
}

.divider span {
  position: relative;
  background: white;
  padding: 0 15px;
  color: #aaa;
  font-size: 13px;
}

.hint-text {
  color: #999;
  font-size: 13px;
  margin-top: 20px;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration {
  position: absolute;
  font-size: 60px;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.pet1 {
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.pet2 {
  top: 60%;
  right: 8%;
  animation-delay: 2s;
}

.pet3 {
  bottom: 15%;
  left: 15%;
  animation-delay: 4s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@media (max-width: 480px) {
  .auth-card {
    width: 90%;
    max-width: 350px;
    padding: 30px 25px;
    margin: 20px;
  }

  .auth-title {
    font-size: 24px;
  }

  .auth-input {
    padding: 14px 45px 14px 14px;
  }

  .input-icon {
    right: 15px;
  }
}
</style>