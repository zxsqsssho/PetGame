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

        <!-- 头像上传区域 -->
        <div class="avatar-section">
          <label class="avatar-label">选择头像</label>
          <div class="avatar-upload-area">
            <!-- 预览区域 -->
            <div
                class="avatar-preview"
                :style="{
                backgroundImage: previewImage ? `url(${previewImage})` : 'none',
                backgroundColor: !previewImage ? '#f5f5f5' : 'transparent'
              }"
                @click="triggerFileInput"
            >
              <div v-if="!previewImage && !selectedDefaultAvatar" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传</span>
              </div>
              <div v-else-if="selectedDefaultAvatar && !previewImage" class="default-avatar-preview">
                <span class="default-avatar-emoji">{{ getDefaultAvatarEmoji(selectedDefaultAvatar) }}</span>
              </div>
              <div v-if="previewImage" class="upload-overlay">
                <span class="change-text">更换图片</span>
              </div>
            </div>

            <!-- 隐藏的file input -->
            <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept="image/jpeg,image/png,image/gif"
                class="file-input"
            />

            <!-- 上传按钮 -->
            <div class="upload-controls">
              <button class="upload-btn" @click="triggerFileInput" type="button">
                <span class="btn-icon">📁</span>
                <span>选择图片</span>
              </button>
              <button
                  v-if="previewImage"
                  class="remove-btn"
                  @click="removeAvatar"
                  type="button"
              >
                <span class="btn-icon">🗑️</span>
                <span>移除</span>
              </button>
            </div>

            <!-- 提示信息 -->
            <div class="upload-hint">
              <p>支持 JPG、PNG、GIF 格式，最大 2MB</p>
              <p>建议尺寸：200×200像素</p>
            </div>
          </div>

          <div v-if="errors.avatar" class="error-message">{{ errors.avatar }}</div>

          <!-- 备选头像 -->
          <div class="alternative-avatars">
            <p class="alternative-label">或选择默认头像：</p>
            <div class="avatar-grid">
              <div
                  v-for="avatar in defaultAvatars"
                  :key="avatar.id"
                  class="avatar-item"
                  :class="{
                  selected: selectedDefaultAvatar === avatar.id,
                  'has-image': previewImage && selectedDefaultAvatar === avatar.id
                }"
                  @click="selectDefaultAvatar(avatar.id)"
              >
                <span class="avatar-emoji">{{ avatar.emoji }}</span>
                <span class="avatar-name">{{ avatar.name }}</span>
              </div>
            </div>
          </div>
        </div>

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

        <button class="auth-btn" @click="register" :disabled="loading">
          <span v-if="loading">注册中...</span>
          <span v-else>注册账号</span>
        </button>

        <div class="auth-footer">
          <p class="switch-text">
            已有账号？
            <span class="link" @click="goLogin">立即登录</span>
          </p>
          <p class="terms-text">
            注册即表示同意
            <span class="link" @click="showTerms">用户协议</span>
          </p>
        </div>
      </div>
    </div>

    <div class="bg-decoration">
      <div class="decoration pet1">🐱</div>
      <div class="decoration pet2">🐶</div>
      <div class="decoration pet3">🐟</div>
      <div class="decoration pet4">🐦</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";

const router = useRouter()
const fileInput = ref(null)

const formData = reactive({
  account: "",
  name: "",
  password: "",
})

const confirmPassword = ref("")
const loading = ref(false)
const previewImage = ref("") // 图片预览URL
const selectedDefaultAvatar = ref("") // 选中的默认头像ID
const avatarFile = ref(null) // 上传的文件对象
const errors = reactive({
  account: "",
  name: "",
  password: "",
  confirmPassword: "",
  avatar: ""
})

// 默认头像选项
const defaultAvatars = [
  { id: 'cat', emoji: '🐱', name: '小猫' },
  { id: 'dog', emoji: '🐶', name: '小狗' },
  { id: 'rabbit', emoji: '🐰', name: '兔子' },
  { id: 'bird', emoji: '🐦', name: '小鸟' },
  { id: 'fish', emoji: '🐟', name: '小鱼' },
  { id: 'fox', emoji: '🦊', name: '狐狸' }
]

// 根据ID获取默认头像的emoji
const getDefaultAvatarEmoji = (avatarId) => {
  const avatar = defaultAvatars.find(a => a.id === avatarId)
  return avatar ? avatar.emoji : '👤'
}

// 清除错误信息
const clearErrors = () => {
  errors.account = ""
  errors.name = ""
  errors.password = ""
  errors.confirmPassword = ""
  errors.avatar = ""
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click()
}

// 处理文件上传
const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif']
  if (!validTypes.includes(file.type)) {
    alert('请选择 JPG、PNG 或 GIF 格式的图片')
    return
  }

  // 检查文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过2MB')
    return
  }

  // 创建预览URL
  previewImage.value = URL.createObjectURL(file)
  avatarFile.value = file
  selectedDefaultAvatar.value = "" // 取消默认头像选择
  errors.avatar = "" // 清除头像错误

  console.log('选择的文件:', file.name, file.size, file.type)
}

