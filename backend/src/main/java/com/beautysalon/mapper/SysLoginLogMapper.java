package com.beautysalon.mapper;

import com.beautysalon.entity.SysLoginLog;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface SysLoginLogMapper {

    @Insert("INSERT INTO sys_login_log (user_id, username, ip_address, login_location, browser, os, status, message, login_time) " +
            "VALUES (#{userId}, #{username}, #{ipAddress}, #{loginLocation}, #{browser}, #{os}, #{status}, #{message}, #{loginTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysLoginLog log);

    @Select("<script>" +
            "SELECT * FROM sys_login_log WHERE 1=1 " +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='beginTime != null'> AND login_time &gt;= #{beginTime} </if>" +
            "<if test='endTime != null'> AND login_time &lt;= #{endTime} </if>" +
            "ORDER BY login_time DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<SysLoginLog> selectPage(Map<String, Object> params);

    @Select("SELECT COUNT(*) FROM sys_login_log WHERE 1=1 " +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>")
    long countPage(Map<String, Object> params);

    @Select("SELECT * FROM sys_login_log WHERE user_id = #{userId} ORDER BY login_time DESC LIMIT 10")
    List<SysLoginLog> selectRecentByUserId(@Param("userId") Long userId);
}
