<template>
  <div class="public-shell">
    <el-card class="public-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <div class="eyebrow">FranSys Public</div>
            <h2>客户线索提交</h2>
          </div>
          <el-tag type="success">免登录填写</el-tag>
        </div>
      </template>
      <div class="public-summary">
        <div>请尽量填写完整联系方式和当前需求，我们会据此更快安排回访。</div>
        <div>带 * 的核心信息建议优先填写，提交后内部人员会尽快联系您。</div>
      </div>
      <el-form :model="form" label-position="top">
        <div class="grid-2">
          <el-form-item label="客户姓名 *"><el-input v-model="form.customerName" placeholder="请输入您的姓名" clearable /></el-form-item>
          <el-form-item label="联系方式 *"><el-input v-model="form.contactPhone" placeholder="请输入手机或常用联系电话" clearable /></el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" style="width: 100%" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="120" style="width: 100%" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱，便于发送方案" clearable /></el-form-item>
          <el-form-item label="微信"><el-input v-model="form.wechatNo" placeholder="请输入微信号" clearable /></el-form-item>
          <el-form-item label="地区"><el-input v-model="form.region" placeholder="请输入所在地区" clearable /></el-form-item>
          <el-form-item label="家庭结构"><el-input v-model="form.familyStructure" placeholder="例如 三口之家 / 与父母同住" clearable /></el-form-item>
          <el-form-item label="来源渠道"><el-input v-model="form.sourceChannel" placeholder="如 自然咨询 / 转介绍 / 社交媒体" clearable /></el-form-item>
          <el-form-item label="推荐人 / 渠道人"><el-input v-model="form.referrerName" placeholder="如有推荐人可填写" clearable /></el-form-item>
          <el-form-item label="服务对象"><el-input v-model="form.serviceObject" placeholder="例如 本人 / 父母 / 长辈" clearable /></el-form-item>
          <el-form-item label="初步需求类型"><el-input v-model="form.initialNeedType" placeholder="请简要描述当前需求" clearable /></el-form-item>
          <el-form-item label="服务偏好"><el-input v-model="form.servicePreference" placeholder="例如 上门 / 到店 / 都可以" clearable /></el-form-item>
          <el-form-item label="需求紧急度"><el-input v-model="form.urgency" placeholder="例如 高 / 中 / 低" clearable /></el-form-item>
          <el-form-item label="预算区间"><el-input v-model="form.budgetRange" placeholder="例如 3000-5000" clearable /></el-form-item>
        </div>
        <el-form-item label="补充说明"><el-input v-model="form.remark" type="textarea" :rows="4" placeholder="可补充病史、家庭情况、希望的服务安排等" /></el-form-item>
        <el-button type="primary" class="full-width" @click="submit">提交线索</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'
import { isBlank, isValidEmail, isValidPhone } from '../../constants/ui'

const form = reactive<any>({
  customerName: '',
  contactPhone: '',
  gender: '',
  age: undefined,
  email: '',
  wechatNo: '',
  region: '',
  familyStructure: '',
  sourceChannel: '自然咨询',
  referrerName: '',
  serviceObject: '',
  initialNeedType: '',
  servicePreference: '',
  urgency: '',
  budgetRange: '',
  remark: '',
})

async function submit() {
  if (isBlank(form.customerName)) {
    ElMessage.warning('请填写客户姓名')
    return
  }
  if (!isValidPhone(form.contactPhone)) {
    ElMessage.warning('请输入有效的联系方式')
    return
  }
  if (!isValidEmail(form.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }
  await api.submitPublicLead(form)
  ElMessage.success('提交成功，我们会尽快联系您')
  Object.assign(form, {
    customerName: '',
    contactPhone: '',
    gender: '',
    age: undefined,
    email: '',
    wechatNo: '',
    region: '',
    familyStructure: '',
    sourceChannel: '自然咨询',
    referrerName: '',
    serviceObject: '',
    initialNeedType: '',
    servicePreference: '',
    urgency: '',
    budgetRange: '',
    remark: '',
  })
}
</script>
