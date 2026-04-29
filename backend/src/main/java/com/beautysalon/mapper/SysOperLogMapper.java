package com.beautysalon.mapper;

import com.beautysalon.entity.SysOperLog;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SysOperLogMapper {

    @Insert("INSERT INTO sys_oper_log (module, business_type, method, request_method, operator_type, operator_id, operator_name, " +
            "request_url, request_params, response_params, status, error_msg, ip_address, user_agent, operation_time, duration_ms) " +
            "VALUES (#{module}, #{businessType}, #{method}, #{requestMethod}, #{operatorType}, #{operatorId}, #{operatorName}, " +
            "#{requestUrl}, #{requestParams}, #{responseParams}, #{status}, #{errorMsg}, #{ipAddress}, #{userAgent}, #{operationTime}, #{durationMs})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysOperLog log);

    @Select("<script>" +
            "SELECT * FROM sys_oper_log WHERE 1=1 " +
            "<if test='module != null and module != \"\"'> AND module = #{module} </if>" +
            "<if test='operatorName != null and operatorName != \"\"'> AND operator_name LIKE CONCAT('%', #{operatorName}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='beginTime != null'> AND operation_time &gt;= #{beginTime} </if>" +
            "<if test='endTime != null'> AND operation_time &lt;= #{endTime} </if>" +
            "ORDER BY operation_time DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<SysOperLog> selectPage(Map<String, Object> params);

    @Select("SELECT COUNT(*) FROM sys_oper_log WHERE 1=1 " +
            "<if test='module != null and module != \"\"'> AND module = #{module} </if>" +
            "<if test='operatorName != null and operatorName != \"\"'> AND operator_name LIKE CONCAT('%', #{operatorName}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='beginTime != null'> AND operation_time &gt;= #{beginTime} </if>" +
            "<if test='endTime != null'> AND operation_time &lt;= #{endTime} </if>")
    long countPage(Map<String, Object> params);

    @Delete("DELETE FROM sys_oper_log WHERE operation_time &lt; #{beforeTime}")
    int deleteBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
