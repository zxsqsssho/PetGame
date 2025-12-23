<!--code/src/views/Register.vue-->
<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">🎮</div>
        <h1 class="auth-title">创建新账号</h1>
        <p class="auth-subtitle">开始你的宠物收集之旅</p>
      </div>

      <div class="form-container">
        <!-- 账号输入 -->
        <div class="input-group">
          <input
              v-model="formData.account"
              type="text"
              placeholder="请输入账号"
              class="auth-input"
              @keyup.enter="register"
          />
          <span class="input-icon">👤</span>
          <div v-if="errors.account" class="error-message">{{ errors.account }}</div>
        </div>

        <!-- 昵称输入 -->
        <div class="input-group">
          <input
              v-model="formData.name"
              type="text"
              placeholder="请输入昵称"
              class="auth-input"
              @keyup.enter="register"
          />
          <span class="input-icon">✨</span>
          <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
        </div>

        <!-- 头像选择区域 -->
        <div class="avatar-selector-section">
          <div class="section-header">
            <div class="section-icon">🖼️</div>
            <h3 class="section-title">选择头像</h3>
          </div>

          <!-- 头像选择网格 -->
          <div class="avatar-grid">
            <div
                v-for="(avatar, index) in avatarOptions"
                :key="index"
                class="avatar-card"
                :class="{
                'selected': selectedAvatar === avatar.value,
                'pulse': selectedAvatar === avatar.value
              }"
                @click="selectAvatar(avatar.value)"
            >
              <div class="avatar-frame">
                <img
                    :src="avatar.image"
                    :alt="avatar.label"
                    class="avatar-img"
                />
                <div v-if="selectedAvatar === avatar.value" class="selected-badge">
                  <span class="badge-icon">✓</span>
                </div>
                <div class="avatar-overlay">
                  <span class="overlay-text">选择</span>
                </div>
              </div>
              <div class="avatar-info">
                <span class="avatar-name">{{ avatar.label }}</span>
                <span v-if="selectedAvatar === avatar.value" class="selected-label">
                  <span class="selected-icon">⭐</span> 已选择
                </span>
              </div>
            </div>
          </div>

          <!-- 当前选择预览 -->
          <div v-if="selectedAvatar" class="current-selection">
            <div class="selection-header">
              <span class="selection-icon">👁️</span>
              <span class="selection-title">当前选择</span>
            </div>
            <div class="selection-preview">
              <div class="preview-frame">
                <img
                    :src="getAvatarImage(selectedAvatar)"
                    alt="已选头像"
                    class="preview-img"
                />
                <div class="preview-glow"></div>
              </div>
              <div class="preview-info">
                <div class="preview-name">{{ getAvatarLabel(selectedAvatar) }}</div>
                <div class="preview-status">
                  <span class="status-icon">✅</span>
                  <span class="status-text">准备就绪</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="errors.avatar" class="error-message error-avatar">{{ errors.avatar }}</div>
        </div>

        <!-- 密码输入 -->
        <div class="input-group">
          <input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              class="auth-input"
              @keyup.enter="register"
          />
          <span class="input-icon">🔒</span>
          <div v-if="errors.password" class="error-message">{{ errors.password }}</div>
        </div>

        <!-- 确认密码 -->
        <div class="input-group">
          <input
              v-model="confirmPassword"
              type="password"
              placeholder="确认密码"
              class="auth-input"
              @keyup.enter="register"
          />
          <span class="input-icon">✓</span>
          <div v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</div>
        </div>

        <!-- 注册按钮 -->
        <button class="auth-btn" @click="register" :disabled="loading">
          <span class="btn-content">
            <span v-if="loading" class="btn-icon loading-icon">⏳</span>
            <span v-else class="btn-icon">🚀</span>
            <span v-if="loading">注册中...</span>
            <span v-else>开始冒险</span>
          </span>
          <div class="btn-shine"></div>
        </button>

        <!-- 底部链接 -->
        <div class="auth-footer">
          <p class="switch-text">
            <span class="link-text">已有账号？</span>
            <span class="link" @click="goLogin">
              <span class="link-icon">↗️</span>
              立即登录
            </span>
          </p>
          <p class="terms-text">
            注册即表示同意
            <span class="link" @click="showTerms">
              <span class="link-icon">📜</span>
              用户协议
            </span>
          </p>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="decoration pet1">🐱</div>
      <div class="decoration pet2">🐶</div>
      <div class="decoration pet3">🐟</div>
      <div class="decoration pet4">🐦</div>
    </div>

    <!-- 浮动装饰 -->
    <div class="floating-pets">
      <div class="floating-pet fp1" :style="{ animationDelay: '0s' }">🎭</div>
      <div class="floating-pet fp2" :style="{ animationDelay: '1s' }">🎨</div>
      <div class="floating-pet fp3" :style="{ animationDelay: '2s' }">✨</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";

