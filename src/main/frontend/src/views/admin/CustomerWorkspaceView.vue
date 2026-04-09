<template>
  <div class="page-stack">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-title">客户管理与跟进记录</div>
        <el-button type="primary" @click="openCustomerDialog()">新增客户</el-button>
      </div>
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="编号 / 姓名 / 电话 / 微信" clearable />
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="filters.sourceChannel" clearable style="width: 150px">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="filters.customerLevel" clearable style="width: 120px">
            <el-option v-for="item in levelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="filters.nextFollowRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="已归档">
          <el-select v-model="filters.archived" clearable style="width: 120px">
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCustomers">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="customer-workspace">
      <el-card shadow="never">
        <template #header>
          <div class="split-header">
            <span>客户列表</span>
            <el-tag type="info">{{ customers.length }} 条</el-tag>
          </div>
        </template>
        <el-table :data="customers" border highlight-current-row @row-click="selectCustomer" height="680">
          <el-table-column prop="customerName" label="客户" min-width="110" />
          <el-table-column prop="customerLevel" label="等级" width="68" />
          <el-table-column prop="sourceChannel" label="渠道" min-width="110" show-overflow-tooltip />
          <el-table-column prop="followUpAt" label="下次跟进" min-width="165" />
          <el-table-column label="状态" width="74">
            <template #default="{ row }">
              <el-tag :type="row.archived ? 'info' : 'success'">{{ row.archived ? '归档' : '活跃' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <div class="customer-detail-column">
        <el-empty v-if="!detail.customer" description="从左侧选择一个客户查看详情" />
        <template v-else>
          <el-card shadow="never">
            <template #header>
              <div class="split-header">
                <div>
                  <div class="customer-name-line">
                    <span class="toolbar-title">{{ detail.customer.customerName }}</span>
                    <el-tag>{{ detail.customer.customerLevel || '未分级' }}</el-tag>
                    <el-tag type="warning">{{ detail.customer.currentStatus || '待跟进' }}</el-tag>
                  </div>
                  <div class="muted-text">{{ detail.followFrequencyHint }}</div>
                </div>
                <div class="header-actions">
                  <el-button @click="openCustomerDialog(detail.customer)">编辑客户</el-button>
                  <el-button type="primary" @click="openFollowDialog">新增跟进</el-button>
                  <el-button type="success" @click="openRecommendationDialog">新增推荐</el-button>
                </div>
              </div>
            </template>

            <el-descriptions :column="3" border>
              <el-descriptions-item label="客户编号">{{ detail.customer.leadNo }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ detail.customer.contactPhone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ detail.customer.ownerName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ detail.customer.gender || '-' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ detail.customer.age || '-' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ detail.customer.email || '-' }}</el-descriptions-item>
              <el-descriptions-item label="微信">{{ detail.customer.wechatNo || '-' }}</el-descriptions-item>
              <el-descriptions-item label="地区">{{ detail.customer.region || detail.customer.cityArea || '-' }}</el-descriptions-item>
              <el-descriptions-item label="来源渠道">{{ detail.customer.sourceChannel || '-' }}</el-descriptions-item>
              <el-descriptions-item label="初步需求">{{ detail.customer.initialNeedType || '-' }}</el-descriptions-item>
              <el-descriptions-item label="服务偏好">{{ detail.customer.servicePreference || '-' }}</el-descriptions-item>
              <el-descriptions-item label="预算">{{ detail.customer.budgetRange || '-' }}</el-descriptions-item>
              <el-descriptions-item label="最近跟进">{{ detail.customer.lastFollowUpAt || '-' }}</el-descriptions-item>
              <el-descriptions-item label="下次跟进">{{ detail.customer.followUpAt || '-' }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ detail.customer.remark || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <div class="two-column customer-panels">
            <el-card shadow="never">
              <template #header>
                <div class="split-header">
                  <span>跟进时间线</span>
                  <el-tag type="info">{{ detail.followRecords?.length || 0 }} 条</el-tag>
                </div>
              </template>
              <el-timeline>
                <el-timeline-item
                  v-for="item in detail.followRecords || []"
                  :key="item.id"
                  :timestamp="item.followAt"
                  placement="top"
                >
                  <el-card class="timeline-card" shadow="hover">
                    <div class="timeline-head">
                      <span>{{ contactMethodLabel(item.contactMethod) }}</span>
                      <span class="muted-inline">{{ item.ownerName || '-' }}</span>
                    </div>
                    <div><strong>沟通摘要：</strong>{{ item.communicationSummary || '-' }}</div>
                    <div><strong>客户诉求：</strong>{{ item.customerNeed || '-' }}</div>
                    <div><strong>我方反馈：</strong>{{ item.ourFeedback || '-' }}</div>
                    <div><strong>客户反馈：</strong>{{ item.customerFeedback || '-' }}</div>
                    <div><strong>下一步动作：</strong>{{ item.nextAction || '-' }}</div>
                    <div class="muted-inline">下次跟进：{{ item.nextFollowUpAt || '-' }}</div>
                    <div class="muted-inline">等级变更：{{ item.levelBefore || '-' }} → {{ item.levelAfter || '-' }}</div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </el-card>

            <el-card shadow="never">
              <template #header>
                <div class="split-header">
                  <span>推荐方案</span>
                  <el-tag type="success">{{ detail.recommendations?.length || 0 }} 次</el-tag>
                </div>
              </template>
              <div class="recommendation-stack">
                <el-card
                  v-for="recommendation in detail.recommendations || []"
                  :key="recommendation.id"
                  shadow="hover"
                  class="recommendation-card"
                >
                  <template #header>
                    <div class="split-header">
                      <div>
                        <div>{{ recommendation.recommendationReason || '未填写推荐理由' }}</div>
                        <div class="muted-inline">{{ recommendation.ownerName }} · {{ recommendation.createdAt }}</div>
                      </div>
                    </div>
                  </template>
                  <el-table :data="recommendation.items" size="small" border>
                    <el-table-column prop="priorityNo" label="优先级" width="84" />
                    <el-table-column prop="itemType" label="类型" width="90" />
                    <el-table-column prop="itemName" label="名称" min-width="160" />
                    <el-table-column prop="enterpriseName" label="所属主体" min-width="120" />
                    <el-table-column prop="quotedPrice" label="报价" width="100" />
                    <el-table-column prop="note" label="备注" min-width="140" />
                  </el-table>
                  <div class="detail-summary muted-inline">{{ recommendation.remark || '无补充说明' }}</div>
                </el-card>
              </div>
            </el-card>
          </div>
        </template>
      </div>
    </div>

    <el-dialog v-model="customerDialogVisible" :title="customerForm.id ? '编辑客户' : '新增客户'" width="860px">
      <el-form :model="customerForm" label-width="110px" class="grid-form">
        <el-form-item label="客户姓名"><el-input v-model="customerForm.customerName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="customerForm.contactPhone" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="customerForm.gender" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="customerForm.age" :min="0" :max="120" style="width: 100%" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="customerForm.email" /></el-form-item>
        <el-form-item label="微信"><el-input v-model="customerForm.wechatNo" /></el-form-item>
        <el-form-item label="地区"><el-input v-model="customerForm.region" /></el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="customerForm.sourceChannel" style="width: 100%" clearable>
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="customerForm.customerLevel" style="width: 100%">
            <el-option v-for="item in levelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前状态"><el-input v-model="customerForm.currentStatus" /></el-form-item>
        <el-form-item label="推荐人"><el-input v-model="customerForm.referrerName" /></el-form-item>
        <el-form-item label="初步需求"><el-input v-model="customerForm.initialNeedType" /></el-form-item>
        <el-form-item label="服务偏好"><el-input v-model="customerForm.servicePreference" /></el-form-item>
        <el-form-item label="预算范围"><el-input v-model="customerForm.budgetRange" /></el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="customerForm.followUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
        <el-form-item label="建档日期"><el-date-picker v-model="customerForm.entryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="归档">
          <el-switch v-model="customerForm.archived" />
        </el-form-item>
      </el-form>
      <el-form :model="customerForm" label-width="110px">
        <el-form-item label="备注"><el-input v-model="customerForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="customerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCustomer">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialogVisible" title="新增跟进记录" width="760px">
      <el-form :model="followForm" label-width="110px" class="grid-form">
        <el-form-item label="跟进时间"><el-date-picker v-model="followForm.followAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
        <el-form-item label="接触方式">
          <el-select v-model="followForm.contactMethod" style="width: 100%">
            <el-option v-for="item in contactMethods" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="followForm.nextFollowUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
        <el-form-item label="等级变更">
          <el-select v-model="followForm.customerLevel" style="width: 100%">
            <el-option v-for="item in levelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-form :model="followForm" label-width="110px">
        <el-form-item label="沟通摘要"><el-input v-model="followForm.communicationSummary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="客户诉求"><el-input v-model="followForm.customerNeed" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="我方反馈"><el-input v-model="followForm.ourFeedback" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="客户反馈"><el-input v-model="followForm.customerFeedback" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="下一步动作"><el-input v-model="followForm.nextAction" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFollowRecord">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recommendationDialogVisible" title="新增推荐方案" width="860px">
      <el-form :model="recommendationForm" label-width="110px">
        <el-form-item label="推荐理由"><el-input v-model="recommendationForm.recommendationReason" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="补充说明"><el-input v-model="recommendationForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <div class="toolbar compact-toolbar">
        <div class="toolbar-title">推荐项</div>
        <el-button @click="addRecommendationItem">新增一项</el-button>
      </div>
      <div class="recommendation-editor">
        <div v-for="(item, index) in recommendationForm.items" :key="index" class="recommendation-editor-row">
          <el-select v-model="item.itemType" style="width: 120px">
            <el-option label="产品" value="PRODUCT" />
            <el-option label="套餐包" value="PACKAGE" />
          </el-select>
          <el-select v-model="item.itemId" filterable style="flex: 1">
            <el-option
              v-for="option in recommendationOptions(item.itemType)"
              :key="`${item.itemType}-${option.id}`"
              :label="option.name"
              :value="option.id"
            />
          </el-select>
          <el-input-number v-model="item.priorityNo" :min="1" :max="99" />
          <el-input v-model="item.quotedPrice" placeholder="报价" />
          <el-input v-model="item.note" placeholder="备注" />
          <el-button text type="danger" @click="removeRecommendationItem(index)">删除</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="recommendationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecommendation">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'

const route = useRoute()
const router = useRouter()

const dicts = reactive<any>({})
const customers = ref<any[]>([])
const detail = reactive<any>({ customer: null, followRecords: [], recommendations: [], followFrequencyHint: '' })
const selectedCustomerId = ref<number | null>(null)

const customerDialogVisible = ref(false)
const followDialogVisible = ref(false)
const recommendationDialogVisible = ref(false)

const filters = reactive<any>({
  keyword: '',
  sourceChannel: '',
  customerLevel: '',
  archived: undefined,
  nextFollowRange: [],
})

const levelOptions = ['A', 'B', 'C', 'D']
const contactMethods = [
  { label: '电话', value: 'PHONE' },
  { label: '微信', value: 'WECHAT' },
  { label: '邮件', value: 'EMAIL' },
  { label: '面访', value: 'VISIT' },
]

const customerForm = reactive<any>({})
const followForm = reactive<any>({})
const recommendationForm = reactive<any>({ items: [] })
const products = ref<any[]>([])
const packages = ref<any[]>([])

function resetCustomerForm() {
  Object.assign(customerForm, {
    id: undefined,
    customerName: '',
    contactPhone: '',
    gender: '',
    age: undefined,
    email: '',
    wechatNo: '',
    region: '',
    sourceChannel: '',
    customerLevel: 'B',
    currentStatus: '待跟进',
    referrerName: '',
    initialNeedType: '',
    servicePreference: '',
    budgetRange: '',
    entryDate: '',
    followUpAt: '',
    remark: '',
    archived: false,
  })
}

function resetFollowForm() {
  Object.assign(followForm, {
    followAt: '',
    contactMethod: 'PHONE',
    communicationSummary: '',
    customerNeed: '',
    ourFeedback: '',
    customerFeedback: '',
    nextAction: '',
    nextFollowUpAt: '',
    customerLevel: detail.customer?.customerLevel || 'B',
  })
}

function resetRecommendationForm() {
  Object.assign(recommendationForm, {
    recommendationReason: '',
    remark: '',
    items: [{
      itemType: 'PRODUCT',
      itemId: undefined,
      priorityNo: 1,
      quotedPrice: '',
      note: '',
    }],
  })
}

function recommendationOptions(itemType: string) {
  return itemType === 'PACKAGE' ? packages.value : products.value
}

async function loadCustomers() {
  const params: any = {
    keyword: filters.keyword,
    sourceChannel: filters.sourceChannel,
    customerLevel: filters.customerLevel,
    archived: filters.archived,
  }
  if (filters.nextFollowRange?.length === 2) {
    params.nextFollowStart = filters.nextFollowRange[0]
    params.nextFollowEnd = filters.nextFollowRange[1]
  }
  customers.value = await api.customers(params)
  if (selectedCustomerId.value && !customers.value.find((item) => item.id === selectedCustomerId.value)) {
    selectedCustomerId.value = null
    Object.assign(detail, { customer: null, followRecords: [], recommendations: [], followFrequencyHint: '' })
  }
}

async function loadDetail(customerId: number | null) {
  if (!customerId) {
    return
  }
  selectedCustomerId.value = customerId
  Object.assign(detail, await api.customerDetail(customerId))
}

async function ensureSelection() {
  const queryId = Number(route.query.customerId)
  if (queryId) {
    await loadDetail(queryId)
    return
  }
  if (!selectedCustomerId.value && customers.value.length) {
    await selectCustomer(customers.value[0])
  }
}

async function selectCustomer(row: any) {
  if (!row?.id) {
    return
  }
  await router.replace({ path: '/admin/customers', query: { customerId: String(row.id) } })
  await loadDetail(row.id)
}

function openCustomerDialog(row?: any) {
  resetCustomerForm()
  if (row) {
    Object.assign(customerForm, row)
  }
  customerDialogVisible.value = true
}

function openFollowDialog() {
  resetFollowForm()
  followDialogVisible.value = true
}

function openRecommendationDialog() {
  resetRecommendationForm()
  recommendationDialogVisible.value = true
}

async function saveCustomer() {
  if (customerForm.id) {
    await api.updateCustomer(customerForm.id, customerForm)
  } else {
    const saved = await api.createCustomer(customerForm)
    customerForm.id = saved.id
  }
  ElMessage.success('客户信息已保存')
  customerDialogVisible.value = false
  await loadCustomers()
  if (customerForm.id) {
    await router.replace({ path: '/admin/customers', query: { customerId: String(customerForm.id) } })
    await loadDetail(customerForm.id)
  }
}

async function saveFollowRecord() {
  if (!selectedCustomerId.value) return
  await api.saveFollowRecord(selectedCustomerId.value, followForm)
  ElMessage.success('跟进记录已保存')
  followDialogVisible.value = false
  await loadCustomers()
  await loadDetail(selectedCustomerId.value)
}

function addRecommendationItem() {
  recommendationForm.items.push({
    itemType: 'PRODUCT',
    itemId: undefined,
    priorityNo: recommendationForm.items.length + 1,
    quotedPrice: '',
    note: '',
  })
}

function removeRecommendationItem(index: number | string) {
  recommendationForm.items.splice(Number(index), 1)
}

async function saveRecommendation() {
  if (!selectedCustomerId.value) return
  const payload = {
    ...recommendationForm,
    items: recommendationForm.items.map((item: any) => ({
      ...item,
      quotedPrice: item.quotedPrice === '' ? null : item.quotedPrice,
    })),
  }
  await api.saveRecommendation(selectedCustomerId.value, payload)
  ElMessage.success('推荐方案已保存')
  recommendationDialogVisible.value = false
  await loadDetail(selectedCustomerId.value)
}

function resetFilters() {
  Object.assign(filters, {
    keyword: '',
    sourceChannel: '',
    customerLevel: '',
    archived: undefined,
    nextFollowRange: [],
  })
  loadCustomers()
}

function contactMethodLabel(value: string) {
  return contactMethods.find((item) => item.value === value)?.label || value || '-'
}

onMounted(async () => {
  resetCustomerForm()
  resetFollowForm()
  resetRecommendationForm()
  Object.assign(dicts, await api.dictsGrouped())
  products.value = await api.products()
  packages.value = await api.productPackages()
  await loadCustomers()
  await ensureSelection()
})

watch(
  () => route.query.customerId,
  async (value) => {
    const customerId = Number(value)
    if (customerId) {
      await loadDetail(customerId)
    }
  },
)
</script>
