<template>
  <div class="page-stack">
    <div class="public-summary">
      <div>仪表盘聚合客户总量、客户等级和推荐情况，适合日常巡检和工作分配。</div>
      <div>如果统计为空，通常说明当前还没有录入客户、推荐或相关数据。</div>
    </div>
    <div class="stat-grid">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>

    <div class="two-column">
      <el-card shadow="never">
        <template #header>来源渠道分布</template>
        <el-table :data="overview.sourceChannelStats || []" size="small" empty-text="暂无来源渠道统计">
          <el-table-column prop="label" label="渠道" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
      <el-card shadow="never">
        <template #header>客户等级分布</template>
        <el-table :data="overview.customerLevelStats || []" size="small" empty-text="暂无客户等级统计">
          <el-table-column prop="label" label="等级" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
    </div>

    <div class="two-column">
      <el-card shadow="never">
        <template #header>推荐类型分布</template>
        <el-table :data="overview.recommendationTypeStats || []" size="small" empty-text="暂无推荐类型统计">
          <el-table-column prop="label" label="类型" />
          <el-table-column prop="value" label="数量" width="100" />
        </el-table>
      </el-card>
      <el-card shadow="never">
        <template #header>最近建档客户</template>
        <el-table :data="overview.recentCustomers || []" size="small" empty-text="暂无最近建档客户">
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
