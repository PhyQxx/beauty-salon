/**
 * 日期时间工具函数
 */
import dayjs from 'dayjs'

// 格式化日期
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 格式化时间
export function formatTime(time, format = 'HH:mm') {
  if (!time) return ''
  return dayjs(time).format(format)
}

// 格式化日期时间
export function formatDateTime(dateTime, format = 'YYYY-MM-DD HH:mm') {
  if (!dateTime) return ''
  return dayjs(dateTime).format(format)
}

// 获取相对时间描述
export function getRelativeTime(date) {
  if (!date) return ''
  const now = dayjs()
  const target = dayjs(date)
  const diffDays = now.diff(target, 'day')
  
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '昨天'
  if (diffDays === 2) return '前天'
  if (diffDays < 7) return `${diffDays}天前`
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}周前`
  if (diffDays < 365) return `${Math.floor(diffDays / 30)}月前`
  return `${Math.floor(diffDays / 365)}年前`
}

// 获取未来N天的日期列表
export function getFutureDays(days = 14, format = 'YYYY-MM-DD') {
  return Array.from({ length: days }, (_, i) => {
    const date = dayjs().add(i, 'day')
    return {
      value: date.format(format),
      week: date.day() === 0 ? '周日' : date.day() === 6 ? '周六' : ['周一', '周二', '周三', '周四', '周五'][date.day() - 1],
      day: date.format('MM/DD'),
      month: date.format('MM'),
      dayNum: date.date()
    }
  })
}

// 判断是否过期
export function isExpired(date) {
  if (!date) return false
  return dayjs(date).isBefore(dayjs())
}

// 判断是否是今天
export function isToday(date) {
  if (!date) return false
  return dayjs(date).isSame(dayjs(), 'day')
}
