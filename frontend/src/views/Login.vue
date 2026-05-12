<template>
  <div class="login-page">
    <!-- 左侧品牌展示区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-mark">美</div>
          <h1 class="brand-title">美容沙龙管理系统</h1>
        </div>
        <p class="brand-slogan">专业 · 优雅 · 高效</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon class="feature-icon"><Calendar /></el-icon>
            <span>智能预约管理</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><User /></el-icon>
            <span>客户关系维护</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><TrendCharts /></el-icon>
            <span>数据驱动决策</span>
          </div>
        </div>
      </div>
      <!-- 装饰圆 -->
      <div class="deco-circle c1" />
      <div class="deco-circle c2" />
      <div class="deco-circle c3" />
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-form-panel">
      <div class="form-wrapper">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">请登录您的管理账户</p>

        <el-form :model="loginForm" class="login-form" @submit.prevent="handleLogin">
          <el-form-item>
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              style="width: 100%; height: 48px; font-size: 16px; font-weight: 600; border-radius: 12px;"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>忘记密码？</span>
          <a href="#">联系管理员</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'
import { login } from '@/api/sys/user'
import { User, Lock, Calendar, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(loginForm.username, loginForm.password)
    if (data.token) {
      localStorage.setItem('token', data.token)
      localStorage.setItem('refreshToken', data.refreshToken || '')
      Cookies.set('beauty_salon_token', data.token, { path: '/' })
    }
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

/* 左侧品牌区 */
.login-brand {
  width: 55%;
  background: linear-gradient(135deg, #e85d75 0%, #c94a61 50%, #a83d52 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 2;
  color: white;
  text-align: center;
  padding: 40px;
}

.brand-logo {
  margin-bottom: 24px;
}

.logo-mark {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  margin: 0 auto 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 2px;
}

.brand-slogan {
  font-size: 18px;
  opacity: 0.85;
  margin: 12px 0 48px;
  letter-spacing: 8px;
}

.brand-features {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-icon {
  font-size: 28px;
  background: rgba(255, 255, 255, 0.12);
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

/* 装饰圆 */
.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.deco-circle.c1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.deco-circle.c2 {
  width: 300px;
  height: 300px;
  bottom: 80px;
  left: -80px;
}

.deco-circle.c3 {
  width: 180px;
  height: 180px;
  bottom: -40px;
  right: 120px;
}

/* 右侧表单区 */
.login-form-panel {
  width: 45%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 15px;
  color: #9ca3af;
  margin: 0 0 32px 0;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  padding: 4px 16px;
  height: 48px;
}

.login-form :deep(.el-input__inner) {
  font-size: 15px;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
}

.login-footer a {
  color: #e85d75;
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.login-footer a:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 900px) {
  .login-brand {
    display: none;
  }
  .login-form-panel {
    width: 100%;
  }
}
</style>
