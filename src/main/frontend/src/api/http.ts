import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'

const http = axios.create({
  baseURL: '/',
  timeout: 15000,
})

function clearAuthAndRedirect() {
  const authStore = useAuthStore()
  authStore.clearAuth()
  if (location.pathname.startsWith('/admin')) {
    location.href = '/login'
  }
}

function isAuthFailureMessage(message?: string) {
  if (!message) {
    return false
  }
  return ['登录', '认证', '凭证', 'token', '未授权', '无权限'].some((keyword) => message.toLowerCase().includes(keyword.toLowerCase()))
}

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && payload.code === 0) {
      return payload.data
    }
    const message = payload?.message || '请求失败'
    if (isAuthFailureMessage(message)) {
      clearAuthAndRedirect()
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络异常'
    const authStore = useAuthStore()
    const hasToken = !!authStore.token
    if (error.response?.status === 401 || (error.response?.status === 403 && hasToken && location.pathname.startsWith('/admin'))) {
      clearAuthAndRedirect()
    }
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default http
