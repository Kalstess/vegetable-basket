<template>
  <div class="route-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆路线填报</span>
        </div>
      </template>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="选择车辆" prop="vehicleId">
          <el-select
            v-model="formData.vehicleId"
            placeholder="请选择车辆"
            style="width: 100%"
            @change="handleVehicleChange"
          >
            <el-option
              v-for="vehicle in vehicles"
              :key="vehicle.id"
              :label="vehicle.plateNumber"
              :value="vehicle.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="路线日期" prop="routeDate">
          <el-date-picker
            v-model="formData.routeDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-divider>路线点信息</el-divider>

        <div v-for="(point, index) in formData.points" :key="index" class="route-point-item">
          <el-card shadow="hover" style="margin-bottom: 16px;">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>路线点 {{ index + 1 }}</span>
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  @click="removePoint(index)"
                  v-if="formData.points.length > 1"
                >
                  删除
                </el-button>
              </div>
            </template>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item
                  :label="`地点类型`"
                  :prop="`points.${index}.pointType`"
                  :rules="pointRules.pointType"
                >
                  <el-select
                    v-model="formData.points[index].pointType"
                    placeholder="请选择类型"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="type in pointTypes"
                      :key="type"
                      :label="type"
                      :value="type"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item
                  :label="`到达时间`"
                  :prop="`points.${index}.arriveTime`"
                  :rules="pointRules.arriveTime"
                >
                  <el-time-picker
                    v-model="formData.points[index].arriveTime"
                    format="HH:mm"
                    value-format="HH:mm"
                    placeholder="选择时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item
              :label="`地址`"
              :prop="`points.${index}.address`"
              :rules="pointRules.address"
            >
              <el-input
                v-model="formData.points[index].address"
                type="textarea"
                :rows="2"
                placeholder="请输入地址"
              />
            </el-form-item>

            <el-form-item label="备注">
              <el-input
                v-model="formData.points[index].description"
                type="textarea"
                :rows="2"
                placeholder="请输入备注（可选）"
              />
            </el-form-item>
          </el-card>
        </div>

        <el-button
          type="primary"
          :icon="Plus"
          @click="addPoint"
          style="width: 100%; margin-bottom: 20px;"
        >
          添加路线点
        </el-button>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交路线
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { routePointApi } from '@/api'
import { vehicleApi } from '@/api'
import dayjs from 'dayjs'

const formRef = ref(null)
const formData = ref({
  vehicleId: null,
  routeDate: dayjs().format('YYYY-MM-DD'),
  points: [
    {
      pointType: '',
      address: '',
      arriveTime: null,
      description: ''
    }
  ]
})
const vehicles = ref([])
const submitting = ref(false)

const pointTypes = [
  '批发市场',
  '配送中心',
  '超市卖场便利店',
  '餐饮店',
  '商场',
  '菜场',
  '其他'
]

const rules = {
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  routeDate: [{ required: true, message: '请选择路线日期', trigger: 'change' }]
}

const pointRules = {
  pointType: [{ required: true, message: '请选择地点类型', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  arriveTime: [{ required: true, message: '请选择到达时间', trigger: 'change' }]
}

onMounted(() => {
  loadVehicles()
})

const loadVehicles = async () => {
  try {
    const data = await vehicleApi.getAll()
    vehicles.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载车辆列表失败')
  }
}

const handleVehicleChange = (vehicleId) => {
  const vehicle = vehicles.value.find(v => v.id === vehicleId)
  if (vehicle) {
    formData.value.vehiclePlateNumber = vehicle.plateNumber
  }
}

const addPoint = () => {
  formData.value.points.push({
    pointType: '',
    address: '',
    arriveTime: null,
    description: ''
  })
}

const removePoint = (index) => {
  formData.value.points.splice(index, 1)
}

// 将时间字符串（HH:mm）转换为分钟数，用于比较
const timeToMinutes = (timeStr) => {
  if (!timeStr) return 0
  const parts = timeStr.split(':')
  if (parts.length >= 2) {
    return parseInt(parts[0]) * 60 + parseInt(parts[1])
  }
  return 0
}

const generateRouteId = () => {
  const date = new Date()
  return `ROUTE_${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}${String(date.getDate()).padStart(2, '0')}_${Date.now()}`
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 先验证主表单
  try {
    await formRef.value.validate()
  } catch (error) {
    // 表单验证失败，Element Plus会自动显示错误信息
    return
  }
  
  // 验证路线点
  if (!formData.value.points || formData.value.points.length === 0) {
    ElMessage.warning('请至少添加一个路线点')
    return
  }
  
  // 手动验证每个路线点的必填项
  let hasError = false
  for (let i = 0; i < formData.value.points.length; i++) {
    const p = formData.value.points[i]
    // 检查地点类型
    if (!p.pointType || (typeof p.pointType === 'string' && !p.pointType.trim())) {
      ElMessage.warning(`路线点${i + 1}：请选择地点类型`)
      hasError = true
      break
    }
    // 检查地址
    if (!p.address || (typeof p.address === 'string' && !p.address.trim())) {
      ElMessage.warning(`路线点${i + 1}：请输入地址`)
      hasError = true
      break
    }
    // 检查到达时间
    if (!p.arriveTime || (typeof p.arriveTime === 'string' && !p.arriveTime.trim())) {
      ElMessage.warning(`路线点${i + 1}：请选择到达时间`)
      hasError = true
      break
    }
  }
  
  if (hasError) {
    return
  }
  
  // 验证到达时间顺序：前面节点的到达时间不能晚于后面节点的时间
  for (let i = 0; i < formData.value.points.length - 1; i++) {
    const currentTime = formData.value.points[i].arriveTime
    const nextTime = formData.value.points[i + 1].arriveTime
    
    if (currentTime && nextTime) {
      // 将时间字符串转换为可比较的格式（HH:mm）
      const currentMinutes = timeToMinutes(currentTime)
      const nextMinutes = timeToMinutes(nextTime)
      
      if (currentMinutes > nextMinutes) {
        ElMessage.warning(`路线点${i + 1}的到达时间（${currentTime}）不能晚于路线点${i + 2}的到达时间（${nextTime}）`)
        hasError = true
        break
      }
    }
  }
  
  if (hasError) {
    return
  }
  
  submitting.value = true
  
  try {
    const routeId = generateRouteId()
    const routePoints = formData.value.points.map((p, idx) => {
      // 将日期时间格式转换为 ISO 格式（T 分隔）：2025-11-15T01:06:00
      const arriveTime = p.arriveTime
        ? `${formData.value.routeDate}T${p.arriveTime}:00`
        : null
      return {
        vehicle: { id: formData.value.vehicleId },
        routeDate: formData.value.routeDate,
        routeId: routeId,
        seq: idx + 1,
        address: p.address.trim(),
        pointType: p.pointType.trim(),
        arriveTime: arriveTime,
        description: (p.description || '').trim()
      }
    })
    
    await routePointApi.createBatch(routePoints)
    ElMessage.success('提交成功')
    handleReset()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  formData.value = {
    vehicleId: null,
    routeDate: dayjs().format('YYYY-MM-DD'),
    points: [
      {
        pointType: '',
        address: '',
        arriveTime: null,
        description: ''
      }
    ]
  }
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.route-form {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .route-point-item {
    margin-bottom: 16px;
  }
}
</style>

