package com.beautysalon.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

/**
 * 客户创建请求DTO
 * 用于新增客户时的数据接收
 *
 * @author BeautySalon Team
 */
@Data
@ApiModel(description = "客户创建请求参数")
public class CustomerCreateDTO {

    /**
     * 客户姓名
     */
    @ApiModelProperty(value = "客户姓名", required = true)
    private String name;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;

    /**
     * 客户性别：0-未知，1-男，2-女
     */
    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    private Integer gender;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    /**
     * 会员等级：1-普通会员，2-银卡会员，3-金卡会员，4-钻石会员
     */
    @ApiModelProperty(value = "会员等级：1-普通会员，2-银卡会员，3-金卡会员，4-钻石会员")
    private Integer memberLevel;

    /**
     * 初始积分
     */
    @ApiModelProperty(value = "初始积分")
    private Integer points;

    /**
     * 初始余额
     */
    @ApiModelProperty(value = "初始余额")
    private java.math.BigDecimal balance;

    /**
     * 客户来源：1-自然到店，2-线上推广，3-老客推荐，4-其他
     */
    @ApiModelProperty(value = "客户来源：1-自然到店，2-线上推广，3-老客推荐，4-其他")
    private Integer source;

    /**
     * 客户头像URL
     */
    @ApiModelProperty(value = "客户头像URL")
    private String avatar;

    /**
     * 客户备注
     */
    @ApiModelProperty(value = "客户备注")
    private String remark;
}
