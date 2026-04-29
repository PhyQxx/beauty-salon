package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体类
 * 记录客户的每一次充值行为，包括充值金额、赠送金额、支付方式等
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_recharge")
public class PosRecharge {

    /**
     * 充值记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 充值订单号
     */
    private String rechargeNo;

    /**
     * 充值金额（实际充值）
     */
    private BigDecimal amount;

    /**
     * 赠送金额（满送活动赠送）
     */
    private BigDecimal giftAmount;

    /**
     * 充值后余额
     */
    private BigDecimal balanceAfter;

    /**
     * 支付方式：1-现金 2-银行卡 3-微信 4-支付宝 5-会员卡
     */
    private Integer payType;

    /**
     * 充值类型：1-充值 2-赠送 3-退款返还
     */
    private Integer rechargeType;

    /**
     * 关联的原订单ID（退款返还时使用）
     */
    private Long refundOrderId;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 操作员名称
     */
    private String operatorName;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标志：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
