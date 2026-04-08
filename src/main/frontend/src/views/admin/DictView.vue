<template>
  <div class="page-stack">
    <div class="toolbar">
      <div class="toolbar-title">字典配置</div>
      <el-button type="primary" @click="openDialog()">新增字典项</el-button>
    </div>
    <el-table :data="rows" border>
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
        <el-form-item label="字典类型"><el-input v-model="form.dictType" /></el-form-item>
        <el-form-item label="键值"><el-input v-model="form.itemKey" /></el-form-item>
        <el-form-item label="显示名称"><el-input v-model="form.itemLabel" /></el-form-item>
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
