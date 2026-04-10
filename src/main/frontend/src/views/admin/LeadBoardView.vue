<template>
  <div class="page-stack">
    <div class="toolbar">
      <el-tag type="info">{{ pageSummary }}</el-tag>
      <el-button type="primary" @click="openDialog">新建内部线索</el-button>
    </div>

    <el-card shadow="never">
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="编号/姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="filters.sourceChannel" clearable style="width: 150px">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-select v-model="filters.customerLevel" clearable style="width: 120px">
              <el-option v-for="item in dicts.customer_level || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
            <IntentLevelGuideDialog />
          </div>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-select v-model="filters.currentStatus" clearable style="width: 140px">
            <el-option v-for="item in dicts.current_status || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="rows" border>
      <el-table-column prop="leadNo" label="线索编号" width="160" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="contactPhone" label="联系方式" width="140" />
      <el-table-column prop="sourceChannel" label="来源渠道" width="120" />
      <el-table-column prop="customerLevel" label="客户等级" width="100" />
      <el-table-column prop="currentStatus" label="当前状态" width="110" />
      <el-table-column prop="workflowStage" label="流程阶段" width="120" />
      <el-table-column prop="ownerName" label="负责人" width="120" />
      <el-table-column prop="followUpAt" label="下次跟进" min-width="180" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新建内部线索" width="820px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="客户姓名"><el-input v-model="form.customerName" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.contactPhone" /></el-form-item>
        <el-form-item label="录入日期"><el-date-picker v-model="form.entryDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="城市区域"><el-input v-model="form.cityArea" /></el-form-item>
        <el-form-item label="年龄段"><el-input v-model="form.ageRange" /></el-form-item>
        <el-form-item label="家庭结构"><el-input v-model="form.familyStructure" /></el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="form.sourceChannel" style="width: 100%">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="推荐人"><el-input v-model="form.referrerName" /></el-form-item>
        <el-form-item label="服务对象">
          <el-select v-model="form.serviceObject" style="width: 100%">
            <el-option v-for="item in dicts.service_object || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="初步需求"><el-input v-model="form.initialNeedType" /></el-form-item>
        <el-form-item label="服务偏好">
          <el-select v-model="form.servicePreference" style="width: 100%">
            <el-option v-for="item in dicts.service_preference || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急度">
          <el-select v-model="form.urgency" style="width: 100%">
            <el-option v-for="item in dicts.urgency || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算区间"><el-input v-model="form.budgetRange" /></el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="form.followUpAt" type="datetime" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="意向等级判定">
          <IntentLevelGuideDialog />
        </el-form-item>
      </el-form>
      <el-form :model="form" label-width="110px">
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveLead">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import IntentLevelGuideDialog from '../../components/IntentLevelGuideDialog.vue'

const route = useRoute()
const router = useRouter()
const rows = ref<any[]>([])
const dicts = reactive<any>({})
const dialogVisible = ref(false)

const filters = reactive({
  keyword: '',
  sourceChannel: '',
  customerLevel: '',
  currentStatus: '',
  workflowStages: '',
})

const form = reactive<any>({})

const pageSummary = computed(() => {
  const stageLabels = (route.meta.workflowStages as string[] | undefined) || []
  if (!stageLabels.length) {
    return '显示全部客户主线，支持按来源、等级、状态继续筛选'
  }
  return `默认显示流程阶段：${stageLabels.join(' / ')}`
})

function resetForm() {
  Object.assign(form, {
    customerName: '',
    contactPhone: '',
    entryDate: '',
    cityArea: '',
    ageRange: '',
    familyStructure: '',
    sourceChannel: '',
    referrerName: '',
    serviceObject: '',
    initialNeedType: '',
    servicePreference: '',
    urgency: '',
    budgetRange: '',
    followUpAt: '',
    remark: '',
  })
}

function openDialog() {
  resetForm()
  dialogVisible.value = true
}

function openDetail(id: number) {
  router.push(`/admin/detail/${id}?tab=${route.meta.focusTab || 'overview'}`)
}

async function load() {
  rows.value = await api.leads(filters)
}

function resetFilters() {
  Object.assign(filters, {
    keyword: '',
    sourceChannel: '',
    customerLevel: '',
    currentStatus: '',
    workflowStages: defaultWorkflowStages(),
  })
  load()
}

function defaultWorkflowStages() {
  const stages = (route.meta.workflowStages as string[] | undefined) || []
  return stages.join(',')
}

function applyRouteDefaults() {
  filters.workflowStages = defaultWorkflowStages()
  load()
}

async function saveLead() {
  await api.createInternalLead(form)
  ElMessage.success('线索已创建')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  Object.assign(dicts, await api.dictsGrouped())
  await applyRouteDefaults()
})

watch(
  () => route.fullPath,
  () => {
    applyRouteDefaults()
  },
)
</script>
