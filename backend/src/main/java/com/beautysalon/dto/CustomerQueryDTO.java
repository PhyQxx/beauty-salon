package com.beautysalon.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户查询请求DTO
 * 用于分页查询和条件筛选
 *
 * @author BeautySalon Team
 */
@Data
@ApiModel(description = "客户查询请求参数")
public class CustomerQueryDTO {

    /**
     * 当前页码，默认第1页
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer page = 1;

    /**
     * 每页记录数，默认10条
     */
    @ApiModelProperty(value = "每页记录数", example = "10")
    private Integer limit = 10;

    /**
     * 关键词搜索（支持姓名、手机号）
     */
    @ApiModelProperty(value = "关键词搜索（姓名/手机号）")
    private String keyword;

    /**
     * 客户状态：0-无效，1-有效
     */
    @ApiModelProperty(value = "客户状态：0-无效，1-有效")
    private Integer status;

    /**
     * 会员等级：1-普通会员，2-银卡会员，3-金卡会员，4-钻石会员
     */
    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

    /**
     * 客户来源：1-自然到店，2-线上推广，3-老客推荐，4-其他
     */
    @ApiModelProperty(value = "客户来源")
    private Integer source;

    /**
     * 开始日期（注册时间）
     */
    @ApiModelProperty(value = "注册开始日期")
    private String startDate;

    /**
     * 结束日期（注册时间）
     */
    @ApiModelProperty(value = "注册结束日期")
    private String endDate;

    /**
     * 排序字段，默认create_time
     */
    @ApiModelProperty(value = "排序字段")
    private String orderField = "create_time";

    /**
     * 排序方向，默认desc
     */
    @ApiModelProperty(value = "排序方向：asc/desc")
    private String orderDirection = "desc";
}
