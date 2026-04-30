<template>
  <div class="stats">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">时间范围选择</span>
              <div class="date-pickers">
                <el-date-picker
                  v-model="startDate"
                  type="date"
                  placeholder="开始日期"
                  value-format="YYYY-MM-DD"
                  :clearable="false"
                />
                <span class="date-separator">至</span>
                <el-date-picker
                  v-model="endDate"
                  type="date"
                  placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  :clearable="false"
                />
                <el-button type="primary" @click="loadAllStats">
                  <el-icon><Refresh /></el-icon>
                  查询
                </el-button>
              </div>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="8">
        <el-card class="stat-summary-card">
          <div class="summary-content">
            <div class="summary-icon borrow-summary">
              <el-icon size="28"><Tickets /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ summaryData.borrowCount || 0 }}</div>
              <div class="summary-label">借阅册数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-summary-card">
          <div class="summary-content">
            <div class="summary-icon return-summary">
              <el-icon size="28"><CircleCheck /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ summaryData.returnCount || 0 }}</div>
              <div class="summary-label">归还册数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-summary-card">
          <div class="summary-content">
            <div class="summary-icon visit-summary">
              <el-icon size="28"><UserFilled /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ summaryData.visitCount || 0 }}</div>
              <div class="summary-label">到馆人次</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">借阅量排行 TOP 20</span>
          </template>
          <el-table :data="borrowRanking" stripe max-height="400">
            <el-table-column prop="rank" label="排名" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.rank <= 3" :type="getRankType(scope.row.rank)" size="large">
                  {{ scope.row.rank }}
                </el-tag>
                <span v-else>{{ scope.row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" show-overflow-tooltip />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center">
              <template #default="scope">
                <el-tag type="primary">{{ scope.row.borrowCount }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">热门书目 TOP 20</span>
          </template>
          <el-table :data="popularBooks" stripe max-height="400">
            <el-table-column prop="rank" label="排名" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.rank <= 3" :type="getRankType(scope.row.rank)" size="large">
                  {{ scope.row.rank }}
                </el-tag>
                <span v-else>{{ scope.row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" show-overflow-tooltip />
            <el-table-column prop="availableCount" label="可借册数" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.availableCount > 0 ? 'success' : 'danger'">
                  {{ scope.row.availableCount }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span class="card-title">近12个月借阅趋势</span>
          </template>
          <el-table :data="monthlyTrend" stripe>
            <el-table-column prop="month" label="月份" width="120" align="center" />
            <el-table-column prop="borrowCount" label="借阅册数" width="150" align="center">
              <template #default="scope">
                <el-progress :percentage="getPercentage(scope.row.borrowCount, maxBorrow)" :color="'#409EFF'" :stroke-width="20" />
              </template>
            </el-table-column>
            <el-table-column prop="returnCount" label="归还册数" width="150" align="center">
              <template #default="scope">
                <el-progress :percentage="getPercentage(scope.row.returnCount, maxReturn)" :color="'#67C23A'" :stroke-width="20" />
              </template>
            </el-table-column>
            <el-table-column prop="borrowCount" label="借阅量" width="100" align="center">
              <template #default="scope">
                <el-tag type="primary" size="large">{{ scope.row.borrowCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="returnCount" label="归还量" width="100" align="center">
              <template #default="scope">
                <el-tag type="success" size="large">{{ scope.row.returnCount }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  getBorrowRanking, 
  getPopularBooks, 
  getMonthlyStats,
  getBorrowCount,
  getReturnCount,
  getVisitCount
} from '@/api/stats'

const today = new Date()
const oneMonthAgo = new Date()
oneMonthAgo.setMonth(today.getMonth() - 1)

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const startDate = ref(formatDate(oneMonthAgo))
const endDate = ref(formatDate(today))

const summaryData = ref({
  borrowCount: 0,
  returnCount: 0,
  visitCount: 0
})

const borrowRanking = ref([])
const popularBooks = ref([])
const monthlyTrend = ref([])

const maxBorrow = computed(() => {
  if (!monthlyTrend.value.length) return 1
  return Math.max(...monthlyTrend.value.map(item => item.borrowCount || 1))
})

const maxReturn = computed(() => {
  if (!monthlyTrend.value.length) return 1
  return Math.max(...monthlyTrend.value.map(item => item.returnCount || 1))
})

const getPercentage = (value, max) => {
  if (!max) return 0
  return Math.round((value / max) * 100)
}

const getRankType = (rank) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'info'
  return ''
}

const loadSummaryData = async () => {
  try {
    const [borrowRes, returnRes, visitRes] = await Promise.all([
      getBorrowCount(startDate.value, endDate.value),
      getReturnCount(startDate.value, endDate.value),
      getVisitCount(startDate.value, endDate.value)
    ])
    
    summaryData.value = {
      borrowCount: borrowRes.data || 0,
      returnCount: returnRes.data || 0,
      visitCount: visitRes.data || 0
    }
  } catch (error) {
    console.error('Failed to load summary data:', error)
  }
}

const loadBorrowRanking = async () => {
  try {
    const res = await getBorrowRanking(20)
    const data = res.data || []
    borrowRanking.value = data.map((item, index) => ({
      ...item,
      rank: index + 1
    }))
  } catch (error) {
    console.error('Failed to load borrow ranking:', error)
  }
}

const loadPopularBooks = async () => {
  try {
    const res = await getPopularBooks(20)
    const data = res.data || []
    popularBooks.value = data.map((item, index) => ({
      ...item,
      rank: index + 1
    }))
  } catch (error) {
    console.error('Failed to load popular books:', error)
  }
}

const loadMonthlyTrend = async () => {
  try {
    const res = await getMonthlyStats(12)
    monthlyTrend.value = res.data || []
  } catch (error) {
    console.error('Failed to load monthly trend:', error)
  }
}

const loadAllStats = () => {
  loadSummaryData()
  loadBorrowRanking()
  loadPopularBooks()
  loadMonthlyTrend()
}

onMounted(() => {
  loadAllStats()
})
</script>

<style scoped>
.stats {
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

.date-pickers {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  padding: 0 5px;
  color: #909399;
}

.mt-20 {
  margin-top: 20px;
}

.stat-summary-card {
  margin-bottom: 0;
}

.summary-content {
  display: flex;
  align-items: center;
}

.summary-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.borrow-summary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.return-summary {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.visit-summary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.summary-info {
  margin-left: 16px;
}

.summary-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.summary-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
</style>
