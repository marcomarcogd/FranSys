<template>
  <el-button text type="primary" class="intent-guide-trigger" @click="visible = true">
    {{ buttonText }}
  </el-button>

  <el-dialog v-model="visible" title="客户意向等级判定依据" width="760px">
    <div class="intent-guide-header">
      统一使用以下标准判断客户意向等级，并按建议频率推进跟进动作。
    </div>
    <el-table :data="intentLevelGuideRows" border class="intent-guide-table">
      <el-table-column label="等级" width="96" align="center">
        <template #default="{ row }">
          <el-tag effect="dark" :type="row.tagType || undefined">{{ row.shortLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="criteria" label="判断依据" min-width="360" />
      <el-table-column prop="followFrequency" label="跟进频率" width="170" align="center" />
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { intentLevelGuideRows } from '../constants/ui'

withDefaults(defineProps<{
  buttonText?: string
}>(), {
  buttonText: '查看判定依据',
})

const visible = ref(false)
</script>

<style scoped>
.intent-guide-trigger {
  padding-left: 0;
}

.intent-guide-header {
  margin-bottom: 14px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f5f7fa;
  color: #5f7183;
  line-height: 1.6;
}

.intent-guide-table :deep(.el-table__header-wrapper th) {
  background: #f8f9fb;
}
</style>
