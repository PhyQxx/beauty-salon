package com.beautysalon.mapper;

import com.beautysalon.entity.PosOrder;
import com.beautysalon.entity.PosOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 * 提供订单和订单明细的数据库操作方法
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosOrderMapper {

    // ==================== 订单操作 ====================

    /**
     * 插入订单
     *
     * @param order 订单实体
     * @return 影响行数
     */
    int insert(PosOrder order);

    /**
     * 更新订单
     *
     * @param order 订单实体
     * @return 影响行数
     */
    int update(PosOrder order);

    /**
     * 删除订单（逻辑删除）
     *
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单
     */
    PosOrder selectById(@Param("id") Long id);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单
     */
    PosOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 分页查询订单列表
     *
     * @param params 查询参数
     * @return 订单列表
     */
    List<PosOrder> selectPage(Map<String, Object> params);

    /**
     * 根据客户ID查询订单列表
     *
     * @param customerId 客户ID
     * @return 订单列表
     */
    List<PosOrder> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 按时间范围查询订单
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<PosOrder> selectByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计时间范围内的订单数
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单数
     */
    int countByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计时间范围内的实收金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 实收金额
     */
    BigDecimal sumPayAmountByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计时间范围内的退款金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 退款金额
     */
    BigDecimal sumRefundAmountByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // ==================== 订单明细操作 ====================

    /**
     * 插入订单明细
     *
     * @param item 订单明细实体
     * @return 影响行数
     */
    int insertItem(PosOrderItem item);

    /**
     * 批量插入订单明细
     *
     * @param items 订单明细列表
     * @return 影响行数
     */
    int insertItems(@Param("items") List<PosOrderItem> items);

    /**
     * 更新订单明细
     *
     * @param item 订单明细实体
     * @return 影响行数
     */
    int updateItem(PosOrderItem item);

    /**
     * 根据订单ID查询订单明细
     *
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<PosOrderItem> selectItemsByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据ID查询订单明细
     *
     * @param id 订单明细ID
     * @return 订单明细
     */
    PosOrderItem selectItemById(@Param("id") Long id);

    /**
     * 删除订单的所有明细
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteItemsByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询客户未使用的套餐明细
     *
     * @param customerId 客户ID
     * @return 未使用的套餐明细列表
     */
    List<PosOrderItem> selectUnusedPackageItems(@Param("customerId") Long customerId);

    /**
     * 使用套餐明细（标记为已使用）
     *
     * @param id 订单明细ID
     * @param usedTime 使用时间
     * @return 影响行数
     */
    int markItemUsed(@Param("id") Long id, @Param("usedTime") LocalDateTime usedTime);

    // ==================== 统计报表 ====================

    /**
     * 按支付方式统计金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 各支付方式金额列表
     */
    List<Map<String, Object>> sumAmountByPayType(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 按订单类型统计金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 各订单类型金额列表
     */
    List<Map<String, Object>> sumAmountByOrderType(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 日报表统计
     *
     * @param date 日期
     * @return 日报统计数据
     */
    Map<String, Object> selectDailyReport(@Param("date") String date);

    /**
     * 周报表统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周报统计数据
     */
    Map<String, Object> selectWeeklyReport(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 月报表统计
     *
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 月报统计数据
     */
    Map<String, Object> selectMonthlyReport(@Param("yearMonth") String yearMonth);
}
