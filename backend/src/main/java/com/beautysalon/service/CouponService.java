package com.beautysalon.service;

import com.beautysalon.entity.PosCoupon;

import java.util.List;
import java.util.Map;

/**
 * 优惠券服务接口
 *
 * @author BeautySalon Team
 */
public interface CouponService {

    /**
     * 分页查询优惠券列表
     */
    Map<String, Object> queryCouponPage(Integer page, Integer limit, String keyword, Integer status);

    /**
     * 获取优惠券详情
     */
    PosCoupon getCouponById(Long id);

    /**
     * 创建优惠券
     */
    Long createCoupon(PosCoupon coupon);

    /**
     * 更新优惠券
     */
    boolean updateCoupon(Long id, PosCoupon coupon);

    /**
     * 删除优惠券
     */
    boolean deleteCoupon(Long id);

    /**
     * 发放优惠券给客户
     */
    boolean distributeCoupon(Long couponId, Long customerId);

    /**
     * 客户领取优惠券
     */
    boolean receiveCoupon(Long couponId, Long customerId);

    /**
     * 使用优惠券
     */
    boolean useCoupon(Long customerCouponId, Long orderId);

    /**
     * 获取客户优惠券列表
     */
    Map<String, Object> getCustomerCoupons(Long customerId, Integer status);

    /**
     * 更新优惠券状态（过期检查等定时任务）
     */
    int updateExpiredCouponsStatus();
}
