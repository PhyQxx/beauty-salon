package com.beautysalon.dto;

import java.math.BigDecimal;

/**
 * 创建预约请求DTO
 * 用于接收前端创建预约时的请求参数
 *
 * @author BeautySalon Team
 */
public class AppointmentCreateDTO {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 美容师/技师ID
     */
    private Long beauticianId;

    /**
     * 服务项目ID
     */
    private Long serviceItemId;

    /**
     * 预约日期 - 格式: yyyy-MM-dd
     */
    private String appointmentDate;

    /**
     * 预约开始时间 - 格式: HH:mm
     */
    private String startTime;

    /**
     * 预约结束时间 - 格式: HH:mm
     */
    private String endTime;

    /**
     * 服务时长（分钟）
     */
    private Integer duration;

    /**
     * 预约金额
     */
    private BigDecimal amount;

    /**
     * 备注信息
     */
    private String remark;

    // ==================== Getter/Setter ====================

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

    public Long getBeauticianId() {
        return beauticianId;
    }

    public void setBeauticianId(Long beauticianId) {
        this.beauticianId = beauticianId;
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
