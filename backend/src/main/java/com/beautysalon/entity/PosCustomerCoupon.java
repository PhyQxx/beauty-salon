package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户优惠券实体类
 * 记录客户领取的优惠券及使用状态
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_customer_coupon")
public class PosCustomerCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 领取的优惠券码
     */
    private String code;

    /**
     * 状态：1=未使用, 2=已使用, 3=已过期
     */
    private Integer status;

    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用的订单ID
     */
    private Long orderId;

    /**
     * 过期时间
     */
    private LocalDateTime expiredTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
