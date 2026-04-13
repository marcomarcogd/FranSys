import http from './http'

const wrap = <T = any>(promise: any) => promise as Promise<T>

export const api = {
  login: (payload: { username: string; password: string }) => wrap(http.post('/api/auth/login', payload)),
  me: () => wrap(http.get('/api/auth/me')),
  dashboard: () => wrap(http.get('/api/dashboard/overview')),
  dictsGrouped: () => wrap(http.get('/api/dicts/grouped')),
  dicts: () => wrap(http.get('/api/dicts')),
  saveDict: (payload: any) => wrap(http.post('/api/dicts', payload)),
  enterprises: () => wrap(http.get('/api/enterprises')),
  saveEnterprise: (payload: any) => wrap(http.post('/api/enterprises', payload)),
  customers: (params: any) => wrap(http.get('/api/customers', { params })),
  customerDetail: (id: number | string) => wrap(http.get(`/api/customers/${id}`)),
  createCustomer: (payload: any) => wrap(http.post('/api/customers', payload)),
  updateCustomer: (id: number | string, payload: any) => wrap(http.put(`/api/customers/${id}`, payload)),
  batchAssignCustomers: (payload: any) => wrap(http.post('/api/customers/batch/assign', payload)),
  batchArchiveCustomers: (payload: any) => wrap(http.post('/api/customers/batch/archive', payload)),
  saveFollowRecord: (id: number | string, payload: any) => wrap(http.post(`/api/customers/${id}/follow-records`, payload)),
  saveRecommendation: (id: number | string, payload: any) => wrap(http.post(`/api/customers/${id}/recommendations`, payload)),
  products: (params?: any) => wrap(http.get('/api/products', { params })),
  saveProduct: (payload: any) => wrap(http.post('/api/products', payload)),
  productPackages: (params?: any) => wrap(http.get('/api/product-packages', { params })),
  saveProductPackage: (payload: any) => wrap(http.post('/api/product-packages', payload)),
  uploadProductImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return wrap(http.post('/api/files/upload/product-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }))
  },
  systemMeta: () => wrap(http.get('/api/system/meta')),
  assignableUsers: () => wrap(http.get('/api/system/assignable-users')),
  saveUser: (payload: any) => wrap(http.post('/api/system/users', payload)),
}
