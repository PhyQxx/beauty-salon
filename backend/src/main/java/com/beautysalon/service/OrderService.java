package com.beautysalon.service;

import com.beautysalon.dto.OrderCreateDTO;
import com.beautysalon.entity.PosOrder;
import com.beautysalon.vo.OrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 * 定义订单业务的核心方法
 *
 * @author BeautySalon Team
 */
public interface OrderService {

    // ==================== 订单基础操作 ====================

    /**
     * 创建服务订单
     *
     * @param dto 订单创建参数
     * @return 创建结果（含订单ID、订单号等）
     */
    Map<String, Object> createServiceOrder(OrderCreateDTO dto);

    /**
     * 创建商品订单
     *
     * @param dto 订单创建参数
     * @return 创建结果
     */
    Map<String, Object> createProductOrder(OrderCreateDTO dto);

    /**
     * 创建套餐订单
     *
     * @param dto 订单创建参数
     * @return 创建结果
     */
    Map<String, Object> createPackageOrder(OrderCreateDTO dto);

    /**
     * 支付订单
     * 支持多种支付方式组合，更新订单状态和客户余额
     *
     * @param orderId 订单ID
     * @param payType 支付方式
     * @param operatorId 操作员ID
     * @return 支付结果
     */
    Map<String, Object> pay(Long orderId, Integer payType, Long operatorId);

    /**
     * 消费扣款
     * 使用会员卡余额支付订单
     *
     * @param customerId 客户ID
     * @param amount 扣款金额
     * @param orderId 关联订单ID
     * @param operatorId 操作员ID
     * @return 扣款结果
     */
    Map<String, Object> deductBalance(Long customerId, BigDecimal amount, Long orderId, Long operatorId);

    /**
     * 订单退款
     * 全额或部分退款，退还实际支付部分（不退赠送部分）
     *
     * @param orderId 订单ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @param operatorId 操作员ID
     * @return 退款结果
     */
    Map<String, Object> refund(Long orderId, BigDecimal refundAmount, String reason, Long operatorId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param reason 取消原因
     * @param operatorId 操作员ID
     * @return 取消结果
     */
    Map<String, Object> cancel(Long orderId, String reason, Long operatorId);

    /**
     * 完成订单
     * 标记订单为已完成状态
     *
     * @param orderId 订单ID
     * @param operatorId 操作员ID
     * @return 完成结果
     */
    Map<String, Object> complete(Long orderId, Long operatorId);

    // ==================== 查询操作 ====================

    /**
     * 分页查询订单列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param orderNo 订单号（可选）
     * @param customerId 客户ID（可选）
     * @param orderType 订单类型（可选）
     * @param payStatus 支付状态（可选）
     * @param status 订单状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 订单列表
     */
    List<OrderVO> listPage(int page, int limit, String orderNo, Long customerId, Integer orderType,
                          Integer payStatus, Integer status, String startDate, String endDate);

    /**
     * 根据ID获取订单详情（含明细）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getById(Long orderId);

    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderVO getByOrderNo(String orderNo);

    /**
     * 获取客户的订单列表
     *
     * @param customerId 客户ID
     * @return 订单列表
     */
    List<OrderVO> getByCustomerId(Long customerId);

    /**
     * 使用套餐明细
     * 标记套餐项为已使用
     *
     * @param itemId 订单明细ID
     * @param beauticianId 美容师ID
     * @return 使用结果
     */
    Map<String, Object> usePackageItem(Long itemId, Long beauticianId);

    // ==================== 报表统计 ====================

    /**
     * 日结报表
     * 统计指定日期的订单情况
     *
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 日结数据
     */
    Map<String, Object> getDailyReport(String date);

    /**
     * 周报表
     * 统计指定周的订单情况
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周统计数据
     */
    Map<String, Object> getWeeklyReport(String startDate, String endDate);

    /**
     * 月报表
     * 统计指定月的订单情况
     *
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 月统计数据
     */
    Map<String, Object> getMonthlyReport(String yearMonth);

    /**
     * 营业统计
     * 统计时间范围内的营业收入
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 营业统计
     */
    Map<String, Object> getStatistics(String startDate, String endDate);

    // ==================== 辅助方法 ====================

    /**
     * 验证订单数据有效性
     *
     * @param dto 订单创建参数
     * @return 验证结果
     */
    Map<String, Object> validateOrder(OrderCreateDTO dto);

    /**
     * 生成订单号
     *
     * @param prefix 订单号前缀
     * @return 订单号
     */
    String generateOrderNo(String prefix);

    /**
     * 计算订单金额
     *
     * @param dto 订单创建参数
     * @return 订单金额信息
     */
    Map<String, BigDecimal> calculateAmount(OrderCreateDTO dto);
}
