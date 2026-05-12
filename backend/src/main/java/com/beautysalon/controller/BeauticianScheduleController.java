package com.beautysalon.controller;

import com.beautysalon.common.Result;
import com.beautysalon.entity.BeauticianSchedule;
import com.beautysalon.service.BeauticianScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Api(tags = "美容师排班管理")
@RestController
@RequestMapping("/beautician/schedule")
public class BeauticianScheduleController {

    @Autowired
    private BeauticianScheduleService scheduleService;

    @ApiOperation("查询排班列表")
    @GetMapping("/list")
    public Result<List<BeauticianSchedule>> list(
            @RequestParam Long beauticianId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<BeauticianSchedule> list = scheduleService.listByBeauticianAndRange(
                beauticianId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success("查询成功", list);
    }

    @ApiOperation("根据ID获取排班")
    @GetMapping("/{id}")
    public Result<BeauticianSchedule> getById(@PathVariable Long id) {
        BeauticianSchedule schedule = scheduleService.getById(id);
        if (schedule == null || schedule.getDeleted() == 1) {
            return Result.notFound("排班记录不存在");
        }
        return Result.success("查询成功", schedule);
    }

    @ApiOperation("创建排班")
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody BeauticianSchedule schedule) {
        Map<String, Object> result = scheduleService.createSchedule(schedule);
        if (Boolean.FALSE.equals(result.get("success"))) {
            return Result.error(result.get("message").toString());
        }
        return Result.success(result.get("message").toString(), result);
    }

    @ApiOperation("更新排班")
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody BeauticianSchedule schedule) {
        Map<String, Object> result = scheduleService.updateSchedule(id, schedule);
        if (Boolean.FALSE.equals(result.get("success"))) {
            return Result.error(result.get("message").toString());
        }
        return Result.success(result.get("message").toString(), result);
    }

    @ApiOperation("删除排班")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = scheduleService.deleteSchedule(id);
        if (!success) {
            return Result.error("删除失败，记录不存在");
        }
        return Result.success("删除成功");
    }

    @ApiOperation("批量创建排班")
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchCreate(
            @RequestParam Long beauticianId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "09:00:00") String startTime,
            @RequestParam(defaultValue = "18:00:00") String endTime,
            @RequestParam(defaultValue = "1") Integer isWorking,
            @RequestParam(defaultValue = "5") Integer maxAppointments,
            @RequestParam(required = false) String remark) {
        Map<String, Object> result = scheduleService.batchCreateSchedules(
                beauticianId, LocalDate.parse(startDate), LocalDate.parse(endDate),
                startTime, endTime, isWorking, maxAppointments, remark);
        if (Boolean.FALSE.equals(result.get("success"))) {
            return Result.error(result.get("message").toString());
        }
        return Result.success(result.get("message").toString(), result);
    }
}
