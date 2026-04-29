package com.beautysalon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CRM客户实体类
 * 对应客户表，记录会员基本信息、等级、积分、余额等
 *
 * @author BeautySalon Team
 */
@Data
@TableName("crm_customer")
public class CrmCustomer {

    /**
     * 客户ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户姓名
     */
    private String name;

    /**
     * 手机号码，唯一标识
     */
    private String phone;

    /**
     * 客户性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    /**
     * 会员等级：1-普通会员，2-银卡会员，3-金卡会员，4-钻石会员
     */
    private Integer memberLevel;

    /**
     * 客户积分
     */
    private Integer points;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 客户状态：0-无效，1-有效
     */
    private Integer status;

    /**
     * 客户来源：1-自然到店，2-线上推广，3-老客推荐，4-其他
     */
    private Integer source;

    /**
     * 客户头像URL
     */
    private String avatar;

    /**
     * 客户备注
     */
    private String remark;

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
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
