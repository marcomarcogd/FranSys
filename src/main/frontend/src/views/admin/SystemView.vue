<template>
  <div class="page-stack">
    <div class="page-actions">
      <el-button type="primary" @click="openDialog()">新增用户</el-button>
    </div>

    <div class="two-column">
      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-toolbar">
            <div class="section-title-group">
              <div class="section-title">账号列表</div>
              <div class="section-subtitle">配置角色、账号等级和直属上级</div>
            </div>
            <el-tag type="info">{{ (meta.users || []).length }} 个</el-tag>
          </div>
        </template>
        <el-table :data="meta.users || []" border empty-text="当前还没有内部账号">
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="displayName" label="姓名" width="120" />
          <el-table-column label="角色" width="110">
            <template #default="{ row }">{{ roleCodeLabel(row.roleCode) }}</template>
          </el-table-column>
          <el-table-column label="等级" width="90">
            <template #default="{ row }">{{ accountLevelLabel(row.accountLevel) }}</template>
          </el-table-column>
          <el-table-column prop="managerDisplayName" label="直属上级" width="120">
            <template #default="{ row }">{{ row.managerDisplayName || '无' }}</template>
          </el-table-column>
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

      <el-card shadow="never" class="workspace-card">
        <template #header>
          <div class="section-title-group">
            <div class="section-title">角色说明</div>
            <div class="section-subtitle">角色决定功能权限，账号等级决定数据范围</div>
          </div>
        </template>
        <el-empty v-if="!(meta.roles || []).length" description="暂无角色信息" />
        <el-timeline v-else>
          <el-timeline-item v-for="role in meta.roles || []" :key="role.id" :timestamp="roleCodeLabel(role.roleCode)">
            <div class="role-name">{{ role.roleName }}</div>
            <div class="muted-text">{{ role.description }}</div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="620px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="用户名"><el-input v-model="form.username" placeholder="请输入登录用户名" clearable /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.displayName" placeholder="请输入用户姓名" clearable /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode" filterable style="width: 100%" placeholder="请选择角色">
            <el-option v-for="role in meta.roles || []" :key="role.id" :label="role.roleName" :value="role.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号等级">
          <el-select v-model="form.accountLevel" filterable style="width: 100%" placeholder="请选择账号等级">
            <el-option label="成员" value="STAFF" />
            <el-option label="主管" value="LEADER" />
          </el-select>
        </el-form-item>
        <el-form-item label="直属上级">
          <el-select v-model="form.managerUserId" clearable filterable style="width: 100%" placeholder="请选择直属上级" popper-class="wide-select-popper">
            <el-option
              v-for="user in managerOptions"
              :key="user.id"
              :label="`${user.displayName} · ${accountLevelLabel(user.accountLevel)}`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="密码">
          <div style="width: 100%">
            <el-input v-model="form.password" placeholder="编辑时留空即可" show-password />
            <div class="field-help">{{ form.id ? '留空表示不修改当前密码。' : '新建账号时必须设置登录密码。' }}</div>
          </div>
        </el-form-item>
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
import { accountLevelLabel, isBlank, roleCodeLabel } from '../../constants/ui'

const meta = reactive<any>({})
const dialogVisible = ref(false)
const form = reactive<any>({})

const managerOptions = computed(() =>
  (meta.users || []).filter((item: any) => {
    if (form.id && item.id === form.id) {
      return false
    }
    return item.roleCode === 'ROLE_ADMIN' || item.accountLevel === 'LEADER'
  }),
)

function resetForm() {
  Object.assign(form, {
    id: undefined,
    username: '',
    displayName: '',
    roleCode: '',
    accountLevel: 'STAFF',
    managerUserId: undefined,
    password: '',
    enabled: true,
  })
}

function openDialog(row?: any) {
  resetForm()
  if (row) {
    Object.assign(form, row, { password: '' })
  }
  dialogVisible.value = true
}

async function load() {
  Object.assign(meta, await api.systemMeta())
}

async function save() {
  if (isBlank(form.username)) {
    ElMessage.warning('请填写用户名')
    return
  }
  if (isBlank(form.displayName)) {
    ElMessage.warning('请填写姓名')
    return
  }
  if (isBlank(form.roleCode)) {
    ElMessage.warning('请选择角色')
    return
  }
  if (isBlank(form.accountLevel)) {
    ElMessage.warning('请选择账号等级')
    return
  }
  if (!form.id && isBlank(form.password)) {
    ElMessage.warning('新建用户时必须设置密码')
    return
  }
  if (form.roleCode === 'ROLE_ADMIN') {
    form.managerUserId = undefined
  }
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
