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
      <el-form :model="form" label-position="top">
        <div class="grid-2">
          <el-form-item label="客户姓名"><el-input v-model="form.customerName" /></el-form-item>
          <el-form-item label="联系方式"><el-input v-model="form.contactPhone" /></el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="120" style="width: 100%" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="微信"><el-input v-model="form.wechatNo" /></el-form-item>
          <el-form-item label="地区"><el-input v-model="form.region" /></el-form-item>
          <el-form-item label="家庭结构"><el-input v-model="form.familyStructure" /></el-form-item>
          <el-form-item label="来源渠道"><el-input v-model="form.sourceChannel" placeholder="如自然咨询/熟人推荐" /></el-form-item>
          <el-form-item label="推荐人 / 渠道人"><el-input v-model="form.referrerName" /></el-form-item>
          <el-form-item label="服务对象"><el-input v-model="form.serviceObject" /></el-form-item>
          <el-form-item label="初步需求类型"><el-input v-model="form.initialNeedType" /></el-form-item>
          <el-form-item label="服务偏好"><el-input v-model="form.servicePreference" /></el-form-item>
          <el-form-item label="需求紧急度"><el-input v-model="form.urgency" /></el-form-item>
          <el-form-item label="预算区间"><el-input v-model="form.budgetRange" /></el-form-item>
        </div>
        <el-form-item label="补充说明"><el-input v-model="form.remark" type="textarea" :rows="4" /></el-form-item>
        <el-button type="primary" class="full-width" @click="submit">提交线索</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api/fransys'

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
