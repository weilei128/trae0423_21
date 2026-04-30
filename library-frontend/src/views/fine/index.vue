<template>
  <div class="fine-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">逾期罚款</span>
          <el-button type="warning" @click="handleCheckOverdue">
            <el-icon><Refresh /></el-icon>
            检查超期
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="未缴" :value="0" />
            <el-option label="已缴" :value="1" />
            <el-option label="部分缴" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="readerId" label="读者ID" width="100" />
        <el-table-column prop="borrowRecordId" label="借阅记录ID" width="120" />
        <el-table-column prop="overdueDays" label="超期天数" width="100" align="center">
          <template #default="scope">
            <el-tag type="danger">{{ scope.row.overdueDays }} 天</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fineAmount" label="罚款金额" width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">
              ¥{{ scope.row.fineAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴金额" width="120">
          <template #default="scope">
            <span style="color: #67c23a; font-weight: bold;">
              ¥{{ scope.row.paidAmount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="fineDate" label="罚款日期" width="120" />
        <el-table-column prop="paidDate" label="缴费日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status !== 1"
              type="success"
              link
              @click="handlePay(scope.row)"
            >缴费</el-button>
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
    </el-card>

    <el-dialog
      v-model="payDialogVisible"
      title="缴费处理"
      width="400px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="罚款金额">
          <span style="color: #f56c6c; font-weight: bold; font-size: 18px;">
            ¥{{ currentFine?.fineAmount }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="已缴金额">
          <span style="color: #67c23a; font-weight: bold;">
            ¥{{ currentFine?.paidAmount || 0 }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="待缴金额">
          <span style="color: #e6a23c; font-weight: bold; font-size: 18px;">
            ¥{{ remainingAmount }}
          </span>
        </el-descriptions-item>
      </el-descriptions>
      <el-form label-width="80px" style="margin-top: 20px;">
        <el-form-item label="缴费金额">
          <el-input-number
            v-model="payAmount"
            :min="0.01"
            :max="remainingAmount"
            :precision="2"
            :step="0.5"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPay">确认缴费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getFinePage,
  payFine,
  checkOverdueFines
} from '@/api/fine'

const loading = ref(false)
const tableData = ref([])
const payDialogVisible = ref(false)
const currentFine = ref(null)
const payAmount = ref(0)

const searchForm = reactive({
  status: null
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const statusMap = {
  0: { text: '未缴', type: 'danger' },
  1: { text: '已缴', type: 'success' },
  2: { text: '部分缴', type: 'warning' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const remainingAmount = computed(() => {
  if (!currentFine.value) return 0
  const fineAmount = parseFloat(currentFine.value.fineAmount) || 0
  const paidAmount = parseFloat(currentFine.value.paidAmount) || 0
  return (fineAmount - paidAmount).toFixed(2)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getFinePage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load fines:', error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadData()
}

const handleCheckOverdue = async () => {
  try {
    await checkOverdueFines()
    ElMessage.success('检查完成')
    loadData()
  } catch (error) {
    console.error('Failed to check overdue:', error)
  }
}

const handlePay = (row) => {
  currentFine.value = row
  const fineAmount = parseFloat(row.fineAmount) || 0
  const paidAmount = parseFloat(row.paidAmount) || 0
  payAmount.value = parseFloat((fineAmount - paidAmount).toFixed(2))
  payDialogVisible.value = true
}

const submitPay = async () => {
  if (!currentFine.value) return
  try {
    await ElMessageBox.confirm(`确认缴费 ¥${payAmount.value}？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await payFine(currentFine.value.id, payAmount.value)
    ElMessage.success('缴费成功')
    payDialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to pay fine:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.fine-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
