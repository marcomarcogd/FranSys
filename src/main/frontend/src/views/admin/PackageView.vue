<template>
  <div class="page-stack">
    <div v-if="canEditSupply" class="page-actions">
      <el-button v-if="canEditSupply" type="primary" @click="openDialog()">新增套餐方案</el-button>
    </div>

    <el-card shadow="never" class="workspace-card">
      <template #header>
        <div class="section-toolbar">
          <div class="section-title-group">
            <div class="section-title">套餐方案列表</div>
            <div class="section-subtitle">把多个产品组合成可直接推荐给客户的整套方案</div>
          </div>
          <el-tag type="info">{{ rows.length }} 个</el-tag>
        </div>
      </template>

      <el-table :data="rows" border empty-text="当前还没有套餐方案">
        <el-table-column prop="name" label="方案名称" min-width="180" />
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
        <el-table-column v-if="canEditSupply" label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑套餐方案' : '新增套餐方案'" width="980px">
      <el-form :model="form" label-width="110px" class="grid-form">
        <el-form-item label="方案名称"><el-input v-model="form.name" placeholder="请输入方案名称" clearable /></el-form-item>
        <el-form-item label="适用场景">
          <el-select v-model="form.applicableScene" filterable clearable style="width: 100%" placeholder="请选择适用场景">
            <el-option v-for="item in sceneOptions" :key="item.id" :label="item.itemLabel" :value="item.itemKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="参考价格"><el-input v-model="form.price" placeholder="请输入价格，例如 6999" clearable /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.active" /></el-form-item>
      </el-form>
      <el-form :model="form" label-width="110px" class="dialog-section">
        <el-form-item label="方案说明"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入方案特点、适用客户和整体说明" /></el-form-item>
      </el-form>

      <div class="section-toolbar compact-toolbar">
        <div class="section-title-group">
          <div class="section-title">方案内容</div>
          <div class="section-subtitle">按顺序添加产品，并补充数量或服务说明</div>
        </div>
        <el-button @click="addItem">新增产品</el-button>
      </div>
      <div class="package-editor">
        <div v-for="(item, index) in form.items" :key="index" class="package-editor-row">
          <div class="editor-row-primary">
            <div class="field-stack">
              <div class="field-label">产品</div>
              <el-select v-model="item.productId" filterable style="width: 100%" placeholder="请选择产品" popper-class="wide-select-popper">
                <el-option
                  v-for="product in products"
                  :key="product.id"
                  :label="productOptionLabel(product)"
                  :value="product.id"
                >
                  <div class="rich-option">
                    <div class="rich-option-title">{{ product.name }}</div>
                    <div class="rich-option-meta">
                      {{ product.enterpriseName || '未设置企业' }} · {{ product.specification || '未设置规格' }} ·
                      {{ product.category || '未设置分类' }} · {{ formatPriceText(product.price) }}
                    </div>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="editor-row-secondary">
            <div class="field-stack">
              <div class="field-label">数量</div>
              <el-input-number v-model="item.quantity" :min="1" style="width: 100%" />
            </div>
            <div class="field-stack">
              <div class="field-label">排序</div>
              <el-input-number v-model="item.sortOrder" :min="1" style="width: 100%" />
            </div>
            <div class="field-stack field-span-2">
              <div class="field-label">备注</div>
              <el-input v-model="item.itemNote" placeholder="例如：含一次上门服务" />
            </div>
            <div class="editor-remove">
              <el-button text type="danger" @click="removeItem(index)">删除</el-button>
            </div>
          </div>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import { formatPriceText, isBlank } from '../../constants/ui'
import { useSystemDicts } from '../../composables/useSystemDicts'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()
const { dicts, loadSystemDicts } = useSystemDicts()
const canEditSupply = computed(() => authStore.user?.roleCode !== 'ROLE_SALES')
const rows = ref<any[]>([])
const products = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({})
const sceneOptions = computed(() => dicts.package_applicable_scene || [])

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

function productOptionLabel(product: any) {
  return `${product.name} · ${product.enterpriseName || '未设置企业'} · ${product.specification || '未设置规格'}`
}

async function save() {
  if (isBlank(form.name)) {
    ElMessage.warning('请填写方案名称')
    return
  }
  if (!form.items.length) {
    ElMessage.warning('请至少添加一个产品')
    return
  }
  const invalidItem = form.items.find((item: any) => !item.productId)
  if (invalidItem) {
    ElMessage.warning('请为每个条目选择具体产品')
    return
  }
  if (form.price !== '' && Number.isNaN(Number(form.price))) {
    ElMessage.warning('请输入有效的方案价格')
    return
  }
  await api.saveProductPackage({
    ...form,
    price: form.price === '' ? null : form.price,
  })
  ElMessage.success('套餐方案已保存')
  dialogVisible.value = false
  await load()
}

onMounted(async () => {
  resetForm()
  await loadSystemDicts()
  products.value = await api.products()
  await load()
})
</script>
