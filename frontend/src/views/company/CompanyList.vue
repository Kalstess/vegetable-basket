<template>
  <div class="company-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增企业
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入企业名称或经营范围"
          style="width: 300px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px;"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="企业名称" min-width="200" />
        <el-table-column prop="companyType" label="企业性质" width="120" />
        <el-table-column prop="legalPersonName" label="负责人" width="120" />
        <el-table-column prop="legalPersonPhone" label="联系电话" width="150" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <CompanyDialog
      v-model="dialogVisible"
      :form-data="currentRow"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { companyApi } from '@/api'
import CompanyDialog from './CompanyDialog.vue'

const loading = ref(false)
const searchKeyword = ref('')
const tableData = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    if (searchKeyword.value) {
      const data = await companyApi.search(searchKeyword.value)
      tableData.value = data || []
      pagination.value.total = data?.length || 0
    } else {
      const data = await companyApi.getAll()
      tableData.value = data || []
      pagination.value.total = data?.length || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadData()
}

const handleReset = () => {
  searchKeyword.value = ''
  handleSearch()
}

const handleAdd = () => {
  currentRow.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该企业吗？', '提示', {
      type: 'warning'
    })
    await companyApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleDialogSuccess = () => {
  dialogVisible.value = false
  loadData()
}

const handleSizeChange = () => {
  loadData()
}

const handlePageChange = () => {
  loadData()
}
</script>

<style scoped lang="scss">
.company-list {
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

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

