<template>
  <div class="page-stack">
    <div class="stat-grid">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>

    <div class="two-column dashboard-panels">
      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-toolbar">
            <div class="section-title-group">
              <div class="section-title">待跟进客户</div>
              <div class="section-subtitle">优先处理已到期或即将跟进的客户</div>
            </div>
            <el-tag type="warning">{{ overview.dueFollowCount || 0 }} 位</el-tag>
          </div>
        </template>
        <el-table
          :data="overview.dueFollowCustomers || []"
          size="small"
          class="mini-table"
          empty-text="当前没有待跟进客户"
        >
          <el-table-column prop="customerName" label="客户" min-width="120">
            <template #default="{ row }">
              <span class="table-link" @click="openCustomer(row.id)">{{ row.customerName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="等级" width="80">
            <template #default="{ row }">{{ customerLevelShortLabel(row.customerLevel) }}</template>
          </el-table-column>
          <el-table-column prop="ownerName" label="负责人" width="110" />
          <el-table-column prop="followUpAt" label="计划跟进" min-width="160" />
        </el-table>
      </el-card>

      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-toolbar">
            <div class="section-title-group">
              <div class="section-title">最近新增客户</div>
              <div class="section-subtitle">帮助快速进入最近录入的客户资料</div>
            </div>
            <el-tag type="info">{{ (overview.recentCustomers || []).length }} 位</el-tag>
          </div>
        </template>
        <el-table
          :data="overview.recentCustomers || []"
          size="small"
          class="mini-table"
          empty-text="暂无最近新增客户"
        >
          <el-table-column prop="leadNo" label="客户编号" width="148" />
          <el-table-column prop="customerName" label="客户" min-width="110">
            <template #default="{ row }">
              <span class="table-link" @click="openCustomer(row.id)">{{ row.customerName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="sourceChannel" label="来源" min-width="100" />
          <el-table-column label="等级" width="80">
            <template #default="{ row }">{{ customerLevelShortLabel(row.customerLevel) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <div class="three-column">
      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-title">来源分布</div>
        </template>
        <el-table :data="overview.sourceChannelStats || []" size="small" class="mini-table" empty-text="暂无数据">
          <el-table-column prop="label" label="渠道" />
          <el-table-column prop="value" label="数量" width="90" />
        </el-table>
      </el-card>

      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-title">客户等级</div>
        </template>
        <el-table :data="customerLevelStats" size="small" class="mini-table" empty-text="暂无数据">
          <el-table-column prop="label" label="等级" />
          <el-table-column prop="value" label="数量" width="90" />
        </el-table>
      </el-card>

      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-title">推荐类型</div>
        </template>
        <el-table :data="recommendationStats" size="small" class="mini-table" empty-text="暂无数据">
          <el-table-column prop="label" label="类型" />
          <el-table-column prop="value" label="数量" width="90" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../../api/fransys'
import { customerLevelShortLabel, recommendationTypeLabel } from '../../constants/ui'

const router = useRouter()
const overview = reactive<any>({})

const stats = computed(() => [
  { label: '客户总数', value: overview.totalCustomers || 0 },
  { label: '今日新增', value: overview.todayCustomers || 0 },
  { label: 'A级客户', value: overview.aLevelCustomers || 0 },
  { label: 'B级客户', value: overview.bLevelCustomers || 0 },
  { label: '待跟进', value: overview.dueFollowCount || 0 },
  { label: '已归档', value: overview.archivedCustomers || 0 },
])

const recommendationStats = computed(() =>
  (overview.recommendationTypeStats || []).map((item: any) => ({
    ...item,
    label: recommendationTypeLabel(item.label),
  })),
)

const customerLevelStats = computed(() =>
  (overview.customerLevelStats || []).map((item: any) => ({
    ...item,
    label: customerLevelShortLabel(item.label),
  })),
)

function openCustomer(customerId: number | string) {
  router.push({ path: '/admin/customers', query: { customerId: String(customerId) } })
}

onMounted(async () => {
  Object.assign(overview, await api.dashboard())
})
</script>
