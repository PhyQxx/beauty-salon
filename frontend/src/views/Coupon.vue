<template>
  <div class="coupon-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="优惠券名称">
          <el-input v-model="searchForm.name" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="优惠券类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="满减券" :value="1" />
            <el-option label="折扣券" :value="2" />
            <el-option label="兑换券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待发放" :value="0" />
            <el-option label="已发放" :value="1" />
            <el-option label="已使用" :value="2" />
            <el-option label="已过期" :value="3" />
            <el-option label="已撤回" :value="4" />
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
        <span>优惠券列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增优惠券</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)" size="small">
              {{ typeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="面值/折扣" width="100">
          <template #default="{ row }">
            {{ row.type === 2 ? row.value + '折' : '¥' + row.value }}
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="使用门槛" width="100">
          <template #default="{ row }">
            {{ row.minAmount > 0 ? '满¥' + row.minAmount : '无门槛' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放数量" width="90" />
        <el-table-column prop="usedCount" label="已使用" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="160" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDistribute(row)">发放</el-button>
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
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠券类型" prop="type">
          <el-select v-model="formData.type" placeholder="选择优惠券类型" @change="handleTypeChange">
            <el-option label="满减券" :value="1" />
            <el-option label="折扣券" :value="2" />
            <el-option label="兑换券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="面值" prop="value" v-if="formData.type !== 2">
          <el-input-number v-model="formData.value" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="折扣" prop="value" v-else>
          <el-input-number v-model="formData.value" :min="0" :max="10" :precision="1" />
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="formData.minAmount" :min="0" :precision="2" placeholder="0表示无门槛" />
        </el-form-item>
        <el-form-item label="发放数量" prop="totalCount">
          <el-input-number v-model="formData.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="有效期" prop="validDays">
          <el-input-number v-model="formData.validDays" :min="1" placeholder="发放后多少天有效" />
        </el-form-item>
        <el-form-item label="使用说明" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入使用说明" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="优惠券详情" v-model="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="优惠券名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="优惠券类型">
          <el-tag :type="typeTagType(detailData.type)" size="small">
            {{ typeName(detailData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="面值/折扣">
          {{ detailData.type === 2 ? detailData.value + '折' : '¥' + detailData.value }}
        </el-descriptions-item>
        <el-descriptions-item label="使用门槛">
          {{ detailData.minAmount > 0 ? '满¥' + detailData.minAmount : '无门槛' }}
        </el-descriptions-item>
        <el-descriptions-item label="发放数量">{{ detailData.totalCount }}</el-descriptions-item>
        <el-descriptions-item label="已使用">{{ detailData.usedCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ statusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="有效期">{{ detailData.validDays }}天</el-descriptions-item>
        <el-descriptions-item label="使用说明" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 发放弹窗 -->
    <el-dialog title="发放优惠券" v-model="distributeVisible" width="500px">
      <el-form ref="distributeRef" :model="distributeForm" label-width="100px">
        <el-form-item label="优惠券">
          <span>{{ distributeForm.couponName }}</span>
        </el-form-item>
        <el-form-item label="发放数量">
          <span>{{ distributeForm.availableCount }}</span>
        </el-form-item>
        <el-form-item label="发放方式">
          <el-radio-group v-model="distributeForm.distributeType">
            <el-radio :label="1">指定客户</el-radio>
            <el-radio :label="2">批量发放</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择客户" v-if="distributeForm.distributeType === 1" prop="customerIds">
          <el-select v-model="distributeForm.customerIds" multiple placeholder="请选择客户" style="width: 100%;">
            <el-option v-for="item in customerList" :key="item.id" :label="item.name + ' (' + item.phone + ')'" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级" v-if="distributeForm.distributeType === 2">
          <el-checkbox-group v-model="distributeForm.memberLevels">
            <el-checkbox :label="1">普通</el-checkbox>
            <el-checkbox :label="2">银卡</el-checkbox>
            <el-checkbox :label="3">金卡</el-checkbox>
            <el-checkbox :label="4">钻石</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="distributeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDistributeSubmit">确定发放</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCouponList, getCouponById, addCoupon, updateCoupon, deleteCoupon, distributeCoupon, batchDistributeCoupon } from '@/api/campaign'
import { getCustomerSimpleList } from '@/api/crm/customer'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const distributeVisible = ref(false)
const dialogTitle = ref('新增优惠券')
const formRef = ref(null)
const distributeRef = ref(null)

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
  value: 0,
  minAmount: 0,
  totalCount: 100,
  validDays: 30,
  description: ''
})

const detailData = ref(null)

const customerList = ref([])

const distributeForm = reactive({
  couponId: null,
  couponName: '',
  availableCount: 0,
  distributeType: 1,
  customerIds: [],
  memberLevels: []
})

const formRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入面值或折扣', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发放数量', trigger: 'blur' }],
  validDays: [{ required: true, message: '请输入有效期天数', trigger: 'blur' }]
}

const typeName = (type) => {
  const map = { 1: '满减券', 2: '折扣券', 3: '兑换券' }
  return map[type] || '未知'
}

const typeTagType = (type) => {
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[type] || ''
}

const statusName = (status) => {
  const map = { 0: '待发放', 1: '已发放', 2: '已使用', 3: '已过期', 4: '已撤回' }
  return map[status] || '未知'
}

const statusTagType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger', 4: '' }
  return map[status] || ''
}

const handleTypeChange = () => {
  if (formData.type === 2) {
    formData.value = 9.5
  } else {
    formData.value = 0
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
    const res = await getCouponList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取优惠券列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCustomerList = async () => {
  try {
    const res = await getCustomerSimpleList()
    customerList.value = res || []
  } catch (error) {
    console.error('获取客户列表失败', error)
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
  dialogTitle.value = '新增优惠券'
  Object.keys(formData).forEach(key => {
    if (key === 'type') formData[key] = 1
    else if (key === 'value') formData[key] = 0
    else if (key === 'minAmount') formData[key] = 0
    else if (key === 'totalCount') formData[key] = 100
    else if (key === 'validDays') formData[key] = 30
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑优惠券'
  try {
    const res = await getCouponById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] || ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取优惠券信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getCouponById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取优惠券详情失败')
  }
}

const handleDistribute = (row) => {
  distributeForm.couponId = row.id
  distributeForm.couponName = row.name
  distributeForm.availableCount = row.totalCount - row.usedCount
  distributeForm.distributeType = 1
  distributeForm.customerIds = []
  distributeForm.memberLevels = []
  distributeVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCoupon(row.id)
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
          await updateCoupon(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await addCoupon(formData)
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

const handleDistributeSubmit = async () => {
  try {
    if (distributeForm.distributeType === 1) {
      if (!distributeForm.customerIds.length) {
        ElMessage.warning('请选择客户')
        return
      }
      await distributeCoupon(distributeForm.couponId, distributeForm.customerIds)
    } else {
      if (!distributeForm.memberLevels.length) {
        ElMessage.warning('请选择客户等级')
        return
      }
      await batchDistributeCoupon({
        couponId: distributeForm.couponId,
        memberLevels: distributeForm.memberLevels
      })
    }
    ElMessage.success('发放成功')
    distributeVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('发放失败')
  }
}

onMounted(() => {
  fetchData()
  fetchCustomerList()
})
</script>

<style scoped>
.coupon-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
