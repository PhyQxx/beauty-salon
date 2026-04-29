package com.beautysalon.service;

import com.beautysalon.entity.SysOperLog;
import com.beautysalon.mapper.SysOperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 异步记录操作日志（不阻塞主线程）
     */
    @Async
    public void logAsync(SysOperLog log) {
        try {
            operLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不影响主业务
            System.err.println("操作日志记录失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询操作日志
     */
    public Map<String, Object> queryPage(Map<String, Object> params) {
        long total = operLogMapper.countPage(params);
        List<SysOperLog> list = operLogMapper.selectPage(params);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    /**
     * 清理过期日志（保留90天）
     */
    public void cleanOldLogs() {
        LocalDateTime before = LocalDateTime.now().minusDays(90);
        operLogMapper.deleteBefore(before);
    }
}
