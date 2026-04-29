package com.beautysalon.vo;

import com.beautysalon.entity.PosCampaign;

import java.time.LocalDateTime;

/**
 * 活动响应VO
 * 用于返回活动详情信息给客户端
 *
 * @author BeautySalon Team
 */
public class CampaignVO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型：1-打折 2-满减 3-赠品 4-限时特价 5-邀请有礼
     */
    private Integer type;

    /**
     * 活动类型描述
     */
    private String typeDescription;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动规则（JSON格式）
     */
    private String rules;

    /**
     * 活动状态：0-未开始 1-进行中 2-已结束 3-已取消
     */
    private Integer status;

    /**
     * 活动状态描述
     */
    private String statusDescription;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 已发放优惠券数量
     */
    private Integer issuedCount;

    /**
     * 已使用优惠券数量
     */
    private Integer usedCount;

    /**
     * 已过期优惠券数量
     */
    private Integer expiredCount;

    /**
     * 参与人数
     */
    private Integer participantCount;

    // Getter and Setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIssuedCount() {
        return issuedCount;
    }

    public void setIssuedCount(Integer issuedCount) {
        this.issuedCount = issuedCount;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Integer getExpiredCount() {
        return expiredCount;
    }

    public void setExpiredCount(Integer expiredCount) {
        this.expiredCount = expiredCount;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    /**
     * 将实体对象转换为VO
     *
     * @param campaign 活动实体
     * @return 活动VO
     */
    public static CampaignVO fromEntity(PosCampaign campaign) {
        CampaignVO vo = new CampaignVO();
        vo.setId(campaign.getId());
        vo.setName(campaign.getName());
        vo.setType(campaign.getType());
        vo.setTypeDescription(campaign.getTypeDescription());
        vo.setStartTime(campaign.getStartTime());
        vo.setEndTime(campaign.getEndTime());
        vo.setRules(campaign.getRules());
        vo.setStatus(campaign.getStatus());
        vo.setStatusDescription(campaign.getStatusDescription());
        vo.setDescription(campaign.getDescription());
        vo.setStoreId(campaign.getStoreId());
        vo.setOperatorId(campaign.getOperatorId());
        vo.setCreatedAt(campaign.getCreatedAt());
        return vo;
    }
}
