<template>
  <div class="campaign-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.name" placeholder="请输入活动名称" clearable />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="满减" :value="1" />
            <el-option label="折扣" :value="2" />
            <el-option label="赠品" :value="3" />
            <el-option label="积分" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
            <el-option label="已取消" :value="3" />
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
        <span>活动列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增活动</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="活动名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)" size="small">
              {{ typeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="200">
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="选择活动类型">
            <el-option label="满减" :value="1" />
            <el-option label="折扣" :value="2" />
            <el-option label="赠品" :value="3" />
            <el-option label="积分" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="活动规则" prop="rules">
          <el-input v-model="formData.rules" type="textarea" rows="3" placeholder="请输入活动规则" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="活动详情" v-model="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="活动名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="活动类型">
          <el-tag :type="typeTagType(detailData.type)" size="small">
            {{ typeName(detailData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailData.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailData.endTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ statusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="参与人数">{{ detailData.participantCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="活动描述" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="活动规则" :span="2">{{ detailData.rules || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCampaignList, getCampaignById, addCampaign, updateCampaign, deleteCampaign } from '@/api/campaign'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增活动')
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  type: null,
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
  type: 1,
  startTime: '',
  endTime: '',
  description: '',
  rules: ''
})

const detailData = ref(null)

const formRules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const typeName = (type) => {
  const map = { 1: '满减', 2: '折扣', 3: '赠品', 4: '积分' }
  return map[type] || '未知'
}

const typeTagType = (type) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'info' }
  return map[type] || ''
}

const statusName = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已结束', 3: '已取消' }
  return map[status] || '未知'
}

const statusTagType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getCampaignList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.type = null
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
  dialogTitle.value = '新增活动'
  Object.keys(formData).forEach(key => {
    if (key === 'type') formData[key] = 1
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑活动'
  try {
    const res = await getCampaignById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] || ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取活动信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getCampaignById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取活动详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCampaign(row.id)
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
          await updateCampaign(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await addCampaign(formData)
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
.campaign-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
