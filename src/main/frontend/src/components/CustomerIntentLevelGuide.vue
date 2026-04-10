<template>
  <div class="intent-guide">
    <div class="intent-guide-table">
      <div class="intent-guide-row intent-guide-row--head">
        <div>等级</div>
        <div>判断依据</div>
        <div>跟进频率</div>
      </div>
      <div
        v-for="item in rules"
        :key="item.value"
        class="intent-guide-row"
        :class="{ 'is-active': item.value === selectedLevel }"
      >
        <div class="intent-guide-level">
          <el-tag :type="item.tagType || 'info'" effect="light">
            {{ `${item.shortLabel}${item.meaning}` }}
          </el-tag>
        </div>
        <div class="intent-guide-criteria">{{ item.criteria }}</div>
        <div class="intent-guide-frequency">{{ item.followFrequency }}</div>
      </div>
    </div>

    <div class="intent-guide-cards">
      <div
        v-for="item in rules"
        :key="`${item.value}-card`"
        class="intent-guide-card"
        :class="{ 'is-active': item.value === selectedLevel }"
      >
        <div class="intent-guide-card-head">
          <el-tag :type="item.tagType || 'info'" effect="light">
            {{ `${item.shortLabel}${item.meaning}` }}
          </el-tag>
          <span>{{ item.followFrequency }}</span>
        </div>
        <div class="intent-guide-card-body">{{ item.criteria }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { customerLevelOptions } from '../constants/ui'

defineProps<{
  selectedLevel?: string
}>()

const rules = computed(() => customerLevelOptions)
</script>

<style scoped>
.intent-guide {
  display: grid;
  gap: 12px;
  width: 100%;
}

.intent-guide-table {
  display: grid;
  gap: 8px;
}

.intent-guide-row {
  display: grid;
  grid-template-columns: 160px minmax(0, 1fr) 150px;
  gap: 14px;
  align-items: center;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(248, 244, 237, 0.72);
  border: 1px solid rgba(17, 33, 45, 0.08);
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.intent-guide-row--head {
  background: transparent;
  border: 0;
  padding: 0 2px;
  color: #617285;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.intent-guide-row.is-active,
.intent-guide-card.is-active {
  border-color: rgba(240, 138, 93, 0.5);
  box-shadow: 0 14px 30px rgba(17, 33, 45, 0.08);
  transform: translateY(-1px);
  background: rgba(255, 252, 248, 0.96);
}

.intent-guide-level,
.intent-guide-frequency {
  display: flex;
  align-items: center;
}

.intent-guide-criteria,
.intent-guide-frequency,
.intent-guide-card-body {
  color: #33485c;
  line-height: 1.65;
}

.intent-guide-cards {
  display: none;
}

.intent-guide-card {
  display: grid;
  gap: 10px;
  padding: 16px;
  border-radius: 18px;
  background: rgba(248, 244, 237, 0.72);
  border: 1px solid rgba(17, 33, 45, 0.08);
}

.intent-guide-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  color: #617285;
}

@media (max-width: 960px) {
  .intent-guide-table {
    display: none;
  }

  .intent-guide-cards {
    display: grid;
    gap: 12px;
  }
}
</style>
