package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存流水记录实体类
 */
@Data
@TableName("pos_inventory_record")
public class PosInventoryRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long storeId;

    /**
     * 变动类型: 1-采购入库, 2-退货出库, 3-销售出库, 4-退货入库, 5-盘点调整, 6-领用出库
     */
    private Integer type;

    private Integer quantity;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private Long operatorId;

    private String orderNo;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
