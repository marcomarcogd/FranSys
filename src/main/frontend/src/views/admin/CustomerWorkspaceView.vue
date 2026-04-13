<template>
  <div class="page-stack">
    <el-card shadow="never" class="filter-card customer-filter-card">
      <div class="section-toolbar">
        <div class="section-title-group">
          <div class="section-title">快速筛选</div>
          <div class="section-subtitle">{{ scopeHint }}</div>
        </div>
        <div class="customer-quick-tags">
          <el-tag type="info">客户 {{ customerQuickStats.total }}</el-tag>
          <el-tag v-if="canViewUnassigned" type="warning">待分配 {{ customerQuickStats.unassigned }}</el-tag>
          <el-tag type="danger">待跟进 {{ customerQuickStats.due }}</el-tag>
          <el-tag type="success">已归档 {{ customerQuickStats.archived }}</el-tag>
          <el-button type="primary" @click="openCustomerDialog()">新建客户</el-button>
        </div>
      </div>

      <el-form :model="filters" class="customer-filter-grid">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="搜索客户编号、姓名、电话或微信" clearable />
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-select v-model="filters.sourceChannel" clearable filterable placeholder="请选择来源渠道">
            <el-option v-for="item in dicts.source_channel || []" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="意向等级">
          <el-select v-model="filters.customerLevel" clearable placeholder="请选择意向等级">
            <el-option v-for="item in intentLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价值等级">
          <el-select v-model="filters.customerValueLevel" clearable placeholder="请选择价值等级">
            <el-option v-for="item in valueLevelOptions" :key="item.value" :label="item.optionLabel" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canManageOwnership" label="负责人">
          <el-select
            v-model="filters.ownerId"
            clearable
            filterable
            placeholder="全部负责人"
            :disabled="filters.unassignedOnly"
          >
            <el-option v-for="user in assignableUsers" :key="user.id" :label="user.displayName" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户状态">
          <el-select v-model="filters.archived" clearable placeholder="全部客户">
            <el-option label="活跃客户" :value="false" />
            <el-option label="已归档" :value="true" />
          </el-select>
        </el-form-item>
        <el-form-item label="最近跟进">
          <el-date-picker
            v-model="filters.lastFollowRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="filters.nextFollowRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>
        <el-form-item v-if="canViewUnassigned" label="包含未分配">
          <el-switch v-model="filters.includeUnassigned" :disabled="filters.unassignedOnly" />
        </el-form-item>
        <el-form-item v-if="canViewUnassigned" label="仅看未分配">
          <el-switch v-model="filters.unassignedOnly" />
        </el-form-item>
        <div class="customer-filter-actions form-item-full">
          <el-button type="primary" @click="loadCustomers">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never" class="workspace-card customer-table-card">
      <template #header>
        <div class="section-toolbar">
          <div class="section-title-group">
            <div class="section-title">客户列表</div>
            <div class="section-subtitle">{{ tableSubtitle }}</div>
          </div>
          <div class="header-actions">
            <el-tag type="info">{{ scopeLabel }}</el-tag>
            <el-tag type="success">{{ `${customers.length} 位客户` }}</el-tag>
          </div>
        </div>
      </template>

      <el-table
        v-loading="listLoading"
        :data="customers"
        row-key="id"
        border
        class="customer-main-table"
        height="calc(100vh - 330px)"
        highlight-current-row
        :current-row-key="selectedCustomerId || undefined"
        empty-text="当前没有符合条件的客户"
        @row-click="selectCustomer"
      >
        <el-table-column fixed="left" label="客户" min-width="250">
          <template #default="{ row }">
            <div class="customer-row-summary">
              <div class="customer-row-head">
                <span class="customer-row-name">{{ row.customerName }}</span>
                <span class="customer-row-code">{{ row.leadNo }}</span>
              </div>
              <div class="customer-row-meta">
                <span>{{ row.contactPhone || '未留电话' }}</span>
                <span>{{ row.region || '地区待补充' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="意向等级" width="110">
          <template #default="{ row }">
            <el-tag effect="plain">{{ customerLevelShortLabel(row.customerLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="价值等级" width="110">
          <template #default="{ row }">
            <el-tag effect="plain" type="success">{{ customerValueLevelShortLabel(row.customerValueLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="负责人" min-width="128">
          <template #default="{ row }">{{ row.ownerName || '未分配' }}</template>
        </el-table-column>
        <el-table-column prop="sourceChannel" label="来源渠道" min-width="130" show-overflow-tooltip />
        <el-table-column label="最近跟进" width="148">
          <template #default="{ row }">{{ formatDateTime(row.lastFollowUpAt, '暂无记录') }}</template>
        </el-table-column>
        <el-table-column label="下次跟进" min-width="176">
          <template #default="{ row }">
            <div class="follow-plan-cell">
              <span>{{ formatDateTime(row.followUpAt, row.archived ? '无需安排' : '未安排') }}</span>
              <el-tag size="small" :type="followPlanInfo(row).type" effect="light">{{ followPlanInfo(row).label }}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="128" fixed="right">
          <template #default="{ row }">
            <el-tag :type="customerStatusType(row)" effect="light">{{ customerStatusText(row) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer
      v-model="detailDrawerVisible"
      direction="rtl"
      size="980px"
      :with-header="false"
      class="customer-detail-drawer"
      @close="handleDrawerClose"
    >
      <div v-loading="detailLoading" class="customer-drawer-shell">
        <template v-if="detail.customer">
          <div class="customer-drawer-header">
            <div class="customer-drawer-header-main">
              <div class="customer-drawer-name-line">
                <div class="customer-drawer-name">{{ detail.customer.customerName }}</div>
                <el-tag effect="plain">{{ `意向 ${customerLevelShortLabel(detail.customer.customerLevel)}` }}</el-tag>
                <el-tag type="success" effect="plain">{{ `价值 ${customerValueLevelShortLabel(detail.customer.customerValueLevel)}` }}</el-tag>
                <el-tag :type="customerStatusType(detail.customer)" effect="light">{{ customerStatusText(detail.customer) }}</el-tag>
              </div>
              <div class="customer-drawer-meta">
                <span>{{ detail.customer.leadNo }}</span>
                <span>{{ detail.customer.contactPhone || '未留联系电话' }}</span>
                <span>{{ detail.customer.ownerName || '未分配负责人' }}</span>
              </div>
              <div class="section-subtitle">{{ detail.followFrequencyHint || customerLevelHint(detail.customer.customerLevel) }}</div>
            </div>
            <div class="customer-drawer-actions">
              <el-button @click="openCustomerDialog(detail.customer)">编辑资料</el-button>
              <el-button v-if="canManageOwnership" @click="openCustomerDialog(detail.customer)">
                {{ detail.customer.ownerId ? '调整归属' : '分配客户' }}
              </el-button>
              <el-button @click="handleDrawerClose">关闭</el-button>
            </div>
          </div>

          <div class="customer-drawer-body">
            <div class="drawer-stat-grid">
              <div class="drawer-stat-card">
                <div class="drawer-stat-label">最近跟进</div>
                <div class="drawer-stat-value drawer-stat-value--compact">{{ formatDateTime(detail.customer.lastFollowUpAt, '暂无记录') }}</div>
              </div>
              <div class="drawer-stat-card">
                <div class="drawer-stat-label">下次跟进</div>
                <div class="drawer-stat-value drawer-stat-value--compact">{{ formatDateTime(detail.customer.followUpAt, '未安排') }}</div>
              </div>
              <div class="drawer-stat-card">
                <div class="drawer-stat-label">跟进记录</div>
                <div class="drawer-stat-value">{{ detail.followRecords?.length || 0 }}</div>
              </div>
              <div class="drawer-stat-card">
                <div class="drawer-stat-label">推荐方案</div>
                <div class="drawer-stat-value">{{ detail.recommendations?.length || 0 }}</div>
              </div>
            </div>

            <el-tabs v-model="detailTab" class="drawer-tabs">
              <el-tab-pane label="客户概览" name="overview">
                <div class="drawer-section-grid">
                  <el-card shadow="never" class="drawer-section-card">
                    <template #header><div class="section-title">基础信息</div></template>
                    <div class="overview-field-grid">
                      <div class="overview-field">
                        <div class="overview-label">客户编号</div>
                        <div class="overview-value">{{ detail.customer.leadNo }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">联系电话</div>
                        <div class="overview-value">{{ detail.customer.contactPhone || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">负责人</div>
                        <div class="overview-value">{{ detail.customer.ownerName || '未分配' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">意向等级</div>
                        <div class="overview-value">{{ customerLevelLabel(detail.customer.customerLevel) }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">价值等级</div>
                        <div class="overview-value">{{ customerValueLevelLabel(detail.customer.customerValueLevel) }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">当前状态</div>
                        <div class="overview-value">{{ customerStatusText(detail.customer) }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">性别</div>
                        <div class="overview-value">{{ detail.customer.gender || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">年龄</div>
                        <div class="overview-value">{{ detail.customer.age || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">邮箱</div>
                        <div class="overview-value">{{ detail.customer.email || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">微信</div>
                        <div class="overview-value">{{ detail.customer.wechatNo || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">地区</div>
                        <div class="overview-value">{{ detail.customer.region || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">建档日期</div>
                        <div class="overview-value">{{ formatDate(detail.customer.entryDate, '未填写') }}</div>
                      </div>
                    </div>
                  </el-card>

                  <el-card shadow="never" class="drawer-section-card">
                    <template #header><div class="section-title">需求与偏好</div></template>
                    <div class="overview-field-grid">
                      <div class="overview-field">
                        <div class="overview-label">来源渠道</div>
                        <div class="overview-value">{{ detail.customer.sourceChannel || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">推荐人</div>
                        <div class="overview-value">{{ detail.customer.referrerName || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">当前需求</div>
                        <div class="overview-value">{{ detail.customer.initialNeedType || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">服务偏好</div>
                        <div class="overview-value">{{ detail.customer.servicePreference || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">预算范围</div>
                        <div class="overview-value">{{ detail.customer.budgetRange || '未填写' }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">最近跟进</div>
                        <div class="overview-value">{{ formatDateTime(detail.customer.lastFollowUpAt, '暂无记录') }}</div>
                      </div>
                      <div class="overview-field">
                        <div class="overview-label">下次跟进</div>
                        <div class="overview-value">{{ formatDateTime(detail.customer.followUpAt, '未安排') }}</div>
                      </div>
                    </div>
                  </el-card>

                  <el-card shadow="never" class="drawer-section-card drawer-section-card--full">
                    <template #header><div class="section-title">补充说明</div></template>
                    <div class="overview-note">
                      {{ detail.customer.remark || '暂无补充说明，可在编辑资料时补充沟通背景、注意事项或服务偏好。' }}
                    </div>
                  </el-card>
                </div>
              </el-tab-pane>

              <el-tab-pane label="跟进记录" name="follow">
                <div class="drawer-pane-stack">
                  <div class="section-toolbar">
                    <div class="section-title-group">
                      <div class="section-title">跟进时间线</div>
                      <div class="section-subtitle">记录每次沟通结果，并安排下一步动作</div>
                    </div>
                    <div class="header-actions">
                      <el-tag type="info">{{ detail.followRecords?.length || 0 }} 条</el-tag>
                      <el-button type="primary" @click="openFollowDialog">新增跟进</el-button>
                    </div>
                  </div>
                  <el-empty v-if="!(detail.followRecords?.length)" description="还没有跟进记录，先补一条本次沟通情况">
                    <el-button type="primary" @click="openFollowDialog">新增跟进</el-button>
                  </el-empty>
                  <el-timeline v-else>
                    <el-timeline-item
                      v-for="item in detail.followRecords || []"
                      :key="item.id"
                      :timestamp="formatDateTime(item.followAt, '未记录时间')"
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
                        <div class="muted-inline">下次跟进：{{ formatDateTime(item.nextFollowUpAt, '未安排') }}</div>
                        <div class="muted-inline">
                          意向等级变化：{{ customerLevelShortLabel(item.levelBefore) }} → {{ customerLevelShortLabel(item.levelAfter) }}
                        </div>
                      </el-card>
                    </el-timeline-item>
                  </el-timeline>
                </div>
              </el-tab-pane>

              <el-tab-pane label="推荐方案" name="recommendation">
                <div class="drawer-pane-stack">
                  <div class="section-toolbar">
                    <div class="section-title-group">
                      <div class="section-title">推荐方案</div>
                      <div class="section-subtitle">可推荐单个产品，也可组合套餐方案</div>
                    </div>
                    <div class="header-actions">
                      <el-tag type="success">{{ detail.recommendations?.length || 0 }} 次</el-tag>
                      <el-button type="success" @click="openRecommendationDialog">新增推荐</el-button>
                    </div>
                  </div>
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
                            <div class="section-subtitle">{{ recommendationMeta(recommendation) }}</div>
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
                        <el-table-column prop="note" label="备注" min-width="140" show-overflow-tooltip />
                      </el-table>
                      <div class="detail-summary muted-inline">{{ recommendation.remark || '无补充说明' }}</div>
                    </el-card>
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="意向等级依据" name="intent">
                <div class="drawer-pane-stack">
                  <div class="intent-reference-summary intent-reference-summary--drawer">
                    <div class="intent-reference-topline">
                      <el-tag :type="selectedIntentRule?.tagType || 'info'" effect="light">
                        {{ selectedIntentRule ? `${selectedIntentRule.shortLabel}${selectedIntentRule.meaning}` : '未设置意向等级' }}
                      </el-tag>
                      <span>{{ selectedIntentRule?.followFrequency || '请先设置意向等级' }}</span>
                    </div>
                    <div class="intent-reference-criteria">{{ selectedIntentRule?.criteria || '还没有可参考的判断依据。' }}</div>
                    <div class="section-subtitle">建议动作：{{ selectedIntentRule?.followAdvice || '建议先确认客户意向，再安排后续动作。' }}</div>
                  </div>
                  <CustomerIntentLevelGuide :selected-level="detail.customer.customerLevel" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </template>
      </div>
    </el-drawer>

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
          <el-select v-model="customerForm.sourceChannel" style="width: 100%" clearable filterable placeholder="请选择">
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
            format="YYYY-MM-DD HH:mm"
            style="width: 100%"
            placeholder="请选择时间"
          />
        </el-form-item>
        <el-form-item label="建档日期">
          <el-date-picker
            v-model="customerForm.entryDate"
            type="date"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
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
          <el-date-picker
            v-model="followForm.followAt"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm"
            style="width: 100%"
            placeholder="请选择时间"
          />
        </el-form-item>
        <el-form-item label="接触方式">
          <el-select v-model="followForm.contactMethod" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in contactMethods" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="followForm.nextFollowUpAt"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm"
            style="width: 100%"
            placeholder="请选择时间"
          />
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
          <div class="editor-row-primary">
            <div class="field-stack editor-type-field">
              <div class="field-label">推荐类型</div>
              <el-select v-model="item.itemType" style="width: 100%" placeholder="请选择类型">
                <el-option label="产品" value="PRODUCT" />
                <el-option label="套餐方案" value="PACKAGE" />
              </el-select>
            </div>
            <div class="field-stack">
              <div class="field-label">{{ item.itemType === 'PACKAGE' ? '套餐方案' : '产品' }}</div>
              <el-select
                v-model="item.itemId"
                filterable
                style="width: 100%"
                placeholder="请选择内容"
                popper-class="wide-select-popper"
              >
                <el-option
                  v-for="option in recommendationOptions(item.itemType)"
                  :key="`${item.itemType}-${option.id}`"
                  :label="recommendationOptionLabel(item.itemType, option)"
                  :value="option.id"
                >
                  <div class="rich-option">
                    <div class="rich-option-title">{{ option.name }}</div>
                    <div class="rich-option-meta">{{ recommendationOptionMeta(item.itemType, option) }}</div>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="editor-row-secondary recommendation-editor-secondary">
            <div class="field-stack">
              <div class="field-label">优先级</div>
              <el-input-number v-model="item.priorityNo" :min="1" :max="99" style="width: 100%" />
            </div>
            <div class="field-stack">
              <div class="field-label">报价</div>
              <el-input v-model="item.quotedPrice" placeholder="如 2980" />
            </div>
            <div class="field-stack field-span-2">
              <div class="field-label">备注</div>
              <el-input v-model="item.note" placeholder="补充报价口径、亮点或注意事项" />
            </div>
            <div class="editor-remove">
              <el-button text type="danger" @click="removeRecommendationItem(index)">删除</el-button>
            </div>
          </div>
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
import { useSystemDicts } from '../../composables/useSystemDicts'
import { useAuthStore } from '../../store/auth'
import CustomerIntentLevelGuide from '../../components/CustomerIntentLevelGuide.vue'
import {
  customerLevelHint,
  customerLevelLabel,
  customerLevelOptionLabel,
  customerLevelOptions,
  customerLevelRule,
  customerLevelShortLabel,
  customerValueLevelLabel,
  customerValueLevelOptionLabel,
  customerValueLevelOptions,
  customerValueLevelShortLabel,
  formatDate,
  formatDateTime,
  formatPriceText,
  isBlank,
  isValidEmail,
  isValidPhone,
  recommendationTypeLabel,
  toDateValue,
} from '../../constants/ui'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { dicts, loadSystemDicts } = useSystemDicts()

const customers = ref<any[]>([])
const detail = reactive<any>({ customer: null, followRecords: [], recommendations: [], followFrequencyHint: '' })
const selectedCustomerId = ref<number | null>(null)
const assignableUsers = ref<any[]>([])
const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const listLoading = ref(false)
const detailTab = ref('overview')

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
const tableSubtitle = computed(() => `${scopeLabel.value}按待跟进优先排序，点击行即可打开客户详情工作台。`)

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
const customerQuickStats = computed(() => {
  const now = Date.now()
  const dayLater = now + 24 * 60 * 60 * 1000
  return customers.value.reduce(
    (acc, item) => {
      acc.total += 1
      if (item.archived) {
        acc.archived += 1
      }
      if (!item.ownerId) {
        acc.unassigned += 1
      }
      const followAt = toDateValue(item.followUpAt)
      if (!item.archived && item.ownerId && followAt && followAt.getTime() <= dayLater) {
        acc.due += 1
      }
      return acc
    },
    { total: 0, archived: 0, unassigned: 0, due: 0 },
  )
})

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

function recommendationOptionLabel(itemType: string, option: any) {
  if (itemType === 'PACKAGE') {
    return `${option.name} · ${option.applicableScene || '未设置场景'} · ${formatPriceText(option.price)}`
  }
  return `${option.name} · ${option.enterpriseName || '未设置企业'} · ${option.specification || '未设置规格'}`
}

function recommendationOptionMeta(itemType: string, option: any) {
  if (itemType === 'PACKAGE') {
    return `${option.applicableScene || '未设置场景'} · 包含 ${option.itemCount || 0} 个产品 · ${formatPriceText(option.price)}`
  }
  return `${option.enterpriseName || '未设置企业'} · ${option.category || '未设置分类'} · ${option.specification || '未设置规格'} · ${formatPriceText(option.price)}`
}

function recommendationMeta(recommendation: any) {
  return [recommendation.ownerName || '未记录负责人', formatDateTime(recommendation.createdAt, '未记录时间')].join(' · ')
}

function customerStatusText(row: any) {
  if (!row) {
    return '未设置'
  }
  if (row.archived) {
    return '已归档'
  }
  if (!row.ownerId) {
    return '待分配'
  }
  return row.currentStatus || '跟进中'
}

function customerStatusType(row: any) {
  if (!row) {
    return 'info'
  }
  if (row.archived) {
    return 'info'
  }
  if (!row.ownerId) {
    return 'warning'
  }
  if ((row.currentStatus || '').includes('待')) {
    return 'warning'
  }
  return 'success'
}

function followPlanInfo(row: any) {
  if (!row?.ownerId) {
    return { label: '待分配', type: 'warning' as const }
  }
  if (row.archived) {
    return { label: '无需跟进', type: 'info' as const }
  }
  const followAt = toDateValue(row.followUpAt)
  if (!followAt) {
    return { label: '待安排', type: 'info' as const }
  }
  const diff = followAt.getTime() - Date.now()
  if (diff < 0) {
    return { label: '已到期', type: 'danger' as const }
  }
  if (diff <= 24 * 60 * 60 * 1000) {
    return { label: '今日待跟进', type: 'warning' as const }
  }
  if (diff <= 3 * 24 * 60 * 60 * 1000) {
    return { label: '三日内跟进', type: 'success' as const }
  }
  return { label: '已排期', type: 'success' as const }
}

async function loadAssignableUsers() {
  if (!canManageOwnership.value) {
    assignableUsers.value = []
    return
  }
  assignableUsers.value = await api.assignableUsers()
}

async function loadCustomers() {
  listLoading.value = true
  try {
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
      detailDrawerVisible.value = false
      Object.assign(detail, { customer: null, followRecords: [], recommendations: [], followFrequencyHint: '' })
      if (route.query.customerId) {
        await router.replace({ path: '/admin/customers' })
      }
    }
  } finally {
    listLoading.value = false
  }
}

async function loadDetail(customerId: number | null) {
  if (!customerId) {
    return
  }
  detailLoading.value = true
  try {
    selectedCustomerId.value = customerId
    Object.assign(detail, await api.customerDetail(customerId))
  } finally {
    detailLoading.value = false
  }
}

async function syncDrawerWithRoute() {
  const customerId = Number(route.query.customerId)
  if (!customerId) {
    detailDrawerVisible.value = false
    detailTab.value = 'overview'
    return
  }
  if (customers.value.length && !customers.value.find((item) => item.id === customerId)) {
    detailDrawerVisible.value = false
    await router.replace({ path: '/admin/customers' })
    return
  }
  try {
    await loadDetail(customerId)
    detailDrawerVisible.value = true
  } catch {
    detailDrawerVisible.value = false
    await router.replace({ path: '/admin/customers' })
  }
}

async function openCustomerDrawer(customerId: number | string, tab = 'overview') {
  const targetId = Number(customerId)
  if (!targetId) {
    return
  }
  detailTab.value = tab
  if (Number(route.query.customerId) === targetId) {
    await loadDetail(targetId)
    detailDrawerVisible.value = true
    return
  }
  await router.replace({ path: '/admin/customers', query: { customerId: String(targetId) } })
}

async function selectCustomer(row: any) {
  if (!row?.id) {
    return
  }
  await openCustomerDrawer(row.id)
}

async function handleDrawerClose() {
  detailDrawerVisible.value = false
  detailTab.value = 'overview'
  if (route.query.customerId) {
    await router.replace({ path: '/admin/customers' })
  }
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
  detailTab.value = 'follow'
  resetFollowForm()
  followDialogVisible.value = true
}

function openRecommendationDialog() {
  if (!selectedCustomerId.value) {
    ElMessage.warning('请先选择客户')
    return
  }
  detailTab.value = 'recommendation'
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
    await openCustomerDrawer(customerForm.id)
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
  detailDrawerVisible.value = true
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
  detailDrawerVisible.value = true
}

async function resetFilters() {
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
  await loadCustomers()
  await syncDrawerWithRoute()
}

function contactMethodLabel(value: string) {
  return contactMethods.find((item) => item.value === value)?.label || value || '未记录方式'
}

onMounted(async () => {
  resetCustomerForm()
  resetFollowForm()
  resetRecommendationForm()
  await loadSystemDicts()
  products.value = await api.products()
  packages.value = await api.productPackages()
  await loadAssignableUsers()
  filters.includeUnassigned = canViewUnassigned.value
  await loadCustomers()
  await syncDrawerWithRoute()
})

watch(
  () => route.query.customerId,
  async () => {
    await syncDrawerWithRoute()
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
