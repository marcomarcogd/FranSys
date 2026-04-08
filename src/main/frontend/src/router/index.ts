import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'
import AdminLayout from '../layouts/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/admin/DashboardView.vue'
import LeadBoardView from '../views/admin/LeadBoardView.vue'
import LeadDetailView from '../views/admin/LeadDetailView.vue'
import EnterpriseView from '../views/admin/EnterpriseView.vue'
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
      { name: 'admin-leads', path: 'leads', component: LeadBoardView, meta: { title: '客户主线台账', icon: 'Tickets', focusTab: 'identification', workflowStages: [] } },
      { name: 'admin-identification', path: 'identification', component: LeadBoardView, meta: { title: '客户识别与分级', icon: 'User', focusTab: 'identification', workflowStages: ['NEW', 'IDENTIFIED'] } },
      { name: 'admin-assessment', path: 'assessment', component: LeadBoardView, meta: { title: '需求评估', icon: 'DocumentChecked', focusTab: 'assessment', workflowStages: ['ASSESSED'] } },
      { name: 'admin-match', path: 'match', component: LeadBoardView, meta: { title: '企业匹配与推荐', icon: 'Connection', focusTab: 'match', workflowStages: ['MATCHED'] } },
      { name: 'admin-delivery', path: 'delivery', component: LeadBoardView, meta: { title: '交付监督与回访', icon: 'Checked', focusTab: 'delivery', workflowStages: ['DELIVERING', 'DELIVERED'] } },
      { name: 'admin-aftersales', path: 'aftersales', component: LeadBoardView, meta: { title: '售后复购跟进', icon: 'RefreshRight', focusTab: 'aftersales', workflowStages: ['AFTER_SALES'] } },
      { name: 'admin-detail', path: 'detail/:id', component: LeadDetailView, meta: { title: '客户详情', hidden: true } },
      { name: 'admin-enterprises', path: 'enterprises', component: EnterpriseView, meta: { title: '企业库', icon: 'OfficeBuilding' } },
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
