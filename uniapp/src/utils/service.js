/**
 * 服务项目工具函数
 */

// 服务图标映射
export const serviceIconMap = {
  facial: '🧴',
  nail: '💅',
  eye: '👁️',
  body: '💆',
  hair: '💇',
  makeup: '💄'
}

// 服务分类名称映射
export const categoryNameMap = {
  all: '全部',
  facial: '面部护理',
  nail: '美甲美睫',
  eye: '美睫',
  body: '身体护理',
  hair: '美发',
  makeup: '化妆'
}

// 获取服务图标
export function getServiceIcon(category) {
  return serviceIconMap[category] || '💆'
}

// 获取分类名称
export function getCategoryName(category) {
  return categoryNameMap[category] || category || '其他'
}

// 格式化价格
export function formatPrice(price) {
  return typeof price === 'number' ? price.toFixed(2) : '0.00'
}

// 格式化时长
export function formatDuration(minutes) {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

// 美容师等级映射
export const beauticianLevelMap = {
  1: '初级',
  2: '中级',
  3: '高级',
  4: '首席'
}

// 获取美容师等级文本
export function getLevelText(level) {
  return beauticianLevelMap[level] || '技师'
}

// 获取默认头像emoji
export function getDefaultAvatar(gender) {
  return gender === 1 ? '👨' : '👩'
}

// 获取星级显示
export function getStars(rating) {
  const r = parseFloat(rating) || 5
  const full = Math.floor(r)
  return '★'.repeat(full) + '☆'.repeat(5 - full)
}
