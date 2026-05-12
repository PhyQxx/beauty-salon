package com.beautysalon.service.impl;

import com.beautysalon.entity.BeauticianSchedule;
import com.beautysalon.mapper.BeauticianScheduleMapper;
import com.beautysalon.service.BeauticianScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BeauticianScheduleServiceImpl implements BeauticianScheduleService {

    @Autowired
    private BeauticianScheduleMapper scheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createSchedule(BeauticianSchedule schedule) {
        Map<String, Object> result = new HashMap<>();

        BeauticianSchedule existing = scheduleMapper.selectByBeauticianAndDate(
                schedule.getBeauticianId(), schedule.getWorkDate());
        if (existing != null) {
            result.put("success", false);
            result.put("message", "该日期已有排班记录");
            return result;
        }

        schedule.setDeleted(0);
        scheduleMapper.insert(schedule);
        result.put("success", true);
        result.put("message", "排班创建成功");
        result.put("id", schedule.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateSchedule(Long id, BeauticianSchedule schedule) {
        Map<String, Object> result = new HashMap<>();
        BeauticianSchedule existing = scheduleMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            result.put("success", false);
            result.put("message", "排班记录不存在");
            return result;
        }

        schedule.setId(id);
        scheduleMapper.updateById(schedule);
        result.put("success", true);
        result.put("message", "排班更新成功");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long id) {
        BeauticianSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            return false;
        }
        schedule.setDeleted(1);
        return scheduleMapper.updateById(schedule) > 0;
    }

    @Override
    public BeauticianSchedule getById(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public List<BeauticianSchedule> listByBeauticianAndRange(Long beauticianId, LocalDate startDate, LocalDate endDate) {
        return scheduleMapper.selectByBeauticianAndDateRange(beauticianId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchCreateSchedules(Long beauticianId, LocalDate startDate, LocalDate endDate,
                                                     String startTime, String endTime, Integer isWorking,
                                                     Integer maxAppointments, String remark) {
        Map<String, Object> result = new HashMap<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime sTime = LocalTime.parse(startTime, timeFormatter);
        LocalTime eTime = LocalTime.parse(endTime, timeFormatter);

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > 365) {
            result.put("success", false);
            result.put("message", "批量排班时间跨度不能超过365天");
            return result;
        }

        int count = 0;
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            BeauticianSchedule existing = scheduleMapper.selectByBeauticianAndDate(beauticianId, date);
            if (existing != null) {
                continue;
            }

            BeauticianSchedule schedule = new BeauticianSchedule();
            schedule.setBeauticianId(beauticianId);
            schedule.setWorkDate(date);
            schedule.setStartTime(sTime);
            schedule.setEndTime(eTime);
            schedule.setIsWorking(isWorking != null ? isWorking : 1);
            schedule.setMaxAppointments(maxAppointments != null ? maxAppointments : 5);
            schedule.setRemark(remark);
            schedule.setDeleted(0);
            scheduleMapper.insert(schedule);
            count++;
        }

        result.put("success", true);
        result.put("message", "批量排班成功，新增 " + count + " 条记录");
        result.put("count", count);
        return result;
    }
}
