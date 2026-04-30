<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon book-icon">
              <el-icon size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalBooks || 0 }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon collection-icon">
              <el-icon size="32"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCollections || 0 }}</div>
              <div class="stat-label">馆藏总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon reader-icon">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalReaders || 0 }}</div>
              <div class="stat-label">读者总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon borrow-icon">
              <el-icon size="32"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeBorrows || 0 }}</div>
              <div class="stat-label">当前借阅</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">借阅提醒</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="超期图书">
              <el-tag type="danger" size="large">{{ stats.overdueCount || 0 }} 本</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="未缴罚款">
              <el-tag type="warning" size="large">{{ stats.unpaidFinesCount || 0 }} 笔</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="本月借阅">
              <el-tag type="primary" size="large">{{ stats.monthlyBorrows || 0 }} 册</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="本月归还">
              <el-tag type="success" size="large">{{ stats.monthlyReturns || 0 }} 册</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="goTo('/borrow')">
              <el-icon><Plus /></el-icon>
              借书登记
            </el-button>
            <el-button type="success" size="large" @click="goTo('/borrow')">
              <el-icon><Check /></el-icon>
              还书登记
            </el-button>
            <el-button type="warning" size="large" @click="goTo('/books')">
              <el-icon><Search /></el-icon>
              图书查询
            </el-button>
            <el-button type="info" size="large" @click="goTo('/readers')">
              <el-icon><UserFilled /></el-icon>
              读者查询
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">热门图书 TOP 10</span>
          </template>
          <el-table :data="popularBooks" stripe max-height="300">
            <el-table-column prop="rank" label="排名" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.rank <= 3" :type="getRankType(scope.row.rank)">
                  {{ scope.row.rank }}
                </el-tag>
                <span v-else>{{ scope.row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip />
            <el-table-column prop="author" label="作者" width="100" show-overflow-tooltip />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">近6个月借阅趋势</span>
          </template>
          <el-table :data="monthlyStats" stripe max-height="300">
            <el-table-column prop="month" label="月份" width="120" align="center" />
            <el-table-column prop="borrowCount" label="借阅量" width="120" align="center">
              <template #default="scope">
                <el-tag type="primary">{{ scope.row.borrowCount || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="returnCount" label="归还量" width="120" align="center">
              <template #default="scope">
                <el-tag type="success">{{ scope.row.returnCount || 0 }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOverviewStats, getPopularBooks, getMonthlyStats } from '@/api/stats'

const router = useRouter()

const stats = ref({})
const popularBooks = ref([])
const monthlyStats = ref([])

const loadStats = async () => {
  try {
    const res = await getOverviewStats()
    stats.value = res.data || {}
  } catch (error) {
    console.error('Failed to load overview stats:', error)
  }
}

const loadPopularBooks = async () => {
  try {
    const res = await getPopularBooks(10)
    popularBooks.value = res.data || []
  } catch (error) {
    console.error('Failed to load popular books:', error)
  }
}

const loadMonthlyStats = async () => {
  try {
    const res = await getMonthlyStats(6)
    monthlyStats.value = res.data || []
  } catch (error) {
    console.error('Failed to load monthly stats:', error)
  }
}

const goTo = (path) => {
  router.push(path)
}

const getRankType = (rank) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'info'
  return ''
}

onMounted(() => {
  loadStats()
  loadPopularBooks()
  loadMonthlyStats()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.book-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.collection-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.reader-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.borrow-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  margin-left: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.mt-20 {
  margin-top: 20px;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: center;
  padding: 20px 0;
}

.quick-actions .el-button {
  min-width: 120px;
}
</style>
