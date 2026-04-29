package com.beautysalon.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单响应VO
 * 用于返回订单的展示信息
 *
 * @author BeautySalon Team
 */
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
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
     * 订单类型：1-服务订单 2-商品订单 3-套餐订单 4-充值订单
     */
    private Integer orderType;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 订单来源名称
     */
    private String sourceName;

    /**
     * 商品总金额
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
     * 第三方支付金额
     */
    private BigDecimal thirdPayAmount;

    /**
     * 支付状态
     */
    private Integer payStatus;

    /**
     * 支付状态名称
     */
    private String payStatusName;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态名称
     */
    private String statusName;

    /**
     * 完成时间
     */
    private String completeTime;

    /**
     * 美容师名称
     */
    private String beauticianName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 操作员名称
     */
    private String operatorName;

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 订单明细VO内部类
     */
    public static class OrderItemVO {

        /**
         * 订单明细ID
         */
        private Long id;

        /**
         * 项目类型名称
         */
        private String itemTypeName;

        /**
         * 项目名称
         */
        private String productName;

        /**
         * 项目分类
         */
        private String category;

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
         * 优惠金额
         */
        private BigDecimal discountAmount;

        /**
         * 折后金额
         */
        private BigDecimal discountedPrice;

        /**
         * 是否赠送
         */
        private String isGiftName;

        /**
         * 美容师名称
         */
        private String beauticianName;

        /**
         * 是否已使用
         */
        private String usedName;

        /**
         * 有效期
         */
        private String validPeriod;

        // Getter and Setter methods

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getItemTypeName() {
            return itemTypeName;
        }

        public void setItemTypeName(String itemTypeName) {
            this.itemTypeName = itemTypeName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }

        public BigDecimal getDiscountedPrice() {
            return discountedPrice;
        }

        public void setDiscountedPrice(BigDecimal discountedPrice) {
            this.discountedPrice = discountedPrice;
        }

        public String getIsGiftName() {
            return isGiftName;
        }

        public void setIsGiftName(String isGiftName) {
            this.isGiftName = isGiftName;
        }

        public String getBeauticianName() {
            return beauticianName;
        }

        public void setBeauticianName(String beauticianName) {
            this.beauticianName = beauticianName;
        }

        public String getUsedName() {
            return usedName;
        }

        public void setUsedName(String usedName) {
            this.usedName = usedName;
        }

        public String getValidPeriod() {
            return validPeriod;
        }

        public void setValidPeriod(String validPeriod) {
            this.validPeriod = validPeriod;
        }
    }

    // Getter and Setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
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

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayStatusName() {
        return payStatusName;
    }

    public void setPayStatusName(String payStatusName) {
        this.payStatusName = payStatusName;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getBeauticianName() {
        return beauticianName;
    }

    public void setBeauticianName(String beauticianName) {
        this.beauticianName = beauticianName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public List<OrderItemVO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemVO> items) {
        this.items = items;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
