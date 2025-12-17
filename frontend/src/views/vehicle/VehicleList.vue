<template>
  <div class="vehicle-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <div>
            <el-button @click="handleExportTemplate">
              <el-icon><Download /></el-icon>
              下载模板
            </el-button>
            <el-button @click="handleImport">
              <el-icon><Upload /></el-icon>
              导入Excel
            </el-button>
            <el-button @click="handleExport">
              <el-icon><Download /></el-icon>
              导出Excel
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增车辆
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-select v-model="searchCompanyId" placeholder="选择企业" clearable style="width: 200px;">
          <el-option
              v-for="company in companies"
              :key="company.id"
              :label="company.name"
              :value="company.id"
          />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-alert
          type="info"
          :closable="false"
          style="margin-left: 20px; flex: 1;"
        >
          <template #title>
            <span style="font-size: 12px;">提示：支持Excel批量导入，请先下载模板，填写后上传。购置时间格式：YYYY-MM-DD（如：2020-01-01）</span>
          </template>
        </el-alert>
      </div>

      <!-- 数据表 -->
      <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%; margin-top: 20px;"
          border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="plateNumber" label="车牌号" width="120" />
        <el-table-column prop="companyName" label="所属企业" min-width="150" />
        <el-table-column prop="vehicleCategory" label="车辆类别" width="140" />
        <el-table-column prop="vehicleType" label="车辆类型" width="100" />
        <el-table-column prop="colorPlate" label="车牌颜色" width="100" />
        <el-table-column prop="emissionStandard" label="排放标准" width="100" />
        <el-table-column prop="approvedLoad" label="核定载质量(吨)" width="130" />
        <el-table-column prop="purchaseDate" label="购置时间" width="120" />
        <el-table-column prop="isActive" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="currentRow ? '编辑车辆' : '新增车辆'"
        width="700px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="所属企业" prop="companyId">
          <el-select v-model="formData.companyId" placeholder="请选择企业" style="width: 100%;">
            <el-option
                v-for="company in companies"
                :key="company.id"
                :label="company.name"
                :value="company.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="车牌号" prop="plateNumber">
          <el-input v-model="formData.plateNumber" placeholder="请输入车牌号" />
        </el-form-item>

        <el-form-item label="车辆类别" prop="vehicleCategory">
          <el-select v-model="formData.vehicleCategory" placeholder="请选择类别" style="width: 100%;">
            <el-option label="菜篮子工程车" value="菜篮子工程车" />
            <el-option label="非菜篮子工程车" value="非菜篮子工程车" />
          </el-select>
        </el-form-item>

        <el-form-item label="车辆类型" prop="vehicleType">
          <el-select v-model="formData.vehicleType" placeholder="请选择类型" style="width: 100%;">
            <el-option label="普通" value="普通" />
            <el-option label="冷藏" value="冷藏" />
          </el-select>
        </el-form-item>

        <el-form-item label="车牌颜色" prop="colorPlate">
          <el-select v-model="formData.colorPlate" placeholder="请选择颜色" style="width: 100%;">
            <el-option label="蓝牌" value="蓝牌" />
            <el-option label="黄牌" value="黄牌" />
            <el-option label="绿牌" value="绿牌" />
          </el-select>
        </el-form-item>

        <el-form-item label="排放标准" prop="emissionStandard">
          <el-input v-model="formData.emissionStandard" placeholder="如：国五 / 国六 / 新能源" />
        </el-form-item>

        <el-form-item label="核定载质量(吨)" prop="approvedLoad">
          <el-input-number v-model="formData.approvedLoad" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="购置时间" prop="purchaseDate">
          <el-date-picker
              v-model="formData.purchaseDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="车架号(VIN)" prop="vin">
          <el-input v-model="formData.vin" placeholder="输入车架号" />
        </el-form-item>

        <el-form-item label="发动机号" prop="engineNo">
          <el-input v-model="formData.engineNo" placeholder="输入发动机号" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Upload } from '@element-plus/icons-vue'
import { vehicleApi, companyApi } from '@/api'
import { exportToExcel, importFromExcel, createExcelTemplate } from '@/utils/excel'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const searchCompanyId = ref(null)
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)

const formData = ref({
  companyId: null,
  plateNumber: '',
  vehicleCategory: '菜篮子工程车',
  vehicleType: '普通',
  colorPlate: '蓝牌',
  emissionStandard: '',
  approvedLoad: 0,
  purchaseDate: '',
  vin: '',
  engineNo: ''
})

