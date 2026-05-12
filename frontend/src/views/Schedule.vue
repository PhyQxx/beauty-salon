<template>
  <div class="schedule-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="美容师">
          <el-select v-model="searchForm.beauticianId" placeholder="选择美容师" clearable>
            <el-option
              v-for="item in beauticianList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleBatch">批量排班</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>排班列表</span>
          <el-button type="primary" size="small" @click="handleAdd">新增排班</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="beauticianName" label="美容师" width="120" />
        <el-table-column prop="workDate" label="工作日期" width="120" />
        <el-table-column prop="startTime" label="上班时间" width="100" />
        <el-table-column prop="endTime" label="下班时间" width="100" />
        <el-table-column prop="isWorking" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isWorking === 1 ? 'success' : 'info'" size="small">
              {{ row.isWorking === 1 ? '上班' : '休息' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maxAppointments" label="最大预约数" width="110" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="美容师">
          <el-select v-model="formData.beauticianId" placeholder="选择美容师" :disabled="isEdit">
            <el-option
              v-for="item in beauticianList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工作日期">
          <el-date-picker v-model="formData.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="上班时间">
          <el-time-picker v-model="formData.startTime" placeholder="上班时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="下班时间">
          <el-time-picker v-model="formData.endTime" placeholder="下班时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="是否上班">
          <el-radio-group v-model="formData.isWorking">
            <el-radio :label="1">上班</el-radio>
            <el-radio :label="0">休息</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最大预约数">
          <el-input-number v-model="formData.maxAppointments" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" rows="2" placeholder="备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量排班弹窗 -->
    <el-dialog title="批量排班" v-model="batchVisible" width="500px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="美容师">
          <el-select v-model="batchForm.beauticianId" placeholder="选择美容师">
            <el-option
              v-for="item in beauticianList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="batchDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="上班时间">
          <el-time-picker v-model="batchForm.startTime" placeholder="上班时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="下班时间">
          <el-time-picker v-model="batchForm.endTime" placeholder="下班时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="是否上班">
          <el-radio-group v-model="batchForm.isWorking">
            <el-radio :label="1">上班</el-radio>
            <el-radio :label="0">休息</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最大预约数">
          <el-input-number v-model="batchForm.maxAppointments" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="batchForm.remark" type="textarea" rows="2" placeholder="备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const batchVisible = ref(false)
const dialogTitle = ref('新增排班')
const isEdit = ref(false)
const dateRange = ref([])
const batchDateRange = ref([])

const beauticianList = ref([])

const searchForm = reactive({
  beauticianId: null
})

const formData = reactive({
  id: null,
  beauticianId: null,
  workDate: '',
  startTime: '09:00:00',
  endTime: '18:00:00',
  isWorking: 1,
  maxAppointments: 5,
  remark: ''
})

const batchForm = reactive({
  beauticianId: null,
  startDate: '',
  endDate: '',
  startTime: '09:00:00',
  endTime: '18:00:00',
  isWorking: 1,
  maxAppointments: 5,
  remark: ''
})

const fetchBeauticians = async () => {
  try {
    const res = await request({ url: '/beautician/list', method: 'get', params: { page: 1, limit: 100 } })
    beauticianList.value = res.data || []
  } catch (error) {
    console.error('获取美容师列表失败', error)
  }
}

const fetchData = async () => {
  if (!searchForm.beauticianId) {
    tableData.value = []
    return
  }
  loading.value = true
  try {
    const startDate = dateRange.value && dateRange.value[0] ? dateRange.value[0] : new Date().toISOString().split('T')[0]
    const endDate = dateRange.value && dateRange.value[1] ? dateRange.value[1] : startDate
    const res = await request({
      url: '/beautician/schedule/list',
      method: 'get',
      params: {
        beauticianId: searchForm.beauticianId,
        startDate,
        endDate
      }
    })
    const list = res || []
    // 补充美容师名称
    tableData.value = list.map(item => {
      const b = beauticianList.value.find(x => x.id === item.beauticianId)
      return { ...item, beauticianName: b ? b.name : item.beauticianId }
    })
  } catch (error) {
    ElMessage.error('获取排班列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchData()
}

const handleReset = () => {
  searchForm.beauticianId = null
  dateRange.value = []
  tableData.value = []
}

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    beauticianId: searchForm.beauticianId,
    workDate: '',
    startTime: '09:00:00',
    endTime: '18:00:00',
    isWorking: 1,
    maxAppointments: 5,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑排班'
  isEdit.value = true
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await request({ url: `/beautician/schedule/${formData.id}`, method: 'put', data: formData })
      ElMessage.success('更新成功')
    } else {
      await request({ url: '/beautician/schedule', method: 'post', data: formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该排班记录？', '提示', { type: 'warning' })
    await request({ url: `/beautician/schedule/${row.id}`, method: 'delete' })
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatch = () => {
  batchForm.beauticianId = searchForm.beauticianId
  batchDateRange.value = []
  batchForm.startTime = '09:00:00'
  batchForm.endTime = '18:00:00'
  batchForm.isWorking = 1
  batchForm.maxAppointments = 5
  batchForm.remark = ''
  batchVisible.value = true
}

const handleBatchSubmit = async () => {
  if (!batchForm.beauticianId) {
    ElMessage.warning('请选择美容师')
    return
  }
  if (!batchDateRange.value || batchDateRange.value.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  try {
    await request({
      url: '/beautician/schedule/batch',
      method: 'post',
      params: {
        beauticianId: batchForm.beauticianId,
        startDate: batchDateRange.value[0],
        endDate: batchDateRange.value[1],
        startTime: batchForm.startTime,
        endTime: batchForm.endTime,
        isWorking: batchForm.isWorking,
        maxAppointments: batchForm.maxAppointments,
        remark: batchForm.remark
      }
    })
    ElMessage.success('批量排班成功')
    batchVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('批量排班失败')
  }
}

onMounted(() => {
  fetchBeauticians()
})
</script>

<style scoped>
.schedule-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
