package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡/套餐实体类
 * 对应会员卡表，记录会员卡、套餐的基本信息
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_membership_card")
public class PosMembershipCard {

    /**
     * 会员卡ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡编码，唯一标识
     */
    private String code;

    /**
     * 卡名称
     */
    private String name;

    /**
     * 卡类型: 1=充值卡 2=次卡 3=时间卡
     */
    private Integer type;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 面值/次数
     */
    private BigDecimal faceValue;

    /**
     * 有效期（天）
     */
    private Integer durationDays;

    /**
     * 卡说明
     */
    private String description;

    /**
     * 权益说明
     */
    private String benefitDesc;

    /**
     * 是否上架: 0=下架 1=上架
     */
    private Integer isActive;

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
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
