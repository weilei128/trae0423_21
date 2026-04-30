<template>
  <div class="book-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">书目管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加图书
            </el-button>
            <el-button type="success" @click="handleBatchImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="书名/作者/ISBN"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable>
            <el-option label="文学" value="文学" />
            <el-option label="科技" value="科技" />
            <el-option label="历史" value="历史" />
            <el-option label="经济" value="经济" />
            <el-option label="教育" value="教育" />
            <el-option label="其他" value="其他" />
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

      <el-table
        :data="tableData"
        stripe
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="publisher" label="出版社" width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="80" />
        <el-table-column prop="totalCopies" label="总册数" width="80" align="center" />
        <el-table-column prop="availableCopies" label="可借" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.availableCopies > 0 ? 'success' : 'danger'">
              {{ scope.row.availableCopies }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="formData.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="formData.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期">
          <el-date-picker
            v-model="formData.publishDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="文学" value="文学" />
            <el-option label="科技" value="科技" />
            <el-option label="历史" value="历史" />
            <el-option label="经济" value="经济" />
            <el-option label="教育" value="教育" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="总册数" prop="totalCopies">
          <el-input-number v-model="formData.totalCopies" :min="0" />
        </el-form-item>
        <el-form-item label="可借册数">
          <el-input-number v-model="formData.availableCopies" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="importVisible"
      title="批量导入"
      width="500px"
    >
      <el-upload
        class="upload-demo"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".xlsx,.xls,.csv"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">只能上传 xlsx/xls/csv 文件</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookPage, createBook, updateBook, deleteBook, batchDeleteBooks } from '@/api/book'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])
const dialogVisible = ref(false)
const importVisible = ref(false)
const formRef = ref(null)
const uploadFile = ref(null)

const searchForm = reactive({
  keyword: '',
  category: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  publishDate: '',
  category: '',
  totalCopies: 0,
  availableCopies: 0,
  description: ''
})

const formRules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  totalCopies: [{ required: true, message: '请输入总册数', trigger: 'blur' }]
}

const dialogTitle = computed(() => formData.id ? '编辑图书' : '添加图书')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getBookPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load books:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.category = ''
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

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const resetForm = () => {
  formData.id = null
  formData.isbn = ''
  formData.title = ''
  formData.author = ''
  formData.publisher = ''
  formData.publishDate = ''
  formData.category = ''
  formData.totalCopies = 0
  formData.availableCopies = 0
  formData.description = ''
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
    await ElMessageBox.confirm('确定要删除该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBook(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete book:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateBook(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createBook(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('Failed to submit book:', error)
      }
    }
  })
}

const handleBatchImport = () => {
  importVisible.value = true
}

const handleFileChange = (file) => {
  uploadFile.value = file
}

const handleImportSubmit = () => {
  ElMessage.info('批量导入功能需要后端处理文件解析，这里只是示例')
  importVisible.value = false
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.book-container {
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
