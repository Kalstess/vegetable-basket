<template>
  <div class="route-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆路线管理</span>
          <el-button type="primary" @click="handleAdd">新增路线</el-button>
        </div>
      </template>

      <el-table :data="routeList" v-loading="loading" stripe>
        <el-table-column prop="routeId" label="路线ID" width="200" />
        <el-table-column prop="vehiclePlateNumber" label="车辆" width="120" />
        <el-table-column prop="routeDate" label="路线日期" width="120" />
        <el-table-column prop="pointCount" label="路线点数" width="100" />
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
      title="路线详情"
      width="800px"
    >
      <el-table :data="routePoints" stripe>
        <el-table-column prop="seq" label="序号" width="80" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="pointType" label="类型" width="120" />
        <el-table-column label="到达时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.arriveTime) }}
          </template>
        </el-table-column>
        <el-table-column label="出发时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.departTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="备注" min-width="150" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { routePointApi } from '@/api'
import dayjs from 'dayjs'

const router = useRouter()

const routeList = ref([])
const loading = ref(false)
const viewDialogVisible = ref(false)
const routePoints = ref([])
const currentRouteId = ref('')

onMounted(() => {
  loadList()
})

const loadList = async () => {
  try {
    loading.value = true
    const data = await routePointApi.getAll()
    // 按路线ID分组
    const routeMap = new Map()
    if (Array.isArray(data)) {
      data.forEach(point => {
        if (!routeMap.has(point.routeId)) {
          routeMap.set(point.routeId, {
            routeId: point.routeId,
            vehiclePlateNumber: point.vehiclePlateNumber,
            routeDate: point.routeDate,
            pointCount: 0
          })
        }
        routeMap.get(point.routeId).pointCount++
      })
    }
    routeList.value = Array.from(routeMap.values())
  } catch (error) {
    ElMessage.error('加载路线列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push({ name: 'RouteForm' })
}

const handleView = async (row) => {
  try {
    const data = await routePointApi.getByRouteId(row.routeId)
    routePoints.value = Array.isArray(data) ? data.sort((a, b) => a.seq - b.seq) : []
    currentRouteId.value = row.routeId
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载路线详情失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条路线吗？', '提示', {
      type: 'warning'
    })
    await routePointApi.deleteByRouteId(row.routeId)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  try {
    return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
  } catch (e) {
    return dateTime
  }
}
</script>

<style scoped lang="scss">
.route-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>

