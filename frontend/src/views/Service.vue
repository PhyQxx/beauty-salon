<template>
  <div class="service-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="项目名称" clearable />
        </el-form-item>
        <el-form-item label="服务分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部" clearable>
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.isActive" placeholder="全部" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <div slot="header" class="clearfix">
        <span>服务项目列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增项目</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="项目名称" width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="isActive" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'info'" size="small">
              {{ row.isActive === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleToggleStatus(row)">
              {{ row.isActive === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="text" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.page"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.limit"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="服务分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="formData.duration" :min="0" :step="15" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="formData.isActive">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="项目详情" v-model="detailVisible" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="项目名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="服务分类">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ detailData.price }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ detailData.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sort }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.isActive === 1 ? 'success' : 'info'" size="small">
            {{ detailData.isActive === 1 ? '上架' : '下架' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getServiceList, getCategories, getServiceById, addService, updateService, deleteService, updateServiceStatus } from '@/api/pos/service'

const loading = ref(false)
const tableData = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增项目')
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  isActive: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const formData = reactive({
  id: null,
  name: '',
  categoryId: null,
  price: 0,
  duration: 60,
  description: '',
  sort: 0,
  isActive: 1
})

const detailData = ref(null)

const formRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择服务分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res || []
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getServiceList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取服务项目列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.isActive = null
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.limit = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增项目'
  Object.keys(formData).forEach(key => {
    if (key === 'isActive') formData[key] = 1
    else if (key === 'price') formData[key] = 0
    else if (key === 'duration') formData[key] = 60
    else if (key === 'sort') formData[key] = 0
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑项目'
  try {
    const res = await getServiceById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] !== undefined ? res[key] : ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取项目信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getServiceById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取项目详情失败')
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateService(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await addService(formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  const action = row.isActive === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定要${action}该项目吗？`, '提示')
    await updateServiceStatus(row.id, row.isActive === 1 ? 0 : 1)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该项目吗？', '警告', { type: 'warning' })
    await deleteService(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>

<style scoped>
.service-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
