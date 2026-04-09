<template>
  <div class="page-stack">
    <div class="toolbar">
      <div class="toolbar-title">产品套餐包</div>
      <el-button type="primary" @click="openDialog()">新增套餐包</el-button>
    </div>

    <div class="public-summary">
      <div>套餐包可跨企业组合多个产品，适合快速给客户做整包推荐。</div>
      <div>保存前请确保每个条目都选了产品，数量和排序都合理。</div>
    </div>

    <el-table :data="rows" border empty-text="暂无套餐包，请先新增套餐包">
      <el-table-column prop="name" label="套餐名称" min-width="180" />
      <el-table-column prop="applicableScene" label="适用场景" min-width="180" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="itemCount" label="产品数" width="90" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.active ? 'success' : 'info'">{{ row.active ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="包含产品" min-width="240">
        <template #default="{ row }">
          <div class="package-item-preview">
            <el-tag v-for="item in row.items" :key="item.id" class="package-preview-tag">{{ item.productName }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑套餐包' : '新增套餐包'" width="920px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="套餐名称"><el-input v-model="form.name" placeholder="请输入套餐名称" clearable /></el-form-item>
        <el-form-item label="适用场景"><el-input v-model="form.applicableScene" placeholder="请输入适用场景，例如 术后恢复 / 长者照护" clearable /></el-form-item>
        <el-form-item label="参考价格"><el-input v-model="form.price" placeholder="请输入价格，例如 6999" clearable /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.active" /></el-form-item>
      </el-form>
      <el-form :model="form" label-width="110px">
        <el-form-item label="套餐说明"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入套餐特点、适用客户和整体说明" /></el-form-item>
      </el-form>

      <div class="toolbar compact-toolbar">
        <div class="toolbar-title">套餐内容</div>
        <el-button @click="addItem">新增产品</el-button>
      </div>
      <div class="package-editor">
        <div v-for="(item, index) in form.items" :key="index" class="package-editor-row">
          <el-select v-model="item.productId" filterable style="flex: 1" placeholder="请选择产品">
            <el-option v-for="product in products" :key="product.id" :label="`${product.name} / ${product.enterpriseName}`" :value="product.id" />
          </el-select>
          <el-input-number v-model="item.quantity" :min="1" />
          <el-input v-model="item.itemNote" placeholder="条目备注，例如 含一次上门服务" />
          <el-input-number v-model="item.sortOrder" :min="1" />
          <el-button text type="danger" @click="removeItem(index)">删除</el-button>
        </div>
      </div>

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
const products = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({})

function resetForm() {
  Object.assign(form, {
    id: undefined,
    name: '',
    applicableScene: '',
    price: '',
    description: '',
    active: true,
    items: [{
      productId: undefined,
      quantity: 1,
      itemNote: '',
      sortOrder: 1,
    }],
  })
}

async function load() {
  rows.value = await api.productPackages()
}

function openDialog(row?: any) {
  resetForm()
  if (row) {
    Object.assign(form, {
      ...row,
      items: row.items?.map((item: any, index: number) => ({
        productId: item.productId,
        quantity: item.quantity,
        itemNote: item.itemNote,
        sortOrder: item.sortOrder || index + 1,
      })) || [],
    })
  }
  dialogVisible.value = true
}

function addItem() {
  form.items.push({
    productId: undefined,
    quantity: 1,
    itemNote: '',
    sortOrder: form.items.length + 1,
  })
}

function removeItem(index: number | string) {
  form.items.splice(Number(index), 1)
}

async function save() {
  if (isBlank(form.name)) {
    ElMessage.warning('请填写套餐名称')
    return
  }
  if (!form.items.length) {
    ElMessage.warning('请至少添加一个产品')
    return
  }
  const invalidItem = form.items.find((item: any) => !item.productId)
  if (invalidItem) {
    ElMessage.warning('请为每个套餐条目选择具体产品')
    return
  }
  if (form.price !== '' && Number.isNaN(Number(form.price))) {
    ElMessage.warning('请输入有效的套餐价格')
    return
  }
  await api.saveProductPackage({
    ...form,
    price: form.price === '' ? null : form.price,
  })
  ElMessage.success('套餐包已保存')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  products.value = await api.products()
  await load()
})
</script>
