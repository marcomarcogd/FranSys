<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="hero-card">
        <div class="eyebrow">FranSys</div>
        <h1>FranSys</h1>
        <p>内部管理端负责筛选、评估、匹配、监督和复购；公开表单只开放客户可填写部分。</p>
      </div>
      <el-card class="form-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>内部登录</span>
            <el-tag>admin / sales / ops</el-tag>
          </div>
        </template>
        <div class="public-summary">
          <div>请输入内部账号和密码登录管理端。</div>
          <div>登录失效后系统会自动跳回登录页。</div>
        </div>
        <el-form :model="form" label-position="top" @submit.prevent="submit">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号，例如 admin" clearable />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" clearable />
          </el-form-item>
          <el-button type="primary" class="full-width" @click="submit">登录管理端</el-button>
          <div class="login-tip">默认账号密码：`admin / Admin@123`、`sales / Sales@123`、`ops / Ops@123`</div>
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
  username: 'admin',
  password: 'Admin@123',
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
