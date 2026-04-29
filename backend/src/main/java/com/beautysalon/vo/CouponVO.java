package com.beautysalon.vo;

import com.beautysalon.entity.PosCoupon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 优惠券响应VO
 *
 * @author BeautySalon Team
 */
public class CouponVO {

    private Long id;
    private String code;
    private String name;
    private Integer type;           // 1=满减券 2=折扣券 3=兑换券
    private Integer discountType;   // 1=满减 2=折扣
    private BigDecimal minAmount;   // 使用门槛
    private BigDecimal discountValue;
    private BigDecimal discountRate;
    private Integer validType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer validDays;
    private Integer totalCount;
    private Integer remainCount;
    private Integer perLimit;
    private Integer receiveCount;
    private Integer useCount;
    private Integer status;
    private Integer targetType;
    private String serviceIds;
    private String imageUrl;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getter / Setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getDiscountType() { return discountType; }
    public void setDiscountType(Integer discountType) { this.discountType = discountType; }

    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public Integer getValidType() { return validType; }
    public void setValidType(Integer validType) { this.validType = validType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getRemainCount() { return remainCount; }
    public void setRemainCount(Integer remainCount) { this.remainCount = remainCount; }

    public Integer getPerLimit() { return perLimit; }
    public void setPerLimit(Integer perLimit) { this.perLimit = perLimit; }

    public Integer getReceiveCount() { return receiveCount; }
    public void setReceiveCount(Integer receiveCount) { this.receiveCount = receiveCount; }

    public Integer getUseCount() { return useCount; }
    public void setUseCount(Integer useCount) { this.useCount = useCount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getTargetType() { return targetType; }
    public void setTargetType(Integer targetType) { this.targetType = targetType; }

    public String getServiceIds() { return serviceIds; }
    public void setServiceIds(String serviceIds) { this.serviceIds = serviceIds; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public static CouponVO fromEntity(PosCoupon coupon) {
        CouponVO vo = new CouponVO();
        vo.setId(coupon.getId());
        vo.setCode(coupon.getCode());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setDiscountType(coupon.getDiscountType());
        vo.setMinAmount(coupon.getMinAmount());
        vo.setDiscountValue(coupon.getDiscountValue());
        vo.setDiscountRate(coupon.getDiscountRate());
        vo.setValidType(coupon.getValidType());
        vo.setStartDate(coupon.getStartDate());
        vo.setEndDate(coupon.getEndDate());
        vo.setValidDays(coupon.getValidDays());
        vo.setTotalCount(coupon.getTotalCount());
        vo.setRemainCount(coupon.getRemainCount());
        vo.setPerLimit(coupon.getPerLimit());
        vo.setReceiveCount(coupon.getReceiveCount());
        vo.setUseCount(coupon.getUseCount());
        vo.setStatus(coupon.getStatus());
        vo.setTargetType(coupon.getTargetType());
        vo.setServiceIds(coupon.getServiceIds());
        vo.setImageUrl(coupon.getImageUrl());
        vo.setDescription(coupon.getDescription());
        vo.setCreateTime(coupon.getCreateTime());
        vo.setUpdateTime(coupon.getUpdateTime());
        return vo;
    }
}
