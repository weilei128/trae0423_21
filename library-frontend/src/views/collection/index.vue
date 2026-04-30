<template>
  <div class="collection-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">馆藏管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加馆藏
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="图书">
          <el-select
            v-model="searchForm.bookId"
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
        <el-form-item label="条码">
          <el-input
            v-model="searchForm.barcode"
            placeholder="请输入条码"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="在架" :value="0" />
            <el-option label="借出" :value="1" />
            <el-option label="预约中" :value="2" />
            <el-option label="损坏" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column prop="barcode" label="馆藏条码" width="140" />
        <el-table-column prop="location" label="库位" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="图书" prop="bookId">
          <el-select
            v-model="formData.bookId"
            placeholder="请选择图书"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="book in bookList"
              :key="book.id"
              :label="book.title"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="条码" prop="barcode">
          <el-input v-model="formData.barcode" placeholder="请输入馆藏条码" />
        </el-form-item>
        <el-form-item label="库位">
          <el-input v-model="formData.location" placeholder="请输入库位" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="在架" :value="0" />
            <el-option label="借出" :value="1" />
            <el-option label="预约中" :value="2" />
            <el-option label="损坏" :value="3" />
          </el-select>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCollectionPage, createCollection, updateCollection, deleteCollection } from '@/api/collection'
import { getBookList } from '@/api/book'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const bookList = ref([])

const searchForm = reactive({
  bookId: null,
  barcode: '',
  status: null
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  bookId: null,
  barcode: '',
  location: '',
  status: 0
})

const formRules = {
  bookId: [{ required: true, message: '请选择图书', trigger: 'change' }],
  barcode: [{ required: true, message: '请输入条码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const dialogTitle = computed(() => formData.id ? '编辑馆藏' : '添加馆藏')

const statusMap = {
  0: { text: '在架', type: 'success' },
  1: { text: '借出', type: 'danger' },
  2: { text: '预约中', type: 'warning' },
  3: { text: '损坏', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const loadBooks = async () => {
  try {
    const res = await getBookList()
    bookList.value = res.data || []
  } catch (error) {
    console.error('Failed to load books:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getCollectionPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load collections:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadData()
}

const handleReset = () => {
  searchForm.bookId = null
  searchForm.barcode = ''
  searchForm.status = null
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadData()
}

const resetForm = () => {
  formData.id = null
  formData.bookId = null
  formData.barcode = ''
  formData.location = ''
  formData.status = 0
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该馆藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCollection(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete collection:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateCollection(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createCollection(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('Failed to submit collection:', error)
      }
    }
  })
}

onMounted(() => {
  loadBooks()
  loadData()
})
</script>

<style scoped>
.collection-container {
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
