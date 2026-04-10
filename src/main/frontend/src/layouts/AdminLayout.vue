<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="brand">
        <div class="brand-mark">Fran</div>
        <div>
          <div class="brand-title">FranSys</div>
          <div class="brand-subtitle">客户与供给协同平台</div>
        </div>
      </div>
      <el-menu :default-active="activeMenuPath" class="sidebar-menu" router>
        <el-menu-item-group v-for="group in groupedMenuRoutes" :key="group.key">
          <template #title>
            <span class="menu-group-title">{{ group.label }}</span>
          </template>
          <el-menu-item
            v-for="item in group.items"
            :key="item.name || item.path"
            :index="resolveMenuPath(item.path)"
          >
            <el-icon><component :is="item.meta?.icon || 'Menu'" /></el-icon>
            <span>{{ item.meta?.title }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-menu>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <div>
          <div class="header-title">{{ route.meta?.title || 'FranSys' }}</div>
          <div class="header-subtitle">{{ route.meta?.subtitle || '集中处理客户、供给和内部协作' }}</div>
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
const customerAliasPaths = new Set([
  '/admin/leads',
  '/admin/identification',
  '/admin/assessment',
  '/admin/match',
  '/admin/delivery',
  '/admin/aftersales',
])
const navGroups = [
  { key: 'workspace', label: '工作台' },
  { key: 'supply', label: '供给中心' },
  { key: 'system', label: '系统设置' },
]

const menuRoutes = computed(() => {
  const adminRoute = router.getRoutes().find((item) => item.path === '/admin')
  return (adminRoute?.children || []).filter((item) => {
    if (item.meta?.hidden) {
      return false
    }
    if (!item.meta?.title) {
      return false
    }
    return !item.redirect
  })
})

const groupedMenuRoutes = computed(() =>
  navGroups
    .map((group) => ({
      ...group,
      items: menuRoutes.value.filter((item) => item.meta?.navGroup === group.key),
    }))
    .filter((group) => group.items.length),
)

const activeMenuPath = computed(() => {
  if (route.path.startsWith('/admin/detail/') || customerAliasPaths.has(route.path)) {
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
