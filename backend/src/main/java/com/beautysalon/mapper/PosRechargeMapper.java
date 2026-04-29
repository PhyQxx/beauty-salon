package com.beautysalon.mapper;

import com.beautysalon.entity.PosRecharge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 充值记录Mapper接口
 * 提供充值记录的数据库操作方法
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosRechargeMapper {

    /**
     * 插入充值记录
     *
     * @param recharge 充值记录实体
     * @return 影响行数
     */
    int insert(PosRecharge recharge);

    /**
     * 更新充值记录
     *
     * @param recharge 充值记录实体
     * @return 影响行数
     */
    int update(PosRecharge recharge);

    /**
     * 根据ID删除充值记录（逻辑删除）
     *
     * @param id 充值记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询充值记录
     *
     * @param id 充值记录ID
     * @return 充值记录
     */
    PosRecharge selectById(@Param("id") Long id);

    /**
     * 根据充值订单号查询
     *
     * @param rechargeNo 充值订单号
     * @return 充值记录
     */
    PosRecharge selectByRechargeNo(@Param("rechargeNo") String rechargeNo);

    /**
     * 分页查询充值记录
     *
     * @param params 查询参数
     * @return 充值记录列表
     */
    List<PosRecharge> selectPage(Map<String, Object> params);

    /**
     * 查询客户的所有充值记录
     *
     * @param customerId 客户ID
     * @return 充值记录列表
     */
    List<PosRecharge> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询客户最后一次充值记录（获取当前余额）
     *
     * @param customerId 客户ID
     * @return 最后一次充值记录
     */
    PosRecharge selectLastByCustomerId(@Param("customerId") Long customerId);

    /**
     * 按时间范围查询充值记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 充值记录列表
     */
    List<PosRecharge> selectByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计某时间范围内的充值总额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 充值总额
     */
    BigDecimal sumAmountByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计某时间范围内的赠送总额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 赠送总额
     */
    BigDecimal sumGiftAmountByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计某时间范围内的充值次数
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 充值次数
     */
    int countByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
