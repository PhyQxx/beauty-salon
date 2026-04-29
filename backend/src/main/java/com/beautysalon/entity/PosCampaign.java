package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动实体类
 * 记录美容院的所有营销活动，包括打折、满减、赠品、限时特价、邀请有礼等类型
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_campaign")
public class PosCampaign {

    /**
     * 活动ID
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除标志：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 获取活动类型描述
     */
    public String getTypeDescription() {
        if (type == null) return "";
        switch (type) {
            case 1: return "打折";
            case 2: return "满减";
            case 3: return "赠品";
            case 4: return "限时特价";
            case 5: return "邀请有礼";
            default: return "未知";
        }
    }

    /**
     * 获取活动状态描述
     */
    public String getStatusDescription() {
        if (status == null) return "";
        switch (status) {
            case 0: return "未开始";
            case 1: return "进行中";
            case 2: return "已结束";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
