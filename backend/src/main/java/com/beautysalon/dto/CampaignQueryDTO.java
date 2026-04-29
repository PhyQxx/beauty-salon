package com.beautysalon.dto;

import java.time.LocalDateTime;

/**
 * 活动查询DTO
 * 用于接收客户端提交的查询活动请求参数
 *
 * @author BeautySalon Team
 */
public class CampaignQueryDTO {

    /**
     * 活动名称（模糊查询）
     */
    private String name;

    /**
     * 活动类型：1-打折 2-满减 3-赠品 4-限时特价 5-邀请有礼
     */
    private Integer type;

    /**
     * 活动状态：0-未开始 1-进行中 2-已结束 3-已取消
     */
    private Integer status;

    /**
     * 开始时间（查询范围起始）
     */
    private LocalDateTime startTimeStart;

    /**
     * 开始时间（查询范围结束）
     */
    private LocalDateTime startTimeEnd;

    /**
     * 结束时间（查询范围起始）
     */
    private LocalDateTime endTimeStart;

    /**
     * 结束时间（查询范围结束）
     */
    private LocalDateTime endTimeEnd;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer limit;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getStartTimeStart() {
        return startTimeStart;
    }

    public void setStartTimeStart(LocalDateTime startTimeStart) {
        this.startTimeStart = startTimeStart;
    }

    public LocalDateTime getStartTimeEnd() {
        return startTimeEnd;
    }

    public void setStartTimeEnd(LocalDateTime startTimeEnd) {
        this.startTimeEnd = startTimeEnd;
    }

    public LocalDateTime getEndTimeStart() {
        return endTimeStart;
    }

    public void setEndTimeStart(LocalDateTime endTimeStart) {
        this.endTimeStart = endTimeStart;
    }

    public LocalDateTime getEndTimeEnd() {
        return endTimeEnd;
    }

    public void setEndTimeEnd(LocalDateTime endTimeEnd) {
        this.endTimeEnd = endTimeEnd;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
