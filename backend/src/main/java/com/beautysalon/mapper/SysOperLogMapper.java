package com.beautysalon.mapper;

import com.beautysalon.entity.SysOperLog;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 Mapper 接口
 * 
 * @author BeautySalon Team
 */
@Mapper
public interface SysOperLogMapper {

    @Insert("INSERT INTO sys_oper_log (module, business_type, method, request_method, operator_type, operator_id, operator_name, " +
            "request_url, request_params, response_params, status, error_msg, ip_address, user_agent, operation_time, duration_ms) " +
            "VALUES (#{module}, #{businessType}, #{method}, #{requestMethod}, #{operatorType}, #{operatorId}, #{operatorName}, " +
            "#{requestUrl}, #{requestParams}, #{responseParams}, #{status}, #{errorMsg}, #{ipAddress}, #{userAgent}, #{operationTime}, #{durationMs})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysOperLog log);

    /**
     * 分页查询操作日志
     * 实现在 SysOperLogMapper.xml 中
     */
    List<SysOperLog> selectPage(Map<String, Object> params);

    /**
     * 统计查询日志总数
     * 实现在 SysOperLogMapper.xml 中
     */
    long countPage(Map<String, Object> params);

    @Delete("DELETE FROM sys_oper_log WHERE operation_time < #{beforeTime}")
    int deleteBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
