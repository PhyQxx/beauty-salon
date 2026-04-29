package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_coupon")
public class PosCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券码
     */
    private String code;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型: 1=满减券 2=折扣券 3=兑换券
     */
    private Integer type;

    /**
     * 优惠方式: 1=满减 2=折扣
     */
    private Integer discountType;

    /**
     * 使用门槛(满X元)
     */
    private BigDecimal minAmount;

    /**
     * 优惠值
     */
    private BigDecimal discountValue;

    /**
     * 折扣率(%)
     */
    private BigDecimal discountRate;

    /**
     * 有效期类型: 1=固定日期 2=领取后N天
     */
    private Integer validType;

    /**
     * 有效期开始
     */
    private LocalDate startDate;

    /**
     * 有效期结束
     */
    private LocalDate endDate;

    /**
     * 领取后有效天数
     */
    private Integer validDays;

    /**
     * 发放总数
     */
    private Integer totalCount;

    /**
     * 剩余数量
     */
    private Integer remainCount;

    /**
     * 每人限领
     */
    private Integer perLimit;

    /**
     * 已领取数量
     */
    private Integer receiveCount;

    /**
     * 已使用数量
     */
    private Integer useCount;

    /**
     * 状态: 0=未启用 1=启用 2=已下架
     */
    private Integer status;

    /**
     * 适用对象: 1=全部 2=新人 3=会员
     */
    private Integer targetType;

    /**
     * 适用服务ID列表(逗号分隔)
     */
    private String serviceIds;

    /**
     * 优惠券图片
     */
    private String imageUrl;

    /**
     * 关联活动ID
     */
    private Long campaignId;

    /**
     * 使用说明
     */
    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
