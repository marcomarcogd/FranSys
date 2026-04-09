<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="brand">
        <div class="brand-mark">Fran</div>
        <div>
          <div class="brand-title">FranSys</div>
          <div class="brand-subtitle">内部运营系统</div>
        </div>
      </div>
      <el-menu :default-active="activeMenuPath" class="sidebar-menu" router>
        <el-menu-item v-for="item in menuRoutes" :key="item.name || item.path" :index="resolveMenuPath(item.path)">
          <el-icon><component :is="item.meta?.icon || 'Menu'" /></el-icon>
          <span>{{ item.meta?.title }}</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <div>
          <div class="header-title">{{ route.meta?.title || 'FranSys' }}</div>
          <div class="header-subtitle">客户跟进、企业供给、产品套餐与推荐一体化管理</div>
        </div>
        <div class="header-actions">
          <el-tag type="success">{{ authStore.user?.displayName || '内部账号' }}</el-tag>
          <el-button text @click="logout">退出登录</el-button>
        </div>
      </header>
      <main class="admin-content">
        <RouterView />
      </main>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api/fransys'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const menuRoutes = computed(() => {
  const adminRoute = router.getRoutes().find((item) => item.path === '/admin')
  return (adminRoute?.children || []).filter((item) => !item.meta?.hidden)
})

const activeMenuPath = computed(() => {
  if (
    route.path.startsWith('/admin/detail/')
    || route.path.startsWith('/admin/leads')
    || route.path.startsWith('/admin/identification')
    || route.path.startsWith('/admin/assessment')
    || route.path.startsWith('/admin/match')
    || route.path.startsWith('/admin/delivery')
    || route.path.startsWith('/admin/aftersales')
  ) {
    return '/admin/customers'
  }
  return route.path
})

function resolveMenuPath(path: string) {
  return path.startsWith('/') ? path : `/admin/${path}`
}

async function fetchMe() {
  if (!authStore.user && authStore.token) {
    authStore.setUser(await api.me())
  }
}

function logout() {
  authStore.clearAuth()
  router.push('/login')
}

onMounted(fetchMe)
</script>
