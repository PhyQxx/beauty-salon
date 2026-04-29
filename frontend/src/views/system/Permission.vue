<template>
  <div class="permission-container">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="角色">
          <el-select v-model="selectedRole" placeholder="请选择角色" @change="handleRoleChange">
            <el-option label="管理员" :value="1" />
            <el-option label="技师" :value="2" />
            <el-option label="前台" :value="3" />
            <el-option label="经理" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="handleSavePermissions">保存权限</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div slot="header" class="clearfix">
        <span>权限分配</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="section-title">系统权限</div>
          <el-tree
            ref="systemTreeRef"
            :data="systemPermissions"
            :props="{ children: 'children', label: 'name' }"
            node-key="id"
            show-checkbox
            default-expand-all
          />
        </el-col>
        <el-col :span="12">
          <div class="section-title">业务权限</div>
          <el-tree
            ref="businessTreeRef"
            :data="businessPermissions"
            :props="{ children: 'children', label: 'name' }"
            node-key="id"
            show-checkbox
            default-expand-all
          />
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card" style="margin-top: 16px;">
      <div slot="header" class="clearfix">
        <span>全部权限树</span>
      </div>
      <el-table :data="allPermissions" v-loading="loading" stripe row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="权限名称" width="180" />
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'success'" size="small">
              {{ row.type === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="sort" label="排序" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissionTree, getPermissionsByRoleId, updateRolePermissions } from '@/api/system/permission'

const loading = ref(false)
const selectedRole = ref(null)
const allPermissions = ref([])
const systemPermissions = ref([])
const businessPermissions = ref([])

const systemTreeRef = ref(null)
const businessTreeRef = ref(null)

onMounted(async () => {
  await loadPermissionTree()
})

async function loadPermissionTree() {
  loading.value = true
  try {
    const res = await getPermissionTree()
    allPermissions.value = res || []

    // 按类型分组
    systemPermissions.value = (res || []).filter(p => p.type === 1 && (p.code || '').startsWith('sys:'))
    businessPermissions.value = (res || []).filter(p => p.type === 1 && !(p.code || '').startsWith('sys:'))
  } catch (e) {
    ElMessage.error('加载权限树失败')
  } finally {
    loading.value = false
  }
}

async function handleRoleChange(roleId) {
  if (!roleId) return
  try {
    const res = await getPermissionsByRoleId(roleId)
    const checkedIds = (res || []).map(p => p.id)

    // 设置选中状态
    if (systemTreeRef.value) {
      systemTreeRef.value.setCheckedKeys(checkedIds)
    }
    if (businessTreeRef.value) {
      businessTreeRef.value.setCheckedKeys(checkedIds)
    }
  } catch (e) {
    ElMessage.error('加载角色权限失败')
  }
}

function handleQuery() {
  if (selectedRole.value) {
    handleRoleChange(selectedRole.value)
  } else {
    ElMessage.warning('请先选择角色')
  }
}

async function handleSavePermissions() {
  if (!selectedRole.value) {
    ElMessage.warning('请先选择角色')
    return
  }

  const systemChecked = systemTreeRef.value?.getCheckedKeys() || []
  const businessChecked = businessTreeRef.value?.getCheckedKeys() || []
  const allChecked = [...systemChecked, ...businessChecked]

  try {
    await updateRolePermissions(selectedRole.value, allChecked)
    ElMessage.success('权限保存成功')
  } catch (e) {
    ElMessage.error('权限保存失败')
  }
}
</script>

<style scoped>
.permission-container {
  padding: 16px;
}
.section-title {
  font-weight: bold;
  margin-bottom: 12px;
  font-size: 14px;
  color: #409eff;
}
</style>
