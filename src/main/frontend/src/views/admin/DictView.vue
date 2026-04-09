<template>
  <div class="page-stack">
    <div class="toolbar">
      <div class="toolbar-title">字典配置</div>
      <el-button type="primary" @click="openDialog()">新增字典项</el-button>
    </div>
    <div class="public-summary">
      <div>字典项会影响下拉框和筛选项展示，请保持键值稳定、显示名称易懂。</div>
      <div>新增前建议先确认同类型下是否已有重复项。</div>
    </div>
    <el-table :data="rows" border empty-text="暂无字典项，请先新增字典配置">
      <el-table-column prop="dictType" label="字典类型" width="180" />
      <el-table-column prop="itemKey" label="键值" width="180" />
      <el-table-column prop="itemLabel" label="显示名称" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="字典项" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="字典类型"><el-input v-model="form.dictType" placeholder="请输入字典类型，例如 customer_level" clearable /></el-form-item>
        <el-form-item label="键值"><el-input v-model="form.itemKey" placeholder="请输入存储键值，例如 A" clearable /></el-form-item>
        <el-form-item label="显示名称"><el-input v-model="form.itemLabel" placeholder="请输入展示名称，例如 A级客户" clearable /></el-form-item>
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import { isBlank } from '../../constants/ui'

const rows = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({})

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
  await load()
}

onMounted(async () => {
  resetForm()
  await load()
})
</script>
