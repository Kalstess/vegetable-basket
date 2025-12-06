<template>
  <div class="survey-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>问卷调查</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增问卷
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-select v-model="searchYear" placeholder="选择年份" clearable style="width: 200px;">
          <el-option
            v-for="year in years"
            :key="year"
            :label="year + '年'"
            :value="year"
          />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px;"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="companyName" label="企业名称" min-width="200" />
        <el-table-column prop="surveyYear" label="调查年份" width="120" />
        <el-table-column prop="submitStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.submitStatus)">
              {{ row.submitStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ row.submitTime ? new Date(row.submitTime).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="submittedBy" label="提交人" width="120" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.submitStatus === '草稿'" type="success" link @click="handleSubmit(row)">提交</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentRow ? '编辑问卷调查' : '新增问卷调查'"
      width="90%"
      :close-on-click-modal="false"
    >
      <el-steps :active="activeStep" finish-status="success" style="margin-bottom: 30px;">
        <el-step title="基本信息" />
        <el-step title="运输量及车辆" />
        <el-step title="产品类型" />
        <el-step title="载具及定位" />
        <el-step title="冷藏车信息" />
        <el-step title="其他信息" />
      </el-steps>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="180px">
        <!-- 第一步：基本信息 -->
        <div v-show="activeStep === 0">
          <el-form-item label="企业" prop="companyId">
            <el-select v-model="formData.companyId" placeholder="请选择企业" style="width: 100%;" :disabled="!!currentRow">
              <el-option
                v-for="company in companies"
                :key="company.id"
                :label="company.name"
                :value="company.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="调查年份" prop="surveyYear">
            <el-input-number v-model="formData.surveyYear" :min="2020" :max="2099" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="主要配送客户类型" prop="deliveryCustomerTypes">
            <el-checkbox-group v-model="formData.deliveryCustomerTypes">
              <el-checkbox label="电商平台">电商平台</el-checkbox>
              <el-checkbox label="连锁商超">连锁商超</el-checkbox>
              <el-checkbox label="餐饮及中央厨房">餐饮及中央厨房</el-checkbox>
              <el-checkbox label="批发市场">批发市场</el-checkbox>
              <el-checkbox label="食品加工与生产企业">食品加工与生产企业</el-checkbox>
              <el-checkbox label="食堂学校机关企业">食堂（学校/机关/企业）</el-checkbox>
              <el-checkbox label="个人团购自提点等">个人（团购自提点等）</el-checkbox>
              <el-checkbox label="其他">其他</el-checkbox>
            </el-checkbox-group>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">最多选择3项</div>
          </el-form-item>
        </div>

        <!-- 第二步：运输量及车辆 -->
        <div v-show="activeStep === 1">
          <el-form-item label="2022年运输量(吨)">
            <el-input-number v-model="formData.annualTransport2022" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2023年运输量(吨)">
            <el-input-number v-model="formData.annualTransport2023" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2024年运输量(吨)">
            <el-input-number v-model="formData.annualTransport2024" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2025年运输量(吨)">
            <el-input-number v-model="formData.annualTransport2025" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="未来三年自有货运车辆预计">
            <el-radio-group v-model="formData.futureVehiclePlan">
              <el-radio label="增加">增加</el-radio>
              <el-radio label="减少">减少</el-radio>
              <el-radio label="不变">不变</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="2025年营业收入(万元)">
            <el-input-number v-model="formData.revenue2025" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2025年普通菜篮子工程车(辆)">
            <el-input-number v-model="formData.regularBasketVehicles2025" :min="0" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2025年冷藏菜篮子工程车(辆)">
            <el-input-number v-model="formData.coldBasketVehicles2025" :min="0" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2025年普通其他货运车(辆)">
            <el-input-number v-model="formData.regularOtherVehicles2025" :min="0" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="2025年冷藏其他货运车(辆)">
            <el-input-number v-model="formData.coldOtherVehicles2025" :min="0" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="菜篮子车平均每月出车次数相比其他货运车">
            <el-radio-group v-model="formData.basketTripComparison">
              <el-radio label="非常低">非常低</el-radio>
              <el-radio label="低">低</el-radio>
              <el-radio label="差不多">差不多</el-radio>
              <el-radio label="高">高</el-radio>
              <el-radio label="非常高">非常高</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <!-- 第三步：产品类型 -->
        <div v-show="activeStep === 2">
          <el-form-item label="最主要产品种类">
            <el-radio-group v-model="formData.mainProductType">
              <el-radio label="A生鲜蔬菜类">A.生鲜蔬菜类</el-radio>
              <el-radio label="B肉类及其制品">B.肉类及其制品</el-radio>
              <el-radio label="C水产品类">C.水产品类</el-radio>
              <el-radio label="D豆制品类">D.豆制品类</el-radio>
              <el-radio label="E蛋奶及其制品">E.蛋奶及其制品</el-radio>
              <el-radio label="F水果类">F.水果类</el-radio>
              <el-radio label="G粮食及其制品">G.粮食及其制品</el-radio>
              <el-radio label="H综合配送类">H.综合配送类</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="次要产品种类">
            <el-radio-group v-model="formData.secondaryProductType">
              <el-radio label="A生鲜蔬菜类">A.生鲜蔬菜类</el-radio>
              <el-radio label="B肉类及其制品">B.肉类及其制品</el-radio>
              <el-radio label="C水产品类">C.水产品类</el-radio>
              <el-radio label="D豆制品类">D.豆制品类</el-radio>
              <el-radio label="E蛋奶及其制品">E.蛋奶及其制品</el-radio>
              <el-radio label="F水果类">F.水果类</el-radio>
              <el-radio label="G粮食及其制品">G.粮食及其制品</el-radio>
              <el-radio label="H综合配送类">H.综合配送类</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <!-- 第四步：载具及定位 -->
        <div v-show="activeStep === 3">
          <el-form-item label="标准化载具">
            <el-checkbox-group v-model="formData.standardEquipmentTypes">
              <el-checkbox label="标准周转箱">标准周转箱</el-checkbox>
              <el-checkbox label="标准周转筐">标准周转筐</el-checkbox>
              <el-checkbox label="标准托盘">标准托盘</el-checkbox>
              <el-checkbox label="标准笼车">标准笼车</el-checkbox>
              <el-checkbox label="没有使用标准化载具">没有使用标准化载具</el-checkbox>
              <el-checkbox label="其他">其他</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="卫星定位系统类型">
            <el-radio-group v-model="formData.gpsSystemType">
              <el-radio label="GPS单模">GPS单模</el-radio>
              <el-radio label="北斗单模">北斗单模</el-radio>
              <el-radio label="北斗GPS双模">北斗+GPS双模</el-radio>
              <el-radio label="北斗GPSGLONASSGalileo多星融合">北斗+GPS+GLONASS/Galileo多星融合</el-radio>
              <el-radio label="暂未安装任何卫星定位系统">暂未安装任何卫星定位系统</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="已安装卫星定位系统的菜篮子工程车接入的平台">
            <el-checkbox-group v-model="formData.gpsPlatformTypes">
              <el-checkbox label="全国道路货运车辆公共监管与服务平台简称全国货运平台">全国道路货运车辆公共监管与服务平台（简称"全国货运平台"）</el-checkbox>
              <el-checkbox label="上海主副食品市场运行调控系统">上海主副食品市场运行调控系统</el-checkbox>
              <el-checkbox label="企业自建或第三方车队管理系统TMS">企业自建或第三方车队管理系统（TMS）</el-checkbox>
              <el-checkbox label="甲方货主或物流平台的运力协同系统">甲方货主或物流平台的运力协同系统</el-checkbox>
              <el-checkbox label="上海交通局执法支队监管平台">上海交通局/执法支队监管平台</el-checkbox>
              <el-checkbox label="未接入任何平台">未接入任何平台</el-checkbox>
              <el-checkbox label="其他">其他</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </div>

        <!-- 第五步：冷藏车信息 -->
        <div v-show="activeStep === 4">
          <el-form-item label="是否有冷藏菜篮子工程车">
            <el-switch v-model="formData.hasColdBasketVehicle" />
          </el-form-item>
          <template v-if="formData.hasColdBasketVehicle">
            <el-form-item label="是否有多温区/分舱/分栏设计">
              <el-radio-group v-model="formData.coldVehicleMultiTemp">
                <el-radio :label="true">有</el-radio>
                <el-radio :label="false">没有</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="温度记录设备">
              <el-radio-group v-model="formData.tempRecordingDevice">
                <el-radio label="只有驾驶区有温度显示无记录">只有驾驶区有温度显示(无记录)</el-radio>
                <el-radio label="车载温度记录仪单机无网络型">车载温度记录仪(单机无网络型)</el-radio>
                <el-radio label="车载温度记录仪无线网络传输型">车载温度记录仪(无线网络传输型)</el-radio>
                <el-radio label="实时云端监控异常报警平台">实时云端监控+异常报警平台</el-radio>
                <el-radio label="无温控记录设备">无温控记录设备</el-radio>
                <el-radio label="其他">其他</el-radio>
              </el-radio-group>
            </el-form-item>
          </template>
        </div>

        <!-- 第六步：其他信息 -->
        <div v-show="activeStep === 5">
          <el-form-item label="平均装载率">
            <el-radio-group v-model="formData.avgLoadingRate">
              <el-radio label="小于等于50">≤50%</el-radio>
              <el-radio label="五十一到七十">51-70%</el-radio>
              <el-radio label="七十一到九十">71-90%</el-radio>
              <el-radio label="九十一以上">91%以上</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="使用菜篮子车最看重的因素">
            <el-radio-group v-model="formData.basketVehicleImportance">
              <el-radio label="进市区通行的便利性">进市区通行的便利性</el-radio>
              <el-radio label="高速隧道桥通行费的减免">高速/隧道/桥通行费的减免</el-radio>
              <el-radio label="车身统一菜篮子工程标识带来的企业公益形象提升">车身统一"菜篮子工程"标识带来的企业公益形象提升</el-radio>
              <el-radio label="其他">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="formData.basketVehicleImportance === '其他'" label="其他因素说明">
            <el-input v-model="formData.otherImportance" type="textarea" :rows="3" placeholder="请输入其他因素说明" />
          </el-form-item>
          <el-form-item label="运营中遇到的主要问题（请按重要性排序）">
            <el-alert
              title="请使用上下箭头调整顺序，最上面的为最重要的问题"
              type="info"
              :closable="false"
              style="margin-bottom: 10px;"
            />
            <div v-for="(problem, index) in formData.operationalProblems" :key="problem.problemType" class="problem-item">
              <el-icon style="margin-right: 10px;"><Rank /></el-icon>
              <span style="flex: 1;">{{ index + 1 }}. {{ getProblemLabel(problem.problemType) }}</span>
              <el-button
                v-if="index > 0"
                type="text"
                @click="moveProblemUp(index)"
                style="margin-right: 5px;"
              >
                <el-icon><ArrowUp /></el-icon>
              </el-button>
              <el-button
                v-if="index < formData.operationalProblems.length - 1"
                type="text"
                @click="moveProblemDown(index)"
                style="margin-right: 10px;"
              >
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <el-input
                v-if="problem.problemType === '其他'"
                v-model="problem.otherDescription"
                placeholder="请输入其他问题说明"
                style="width: 300px;"
              />
            </div>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div style="display: flex; justify-content: space-between;">
          <el-button @click="dialogVisible = false">取消</el-button>
          <div>
            <el-button v-if="activeStep > 0" @click="activeStep--">上一步</el-button>
            <el-button v-if="activeStep < 5" type="primary" @click="activeStep++">下一步</el-button>
            <el-button v-if="activeStep === 5" type="primary" @click="handleSave">保存草稿</el-button>
            <el-button v-if="activeStep === 5 && currentRow" type="success" @click="handleSubmitForm">提交</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Rank, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { surveyApi, companyApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const companies = ref([])
const searchYear = ref(null)
const dialogVisible = ref(false)
const currentRow = ref(null)
const formRef = ref(null)
const activeStep = ref(0)

const years = ref([])
for (let i = 2020; i <= new Date().getFullYear() + 1; i++) {
  years.value.push(i)
}

const problemOptions = [
  { value: '企业经营下滑无法重新更换新车', label: '企业经营下滑无法重新更换新车' },
  { value: '高峰时段限行无法进城', label: '高峰时段限行无法进城' },
  { value: '限行区外缺少合法卸货点', label: '限行区外缺少合法卸货点' },
  { value: '社区商超地下停车场限高禁入', label: '社区/商超地下停车场限高/禁入' },
  { value: '卸货车位被社会车占用', label: '卸货车位被社会车占用' },
  { value: '交警城管处罚频繁', label: '交警/城管处罚频繁' },
  { value: '新能源续航不足冬季掉电', label: '新能源续航不足、冬季掉电' },
  { value: '新能源充电桩少', label: '新能源充电桩少' },
  { value: '冷藏车购置维护成本高', label: '冷藏车购置/维护成本高' },
  { value: '司机短缺工资上涨', label: '司机短缺、工资上涨' },
  { value: '客户账期长现金流压力', label: '客户账期长、现金流压力' },
  { value: '回程空驶率高', label: '回程空驶率高' },
  { value: '无法进行尾板箱体分割等改装', label: '无法进行尾板，箱体分割等改装' },
  { value: '其他', label: '其他' }
]

const formData = ref({
  companyId: null,
  surveyYear: new Date().getFullYear(),
  deliveryCustomerTypes: [],
  annualTransport2022: null,
  annualTransport2023: null,
  annualTransport2024: null,
  annualTransport2025: null,
  futureVehiclePlan: null,
  revenue2025: null,
  regularBasketVehicles2025: 0,
  coldBasketVehicles2025: 0,
  regularOtherVehicles2025: 0,
  coldOtherVehicles2025: 0,
  basketTripComparison: null,
  mainProductType: null,
  secondaryProductType: null,
  standardEquipmentTypes: [],
  gpsSystemType: null,
  gpsPlatformTypes: [],
  hasColdBasketVehicle: false,
  coldVehicleMultiTemp: null,
  tempRecordingDevice: null,
  avgLoadingRate: null,
  basketVehicleImportance: null,
  otherImportance: null,
  operationalProblems: []
})

const rules = {
  companyId: [{ required: true, message: '请选择企业', trigger: 'change' }],
  surveyYear: [{ required: true, message: '请输入调查年份', trigger: 'blur' }],
  deliveryCustomerTypes: [
    { type: 'array', max: 3, message: '最多选择3项', trigger: 'change' }
  ]
}

onMounted(() => {
  loadCompanies()
  loadData()
  // 初始化运营问题列表
  formData.value.operationalProblems = problemOptions.map((opt, index) => ({
    problemType: opt.value,
    sortOrder: index + 1,
    otherDescription: null
  }))
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
    let data = []
    if (searchYear.value) {
      data = await surveyApi.getByYear(searchYear.value)
    } else {
      data = await surveyApi.getAll()
    }
    tableData.value = data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchYear.value = null
  loadData()
}

const handleAdd = () => {
  currentRow.value = null
  activeStep.value = 0
  formData.value = {
    companyId: null,
    surveyYear: new Date().getFullYear(),
    deliveryCustomerTypes: [],
    annualTransport2022: null,
    annualTransport2023: null,
    annualTransport2024: null,
    annualTransport2025: null,
    futureVehiclePlan: null,
    revenue2025: null,
    regularBasketVehicles2025: 0,
    coldBasketVehicles2025: 0,
    regularOtherVehicles2025: 0,
    coldOtherVehicles2025: 0,
    basketTripComparison: null,
    mainProductType: null,
    secondaryProductType: null,
    standardEquipmentTypes: [],
    gpsSystemType: null,
    gpsPlatformTypes: [],
    hasColdBasketVehicle: false,
    coldVehicleMultiTemp: null,
    tempRecordingDevice: null,
    avgLoadingRate: null,
    basketVehicleImportance: null,
    otherImportance: null,
    operationalProblems: problemOptions.map((opt, index) => ({
      problemType: opt.value,
      sortOrder: index + 1,
      otherDescription: null
    }))
  }
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const data = await surveyApi.getById(row.id)
    currentRow.value = data
    activeStep.value = 0
    
    // 填充表单数据
    formData.value = {
      id: data.id,
      companyId: data.companyId,
      surveyYear: data.surveyYear,
      deliveryCustomerTypes: data.deliveryCustomerTypes || [],
      annualTransport2022: data.annualTransport2022,
      annualTransport2023: data.annualTransport2023,
      annualTransport2024: data.annualTransport2024,
      annualTransport2025: data.annualTransport2025,
      futureVehiclePlan: data.futureVehiclePlan,
      revenue2025: data.revenue2025,
      regularBasketVehicles2025: data.regularBasketVehicles2025 || 0,
      coldBasketVehicles2025: data.coldBasketVehicles2025 || 0,
      regularOtherVehicles2025: data.regularOtherVehicles2025 || 0,
      coldOtherVehicles2025: data.coldOtherVehicles2025 || 0,
      basketTripComparison: data.basketTripComparison,
      mainProductType: data.mainProductType,
      secondaryProductType: data.secondaryProductType,
      standardEquipmentTypes: data.standardEquipmentTypes || [],
      gpsSystemType: data.gpsSystemType,
      gpsPlatformTypes: data.gpsPlatformTypes || [],
      hasColdBasketVehicle: data.hasColdBasketVehicle || false,
      coldVehicleMultiTemp: data.coldVehicleMultiTemp,
      tempRecordingDevice: data.tempRecordingDevice,
      avgLoadingRate: data.avgLoadingRate,
      basketVehicleImportance: data.basketVehicleImportance,
      otherImportance: data.otherImportance,
      operationalProblems: data.operationalProblems || problemOptions.map((opt, index) => ({
        problemType: opt.value,
        sortOrder: index + 1,
        otherDescription: null
      }))
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 更新排序
        formData.value.operationalProblems.forEach((p, index) => {
          p.sortOrder = index + 1
        })
        
        await surveyApi.createOrUpdate(formData.value)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

const handleSubmitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 先保存
        await handleSave()
        // 再提交
        await surveyApi.submit(currentRow.value.id)
        ElMessage.success('提交成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(error.response?.data?.message || '提交失败')
      }
    }
  })
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该问卷调查吗？提交后将无法修改。', '提示', {
      type: 'warning'
    })
    await surveyApi.submit(row.id)
    ElMessage.success('提交成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该问卷调查记录吗？', '提示', {
      type: 'warning'
    })
    await surveyApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const statusMap = {
    '草稿': 'info',
    '已提交': 'warning',
    '已审核': 'success',
    '已驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

const getProblemLabel = (value) => {
  const option = problemOptions.find(opt => opt.value === value)
  return option ? option.label : value
}

const moveProblemUp = (index) => {
  if (index > 0) {
    const problems = formData.value.operationalProblems
    const temp = problems[index]
    problems[index] = problems[index - 1]
    problems[index - 1] = temp
    // 更新排序
    problems.forEach((p, i) => {
      p.sortOrder = i + 1
    })
  }
}

const moveProblemDown = (index) => {
  if (index < formData.value.operationalProblems.length - 1) {
    const problems = formData.value.operationalProblems
    const temp = problems[index]
    problems[index] = problems[index + 1]
    problems[index + 1] = temp
    // 更新排序
    problems.forEach((p, i) => {
      p.sortOrder = i + 1
    })
  }
}
</script>

<style scoped lang="scss">
.survey-list {
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

  .problem-item {
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background-color: #f5f7fa;
  }
}
</style>

