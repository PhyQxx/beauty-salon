package com.beautysalon.controller;

import com.beautysalon.entity.SysLoginLog;
import com.beautysalon.entity.SysOperLog;
import com.beautysalon.service.SysLoginLogService;
import com.beautysalon.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/log")
public class SysLogController {

    @Autowired
    private SysOperLogService operLogService;

    @Autowired
    private SysLoginLogService loginLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/oper")
    public Map<String, Object> queryOperLog(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        Map<String, Object> params = new HashMap<>();
        params.put("module", module);
        params.put("operatorName", operatorName);
        params.put("status", status);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);

        Map<String, Object> result = operLogService.queryPage(params);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result.get("list"));
        response.put("total", result.get("total"));
        return response;
    }

    /**
     * 分页查询登录日志
     */
    @GetMapping("/login")
    public Map<String, Object> queryLoginLog(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("status", status);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);

        Map<String, Object> result = loginLogService.queryPage(params);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result.get("list"));
        response.put("total", result.get("total"));
        return response;
    }

    /**
     * 获取用户最近登录记录
     */
    @GetMapping("/login/recent/{userId}")
    public Map<String, Object> getRecentLogin(@PathVariable Long userId) {
        List<SysLoginLog> logs = loginLogService.getRecentByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", logs);
        return response;
    }
}
