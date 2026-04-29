package com.beautysalon.controller;

import com.beautysalon.dto.AppointmentCreateDTO;
import com.beautysalon.dto.AppointmentQueryDTO;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.vo.AppointmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约管理 Controller
 * 负责预约的创建、查询、取消、状态变更等操作
 *
 * @author BeautySalon Team
 */
@Api(tags = "预约管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 分页查询预约列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param appointmentNo 预约单号
     * @param customerId 客户ID
     * @param beauticianId 技师ID
     * @param status 预约状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param keyword 关键词搜索
     * @return 预约分页列表
     */
    @ApiOperation("分页查询预约列表")
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String appointmentNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long beauticianId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword) {

        // 构建查询DTO
        AppointmentQueryDTO queryDTO = new AppointmentQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setLimit(limit);
        queryDTO.setAppointmentNo(appointmentNo);
        queryDTO.setCustomerId(customerId);
        queryDTO.setBeauticianId(beauticianId);
        queryDTO.setStatus(status);
        queryDTO.setStartDate(startDate);
        queryDTO.setEndDate(endDate);
        queryDTO.setKeyword(keyword);

        return appointmentService.queryAppointmentPage(queryDTO);
    }

    /**
     * 根据ID获取预约详情
     *
     * @param id 预约ID
     * @return 预约详情
     */
    @ApiOperation("根据ID获取预约详情")
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        AppointmentVO appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            result.put("success", false);
            result.put("message", "预约不存在");
            return result;
        }

        result.put("success", true);
        result.put("data", appointment);
        return result;
    }

    /**
     * 创建预约
     *
     * @param dto 预约信息
     * @return 操作结果
     */
    @ApiOperation("创建预约")
    @PostMapping
    public Map<String, Object> save(@RequestBody AppointmentCreateDTO dto) {
        return appointmentService.createAppointment(dto);
    }

    /**
     * 更新预约信息
     *
     * @param id 预约ID
     * @param dto 预约信息
     * @return 操作结果
     */
    @ApiOperation("更新预约信息")
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody AppointmentCreateDTO dto) {
        return appointmentService.updateAppointment(id, dto);
    }

    /**
     * 取消预约
     *
     * @param id 预约ID
     * @param reason 取消原因
     * @return 操作结果
     */
    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    public Map<String, Object> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return appointmentService.cancelAppointment(id, reason);
    }

    /**
     * 确认预约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("确认预约")
    @PutMapping("/{id}/confirm")
    public Map<String, Object> confirm(@PathVariable Long id) {
        return appointmentService.confirmAppointment(id);
    }

    /**
     * 客户到店
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("客户到店")
    @PutMapping("/{id}/arrived")
    public Map<String, Object> arrived(@PathVariable Long id) {
        return appointmentService.arriveAppointment(id);
    }

    /**
     * 开始服务
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("开始服务")
    @PutMapping("/{id}/start")
    public Map<String, Object> startService(@PathVariable Long id) {
        return appointmentService.startService(id);
    }

    /**
     * 完成服务
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("完成服务")
    @PutMapping("/{id}/complete")
    public Map<String, Object> complete(@PathVariable Long id) {
        return appointmentService.completeService(id);
    }

    /**
     * 标记为失约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("标记失约")
    @PutMapping("/{id}/no-show")
    public Map<String, Object> noShow(@PathVariable Long id) {
        return appointmentService.markNoShow(id);
    }

    /**
     * 查询技师的可用预约时间段
     *
     * @param beauticianId 技师ID
     * @param date 日期
     * @param serviceDuration 服务时长（分钟），可选
     * @return 可用时间段列表
     */
    @ApiOperation("查询技师可用时间段")
    @GetMapping("/available-slots")
    public Map<String, Object> getAvailableSlots(
            @RequestParam Long beauticianId,
            @RequestParam String date,
            @RequestParam(required = false) Integer serviceDuration) {

        Map<String, Object> result = new HashMap<>();

        Integer duration = serviceDuration != null ? serviceDuration : 60;
        List<Map<String, String>> slots = appointmentService.getAvailableTimeSlots(beauticianId, date, duration);

        result.put("success", true);
        result.put("data", slots);
        result.put("beauticianId", beauticianId);
        result.put("date", date);
        result.put("serviceDuration", duration);

        return result;
    }

    /**
     * 获取预约日历视图
     * 用于日历组件展示某日期范围的预约
     *
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     * @return 日历数据
     */
    @ApiOperation("获取预约日历视图")
    @GetMapping("/calendar")
    public Map<String, Object> getCalendarView(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Map<String, Object> result = new HashMap<>();

        List<AppointmentVO> calendarData = appointmentService.getCalendarView(startDate, endDate);

        result.put("success", true);
        result.put("data", calendarData);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        return result;
    }

    /**
     * 获取美容师排班视图
     * 查看指定日期所有美容师的预约安排
     *
     * @param date 日期 (yyyy-MM-dd)
     * @return 排班列表
     */
    @ApiOperation("获取美容师排班视图")
    @GetMapping("/schedule")
    public Map<String, Object> getBeauticianSchedule(@RequestParam String date) {
        Map<String, Object> result = new HashMap<>();

        List<AppointmentVO> scheduleData = appointmentService.getBeauticianSchedule(date);

        result.put("success", true);
        result.put("data", scheduleData);
        result.put("date", date);

        return result;
    }

    /**
     * 检测时间段是否冲突
     *
     * @param beauticianId 美容师ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（更新时使用）
     * @return 冲突检测结果
     */
    @ApiOperation("检测时间段冲突")
    @GetMapping("/check-conflict")
    public Map<String, Object> checkConflict(
            @RequestParam Long beauticianId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) Long excludeId) {

        Map<String, Object> result = new HashMap<>();

        boolean hasConflict = appointmentService.checkTimeConflict(beauticianId, date, startTime, endTime, excludeId);

        result.put("success", true);
        result.put("hasConflict", hasConflict);
        result.put("message", hasConflict ? "该时间段已被占用" : "该时间段可用");

        return result;
    }

    /**
     * 获取今日预约概览
     *
     * @return 今日预约统计
     */
    @ApiOperation("获取今日预约概览")
    @GetMapping("/today-overview")
    public Map<String, Object> getTodayOverview() {
        return appointmentService.getTodayOverview();
    }

    /**
     * 生成预约单号
     *
     * @return 预约单号
     */
    @ApiOperation("生成预约单号")
    @GetMapping("/generate-no")
    public Map<String, Object> generateAppointmentNo() {
        Map<String, Object> result = new HashMap<>();

        String appointmentNo = appointmentService.generateAppointmentNo();

        result.put("success", true);
        result.put("appointmentNo", appointmentNo);

        return result;
    }
}
