package com.beautysalon.mapper;

import com.beautysalon.dto.AppointmentQueryDTO;
import com.beautysalon.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约 Mapper 接口
 * 对应数据库中预约信息表的CRUD操作
 *
 * @author BeautySalon Team
 */
@Mapper
public interface AppointmentMapper {

    /**
     * 插入预约记录
     *
     * @param appointment 预约实体
     * @return 影响行数
     */
    int insert(Appointment appointment);

    /**
     * 更新预约记录
     *
     * @param appointment 预约实体
     * @return 影响行数
     */
    int update(Appointment appointment);

    /**
     * 删除预约记录（物理删除）
     *
     * @param id 预约ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询预约详情
     *
     * @param id 预约ID
     * @return 预约实体
     */
    Appointment selectById(@Param("id") Long id);

    /**
     * 根据预约单号查询
     *
     * @param appointmentNo 预约单号
     * @return 预约实体
     */
    Appointment selectByAppointmentNo(@Param("appointmentNo") String appointmentNo);

    /**
     * 分页查询预约列表
     *
     * @param queryDTO 查询条件
     * @return 预约列表
     */
    List<Appointment> selectList(AppointmentQueryDTO queryDTO);

    /**
     * 统计预约总数（用于分页）
     *
     * @param queryDTO 查询条件
     * @return 总记录数
     */
    int countList(AppointmentQueryDTO queryDTO);

    /**
     * 查询某美容师在某日期的预约列表
     *
     * @param beauticianId 美容师ID
     * @param date 日期
     * @return 预约列表
     */
    List<Appointment> selectByBeauticianAndDate(
            @Param("beauticianId") Long beauticianId,
            @Param("date") String date);

    /**
     * 查询某日期的所有预约（用于日历视图）
     *
     * @param date 日期
     * @return 预约列表
     */
    List<Appointment> selectByDate(@Param("date") String date);

    /**
     * 查询某日期范围的所有预约（用于日历视图）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约列表
     */
    List<Appointment> selectByDateRange(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 检查时间段冲突 - 检查同一美容师在同一时间是否有其他预约
     *
     * @param beauticianId 美容师ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（用于更新时排除自己）
     * @return 冲突的预约数量
     */
    int checkTimeConflict(
            @Param("beauticianId") Long beauticianId,
            @Param("date") String date,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("excludeId") Long excludeId);

    /**
     * 查询今日预约概览统计
     *
     * @param today 今日日期
     * @return 各状态的预约数量
     */
    List<Appointment> selectTodayStatistics(@Param("today") String today);

    /**
     * 更新预约状态
     *
     * @param id 预约ID
     * @param status 新状态
     * @param cancelReason 取消原因（可选）
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("cancelReason") String cancelReason);

    /**
     * 查询美容师排班信息（某日期所有美容师的预约）
     *
     * @param date 日期
     * @return 预约列表（包含美容师信息）
     */
    List<Appointment> selectScheduleByDate(@Param("date") String date);
}
