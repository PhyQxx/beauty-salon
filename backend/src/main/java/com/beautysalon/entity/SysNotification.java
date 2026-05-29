package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息通知记录实体类
 *
 * @author BeautySalon Team
 */
@Data
@TableName("sys_notification")
public class SysNotification {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 接收用户ID (如果是员工)
     */
    private Long userId;

    /**
     * 接收客户ID (如果是会员)
     */
    private Long customerId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型: 1-预约成功, 2-预约提醒, 3-消费通知, 4-系统通知
     */
    private Integer type;

    /**
     * 渠道: 1-站内信, 2-短信, 3-小程序订阅消息
     */
    private Integer channel;

    /**
     * 发送状态: 0-未发送, 1-发送成功, 2-发送失败
     */
    private Integer status;

    /**
     * 阅读状态: 0-未读, 1-已读
     */
    private Integer readStatus;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

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
}
