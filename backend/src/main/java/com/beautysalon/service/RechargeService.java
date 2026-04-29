package com.beautysalon.service;

import com.beautysalon.dto.RechargeDTO;
import com.beautysalon.entity.PosRecharge;
import com.beautysalon.vo.RechargeVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 充值服务接口
 * 定义充值业务的核心方法
 *
 * @author BeautySalon Team
 */
public interface RechargeService {

    /**
     * 余额充值
     * 根据满送规则计算赠送金额，更新客户余额，记录充值流水
     *
     * @param dto 充值请求参数
     * @return 充值结果（含充值记录ID、新余额等）
     */
    Map<String, Object> recharge(RechargeDTO dto);

    /**
     * 获取充值规则（满送规则）
     * 根据充值金额返回应赠送的金额
     *
     * @param amount 充值金额
     * @return 赠送金额
     */
    BigDecimal getGiftAmount(BigDecimal amount);

    /**
     * 分页查询充值记录
     *
     * @param page 页码
     * @param limit 每页数量
     * @param customerId 客户ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 充值记录列表
     */
    List<RechargeVO> listPage(int page, int limit, Long customerId, String startDate, String endDate);

    /**
     * 根据ID获取充值记录详情
     *
     * @param id 充值记录ID
     * @return 充值记录详情
     */
    RechargeVO getById(Long id);

    /**
     * 根据充值订单号获取充值记录
     *
     * @param rechargeNo 充值订单号
     * @return 充值记录
     */
    RechargeVO getByRechargeNo(String rechargeNo);

    /**
     * 获取客户当前余额
     *
     * @param customerId 客户ID
     * @return 当前余额
     */
    BigDecimal getCustomerBalance(Long customerId);

    /**
     * 获取客户充值记录列表
     *
     * @param customerId 客户ID
     * @return 充值记录列表
     */
    List<RechargeVO> getByCustomerId(Long customerId);

    /**
     * 充值退款（退还余额）
     * 仅退还余额部分，不退还赠送部分
     *
     * @param orderId 原订单ID
     * @param customerId 客户ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @param operatorId 操作员ID
     * @return 退款结果
     */
    Map<String, Object> refund(Long orderId, Long customerId, BigDecimal refundAmount, String reason, Long operatorId);

    /**
     * 日充值报表
     *
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 日充值统计
     */
    Map<String, Object> getDailyReport(String date);

    /**
     * 周充值报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周充值统计
     */
    Map<String, Object> getWeeklyReport(String startDate, String endDate);

    /**
     * 月充值报表
     *
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 月充值统计
     */
    Map<String, Object> getMonthlyReport(String yearMonth);

    /**
     * 验证充值数据有效性
     *
     * @param dto 充值请求
     * @return 验证结果
     */
    Map<String, Object> validateRecharge(RechargeDTO dto);
}
