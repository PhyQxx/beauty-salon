package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 美容师实体类
 *
 * @author BeautySalon Team
 */
@Data
@TableName("beautician")
public class Beautician {

    /**
     * 美容师ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 工号
     */
    private String code;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别: 0=女 1=男 2=未知
     */
    private Integer gender;

    /**
     * 擅长领域
     */
    private String specialty;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 等级: 1=初级 2=中级 3=高级 4=首席
     */
    private Integer level;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 服务次数
     */
    private Integer serviceCount;

    /**
     * 状态: 0=离职 1=在职 2=休假
     */
    private Integer status;

    /**
     * 入职日期
     */
    private LocalDate joinDate;

    /**
     * 删除标记: 0=未删除 1=已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