// 导入头像图片
import txone from '@/assets/txone.jpg'
import txtwo from '@/assets/txtwo.jpg'
import txthree from '@/assets/txthree.jpg'

const router = useRouter()

// 表单数据
const formData = reactive({
  account: "",
  name: "",
  password: "",
  avatar: ""
})

const confirmPassword = ref("")
const loading = ref(false)
const selectedAvatar = ref("")
const errors = reactive({
  account: "",
  name: "",
  password: "",
  confirmPassword: "",
  avatar: ""
})

// 头像选项配置
const avatarOptions = [
  {
    value: "txone.jpg",
    label: "神秘学者",
    image: txone,
    description: "智慧与神秘的化身"
  },
  {
    value: "txtwo.jpg",
    label: "光明战士",
    image: txtwo,
    description: "勇气与正义的守护者"
  },
  {
    value: "txthree.jpg",
    label: "森林精灵",
    image: txthree,
    description: "自然与生命的使者"
  }
]

// 获取头像图片
const getAvatarImage = (value) => {
  const option = avatarOptions.find(opt => opt.value === value)
  return option ? option.image : ''
}

// 获取头像标签
const getAvatarLabel = (value) => {
  const option = avatarOptions.find(opt => opt.value === value)
  return option ? option.label : ''
}

// 选择头像
const selectAvatar = (value) => {
  selectedAvatar.value = value
  formData.avatar = `/avatars/${value}`
  errors.avatar = ""
}

// 清除错误信息
const clearErrors = () => {
  errors.account = ""
  errors.name = ""
  errors.password = ""
  errors.confirmPassword = ""
  errors.avatar = ""
}

// 验证表单
const validateForm = () => {
  clearErrors()
  let isValid = true

  // 验证账号
  if (!formData.account.trim()) {
    errors.account = "请输入账号"
    isValid = false
  } else if (formData.account.length < 3 || formData.account.length > 20) {
    errors.account = "账号长度需在3-20个字符之间"
    isValid = false
  }

  // 验证昵称
  if (!formData.name.trim()) {
    errors.name = "请输入昵称"
    isValid = false
  } else if (formData.name.length < 2 || formData.name.length > 20) {
    errors.name = "昵称长度需在2-20个字符之间"
    isValid = false
  }

  // 验证头像
  if (!selectedAvatar.value) {
    errors.avatar = "请选择一张头像"
    isValid = false
  }

  // 验证密码
  if (!formData.password.trim()) {
    errors.password = "请输入密码"
    isValid = false
  } else if (formData.password.length < 6 || formData.password.length > 20) {
    errors.password = "密码长度需在6-20个字符之间"
    isValid = false
  }

  // 验证确认密码
  if (!confirmPassword.value.trim()) {
    errors.confirmPassword = "请确认密码"
    isValid = false
  } else if (formData.password !== confirmPassword.value) {
    errors.confirmPassword = "两次输入的密码不一致"
    isValid = false
  }

  return isValid
}

