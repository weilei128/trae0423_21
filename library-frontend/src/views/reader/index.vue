<template>
  <div class="reader-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">读者管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加读者
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="姓名/读者证/电话"
            clearable
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="教师" value="教师" />
            <el-option label="学生" value="学生" />
            <el-option label="职工" value="职工" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="黑名单">
          <el-select v-model="searchForm.isBlacklist" placeholder="请选择" clearable>
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
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
        <el-table-column prop="readerCard" label="读者证" width="140" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="maxBorrowCount" label="最大借阅" width="100" align="center" />
        <el-table-column prop="expiryDate" label="有效期" width="120" />
        <el-table-column prop="isBlacklist" label="黑名单" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isBlacklist === 1 ? 'danger' : 'success'">
              {{ scope.row.isBlacklist === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.isBlacklist !== 1"
              type="warning"
              link
              @click="handleAddBlacklist(scope.row)"
            >拉黑</el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleRemoveBlacklist(scope.row)"
            >移除</el-button>
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
        <el-form-item label="读者证" prop="readerCard">
          <el-input v-model="formData.readerCard" placeholder="请输入读者证号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="教师" value="教师" />
            <el-option label="学生" value="学生" />
            <el-option label="职工" value="职工" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大借阅数">
          <el-input-number v-model="formData.maxBorrowCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker
            v-model="formData.expiryDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="blacklistDialogVisible"
      title="加入黑名单"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="原因">
          <el-input
            v-model="blacklistReason"
            type="textarea"
            :rows="3"
            placeholder="请输入拉黑原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="blacklistDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBlacklist">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getReaderPage,
  createReader,
  updateReader,
  deleteReader,
  addToBlacklist,
  removeFromBlacklist
} from '@/api/reader'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const blacklistDialogVisible = ref(false)
const formRef = ref(null)
const currentReader = ref(null)
const blacklistReason = ref('')

const searchForm = reactive({
  keyword: '',
  type: '',
  isBlacklist: null
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  readerCard: '',
  name: '',
  gender: '男',
  phone: '',
  email: '',
  type: '',
  maxBorrowCount: 10,
  expiryDate: '',
  isBlacklist: 0
})

const formRules = {
  readerCard: [{ required: true, message: '请输入读者证号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const dialogTitle = computed(() => formData.id ? '编辑读者' : '添加读者')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getReaderPage(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('Failed to load readers:', error)
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
  searchForm.type = ''
  searchForm.isBlacklist = null
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
  formData.readerCard = ''
  formData.name = ''
  formData.gender = '男'
  formData.phone = ''
  formData.email = ''
  formData.type = ''
  formData.maxBorrowCount = 10
  formData.expiryDate = ''
  formData.isBlacklist = 0
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
    await ElMessageBox.confirm('确定要删除该读者吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReader(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete reader:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateReader(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createReader(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('Failed to submit reader:', error)
      }
    }
  })
}

const handleAddBlacklist = (row) => {
  currentReader.value = row
  blacklistReason.value = ''
  blacklistDialogVisible.value = true
}

const submitBlacklist = async () => {
  try {
    await addToBlacklist(currentReader.value.id, blacklistReason.value)
    ElMessage.success('已加入黑名单')
    blacklistDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('Failed to add to blacklist:', error)
  }
}

const handleRemoveBlacklist = async (row) => {
  try {
    await ElMessageBox.confirm('确定要从黑名单中移除该读者吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromBlacklist(row.id)
    ElMessage.success('已从黑名单移除')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to remove from blacklist:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reader-container {
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
