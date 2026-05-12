package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 美容师排班实体类
 */
@Data
@TableName("beautician_schedule")
public class BeauticianSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long beauticianId;

    private LocalDate workDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer isWorking;

    private Integer maxAppointments;

    private Long storeId;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
