<template>
  <div class="page-stack">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-title">客户管理与跟进记录</div>
        <el-button type="primary" @click="openCustomerDialog()">新增客户</el-button>
      </div>
      <div class="public-summary">
        <div>客户等级会直接带出跟进频率建议，建议先分级再安排跟进计划。</div>
        <div>新增跟进和推荐前，请先从左侧选中一个客户。</div>
      </div>
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="编号 / 姓名 / 电话 / 微信" clearable />
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="filters.sourceChannel" clearable style="width: 160px" placeholder="请选择来源渠道">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="filters.customerLevel" clearable style="width: 360px" placeholder="请选择客户等级">
            <el-option v-for="item in levelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
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
          <el-select v-model="filters.archived" clearable style="width: 120px" placeholder="请选择">
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
        <el-table
          :data="customers"
          border
          highlight-current-row
          @row-click="selectCustomer"
          height="680"
          empty-text="暂无客户记录，请先新增客户或调整筛选条件"
        >
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
        <el-empty v-if="!detail.customer" description="请先从左侧选择一个客户，再查看跟进记录和推荐方案" />
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
              <el-descriptions-item label="联系电话">{{ detail.customer.contactPhone || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ detail.customer.ownerName || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ detail.customer.gender || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ detail.customer.age || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ detail.customer.email || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="微信">{{ detail.customer.wechatNo || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="地区">{{ detail.customer.region || detail.customer.cityArea || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="来源渠道">{{ detail.customer.sourceChannel || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="初步需求">{{ detail.customer.initialNeedType || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="服务偏好">{{ detail.customer.servicePreference || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="预算">{{ detail.customer.budgetRange || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="最近跟进">{{ detail.customer.lastFollowUpAt || '暂无记录' }}</el-descriptions-item>
              <el-descriptions-item label="下次跟进">{{ detail.customer.followUpAt || '未安排' }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ detail.customer.remark || '无补充说明' }}</el-descriptions-item>
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
              <el-empty v-if="!(detail.followRecords?.length)" description="暂无跟进记录，点击“新增跟进”开始记录客户互动" />
              <el-timeline v-else>
                <el-timeline-item
                  v-for="item in detail.followRecords || []"
                  :key="item.id"
                  :timestamp="item.followAt"
                  placement="top"
                >
                  <el-card class="timeline-card" shadow="hover">
                    <div class="timeline-head">
                      <span>{{ contactMethodLabel(item.contactMethod) }}</span>
                      <span class="muted-inline">{{ item.ownerName || '未记录负责人' }}</span>
                    </div>
                    <div><strong>沟通摘要：</strong>{{ item.communicationSummary || '未填写' }}</div>
                    <div><strong>客户诉求：</strong>{{ item.customerNeed || '未填写' }}</div>
                    <div><strong>我方反馈：</strong>{{ item.ourFeedback || '未填写' }}</div>
                    <div><strong>客户反馈：</strong>{{ item.customerFeedback || '未填写' }}</div>
                    <div><strong>下一步动作：</strong>{{ item.nextAction || '未填写' }}</div>
                    <div class="muted-inline">下次跟进：{{ item.nextFollowUpAt || '未安排' }}</div>
                    <div class="muted-inline">等级变更：{{ item.levelBefore || '未分级' }} → {{ item.levelAfter || '未分级' }}</div>
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
              <el-empty v-if="!(detail.recommendations?.length)" description="暂无推荐方案，可为客户推荐单个产品或套餐包" />
              <div v-else class="recommendation-stack">
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
                  <el-table :data="recommendation.items" size="small" border empty-text="暂无推荐明细">
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
        <el-form-item label="客户姓名"><el-input v-model="customerForm.customerName" placeholder="请输入客户姓名" clearable /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="customerForm.contactPhone" placeholder="请输入联系电话" clearable /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="customerForm.gender" style="width: 100%" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="customerForm.age" :min="0" :max="120" style="width: 100%" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="customerForm.email" placeholder="请输入邮箱，例如 demo@example.com" clearable /></el-form-item>
        <el-form-item label="微信"><el-input v-model="customerForm.wechatNo" placeholder="请输入微信号" clearable /></el-form-item>
        <el-form-item label="地区"><el-input v-model="customerForm.region" placeholder="请输入地区，例如 上海浦东" clearable /></el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="customerForm.sourceChannel" style="width: 100%" clearable placeholder="请选择来源渠道">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="customerForm.customerLevel" style="width: 100%" placeholder="请选择客户等级">
            <el-option v-for="item in levelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前状态"><el-input v-model="customerForm.currentStatus" placeholder="请输入当前状态，例如 待跟进" clearable /></el-form-item>
        <el-form-item label="推荐人"><el-input v-model="customerForm.referrerName" placeholder="请输入推荐人或来源人" clearable /></el-form-item>
        <el-form-item label="初步需求"><el-input v-model="customerForm.initialNeedType" placeholder="请输入客户当前关注的需求" clearable /></el-form-item>
        <el-form-item label="服务偏好"><el-input v-model="customerForm.servicePreference" placeholder="请输入服务偏好，例如 上门 / 到店" clearable /></el-form-item>
        <el-form-item label="预算范围"><el-input v-model="customerForm.budgetRange" placeholder="请输入预算范围，例如 3000-5000" clearable /></el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="customerForm.followUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" placeholder="请选择下次跟进时间" /></el-form-item>
        <el-form-item label="建档日期"><el-date-picker v-model="customerForm.entryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="请选择建档日期" /></el-form-item>
        <el-form-item label="归档">
          <el-switch v-model="customerForm.archived" />
        </el-form-item>
      </el-form>
      <el-form :model="customerForm" label-width="110px">
        <el-form-item label="备注"><el-input v-model="customerForm.remark" type="textarea" :rows="3" placeholder="请输入客户补充说明、沟通背景或特别提醒" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="customerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCustomer">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialogVisible" title="新增跟进记录" width="760px">
      <el-form :model="followForm" label-width="110px" class="grid-form">
        <el-form-item label="跟进时间"><el-date-picker v-model="followForm.followAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" placeholder="请选择本次跟进时间" /></el-form-item>
        <el-form-item label="接触方式">
          <el-select v-model="followForm.contactMethod" style="width: 100%" placeholder="请选择接触方式">
            <el-option v-for="item in contactMethods" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="followForm.nextFollowUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" placeholder="请选择下次跟进时间" /></el-form-item>
        <el-form-item label="等级变更">
          <el-select v-model="followForm.customerLevel" style="width: 100%" placeholder="请选择更新后的客户等级">
            <el-option v-for="item in levelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-form :model="followForm" label-width="110px">
        <el-form-item label="沟通摘要"><el-input v-model="followForm.communicationSummary" type="textarea" :rows="2" placeholder="请概括本次沟通主题和关键结论" /></el-form-item>
        <el-form-item label="客户诉求"><el-input v-model="followForm.customerNeed" type="textarea" :rows="2" placeholder="请记录客户当前最关心的诉求或问题" /></el-form-item>
        <el-form-item label="我方反馈"><el-input v-model="followForm.ourFeedback" type="textarea" :rows="2" placeholder="请记录你给出的建议、方案或承诺" /></el-form-item>
        <el-form-item label="客户反馈"><el-input v-model="followForm.customerFeedback" type="textarea" :rows="2" placeholder="请记录客户的态度、疑虑或反馈结果" /></el-form-item>
        <el-form-item label="下一步动作"><el-input v-model="followForm.nextAction" type="textarea" :rows="2" placeholder="请写明下一步计划，例如 发送方案、再次电话跟进" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFollowRecord">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recommendationDialogVisible" title="新增推荐方案" width="860px">
      <el-form :model="recommendationForm" label-width="110px">
        <el-form-item label="推荐理由"><el-input v-model="recommendationForm.recommendationReason" type="textarea" :rows="2" placeholder="请说明为什么这批产品或套餐适合当前客户" /></el-form-item>
        <el-form-item label="补充说明"><el-input v-model="recommendationForm.remark" type="textarea" :rows="2" placeholder="可补充报价口径、沟通注意事项或特殊说明" /></el-form-item>
      </el-form>
      <div class="toolbar compact-toolbar">
        <div class="toolbar-title">推荐项</div>
        <el-button @click="addRecommendationItem">新增一项</el-button>
      </div>
      <div class="public-summary">
        <div>每个推荐项都需要选择类型和具体内容。</div>
        <div>可同时推荐单个产品和套餐包，优先级数字越小越靠前。</div>
      </div>
      <div class="recommendation-editor">
        <div v-for="(item, index) in recommendationForm.items" :key="index" class="recommendation-editor-row">
          <el-select v-model="item.itemType" style="width: 120px" placeholder="类型">
            <el-option label="产品" value="PRODUCT" />
            <el-option label="套餐包" value="PACKAGE" />
          </el-select>
          <el-select v-model="item.itemId" filterable style="flex: 1" placeholder="请选择推荐项">
            <el-option
              v-for="option in recommendationOptions(item.itemType)"
              :key="`${item.itemType}-${option.id}`"
              :label="option.name"
              :value="option.id"
            />
          </el-select>
          <el-input-number v-model="item.priorityNo" :min="1" :max="99" />
          <el-input v-model="item.quotedPrice" placeholder="报价，例如 3999" />
          <el-input v-model="item.note" placeholder="推荐备注" />
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
import { customerLevelHint, customerLevelOptions, isBlank, isValidEmail, isValidPhone } from '../../constants/ui'

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

const levelOptions = customerLevelOptions.map((item) => ({
  ...item,
  optionLabel: `${item.value}级：${item.description}`,
}))
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
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先从左侧选择一个客户')
    return
  }
  resetFollowForm()
  followDialogVisible.value = true
}

function openRecommendationDialog() {
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先从左侧选择一个客户')
    return
  }
  resetRecommendationForm()
  recommendationDialogVisible.value = true
}

async function saveCustomer() {
  if (isBlank(customerForm.customerName)) {
    ElMessage.warning('请先填写客户姓名')
    return
  }
  if (!isValidPhone(customerForm.contactPhone)) {
    ElMessage.warning('请输入有效的联系电话')
    return
  }
  if (!isValidEmail(customerForm.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }
  if (customerForm.age !== undefined && (Number(customerForm.age) < 0 || Number(customerForm.age) > 120)) {
    ElMessage.warning('年龄需在 0 到 120 之间')
    return
  }
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
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先从左侧选择一个客户')
    return
  }
  if (isBlank(followForm.communicationSummary)) {
    ElMessage.warning('请先填写沟通摘要')
    return
  }
  if (isBlank(followForm.customerNeed)) {
    ElMessage.warning('请补充客户诉求')
    return
  }
  if (isBlank(followForm.nextAction)) {
    ElMessage.warning('请填写下一步动作')
    return
  }
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
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先从左侧选择一个客户')
    return
  }
  if (!recommendationForm.items.length) {
    ElMessage.warning('请至少添加一个推荐项')
    return
  }
  const invalidItem = recommendationForm.items.find((item: any) => !item.itemType || !item.itemId)
  if (invalidItem) {
    ElMessage.warning('请为每个推荐项选择完整的类型和内容')
    return
  }
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
  return contactMethods.find((item) => item.value === value)?.label || value || '未记录方式'
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

watch(
  () => followForm.customerLevel,
  (value) => {
    if (value) {
      detail.followFrequencyHint = customerLevelHint(value)
    }
  },
)
</script>
