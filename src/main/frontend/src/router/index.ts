import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'
import AdminLayout from '../layouts/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/admin/DashboardView.vue'
import CustomerWorkspaceView from '../views/admin/CustomerWorkspaceView.vue'
import EnterpriseView from '../views/admin/EnterpriseView.vue'
import ProductView from '../views/admin/ProductView.vue'
import PackageView from '../views/admin/PackageView.vue'
import DictView from '../views/admin/DictView.vue'
import SystemView from '../views/admin/SystemView.vue'
import PublicLeadView from '../views/public/PublicLeadView.vue'
import PublicFeedbackView from '../views/public/PublicFeedbackView.vue'

const routes = [
  {
    path: '/login',
    component: LoginView,
  },
  {
    path: '/public/lead',
    component: PublicLeadView,
  },
  {
    path: '/public/feedback/:token',
    component: PublicFeedbackView,
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      { name: 'admin-dashboard', path: 'dashboard', component: DashboardView, meta: { title: '仪表盘', icon: 'DataBoard' } },
      { name: 'admin-customers', path: 'customers', component: CustomerWorkspaceView, meta: { title: '客户管理', icon: 'UserFilled' } },
      { path: 'leads', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'identification', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'assessment', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'match', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'delivery', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'aftersales', redirect: '/admin/customers', meta: { hidden: true } },
      { path: 'detail/:id', redirect: (to: any) => `/admin/customers?customerId=${to.params.id}`, meta: { hidden: true } },
      { name: 'admin-enterprises', path: 'enterprises', component: EnterpriseView, meta: { title: '企业库', icon: 'OfficeBuilding' } },
      { name: 'admin-products', path: 'products', component: ProductView, meta: { title: '产品库', icon: 'Goods' } },
      { name: 'admin-packages', path: 'packages', component: PackageView, meta: { title: '产品套餐包', icon: 'Box' } },
      { name: 'admin-dicts', path: 'dicts', component: DictView, meta: { title: '字典配置', icon: 'Collection' } },
      { name: 'admin-system', path: 'system', component: SystemView, meta: { title: '用户与角色', icon: 'Setting' } },
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
  const isPublic = to.path.startsWith('/public') || to.path === '/login'
  if (!isPublic && !authStore.token) {
    return '/login'
  }
  if (to.path === '/login' && authStore.token) {
    return '/admin/dashboard'
  }
  return true
})

export default router
