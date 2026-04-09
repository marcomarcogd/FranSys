export const customerLevelOptions = [
  {
    value: 'A',
    label: 'A',
    description: '已报价/近期有明确计划，建议每 3 天跟进 1 次',
    hint: 'A级客户建议每 3 天至少跟进 1 次',
  },
  {
    value: 'B',
    label: 'B',
    description: '需求较明确/正在比较，建议每周跟进 1 次',
    hint: 'B级客户建议每周至少跟进 1 次',
  },
  {
    value: 'C',
    label: 'C',
    description: '已留资但互动弱，建议每 2 周跟进 1 次',
    hint: 'C级客户建议每 2 周至少跟进 1 次',
  },
  {
    value: 'D',
    label: 'D',
    description: '长期无响应或失效，建议季度触达或归档',
    hint: 'D级客户建议季度触达或归档',
  },
]

export function customerLevelLabel(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.label}级：${option.description}` : value || '未分级'
}

export function customerLevelHint(value?: string) {
  return customerLevelOptions.find((item) => item.value === value)?.hint || '请先为客户设置 A / B / C / D 等级'
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
