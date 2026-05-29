package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 门店信息实体类
 *
 * @author BeautySalon Team
 */
@Data
@TableName("sys_store")
public class SysStore {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 门店编码
     */
    private String code;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 店长姓名
     */
    private String manager;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 状态: 1-正常, 0-关闭
     */
    private Integer status;

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
