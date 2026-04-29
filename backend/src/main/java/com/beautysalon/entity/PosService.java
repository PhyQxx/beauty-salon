package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体类
 * 对应服务项目表，记录服务项目的基本信息
 *
 * @author BeautySalon Team
 */
@Data
@TableName("pos_service")
public class PosService {

    /**
     * 服务项目ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目编码，唯一标识
     */
    private String code;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目分类: 护肤/美发/美甲/化妆/按摩等
     */
    private String category;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 预计时长（分钟）
     */
    private Integer duration;

    /**
     * 标准价格
     */
    private BigDecimal price;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 项目图片URL
     */
    private String imageUrl;

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