// 注册函数
const register = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    const requestData = {
      username: formData.account,
      name: formData.name,
      password: formData.password,
      avatar: formData.avatar
    }

    console.log('准备发送注册数据:', requestData)

    // 测试多个可能的URL
    const testUrls = [
      'http://localhost:8080/pet_game/api/user/register',
      'http://localhost:8080/api/user/register',
      'http://127.0.0.1:8080/pet_game/api/user/register',
      'http://127.0.0.1:8080/api/user/register'
    ]

    let response = null
    let lastError = null

    for (const url of testUrls) {
      try {
        console.log('尝试请求:', url)

        response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(requestData)
        })

        console.log(`URL ${url} 响应状态:`, response.status)

        if (response.ok) {
          const res = await response.json()
          console.log('注册响应数据:', res)

          if (res.code === 0) {
            // 保存用户信息
            localStorage.setItem('userInfo', JSON.stringify(res.data))
            localStorage.setItem('token', 'user-token-' + Date.now())

            // 注册成功动画
            const authBtn = document.querySelector('.auth-btn')
            if (authBtn) {
              authBtn.classList.add('success')
              setTimeout(() => {
                authBtn.classList.remove('success')
                alert("🎉 注册成功！欢迎来到宠物收集之旅！")
                router.push("/login")
              }, 800)
            } else {
              alert("🎉 注册成功！欢迎来到宠物收集之旅！")
              router.push("/login")
            }
            return
          } else {
            alert("❌ " + (res.msg || "注册失败"))
            return
          }
        } else {
          const errorText = await response.text()
          lastError = `HTTP ${response.status}: ${errorText}`
          console.warn('请求失败:', url, response.status, errorText)
        }
      } catch (error) {
        lastError = error.message
        console.warn('请求异常:', url, error.message)
      }
    }

    // 所有URL都失败
    console.error('所有接口尝试都失败，最后错误:', lastError)
    showConnectionError()

  } catch (error) {
    console.error("注册过程异常:", error)
    alert("❌ 注册失败: " + error.message)
  } finally {
    loading.value = false
  }
}

// 显示连接错误帮助信息
const showConnectionError = () => {
  const helpInfo = `
🚫 无法连接到服务器，请检查：

🔧 1. 后端是否运行？
   • 访问 http://localhost:8080/
   • 如果显示 Tomcat 欢迎页，说明服务正常

📂 2. 项目是否部署？
   • 检查 tomcat/webapps/ 下是否有 pet_game 文件夹
   • 或访问 http://localhost:8080/manager 查看部署列表

🔗 3. 接口URL是否正确？
   • 尝试访问 http://localhost:8080/pet_game/
   • 或 http://localhost:8080/api/test (如果部署了测试接口)

⚙️ 4. 需要重启吗？
   • 停止 Tomcat: catalina stop
   • 清理旧的: rm -rf webapps/pet_game*
   • 重启 Tomcat: catalina start

📋 5. 查看日志：
   • Tomcat日志: logs/catalina.out
   • 检查控制台输出

💡 常见解决方案：
   • 确保项目正确打包为 WAR 文件
   • 确认数据库连接配置正确
   • 检查防火墙或端口占用问题
   • 使用开发者工具查看网络请求详情
`

  alert(helpInfo)
}

// 跳转到登录页面
const goLogin = () => {
  router.push("/login")
}

// 显示用户协议
const showTerms = () => {
  alert(`
# 《宠物收集之旅》用户协议

## 欢迎注册
欢迎您注册并使用《宠物收集之旅》游戏服务。在注册前，请仔细阅读以下协议：

## 一、服务条款
1. 您需要提供真实有效的注册信息
2. 您需要妥善保管账号和密码
3. 您同意遵守游戏规则和社区准则

## 二、用户行为规范
1. 禁止使用外挂、作弊程序
2. 禁止发布违法、违规信息
3. 禁止骚扰其他玩家
4. 遵守游戏内的公平竞争原则

## 三、虚拟财产
1. 游戏内的虚拟货币、道具等归游戏运营方所有
2. 禁止虚拟财产与现实货币交易
3. 账号封禁可能导致虚拟财产损失

## 四、隐私保护
1. 我们将保护您的个人信息安全
2. 我们会收集必要的游戏数据用于服务优化
3. 未经您同意，不会向第三方泄露信息

## 五、责任限制
1. 游戏运营方对不可抗力导致的损失不承担责任
2. 因用户违规导致的损失由用户自行承担
3. 游戏运营方有权根据情况调整服务内容

## 六、未成年人保护
1. 未成年人应在家长指导下使用
2. 我们建议家长监管未成年人的游戏时间
3. 如有疑问请联系客服

## 七、协议修改
我们保留修改本协议的权利，修改后会通过适当方式通知。

注册即表示您已阅读并同意以上所有条款。
如有疑问，请联系客服邮箱：support@petgame.com
`)
}

