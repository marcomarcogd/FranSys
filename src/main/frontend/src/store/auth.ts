import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('fransys-token') || '')
  const user = ref<any>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(value: string) {
    token.value = value
    localStorage.setItem('fransys-token', value)
  }

  function setUser(value: any) {
    user.value = value
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    localStorage.removeItem('fransys-token')
  }

  return {
    token,
    user,
    isLoggedIn,
    setToken,
    setUser,
    clearAuth,
  }
})
