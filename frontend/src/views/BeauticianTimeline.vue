<template>
  <div class="timeline-container">
    <!-- 顶部工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :clearable="false"
            @change="handleDateChange"
          />
          <el-radio-group v-model="viewMode" size="small" style="margin-left: 16px;">
            <el-radio-button label="day">日视图</el-radio-button>
            <el-radio-button label="week">周视图</el-radio-button>
          </el-radio-group>
        </div>
        <div class="toolbar-right">
          <div class="legend">
            <span class="legend-item"><span class="legend-dot pending" />待确认</span>
            <span class="legend-item"><span class="legend-dot confirmed" />已确认</span>
            <span class="legend-item"><span class="legend-dot arrived" />已到店</span>
            <span class="legend-item"><span class="legend-dot service" />服务中</span>
            <span class="legend-item"><span class="legend-dot completed" />已完成</span>
            <span class="legend-item"><span class="legend-dot cancelled" />已取消</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 时间轴主体 -->
    <el-card class="timeline-card" shadow="never" v-loading="loading">
      <div class="timeline-wrapper">
        <!-- 左侧美容师列 -->
        <div class="beautician-col">
          <div class="col-header">美容师</div>
          <div class="col-body">
            <div
              v-for="b in beauticians"
              :key="b.id"
              class="beautician-row"
              :class="{ 'is-off': isOff(b.id) }"
            >
              <el-avatar :size="32" :src="b.avatar" class="b-avatar">
                {{ b.name?.charAt(0) || '美' }}
              </el-avatar>
              <div class="b-info">
                <div class="b-name">{{ b.name }}</div>
                <div class="b-time">{{ getWorkTime(b.id) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧时间轴区域 -->
        <div class="schedule-area" ref="scheduleAreaRef">
          <!-- 时间刻度头 -->
          <div class="time-header">
            <div
              v-for="slot in timeSlots"
              :key="slot.value"
              class="time-slot"
              :style="{ width: slotWidth + 'px' }"
            >
              {{ slot.label }}
            </div>
          </div>

          <!-- 时间轴内容 -->
          <div class="time-body">
            <div
              v-for="b in beauticians"
              :key="b.id"
              class="time-row"
              :class="{ 'is-off': isOff(b.id) }"
            >
              <!-- 工作时间背景 -->
              <div
                v-if="!isOff(b.id)"
                class="work-bg"
                :style="getWorkBgStyle(b.id)"
              />
              <!-- 午休背景 -->
              <div
                v-if="getLunchStyle(b.id)"
                class="lunch-bg"
                :style="getLunchStyle(b.id)"
              />
              <!-- 时间网格线 -->
              <div
                v-for="slot in timeSlots"
                :key="slot.value"
                class="grid-line"
                :style="{ width: slotWidth + 'px' }"
              />

              <!-- 预约块 -->
              <div
                v-for="appt in getAppointments(b.id)"
                :key="appt.id"
                class="appt-block"
                :class="'status-' + appt.status"
                :style="getApptStyle(appt)"
                @click="handleApptClick(appt)"
              >
                <div class="appt-title">{{ appt.customerName }}</div>
                <div class="appt-service">{{ appt.serviceItemName }}</div>
                <div class="appt-time">{{ appt.startTime?.slice(0,5) }} - {{ appt.endTime?.slice(0,5) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 预约详情弹窗 -->
    <el-dialog v-model="detailVisible" title="预约详情" width="500px">
      <el-descriptions :column="1" border v-if="selectedAppt">
        <el-descriptions-item label="预约单号">{{ selectedAppt.appointmentNo }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ selectedAppt.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ selectedAppt.customerPhone }}</el-descriptions-item>
        <el-descriptions-item label="美容师">{{ selectedAppt.beauticianName }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ selectedAppt.serviceItemName }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ selectedAppt.appointmentDate }} {{ selectedAppt.startTime?.slice(0,5) }} - {{ selectedAppt.endTime?.slice(0,5) }}</el-descriptions-item>
        <el-descriptions-item label="服务时长">{{ selectedAppt.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(selectedAppt.status)" size="small">{{ selectedAppt.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ selectedAppt.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const selectedDate = ref(new Date().toISOString().split('T')[0])
const viewMode = ref('day')
const beauticians = ref([])
const appointments = ref([])
const schedules = ref([])
const detailVisible = ref(false)
const selectedAppt = ref(null)

const scheduleAreaRef = ref(null)

// 时间配置
const startHour = 8
const endHour = 20
const slotMinutes = 30
const slotWidth = ref(80)

const timeSlots = computed(() => {
  const slots = []
  for (let h = startHour; h < endHour; h++) {
    slots.push({ label: `${h}:00`, value: `${String(h).padStart(2, '0')}:00` })
    slots.push({ label: '', value: `${String(h).padStart(2, '0')}:30` })
  }
  slots.push({ label: `${endHour}:00`, value: `${String(endHour).padStart(2, '0')}:00` })
  return slots
})

const totalMinutes = computed(() => (endHour - startHour) * 60)

// 获取美容师列表
const fetchBeauticians = async () => {
  try {
    const res = await request({ url: '/beautician/active', method: 'get' })
    beauticians.value = res || []
  } catch (error) {
    console.error('获取美容师列表失败', error)
  }
}

// 获取预约数据
const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/appointment/schedule',
      method: 'get',
      params: { date: selectedDate.value }
    })
    appointments.value = res || []
  } catch (error) {
    ElMessage.error('获取预约数据失败')
  } finally {
    loading.value = false
  }
}