// 组件加载时默认选择第一张头像
onMounted(() => {
  selectAvatar(avatarOptions[0].value)
})
</script>

<style scoped>
/* 基础布局 */
.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg,
  rgba(227, 242, 253, 0.9) 0%,
  rgba(243, 229, 245, 0.9) 50%,
  rgba(255, 248, 225, 0.9) 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.auth-card {
  width: 500px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px 35px;
  border-radius: 24px;
  text-align: center;
  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 10;
  animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 头部样式 */
.auth-header {
  margin-bottom: 30px;
}

.auth-logo {
  font-size: 55px;
  margin-bottom: 15px;
  animation: pulseGlow 2s infinite ease-in-out;
  filter: drop-shadow(0 4px 8px rgba(255, 154, 158, 0.3));
}

.auth-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  background: linear-gradient(45deg, #ff6b9d, #c779d0, #4bc0c8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: -0.5px;
}

.auth-subtitle {
  color: #888;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

/* 输入框样式 */
.input-group {
  position: relative;
  margin-bottom: 22px;
}

.auth-input {
  width: 100%;
  padding: 16px 50px 16px 18px;
  border: 2px solid rgba(224, 224, 224, 0.8);
  border-radius: 14px;
  font-size: 15px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: rgba(248, 249, 250, 0.8);
  box-sizing: border-box;
  color: #444;
  font-weight: 500;
}

.auth-input:focus {
  outline: none;
  border-color: #ff9a9e;
  background: white;
  box-shadow:
      0 0 0 4px rgba(255, 154, 158, 0.15),
      0 4px 20px rgba(255, 154, 158, 0.2);
  transform: translateY(-2px);
}

.auth-input.error {
  border-color: #ff6b6b;
  animation: inputError 0.5s ease;
}

.input-icon {
  position: absolute;
  right: 18px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  font-size: 18px;
  pointer-events: none;
  transition: color 0.3s ease;
}

.auth-input:focus + .input-icon {
  color: #ff9a9e;
}

/* 错误消息 */
.error-message {
  color: #ff6b6b;
  font-size: 13px;
  margin-top: 6px;
  text-align: left;
  padding-left: 10px;
  animation: shakeError 0.4s ease;
  font-weight: 500;
}

.error-avatar {
  margin-top: 15px;
  text-align: center;
}

/* 头像选择器 */
.avatar-selector-section {
  background: linear-gradient(145deg, #f8faff, #f5f0ff);
  border-radius: 18px;
  padding: 25px;
  margin: 25px 0;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
}

.avatar-selector-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff9a9e, #fad0c4, #a1c4fd);
  border-radius: 2px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 25px;
}

.section-icon {
  font-size: 24px;
  animation: bounce 2s infinite;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #444;
  margin: 0;
  letter-spacing: -0.3px;
}

/* 头像网格 */
.avatar-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.avatar-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
}

.avatar-card:hover {
  transform: translateY(-8px);
}

.avatar-card.selected {
  transform: translateY(-5px);
}

.avatar-card.selected .avatar-frame {
  box-shadow:
      0 12px 30px rgba(255, 154, 158, 0.4),
      0 0 0 4px rgba(255, 154, 158, 0.2);
}

.avatar-card.pulse .avatar-frame::after {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  border: 2px solid #ff9a9e;
  animation: pulseRing 1.5s infinite;
  pointer-events: none;
}

.avatar-frame {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  border: 4px solid white;
  box-shadow:
      0 8px 20px rgba(0, 0, 0, 0.1),
      inset 0 0 0 2px rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  background: linear-gradient(45deg, #f8f9fa, #e9ecef);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.avatar-card:hover .avatar-img {
  transform: scale(1.08);
}

.selected-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  background: linear-gradient(45deg, #4cd964, #5ac8fa);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(76, 217, 100, 0.4);
  z-index: 2;
  animation: badgePop 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.badge-icon {
  color: white;
  font-size: 16px;
  font-weight: bold;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar-card:hover .avatar-overlay {
  opacity: 1;
}

.overlay-text {
  color: white;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 1px;
  background: rgba(255, 154, 158, 0.9);
  padding: 6px 12px;
  border-radius: 20px;
}

.avatar-info {
  margin-top: 12px;
  text-align: center;
}

.avatar-name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 4px;
}

.selected-label {
  display: inline-block;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  animation: fadeIn 0.3s ease;
}

.selected-icon {
  font-size: 10px;
  margin-right: 4px;
}

/* 当前选择预览 */
.current-selection {
  background: linear-gradient(135deg, #ffffff, #f8f9ff);
  border-radius: 16px;
  padding: 20px;
  border: 2px solid rgba(255, 154, 158, 0.2);
  box-shadow:
      inset 0 2px 4px rgba(255, 255, 255, 0.6),
      0 4px 12px rgba(0, 0, 0, 0.05);
}

.selection-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.selection-icon {
  font-size: 20px;
  animation: spin 4s linear infinite;
}

.selection-title {
  font-size: 16px;
  font-weight: 700;
  color: #555;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.selection-preview {
  display: flex;
  align-items: center;
  gap: 20px;
}

.preview-frame {
  position: relative;
  width: 80px;
  height: 80px;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: 4px solid white;
  box-shadow:
      0 8px 25px rgba(255, 154, 158, 0.3),
      0 0 0 2px rgba(255, 154, 158, 0.1);
  position: relative;
  z-index: 1;
}

.preview-glow {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4, #a1c4fd);
  filter: blur(15px);
  opacity: 0.4;
  animation: glowPulse 2s infinite alternate;
}

.preview-info {
  flex: 1;
  text-align: left;
}

.preview-name {
  font-size: 18px;
  font-weight: 800;
  color: #333;
  margin-bottom: 8px;
  background: linear-gradient(45deg, #ff6b9d, #c779d0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.preview-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-icon {
  font-size: 14px;
  animation: bounce 1s infinite;
}

.status-text {
  font-size: 14px;
  color: #4cd964;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 注册按钮 */
.auth-btn {
  width: 100%;
  padding: 18px;
  margin-top: 30px;
  font-size: 17px;
  font-weight: 700;
  background: linear-gradient(45deg,
  #ff6b9d 0%,
  #c779d0 30%,
  #4bc0c8 70%,
  #4cd964 100%);
  background-size: 300% 100%;
  border-radius: 16px;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow:
      0 8px 25px rgba(255, 154, 158, 0.4),
      0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-4px);
  box-shadow:
      0 12px 35px rgba(255, 154, 158, 0.5),
      0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  background-position: 100% 0;
}

.auth-btn:active:not(:disabled) {
  transform: translateY(-2px);
  box-shadow:
      0 6px 20px rgba(255, 154, 158, 0.4),
      0 0 0 1px rgba(255, 255, 255, 0.2) inset;
}

.auth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow:
      0 8px 25px rgba(255, 154, 158, 0.4),
      0 0 0 1px rgba(255, 255, 255, 0.2) inset !important;
}

.auth-btn.success {
  animation: successAnimation 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  position: relative;
  z-index: 2;
}

.btn-icon {
  font-size: 18px;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

.btn-shine {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
      to right,
      rgba(255, 255, 255, 0) 0%,
      rgba(255, 255, 255, 0.3) 50%,
      rgba(255, 255, 255, 0) 100%
  );
  transform: rotate(30deg);
  animation: shine 3s infinite ease-in-out;
}

/* 底部链接 */
.auth-footer {
  margin-top: 30px;
}

.switch-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #666;
  font-size: 15px;
  margin-bottom: 15px;
}

.terms-text {
  color: #888;
  font-size: 13px;
  margin-top: 20px;
}

.link-text {
  opacity: 0.8;
}

.link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #ff9a9e;
  cursor: pointer;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 4px 10px;
  border-radius: 8px;
  background: rgba(255, 154, 158, 0.1);
  border: 1px solid transparent;
}

.link:hover {
  color: #ff7b81;
  transform: translateY(-2px);
  background: rgba(255, 154, 158, 0.15);
  border-color: rgba(255, 154, 158, 0.3);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.2);
}

.link-icon {
  font-size: 14px;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration {
  position: absolute;
  font-size: 70px;
  opacity: 0.07;
  animation: float 10s ease-in-out infinite;
  filter: blur(1px);
}

.pet1 {
  top: 10%;
  left: 8%;
  animation-delay: 0s;
}

.pet2 {
  top: 75%;
  right: 10%;
  animation-delay: 2s;
}

.pet3 {
  bottom: 15%;
  left: 20%;
  animation-delay: 4s;
}

.pet4 {
  top: 25%;
  right: 8%;
  animation-delay: 6s;
}

.floating-pets {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-pet {
  position: absolute;
  font-size: 30px;
  animation: floatAround 15s linear infinite;
  opacity: 0.3;
  filter: blur(0.5px);
}

.fp1 {
  top: 20%;
  left: 5%;
  animation-delay: 0s;
}

.fp2 {
  top: 60%;
  left: 15%;
  animation-delay: 5s;
}

.fp3 {
  top: 40%;
  right: 10%;
  animation-delay: 10s;
}

/* 动画定义 */
@keyframes pulseGlow {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 4px 8px rgba(255, 154, 158, 0.3));
  }
  50% {
    transform: scale(1.08);
    filter: drop-shadow(0 4px 15px rgba(255, 154, 158, 0.5));
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  33% {
    transform: translateY(-20px) rotate(5deg) scale(1.05);
  }
  66% {
    transform: translateY(10px) rotate(-5deg) scale(0.95);
  }
}

@keyframes floatAround {
  0% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(50px, -30px) rotate(90deg); }
  50% { transform: translate(100px, 20px) rotate(180deg); }
  75% { transform: translate(30px, 50px) rotate(270deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulseRing {
  0% { opacity: 0.8; transform: scale(1); }
  100% { opacity: 0; transform: scale(1.1); }
}

@keyframes badgePop {
  0% { transform: scale(0); }
  80% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

@keyframes glowPulse {
  0% { opacity: 0.3; transform: scale(1); }
  100% { opacity: 0.6; transform: scale(1.05); }
}

@keyframes shakeError {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-5px); }
  40%, 80% { transform: translateX(5px); }
}

@keyframes inputError {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-3px); }
  40%, 80% { transform: translateX(3px); }
}

@keyframes successAnimation {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); box-shadow: 0 15px 40px rgba(76, 217, 100, 0.6); }
  100% { transform: scale(1); }
}

