package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约实体类
 * 对应数据库中的预约信息表
 *
 * @author BeautySalon Team
 */
@Data
@TableName("appointment")
public class Appointment {

    /**
     * 预约ID - 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 预约单号 - 唯一编号
     */
    private String appointmentNo;

    /**
     * 客户ID - 关联客户信息
     */
    private Long customerId;

    /**
     * 客户姓名 - 反显用
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
     * 美容师姓名 - 反显用
     */
    private String beauticianName;

    /**
     * 服务项目ID
     */
    private Long serviceItemId;

    /**
     * 服务项目名称 - 反显用
     */
    private String serviceItemName;

    /**
     * 预约日期
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
     * 预约状态: 0-待确认, 1-已确认, 2-已到店, 3-服务中, 4-已完成, 5-已取消, 6-失约
     */
    private Integer status;

    /**
     * 预约金额
     */
    private BigDecimal amount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

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
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已到店";
            case 3: return "服务中";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "失约";
            default: return "未知";
        }
    }
}