// 选择默认头像
const selectDefaultAvatar = (avatarId) => {
  // 清除已上传的文件
  if (previewImage.value) {
    URL.revokeObjectURL(previewImage.value)
  }
  previewImage.value = ""
  avatarFile.value = null
  selectedDefaultAvatar.value = avatarId
  errors.avatar = "" // 清除头像错误

  // 重置文件输入
  if (fileInput.value) {
    fileInput.value.value = ""
  }

  console.log('选择了默认头像:', avatarId)
}

// 移除头像
const removeAvatar = () => {
  if (previewImage.value) {
    URL.revokeObjectURL(previewImage.value)
  }
  previewImage.value = ""
  avatarFile.value = null
  selectedDefaultAvatar.value = ""
  errors.avatar = "请上传头像或选择默认头像" // 设置错误信息

  if (fileInput.value) {
    fileInput.value.value = ""
  }
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
  if (!previewImage.value && !selectedDefaultAvatar.value) {
    errors.avatar = "请上传头像或选择默认头像"
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

// 将图片转换为Base64格式（备用功能，当前版本使用默认头像）
const convertImageToBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => resolve(e.target.result)
    reader.onerror = (e) => reject(e)
    reader.readAsDataURL(file)
  })
}

// 注册函数
// 修改Register.vue中的register函数
const register = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    // 准备注册数据 - 使用JSON格式
    const registerData = {
      username: formData.account,
      name: formData.name,
      password: formData.password,
      avatar: selectedDefaultAvatar.value || "default"
    }

    console.log('准备发送的JSON数据:', registerData)
    console.log('请求URL: http://localhost:8080/pet_game/api/user/register')

    // 尝试多个可能的URL
    const urls = [
      'http://localhost:8080/pet_game/api/user/register',
      'http://localhost:8080/api/user/register',
      'http://127.0.0.1:8080/pet_game/api/user/register'
    ]

    let response = null
    let lastError = null

    for (const url of urls) {
      try {
        console.log('尝试请求:', url)
        response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=UTF-8',
          },
          body: JSON.stringify(registerData)
        })

        console.log('响应状态:', response.status, response.statusText)

        if (response.ok) {
          const res = await response.json()
          console.log('注册响应:', res)

          if (res.code === 0) {
            alert("注册成功！")
            router.push("/login")
            return
          } else {
            alert(res.msg || "注册失败")
            return
          }
        } else {
          lastError = `HTTP ${response.status}: ${response.statusText}`
          console.warn('请求失败:', url, response.status)
        }
      } catch (err) {
        lastError = err.message
        console.warn('请求异常:', url, err.message)
      }
    }

    // 所有URL都失败，显示详细错误
    console.error('所有接口尝试都失败:', lastError)
    showConnectionError()

  } catch (error) {
    console.error("注册错误:", error)
    console.error("错误详情:", error.message)
    showConnectionError()
  } finally {
    loading.value = false
  }
}

// 新增：显示连接错误的函数
const showConnectionError = () => {
  alert(`连接服务器失败，请检查：

1. 🔧 后端项目是否部署
   - 访问 http://localhost:8080/ 确认Tomcat运行
   - 访问 http://localhost:8080/pet_game/ 确认项目部署

2. 📁 项目结构是否正确
   - 检查 tomcat/webapps/ 下是否有 pet_game 文件夹
   - 确认 Servlet 类已编译

3. ⚙️ 接口路径问题
   - 检查 web.xml 或 @WebServlet 注解配置
   - 确认路径是 /api/user/register

4. 🔄 重启Tomcat
   - 停止并重启Tomcat服务
   - 清理浏览器缓存

5. 🔍 查看Tomcat日志
   - 检查 tomcat/logs/catalina.out
   - 查看是否有部署或编译错误

如果问题持续，请：
- 使用浏览器开发者工具查看Network面板
- 检查控制台是否有CORS错误
- 确保前后端端口一致`)
}

const goLogin = () => {
  router.push("/login")
}

const showTerms = () => {
  alert("用户协议页面（待实现）")
}

// 组件卸载时清理URL
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (previewImage.value) {
    URL.revokeObjectURL(previewImage.value)
  }
})
</script>

<style scoped>
/* 原有样式保持不变，添加错误提示样式 */
.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.auth-card {
  width: 480px;
  max-width: 90vw;
  background: white;
  padding: 35px 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 15px 50px rgba(0,0,0,0.15);
  position: relative;
  z-index: 10;
  animation: slideUp 0.6s ease-out;
}

.auth-header {
  margin-bottom: 25px;
}

.auth-logo {
  font-size: 45px;
  margin-bottom: 12px;
  animation: pulse 2s infinite;
}

