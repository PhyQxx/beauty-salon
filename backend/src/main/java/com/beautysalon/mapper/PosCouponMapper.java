package com.beautysalon.mapper;

import com.beautysalon.entity.PosCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券Mapper接口
 * 提供优惠券的数据库操作方法
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosCouponMapper {

    // ==================== 基础操作 ====================

    /**
     * 插入优惠券
     *
     * @param coupon 优惠券实体
     * @return 影响行数
     */
    int insert(PosCoupon coupon);

    /**
     * 批量插入优惠券
     *
     * @param coupons 优惠券列表
     * @return 影响行数
     */
    int insertBatch(@Param("coupons") List<PosCoupon> coupons);

    /**
     * 更新优惠券
     *
     * @param coupon 优惠券实体
     * @return 影响行数
     */
    int update(PosCoupon coupon);

    /**
     * 删除优惠券（逻辑删除）
     *
     * @param id 优惠券ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询优惠券
     *
     * @param id 优惠券ID
     * @return 优惠券
     */
    PosCoupon selectById(@Param("id") Long id);

    /**
     * 根据优惠券码查询
     *
     * @param code 优惠券码
     * @return 优惠券
     */
    PosCoupon selectByCode(@Param("code") String code);

    /**
     * 分页查询优惠券列表
     *
     * @param params 查询参数
     * @return 优惠券列表
     */
    List<PosCoupon> selectPage(Map<String, Object> params);

    // ==================== 会员优惠券查询 ====================

    /**
     * 根据会员ID查询优惠券列表
     *
     * @param memberId 会员ID
     * @return 优惠券列表
     */
    List<PosCoupon> selectByMemberId(@Param("memberId") Long memberId);

    /**
     * 根据会员ID和状态查询优惠券
     *
     * @param memberId 会员ID
     * @param status 优惠券状态
     * @return 优惠券列表
     */
    List<PosCoupon> selectByMemberIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("status") Integer status);

    /**
     * 查询会员可用的优惠券（待使用且在有效期内）
     *
     * @param memberId 会员ID
     * @param now 当前时间
     * @return 可用优惠券列表
     */
    List<PosCoupon> selectAvailableByMemberId(
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now);

    // ==================== 活动优惠券查询 ====================

    /**
     * 根据活动ID查询优惠券列表
     *
     * @param campaignId 活动ID
     * @return 优惠券列表
     */
    List<PosCoupon> selectByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * 统计活动发放的优惠券数量
     *
     * @param campaignId 活动ID
     * @return 已发放数量
     */
    int countByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * 统计活动已使用的优惠券数量
     *
     * @param campaignId 活动ID
     * @return 已使用数量
     */
    int countUsedByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * 统计活动已过期的优惠券数量
     *
     * @param campaignId 活动ID
     * @return 已过期数量
     */
    int countExpiredByCampaignId(@Param("campaignId") Long campaignId);

    // ==================== 核销操作 ====================

    /**
     * 使用优惠券（核销）
     *
     * @param id 优惠券ID
     * @param usedAt 使用时间
     * @param orderId 使用的订单ID
     * @param operatorId 操作员ID
     * @return 影响行数
     */
    int useCoupon(
            @Param("id") Long id,
            @Param("usedAt") LocalDateTime usedAt,
            @Param("orderId") Long orderId,
            @Param("operatorId") Long operatorId);

    /**
     * 根据优惠券码核销
     *
     * @param code 优惠券码
     * @param usedAt 使用时间
     * @param orderId 使用的订单ID
     * @param operatorId 操作员ID
     * @return 影响行数
     */
    int useCouponByCode(
            @Param("code") String code,
            @Param("usedAt") LocalDateTime usedAt,
            @Param("orderId") Long orderId,
            @Param("operatorId") Long operatorId);

    // ==================== 状态更新 ====================

    /**
     * 更新优惠券状态
     *
     * @param id 优惠券ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新过期优惠券状态
     *
     * @param now 当前时间
     * @return 影响行数
     */
    int batchUpdateExpiredStatus(@Param("now") LocalDateTime now);

    // ==================== 统计操作 ====================

    /**
     * 统计会员已使用的优惠券数量
     *
     * @param memberId 会员ID
     * @return 已使用数量
     */
    int countUsedByMemberId(@Param("memberId") Long memberId);

    /**
     * 统计会员参与活动的次数（去重）
     *
     * @param campaignId 活动ID
     * @return 参与人数
     */
    int countDistinctMembersByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * 按状态统计优惠券数量
     *
     * @param campaignId 活动ID
     * @param status 状态
     * @return 数量
     */
    int countByCampaignIdAndStatus(
            @Param("campaignId") Long campaignId,
            @Param("status") Integer status);

    // ==================== 报表查询 ====================

    /**
     * 查询活动效果统计数据
     *
     * @param campaignId 活动ID
     * @return 统计数据
     */
    Map<String, Object> selectCampaignStatistics(@Param("campaignId") Long campaignId);

    /**
     * 按天统计优惠券发放情况
     *
     * @param campaignId 活动ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日统计数据
     */
    List<Map<String, Object>> selectDailyStatisticsByCampaignId(
            @Param("campaignId") Long campaignId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
