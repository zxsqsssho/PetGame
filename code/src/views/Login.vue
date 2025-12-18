<!--code/src/views/Login.vue-->
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
import { api } from '@/api/index.js'

const router = useRouter()

const username = ref("")
const password = ref("")

const login = async () => {
  const res = await api.login(username.value, password.value)
  if (res.code === 0) {
    // 把接口返回的用户名存到本地存储
    localStorage.setItem('username', res.data.username)
    alert("登录成功")
    //登录成功后跳转到主页
    router.push('/home')
  } else {
    alert(res.msg)
  }
}

// 补充goRegister函数（实现注册页跳转）
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
