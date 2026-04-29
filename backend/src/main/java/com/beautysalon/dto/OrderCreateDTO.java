package com.beautysalon.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建DTO
 * 用于接收客户端提交的创建订单请求参数
 *
 * @author BeautySalon Team
 */
public class OrderCreateDTO {

    /**
     * 客户ID（必填）
     */
    private Long customerId;

    /**
     * 会员卡ID（可选）
     */
    private Long membershipCardId;

    /**
     * 订单类型：1-服务订单 2-商品订单 3-套餐订单
     */
    private Integer orderType;

    /**
     * 订单来源：1-POS 2-小程序 3-美团 4-大众点评 5-其他
     */
    private Integer source;

    /**
     * 美容师ID（服务订单主美容师）
     */
    private Long beauticianId;

    /**
     * 预约ID（可选）
     */
    private Long appointmentId;

    /**
     * 订单明细列表（必填）
     */
    private List<OrderItemDTO> items;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 支付方式：1-现金 2-银行卡 3-微信 4-支付宝 5-会员卡 6-组合支付
     */
    private Integer payType;

    /**
     * 余额支付金额
     */
    private BigDecimal balancePayAmount;

    /**
     * 现金支付金额
     */
    private BigDecimal cashPayAmount;

    /**
     * 第三方支付金额
     */
    private BigDecimal thirdPayAmount;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 订单明细DTO内部类
     */
    public static class OrderItemDTO {

        /**
         * 项目类型：1-服务 2-商品 3-套餐
         */
        private Integer itemType;

        /**
         * 项目ID
         */
        private Long productId;

        /**
         * 项目名称
         */
        private String productName;

        /**
         * 单价
         */
        private BigDecimal unitPrice;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 小计金额
         */
        private BigDecimal subtotal;

        /**
         * 美容师ID（服务项目）
         */
        private Long beauticianId;

        /**
         * 有效期天数（套餐）
         */
        private Integer validDays;

        /**
         * 备注
         */
        private String remark;

        // Getter and Setter methods

        public Integer getItemType() {
            return itemType;
        }

        public void setItemType(Integer itemType) {
            this.itemType = itemType;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public Long getBeauticianId() {
            return beauticianId;
        }

        public void setBeauticianId(Long beauticianId) {
            this.beauticianId = beauticianId;
        }

        public Integer getValidDays() {
            return validDays;
        }

        public void setValidDays(Integer validDays) {
            this.validDays = validDays;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    // Getter and Setter methods

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMembershipCardId() {
        return membershipCardId;
    }

    public void setMembershipCardId(Long membershipCardId) {
        this.membershipCardId = membershipCardId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Long getBeauticianId() {
        return beauticianId;
    }

    public void setBeauticianId(Long beauticianId) {
        this.beauticianId = beauticianId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getBalancePayAmount() {
        return balancePayAmount;
    }

    public void setBalancePayAmount(BigDecimal balancePayAmount) {
        this.balancePayAmount = balancePayAmount;
    }

    public BigDecimal getCashPayAmount() {
        return cashPayAmount;
    }

    public void setCashPayAmount(BigDecimal cashPayAmount) {
        this.cashPayAmount = cashPayAmount;
    }

    public BigDecimal getThirdPayAmount() {
        return thirdPayAmount;
    }

    public void setThirdPayAmount(BigDecimal thirdPayAmount) {
        this.thirdPayAmount = thirdPayAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
