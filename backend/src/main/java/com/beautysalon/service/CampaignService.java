package com.beautysalon.service;

import com.beautysalon.dto.CampaignCreateDTO;
import com.beautysalon.dto.CampaignQueryDTO;
import com.beautysalon.entity.PosCampaign;
import com.beautysalon.entity.PosCoupon;
import com.beautysalon.vo.CampaignVO;
import com.beautysalon.vo.CouponVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 活动服务接口
 * 定义活动管理的核心业务方法
 *
 * @author BeautySalon Team
 */
public interface CampaignService {

    // ==================== 活动基础操作 ====================

    /**
     * 创建活动
     * 根据活动类型验证规则，创建新的营销活动
     *
     * @param dto 活动创建参数
     * @return 创建结果
     */
    Map<String, Object> createCampaign(CampaignCreateDTO dto);

    /**
     * 更新活动
     *
     * @param id 活动ID
     * @param dto 活动更新参数
     * @return 更新结果
     */
    Map<String, Object> updateCampaign(Long id, CampaignCreateDTO dto);

    /**
     * 取消活动
     *
     * @param id 活动ID
     * @param operatorId 操作员ID
     * @return 取消结果
     */
    Map<String, Object> cancelCampaign(Long id, Long operatorId);

    /**
     * 删除活动（逻辑删除）
     *
     * @param id 活动ID
     * @return 删除结果
     */
    Map<String, Object> deleteCampaign(Long id);

    /**
     * 根据ID获取活动详情
     *
     * @param id 活动ID
     * @return 活动详情
     */
    CampaignVO getCampaignById(Long id);

    /**
     * 分页查询活动列表
     *
     * @param queryDTO 查询参数
     * @return 活动列表
     */
    List<CampaignVO> listCampaigns(CampaignQueryDTO queryDTO);

    // ==================== 优惠券发放与核销 ====================

    /**
     * 发放优惠券给会员
     * 根据活动规则生成优惠券并发放给指定会员
     *
     * @param campaignId 活动ID
     * @param memberId 会员ID
     * @param memberName 会员名称
     * @param memberPhone 会员手机号
     * @param operatorId 操作员ID
     * @return 发放结果
     */
    Map<String, Object> issueCoupon(Long campaignId, Long memberId, String memberName, 
                                     String memberPhone, Long operatorId);

    /**
     * 批量发放优惠券
     * 适用于邀请有礼等场景，一次性生成多张优惠券
     *
     * @param campaignId 活动ID
     * @param memberId 会员ID
     * @param memberName 会员名称
     * @param memberPhone 会员手机号
     * @param quantity 发放数量
     * @param operatorId 操作员ID
     * @return 发放结果
     */
    Map<String, Object> batchIssueCoupons(Long campaignId, Long memberId, String memberName,
                                          String memberPhone, Integer quantity, Long operatorId);

    /**
     * 核销优惠券
     * 会员消费时使用优惠券，更新状态并关联订单
     *
     * @param couponId 优惠券ID
     * @param orderId 订单ID
     * @param operatorId 操作员ID
     * @return 核销结果
     */
    Map<String, Object> redeemCoupon(Long couponId, Long orderId, Long operatorId);

    /**
     * 根据优惠券码核销
     *
     * @param code 优惠券码
     * @param orderId 订单ID
     * @param operatorId 操作员ID
     * @return 核销结果
     */
    Map<String, Object> redeemCouponByCode(String code, Long orderId, Long operatorId);

    /**
     * 根据ID获取优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    CouponVO getCouponById(Long couponId);

    /**
     * 根据优惠券码获取优惠券
     *
     * @param code 优惠券码
     * @return 优惠券详情
     */
    CouponVO getCouponByCode(String code);

    /**
     * 获取会员的优惠券列表
     *
     * @param memberId 会员ID
     * @param status 优惠券状态（可选）
     * @return 优惠券列表
     */
    List<CouponVO> getMemberCoupons(Long memberId, Integer status);

    /**
     * 获取会员可用的优惠券
     *
     * @param memberId 会员ID
     * @return 可用优惠券列表
     */
    List<CouponVO> getAvailableCoupons(Long memberId);

    // ==================== 活动查询 ====================

    /**
     * 查询时间范围内正在进行的活动
     * 用于收银时判断可用的优惠活动
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活动列表
     */
    List<CampaignVO> getActiveCampaigns(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询会员可参与的活动
     *
     * @param memberId 会员ID
     * @return 可参与的活动列表
     */
    List<CampaignVO> getAvailableCampaignsForMember(Long memberId);

    /**
     * 获取活动的优惠券列表
     *
     * @param campaignId 活动ID
     * @return 优惠券列表
     */
    List<CouponVO> getCampaignCoupons(Long campaignId);

    // ==================== 活动效果统计 ====================

    /**
     * 获取活动效果统计
     * 包括发放数量、使用数量、过期数量、参与人数等
     *
     * @param campaignId 活动ID
     * @return 统计数据
     */
    Map<String, Object> getCampaignStatistics(Long campaignId);

    /**
     * 获取活动每日统计
     *
     * @param campaignId 活动ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日统计数据
     */
    List<Map<String, Object>> getCampaignDailyStatistics(Long campaignId, String startDate, String endDate);

    /**
     * 获取门店活动汇总统计
     *
     * @param storeId 门店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 汇总统计数据
     */
    Map<String, Object> getStoreCampaignSummary(Long storeId, String startDate, String endDate);

    // ==================== 定时任务支持 ====================

    /**
     * 更新过期活动状态
     * 定时任务调用，将已过期的活动状态更新为已结束
     *
     * @return 更新数量
     */
    int updateExpiredCampaignsStatus();

    /**
     * 更新过期优惠券状态
     * 定时任务调用，将已过期的优惠券状态更新为已过期
     *
     * @return 更新数量
     */
    int updateExpiredCouponsStatus();

    // ==================== 辅助方法 ====================

    /**
     * 生成优惠券码
     *
     * @param campaignId 活动ID
     * @return 优惠券码
     */
    String generateCouponCode(Long campaignId);

    /**
     * 验证优惠券是否可用
     *
     * @param couponId 优惠券ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    Map<String, Object> validateCoupon(Long couponId, Double orderAmount);

    /**
     * 计算优惠金额
     * 根据活动规则计算订单可享受的优惠
     *
     * @param campaignId 活动ID
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    Double calculateDiscount(Long campaignId, Double orderAmount);
}
