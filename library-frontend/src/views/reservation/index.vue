<template>
  <div class="reservation-container">
    <el-card>
      <template #header>
        <span class="card-title">预约续借</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="图书预约" name="reserve">
          <el-form :inline="true" :model="reserveForm" class="action-form">
            <el-form-item label="读者证">
              <el-input
                v-model="reserveForm.readerCard"
                placeholder="请输入读者证号"
              />
            </el-form-item>
            <el-form-item label="图书">
              <el-select
                v-model="reserveForm.bookId"
                placeholder="请选择图书"
                filterable
                clearable
                style="width: 200px"
              >
                <el-option
                  v-for="book in bookList"
                  :key="book.id"
                  :label="book.title"
                  :value="book.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleReserve">
                <el-icon><Plus /></el-icon>
                预约图书
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <el-table :data="reservationList" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="readerId" label="读者ID" width="100" />
            <el-table-column prop="bookId" label="图书ID" width="100" />
            <el-table-column prop="reservationDate" label="预约日期" width="120" />
            <el-table-column prop="expiryDate" label="过期日期" width="120" />
            <el-table-column prop="queuePosition" label="队列位置" width="100" align="center" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 0"
                  type="warning"
                  link
                  @click="handleCancel(scope.row)"
                >取消预约</el-button>
                <el-button
                  v-if="scope.row.status === 0"
                  type="success"
                  link
                  @click="handleFulfill(scope.row)"
                >履约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="在线续借" name="renew">
          <el-form :inline="true" :model="renewForm" class="action-form">
            <el-form-item label="读者证">
              <el-input
                v-model="renewForm.readerCard"
                placeholder="请输入读者证号查询借阅记录"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearchRenewRecords">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="renewRecords" stripe v-loading="recordsLoading">
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column prop="bookId" label="图书ID" width="100" />
            <el-table-column prop="collectionId" label="馆藏ID" width="100" />
            <el-table-column prop="borrowDate" label="借书日期" width="120" />
            <el-table-column prop="dueDate" label="应还日期" width="120">
              <template #default="scope">
                <el-tag :type="isOverdue(scope.row) ? 'danger' : 'info'">
                  {{ scope.row.dueDate }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="renewCount" label="续借次数" width="100" align="center" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getBorrowStatusType(scope.row.status)">
                  {{ getBorrowStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 0 || scope.row.status === 3"
                  type="primary"
                  link
                  @click="handleRenew(scope.row)"
                >续借</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="预约列表" name="list">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                <el-option label="待处理" :value="0" />
                <el-option label="已履约" :value="1" />
                <el-option label="已取消" :value="2" />
                <el-option label="已过期" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadReservations">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="allReservations" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="readerId" label="读者ID" width="100" />
            <el-table-column prop="bookId" label="图书ID" width="100" />
            <el-table-column prop="reservationDate" label="预约日期" width="120" />
            <el-table-column prop="expiryDate" label="过期日期" width="120" />
            <el-table-column prop="queuePosition" label="队列位置" width="100" align="center" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookList } from '@/api/book'
import { getReaderByCard } from '@/api/reader'
import { getRecordsByReaderId, renewBook } from '@/api/borrow'
import {
  getReservationPage,
  reserveBook,
  cancelReservation,
  fulfillReservation
} from '@/api/reservation'

const activeTab = ref('reserve')
const loading = ref(false)
const recordsLoading = ref(false)

const reserveForm = reactive({
  readerCard: '',
  bookId: null
})

const renewForm = reactive({
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

const bookList = ref([])
const reservationList = ref([])
const renewRecords = ref([])
const allReservations = ref([])

const statusMap = {
  0: { text: '待处理', type: 'warning' },
  1: { text: '已履约', type: 'success' },
  2: { text: '已取消', type: 'info' },
  3: { text: '已过期', type: 'danger' }
}

const borrowStatusMap = {
  0: { text: '借阅中', type: 'primary' },
  1: { text: '已归还', type: 'success' },
  2: { text: '超期', type: 'danger' },
  3: { text: '已续借', type: 'warning' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'
const getBorrowStatusText = (status) => borrowStatusMap[status]?.text || '未知'
const getBorrowStatusType = (status) => borrowStatusMap[status]?.type || 'info'

const isOverdue = (row) => {
  if (row.status === 2) return true
  if (row.status === 0 || row.status === 3) {
    const dueDate = new Date(row.dueDate)
    const today = new Date()
    return dueDate < today
  }
  return false
}

const loadBooks = async () => {
  try {
    const res = await getBookList()
    bookList.value = res.data || []
  } catch (error) {
    console.error('Failed to load books:', error)
  }
}

const loadReservations = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getReservationPage(params)
    allReservations.value = res.data?.records || []
    reservationList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load reservations:', error)
  } finally {
    loading.value = false
  }
}

const handleReserve = async () => {
  if (!reserveForm.readerCard || !reserveForm.bookId) {
    ElMessage.warning('请输入读者证号并选择图书')
    return
  }
  try {
    const readerRes = await getReaderByCard(reserveForm.readerCard)
    if (!readerRes.data) {
      ElMessage.error('未找到该读者')
      return
    }
    await ElMessageBox.confirm('确认预约该图书？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await reserveBook(readerRes.data.id, reserveForm.bookId)
    ElMessage.success('预约成功')
    loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to reserve:', error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消预约？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReservation(row.id)
    ElMessage.success('取消成功')
    loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel:', error)
    }
  }
}

const handleFulfill = async (row) => {
  try {
    await ElMessageBox.confirm('确认履约？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await fulfillReservation(row.id)
    ElMessage.success('履约成功')
    loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to fulfill:', error)
    }
  }
}

const handleSearchRenewRecords = async () => {
  if (!renewForm.readerCard) {
    ElMessage.warning('请输入读者证号')
    return
  }
  recordsLoading.value = true
  try {
    const readerRes = await getReaderByCard(renewForm.readerCard)
    if (readerRes.data) {
      const recordsRes = await getRecordsByReaderId(readerRes.data.id)
      renewRecords.value = recordsRes.data?.filter(r => r.status !== 1) || []
    }
  } catch (error) {
    console.error('Failed to load renew records:', error)
  } finally {
    recordsLoading.value = false
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
    handleSearchRenewRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to renew:', error)
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadReservations()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadReservations()
}

onMounted(() => {
  loadBooks()
  loadReservations()
})
</script>

<style scoped>
.reservation-container {
  padding: 0;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.action-form {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
