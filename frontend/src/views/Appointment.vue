<template>
  <div class="appointment-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户姓名" clearable/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable/>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="searchForm.appointmentDate" type="date" placeholder="选择日期"
                          value-format="yyyy-MM-dd"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待确认" :value="0"/>
            <el-option label="已确认" :value="1"/>
            <el-option label="已到店" :value="2"/>
            <el-option label="服务中" :value="3"/>
            <el-option label="已完成" :value="4"/>
            <el-option label="已取消" :value="5"/>
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
        <span>预约列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增预约</el-button>
      </div>

      <!-- 状态流转说明 -->
      <el-alert
          title="状态流转: 待确认 → 已确认 → 已到店 → 服务中 → 已完成"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 15px;"
      />

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="customerName" label="客户姓名" width="100"/>
        <el-table-column prop="customerPhone" label="手机号" width="130"/>
        <el-table-column prop="serviceName" label="服务项目" min-width="120"/>
        <el-table-column prop="beauticianName" label="技师" width="90"/>
        <el-table-column prop="appointmentDate" label="预约日期" width="110"/>
        <el-table-column prop="appointmentTime" label="预约时间" width="100"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="90"/>
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip/>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button type="text" size="small" @click="handleConfirm(row)" v-if="row.status === 0">确认</el-button>
            <el-button type="text" size="small" @click="handleArrive(row)" v-if="row.status === 1">到店</el-button>
            <el-button type="text" size="small" @click="handleStartService(row)" v-if="row.status === 2">开始服务
            </el-button>
            <el-button type="text" size="small" @click="handleComplete(row)" v-if="row.status === 3">完成</el-button>
            <el-button type="text" size="small" @click="handleCancel(row)" v-if="row.status !== 4 && row.status !== 5">
              取消
            </el-button>
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
  </div>

  <!-- 新增/编辑弹窗 -->
  <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="handleDialogClose">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input v-model="formData.customerName" placeholder="请输入客户姓名"/>
      </el-form-item>
      <el-form-item label="手机号" prop="customerPhone">
        <el-input v-model="formData.customerPhone" placeholder="请输入手机号"/>
      </el-form-item>
      <el-form-item label="服务项目" prop="serviceId">
        <el-select v-model="formData.serviceId" placeholder="请选择服务项目" @change="handleServiceChange">
          <el-option v-for="service in serviceList" :key="service.id" :label="service.name" :value="service.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="技师" prop="beauticianId">
        <el-select v-model="formData.beauticianId" placeholder="请选择技师">
          <el-option v-for="beautician in beauticianList" :key="beautician.id" :label="beautician.name"
                     :value="beautician.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="预约日期" prop="appointmentDate">
        <el-date-picker v-model="formData.appointmentDate" type="date" placeholder="选择日期"
                        value-format="yyyy-MM-dd"/>
      </el-form-item>
      <el-form-item label="预约时间" prop="appointmentTime">
        <el-time-picker v-model="formData.appointmentTime" placeholder="选择时间" format="HH:mm" value-format="HH:mm"/>
      </el-form-item>
      <el-form-item label="时长" prop="duration">
        <el-input-number v-model="formData.duration" :min="15" :step="15"/>
        分钟
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="formData.price" :min="0" :precision="2"/>
        元
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" rows="2" placeholder="请输入备注"/>
      </el-form-item>
    </el-form>
    <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
  </el-dialog>

  <!-- 详情弹窗 -->
  <el-dialog title="预约详情" v-model="detailVisible" width="700px">
    <el-descriptions :column="2" border v-if="detailData">
      <el-descriptions-item label="预约编号">{{ detailData.id }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTagType(detailData.status)" size="small">
          {{ statusName(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="客户姓名">{{ detailData.customerName }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData.customerPhone }}</el-descriptions-item>
      <el-descriptions-item label="服务项目">{{ detailData.serviceName }}</el-descriptions-item>
      <el-descriptions-item label="技师">{{ detailData.beauticianName }}</el-descriptions-item>
      <el-descriptions-item label="预约日期">{{ detailData.appointmentDate }}</el-descriptions-item>
      <el-descriptions-item label="预约时间">{{ detailData.appointmentTime }}</el-descriptions-item>
      <el-descriptions-item label="服务时长">{{ detailData.duration }} 分钟</el-descriptions-item>
      <el-descriptions-item label="价格">¥{{ detailData.price }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
    </el-descriptions>

    <!-- 状态操作按钮 -->
    <div style="margin-top: 20px; text-align: center;">
      <el-button type="primary" @click="handleConfirm(detailData)" v-if="detailData && detailData.status === 0">
        确认预约
      </el-button>
      <el-button type="success" @click="handleArrive(detailData)" v-if="detailData && detailData.status === 1">
        客户到店
      </el-button>
      <el-button type="warning" @click="handleStartService(detailData)" v-if="detailData && detailData.status === 2">
        开始服务
      </el-button>
      <el-button type="success" @click="handleComplete(detailData)" v-if="detailData && detailData.status === 3">
        完成服务
      </el-button>
      <el-button type="danger" @click="handleCancel(detailData)"
                 v-if="detailData && detailData.status !== 4 && detailData.status !== 5">取消预约
      </el-button>
    </div>
  </el-dialog>

  <!-- 取消原因弹窗 -->
  <el-dialog title="取消预约" v-model="cancelDialogVisible" width="400px">
    <el-form ref="cancelRef" :model="cancelForm" :rules="cancelRules" label-width="80px">
      <el-form-item label="预约编号">
        <span>{{ cancelForm.appointmentId }}</span>
      </el-form-item>
      <el-form-item label="客户姓名">
        <span>{{ cancelForm.customerName }}</span>
      </el-form-item>
      <el-form-item label="取消原因" prop="reason">
        <el-input v-model="cancelForm.reason" type="textarea" rows="3" placeholder="请输入取消原因"/>
      </el-form-item>
    </el-form>
    <span slot="footer">
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleCancelSubmit">确认取消</el-button>
      </span>
  </el-dialog>
</template>

<script setup>
import {reactive, ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  getAppointmentList,
  getAppointmentById,
  createAppointment,
  updateAppointment,
  cancelAppointment,
  confirmAppointment,
  arriveAppointment,
  startAppointmentService,
  completeAppointment,
  getAvailableSlots
} from '@/api/appointment'
import {getActiveServices} from '@/api/pos/service'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const cancelDialogVisible = ref(false)
const dialogTitle = ref('新增预约')
const formRef = ref(null)
const cancelRef = ref(null)

const serviceList = ref([])
const beauticianList = ref([
  {id: 1, name: '张三'},
  {id: 2, name: '李四'},
  {id: 3, name: '王五'}
])

const searchForm = reactive({
  customerName: '',
  phone: '',
  appointmentDate: '',
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const formData = reactive({
  id: null,
  customerName: '',
  customerPhone: '',
  serviceId: null,
  serviceName: '',
  beauticianId: null,
  beauticianName: '',
  appointmentDate: '',
  appointmentTime: '',
  duration: 60,
  price: 0,
  remark: ''
})

const detailData = ref(null)

const cancelForm = reactive({
  appointmentId: null,
  customerName: '',
  reason: ''
})

const formRules = {
  customerName: [{required: true, message: '请输入客户姓名', trigger: 'blur'}],
  customerPhone: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur'}
  ],
  serviceId: [{required: true, message: '请选择服务项目', trigger: 'change'}],
  beauticianId: [{required: true, message: '请选择技师', trigger: 'change'}],
  appointmentDate: [{required: true, message: '请选择预约日期', trigger: 'change'}],
  appointmentTime: [{required: true, message: '请选择预约时间', trigger: 'change'}]
}

const cancelRules = {
  reason: [{required: true, message: '请输入取消原因', trigger: 'blur'}]
}

// 状态: 0-待确认 1-已确认 2-已到店 3-服务中 4-已完成 5-已取消
const statusName = (status) => {
  const map = {0: '待确认', 1: '已确认', 2: '已到店', 3: '服务中', 4: '已完成', 5: '已取消'}
  return map[status] || '未知'
}

const statusTagType = (status) => {
  const map = {0: 'warning', 1: 'success', 2: 'primary', 3: 'warning', 4: 'info', 5: 'danger'}
  return map[status] || 'info'
}

const fetchServiceList = async () => {
  try {
    const res = await getActiveServices()
    serviceList.value = res || []
  } catch (error) {
    console.error('获取服务列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      customerName: searchForm.customerName || null,
      phone: searchForm.phone || null,
      appointmentDate: searchForm.appointmentDate || null,
      status: searchForm.status
    }
    const res = await getAppointmentList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.customerName = ''
  searchForm.phone = ''
  searchForm.appointmentDate = ''
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
  dialogTitle.value = '新增预约'
  Object.keys(formData).forEach(key => {
    if (key === 'duration') formData[key] = 60
    else if (key === 'price') formData[key] = 0
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑预约'
  try {
    const res = await getAppointmentById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] || ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取预约信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getAppointmentById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取预约详情失败')
  }
}

const handleServiceChange = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  if (service) {
    formData.serviceName = service.name
    formData.duration = service.duration || 60
    formData.price = service.price || 0
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 设置技师名称
        const beautician = beauticianList.value.find(b => b.id === formData.beauticianId)
        if (beautician) {
          formData.beauticianName = beautician.name
        }

        // 转换字段名与后端 DTO 对齐
        const submitData = {
          ...formData,
          serviceItemId: formData.serviceId,
          startTime: formData.appointmentTime,
          amount: formData.price
        }
        // 删除前端专有字段，避免干扰后端反序列化
        delete submitData.serviceId
        delete submitData.serviceName
        delete submitData.appointmentTime
        delete submitData.price
        delete submitData.beauticianName

        if (formData.id) {
          await updateAppointment(formData.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await createAppointment(submitData)
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

// 确认预约
const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认该预约?', '提示', {type: 'warning'})
    await confirmAppointment(row.id)
    ElMessage.success('预约已确认')
    detailVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 客户到店
const handleArrive = async (row) => {
  try {
    await ElMessageBox.confirm('确认客户已到店?', '提示', {type: 'success'})
    await arriveAppointment(row.id)
    ElMessage.success('客户已到店')
    detailVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 开始服务
const handleStartService = async (row) => {
  try {
    await ElMessageBox.confirm('开始为客户提供服务?', '提示', {type: 'warning'})
    await startAppointmentService(row.id)
    ElMessage.success('服务已开始')
    detailVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 完成服务
const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确认服务已完成?', '提示', {type: 'success'})
    await completeAppointment(row.id)
    ElMessage.success('服务已完成')
    detailVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 取消预约
const handleCancel = (row) => {
  cancelForm.appointmentId = row.id
  cancelForm.customerName = row.customerName
  cancelForm.reason = ''
  cancelDialogVisible.value = true
}

const handleCancelSubmit = async () => {
  await cancelRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await cancelAppointment(cancelForm.appointmentId, cancelForm.reason)
        ElMessage.success('预约已取消')
        cancelDialogVisible.value = false
        detailVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
  fetchServiceList()
})
</script>

<style scoped>
.appointment-page {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
}
</style>
