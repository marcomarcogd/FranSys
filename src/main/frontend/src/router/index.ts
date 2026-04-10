import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { api } from '../api/fransys'
import AdminLayout from '../layouts/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/admin/DashboardView.vue'
import CustomerWorkspaceView from '../views/admin/CustomerWorkspaceView.vue'
import EnterpriseView from '../views/admin/EnterpriseView.vue'
import ProductView from '../views/admin/ProductView.vue'
import PackageView from '../views/admin/PackageView.vue'
import DictView from '../views/admin/DictView.vue'
import SystemView from '../views/admin/SystemView.vue'

const routes = [
  {
    path: '/login',
    component: LoginView,
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        name: 'admin-dashboard',
        path: 'dashboard',
        component: DashboardView,
        meta: {
          title: '工作台',
          subtitle: '查看关键指标、待办客户和最近动态',
          icon: 'DataBoard',
          navGroup: 'workspace',
          roles: ['ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS'],
        },
      },
      {
        name: 'admin-customers',
        path: 'customers',
        component: CustomerWorkspaceView,
        meta: {
          title: '客户管理',
          subtitle: '集中维护客户资料、跟进记录和推荐方案',
          icon: 'UserFilled',
          navGroup: 'workspace',
          roles: ['ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS'],
        },
      },
      { path: 'leads', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'identification', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'assessment', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'match', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'delivery', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'aftersales', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'detail/:id', redirect: (to: any) => `/admin/customers?customerId=${to.params.id}`, meta: { hidden: true } },
      {
        name: 'admin-enterprises',
        path: 'enterprises',
        component: EnterpriseView,
        meta: {
          title: '企业库',
          subtitle: '维护合作企业、联系人和品牌信息',
          icon: 'OfficeBuilding',
          navGroup: 'supply',
          roles: ['ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS'],
        },
      },
      {
        name: 'admin-products',
        path: 'products',
        component: ProductView,
        meta: {
          title: '产品库',
          subtitle: '维护可推荐的单项产品和服务内容',
          icon: 'Goods',
          navGroup: 'supply',
          roles: ['ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS'],
        },
      },
      {
        name: 'admin-packages',
        path: 'packages',
        component: PackageView,
        meta: {
          title: '套餐方案',
          subtitle: '组合跨企业产品，快速生成整套推荐方案',
          icon: 'Box',
          navGroup: 'supply',
          roles: ['ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS'],
        },
      },
      {
        name: 'admin-dicts',
        path: 'dicts',
        component: DictView,
        meta: {
          title: '基础字典',
          subtitle: '维护系统下拉选项和基础枚举',
          icon: 'Collection',
          navGroup: 'system',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        name: 'admin-system',
        path: 'system',
        component: SystemView,
        meta: {
          title: '账号与权限',
          subtitle: '管理内部账号、角色和登录权限',
          icon: 'Setting',
          navGroup: 'system',
          roles: ['ROLE_ADMIN'],
        },
      },
    ],
  },
  {
    path: '/',
    redirect: '/admin/dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  const isPublic = to.path === '/login'
  if (!isPublic && !authStore.token) {
    return '/login'
  }
  if (!isPublic && authStore.token && !authStore.user) {
    try {
      authStore.setUser(await api.me())
    } catch {
      authStore.clearAuth()
      return '/login'
    }
  }
  if (to.path === '/login' && authStore.token) {
    return '/admin/dashboard'
  }
  const allowedRoles = to.meta?.roles as string[] | undefined
  if (!isPublic && allowedRoles?.length && authStore.user && !allowedRoles.includes(authStore.user.roleCode)) {
    return '/admin/dashboard'
  }
  return true
})

export default router
