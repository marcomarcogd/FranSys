<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="hero-card">
        <div class="eyebrow">FranSys Workspace</div>
        <h1>FranSys</h1>
        <p>面向内部团队的客户与供给协同平台，帮助你更高效地管理客户、产品与推荐方案。</p>
      </div>
      <el-card class="form-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>账号登录</span>
          </div>
        </template>
        <el-form :model="form" label-position="top" @submit.prevent="submit">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入登录账号" clearable />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" clearable />
          </el-form-item>
          <el-button type="primary" class="full-width" @click="submit">登录管理端</el-button>
          <div class="login-tip">如账号无法登录，请联系管理员处理。</div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../api/fransys'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const authStore = useAuthStore()
const form = reactive({
  username: '',
  password: '',
})

async function submit() {
  if (!form.username.trim()) {
    ElMessage.warning('请输入登录账号')
    return
  }
  if (!form.password.trim()) {
    ElMessage.warning('请输入登录密码')
    return
  }
  const result: any = await api.login(form)
  authStore.setToken(result.token)
  authStore.setUser(result)
  ElMessage.success('登录成功')
  router.push('/admin/dashboard')
}
</script>
