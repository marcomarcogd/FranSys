<template>
  <div class="public-shell">
    <el-card class="public-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <div class="eyebrow">FranSys</div>
            <h2>服务反馈</h2>
          </div>
          <el-tag>{{ context.orderNo || '反馈链接' }}</el-tag>
        </div>
      </template>
      <div class="public-summary">
        <div>客户：{{ context.customerName || '加载中' }}</div>
        <div>服务机构：{{ context.serviceEnterprise || '加载中' }}</div>
        <div>服务内容：{{ context.serviceProject || '加载中' }}</div>
        <div>请根据实际体验填写，我们会认真参考您的反馈。</div>
      </div>
      <el-form :model="form" label-position="top">
        <div class="grid-2">
          <el-form-item label="满意度评分 *">
            <el-rate v-model="form.satisfactionScore" :max="5" />
          </el-form-item>
          <el-form-item label="是否需要调整">
            <el-switch v-model="form.needAdjustment" />
          </el-form-item>
          <el-form-item label="是否愿意继续使用">
            <el-switch v-model="form.willingContinue" />
          </el-form-item>
          <el-form-item label="是否愿意推荐他人">
            <el-switch v-model="form.willingRecommend" />
          </el-form-item>
        </div>
        <el-form-item label="最满意的地方"><el-input v-model="form.mostSatisfiedPoint" placeholder="请填写让您满意的地方" clearable /></el-form-item>
        <el-form-item label="需要改进的地方"><el-input v-model="form.mostDissatisfiedPoint" placeholder="如有需要改进的地方，请直接说明" clearable /></el-form-item>
        <el-form-item label="后续建议"><el-input v-model="form.nextAction" type="textarea" :rows="3" placeholder="可填写后续希望调整的内容或其他建议" /></el-form-item>
        <el-form-item label="是否有新的需求"><el-switch v-model="form.hasNewDemand" /></el-form-item>
        <el-button type="primary" class="full-width" @click="submit">提交反馈</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'

const route = useRoute()
const context = reactive<any>({})
const form = reactive<any>({
  satisfactionScore: 5,
  mostSatisfiedPoint: '',
  mostDissatisfiedPoint: '',
  needAdjustment: false,
  willingContinue: true,
  willingRecommend: true,
  nextAction: '',
  remark: '',
  hasNewDemand: false,
})

async function submit() {
  if (!form.satisfactionScore) {
    ElMessage.warning('请先给出满意度评分')
    return
  }
  await api.submitFeedback(route.params.token as string, form)
  ElMessage.success('反馈已提交，感谢您的配合')
}

onMounted(async () => {
  Object.assign(context, await api.feedbackContext(route.params.token as string))
})
</script>
