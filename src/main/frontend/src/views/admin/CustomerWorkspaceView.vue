<template>
  <div class="page-stack">
    <div class="page-actions">
      <el-button type="primary" @click="openCustomerDialog()">新建客户</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="section-toolbar">
        <div class="section-title-group">
          <div class="section-title">筛选条件</div>
          <div class="section-subtitle">{{ scopeHint }}</div>
        </div>
      </div>
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="客户编号、姓名、电话或微信" clearable />
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="filters.sourceChannel" clearable style="width: 160px" placeholder="请选择">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="意向等级">
          <el-select v-model="filters.customerLevel" clearable style="width: 320px" placeholder="请选择">
            <el-option v-for="item in intentLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价值等级">
          <el-select v-model="filters.customerValueLevel" clearable style="width: 220px" placeholder="请选择">
            <el-option v-for="item in valueLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canManageOwnership" label="负责人">
          <el-select
            v-model="filters.ownerId"
            clearable
            filterable
            style="width: 180px"
            placeholder="全部负责人"
            :disabled="filters.unassignedOnly"
          >
            <el-option v-for="user in assignableUsers" :key="user.id" :label="user.displayName" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canViewUnassigned" label="包含未分配">
          <el-switch v-model="filters.includeUnassigned" :disabled="filters.unassignedOnly" />
        </el-form-item>
        <el-form-item v-if="canViewUnassigned" label="仅看未分配">
          <el-switch v-model="filters.unassignedOnly" />
        </el-form-item>
        <el-form-item label="最近跟进">
          <el-date-picker
            v-model="filters.lastFollowRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
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
        <el-form-item label="状态">
          <el-select v-model="filters.archived" clearable style="width: 140px" placeholder="请选择">
            <el-option label="活跃" :value="false" />
            <el-option label="归档" :value="true" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCustomers">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="customer-workspace">
      <el-card shadow="never" class="list-card">
        <template #header>
          <div class="section-toolbar">
            <div class="section-title-group">
              <div class="section-title">客户列表</div>
              <div class="section-subtitle">{{ scopeLabel }}内的客户按待跟进优先显示</div>
            </div>
            <el-tag type="info">{{ customers.length }} 位</el-tag>
          </div>
        </template>
        <el-table
          :data="customers"
          row-key="id"
          :current-row-key="selectedCustomerId || undefined"
          border
          highlight-current-row
          height="700"
          empty-text="当前没有符合条件的客户"
          @row-click="selectCustomer"
        >
          <el-table-column prop="customerName" label="客户" min-width="110" />
          <el-table-column label="意向" width="82">
            <template #default="{ row }">
              <el-tag effect="plain">{{ customerLevelShortLabel(row.customerLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="价值" width="82">
            <template #default="{ row }">
              <el-tag effect="plain" type="success">{{ customerValueLevelShortLabel(row.customerValueLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sourceChannel" label="来源" min-width="110" show-overflow-tooltip />
          <el-table-column prop="ownerName" label="负责人" width="110">
            <template #default="{ row }">{{ row.ownerName || '未分配' }}</template>
          </el-table-column>
          <el-table-column prop="followUpAt" label="下次跟进" min-width="165" />
          <el-table-column label="状态" width="92">
            <template #default="{ row }">
              <el-tag :type="row.archived ? 'info' : row.ownerId ? 'success' : 'warning'">
                {{ row.archived ? '归档' : row.ownerId ? '活跃' : '待分配' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <div class="customer-detail-column">
        <el-card v-if="!detail.customer" shadow="never" class="workspace-card">
          <el-empty description="请选择一位客户开始查看资料、跟进记录和推荐方案">
            <el-button type="primary" @click="openCustomerDialog()">新建客户</el-button>
          </el-empty>
        </el-card>

        <template v-else>
          <el-card shadow="never" class="workspace-card">
            <template #header>
              <div class="section-toolbar">
                <div class="section-title-group">
                  <div class="customer-name-line">
                    <span class="section-title">{{ detail.customer.customerName }}</span>
                    <el-tag>{{ `意向 ${customerLevelShortLabel(detail.customer.customerLevel)}` }}</el-tag>
                    <el-tag type="success">{{ `价值 ${customerValueLevelShortLabel(detail.customer.customerValueLevel)}` }}</el-tag>
                    <el-tag :type="detail.customer.ownerId ? 'warning' : 'danger'">
                      {{ detail.customer.currentStatus || '待跟进' }}
                    </el-tag>
                  </div>
                  <div class="section-subtitle">{{ detail.followFrequencyHint }}</div>
                </div>
                <div class="header-actions">
                  <el-button @click="openCustomerDialog(detail.customer)">编辑资料</el-button>
                  <el-button v-if="canManageOwnership" @click="openCustomerDialog(detail.customer)">
                    {{ detail.customer.ownerId ? '调整归属' : '分配客户' }}
                  </el-button>
                </div>
              </div>
            </template>

            <el-descriptions :column="3" border>
              <el-descriptions-item label="客户编号">{{ detail.customer.leadNo }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ detail.customer.contactPhone || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ detail.customer.ownerName || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="意向等级">{{ customerLevelLabel(detail.customer.customerLevel) }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ detail.customer.gender || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ detail.customer.age || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ detail.customer.email || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="微信">{{ detail.customer.wechatNo || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="价值等级">{{ customerValueLevelLabel(detail.customer.customerValueLevel) }}</el-descriptions-item>
              <el-descriptions-item label="地区">{{ detail.customer.region || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="来源渠道">{{ detail.customer.sourceChannel || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="当前需求">{{ detail.customer.initialNeedType || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="服务偏好">{{ detail.customer.servicePreference || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="预算范围">{{ detail.customer.budgetRange || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="最近跟进">{{ detail.customer.lastFollowUpAt || '暂无记录' }}</el-descriptions-item>
              <el-descriptions-item label="下次跟进">{{ detail.customer.followUpAt || '未安排' }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ detail.customer.remark || '暂无补充说明' }}</el-descriptions-item>
            </el-descriptions>

            <div class="intent-reference-block">
              <div class="section-toolbar compact-toolbar">
                <div class="section-title-group">
                  <div class="section-title">意向等级判断依据</div>
                  <div class="section-subtitle">当前客户的分级标准与建议跟进频率</div>
                </div>
                <el-button text @click="intentGuideExpanded = !intentGuideExpanded">
                  {{ intentGuideExpanded ? '收起标准' : '查看完整标准' }}
                </el-button>
              </div>

              <div v-if="selectedIntentRule" class="intent-reference-summary">
                <div class="intent-reference-topline">
                  <el-tag :type="selectedIntentRule.tagType || 'info'" effect="light">
                    {{ `${selectedIntentRule.shortLabel}${selectedIntentRule.meaning}` }}
                  </el-tag>
                  <span>{{ selectedIntentRule.followFrequency }}</span>
                </div>
                <div class="intent-reference-criteria">{{ selectedIntentRule.criteria }}</div>
                <div class="section-subtitle">建议动作：{{ selectedIntentRule.followAdvice }}</div>
              </div>

              <CustomerIntentLevelGuide
                v-if="intentGuideExpanded"
                :selected-level="detail.customer.customerLevel"
              />
            </div>
          </el-card>

          <div class="two-column customer-panels">
            <el-card shadow="never" class="workspace-card">
              <template #header>
                <div class="section-toolbar">
                  <div class="section-title-group">
                    <div class="section-title">跟进时间线</div>
                    <div class="section-subtitle">记录每次沟通结果，并安排下一步动作</div>
                  </div>
                  <div class="header-actions">
                    <el-tag type="info">{{ detail.followRecords?.length || 0 }} 条</el-tag>
                    <el-button type="primary" size="small" @click="openFollowDialog">新增跟进</el-button>
                  </div>
                </div>
              </template>
              <el-empty v-if="!(detail.followRecords?.length)" description="还没有跟进记录，先补一条本次沟通情况">
                <el-button type="primary" @click="openFollowDialog">新增跟进</el-button>
              </el-empty>
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
                    <div><strong>下一步：</strong>{{ item.nextAction || '未填写' }}</div>
                    <div class="muted-inline">下次跟进：{{ item.nextFollowUpAt || '未安排' }}</div>
                    <div class="muted-inline">
                      意向等级变化：{{ customerLevelShortLabel(item.levelBefore) }} → {{ customerLevelShortLabel(item.levelAfter) }}
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </el-card>

            <el-card shadow="never" class="workspace-card">
              <template #header>
                <div class="section-toolbar">
                  <div class="section-title-group">
                    <div class="section-title">推荐方案</div>
                    <div class="section-subtitle">可推荐单个产品，也可组合套餐方案</div>
                  </div>
                  <div class="header-actions">
                    <el-tag type="success">{{ detail.recommendations?.length || 0 }} 次</el-tag>
                    <el-button type="success" size="small" @click="openRecommendationDialog">新增推荐</el-button>
                  </div>
                </div>
              </template>
              <el-empty v-if="!(detail.recommendations?.length)" description="还没有推荐方案，可先添加适合客户的产品或套餐">
                <el-button type="success" @click="openRecommendationDialog">新增推荐</el-button>
              </el-empty>
              <div v-else class="recommendation-stack">
                <el-card
                  v-for="recommendation in detail.recommendations || []"
                  :key="recommendation.id"
                  shadow="hover"
                  class="recommendation-card"
                >
                  <template #header>
                    <div class="section-toolbar">
                      <div class="section-title-group">
                        <div class="section-title">{{ recommendation.recommendationReason || '未填写推荐说明' }}</div>
                        <div class="section-subtitle">{{ recommendation.ownerName }} · {{ recommendation.createdAt }}</div>
                      </div>
                    </div>
                  </template>
                  <el-table :data="recommendation.items" size="small" border empty-text="暂无推荐内容">
                    <el-table-column prop="priorityNo" label="优先级" width="84" />
                    <el-table-column label="类型" width="92">
                      <template #default="{ row }">{{ recommendationTypeLabel(row.itemType) }}</template>
                    </el-table-column>
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

    <el-dialog v-model="customerDialogVisible" :title="customerForm.id ? '编辑客户资料' : '新建客户'" width="860px">
      <el-form :model="customerForm" label-width="110px" class="grid-form">
        <el-form-item label="客户姓名"><el-input v-model="customerForm.customerName" placeholder="请输入客户姓名" clearable /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="customerForm.contactPhone" placeholder="请输入联系电话" clearable /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="customerForm.gender" style="width: 100%" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="customerForm.age" :min="0" :max="120" style="width: 100%" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="customerForm.email" placeholder="请输入邮箱地址" clearable /></el-form-item>
        <el-form-item label="微信"><el-input v-model="customerForm.wechatNo" placeholder="请输入微信号" clearable /></el-form-item>
        <el-form-item label="地区"><el-input v-model="customerForm.region" placeholder="例如 上海浦东" clearable /></el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="customerForm.sourceChannel" style="width: 100%" clearable placeholder="请选择">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="意向等级">
          <el-select v-model="customerForm.customerLevel" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in intentLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价值等级">
          <el-select v-model="customerForm.customerValueLevel" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in valueLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="判断依据" class="form-item-full">
          <CustomerIntentLevelGuide :selected-level="customerForm.customerLevel" />
        </el-form-item>
        <el-form-item v-if="canManageOwnership" label="负责人">
          <el-select v-model="customerForm.ownerId" clearable filterable style="width: 100%" placeholder="请选择负责人">
            <el-option v-for="user in assignableUsers" :key="user.id" :label="user.displayName" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前状态"><el-input v-model="customerForm.currentStatus" placeholder="例如 待沟通、方案沟通中" clearable /></el-form-item>
        <el-form-item label="推荐人"><el-input v-model="customerForm.referrerName" placeholder="如有推荐人可填写" clearable /></el-form-item>
        <el-form-item label="当前需求"><el-input v-model="customerForm.initialNeedType" placeholder="请填写客户当前最关心的需求" clearable /></el-form-item>
        <el-form-item label="服务偏好"><el-input v-model="customerForm.servicePreference" placeholder="例如 上门、到店、均可" clearable /></el-form-item>
        <el-form-item label="预算范围"><el-input v-model="customerForm.budgetRange" placeholder="例如 3000-5000" clearable /></el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="customerForm.followUpAt"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
            placeholder="请选择时间"
          />
        </el-form-item>
        <el-form-item label="建档日期">
          <el-date-picker
            v-model="customerForm.entryDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            placeholder="请选择日期"
          />
        </el-form-item>
        <el-form-item label="归档">
          <el-switch v-model="customerForm.archived" />
        </el-form-item>
      </el-form>
      <el-form :model="customerForm" label-width="110px" class="dialog-section">
        <el-form-item label="备注">
          <el-input v-model="customerForm.remark" type="textarea" :rows="3" placeholder="可补充沟通背景、注意事项或特别说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="customerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCustomer">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialogVisible" title="新增跟进" width="760px">
      <el-form :model="followForm" label-width="110px" class="grid-form">
        <el-form-item label="跟进时间">
          <el-date-picker v-model="followForm.followAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" placeholder="请选择时间" />
        </el-form-item>
        <el-form-item label="接触方式">
          <el-select v-model="followForm.contactMethod" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in contactMethods" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker v-model="followForm.nextFollowUpAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" placeholder="请选择时间" />
        </el-form-item>
        <el-form-item label="更新意向等级">
          <el-select v-model="followForm.customerLevel" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in intentLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="判断依据" class="form-item-full">
          <CustomerIntentLevelGuide :selected-level="followForm.customerLevel" />
        </el-form-item>
      </el-form>
      <el-form :model="followForm" label-width="110px">
        <el-form-item label="沟通摘要"><el-input v-model="followForm.communicationSummary" type="textarea" :rows="2" placeholder="概括本次沟通的重点和结论" /></el-form-item>
        <el-form-item label="客户诉求"><el-input v-model="followForm.customerNeed" type="textarea" :rows="2" placeholder="记录客户当前最关心的问题或需求" /></el-form-item>
        <el-form-item label="我方反馈"><el-input v-model="followForm.ourFeedback" type="textarea" :rows="2" placeholder="记录已给出的建议、方案或承诺" /></el-form-item>
        <el-form-item label="客户反馈"><el-input v-model="followForm.customerFeedback" type="textarea" :rows="2" placeholder="记录客户态度、疑虑或接受情况" /></el-form-item>
        <el-form-item label="下一步"><el-input v-model="followForm.nextAction" type="textarea" :rows="2" placeholder="例如 发送方案、约下次沟通、确认报价" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFollowRecord">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recommendationDialogVisible" title="新增推荐方案" width="860px">
      <el-form :model="recommendationForm" label-width="110px">
        <el-form-item label="推荐说明">
          <el-input v-model="recommendationForm.recommendationReason" type="textarea" :rows="2" placeholder="说明本次推荐适合这位客户的原因" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="recommendationForm.remark" type="textarea" :rows="2" placeholder="可补充报价口径、沟通重点或注意事项" />
        </el-form-item>
      </el-form>
      <div class="section-toolbar compact-toolbar">
        <div class="section-title-group">
          <div class="section-title">推荐内容</div>
          <div class="section-subtitle">可同时添加产品和套餐方案，按优先级排序</div>
        </div>
        <el-button @click="addRecommendationItem">新增推荐项</el-button>
      </div>
      <div class="recommendation-editor">
        <div v-for="(item, index) in recommendationForm.items" :key="index" class="recommendation-editor-row">
          <el-select v-model="item.itemType" style="width: 140px" placeholder="请选择类型">
            <el-option label="产品" value="PRODUCT" />
            <el-option label="套餐方案" value="PACKAGE" />
          </el-select>
          <el-select v-model="item.itemId" filterable style="flex: 1" placeholder="请选择内容">
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import { useAuthStore } from '../../store/auth'
import CustomerIntentLevelGuide from '../../components/CustomerIntentLevelGuide.vue'
import {
  customerLevelLabel,
  customerLevelOptionLabel,
  customerLevelOptions,
  customerLevelRule,
  customerLevelShortLabel,
  customerValueLevelLabel,
  customerValueLevelOptionLabel,
  customerValueLevelOptions,
  customerValueLevelShortLabel,
  isBlank,
  isValidEmail,
  isValidPhone,
  recommendationTypeLabel,
} from '../../constants/ui'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const dicts = reactive<any>({})
const customers = ref<any[]>([])
const detail = reactive<any>({ customer: null, followRecords: [], recommendations: [], followFrequencyHint: '' })
const selectedCustomerId = ref<number | null>(null)
const assignableUsers = ref<any[]>([])
const intentGuideExpanded = ref(false)

const customerDialogVisible = ref(false)
const followDialogVisible = ref(false)
const recommendationDialogVisible = ref(false)

const canManageOwnership = computed(() => authStore.user?.roleCode === 'ROLE_ADMIN' || authStore.user?.accountLevel === 'LEADER')
const canViewUnassigned = computed(() => canManageOwnership.value)
const scopeLabel = computed(() => {
  if (authStore.user?.roleCode === 'ROLE_ADMIN') return '全部客户'
  if (authStore.user?.accountLevel === 'LEADER') return '团队客户'
  return '我的客户'
})
const scopeHint = computed(() => {
  if (authStore.user?.roleCode === 'ROLE_ADMIN') return '管理员可查看全部客户，并处理待分配客户。'
  if (authStore.user?.accountLevel === 'LEADER') return '主管可查看自己及下属客户，也可处理待分配客户。'
  return '当前只显示你本人负责的客户。'
})

const filters = reactive<any>({
  keyword: '',
  sourceChannel: '',
  customerLevel: '',
  customerValueLevel: '',
  ownerId: undefined,
  includeUnassigned: true,
  unassignedOnly: false,
  archived: undefined,
  lastFollowRange: [],
  nextFollowRange: [],
})

const intentLevelOptions = customerLevelOptions.map((item) => ({
  ...item,
  optionLabel: customerLevelOptionLabel(item.value),
}))
const valueLevelOptions = customerValueLevelOptions.map((item) => ({
  ...item,
  optionLabel: customerValueLevelOptionLabel(item.value),
}))
const selectedIntentRule = computed(() => customerLevelRule(detail.customer?.customerLevel))

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
    customerValueLevel: 'C',
    currentStatus: canManageOwnership.value ? '待分配' : '待跟进',
    referrerName: '',
    initialNeedType: '',
    servicePreference: '',
    budgetRange: '',
    entryDate: '',
    followUpAt: '',
    ownerId: undefined,
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

async function loadAssignableUsers() {
  if (!canManageOwnership.value) {
    assignableUsers.value = []
    return
  }
  assignableUsers.value = await api.assignableUsers()
}

async function loadCustomers() {
  const params: any = {
    keyword: filters.keyword,
    sourceChannel: filters.sourceChannel,
    customerLevel: filters.customerLevel,
    customerValueLevel: filters.customerValueLevel,
    archived: filters.archived,
  }
  if (canManageOwnership.value) {
    params.ownerId = filters.ownerId
    params.includeUnassigned = filters.includeUnassigned
    params.unassignedOnly = filters.unassignedOnly
  }
  if (filters.lastFollowRange?.length === 2) {
    params.lastFollowStart = filters.lastFollowRange[0]
    params.lastFollowEnd = filters.lastFollowRange[1]
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
  intentGuideExpanded.value = false
  selectedCustomerId.value = customerId
  Object.assign(detail, await api.customerDetail(customerId))
}

async function ensureSelection() {
  const queryId = Number(route.query.customerId)
  if (queryId) {
    try {
      await loadDetail(queryId)
      return
    } catch {
      if (!customers.value.find((item) => item.id === queryId)) {
        await router.replace({ path: '/admin/customers' })
      }
    }
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
    Object.assign(customerForm, row, { ownerId: row.ownerId ?? undefined })
  }
  customerDialogVisible.value = true
}

function openFollowDialog() {
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先选择客户')
    return
  }
  resetFollowForm()
  followDialogVisible.value = true
}

function openRecommendationDialog() {
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先选择客户')
    return
  }
  resetRecommendationForm()
  recommendationDialogVisible.value = true
}

async function saveCustomer() {
  if (isBlank(customerForm.customerName)) {
    ElMessage.warning('请填写客户姓名')
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
  const payload = {
    ...customerForm,
    ownerId: canManageOwnership.value ? customerForm.ownerId ?? null : undefined,
  }
  if (customerForm.id) {
    await api.updateCustomer(customerForm.id, payload)
  } else {
    const saved = await api.createCustomer(payload)
    customerForm.id = saved.id
  }
  ElMessage.success('客户资料已保存')
  customerDialogVisible.value = false
  await loadCustomers()
  if (customerForm.id) {
    await router.replace({ path: '/admin/customers', query: { customerId: String(customerForm.id) } })
    await loadDetail(customerForm.id)
  }
}

async function saveFollowRecord() {
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先选择客户')
    return
  }
  if (isBlank(followForm.communicationSummary)) {
    ElMessage.warning('请填写沟通摘要')
    return
  }
  if (isBlank(followForm.customerNeed)) {
    ElMessage.warning('请补充客户诉求')
    return
  }
  if (isBlank(followForm.nextAction)) {
    ElMessage.warning('请填写下一步安排')
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
    ElMessage.warning('请先选择客户')
    return
  }
  if (!recommendationForm.items.length) {
    ElMessage.warning('请至少添加一个推荐项')
    return
  }
  const invalidItem = recommendationForm.items.find((item: any) => !item.itemType || !item.itemId)
  if (invalidItem) {
    ElMessage.warning('请为每个推荐项选择完整内容')
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
    customerValueLevel: '',
    ownerId: undefined,
    includeUnassigned: canViewUnassigned.value,
    unassignedOnly: false,
    archived: undefined,
    lastFollowRange: [],
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
  await loadAssignableUsers()
  filters.includeUnassigned = canViewUnassigned.value
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
  () => filters.unassignedOnly,
  (value) => {
    if (value) {
      filters.ownerId = undefined
      filters.includeUnassigned = true
    }
  },
)
</script>
