<template>
  <div class="log-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="操作日志" name="oper" />
        <el-tab-pane label="登录日志" name="login" />
      </el-tabs>
    </el-card>

    <el-card class="table-card">
      <!-- 操作日志表格 -->
      <el-table v-if="activeTab === 'oper'" :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="method" label="方法" width="180" />
        <el-table-column prop="requestMethod" label="请求方式" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="methodTagType(row.requestMethod)">{{ row.requestMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requestUrl" label="请求地址" min-width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationTime" label="操作时间" width="160" />
      </el-table>

      <!-- 登录日志表格 -->
      <el-table v-else :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="loginLocation" label="登录地点" width="160" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示信息" min-width="120" />
        <el-table-column prop="loginTime" label="登录时间" width="160" />
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.limit"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 16px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryOperLog, queryLoginLog } from '@/api/system/log'

const loading = ref(false)
const activeTab = ref('oper')
const tableData = ref([])
const dateRange = ref([])

const searchForm = reactive({
  module: '',
  status: null
})

const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
})

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      limit: pagination.limit
    }

    let res
    if (activeTab.value === 'oper') {
      if (dateRange.value && dateRange.value.length === 2) {
        params.beginTime = dateRange.value[0]
        params.endTime = dateRange.value[1]
      }
      res = await queryOperLog(params)
    } else {
      res = await queryLoginLog(params)
    }

    tableData.value = res?.list || []
    pagination.total = res?.total || 0
  } catch (e) {
    ElMessage.error('加载日志失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  loadData()
}

function handleReset() {
  dateRange.value = []
  pagination.page = 1
  loadData()
}

function handleTabChange() {
  pagination.page = 1
  pagination.total = 0
  tableData.value = []
  loadData()
}

function handleSizeChange(val) {
  pagination.limit = val
  loadData()
}

function handleCurrentChange(val) {
  pagination.page = val
  loadData()
}

function methodTagType(method) {
  const map = { GET: '', POST: 'success', PUT: 'warning', DELETE: 'danger' }
  return map[method] || 'info'
}
</script>

<style scoped>
.log-container {
  padding: 16px;
}
</style>
