export const customerLevelOptions = [
  {
    value: 'A',
    shortLabel: 'A级',
    meaning: '近期成交意向高',
    followAdvice: '建议每 3 天跟进 1 次',
  },
  {
    value: 'B',
    shortLabel: 'B级',
    meaning: '需求明确，正在比较',
    followAdvice: '建议每周跟进 1 次',
  },
  {
    value: 'C',
    shortLabel: 'C级',
    meaning: '已留资，互动较弱',
    followAdvice: '建议每 2 周跟进 1 次',
  },
  {
    value: 'D',
    shortLabel: 'D级',
    meaning: '长期未响应或已失效',
    followAdvice: '建议季度触达或归档',
  },
]

export function customerLevelLabel(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel} · ${option.meaning}` : value || '未分级'
}

export function customerLevelOptionLabel(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel} · ${option.meaning}，${option.followAdvice}` : value || '未分级'
}

export function customerLevelShortLabel(value?: string) {
  return customerLevelOptions.find((item) => item.value === value)?.shortLabel || value || '未分级'
}

export function customerLevelHint(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel} · ${option.followAdvice}` : '请先设置客户等级并安排后续跟进'
}

export function recommendationTypeLabel(value?: string) {
  if (value === 'PRODUCT') {
    return '产品'
  }
  if (value === 'PACKAGE') {
    return '套餐方案'
  }
  return value || '未设置'
}

export function isBlank(value: unknown) {
  return value === null || value === undefined || String(value).trim() === ''
}

export function isValidPhone(value?: string) {
  if (isBlank(value)) return false
  return /^(\+?\d[\d -]{5,}\d)$/.test(String(value).trim())
}

export function isValidEmail(value?: string) {
  if (isBlank(value)) return true
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value).trim())
}

export function toFriendlyErrorMessage(message?: string) {
  if (!message) {
    return '请求失败，请稍后重试'
  }
  if (message.includes('Network Error')) {
    return '网络连接异常，请检查网络后重试'
  }
  if (message.includes('timeout')) {
    return '请求超时，请稍后重试'
  }
  if (message.includes('Failed to fetch')) {
    return '服务暂时不可用，请稍后重试'
  }
  if (message.includes('无权限')) {
    return '当前账号无权执行该操作'
  }
  if (message.includes('登录已失效')) {
    return '登录已失效，请重新登录'
  }
  if (message.startsWith('系统异常:')) {
    return '系统开小差了，请稍后再试'
  }
  return message
}
