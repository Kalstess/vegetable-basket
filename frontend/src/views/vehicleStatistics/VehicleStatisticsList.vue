<template>
  <div class="vehicle-statistics-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆数量统计</span>
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-select
              v-model="selectedCompanyId"
              placeholder="选择企业"
              clearable
              style="width: 200px"
            >
              <el-option
                v-for="company in companies"
                :key="company.id"
                :label="company.name"
                :value="company.id"
              />
            </el-select>
            <el-date-picker
              v-model="statDate"
              type="date"
              placeholder="选择统计日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 180px"
            />
            <el-button type="primary" @click="handleGenerate">生成统计</el-button>
          </div>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="companyName" label="企业名称" min-width="200" />
        <el-table-column prop="statDate" label="统计日期" width="120" />
        <el-table-column label="菜篮子工程车" align="center">
          <el-table-column prop="regularBasketBlue" label="普通(蓝)" width="80" />
          <el-table-column prop="regularBasketYellow" label="普通(黄)" width="80" />
          <el-table-column prop="regularBasketGreen" label="普通(绿)" width="80" />
          <el-table-column prop="coldBasketBlue" label="冷藏(蓝)" width="80" />
          <el-table-column prop="coldBasketYellow" label="冷藏(黄)" width="80" />
          <el-table-column prop="coldBasketGreen" label="冷藏(绿)" width="80" />
          <el-table-column prop="basketEmissionStandard" label="国五以上" width="100" />
        </el-table-column>
        <el-table-column label="非菜篮子工程车" align="center">
          <el-table-column prop="regularFreightBlue" label="普通(蓝)" width="80" />
          <el-table-column prop="regularFreightYellow" label="普通(黄)" width="80" />
          <el-table-column prop="regularFreightGreen" label="普通(绿)" width="80" />
          <el-table-column prop="coldFreightBlue" label="冷藏(蓝)" width="80" />
          <el-table-column prop="coldFreightYellow" label="冷藏(黄)" width="80" />
          <el-table-column prop="coldFreightGreen" label="冷藏(绿)" width="80" />
          <el-table-column prop="freightEmissionStandard" label="国五以上" width="100" />
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="viewDialogVisible"
      title="统计详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentStats">
        <el-descriptions-item label="企业名称">{{ currentStats.companyName }}</el-descriptions-item>
        <el-descriptions-item label="统计日期">{{ currentStats.statDate }}</el-descriptions-item>
        <el-descriptions-item label="普通菜篮子工程车(蓝牌)">{{ currentStats.regularBasketBlue || 0 }}</el-descriptions-item>
        <el-descriptions-item label="普通菜篮子工程车(黄牌)">{{ currentStats.regularBasketYellow || 0 }}</el-descriptions-item>
        <el-descriptions-item label="普通菜篮子工程车(绿牌)">{{ currentStats.regularBasketGreen || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏菜篮子工程车(蓝牌)">{{ currentStats.coldBasketBlue || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏菜篮子工程车(黄牌)">{{ currentStats.coldBasketYellow || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏菜篮子工程车(绿牌)">{{ currentStats.coldBasketGreen || 0 }}</el-descriptions-item>
        <el-descriptions-item label="菜篮子工程车国五以上">{{ currentStats.basketEmissionStandard || 0 }}</el-descriptions-item>
        <el-descriptions-item label="普通货运车(蓝牌)">{{ currentStats.regularFreightBlue || 0 }}</el-descriptions-item>
        <el-descriptions-item label="普通货运车(黄牌)">{{ currentStats.regularFreightYellow || 0 }}</el-descriptions-item>
        <el-descriptions-item label="普通货运车(绿牌)">{{ currentStats.regularFreightGreen || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏货运车(蓝牌)">{{ currentStats.coldFreightBlue || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏货运车(黄牌)">{{ currentStats.coldFreightYellow || 0 }}</el-descriptions-item>
        <el-descriptions-item label="冷藏货运车(绿牌)">{{ currentStats.coldFreightGreen || 0 }}</el-descriptions-item>
        <el-descriptions-item label="非菜篮子工程车国五以上">{{ currentStats.freightEmissionStandard || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { vehicleStatisticsApi, companyApi } from '@/api'
import dayjs from 'dayjs'

const list = ref([])
const loading = ref(false)
const companies = ref([])
const selectedCompanyId = ref(null)
const statDate = ref(dayjs().format('YYYY-MM-DD'))
const viewDialogVisible = ref(false)
const currentStats = ref(null)

onMounted(() => {
  loadCompanies()
  loadList()
})

const loadCompanies = async () => {
  try {
    companies.value = await companyApi.getAll()
  } catch (error) {
    console.error('加载企业列表失败', error)
  }
}

const loadList = async () => {
  try {
    loading.value = true
    const data = await vehicleStatisticsApi.getAll()
    list.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载统计列表失败')
  } finally {
    loading.value = false
  }
}

const handleGenerate = async () => {
  if (!selectedCompanyId.value) {
    ElMessage.warning('请选择企业')
    return
  }
  if (!statDate.value) {
    ElMessage.warning('请选择统计日期')
    return
  }
  try {
    loading.value = true
    await vehicleStatisticsApi.generate(selectedCompanyId.value, statDate.value)
    ElMessage.success('生成统计成功')
    loadList()
    // 生成成功后清空选择
    selectedCompanyId.value = null
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '生成统计失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  currentStats.value = row
  viewDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条统计记录吗？', '提示', {
      type: 'warning'
    })
    await vehicleStatisticsApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped lang="scss">
.vehicle-statistics-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>

