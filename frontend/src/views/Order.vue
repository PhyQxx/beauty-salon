<template>
  <div class="order-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="订单号" clearable />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="searchForm.orderType" placeholder="全部" clearable>
            <el-option label="服务订单" :value="1" />
            <el-option label="商品订单" :value="2" />
            <el-option label="套餐订单" :value="3" />
            <el-option label="充值订单" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="searchForm.payStatus" placeholder="全部" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已退款" :value="4" />
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">{{ statData.totalOrders }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statData.totalAmount }}</div>
            <div class="stat-label">订单总额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statData.netAmount }}</div>
            <div class="stat-label">实收金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statData.refundAmount }}</div>
            <div class="stat-label">退款金额</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <el-button type="primary" size="small" @click="handleCreate">新建订单</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="orderTypeName" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.orderTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template #default="{ row }">
            ¥{{ row.payAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="payStatusName" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="payStatusTagType(row.payStatus)" size="small">
              {{ row.payStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTypeName" label="支付方式" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.payStatus === 0 && row.status !== 3" type="text" size="small" @click="handlePay(row)">支付</el-button>
            <el-button v-if="row.payStatus === 1 && row.status === 1" type="text" size="small" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.payStatus === 1 && row.status !== 3 && row.status !== 4" type="text" size="small" @click="handleRefund(row)">退款</el-button>
            <el-button v-if="row.status !== 2 && row.status !== 3 && row.status !== 4" type="text" size="small" @click="handleCancel(row)">取消</el-button>
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

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" v-model="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ detailData.orderTypeName }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ detailData.customerPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ detailData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ detailData.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ detailData.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detailData.payTypeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="payStatusTagType(detailData.payStatus)" size="small">
            {{ detailData.payStatusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ detailData.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="美容师">{{ detailData.beauticianName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作员">{{ detailData.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款金额" v-if="detailData.refundAmount">¥{{ detailData.refundAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 订单明细 -->
      <div v-if="detailData && detailData.items && detailData.items.length" class="detail-section">
        <h4>订单明细</h4>
        <el-table :data="detailData.items" size="small" border>
          <el-table-column prop="productName" label="项目/商品" />
          <el-table-column prop="itemTypeName" label="类型" width="80" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row }">¥{{ row.unitPrice }}</template>
          </el-table-column>
          <el-table-column prop="subtotal" label="小计" width="100">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 支付弹窗 -->
    <el-dialog title="订单支付" v-model="payVisible" width="400px">
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ payForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="应付金额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ payForm.payAmount }}</span>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="payForm.payType" placeholder="选择支付方式">
            <el-option label="现金" :value="1" />
            <el-option label="银行卡" :value="2" />
            <el-option label="微信" :value="3" />
            <el-option label="支付宝" :value="4" />
            <el-option label="会员卡" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePaySubmit">确认支付</el-button>
      </template>
    </el-dialog>

    <!-- 退款弹窗 -->
    <el-dialog title="订单退款" v-model="refundVisible" width="400px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ refundForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="实付金额">
          <span>¥{{ refundForm.payAmount }}</span>
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input-number v-model="refundForm.refundAmount" :min="0.01" :max="refundForm.maxRefundAmount" :precision="2" />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-input v-model="refundForm.reason" type="textarea" rows="2" placeholder="请输入退款原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRefundSubmit">确认退款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrderList,
  getOrderById,
  payOrder,
  refundOrder,
  cancelOrder,
  completeOrder,
  getOrderStatistics
} from '@/api/pos/order'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const payVisible = ref(false)
const refundVisible = ref(false)
const detailData = ref(null)
const dateRange = ref([])

const statData = reactive({
  totalOrders: 0,
  totalAmount: 0,
  netAmount: 0,
  refundAmount: 0
})

const searchForm = reactive({
  orderNo: '',
  orderType: null,
  payStatus: null,
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const payForm = reactive({
  orderId: null,
  orderNo: '',
  payAmount: 0,
  payType: 1
})

const refundForm = reactive({
  orderId: null,
  orderNo: '',
  payAmount: 0,
  maxRefundAmount: 0,
  refundAmount: 0,
  reason: ''
})

const payStatusTagType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return map[status] || 'info'
}

const statusTagType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }
  return map[status] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getOrderList(params)
    tableData.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const today = new Date().toISOString().split('T')[0]
    const res = await getOrderStatistics(today, today)
    if (res.data) {
      statData.totalOrders = res.data.orderCount || 0
      statData.totalAmount = res.data.totalPayAmount || 0
      statData.netAmount = res.data.netAmount || 0
      statData.refundAmount = res.data.totalRefundAmount || 0
    }
  } catch (error) {
    console.error('获取统计失败', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.orderType = null
  searchForm.payStatus = null
  searchForm.status = null
  dateRange.value = []
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

const handleCreate = () => {
  ElMessage.info('请通过客户详情或收银台创建订单')
}

const handleView = async (row) => {
  try {
    const res = await getOrderById(row.id)
    detailData.value = res.data || res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const handlePay = (row) => {
  payForm.orderId = row.id
  payForm.orderNo = row.orderNo
  payForm.payAmount = row.payAmount
  payForm.payType = 1
  payVisible.value = true
}

const handlePaySubmit = async () => {
  try {
    await payOrder(payForm.orderId, payForm.payType)
    ElMessage.success('支付成功')
    payVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const handleRefund = (row) => {
  refundForm.orderId = row.id
  refundForm.orderNo = row.orderNo
  refundForm.payAmount = row.payAmount
  refundForm.maxRefundAmount = row.payAmount - (row.refundAmount || 0)
  refundForm.refundAmount = refundForm.maxRefundAmount
  refundForm.reason = ''
  refundVisible.value = true
}

const handleRefundSubmit = async () => {
  if (!refundForm.refundAmount || refundForm.refundAmount <= 0) {
    ElMessage.warning('请输入有效的退款金额')
    return
  }
  try {
    await refundOrder(refundForm.orderId, refundForm.refundAmount, refundForm.reason)
    ElMessage.success('退款成功')
    refundVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('退款失败')
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', { type: 'warning' })
    await cancelOrder(row.id, '后台取消')
    ElMessage.success('订单已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确认完成该订单？', '提示', { type: 'info' })
    await completeOrder(row.id)
    ElMessage.success('订单已完成')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchData()
  fetchStatistics()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.stat-row {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.table-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.detail-section {
  margin-top: 20px;
}
.detail-section h4 {
  margin-bottom: 10px;
  color: #303133;
}
</style>
