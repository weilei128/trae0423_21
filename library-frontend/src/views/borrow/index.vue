<template>
  <div class="borrow-container">
    <el-card>
      <template #header>
        <span class="card-title">借阅服务</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="借书登记" name="borrow">
          <el-form :inline="true" :model="borrowForm" class="action-form">
            <el-form-item label="读者证">
              <el-input
                v-model="borrowForm.readerCard"
                placeholder="请输入读者证号"
                @keyup.enter="handleSearchReader"
              >
                <template #append>
                  <el-button @click="handleSearchReader">查询</el-button>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="馆藏条码">
              <el-input
                v-model="borrowForm.barcode"
                placeholder="请扫描或输入条码"
                @keyup.enter="handleSearchCollection"
              >
                <template #append>
                  <el-button @click="handleSearchCollection">查询</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-form>

          <el-divider />

          <el-row :gutter="20">
            <el-col :span="12">
              <el-descriptions title="读者信息" :column="1" border v-if="currentReader">
                <el-descriptions-item label="读者证">{{ currentReader.readerCard }}</el-descriptions-item>
                <el-descriptions-item label="姓名">{{ currentReader.name }}</el-descriptions-item>
                <el-descriptions-item label="类型">{{ currentReader.type }}</el-descriptions-item>
                <el-descriptions-item label="最大借阅">{{ currentReader.maxBorrowCount }} 本</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="currentReader.isBlacklist === 1 ? 'danger' : 'success'">
                    {{ currentReader.isBlacklist === 1 ? '黑名单' : '正常' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
              <el-empty v-else description="请先输入读者证号查询" />
            </el-col>
            <el-col :span="12">
              <el-descriptions title="图书信息" :column="1" border v-if="currentCollection">
                <el-descriptions-item label="条码">{{ currentCollection.barcode }}</el-descriptions-item>
                <el-descriptions-item label="库位">{{ currentCollection.location }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="currentCollection.status === 0 ? 'success' : 'danger'">
                    {{ currentCollection.status === 0 ? '在架可借' : '不可借' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
              <el-empty v-else description="请先输入馆藏条码查询" />
            </el-col>
          </el-row>

          <el-divider />

          <div class="action-buttons">
            <el-button type="primary" size="large" @click="handleBorrow" :disabled="!canBorrow">
              <el-icon><Plus /></el-icon>
              确认借书
            </el-button>
            <el-button size="large" @click="resetBorrowForm">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="还书登记" name="return">
          <el-form :inline="true" :model="returnForm" class="action-form">
            <el-form-item label="读者证">
              <el-input
                v-model="returnForm.readerCard"
                placeholder="请输入读者证号（可选）"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLoadBorrowRecords">
                <el-icon><Search /></el-icon>
                查询借阅记录
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="borrowRecords" stripe v-loading="recordsLoading">
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column prop="bookId" label="图书ID" width="80" />
            <el-table-column prop="collectionId" label="馆藏ID" width="80" />
            <el-table-column prop="borrowDate" label="借书日期" width="120" />
            <el-table-column prop="dueDate" label="应还日期" width="120">
              <template #default="scope">
                <el-tag :type="isOverdue(scope.row) ? 'danger' : 'info'">
                  {{ scope.row.dueDate }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="success" link @click="handleReturn(scope.row)">还书</el-button>
                <el-button type="primary" link @click="handleRenew(scope.row)">续借</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="借阅记录" name="records">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                <el-option label="借阅中" :value="0" />
                <el-option label="已归还" :value="1" />
                <el-option label="超期" :value="2" />
                <el-option label="已续借" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadRecords">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="recordList" stripe v-loading="recordsLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="readerId" label="读者ID" width="100" />
            <el-table-column prop="bookId" label="图书ID" width="100" />
            <el-table-column prop="collectionId" label="馆藏ID" width="100" />
            <el-table-column prop="borrowDate" label="借书日期" width="120" />
            <el-table-column prop="dueDate" label="应还日期" width="120" />
            <el-table-column prop="returnDate" label="还书日期" width="120" />
            <el-table-column prop="renewCount" label="续借次数" width="100" align="center" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReaderByCard } from '@/api/reader'
import { getCollectionByBarcode } from '@/api/collection'
import {
  getBorrowPage,
  getRecordsByReaderId,
  borrowBook,
  returnBook,
  renewBook
} from '@/api/borrow'

const activeTab = ref('borrow')
const recordsLoading = ref(false)

const borrowForm = reactive({
  readerCard: '',
  barcode: ''
})

const returnForm = reactive({
  readerCard: ''
})

const searchForm = reactive({
  status: null
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const currentReader = ref(null)
const currentCollection = ref(null)
const borrowRecords = ref([])
const recordList = ref([])

const canBorrow = computed(() => {
  return currentReader.value && currentCollection.value && currentCollection.value.status === 0
})

const statusMap = {
  0: { text: '借阅中', type: 'primary' },
  1: { text: '已归还', type: 'success' },
  2: { text: '超期', type: 'danger' },
  3: { text: '已续借', type: 'warning' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const isOverdue = (row) => {
  if (row.status === 2) return true
  if (row.status === 0 || row.status === 3) {
    const dueDate = new Date(row.dueDate)
    const today = new Date()
    return dueDate < today
  }
  return false
}

const handleSearchReader = async () => {
  if (!borrowForm.readerCard) {
    ElMessage.warning('请输入读者证号')
    return
  }
  try {
    const res = await getReaderByCard(borrowForm.readerCard)
    currentReader.value = res.data
    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('未找到该读者')
    currentReader.value = null
  }
}

const handleSearchCollection = async () => {
  if (!borrowForm.barcode) {
    ElMessage.warning('请输入馆藏条码')
    return
  }
  try {
    const res = await getCollectionByBarcode(borrowForm.barcode)
    currentCollection.value = res.data
    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('未找到该馆藏')
    currentCollection.value = null
  }
}

const handleBorrow = async () => {
  if (!canBorrow.value) return
  try {
    await ElMessageBox.confirm('确认借书？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await borrowBook(currentReader.value.id, currentCollection.value.id)
    ElMessage.success('借书成功')
    resetBorrowForm()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to borrow:', error)
    }
  }
}

const resetBorrowForm = () => {
  borrowForm.readerCard = ''
  borrowForm.barcode = ''
  currentReader.value = null
  currentCollection.value = null
}

const handleLoadBorrowRecords = async () => {
  if (!returnForm.readerCard) {
    ElMessage.warning('请输入读者证号')
    return
  }
  recordsLoading.value = true
  try {
    const readerRes = await getReaderByCard(returnForm.readerCard)
    if (readerRes.data) {
      const recordsRes = await getRecordsByReaderId(readerRes.data.id)
      borrowRecords.value = recordsRes.data?.filter(r => r.status !== 1) || []
    }
  } catch (error) {
    console.error('Failed to load records:', error)
  } finally {
    recordsLoading.value = false
  }
}

const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确认还书？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await returnBook(row.id)
    ElMessage.success('还书成功')
    handleLoadBorrowRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to return:', error)
    }
  }
}

const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm('确认续借？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await renewBook(row.id)
    ElMessage.success('续借成功')
    handleLoadBorrowRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to renew:', error)
    }
  }
}

const loadRecords = async () => {
  recordsLoading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getBorrowPage(params)
    recordList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load records:', error)
  } finally {
    recordsLoading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadRecords()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadRecords()
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.borrow-container {
  padding: 0;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.action-form {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
