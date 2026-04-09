<template>
  <div class="page-stack">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-title">产品库</div>
        <el-button type="primary" @click="openDialog()">新增产品</el-button>
      </div>
      <div class="public-summary">
        <div>一个企业可维护多款产品，建议先补齐价格、适用人群、服务流程和监管说明。</div>
        <div>产品图片支持上传，也可直接粘贴已有图片地址。</div>
      </div>
      <el-form :inline="true" :model="filters">
        <el-form-item label="所属企业">
          <el-select v-model="filters.enterpriseId" clearable style="width: 220px" placeholder="请选择所属企业">
            <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="filters.category" placeholder="输入分类关键字筛选" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="rows" border empty-text="暂无产品数据，请先新增产品">
      <el-table-column label="配图" width="100">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" fit="cover" class="product-thumb" />
          <span v-else class="muted-inline">未上传</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="产品名称" min-width="180" />
      <el-table-column prop="enterpriseName" label="所属企业" min-width="160" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="applicablePeople" label="适用人群" min-width="120" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.active ? 'success' : 'info'">{{ row.active ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑产品' : '新增产品'" width="860px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="所属企业">
          <el-select v-model="form.enterpriseId" style="width: 100%" placeholder="请选择所属企业">
            <el-option v-for="item in enterprises" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产品名称"><el-input v-model="form.name" placeholder="请输入产品名称" clearable /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.specification" placeholder="请输入规格，例如 8小时/次" clearable /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" placeholder="请输入产品分类" clearable /></el-form-item>
        <el-form-item label="适用人群"><el-input v-model="form.applicablePeople" placeholder="请输入适用人群" clearable /></el-form-item>
        <el-form-item label="价格"><el-input v-model="form.price" placeholder="请输入价格，例如 2999" clearable /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.active" /></el-form-item>
      </el-form>
      <el-form :model="form" label-width="110px">
        <el-form-item label="配图">
          <div class="upload-block">
            <div class="muted-text">支持上传图片文件；若已有固定地址，也可以直接填写图片 URL。</div>
            <el-upload :show-file-list="false" :http-request="uploadImage">
              <el-button>上传图片</el-button>
            </el-upload>
            <el-image v-if="form.imageUrl" :src="form.imageUrl" fit="cover" class="product-thumb large" />
            <el-input v-model="form.imageUrl" placeholder="请输入图片地址，例如 /uploads/products/xxx.png" clearable />
          </div>
        </el-form-item>
        <el-form-item label="服务流程"><el-input v-model="form.serviceProcess" type="textarea" :rows="3" placeholder="请输入标准服务流程，例如 评估 -> 安排 -> 执行 -> 回访" /></el-form-item>
        <el-form-item label="监管说明"><el-input v-model="form.regulatoryInfo" type="textarea" :rows="3" placeholder="请输入合规、监管或备案相关说明" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入产品卖点、适用场景或补充说明" /></el-form-item>
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
const enterprises = ref<any[]>([])
const dialogVisible = ref(false)
const filters = reactive<any>({ enterpriseId: undefined, category: '' })
const form = reactive<any>({})

function resetForm() {
  Object.assign(form, {
    id: undefined,
    enterpriseId: undefined,
    name: '',
    specification: '',
    category: '',
    applicablePeople: '',
    price: '',
    imageUrl: '',
    serviceProcess: '',
    regulatoryInfo: '',
    description: '',
    active: true,
  })
}

function openDialog(row?: any) {
  resetForm()
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function load() {
  rows.value = await api.products(filters)
}

function resetFilters() {
  Object.assign(filters, { enterpriseId: undefined, category: '' })
  load()
}

async function save() {
  if (!form.enterpriseId) {
    ElMessage.warning('请先选择所属企业')
    return
  }
  if (isBlank(form.name)) {
    ElMessage.warning('请填写产品名称')
    return
  }
  if (form.price !== '' && Number.isNaN(Number(form.price))) {
    ElMessage.warning('请输入有效的产品价格')
    return
  }
  await api.saveProduct({
    ...form,
    price: form.price === '' ? null : form.price,
  })
  ElMessage.success('产品已保存')
  dialogVisible.value = false
  await load()
}

async function uploadImage(option: any) {
  if (!option.file?.type?.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  if (option.file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  const result = await api.uploadProductImage(option.file)
  form.imageUrl = result.url
  option.onSuccess?.(result)
  ElMessage.success('图片上传成功')
}

onMounted(async () => {
  resetForm()
  enterprises.value = await api.enterprises()
  await load()
})
</script>