@keyframes shine {
  0% { left: -100%; }
  100% { left: 200%; }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 520px) {
  .auth-card {
    width: 95%;
    max-width: 380px;
    padding: 30px 25px;
  }

  .auth-title {
    font-size: 24px;
  }

  .auth-logo {
    font-size: 45px;
  }

  .avatar-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }

  .avatar-frame {
    border-width: 3px;
  }

  .selection-preview {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .preview-info {
    text-align: center;
  }

  .auth-input {
    padding: 14px 45px 14px 16px;
    font-size: 14px;
  }

  .input-icon {
    right: 16px;
    font-size: 16px;
  }

  .auth-btn {
    padding: 16px;
    font-size: 16px;
  }

  .section-header {
    justify-content: center;
  }

  .avatar-selector-section {
    padding: 20px 15px;
  }
}

@media (min-width: 521px) and (max-width: 768px) {
  .auth-card {
    width: 450px;
  }

  .avatar-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .auth-card {
    background: rgba(40, 40, 50, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .auth-title {
    background: linear-gradient(45deg, #ff9a9e, #fad0c4, #a1c4fd);
    -webkit-background-clip: text;
    background-clip: text;
  }

  .auth-input {
    background: rgba(60, 60, 70, 0.8);
    border-color: rgba(255, 255, 255, 0.1);
    color: #e0e0e0;
  }

  .auth-input:focus {
    border-color: #ff9a9e;
    background: rgba(70, 70, 80, 0.9);
  }

  .avatar-selector-section {
    background: linear-gradient(145deg, #2a2a35, #252530);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .avatar-frame {
    background: linear-gradient(45deg, #3a3a45, #2e2e39);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .avatar-name {
    color: #bbb;
  }

  .current-selection {
    background: linear-gradient(135deg, #353540, #2a2a35);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .preview-name {
    background: linear-gradient(45deg, #ff9a9e, #a1c4fd);
    -webkit-background-clip: text;
    background-clip: text;
  }
}
</style>