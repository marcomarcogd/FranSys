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
  systemMeta: () => wrap(http.get('/api/system/meta')),
  saveUser: (payload: any) => wrap(http.post('/api/system/users', payload)),
  leads: (params: any) => wrap(http.get('/api/leads', { params })),
  leadDetail: (id: number | string) => wrap(http.get(`/api/leads/${id}`)),
  createInternalLead: (payload: any) => wrap(http.post('/api/leads', payload)),
  saveIdentification: (id: number | string, payload: any) => wrap(http.put(`/api/leads/${id}/identification`, payload)),
  saveAssessment: (id: number | string, payload: any) => wrap(http.put(`/api/assessments/${id}`, payload)),
  saveMatch: (id: number | string, payload: any) => wrap(http.put(`/api/matches/${id}`, payload)),
  saveDelivery: (id: number | string, payload: any) => wrap(http.put(`/api/deliveries/${id}`, payload)),
  createFeedbackToken: (id: number | string) => wrap(http.post(`/api/deliveries/${id}/feedback-token`)),
  saveAfterSales: (id: number | string, payload: any) => wrap(http.put(`/api/aftersales/${id}`, payload)),
  submitPublicLead: (payload: any) => wrap(http.post('/api/public/leads', payload)),
  feedbackContext: (token: string) => wrap(http.get(`/api/public/feedback/${token}`)),
  submitFeedback: (token: string, payload: any) => wrap(http.post(`/api/public/feedback/${token}`, payload)),
}
