package com.beautysalon.dto;

/**
 * 预约查询DTO
 * 用于分页查询和筛选预约列表
 *
 * @author BeautySalon Team
 */
public class AppointmentQueryDTO {

    /**
     * 当前页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页记录数
     */
    private Integer limit = 10;

    /**
     * 预约单号 - 精确查询
     */
    private String appointmentNo;

    /**
     * 客户ID - 精确查询
     */
    private Long customerId;

    /**
     * 美容师/技师ID - 精确查询
     */
    private Long beauticianId;

    /**
     * 预约状态 - 精确查询
     * 0-待确认, 1-已确认, 2-已到店, 3-服务中, 4-已完成, 5-已取消, 6-失约
     */
    private Integer status;

    /**
     * 预约开始日期 - 范围查询起点
     * 格式: yyyy-MM-dd
     */
    private String startDate;

    /**
     * 预约结束日期 - 范围查询终点
     * 格式: yyyy-MM-dd
     */
    private String endDate;

    /**
     * 关键词搜索（客户姓名/手机号/美容师姓名）
     */
    private String keyword;

    // ==================== Getter/Setter ====================

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

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getBeauticianId() {
        return beauticianId;
    }

    public void setBeauticianId(Long beauticianId) {
        this.beauticianId = beauticianId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * 门店ID - 数据隔离
     */
    private Long storeId;

    /**
     * 计算分页偏移量
     */
    public Integer getOffset() {
        return (page - 1) * limit;
    }
}