.auth-title {
  font-size: 26px;
  font-weight: 800;
  margin-bottom: 6px;
  color: #333;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.auth-subtitle {
  color: #777;
  font-size: 13px;
  margin-bottom: 8px;
}

.form-container {
  width: 100%;
}

.input-group {
  position: relative;
  margin-bottom: 18px;
  width: 100%;
}

.auth-input {
  width: 100%;
  padding: 14px 45px 14px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: #fafafa;
  box-sizing: border-box;
}

.auth-input:focus {
  outline: none;
  border-color: #ff9a9e;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 154, 158, 0.1);
}

.auth-input.error {
  border-color: #ff6b6b;
}

.auth-input::placeholder {
  color: #aaa;
}

.input-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  font-size: 17px;
  pointer-events: none;
}

/* 错误提示样式 */
.error-message {
  color: #ff6b6b;
  font-size: 12px;
  margin-top: 5px;
  text-align: left;
  padding-left: 5px;
  animation: fadeIn 0.3s ease;
}

/* 头像上传区域 */
.avatar-section {
  margin: 20px 0;
}

.avatar-label {
  display: block;
  margin-bottom: 15px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
  text-align: left;
}

.avatar-upload-area {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 2px dashed #dee2e6;
  transition: border-color 0.3s ease;
}

.avatar-upload-area:hover {
  border-color: #ff9a9e;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-preview:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
}

.upload-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
}

.default-avatar-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.default-avatar-emoji {
  font-size: 50px;
  animation: bounce 2s infinite;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-preview:hover .upload-overlay {
  opacity: 1;
}

.change-text {
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.file-input {
  display: none;
}

.upload-controls {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 15px;
}

.upload-btn, .remove-btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.upload-btn {
  background: #4ecdc4;
  color: white;
}

.upload-btn:hover {
  background: #3bb4ac;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.remove-btn {
  background: #ff6b6b;
  color: white;
}

.remove-btn:hover {
  background: #ff5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.btn-icon {
  font-size: 16px;
}

.upload-hint {
  color: #6c757d;
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
}

.upload-hint p {
  margin: 4px 0;
}

/* 备选头像 */
.alternative-avatars {
  margin-top: 20px;
}

.alternative-label {
  text-align: left;
  color: #666;
  font-size: 13px;
  margin-bottom: 12px;
  font-weight: 500;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.avatar-item {
  padding: 10px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  background: #fafafa;
  box-sizing: border-box;
}

.avatar-item:hover {
  border-color: #ff9a9e;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.2);
}

.avatar-item.selected {
  border-color: #ff9a9e;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4);
  color: white;
}

.avatar-emoji {
  font-size: 22px;
  display: block;
  margin-bottom: 4px;
}

.avatar-name {
  font-size: 11px;
  font-weight: 500;
}

.auth-btn {
  width: 100%;
  padding: 16px;
  margin-top: 20px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4);
  border-radius: 12px;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}

.auth-btn:active:not(:disabled) {
  transform: translateY(0);
}

.auth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3) !important;
}

.auth-footer {
  margin-top: 25px;
}

.switch-text {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.terms-text {
  color: #999;
  font-size: 12px;
  margin-top: 15px;
}

.link {
  color: #ff9a9e;
  cursor: pointer;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.3s;
}

.link:hover {
  color: #ff7b81;
  text-decoration: underline;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration {
  position: absolute;
  font-size: 55px;
  opacity: 0.08;
  animation: float 8s ease-in-out infinite;
}

.pet1 {
  top: 5%;
  left: 5%;
  animation-delay: 0s;
}

.pet2 {
  top: 70%;
  right: 8%;
  animation-delay: 2s;
}

.pet3 {
  bottom: 10%;
  left: 15%;
  animation-delay: 4s;
}

.pet4 {
  top: 20%;
  right: 5%;
  animation-delay: 6s;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
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
    transform: translateY(-20px) rotate(10deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
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
    padding: 25px 20px;
  }

  .avatar-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .auth-title {
    font-size: 22px;
  }

  .auth-logo {
    font-size: 40px;
  }

  .auth-input {
    padding: 13px 40px 13px 13px;
    font-size: 14px;
  }

  .input-icon {
    right: 14px;
    font-size: 16px;
  }

  .auth-btn {
    padding: 15px;
    font-size: 15px;
  }

  .avatar-preview {
    width: 100px;
    height: 100px;
  }

  .upload-controls {
    flex-direction: column;
    gap: 8px;
  }

  .upload-btn, .remove-btn {
    width: 100%;
    justify-content: center;
  }

  .avatar-emoji {
    font-size: 20px;
  }

  .avatar-name {
    font-size: 10px;
  }

  .default-avatar-emoji {
    font-size: 40px;
  }
}

@media (max-width: 380px) {
  .avatar-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .avatar-item {
    padding: 8px;
  }
}
</style>