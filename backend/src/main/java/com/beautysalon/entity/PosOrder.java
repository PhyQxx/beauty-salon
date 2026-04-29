package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 * 记录美容院的每一笔订单，包括服务订单、商品订单、套餐订单等
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_order")
public class PosOrder {

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号（全局唯一）
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 会员卡ID
     */
    private Long membershipCardId;

    /**
     * 订单类型：1-服务订单 2-商品订单 3-套餐订单 4-充值订单
     */
    private Integer orderType;

    /**
     * 订单来源：1-POS 2-小程序 3-美团 4-大众点评 5-其他
     */
    private Integer source;

    /**
     * 商品总金额/服务总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePayAmount;

    /**
     * 现金支付金额
     */
    private BigDecimal cashPayAmount;

    /**
     * 第三方支付金额（微信/支付宝/银行卡）
     */
    private BigDecimal thirdPayAmount;

    /**
     * 支付状态：0-未支付 1-已支付 2-部分支付 3-已退款
     */
    private Integer payStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付方式：1-现金 2-银行卡 3-微信 4-支付宝 5-会员卡 6-组合支付
     */
    private Integer payType;

    /**
     * 订单状态：0-待支付 1-进行中 2-已完成 3-已取消 4-已退款
     */
    private Integer status;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 美容师ID（服务订单主美容师）
     */
    private Long beauticianId;

    /**
     * 美容师名称
     */
    private String beauticianName;

    /**
     * 预约ID（如果有）
     */
    private Long appointmentId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 操作员名称
     */
    private String operatorName;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 订单明细列表
     */
    private List<PosOrderItem> items;

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
