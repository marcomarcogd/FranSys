export const customerLevelOptions = [
  {
    value: 'A',
    shortLabel: 'A级',
    meaning: '高意向',
    followAdvice: '建议每3天1次',
  },
  {
    value: 'B',
    shortLabel: 'B级',
    meaning: '潜力',
    followAdvice: '建议每周1次',
  },
  {
    value: 'C',
    shortLabel: 'C级',
    meaning: '普通',
    followAdvice: '建议每2周1次',
  },
  {
    value: 'D',
    shortLabel: 'D级',
    meaning: '沉默',
    followAdvice: '建议季度触达或归档',
  },
]

export const intentLevelGuideRows = [
  {
    value: 'A',
    shortLabel: 'A级',
    criteria: '已报价/方案审核中/近期有明确采购计划',
    followFrequency: '每3天1次',
    tagType: 'danger',
  },
  {
    value: 'B',
    shortLabel: 'B级',
    criteria: '需求匹配但未报价/正在对比竞品',
    followFrequency: '每周1次',
    tagType: 'warning',
  },
  {
    value: 'C',
    shortLabel: 'C级',
    criteria: '仅留资未互动/行业相关但无迫切需求',
    followFrequency: '每2周1次',
    tagType: 'info',
  },
  {
    value: 'D',
    shortLabel: 'D级',
    criteria: '超3个月无响应/联系方式无效',
    followFrequency: '季度触达或归档',
    tagType: '',
  },
] as const

export const customerValueLevelOptions = [
  {
    value: 'A',
    shortLabel: 'A级',
    meaning: '私定',
  },
  {
    value: 'B',
    shortLabel: 'B级',
    meaning: '尊享',
  },
  {
    value: 'C',
    shortLabel: 'C级',
    meaning: '基础',
  },
  {
    value: 'D',
    shortLabel: 'D级',
    meaning: '会员',
  },
]

export function customerLevelLabel(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning}` : value || '未分级'
}

export function customerLevelOptionLabel(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning}，${option.followAdvice}` : value || '未分级'
}

export function customerLevelShortLabel(value?: string) {
  return customerLevelOptions.find((item) => item.value === value)?.shortLabel || value || '未分级'
}

export function customerLevelHint(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning}，${option.followAdvice}` : '请先设置意向等级并安排后续跟进'
}

export function customerValueLevelLabel(value?: string) {
  const option = customerValueLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning}` : value || '未分级'
}

export function customerValueLevelOptionLabel(value?: string) {
  const option = customerValueLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning}` : value || '未分级'
}

export function customerValueLevelShortLabel(value?: string) {
  return customerValueLevelOptions.find((item) => item.value === value)?.shortLabel || value || '未分级'
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

export function accountLevelLabel(value?: string) {
  if (value === 'LEADER') {
    return '主管'
  }
  if (value === 'STAFF') {
    return '成员'
  }
  return value || '未设置'
}

export function roleCodeLabel(value?: string) {
  if (value === 'ROLE_ADMIN') {
    return '管理员'
  }
  if (value === 'ROLE_SALES') {
    return '销售'
  }
  if (value === 'ROLE_OPERATIONS') {
    return '运营'
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
