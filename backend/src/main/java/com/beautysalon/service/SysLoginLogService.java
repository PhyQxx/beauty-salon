package com.beautysalon.service;

import com.beautysalon.entity.SysLoginLog;
import com.beautysalon.mapper.SysLoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysLoginLogService {

    @Autowired
    private SysLoginLogMapper loginLogMapper;

    /**
     * 异步记录登录日志
     */
    @Async
    public void logAsync(SysLoginLog log) {
        try {
            loginLogMapper.insert(log);
        } catch (Exception e) {
            System.err.println("登录日志记录失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询登录日志
     */
    public Map<String, Object> queryPage(Map<String, Object> params) {
        long total = loginLogMapper.countPage(params);
        List<SysLoginLog> list = loginLogMapper.selectPage(params);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    /**
     * 获取用户最近登录记录
     */
    public List<SysLoginLog> getRecentByUserId(Long userId) {
        return loginLogMapper.selectRecentByUserId(userId);
    }
}
