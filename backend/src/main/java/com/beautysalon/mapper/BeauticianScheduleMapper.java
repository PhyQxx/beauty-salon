package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.BeauticianSchedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface BeauticianScheduleMapper extends BaseMapper<BeauticianSchedule> {

    List<BeauticianSchedule> selectByBeauticianAndDateRange(
            @Param("beauticianId") Long beauticianId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    BeauticianSchedule selectByBeauticianAndDate(
            @Param("beauticianId") Long beauticianId,
            @Param("workDate") LocalDate workDate);
}
