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
        <el-form :model="form" label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button type="primary" class="full-width" @click="submit">登录管理端</el-button>
          <div class="login-tip">默认密码：`Admin@123` / `Sales@123` / `Ops@123`</div>
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
  const result: any = await api.login(form)
  authStore.setToken(result.token)
  authStore.setUser(result)
  ElMessage.success('登录成功')
  router.push('/admin/dashboard')
}
</script>
