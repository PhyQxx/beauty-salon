<template>
  <div class="customer-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="searchForm.memberLevel" placeholder="全部" clearable>
            <el-option label="普通" :value="1" />
            <el-option label="银卡" :value="2" />
            <el-option label="金卡" :value="3" />
            <el-option label="钻石" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="活跃" :value="1" />
            <el-option label="流失" :value="0" />
            <el-option label="休眠" :value="2" />
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
        <span>客户列表</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增客户</el-button>
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
        <el-table-column prop="birthday" label="生日" width="110" />
        <el-table-column prop="memberLevel" label="等级" width="90">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.memberLevel)" size="small">
              {{ levelName(row.memberLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="{ row }">
            ¥{{ row.balance }}
          </template>
        </el-table-column>
        <el-table-column prop="memberPoints" label="积分" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '活跃' : row.status === 2 ? '休眠' : '流失' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleRecharge(row)">充值</el-button>
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
        <el-form-item label="生日" prop="birthday">
          <el-date-picker v-model="formData.birthday" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="formData.memberLevel" placeholder="选择等级">
            <el-option label="普通" :value="1" />
            <el-option label="银卡" :value="2" />
            <el-option label="金卡" :value="3" />
            <el-option label="钻石" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="微信号" prop="wechat">
          <el-input v-model="formData.wechat" placeholder="请输入微信号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="过敏信息" prop="allergyInfo">
          <el-input v-model="formData.allergyInfo" type="textarea" rows="2" placeholder="请输入过敏信息" />
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
    <el-dialog title="客户详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === 0 ? '女' : detailData.gender === 1 ? '男' : '未知' }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ detailData.birthday }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">{{ levelName(detailData.memberLevel) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '活跃' : detailData.status === 2 ? '休眠' : '流失' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户余额">¥{{ detailData.balance }}</el-descriptions-item>
        <el-descriptions-item label="会员积分">{{ detailData.memberPoints }}</el-descriptions-item>
        <el-descriptions-item label="微信号">{{ detailData.wechat || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过敏信息" :span="2">{{ detailData.allergyInfo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 充值弹窗 -->
    <el-dialog title="账户充值" :visible.sync="rechargeVisible" width="400px">
      <el-form ref="rechargeRef" :model="rechargeForm" :rules="rechargeRules" label-width="80px">
        <el-form-item label="客户">
          <span>{{ rechargeForm.customerName }}</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span>¥{{ rechargeForm.currentBalance }}</span>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="rechargeForm.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="充值原因" prop="reason">
          <el-input v-model="rechargeForm.reason" placeholder="如：会员充值" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRechargeSubmit">确定充值</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, getCustomerById, addCustomer, updateCustomer, deleteCustomer, adjustCustomerBalance } from '@/api/crm/customer'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const rechargeVisible = ref(false)
const dialogTitle = ref('新增客户')
const formRef = ref(null)
const rechargeRef = ref(null)

const searchForm = reactive({
  keyword: '',
  memberLevel: null,
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
  gender: 2,
  birthday: '',
  memberLevel: 1,
  wechat: '',
  email: '',
  allergyInfo: '',
  remark: ''
})

const detailData = ref(null)

const rechargeForm = reactive({
  customerId: null,
  customerName: '',
  currentBalance: 0,
  amount: 0,
  reason: '会员充值'
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const rechargeRules = {
  amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }]
}

const levelName = (level) => {
  const map = { 1: '普通', 2: '银卡', 3: '金卡', 4: '钻石' }
  return map[level] || '普通'
}

const levelTagType = (level) => {
  const map = { 1: '', 2: 'warning', 3: 'success', 4: 'danger' }
  return map[level] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getCustomerList(params)
    tableData.value = res.records || res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取客户列表失败')
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
  searchForm.memberLevel = null
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
  dialogTitle.value = '新增客户'
  Object.keys(formData).forEach(key => {
    if (key === 'gender') formData[key] = 2
    else if (key === 'memberLevel') formData[key] = 1
    else formData[key] = ''
  })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑客户'
  try {
    const res = await getCustomerById(row.id)
    Object.keys(formData).forEach(key => {
      formData[key] = res[key] || ''
    })
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取客户信息失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getCustomerById(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取客户详情失败')
  }
}

const handleRecharge = (row) => {
  rechargeForm.customerId = row.id
  rechargeForm.customerName = row.name
  rechargeForm.currentBalance = row.balance
  rechargeForm.amount = 0
  rechargeForm.reason = '会员充值'
  rechargeVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateCustomer(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await addCustomer(formData)
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

const handleRechargeSubmit = async () => {
  await rechargeRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await adjustCustomerBalance(rechargeForm.customerId, rechargeForm.amount, rechargeForm.reason)
        ElMessage.success('充值成功')
        rechargeVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('充值失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.customer-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
