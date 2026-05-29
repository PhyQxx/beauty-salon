<template>
  <div class="inventory-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="产品名称/编码" clearable />
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="searchForm.storeId" placeholder="全部" clearable>
            <el-option label="总店" :value="1" />
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
      <template #header>
        <div class="clearfix">
          <span>库存列表</span>
          <div style="float: right;">
            <el-button type="success" size="small" @click="handleStockIn">采购入库</el-button>
            <el-button type="warning" size="small" @click="handleStockOut">领用出库</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="productName" label="产品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="spec" label="规格" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="stockQuantity" label="当前库存" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stockQuantity <= row.minStock ? 'danger' : 'success'">
              {{ row.stockQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="预警库存" width="100" />
        <el-table-column prop="updateTime" label="最后变动时间" width="160" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleAdjustment(row)">盘点调整</el-button>
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

    <!-- 入库/出库弹窗 -->
    <el-dialog :title="actionTitle" v-model="actionVisible" width="500px" @close="handleActionClose">
      <el-form ref="actionFormRef" :model="actionData" :rules="actionRules" label-width="100px">
        <el-form-item label="产品" prop="productId">
          <el-select v-model="actionData.productId" placeholder="选择产品" filterable style="width: 100%">
            <el-option v-for="item in productList" :key="item.id" :label="item.name + ' (' + item.code + ')'" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店" prop="storeId">
          <el-select v-model="actionData.storeId" placeholder="选择门店" style="width: 100%">
            <el-option label="总店" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item :label="actionType === 'in' ? '入库数量' : '出库数量'" prop="quantity">
          <el-input-number v-model="actionData.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="业务类型" prop="type">
          <el-select v-model="actionData.type" placeholder="选择类型" style="width: 100%">
            <template v-if="actionType === 'in'">
              <el-option label="采购入库" :value="1" />
              <el-option label="退货入库" :value="4" />
            </template>
            <template v-else>
              <el-option label="退货出库" :value="2" />
              <el-option label="销售出库" :value="3" />
              <el-option label="领用出库" :value="6" />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="actionData.remark" type="textarea" rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionVisible = false">取消</el-button>
        <el-button type="primary" @click="handleActionSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 盘点调整弹窗 -->
    <el-dialog title="盘点调整" v-model="adjustVisible" width="500px">
      <el-form ref="adjustFormRef" :model="adjustData" :rules="adjustRules" label-width="100px">
        <el-form-item label="产品名称">{{ adjustData.productName }}</el-form-item>
        <el-form-item label="当前库存">{{ adjustData.currentQuantity }}</el-form-item>
        <el-form-item label="实盘数量" prop="targetQuantity">
          <el-input-number v-model="adjustData.targetQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustData.remark" type="textarea" rows="2" placeholder="调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjustSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryPage, stockIn, stockOut, adjustment } from '@/api/pos/inventory'
import { getProductPage } from '@/api/pos/product'

const loading = ref(false)
const tableData = ref([])
const productList = ref([])
const actionVisible = ref(false)
const adjustVisible = ref(false)
const actionTitle = ref('库存入库')
const actionType = ref('in') // in or out
const actionFormRef = ref(null)
const adjustFormRef = ref(null)

const searchForm = reactive({
  keyword: '',
  storeId: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

const actionData = reactive({
  productId: null,
  storeId: 1,
  quantity: 1,
  type: 1,
  remark: ''
})

const adjustData = reactive({
  productId: null,
  productName: '',
  storeId: 1,
  currentQuantity: 0,
  targetQuantity: 0,
  remark: ''
})

const actionRules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  storeId: [{ required: true, message: '请选择门店', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  type: [{ required: true, message: '请选择业务类型', trigger: 'change' }]
}

const adjustRules = {
  targetQuantity: [{ required: true, message: '请输入实盘数量', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit,
      ...searchForm
    }
    const res = await getInventoryPage(params)
    tableData.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取库存列表失败')
  } finally {
    loading.value = false
  }
}

const fetchProducts = async () => {
  try {
    const res = await getProductPage({ page: 1, limit: 1000, status: 1 })
    productList.value = res.list || []
  } catch (error) {
    console.error('获取产品列表失败', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.storeId = null
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

const handleStockIn = () => {
  actionTitle.value = '库存入库'
  actionType.value = 'in'
  Object.assign(actionData, {
    productId: null,
    storeId: 1,
    quantity: 1,
    type: 1,
    remark: ''
  })
  actionVisible.value = true
}

const handleStockOut = () => {
  actionTitle.value = '库存出库'
  actionType.value = 'out'
  Object.assign(actionData, {
    productId: null,
    storeId: 1,
    quantity: 1,
    type: 6,
    remark: ''
  })
  actionVisible.value = true
}

const handleAdjustment = (row) => {
  Object.assign(adjustData, {
    productId: row.productId,
    productName: row.productName,
    storeId: row.storeId,
    currentQuantity: row.stockQuantity,
    targetQuantity: row.stockQuantity,
    remark: ''
  })
  adjustVisible.value = true
}

const handleActionClose = () => {
  actionFormRef.value?.resetFields()
}

const handleActionSubmit = async () => {
  await actionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (actionType.value === 'in') {
          await stockIn(actionData)
        } else {
          await stockOut(actionData)
        }
        ElMessage.success('操作成功')
        actionVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleAdjustSubmit = async () => {
  await adjustFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await adjustment(adjustData)
        ElMessage.success('盘点成功')
        adjustVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
  fetchProducts()
})
</script>

<style scoped>
.inventory-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
</style>
