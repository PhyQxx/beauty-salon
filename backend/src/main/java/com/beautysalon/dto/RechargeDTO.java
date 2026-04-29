package com.beautysalon.dto;

import java.math.BigDecimal;

/**
 * 充值请求DTO
 * 用于接收客户端提交的充值请求参数
 *
 * @author BeautySalon Team
 */
public class RechargeDTO {

    /**
     * 客户ID（必填）
     */
    private Long customerId;

    /**
     * 充值金额（必填）
     */
    private BigDecimal amount;

    /**
     * 支付方式：1-现金 2-银行卡 3-微信 4-支付宝 5-会员卡
     */
    private Integer payType;

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

    // Getter and Setter methods

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
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
