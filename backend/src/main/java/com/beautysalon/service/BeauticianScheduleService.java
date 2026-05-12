package com.beautysalon.service;

import com.beautysalon.entity.BeauticianSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BeauticianScheduleService {

    Map<String, Object> createSchedule(BeauticianSchedule schedule);

    Map<String, Object> updateSchedule(Long id, BeauticianSchedule schedule);

    boolean deleteSchedule(Long id);

    BeauticianSchedule getById(Long id);

    List<BeauticianSchedule> listByBeauticianAndRange(Long beauticianId, LocalDate startDate, LocalDate endDate);

    Map<String, Object> batchCreateSchedules(Long beauticianId, LocalDate startDate, LocalDate endDate,
                                              String startTime, String endTime, Integer isWorking,
                                              Integer maxAppointments, String remark);
}
