package com.beautysalon.dto;

import java.time.LocalDateTime;

/**
 * 活动创建DTO
 * 用于接收客户端提交的创建活动请求参数
 *
 * @author BeautySalon Team
 */
public class CampaignCreateDTO {

    /**
     * 活动名称（必填）
     */
    private String name;

    /**
     * 活动类型：1-打折 2-满减 3-赠品 4-限时特价 5-邀请有礼（必填）
     */
    private Integer type;

    /**
     * 活动开始时间（必填）
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间（必填）
     */
    private LocalDateTime endTime;

    /**
     * 活动规则JSON字符串（必填）
     */
    private String rules;

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

    // Getter and Setter methods

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
}
