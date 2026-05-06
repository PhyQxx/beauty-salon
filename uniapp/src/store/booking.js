import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const STORAGE_KEY = 'booking_state'

// 从本地存储恢复状态
function loadFromStorage() {
  try {
    const stored = uni.getStorageSync(STORAGE_KEY)
    if (stored) {
      return JSON.parse(stored)
    }
  } catch (e) {
    console.error('Failed to load booking state:', e)
  }
  return null
}

// 保存状态到本地存储
function saveToStorage(state) {
  try {
    uni.setStorageSync(STORAGE_KEY, JSON.stringify(state))
  } catch (e) {
    console.error('Failed to save booking state:', e)
  }
}

export const useBookingStore = defineStore('booking', () => {
  const stored = loadFromStorage()

  // 选中的服务
  const selectedServices = ref(stored?.selectedServices || [])

  // 选中的美容师
  const selectedBeautician = ref(stored?.selectedBeautician || null)

  // 选中的日期和时间
  const selectedDate = ref(stored?.selectedDate || '')
  const selectedTime = ref(stored?.selectedTime || '')

  // 预约备注
  const remark = ref(stored?.remark || '')

  // 预约ID（创建预约后）
  const appointmentId = ref(stored?.appointmentId || null)

  // 计算总价格
  const totalPrice = computed(() => {
    return selectedServices.value.reduce((sum, service) => sum + (service.price || 0), 0)
  })

  // 计算总时长
  const totalDuration = computed(() => {
    return selectedServices.value.reduce((sum, service) => sum + (service.duration || 0), 0)
  })

  // 监听变化自动保存
  function persist() {
    saveToStorage({
      selectedServices: selectedServices.value,
      selectedBeautician: selectedBeautician.value,
      selectedDate: selectedDate.value,
      selectedTime: selectedTime.value,
      remark: remark.value,
      appointmentId: appointmentId.value
    })
  }

  // 添加服务
  function addService(service) {
    if (!selectedServices.value.find(s => s.id === service.id)) {
      selectedServices.value.push(service)
      persist()
    }
  }

  // 移除服务
  function removeService(serviceId) {
    const index = selectedServices.value.findIndex(s => s.id === serviceId)
    if (index > -1) {
      selectedServices.value.splice(index, 1)
      persist()
    }
  }

  // 切换服务选择状态
  function toggleService(service) {
    const index = selectedServices.value.findIndex(s => s.id === service.id)
    if (index > -1) {
      selectedServices.value.splice(index, 1)
    } else {
      selectedServices.value.push(service)
    }
    persist()
  }

  // 判断服务是否已选
  function isServiceSelected(serviceId) {
    return selectedServices.value.some(s => s.id === serviceId)
  }

  // 选择美容师
  function selectBeautician(beautician) {
    selectedBeautician.value = beautician
    persist()
  }

  // 选择日期
  function selectDate(date) {
    selectedDate.value = date
    persist()
  }

  // 选择时间
  function selectTime(time) {
    selectedTime.value = time
    persist()
  }

  // 设置备注
  function setRemark(text) {
    remark.value = text
    persist()
  }

  // 设置预约ID
  function setAppointmentId(id) {
    appointmentId.value = id
    persist()
  }

  // 重置预约状态
  function resetBooking() {
    selectedServices.value = []
    selectedBeautician.value = null
    selectedDate.value = ''
    selectedTime.value = ''
    remark.value = ''
    appointmentId.value = null
    uni.removeStorageSync(STORAGE_KEY)
  }

  return {
    selectedServices,
    selectedBeautician,
    selectedDate,
    selectedTime,
    remark,
    appointmentId,
    totalPrice,
    totalDuration,
    addService,
    removeService,
    toggleService,
    isServiceSelected,
    selectBeautician,
    selectDate,
    selectTime,
    setRemark,
    setAppointmentId,
    resetBooking
  }
})