// 获取排班数据
const fetchSchedules = async () => {
  try {
    // 逐个获取每个美容师的排班
    const promises = beauticians.value.map(b =>
      request({
        url: '/beautician/schedule/list',
        method: 'get',
        params: {
          beauticianId: b.id,
          startDate: selectedDate.value,
          endDate: selectedDate.value
        }
      }).then(res => ({
        beauticianId: b.id,
        schedule: (res || [])[0] || null
      })).catch(() => ({ beauticianId: b.id, schedule: null }))
    )
    const results = await Promise.all(promises)
    schedules.value = results
  } catch (error) {
    console.error('获取排班数据失败', error)
  }
}

const isOff = (beauticianId) => {
  const item = schedules.value.find(s => s.beauticianId === beauticianId)
  if (!item || !item.schedule) return false // 默认上班
  return item.schedule.isWorking === 0
}

const getWorkTime = (beauticianId) => {
  const item = schedules.value.find(s => s.beauticianId === beauticianId)
  if (!item || !item.schedule) return '09:00 - 18:00'
  if (item.schedule.isWorking === 0) return '休息'
  const s = item.schedule.startTime?.slice(0, 5) || '09:00'
  const e = item.schedule.endTime?.slice(0, 5) || '18:00'
  return `${s} - ${e}`
}

const getSchedule = (beauticianId) => {
  const item = schedules.value.find(s => s.beauticianId === beauticianId)
  return item?.schedule
}

const timeToMinutes = (timeStr) => {
  if (!timeStr) return 0
  const [h, m] = timeStr.split(':').map(Number)
  return (h - startHour) * 60 + m
}

const getWorkBgStyle = (beauticianId) => {
  const sched = getSchedule(beauticianId)
  const sTime = sched?.startTime || '09:00:00'
  const eTime = sched?.endTime || '18:00:00'
  const startMin = timeToMinutes(sTime)
  const endMin = timeToMinutes(eTime)
  const left = (startMin / totalMinutes.value) * 100
  const width = ((endMin - startMin) / totalMinutes.value) * 100
  return {
    left: `${left}%`,
    width: `${width}%`
  }
}

const getLunchStyle = (beauticianId) => {
  const sched = getSchedule(beauticianId)
  if (!sched?.lunchStartTime || !sched?.lunchEndTime) return null
  const startMin = timeToMinutes(sched.lunchStartTime)
  const endMin = timeToMinutes(sched.lunchEndTime)
  const left = (startMin / totalMinutes.value) * 100
  const width = ((endMin - startMin) / totalMinutes.value) * 100
  return {
    left: `${left}%`,
    width: `${width}%`
  }
}

const getAppointments = (beauticianId) => {
  return appointments.value.filter(a => a.beauticianId === beauticianId)
}

const getApptStyle = (appt) => {
  const startMin = timeToMinutes(appt.startTime)
  const endMin = timeToMinutes(appt.endTime)
  const left = (startMin / totalMinutes.value) * 100
  const width = ((endMin - startMin) / totalMinutes.value) * 100
  return {
    left: `${left}%`,
    width: `${width}%`
  }
}

const handleDateChange = () => {
  fetchAppointments()
  fetchSchedules()
}

