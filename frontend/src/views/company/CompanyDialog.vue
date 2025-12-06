<template>
  <el-dialog
    v-model="dialogVisible"
    :title="formData?.id ? '编辑企业' : '新增企业'"
    width="600px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
    >
      <el-form-item label="企业名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入企业名称" />
      </el-form-item>
      <el-form-item label="企业性质" prop="companyType">
        <el-select v-model="form.companyType" placeholder="请选择企业性质" style="width: 100%;">
          <el-option label="国有" value="国有" />
          <el-option label="民营" value="民营" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="企业地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入企业地址" />
      </el-form-item>
      <el-form-item label="负责人姓名" prop="legalPersonName">
        <el-input v-model="form.legalPersonName" placeholder="请输入负责人姓名" />
      </el-form-item>
      <el-form-item label="负责人电话" prop="legalPersonPhone">
        <el-input v-model="form.legalPersonPhone" placeholder="请输入负责人电话" />
      </el-form-item>
      <el-form-item label="设立时间" prop="establishedDate">
        <el-date-picker
          v-model="form.establishedDate"
          type="date"
          placeholder="请选择设立时间"
          style="width: 100%;"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="经营范围" prop="businessScope">
        <el-input
          v-model="form.businessScope"
          type="textarea"
          :rows="3"
          placeholder="请输入经营范围"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { companyApi } from '@/api'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(props.modelValue)
const formRef = ref(null)

const form = reactive({
  name: '',
  companyType: '民营',
  address: '',
  legalPersonName: '',
  legalPersonPhone: '',
  establishedDate: '',
  businessScope: ''
})

const rules = {
  name: [
    { required: true, message: '请输入企业名称', trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val && props.formData) {
    Object.assign(form, props.formData)
  } else if (val) {
    resetForm()
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

const resetForm = () => {
  Object.assign(form, {
    name: '',
    companyType: '民营',
    address: '',
    legalPersonName: '',
    legalPersonPhone: '',
    establishedDate: '',
    businessScope: ''
  })
  formRef.value?.clearValidate()
}

const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (props.formData?.id) {
      await companyApi.update(props.formData.id, form)
      ElMessage.success('更新成功')
    } else {
      await companyApi.create(form)
      ElMessage.success('创建成功')
    }
    emit('success')
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  }
}
</script>

