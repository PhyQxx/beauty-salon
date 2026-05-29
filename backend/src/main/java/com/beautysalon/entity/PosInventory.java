package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存实体类
 */
@Data
@TableName("pos_inventory")
public class PosInventory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long storeId;

    private Integer stockQuantity;

    private LocalDateTime lastInventoryTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