const rules = {
  companyId: [{ required: true, message: '请选择所属企业', trigger: 'change' }],
  plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  vehicleCategory: [{ required: true, message: '请选择车辆类别', trigger: 'change' }],
  vehicleType: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
  colorPlate: [{ required: true, message: '请选择车牌颜色', trigger: 'change' }]
}

onMounted(() => {
  loadCompanies()
  loadData()
})

const loadCompanies = async () => {
  try {
    companies.value = await companyApi.getAll()
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    if (searchCompanyId.value) {
      tableData.value = await vehicleApi.getByCompanyId(searchCompanyId.value)
    } else {
      tableData.value = await vehicleApi.getAll()
    }
  } catch (error) {
    ElMessage.error('加载车辆数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchCompanyId.value = null
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  formData.value = {
    companyId: null,
    plateNumber: '',
    vehicleCategory: '菜篮子工程车',
    vehicleType: '普通',
    colorPlate: '蓝牌',
    emissionStandard: '',
    approvedLoad: 0,
    purchaseDate: '',
    vin: '',
    engineNo: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = row
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentRow.value) {
          await vehicleApi.update(currentRow.value.id, formData.value)
          ElMessage.success('更新成功')
        } else {
          await vehicleApi.create(formData.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除车辆 [${row.plateNumber}] 吗？`, '提示', { type: 'warning' })
    await vehicleApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// Excel导入导出功能
const handleExportTemplate = () => {
  const columns = [
    { key: 'companyName', label: '所属企业', required: true, example: '示例企业名称' },
    { key: 'plateNumber', label: '车牌号', required: true, example: '京A12345' },
    { key: 'vehicleCategory', label: '车辆类别', required: true, example: '菜篮子工程车' },
    { key: 'vehicleType', label: '车辆类型', required: true, example: '普通' },
    { key: 'colorPlate', label: '车牌颜色', required: true, example: '蓝牌' },
    { key: 'emissionStandard', label: '排放标准', required: false, example: '国五' },
    { key: 'approvedLoad', label: '核定载质量(吨)', required: false, example: '5.00' },
    { key: 'purchaseDate', label: '购置时间', required: false, example: '2020-01-01' },
    { key: 'vin', label: '车架号(VIN)', required: false, example: 'LSGXXXXXXXXXXXXX' },
    { key: 'engineNo', label: '发动机号', required: false, example: 'XXXXXXXX' }
  ]
  createExcelTemplate(columns, '车辆管理模板.xlsx')
  ElMessage.success('模板下载成功')
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    try {
      ElMessage.info('正在解析Excel文件...')
      const excelData = await importFromExcel(file)
      
      if (excelData.length === 0) {
        ElMessage.warning('Excel文件中没有数据')
        return
      }
      
      // 验证和转换数据
      const errors = []
      const successData = []
      
      for (let i = 0; i < excelData.length; i++) {
        const row = excelData[i]
        const rowNum = i + 3 // Excel行号（从3开始，因为第1行是表头，第2行是必填/可选标记）
        const errorsInRow = []
        
        // 验证必填项
        if (!row['所属企业']) errorsInRow.push('所属企业不能为空')
        if (!row['车牌号']) errorsInRow.push('车牌号不能为空')
        if (!row['车辆类别']) errorsInRow.push('车辆类别不能为空')
        if (!row['车辆类型']) errorsInRow.push('车辆类型不能为空')
        if (!row['车牌颜色']) errorsInRow.push('车牌颜色不能为空')
        
        if (errorsInRow.length > 0) {
          errors.push(`第${rowNum}行: ${errorsInRow.join('; ')}`)
          continue
        }
        
        // 查找企业ID
        const company = companies.value.find(c => c.name === row['所属企业'])
        if (!company) {
          errors.push(`第${rowNum}行: 找不到企业"${row['所属企业']}"`)
          continue
        }
        
        // 处理日期格式
        let purchaseDate = null
        if (row['购置时间']) {
          // 支持多种日期格式
          const dateStr = String(row['购置时间']).trim()
          if (dateStr) {
            // 尝试解析日期
            let date = null
            // Excel日期可能是数字（从1900-01-01开始的天数）
            if (typeof row['购置时间'] === 'number') {
              // Excel日期序列号转换
              const excelEpoch = new Date(1899, 11, 30)
              date = new Date(excelEpoch.getTime() + row['购置时间'] * 24 * 60 * 60 * 1000)
            } else {
              // 尝试解析字符串日期
              date = dayjs(dateStr).isValid() ? dayjs(dateStr).toDate() : null
            }
            if (date && !isNaN(date.getTime())) {
              purchaseDate = dayjs(date).format('YYYY-MM-DD')
            } else {
              errors.push(`第${rowNum}行: 购置时间格式错误，请使用YYYY-MM-DD格式`)
              continue
            }
          }
        }
        
        // 验证枚举值
        const validCategories = ['菜篮子工程车', '非菜篮子工程车']
        if (!validCategories.includes(row['车辆类别'])) {
          errors.push(`第${rowNum}行: 车辆类别必须是"菜篮子工程车"或"非菜篮子工程车"`)
          continue
        }
        
        const validTypes = ['普通', '冷藏']
        if (!validTypes.includes(row['车辆类型'])) {
          errors.push(`第${rowNum}行: 车辆类型必须是"普通"或"冷藏"`)
          continue
        }
        
        const validColors = ['蓝牌', '黄牌', '绿牌']
        if (!validColors.includes(row['车牌颜色'])) {
          errors.push(`第${rowNum}行: 车牌颜色必须是"蓝牌"、"黄牌"或"绿牌"`)
          continue
        }
        
        // 构建数据对象
        const vehicleData = {
          companyId: company.id,
          plateNumber: String(row['车牌号']).trim(),
          vehicleCategory: row['车辆类别'],
          vehicleType: row['车辆类型'],
          colorPlate: row['车牌颜色'],
          emissionStandard: row['排放标准'] ? String(row['排放标准']).trim() : '',
          approvedLoad: row['核定载质量(吨)'] ? parseFloat(row['核定载质量(吨)']) : 0,
          purchaseDate: purchaseDate,
          vin: row['车架号(VIN)'] ? String(row['车架号(VIN)']).trim() : '',
          engineNo: row['发动机号'] ? String(row['发动机号']).trim() : ''
        }
        
        successData.push(vehicleData)
      }
      
      // 显示错误信息
      if (errors.length > 0) {
        ElMessage.warning(`导入完成，但有${errors.length}条错误：\n${errors.slice(0, 5).join('\n')}${errors.length > 5 ? '\n...' : ''}`)
      }
      
      // 批量导入数据
      if (successData.length > 0) {
        ElMessage.info(`正在导入${successData.length}条数据...`)
        let successCount = 0
        let failCount = 0
        
        for (const data of successData) {
          try {
            await vehicleApi.create(data)
            successCount++
          } catch (error) {
            failCount++
            console.error('导入失败:', data, error)
          }
        }
        
        ElMessage.success(`导入完成：成功${successCount}条，失败${failCount}条`)
        loadData()
      }
    } catch (error) {
      ElMessage.error('导入失败: ' + error.message)
    }
  }
  input.click()
}

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  const columns = [
    { key: 'companyName', label: '所属企业', formatter: (val) => val || '' },
    { key: 'plateNumber', label: '车牌号', formatter: (val) => val || '' },
    { key: 'vehicleCategory', label: '车辆类别', formatter: (val) => val || '' },
    { key: 'vehicleType', label: '车辆类型', formatter: (val) => val || '' },
    { key: 'colorPlate', label: '车牌颜色', formatter: (val) => val || '' },
    { key: 'emissionStandard', label: '排放标准', formatter: (val) => val || '' },
    { key: 'approvedLoad', label: '核定载质量(吨)', formatter: (val) => val ?? '' },
    { key: 'purchaseDate', label: '购置时间', formatter: (val) => val ? dayjs(val).format('YYYY-MM-DD') : '' },
    { key: 'vin', label: '车架号(VIN)', formatter: (val) => val || '' },
    { key: 'engineNo', label: '发动机号', formatter: (val) => val || '' },
    { key: 'isActive', label: '状态', formatter: (val) => val ? '有效' : '无效' }
  ]
  
  const filename = `车辆管理_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
  exportToExcel(tableData.value, columns, filename)
  ElMessage.success('导出成功')
}
</script>

<style scoped lang="scss">
.vehicle-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
}
</style>
