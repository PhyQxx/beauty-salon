<template>
  <div class="recharge-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="充值日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          />
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
        <span>充值记录列表</span>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="amount" label="充值金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">+¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="giftAmount" label="赠送金额" width="100">
          <template #default="{ row }">
            {{ row.giftAmount ? '+¥' + row.giftAmount : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="payType" label="支付方式" width="100">
          <template #default="{ row }">
            {{ payTypeName(row.payType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="充值时间" width="160" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" v-if="row.status === 1" @click="handleRefund(row)">退款</el-button>
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

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statistics.totalAmount }}</div>
            <div class="stat-label">充值总额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">{{ statistics.totalCount }}</div>
            <div class="stat-label">充值笔数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statistics.totalGift }}</div>
            <div class="stat-label">赠送总额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">¥{{ statistics.totalRefund }}</div>
            <div class="stat-label">退款总额</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详情弹窗 -->
    <el-dialog title="充值详情" v-model="detailVisible" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ statusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="充值金额">
          <span style="color: #f56c6c; font-weight: bold;">+¥{{ detailData.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="赠送金额">
          {{ detailData.giftAmount ? '+¥' + detailData.giftAmount : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payTypeName(detailData.payType) }}</el-descriptions-item>
        <el-descriptions-item label="充值后余额">¥{{ detailData.balanceAfter }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="充值时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 退款弹窗 -->
    <el-dialog title="退款" v-model="refundVisible" width="400px">
      <el-form ref="refundRef" :model="refundForm" :rules="refundRules" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ refundForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="原充值金额">
          <span style="color: #f56c6c;">+¥{{ refundForm.amount }}</span>
        </el-form-item>
        <el-form-item label="退款金额" prop="refundAmount">
          <el-input-number v-model="refundForm.refundAmount" :min="0" :max="refundForm.amount" :precision="2" />
        </el-form-item>
        <el-form-item label="退款原因" prop="reason">
          <el-input v-model="refundForm.reason" type="textarea" rows="2" placeholder="请输入退款原因" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRefundSubmit">确定退款</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderById, refundOrder } from '@/api/pos/order'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const refundVisible = ref(false)
const detailData = ref(null)
const refundRef = ref(null)
const dateRange = ref([])

const searchForm = reactive({
  customerName: '',
  phone: '',
  startDate: '',
  endDate: ''
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const statistics = reactive({
  totalAmount: 0,
  totalCount: 0,
  totalGift: 0,
  totalRefund: 0
})

const refundForm = reactive({
  id: null,
  orderNo: '',
  amount: 0,
  refundAmount: 0,
  reason: ''
})

const refundRules = {
  refundAmount: [{ required: true, message: '请输入退款金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入退款原因', trigger: 'blur' }]
}

const payTypeName = (type) => {
  const map = { 1: '微信', 2: '支付宝', 3: '现金', 4: '银行卡', 5: '其他' }
  return map[type] || '-'
}

const statusName = (status) => {
  const map = { 0: '已取消', 1: '已完成', 2: '进行中', 3: '已退款' }
  return map[status] || '-'
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
      type: 'recharge',
      ...searchForm
    }
    const res = await getOrderList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取充值记录失败')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    // 简单计算统计数据，实际应该调用专门的统计API
    const params = {
      page: 1,
      limit: 1000,
      type: 'recharge',
      ...searchForm
    }
    const res = await getOrderList(params)
    const list = res.records || res.list || []
    
    let totalAmount = 0
    let totalGift = 0
    let totalRefund = 0
    
    list.forEach(item => {
      if (item.status === 1) {
        totalAmount += item.amount || 0
        totalGift += item.giftAmount || 0
      } else if (item.status === 3) {
        totalRefund += item.amount || 0
      }
    })
    
    statistics.totalAmount = totalAmount.toFixed(2)
    statistics.totalCount = list.filter(item => item.status === 1).length
    statistics.totalGift = totalGift.toFixed(2)
    statistics.totalRefund = totalRefund.toFixed(2)
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
  pagination.page = 1
  fetchData()
  fetchStatistics()
}

const handleReset = () => {
  searchForm.customerName = ''
  searchForm.phone = ''
  dateRange.value = []
  searchForm.startDate = ''
  searchForm.endDate = ''
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

const handleView = async (row) => {
  try {
    const res = await getOrderById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取充值详情失败')
  }
}

const handleRefund = (row) => {
  refundForm.id = row.id
  refundForm.orderNo = row.orderNo
  refundForm.amount = row.amount
  refundForm.refundAmount = row.amount
  refundForm.reason = ''
  refundVisible.value = true
}

const handleRefundSubmit = async () => {
  await refundRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await refundOrder(refundForm.id, refundForm.refundAmount, refundForm.reason)
        ElMessage.success('退款成功')
        refundVisible.value = false
        fetchData()
        fetchStatistics()
      } catch (error) {
        ElMessage.error('退款失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
  fetchStatistics()
})
</script>

<style scoped>
.recharge-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
