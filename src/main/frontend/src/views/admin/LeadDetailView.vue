<template>
  <div class="page-stack">
    <el-card shadow="never">
      <div class="toolbar">
        <div>
          <div class="section-title">{{ detail.lead?.customerName || '客户详情' }}</div>
          <div class="muted-inline">线索编号：{{ detail.lead?.leadNo }} ｜ 当前阶段：{{ detail.lead?.workflowStage }} ｜ 当前状态：{{ detail.lead?.currentStatus }}</div>
        </div>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-descriptions :column="4" border class="detail-summary">
        <el-descriptions-item label="联系方式">{{ detail.lead?.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="来源渠道">{{ detail.lead?.sourceChannel }}</el-descriptions-item>
        <el-descriptions-item label="客户等级">{{ detail.lead?.customerLevel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detail.lead?.ownerName || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="客户识别与分级" name="identification">
        <el-form :model="identification" label-width="130px" class="grid-form">
          <el-form-item label="渠道信任等级">
            <el-select v-model="identification.channelTrustLevel" style="width: 100%">
              <el-option v-for="item in dicts.channel_trust_level || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务对象">
            <el-select v-model="identification.serviceObject" style="width: 100%">
              <el-option v-for="item in dicts.service_object || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="初步需求类型"><el-input v-model="identification.initialNeedType" /></el-form-item>
          <el-form-item label="服务偏好">
            <el-select v-model="identification.servicePreference" style="width: 100%">
              <el-option v-for="item in dicts.service_preference || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="需求紧急度">
            <el-select v-model="identification.urgency" style="width: 100%">
              <el-option v-for="item in dicts.urgency || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="是否有明确痛点"><el-switch v-model="identification.clearPainPoint" /></el-form-item>
          <el-form-item label="是否强信任需求"><el-switch v-model="identification.strongTrustDemand" /></el-form-item>
          <el-form-item label="预算区间"><el-input v-model="identification.budgetRange" /></el-form-item>
          <el-form-item label="决策人"><el-input v-model="identification.decisionMaker" /></el-form-item>
          <el-form-item label="决策速度">
            <el-select v-model="identification.decisionSpeed" style="width: 100%">
              <el-option v-for="item in dicts.decision_speed || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格敏感度">
            <el-select v-model="identification.priceSensitivity" style="width: 100%">
              <el-option v-for="item in dicts.price_sensitivity || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="认证兴趣">
            <el-select v-model="identification.certificationInterest" style="width: 100%">
              <el-option v-for="item in dicts.certification_interest || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="客户等级">
            <el-select v-model="identification.customerLevel" style="width: 100%">
              <el-option v-for="item in dicts.customer_level || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="当前状态">
            <el-select v-model="identification.currentStatus" style="width: 100%">
              <el-option v-for="item in dicts.current_status || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="负责人"><el-input v-model="identification.ownerName" /></el-form-item>
          <el-form-item label="下次跟进时间">
            <el-date-picker v-model="identification.followUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </el-form-item>
        </el-form>
        <el-form :model="identification" label-width="130px">
          <el-form-item label="备注"><el-input v-model="identification.remark" type="textarea" :rows="3" /></el-form-item>
        </el-form>
        <el-button type="primary" @click="saveIdentification">保存识别结果</el-button>
      </el-tab-pane>

      <el-tab-pane label="需求评估" name="assessment">
        <el-form :model="assessment" label-width="140px" class="grid-form">
          <el-form-item label="评估日期"><el-date-picker v-model="assessment.assessmentDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          <el-form-item label="评估人"><el-input v-model="assessment.assessorName" /></el-form-item>
          <el-form-item label="需求周期"><el-input v-model="assessment.needDuration" placeholder="短期 / 长期" /></el-form-item>
          <el-form-item label="目标类型">
            <el-select v-model="assessment.goalType" style="width: 100%">
              <el-option v-for="item in dicts.goal_type || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务方式">
            <el-select v-model="assessment.serviceMode" style="width: 100%">
              <el-option v-for="item in dicts.service_mode || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务时间偏好"><el-input v-model="assessment.serviceTimePreference" /></el-form-item>
          <el-form-item label="服务频次预期"><el-input v-model="assessment.serviceFrequency" /></el-form-item>
          <el-form-item label="风险关注点">
            <el-select v-model="assessment.riskConcern" style="width: 100%">
              <el-option v-for="item in dicts.risk_concern || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="预算区间"><el-input v-model="assessment.budgetRange" /></el-form-item>
          <el-form-item label="决策方式">
            <el-select v-model="assessment.decisionMode" style="width: 100%">
              <el-option v-for="item in dicts.decision_mode || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="启动方式">
            <el-select v-model="assessment.startMode" style="width: 100%">
              <el-option v-for="item in dicts.start_mode || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="推荐服务方式"><el-input v-model="assessment.recommendedServiceMode" /></el-form-item>
          <el-form-item label="推荐周期"><el-input v-model="assessment.recommendedCycle" /></el-form-item>
          <el-form-item label="推荐企业类型"><el-input v-model="assessment.recommendedEnterpriseType" /></el-form-item>
          <el-form-item label="接受统一匹配"><el-switch v-model="assessment.acceptPlatformMatch" /></el-form-item>
          <el-form-item label="需要平台监督"><el-switch v-model="assessment.needPlatformSupervision" /></el-form-item>
          <el-form-item label="有不良服务经历"><el-switch v-model="assessment.badServiceExperience" /></el-form-item>
          <el-form-item label="进入匹配环节"><el-switch v-model="assessment.enterMatchFlow" /></el-form-item>
        </el-form>
        <el-form :model="assessment" label-width="140px">
          <el-form-item label="当前主要问题"><el-input v-model="assessment.mainIssue" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="需求背景"><el-input v-model="assessment.backgroundInfo" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="平台建议结论"><el-input v-model="assessment.platformConclusion" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="风险提示"><el-input v-model="assessment.riskTip" type="textarea" :rows="2" /></el-form-item>
        </el-form>
        <el-button type="primary" @click="saveAssessment">保存需求评估</el-button>
      </el-tab-pane>

      <el-tab-pane label="企业匹配与推荐" name="match">
        <el-form :model="match" label-width="140px" class="grid-form">
          <el-form-item label="需求类型"><el-input v-model="match.needType" /></el-form-item>
          <el-form-item label="服务方式"><el-input v-model="match.serviceMode" /></el-form-item>
          <el-form-item label="预算区间"><el-input v-model="match.budgetRange" /></el-form-item>
          <el-form-item label="主推荐企业">
            <el-select v-model="match.primaryEnterpriseId" style="width: 100%" clearable>
              <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="备选企业">
            <el-select v-model="match.backupEnterpriseId" style="width: 100%" clearable>
              <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="最终成交企业">
            <el-select v-model="match.finalEnterpriseId" style="width: 100%" clearable>
              <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="企业确认接单"><el-switch v-model="match.enterpriseConfirmed" /></el-form-item>
          <el-form-item label="客户接受推荐"><el-switch v-model="match.customerAccepted" /></el-form-item>
        </el-form>
        <el-form :model="match" label-width="140px">
          <el-form-item label="特殊要求"><el-input v-model="match.specialRequirements" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="推荐理由"><el-input v-model="match.recommendationReason" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="不推荐其他企业原因"><el-input v-model="match.otherNotRecommendedReason" type="textarea" :rows="2" /></el-form-item>
        </el-form>
        <el-table :data="match.candidates" border>
          <el-table-column prop="slotCode" label="槽位" width="80" />
          <el-table-column label="候选企业" min-width="220">
            <template #default="{ row }">
              <el-select v-model="row.enterpriseId" style="width: 100%" clearable @change="syncCandidate(row)">
                <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="适配度" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.fitScore" :min="0" :max="100" />
            </template>
          </el-table-column>
          <el-table-column label="价格区间" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.priceRange" />
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="saveMatch">保存企业匹配</el-button>
      </el-tab-pane>

      <el-tab-pane label="交付监督与回访" name="delivery">
        <el-form :model="delivery" label-width="150px" class="grid-form">
          <el-form-item label="服务企业"><el-input v-model="delivery.serviceEnterprise" /></el-form-item>
          <el-form-item label="服务项目"><el-input v-model="delivery.serviceProject" /></el-form-item>
          <el-form-item label="服务方式"><el-input v-model="delivery.serviceMode" /></el-form-item>
          <el-form-item label="服务日期"><el-date-picker v-model="delivery.serviceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          <el-form-item label="服务时间"><el-input v-model="delivery.serviceTime" /></el-form-item>
          <el-form-item label="跟单负责人"><el-input v-model="delivery.coordinatorName" /></el-form-item>
          <el-form-item label="客户确认"><el-switch v-model="delivery.customerConfirmed" /></el-form-item>
          <el-form-item label="企业确认"><el-switch v-model="delivery.enterpriseConfirmed" /></el-form-item>
          <el-form-item label="服务人员确认"><el-switch v-model="delivery.staffConfirmed" /></el-form-item>
          <el-form-item label="服务地点确认"><el-switch v-model="delivery.locationConfirmed" /></el-form-item>
          <el-form-item label="特殊需求确认"><el-switch v-model="delivery.specialNeedConfirmed" /></el-form-item>
          <el-form-item label="风险提示已告知"><el-switch v-model="delivery.riskInformed" /></el-form-item>
          <el-form-item label="按时开始"><el-switch v-model="delivery.startedOnTime" /></el-form-item>
          <el-form-item label="按约到位"><el-switch v-model="delivery.staffOnSite" /></el-form-item>
          <el-form-item label="按约执行"><el-switch v-model="delivery.executedAsAgreed" /></el-form-item>
          <el-form-item label="中途异常"><el-switch v-model="delivery.hasException" /></el-form-item>
          <el-form-item label="客户满意度"><el-rate v-model="delivery.satisfactionScore" :max="5" /></el-form-item>
          <el-form-item label="是否继续使用"><el-switch v-model="delivery.willingContinue" /></el-form-item>
          <el-form-item label="是否推荐他人"><el-switch v-model="delivery.willingRecommend" /></el-form-item>
          <el-form-item label="交付达标"><el-switch v-model="delivery.metStandard" /></el-form-item>
          <el-form-item label="建议继续合作"><el-switch v-model="delivery.continueCooperation" /></el-form-item>
        </el-form>
        <el-form :model="delivery" label-width="150px">
          <el-form-item label="异常内容"><el-input v-model="delivery.exceptionContent" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="处理方式"><el-input v-model="delivery.resolution" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="客户最满意点"><el-input v-model="delivery.mostSatisfiedPoint" /></el-form-item>
          <el-form-item label="客户最不满意点"><el-input v-model="delivery.mostDissatisfiedPoint" /></el-form-item>
          <el-form-item label="后续跟进动作"><el-input v-model="delivery.nextAction" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="delivery.remark" type="textarea" :rows="2" /></el-form-item>
        </el-form>
        <div class="detail-actions">
          <el-button type="primary" @click="saveDelivery">保存交付监督</el-button>
          <el-button @click="createToken">生成回访链接</el-button>
          <el-input v-if="feedbackUrl" :model-value="feedbackUrl" readonly />
        </div>
      </el-tab-pane>

      <el-tab-pane label="售后复购跟进" name="aftersales">
        <el-form :model="afterSales" label-width="150px" class="grid-form">
          <el-form-item label="首次成交日期"><el-date-picker v-model="afterSales.firstDealDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          <el-form-item label="首次服务企业"><el-input v-model="afterSales.firstEnterprise" /></el-form-item>
          <el-form-item label="首次服务项目"><el-input v-model="afterSales.firstServiceProject" /></el-form-item>
          <el-form-item label="首次服务满意度"><el-input v-model="afterSales.firstSatisfaction" /></el-form-item>
          <el-form-item label="平台认可度"><el-input v-model="afterSales.platformRecognition" /></el-form-item>
          <el-form-item label="企业认可度"><el-input v-model="afterSales.enterpriseRecognition" /></el-form-item>
          <el-form-item label="有无新需求"><el-switch v-model="afterSales.hasNewDemand" /></el-form-item>
          <el-form-item label="涉及家庭其他成员"><el-switch v-model="afterSales.otherFamilyDemand" /></el-form-item>
          <el-form-item label="复购可能性">
            <el-select v-model="afterSales.repurchasePossibility" style="width: 100%">
              <el-option v-for="item in dicts.repurchase_possibility || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="复购方向"><el-input v-model="afterSales.repurchaseDirection" /></el-form-item>
          <el-form-item label="推荐沟通时间">
            <el-date-picker v-model="afterSales.recommendedCommunicationTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </el-form-item>
          <el-form-item label="推荐沟通人"><el-input v-model="afterSales.recommendedCommunicationPerson" /></el-form-item>
          <el-form-item label="愿意转介绍"><el-switch v-model="afterSales.willingReferral" /></el-form-item>
          <el-form-item label="形成新线索"><el-switch v-model="afterSales.newLeadCreated" /></el-form-item>
          <el-form-item label="客户生命周期状态">
            <el-select v-model="afterSales.customerLifecycleStatus" style="width: 100%">
              <el-option v-for="item in dicts.customer_lifecycle_status || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="长期负责人"><el-input v-model="afterSales.longTermOwner" /></el-form-item>
        </el-form>
        <el-form :model="afterSales" label-width="150px">
          <el-form-item label="推荐复购方案"><el-input v-model="afterSales.recommendedPlan" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="潜在转介绍对象"><el-input v-model="afterSales.potentialReferralTarget" /></el-form-item>
          <el-form-item label="转介绍进度"><el-input v-model="afterSales.referralProgress" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="afterSales.remark" type="textarea" :rows="2" /></el-form-item>
        </el-form>
        <el-button type="primary" @click="saveAfterSales">保存售后跟进</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'

const route = useRoute()
const router = useRouter()
const activeTab = ref((route.query.tab as string) || 'identification')
const detail = reactive<any>({})
const dicts = reactive<any>({})
const enterprises = ref<any[]>([])
const feedbackUrl = ref('')
const leadId = computed(() => {
  const value = route.params.id
  if (typeof value !== 'string' || !/^\d+$/.test(value)) {
    return null
  }
  return value
})

const identification = reactive<any>({})
const assessment = reactive<any>({})
const match = reactive<any>({})
const delivery = reactive<any>({})
const afterSales = reactive<any>({})

function ensureCandidates() {
  if (!match.candidates || !match.candidates.length) {
    match.candidates = [
      { slotCode: 'A', enterpriseId: undefined, fitScore: 80, priceRange: '' },
      { slotCode: 'B', enterpriseId: undefined, fitScore: 70, priceRange: '' },
      { slotCode: 'C', enterpriseId: undefined, fitScore: 60, priceRange: '' },
    ]
  }
}

function syncCandidate(row: any) {
  const enterprise = enterprises.value.find((item) => item.id === row.enterpriseId)
  if (enterprise) {
    row.enterpriseName = enterprise.name
    row.contactPerson = enterprise.contactPerson
    row.phone = enterprise.phone
    row.serviceArea = enterprise.serviceArea
    row.serviceItems = enterprise.serviceItems
    row.serviceModes = enterprise.serviceModes
    row.responseSpeed = enterprise.responseSpeed
    row.certificationStatus = enterprise.certificationStatus
    row.certificationLevel = enterprise.certificationLevel
    row.expertise = enterprise.expertise
    row.caseExperience = enterprise.caseExperience
    row.priceRange = enterprise.priceRange
    row.serviceTime = enterprise.serviceTime
    row.specialLimit = enterprise.specialLimit
    row.willingToTake = enterprise.willingToTake
  }
}

async function loadDetail() {
  if (!leadId.value) {
    return
  }
  const result: any = await api.leadDetail(leadId.value)
  Object.assign(detail, result)
  Object.assign(identification, result.identification || result.lead || {})
  Object.assign(assessment, result.assessment || {})
  Object.assign(match, result.match || { candidates: [] })
  Object.assign(delivery, result.delivery || {})
  Object.assign(afterSales, result.afterSales || {})
  match.candidates = result.candidates || []
  ensureCandidates()
  feedbackUrl.value = result.feedbackToken ? `${location.origin}/public/feedback/${result.feedbackToken}` : ''
}

async function saveIdentification() {
  if (!leadId.value) return
  await api.saveIdentification(leadId.value, identification)
  ElMessage.success('客户识别已保存')
  await loadDetail()
}

async function saveAssessment() {
  if (!leadId.value) return
  await api.saveAssessment(leadId.value, assessment)
  ElMessage.success('需求评估已保存')
  await loadDetail()
}

async function saveMatch() {
  if (!leadId.value) return
  await api.saveMatch(leadId.value, match)
  ElMessage.success('企业匹配已保存')
  await loadDetail()
}

async function saveDelivery() {
  if (!leadId.value) return
  await api.saveDelivery(leadId.value, delivery)
  ElMessage.success('交付监督已保存')
  await loadDetail()
}

async function createToken() {
  if (!leadId.value) return
  const result: any = await api.createFeedbackToken(leadId.value)
  feedbackUrl.value = `${location.origin}${result.publicUrl}`
  ElMessage.success('回访链接已生成')
}

async function saveAfterSales() {
  if (!leadId.value) return
  await api.saveAfterSales(leadId.value, afterSales)
  ElMessage.success('售后跟进已保存')
  await loadDetail()
}

onMounted(async () => {
  Object.assign(dicts, await api.dictsGrouped())
  enterprises.value = await api.enterprises()
  await loadDetail()
})

watch(
  () => route.query.tab,
  (tab) => {
    if (typeof tab === 'string' && tab) {
      activeTab.value = tab
    }
  },
)

watch(
  () => route.params.id,
  () => {
    if (leadId.value) {
      loadDetail()
    }
  },
)
</script>
