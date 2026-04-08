<template>
  <div class="page-stack">
    <div class="stat-grid">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>

    <div class="two-column">
      <el-card shadow="never">
        <template #header>来源渠道分布</template>
        <el-table :data="overview.sourceChannelStats || []" size="small">
          <el-table-column prop="label" label="渠道" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
      <el-card shadow="never">
        <template #header>满意度分布</template>
        <el-table :data="overview.satisfactionStats || []" size="small">
          <el-table-column prop="label" label="评分" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
    </div>

    <el-card shadow="never">
      <template #header>最新线索</template>
      <el-table :data="overview.recentLeads || []" size="small">
        <el-table-column prop="leadNo" label="线索编号" width="160" />
        <el-table-column prop="customerName" label="客户姓名" />
        <el-table-column prop="sourceChannel" label="来源渠道" />
        <el-table-column prop="currentStatus" label="当前状态" />
        <el-table-column prop="workflowStage" label="流程阶段" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { api } from '../../api/fransys'

const overview = reactive<any>({})

const stats = computed(() => [
  { label: '总线索数', value: overview.totalLeads || 0 },
  { label: '今日新增', value: overview.todayLeads || 0 },
  { label: '已识别', value: overview.identifiedLeads || 0 },
  { label: '已匹配', value: overview.matchedLeads || 0 },
  { label: '交付中', value: overview.deliveringLeads || 0 },
  { label: '售后跟进', value: overview.afterSalesLeads || 0 },
])

onMounted(async () => {
  Object.assign(overview, await api.dashboard())
})
</script>
