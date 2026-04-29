package com.beautysalon.mapper;

import com.beautysalon.entity.PosCampaign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 活动Mapper接口
 * 提供活动的数据库操作方法
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosCampaignMapper {

    // ==================== 基础操作 ====================

    /**
     * 插入活动
     *
     * @param campaign 活动实体
     * @return 影响行数
     */
    int insert(PosCampaign campaign);

    /**
     * 更新活动
     *
     * @param campaign 活动实体
     * @return 影响行数
     */
    int update(PosCampaign campaign);

    /**
     * 删除活动（逻辑删除）
     *
     * @param id 活动ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询活动
     *
     * @param id 活动ID
     * @return 活动
     */
    PosCampaign selectById(@Param("id") Long id);

    /**
     * 分页查询活动列表
     *
     * @param params 查询参数
     * @return 活动列表
     */
    List<PosCampaign> selectPage(Map<String, Object> params);

    /**
     * 根据门店ID查询活动列表
     *
     * @param storeId 门店ID
     * @return 活动列表
     */
    List<PosCampaign> selectByStoreId(@Param("storeId") Long storeId);

    // ==================== 状态查询 ====================

    /**
     * 查询进行中的活动
     *
     * @param now 当前时间
     * @return 进行中的活动列表
     */
    List<PosCampaign> selectOngoing(@Param("now") LocalDateTime now);

    /**
     * 按时间范围查询活动
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活动列表
     */
    List<PosCampaign> selectByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询某个时间点进行中的活动（用于判断用户是否可以使用优惠）
     *
     * @param time 指定时间点
     * @return 进行中的活动列表
     */
    List<PosCampaign> selectActiveAt(@Param("time") LocalDateTime time);

    /**
     * 根据类型查询活动
     *
     * @param type 活动类型
     * @return 活动列表
     */
    List<PosCampaign> selectByType(@Param("type") Integer type);

    /**
     * 根据状态查询活动
     *
     * @param status 活动状态
     * @return 活动列表
     */
    List<PosCampaign> selectByStatus(@Param("status") Integer status);

    // ==================== 统计操作 ====================

    /**
     * 统计门店活动数量
     *
     * @param storeId 门店ID
     * @return 活动数量
     */
    int countByStoreId(@Param("storeId") Long storeId);

    /**
     * 统计时间范围内的活动数量
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活动数量
     */
    int countByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 更新活动状态
     *
     * @param id 活动ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新活动状态（用于定时任务）
     *
     * @param beforeStatus 原状态
     * @param afterStatus 新状态
     * @param time 时间点（用于判断）
     * @return 影响行数
     */
    int batchUpdateStatusByTime(
            @Param("beforeStatus") Integer beforeStatus,
            @Param("afterStatus") Integer afterStatus,
            @Param("time") LocalDateTime time);
}
