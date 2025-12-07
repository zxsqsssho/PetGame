<!--code/src/views/Register.vue-->
<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 class="text-3xl font-bold mb-6 text-center">注册</h2>

      <form @submit.prevent="register">
        <div class="mb-4">
          <label class="block text-gray-700 mb-2">账号</label>
          <input v-model="username" type="text" class="w-full p-2 border rounded">
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 mb-2">密码</label>
          <input v-model="password" type="password" class="w-full p-2 border rounded">
        </div>

        <button class="w-full p-2 bg-green-500 text-white rounded hover:bg-green-600" type="submit">
          注册
        </button>

        <p class="mt-4 text-center">
          已有账号？
          <router-link to="/login" class="text-blue-500 hover:underline">前往登录</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { api } from '@/api/index.js'

const username = ref("")
const password = ref("")
const router = useRouter()

const register = async () => {
  const res = await api.register(username.value, password.value)
  if (res.code === 0) {
    alert("注册成功")
    router.push("/login")
  } else {
    alert(res.msg)
  }
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
