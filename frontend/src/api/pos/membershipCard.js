/**
 * 会员卡/套餐 API 模块
 * 
 * @author BeautySalon Team
 */
import request from '@/utils/request'

/**
 * 分页查询会员卡/套餐列表
 * @param {object} params - 查询参数
 */
export function getMembershipCardList(params) {
  return request({
    url: '/pos/membership-card/list',
    method: 'get',
    params
  })
}

/**
 * 获取会员卡/套餐详情
 * @param {number} id - 卡ID
 */
export function getMembershipCardById(id) {
  return request({
    url: `/pos/membership-card/${id}`,
    method: 'get'
  })
}

/**
 * 新增会员卡/套餐
 * @param {object} data - 卡数据
 */
export function addMembershipCard(data) {
  return request({
    url: '/pos/membership-card',
    method: 'post',
    data
  })
}

/**
 * 更新会员卡/套餐
 * @param {number} id - 卡ID
 * @param {object} data - 卡数据
 */
export function updateMembershipCard(id, data) {
  return request({
    url: `/pos/membership-card/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除会员卡/套餐
 * @param {number} id - 卡ID
 */
export function deleteMembershipCard(id) {
  return request({
    url: `/pos/membership-card/${id}`,
    method: 'delete'
  })
}

/**
 * 上下架会员卡/套餐
 * @param {number} id - 卡ID
 * @param {number} isActive - 是否上架
 */
export function updateMembershipCardStatus(id, isActive) {
  return request({
    url: `/pos/membership-card/${id}/status`,
    method: 'put',
    params: { isActive }
  })
}

/**
 * 获取上架的会员卡/套餐
 */
export function getActiveMembershipCards() {
  return request({
    url: '/pos/membership-card/active',
    method: 'get'
  })
}
