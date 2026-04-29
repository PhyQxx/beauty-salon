package com.beautysalon.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

/**
 * 客户更新请求DTO
 * 用于更新客户信息时的数据接收
 *
 * @author BeautySalon Team
 */
@Data
@ApiModel(description = "客户更新请求参数")
public class CustomerUpdateDTO {

    /**
     * 客户姓名
     */
    @ApiModelProperty(value = "客户姓名")
    private String name;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
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
     * 客户头像URL
     */
    @ApiModelProperty(value = "客户头像URL")
    private String avatar;

    /**
     * 客户备注
     */
    @ApiModelProperty(value = "客户备注")
    private String remark;

    /**
     * 客户状态：0-无效，1-有效
     */
    @ApiModelProperty(value = "客户状态：0-无效，1-有效")
    private Integer status;
}
