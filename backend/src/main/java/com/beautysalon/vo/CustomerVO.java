package com.beautysalon.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户响应VO
 * 用于返回客户详情信息
 *
 * @author BeautySalon Team
 */
@Data
@ApiModel(description = "客户详情响应")
public class CustomerVO {

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private Long id;

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
     * 性别文本
     */
    @ApiModelProperty(value = "性别")
    private String genderText;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 会员等级值
     */
    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

    /**
     * 会员等级文本
     */
    @ApiModelProperty(value = "会员等级名称")
    private String memberLevelText;

    /**
     * 客户积分
     */
    @ApiModelProperty(value = "积分")
    private Integer points;

    /**
     * 账户余额
     */
    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    /**
     * 赠送金余额
     */
    @ApiModelProperty(value = "赠送金余额")
    private BigDecimal giftBalance;

    /**
     * 累计消费金额
     */
    @ApiModelProperty(value = "累计消费")
    private BigDecimal totalConsume;

    /**
     * 累计获得积分
     */
    @ApiModelProperty(value = "累计获得积分")
    private Integer totalPointsEarned;

    /**
     * 累计使用积分
     */
    @ApiModelProperty(value = "累计使用积分")
    private Integer totalPointsUsed;

    /**
     * 归属门店ID
     */
    @ApiModelProperty(value = "归属门店ID")
    private Long storeId;

    /**
     * 客户状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 状态文本
     */
    @ApiModelProperty(value = "状态名称")
    private String statusText;

    /**
     * 客户来源值
     */
    @ApiModelProperty(value = "来源")
    private Integer source;

    /**
     * 来源文本
     */
    @ApiModelProperty(value = "来源名称")
    private String sourceText;

    /**
     * 客户头像URL
     */
    @ApiModelProperty(value = "头像URL")
    private String avatar;

    /**
     * 客户备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
