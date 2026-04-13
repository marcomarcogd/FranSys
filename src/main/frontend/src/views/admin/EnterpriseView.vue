<template>
  <div class="page-stack">
    <div v-if="canEditSupply" class="page-actions">
      <el-button v-if="canEditSupply" type="primary" @click="openDialog()">新增企业</el-button>
    </div>
    <el-card shadow="never" class="workspace-card">
      <template #header>
        <div class="section-toolbar">
          <div class="section-title-group">
            <div class="section-title">企业列表</div>
            <div class="section-subtitle">维护合作企业的联系人、服务范围和品牌优势</div>
          </div>
          <el-tag type="info">{{ rows.length }} 家</el-tag>
        </div>
      </template>
      <el-table :data="rows" border empty-text="当前还没有企业资料">
        <el-table-column prop="name" label="企业名称" min-width="180" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="phone" label="联系方式" width="140" />
        <el-table-column prop="serviceArea" label="服务区域" min-width="180" />
        <el-table-column prop="brandAdvantage" label="品牌优势" min-width="220" show-overflow-tooltip />
        <el-table-column prop="certificationStatus" label="认证状态" width="100" />
        <el-table-column prop="certificationLevel" label="认证等级" width="100" />
        <el-table-column prop="priceRange" label="价格区间" width="140" />
        <el-table-column v-if="canEditSupply" label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑企业' : '新增企业'" width="920px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="企业名称"><el-input v-model="form.name" placeholder="请输入企业名称" clearable /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" clearable /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.phone" placeholder="请输入联系方式" clearable /></el-form-item>
        <el-form-item label="服务区域"><el-input v-model="form.serviceArea" placeholder="请输入服务区域" clearable /></el-form-item>
        <el-form-item label="服务项目"><el-input v-model="form.serviceItems" placeholder="请输入主要服务项目" clearable /></el-form-item>
        <el-form-item label="服务模式">
          <el-select v-model="form.serviceModes" filterable clearable style="width: 100%" placeholder="请选择服务模式">
            <el-option v-for="item in serviceModeOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="响应速度">
          <el-select v-model="form.responseSpeed" filterable clearable style="width: 100%" placeholder="请选择响应速度">
            <el-option v-for="item in responseSpeedOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select v-model="form.certificationStatus" filterable clearable style="width: 100%" placeholder="请选择认证状态">
            <el-option v-for="item in certificationStatusOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="认证等级">
          <el-select v-model="form.certificationLevel" filterable clearable style="width: 100%" placeholder="请选择认证等级">
            <el-option v-for="item in certificationLevelOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-select v-model="form.expertise" filterable clearable style="width: 100%" placeholder="请选择擅长领域">
            <el-option v-for="item in expertiseOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="案例经验"><el-input v-model="form.caseExperience" placeholder="请输入代表案例或服务经验" clearable /></el-form-item>
        <el-form-item label="价格区间"><el-input v-model="form.priceRange" placeholder="请输入价格区间" clearable /></el-form-item>
        <el-form-item label="服务时间"><el-input v-model="form.serviceTime" placeholder="请输入可服务时间" clearable /></el-form-item>
        <el-form-item label="特殊限制"><el-input v-model="form.specialLimit" placeholder="请输入特殊限制或接单边界" clearable /></el-form-item>
      </el-form>
      <el-form :model="form" label-width="110px" class="dialog-section">
        <el-form-item label="品牌优势"><el-input v-model="form.brandAdvantage" type="textarea" :rows="3" placeholder="请输入企业的品牌优势、服务亮点或差异化说明" /></el-form-item>
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
import { isBlank, isValidPhone } from '../../constants/ui'
import { useSystemDicts } from '../../composables/useSystemDicts'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()
const { dicts, loadSystemDicts } = useSystemDicts()
const canEditSupply = computed(() => authStore.user?.roleCode !== 'ROLE_SALES')
const rows = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({})
const serviceModeOptions = computed(() => dicts.enterprise_service_mode || [])
const responseSpeedOptions = computed(() => dicts.enterprise_response_speed || [])
const certificationStatusOptions = computed(() => dicts.enterprise_certification_status || [])
const certificationLevelOptions = computed(() => dicts.enterprise_certification_level || [])
const expertiseOptions = computed(() => dicts.enterprise_expertise || [])

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
    brandAdvantage: '',
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
  if (isBlank(form.name)) {
    ElMessage.warning('请填写企业名称')
    return
  }
  if (!isBlank(form.phone) && !isValidPhone(form.phone)) {
    ElMessage.warning('请输入有效的联系方式')
    return
  }
  await api.saveEnterprise(form)
  ElMessage.success('企业信息已保存')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  await loadSystemDicts()
  await load()
})
</script>
