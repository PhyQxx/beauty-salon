package com.beautysalon.service;

import com.beautysalon.dto.AppointmentCreateDTO;
import com.beautysalon.dto.AppointmentQueryDTO;
import com.beautysalon.vo.AppointmentVO;

import java.util.List;
import java.util.Map;

/**
 * 预约服务接口
 * 定义预约管理的业务方法
 *
 * @author BeautySalon Team
 */
public interface AppointmentService {

    /**
     * 创建预约
     * 包含时间段冲突检测
     *
     * @param dto 创建预约参数
     * @return 成功返回预约ID，失败返回错误信息
     */
    Map<String, Object> createAppointment(AppointmentCreateDTO dto);

    /**
     * 更新预约信息
     *
     * @param id 预约ID
     * @param dto 更新参数
     * @return 操作结果
     */
    Map<String, Object> updateAppointment(Long id, AppointmentCreateDTO dto);

    /**
     * 取消预约
     *
     * @param id 预约ID
     * @param reason 取消原因
     * @return 操作结果
     */
    Map<String, Object> cancelAppointment(Long id, String reason);

    /**
     * 确认预约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    Map<String, Object> confirmAppointment(Long id);

    /**
     * 客户到店登记
     *
     * @param id 预约ID
     * @return 操作结果
     */
    Map<String, Object> arriveAppointment(Long id);

    /**
     * 开始服务
     *
     * @param id 预约ID
     * @return 操作结果
     */
    Map<String, Object> startService(Long id);

    /**
     * 完成服务
     *
     * @param id 预约ID
     * @return 操作结果
     */
    Map<String, Object> completeService(Long id);

    /**
     * 分页查询预约列表
     *
     * @param queryDTO 查询条件
     * @return 预约分页列表
     */
    Map<String, Object> queryAppointmentPage(AppointmentQueryDTO queryDTO);

    /**
     * 获取预约详情
     *
     * @param id 预约ID
     * @return 预约详情
     */
    AppointmentVO getAppointmentById(Long id);

    /**
     * 查询美容师的可用预约时间段
     * 根据已预约情况计算出空闲时间段
     *
     * @param beauticianId 美容师ID
     * @param date 日期
     * @param serviceDuration 服务时长（分钟）
     * @return 可用时间段列表
     */
    List<Map<String, String>> getAvailableTimeSlots(Long beauticianId, String date, Integer serviceDuration);

    /**
     * 获取预约日历视图数据
     * 按日期范围查询，用于日历组件展示
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日历数据
     */
    List<AppointmentVO> getCalendarView(String startDate, String endDate);

    /**
     * 获取美容师排班视图
     * 查看指定日期所有美容师的预约安排
     *
     * @param date 日期
     * @return 排班列表
     */
    List<AppointmentVO> getBeauticianSchedule(String date);

    /**
     * 检测时间段是否冲突
     *
     * @param beauticianId 美容师ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（更新时使用）
     * @return true-冲突，false-不冲突
     */
    boolean checkTimeConflict(Long beauticianId, String date, String startTime, String endTime, Long excludeId);

    /**
     * 获取今日预约概览
     *
     * @return 今日统计信息
     */
    Map<String, Object> getTodayOverview();

    /**
     * 生成预约单号
     *
     * @return 预约单号
     */
    String generateAppointmentNo();

    /**
     * 标记为失约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    Map<String, Object> markNoShow(Long id);

    /**
     * 根据客户ID查询预约记录
     *
     * @param customerId 客户ID
     * @return 预约记录列表
     */
    List<AppointmentVO> getAppointmentsByCustomerId(Long customerId);
}
