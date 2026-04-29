package com.beautysalon.service;

import com.beautysalon.dto.AppointmentCreateDTO;
import com.beautysalon.dto.AppointmentQueryDTO;
import com.beautysalon.entity.Appointment;
import com.beautysalon.mapper.AppointmentMapper;
import com.beautysalon.vo.AppointmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 预约服务实现类
 * 实现预约的创建、查询、状态变更等业务逻辑
 *
 * @author BeautySalon Team
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 工作时间配置（可配置化）
     * 默认营业时间：09:00 - 21:00
     */
    private static final String WORK_START_TIME = "09:00";
    private static final String WORK_END_TIME = "21:00";

    /**
     * 时间段间隔（分钟）
     */
    private static final int TIME_SLOT_INTERVAL = 30;

    /**
     * 预约状态常量
     */
    private static final int STATUS_PENDING = 0;      // 待确认
    private static final int STATUS_CONFIRMED = 1;    // 已确认
    private static final int STATUS_ARRIVED = 2;      // 已到店
    private static final int STATUS_SERVICE = 3;      // 服务中
    private static final int STATUS_COMPLETED = 4;    // 已完成
    private static final int STATUS_CANCELLED = 5;    // 已取消
    private static final int STATUS_NO_SHOW = 6;      // 失约

    @Override
    public Map<String, Object> createAppointment(AppointmentCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();

        // 1. 参数校验
        if (dto.getBeauticianId() == null) {
            result.put("success", false);
            result.put("message", "请选择美容师");
            return result;
        }
        if (dto.getServiceItemId() == null) {
            result.put("success", false);
            result.put("message", "请选择服务项目");
            return result;
        }
        if (!StringUtils.hasText(dto.getAppointmentDate())) {
            result.put("success", false);
            result.put("message", "请选择预约日期");
            return result;
        }
        if (!StringUtils.hasText(dto.getStartTime())) {
            result.put("success", false);
            result.put("message", "请选择预约开始时间");
            return result;
        }

        // 2. 计算结束时间（如果未指定）
        String endTime = dto.getEndTime();
        if (!StringUtils.hasText(endTime) && dto.getDuration() != null) {
            endTime = calculateEndTime(dto.getStartTime(), dto.getDuration());
            dto.setEndTime(endTime);
        }

        // 3. 检测时间段冲突
        if (checkTimeConflict(dto.getBeauticianId(), dto.getAppointmentDate(),
                dto.getStartTime(), endTime, null)) {
            result.put("success", false);
            result.put("message", "该时间段已被其他预约占用，请选择其他时间");
            return result;
        }

        // 4. 生成预约单号
        String appointmentNo = generateAppointmentNo();

        // 5. 构建预约实体
        Appointment appointment = new Appointment();
        appointment.setAppointmentNo(appointmentNo);
        appointment.setCustomerId(dto.getCustomerId());
        appointment.setCustomerName(dto.getCustomerName());
        appointment.setCustomerPhone(dto.getCustomerPhone());
        appointment.setBeauticianId(dto.getBeauticianId());
        appointment.setServiceItemId(dto.getServiceItemId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setDuration(dto.getDuration());
        appointment.setAmount(dto.getAmount());
        appointment.setRemark(dto.getRemark());
        appointment.setStatus(STATUS_PENDING);
        appointment.setDeleted(0);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        // 6. 保存到数据库
        appointmentMapper.insert(appointment);

        result.put("success", true);
        result.put("message", "预约创建成功");
        result.put("data", appointment.getId());
        result.put("appointmentNo", appointmentNo);
        return result;
    }

    @Override
    public Map<String, Object> updateAppointment(Long id, AppointmentCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询原预约
        Appointment existing = appointmentMapper.selectById(id);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "预约不存在");
            return result;
        }

        // 2. 检查状态（已取消或已完成不能修改）
        if (existing.getStatus() == STATUS_CANCELLED) {
            result.put("success", false);
            result.put("message", "已取消的预约不能修改");
            return result;
        }
        if (existing.getStatus() == STATUS_COMPLETED) {
            result.put("success", false);
            result.put("message", "已完成的预约不能修改");
            return result;
        }

        // 3. 计算结束时间
        String endTime = dto.getEndTime();
        if (!StringUtils.hasText(endTime) && dto.getDuration() != null) {
            endTime = calculateEndTime(dto.getStartTime(), dto.getDuration());
        }

        // 4. 检测时间段冲突（排除自己）
        if (checkTimeConflict(dto.getBeauticianId(), dto.getAppointmentDate(),
                dto.getStartTime(), endTime, id)) {
            result.put("success", false);
            result.put("message", "该时间段已被其他预约占用，请选择其他时间");
            return result;
        }

        // 5. 更新预约信息
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setBeauticianId(dto.getBeauticianId());
        appointment.setServiceItemId(dto.getServiceItemId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setDuration(dto.getDuration());
        appointment.setAmount(dto.getAmount());
        appointment.setRemark(dto.getRemark());
        appointment.setUpdateTime(LocalDateTime.now());

        appointmentMapper.update(appointment);

        result.put("success", true);
        result.put("message", "预约更新成功");
        return result;
    }

    @Override
    public Map<String, Object> cancelAppointment(Long id, String reason) {
        Map<String, Object> result = new HashMap<>();

        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            result.put("success", false);
            result.put("message", "预约不存在");
            return result;
        }

        if (appointment.getStatus() == STATUS_CANCELLED) {
            result.put("success", false);
            result.put("message", "预约已经取消，无需重复操作");
            return result;
        }
        if (appointment.getStatus() == STATUS_COMPLETED) {
            result.put("success", false);
            result.put("message", "已完成的预约不能取消");
            return result;
        }

        appointmentMapper.updateStatus(id, STATUS_CANCELLED, reason);

        result.put("success", true);
        result.put("message", "预约取消成功");
        return result;
    }

    @Override
    public Map<String, Object> confirmAppointment(Long id) {
        return updateStatus(id, STATUS_CONFIRMED, "预约已确认", null);
    }

    @Override
    public Map<String, Object> arriveAppointment(Long id) {
        return updateStatus(id, STATUS_ARRIVED, "客户已到店", null);
    }

    @Override
    public Map<String, Object> startService(Long id) {
        return updateStatus(id, STATUS_SERVICE, "服务已开始", null);
    }

    @Override
    public Map<String, Object> completeService(Long id) {
        return updateStatus(id, STATUS_COMPLETED, "服务已完成", null);
    }

    @Override
    public Map<String, Object> queryAppointmentPage(AppointmentQueryDTO queryDTO) {
        Map<String, Object> result = new HashMap<>();

        // 查询列表
        List<Appointment> list = appointmentMapper.selectList(queryDTO);
        // 查询总数
        int total = appointmentMapper.countList(queryDTO);

        // 转换为VO
        List<AppointmentVO> voList = convertToVOList(list);

        result.put("success", true);
        result.put("data", voList);
        result.put("total", total);
        result.put("page", queryDTO.getPage());
        result.put("limit", queryDTO.getLimit());
        return result;
    }

    @Override
    public AppointmentVO getAppointmentById(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return null;
        }
        return convertToVO(appointment);
    }

    @Override
    public List<Map<String, String>> getAvailableTimeSlots(Long beauticianId, String date, Integer serviceDuration) {
        List<Map<String, String>> availableSlots = new ArrayList<>();

        // 获取该美容师指定日期的已预约时间段
        List<Appointment> bookedAppointments = appointmentMapper.selectByBeauticianAndDate(beauticianId, date);

        // 将已预约时间转换为Set便于快速查找
        Set<String> bookedTimes = new HashSet<>();
        for (Appointment appt : bookedAppointments) {
            // 只处理非取消和非完成的预约
            if (appt.getStatus() != STATUS_CANCELLED && appt.getStatus() != STATUS_COMPLETED) {
                bookedTimes.add(appt.getStartTime() + "-" + appt.getEndTime());
            }
        }

        // 生成所有可能的时间段
        List<String> allSlots = generateTimeSlots(serviceDuration != null ? serviceDuration : 60);

        // 过滤出可用时间段
        for (String slot : allSlots) {
            String[] times = slot.split("-");
            String slotStart = times[0];
            String slotEnd = times[1];

            boolean isAvailable = true;
            for (String booked : bookedTimes) {
                String[] bookedTimesArr = booked.split("-");
                // 检查时间重叠
                if (isTimeOverlap(slotStart, slotEnd, bookedTimesArr[0], bookedTimesArr[1])) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                Map<String, String> slotMap = new HashMap<>();
                slotMap.put("startTime", slotStart);
                slotMap.put("endTime", slotEnd);
                slotMap.put("available", "true");
                availableSlots.add(slotMap);
            }
        }

        return availableSlots;
    }

    @Override
    public List<AppointmentVO> getCalendarView(String startDate, String endDate) {
        List<Appointment> list = appointmentMapper.selectByDateRange(startDate, endDate);
        return convertToVOList(list);
    }

    @Override
    public List<AppointmentVO> getBeauticianSchedule(String date) {
        List<Appointment> list = appointmentMapper.selectScheduleByDate(date);
        return convertToVOList(list);
    }

    @Override
    public boolean checkTimeConflict(Long beauticianId, String date, String startTime, String endTime, Long excludeId) {
        int conflictCount = appointmentMapper.checkTimeConflict(beauticianId, date, startTime, endTime, excludeId);
        return conflictCount > 0;
    }

    @Override
    public Map<String, Object> getTodayOverview() {
        Map<String, Object> result = new HashMap<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 查询今日所有状态的预约
        List<Appointment> todayList = appointmentMapper.selectByDate(today);

        // 统计各状态数量
        int total = todayList.size();
        int pending = 0, confirmed = 0, arrived = 0, inService = 0, completed = 0, cancelled = 0;

        for (Appointment appt : todayList) {
            switch (appt.getStatus()) {
                case STATUS_PENDING: pending++; break;
                case STATUS_CONFIRMED: confirmed++; break;
                case STATUS_ARRIVED: arrived++; break;
                case STATUS_SERVICE: inService++; break;
                case STATUS_COMPLETED: completed++; break;
                case STATUS_CANCELLED: cancelled++; break;
            }
        }

        result.put("success", true);
        result.put("date", today);
        result.put("total", total);
        result.put("pending", pending);
        result.put("confirmed", confirmed);
        result.put("arrived", arrived);
        result.put("inService", inService);
        result.put("completed", completed);
        result.put("cancelled", cancelled);

        return result;
    }

    @Override
    public String generateAppointmentNo() {
        // 格式: AP + yyyyMMdd + 6位序号
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 这里简化处理，实际应该查询数据库获取当日最大序号
        String random = String.format("%06d", new Random().nextInt(999999));
        return "AP" + dateStr + random;
    }

    @Override
    public Map<String, Object> markNoShow(Long id) {
        return updateStatus(id, STATUS_NO_SHOW, "客户失约", null);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 更新预约状态
     */
    private Map<String, Object> updateStatus(Long id, int newStatus, String successMessage, String failMessage) {
        Map<String, Object> result = new HashMap<>();

        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            result.put("success", false);
            result.put("message", "预约不存在");
            return result;
        }

        int currentStatus = appointment.getStatus();

        // 状态流转校验
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            result.put("success", false);
            result.put("message", failMessage != null ? failMessage : "不允许的状态变更");
            return result;
        }

        appointmentMapper.updateStatus(id, newStatus, null);

        result.put("success", true);
        result.put("message", successMessage);
        return result;
    }

    /**
     * 检查状态流转是否合法
     */
    private boolean isValidStatusTransition(int current, int target) {
        // 待确认 -> 已确认、已取消
        if (current == STATUS_PENDING) {
            return target == STATUS_CONFIRMED || target == STATUS_CANCELLED;
        }
        // 已确认 -> 已到店、服务中、已取消
        if (current == STATUS_CONFIRMED) {
            return target == STATUS_ARRIVED || target == STATUS_SERVICE || target == STATUS_CANCELLED;
        }
        // 已到店 -> 服务中、已取消、失约
        if (current == STATUS_ARRIVED) {
            return target == STATUS_SERVICE || target == STATUS_CANCELLED || target == STATUS_NO_SHOW;
        }
        // 服务中 -> 已完成、已取消
        if (current == STATUS_SERVICE) {
            return target == STATUS_COMPLETED || target == STATUS_CANCELLED;
        }
        return false;
    }

    /**
     * 计算结束时间
     */
    private String calculateEndTime(String startTime, int durationMinutes) {
        String[] times = startTime.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);

        int totalMinutes = hour * 60 + minute + durationMinutes;
        int endHour = totalMinutes / 60;
        int endMinute = totalMinutes % 60;

        return String.format("%02d:%02d", endHour, endMinute);
    }

    /**
     * 生成时间段列表
     */
    private List<String> generateTimeSlots(int durationMinutes) {
        List<String> slots = new ArrayList<>();

        String[] startTimes = WORK_START_TIME.split(":");
        String[] endTimes = WORK_END_TIME.split(":");

        int startMinutes = Integer.parseInt(startTimes[0]) * 60 + Integer.parseInt(startTimes[1]);
        int endMinutes = Integer.parseInt(endTimes[0]) * 60 + Integer.parseInt(endTimes[1]);

        while (startMinutes + durationMinutes <= endMinutes) {
            int slotEndMinutes = startMinutes + durationMinutes;
            String slot = String.format("%02d:%02d-%02d:%02d",
                    startMinutes / 60, startMinutes % 60,
                    slotEndMinutes / 60, slotEndMinutes % 60);
            slots.add(slot);

            startMinutes += TIME_SLOT_INTERVAL;
        }

        return slots;
    }

    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        int s1 = timeToMinutes(start1);
        int e1 = timeToMinutes(end1);
        int s2 = timeToMinutes(start2);
        int e2 = timeToMinutes(end2);

        // 两个时间段重叠的条件：其中一个的开始时间小于另一个的结束时间
        return s1 < e2 && s2 < e1;
    }

    /**
     * 将时间字符串转换为分钟数
     */
    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * 将Appointment实体转换为VO
     */
    private AppointmentVO convertToVO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentVO vo = new AppointmentVO();
        vo.setId(appointment.getId());
        vo.setAppointmentNo(appointment.getAppointmentNo());
        vo.setCustomerId(appointment.getCustomerId());
        vo.setCustomerName(appointment.getCustomerName());
        vo.setCustomerPhone(appointment.getCustomerPhone());
        vo.setBeauticianId(appointment.getBeauticianId());
        vo.setBeauticianName(appointment.getBeauticianName());
        vo.setServiceItemId(appointment.getServiceItemId());
        vo.setServiceItemName(appointment.getServiceItemName());
        vo.setAppointmentDate(appointment.getAppointmentDate());
        vo.setStartTime(appointment.getStartTime());
        vo.setEndTime(appointment.getEndTime());
        vo.setDuration(appointment.getDuration());
        vo.setStatus(appointment.getStatus());
        vo.setStatusName(appointment.getStatusName());
        vo.setAmount(appointment.getAmount());
        vo.setRemark(appointment.getRemark());
        vo.setCancelReason(appointment.getCancelReason());
        vo.setCreateTime(appointment.getCreateTime());
        vo.setUpdateTime(appointment.getUpdateTime());

        return vo;
    }

    /**
     * 将Appointment列表转换为VO列表
     */
    private List<AppointmentVO> convertToVOList(List<Appointment> list) {
        List<AppointmentVO> voList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Appointment appointment : list) {
                voList.add(convertToVO(appointment));
            }
        }
        return voList;
    }

    @Override
    public List<AppointmentVO> getAppointmentsByCustomerId(Long customerId) {
        if (customerId == null) {
            return new ArrayList<>();
        }
        AppointmentQueryDTO queryDTO = new AppointmentQueryDTO();
        queryDTO.setCustomerId(customerId);
        queryDTO.setPage(1);
        queryDTO.setLimit(100); // 默认查询100条记录
        Map<String, Object> result = queryAppointmentPage(queryDTO);
        List<AppointmentVO> list = (List<AppointmentVO>) result.get("list");
        return list != null ? list : new ArrayList<>();
    }
}
