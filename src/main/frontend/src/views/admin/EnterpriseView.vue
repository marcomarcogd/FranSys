<template>
  <div class="page-stack">
    <div class="toolbar">
      <div class="toolbar-title">企业库</div>
      <el-button type="primary" @click="openDialog()">新增企业</el-button>
    </div>
    <el-table :data="rows" border>
      <el-table-column prop="name" label="企业名称" min-width="180" />
      <el-table-column prop="contactPerson" label="联系人" width="120" />
      <el-table-column prop="phone" label="联系方式" width="140" />
      <el-table-column prop="serviceArea" label="服务区域" min-width="180" />
      <el-table-column prop="certificationStatus" label="认证状态" width="100" />
      <el-table-column prop="certificationLevel" label="认证等级" width="100" />
      <el-table-column prop="priceRange" label="价格区间" width="140" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="企业信息" width="720px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="企业名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="服务区域"><el-input v-model="form.serviceArea" /></el-form-item>
        <el-form-item label="服务项目"><el-input v-model="form.serviceItems" /></el-form-item>
        <el-form-item label="服务模式"><el-input v-model="form.serviceModes" /></el-form-item>
        <el-form-item label="响应速度"><el-input v-model="form.responseSpeed" /></el-form-item>
        <el-form-item label="认证状态"><el-input v-model="form.certificationStatus" /></el-form-item>
        <el-form-item label="认证等级"><el-input v-model="form.certificationLevel" /></el-form-item>
        <el-form-item label="擅长领域"><el-input v-model="form.expertise" /></el-form-item>
        <el-form-item label="案例经验"><el-input v-model="form.caseExperience" /></el-form-item>
        <el-form-item label="价格区间"><el-input v-model="form.priceRange" /></el-form-item>
        <el-form-item label="服务时间"><el-input v-model="form.serviceTime" /></el-form-item>
        <el-form-item label="特殊限制"><el-input v-model="form.specialLimit" /></el-form-item>
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
  Object.assign(form, {
    id: undefined,
    name: '',
    contactPerson: '',
    phone: '',
    serviceArea: '',
    serviceItems: '',
    serviceModes: '',
    responseSpeed: '',
    certificationStatus: '',
    certificationLevel: '',
    expertise: '',
    caseExperience: '',
    priceRange: '',
    serviceTime: '',
    specialLimit: '',
    active: true,
    willingToTake: true,
  })
}

function openDialog(row?: any) {
  resetForm()
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function load() {
  rows.value = await api.enterprises()
}

async function save() {
  await api.saveEnterprise(form)
  ElMessage.success('企业信息已保存')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  await load()
})
</script>
