export const customerLevelOptions = [
  {
    value: 'A',
    shortLabel: 'A级',
    meaning: '高意向',
    criteria: '已报价 / 方案审核中 / 近期有明确采购计划',
    followFrequency: '每 3 天 1 次',
    followAdvice: '建议每 3 天跟进 1 次',
    tagType: 'danger',
  },
  {
    value: 'B',
    shortLabel: 'B级',
    meaning: '潜力',
    criteria: '需求匹配但未报价 / 正在对比竞品',
    followFrequency: '每周 1 次',
    followAdvice: '建议每周跟进 1 次',
    tagType: 'warning',
  },
  {
    value: 'C',
    shortLabel: 'C级',
    meaning: '普通',
    criteria: '仅留资未互动 / 行业相关但无迫切需求',
    followFrequency: '每 2 周 1 次',
    followAdvice: '建议每 2 周跟进 1 次',
    tagType: 'info',
  },
  {
    value: 'D',
    shortLabel: 'D级',
    meaning: '沉默',
    criteria: '超 3 个月无响应 / 联系方式无效',
    followFrequency: '季度触达或归档',
    followAdvice: '建议季度触达或归档',
    tagType: '',
  },
]

export const knownDictTypes = [
  { value: 'source_channel', label: '来源渠道' },
  { value: 'customer_level', label: '意向等级' },
  { value: 'customer_value_level', label: '价值等级' },
  { value: 'product_category', label: '产品分类' },
  { value: 'product_applicable_people', label: '适用人群' },
  { value: 'package_applicable_scene', label: '套餐适用场景' },
  { value: 'enterprise_service_mode', label: '企业服务模式' },
  { value: 'enterprise_response_speed', label: '企业响应速度' },
  { value: 'enterprise_certification_status', label: '企业认证状态' },
  { value: 'enterprise_certification_level', label: '企业认证等级' },
  { value: 'enterprise_expertise', label: '企业擅长领域' },
]

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
  return option ? `${option.shortLabel}${option.meaning}` : value || '未分级'
}

export function customerLevelShortLabel(value?: string) {
  return customerLevelOptions.find((item) => item.value === value)?.shortLabel || value || '未分级'
}

export function customerLevelRule(value?: string) {
  return customerLevelOptions.find((item) => item.value === value)
}

export function customerLevelHint(value?: string) {
  const option = customerLevelOptions.find((item) => item.value === value)
  return option ? `${option.shortLabel}${option.meaning} · ${option.criteria} · ${option.followAdvice}` : '请先设置意向等级并安排后续跟进'
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

export function dictTypeLabel(value?: string) {
  const option = knownDictTypes.find((item) => item.value === value)
  return option?.label || value || '未命名'
}

export function uniqueDictTypeOptions(existingValues: string[] = []) {
  const merged = new Map<string, string>()
  knownDictTypes.forEach((item) => merged.set(item.value, item.label))
  existingValues.filter(Boolean).forEach((value) => {
    if (!merged.has(value)) {
      merged.set(value, value)
    }
  })
  return Array.from(merged.entries()).map(([value, label]) => ({ value, label }))
}

export function formatPriceText(value?: string | number | null) {
  if (value === null || value === undefined || value === '') {
    return '未报价'
  }
  return `${value}`
}

function padDatePart(value: number) {
  return `${value}`.padStart(2, '0')
}

export function toDateValue(value?: unknown) {
  if (value === null || value === undefined || value === '') {
    return null
  }
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value
  }
  const text = String(value).trim()
  if (!text) {
    return null
  }
  if (/^\d{4}-\d{2}-\d{2}$/.test(text)) {
    const dateOnly = new Date(`${text}T00:00:00`)
    return Number.isNaN(dateOnly.getTime()) ? null : dateOnly
  }
  if (/^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}$/.test(text)) {
    const minuteDate = new Date(text.replace(' ', 'T') + ':00')
    return Number.isNaN(minuteDate.getTime()) ? null : minuteDate
  }
  if (/^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}$/.test(text)) {
    const secondDate = new Date(text.replace(' ', 'T'))
    return Number.isNaN(secondDate.getTime()) ? null : secondDate
  }
  const normalized = text.includes(' ') && !text.includes('T') ? text.replace(' ', 'T') : text
  const parsed = new Date(normalized)
  return Number.isNaN(parsed.getTime()) ? null : parsed
}

export function formatDate(value?: unknown, fallback = '未填写') {
  const parsed = toDateValue(value)
  if (!parsed) {
    return fallback
  }
  const year = parsed.getFullYear()
  const month = padDatePart(parsed.getMonth() + 1)
  const day = padDatePart(parsed.getDate())
  return `${year}-${month}-${day}`
}

export function formatDateTime(value?: unknown, fallback = '未填写') {
  const parsed = toDateValue(value)
  if (!parsed) {
    return fallback
  }
  const year = parsed.getFullYear()
  const month = padDatePart(parsed.getMonth() + 1)
  const day = padDatePart(parsed.getDate())
  const hour = padDatePart(parsed.getHours())
  const minute = padDatePart(parsed.getMinutes())
  return `${year}-${month}-${day} ${hour}:${minute}`
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
  if (message.includes('PRODUCT') || message.includes('PACKAGE')) {
    return '推荐类型无效，请重新选择'
  }
  if (message.includes('STAFF') || message.includes('LEADER')) {
    return '账号等级无效，请重新选择'
  }
  return message
}
