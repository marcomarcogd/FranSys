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
        <template #header>客户等级分布</template>
        <el-table :data="overview.customerLevelStats || []" size="small">
          <el-table-column prop="label" label="等级" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
    </div>

    <div class="two-column">
      <el-card shadow="never">
        <template #header>推荐类型分布</template>
        <el-table :data="overview.recommendationTypeStats || []" size="small">
          <el-table-column prop="label" label="类型" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
      <el-card shadow="never">
        <template #header>最近建档客户</template>
        <el-table :data="overview.recentCustomers || []" size="small">
          <el-table-column prop="leadNo" label="客户编号" width="160" />
          <el-table-column prop="customerName" label="客户姓名" />
          <el-table-column prop="sourceChannel" label="来源渠道" />
          <el-table-column prop="customerLevel" label="客户等级" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { api } from '../../api/fransys'

const overview = reactive<any>({})

const stats = computed(() => [
  { label: '客户总数', value: overview.totalCustomers || 0 },
  { label: '今日建档', value: overview.todayCustomers || 0 },
  { label: 'A级客户', value: overview.aLevelCustomers || 0 },
  { label: 'B级客户', value: overview.bLevelCustomers || 0 },
  { label: '到期待跟进', value: overview.dueFollowCount || 0 },
  { label: '已归档', value: overview.archivedCustomers || 0 },
])

onMounted(async () => {
  Object.assign(overview, await api.dashboard())
})
</script>