const handleApptClick = (appt) => {
  selectedAppt.value = appt
  detailVisible.value = true
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info', 5: 'danger' }
  return map[status] || 'info'
}

const loadAll = async () => {
  await fetchBeauticians()
  await fetchAppointments()
  await fetchSchedules()
}

onMounted(() => {
  loadAll()
})

watch(viewMode, () => {
  // 周视图可后续扩展
  ElMessage.info('周视图开发中，当前显示日视图')
})
</script>

<style scoped>
.timeline-container {
  padding: 20px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
}

.toolbar-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.legend {
  display: flex;
  gap: 16px;
  align-items: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.legend-dot.pending { background: #e6a23c; }
.legend-dot.confirmed { background: #409eff; }
.legend-dot.arrived { background: #67c23a; }
.legend-dot.service { background: #9254de; }
.legend-dot.completed { background: #909399; }
.legend-dot.cancelled { background: #f56c6c; }

.timeline-card {
  flex: 1;
  overflow: hidden;
}

.timeline-card :deep(.el-card__body) {
  height: 100%;
  padding: 0;
}

.timeline-wrapper {
  display: flex;
  height: 100%;
  overflow: hidden;
}

/* 左侧美容师列 */
.beautician-col {
  width: 160px;
  flex-shrink: 0;
  border-right: 1px solid #e4e7ed;
  background: #fafafa;
  z-index: 2;
}

.col-header {
  height: 48px;
  line-height: 48px;
  text-align: center;
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
}

.col-body {
  overflow-y: auto;
  height: calc(100% - 48px);
}

.beautician-row {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  border-bottom: 1px solid #ebeef5;
  gap: 8px;
}

.beautician-row.is-off {
  background: #f5f7fa;
  opacity: 0.6;
}

.b-avatar {
  flex-shrink: 0;
  background: #e85d75;
  color: #fff;
  font-size: 14px;
}

.b-info {
  overflow: hidden;
}

.b-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.b-time {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* 右侧时间轴区域 */
.schedule-area {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

.time-header {
  height: 48px;
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
  flex-shrink: 0;
}

.time-slot {
  flex-shrink: 0;
  line-height: 48px;
  text-align: center;
  font-size: 12px;
  color: #606266;
  border-right: 1px dashed #e4e7ed;
  box-sizing: border-box;
}

.time-body {
  flex: 1;
  position: relative;
}

.time-row {
  height: 72px;
  position: relative;
  border-bottom: 1px solid #ebeef5;
  display: flex;
}

.time-row.is-off {
  background: #f5f7fa;
}

.grid-line {
  flex-shrink: 0;
  border-right: 1px dashed #e4e7ed;
  box-sizing: border-box;
  height: 100%;
}

.work-bg {
  position: absolute;
  top: 0;
  height: 100%;
  background: #f0f9ff;
  z-index: 0;
}

.lunch-bg {
  position: absolute;
  top: 0;
  height: 100%;
  background: repeating-linear-gradient(
    45deg,
    #fef0f0,
    #fef0f0 4px,
    #fff 4px,
    #fff 8px
  );
  z-index: 0;
  opacity: 0.5;
}

/* 预约块 */
.appt-block {
  position: absolute;
  top: 6px;
  height: 60px;
  border-radius: 6px;
  padding: 4px 8px;
  box-sizing: border-box;
  cursor: pointer;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
  border: 1px solid transparent;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.appt-block:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
  z-index: 2;
}

.appt-title {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.appt-service {
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
  opacity: 0.85;
}

.appt-time {
  font-size: 10px;
  margin-top: 2px;
  opacity: 0.75;
}

/* 状态颜色 */
.status-0 { /* 待确认 */
  background: #fdf6ec;
  border-color: #e6a23c;
  color: #b8821c;
}
.status-1 { /* 已确认 */
  background: #ecf5ff;
  border-color: #409eff;
  color: #2a7fd8;
}
.status-2 { /* 已到店 */
  background: #f0f9eb;
  border-color: #67c23a;
  color: #529b2e;
}
.status-3 { /* 服务中 */
  background: #f5f0ff;
  border-color: #9254de;
  color: #7a3fc4;
}
.status-4 { /* 已完成 */
  background: #f4f4f5;
  border-color: #909399;
  color: #606266;
}
.status-5 { /* 已取消 */
  background: #fef0f0;
  border-color: #f56c6c;
  color: #c45656;
  text-decoration: line-through;
}
</style>
