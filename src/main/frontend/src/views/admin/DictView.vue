<template>
  <div class="page-stack">
    <div class="page-actions">
      <el-button type="primary" @click="openDialog()">新增字典项</el-button>
    </div>
    <el-card shadow="never" class="workspace-card">
      <template #header>
        <div class="section-toolbar">
          <div class="section-title-group">
            <div class="section-title">字典项列表</div>
            <div class="section-subtitle">维护系统中的下拉选项和基础配置</div>
          </div>
          <el-tag type="info">{{ rows.length }} 项</el-tag>
        </div>
      </template>
      <el-table :data="rows" border empty-text="当前还没有字典项">
        <el-table-column label="字典类型" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ dictTypeLabel(row.dictType) }}</template>
        </el-table-column>
        <el-table-column prop="itemKey" label="键值" width="180" />
        <el-table-column prop="itemLabel" label="显示名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="字典项" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="字典类型">
          <el-select v-model="form.dictType" filterable style="width: 100%" placeholder="请选择字典类型">
            <el-option v-for="item in dictTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="键值"><el-input v-model="form.itemKey" placeholder="请输入内部键值" clearable /></el-form-item>
        <el-form-item label="显示名称"><el-input v-model="form.itemLabel" placeholder="请输入界面展示名称" clearable /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="1" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import { dictTypeLabel, isBlank, uniqueDictTypeOptions } from '../../constants/ui'
import { clearSystemDictsCache } from '../../composables/useSystemDicts'

const rows = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({})
const dictTypeOptions = computed(() => uniqueDictTypeOptions(rows.value.map((item) => item.dictType)))

function resetForm() {
  Object.assign(form, { id: undefined, dictType: '', itemKey: '', itemLabel: '', sortOrder: 1, enabled: true })
}

function openDialog(row?: any) {
  resetForm()
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function load() {
  rows.value = await api.dicts()
}

async function save() {
  if (isBlank(form.dictType)) {
    ElMessage.warning('请填写字典类型')
    return
  }
  if (isBlank(form.itemKey)) {
    ElMessage.warning('请填写字典键值')
    return
  }
  if (isBlank(form.itemLabel)) {
    ElMessage.warning('请填写显示名称')
    return
  }
  await api.saveDict(form)
  ElMessage.success('字典项已保存')
  dialogVisible.value = false
  clearSystemDictsCache()
  await load()
}

onMounted(async () => {
  resetForm()
  await load()
})
</script>
