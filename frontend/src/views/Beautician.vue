<template>
  <div class="beautician-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="searchForm.position" placeholder="全部" clearable>
            <el-option label="美容师" :value="1" />
            <el-option label="高级美容师" :value="2" />
            <el-option label="美容顾问" :value="3" />
            <el-option label="店长" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
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
        <span>美容师列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增美容师</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">
            {{ row.gender === 0 ? '女' : row.gender === 1 ? '男' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" width="110">
          <template #default="{ row }">
            <el-tag :type="positionTagType(row.position)" size="small">
              {{ positionName(row.position) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="specialty" label="专长" min-width="150" show-overflow-tooltip />
        <el-table-column prop="experience" label="从业年限" width="90">
          <template #default="{ row }">
            {{ row.experience }}年
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入职日期" width="110" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :label="0">女</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-select v-model="formData.position" placeholder="选择职位">
            <el-option label="美容师" :value="1" />
            <el-option label="高级美容师" :value="2" />
            <el-option label="美容顾问" :value="3" />
            <el-option label="店长" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="formData.specialty" placeholder="如：面部护理、身体按摩、化妆等" />
        </el-form-item>
        <el-form-item label="从业年限" prop="experience">
          <el-input-number v-model="formData.experience" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker v-model="formData.entryDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="简介" prop="introduction">
          <el-input v-model="formData.introduction" type="textarea" rows="3" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="美容师详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === 0 ? '女' : detailData.gender === 1 ? '男' : '未知' }}</el-descriptions-item>
        <el-descriptions-item label="职位">
          <el-tag :type="positionTagType(detailData.position)" size="small">
            {{ positionName(detailData.position) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="从业年限">{{ detailData.experience }}年</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ detailData.entryDate }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-model="detailData.rating" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="专长" :span="2">{{ detailData.specialty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detailData.introduction || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBeauticianList, getBeauticianById, addBeautician, updateBeautician, deleteBeautician } from '@/api/beautician'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增美容师')
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  position: null,
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const formData = reactive({
  id: null,
  name: '',
  phone: '',
  gender: 0,
  position: 1,
  specialty: '',
  experience: 0,
  entryDate: '',
  introduction: '',
  remark: ''
})

const detailData = ref(null)

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const positionName = (position) => {
  const map = { 1: '美容师', 2: '高级美容师', 3: '美容顾问', 4: '店长' }
  return map[position] || '美容师'
}

const positionTagType = (position) => {
  const map = { 1: '', 2: 'success', 3: 'warning', 4: 'danger' }
  return map[position] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getBeauticianList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取美容师列表失败')
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
  searchForm.position = null
  searchForm.status = null
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
  dialogTitle.value = '新增美容师'
  Object.keys(formData).forEach(key => {
    if (key === 'gender') formData[key] = 0
    else if (key === 'position') formData[key] = 1
    else if (key === 'experience') formData[key] = 0
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑美容师'
  try {
    const res = await getBeauticianById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] !== undefined ? res[key] : ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取美容师信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getBeauticianById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取美容师详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该美容师吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBeautician(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateBeautician(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await addBeautician(formData)
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.beautician-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
