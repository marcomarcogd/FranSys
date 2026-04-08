<template>
  <div class="page-stack">
    <div class="toolbar">
      <div class="toolbar-title">用户与角色</div>
      <el-button type="primary" @click="openDialog()">新增用户</el-button>
    </div>

    <div class="two-column">
      <el-card shadow="never">
        <template #header>内部用户</template>
        <el-table :data="meta.users || []" border>
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="displayName" label="姓名" width="120" />
          <el-table-column prop="roleCode" label="角色" />
          <el-table-column prop="enabled" label="启用" width="80">
            <template #default="{ row }">{{ row.enabled ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>角色说明</template>
        <el-timeline>
          <el-timeline-item v-for="role in meta.roles || []" :key="role.id" :timestamp="role.roleCode">
            <div class="role-name">{{ role.roleName }}</div>
            <div class="muted-text">{{ role.description }}</div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="用户信息" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.displayName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode" style="width: 100%">
            <el-option v-for="role in meta.roles || []" :key="role.id" :label="role.roleName" :value="role.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" placeholder="编辑时留空表示不修改" /></el-form-item>
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

const meta = reactive<any>({})
const dialogVisible = ref(false)
const form = reactive<any>({})

function resetForm() {
  Object.assign(form, { id: undefined, username: '', displayName: '', roleCode: '', password: '', enabled: true })
}

function openDialog(row?: any) {
  resetForm()
  if (row) Object.assign(form, row, { password: '' })
  dialogVisible.value = true
}

async function load() {
  Object.assign(meta, await api.systemMeta())
}

async function save() {
  await api.saveUser(form)
  ElMessage.success('用户已保存')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  await load()
})
</script>
