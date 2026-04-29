package com.beautysalon.mapper;

import com.beautysalon.entity.SysPermission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysPermissionMapper {

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 ORDER BY sort_order")
    List<SysPermission> selectAll();

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND type = 1 ORDER BY sort_order")
    List<SysPermission> selectMenus();

    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0 " +
            "ORDER BY p.sort_order")
    List<SysPermission> selectByRoleId(@Param("roleId") Integer roleId);

    @Select("SELECT p.code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<String> selectCodesByRoleId(@Param("roleId") Integer roleId);

    @Insert("INSERT INTO sys_permission (code, name, type, parent_id, path, icon, sort_order, description, status, deleted, create_time) " +
            "VALUES (#{code}, #{name}, #{type}, #{parentId}, #{path}, #{icon}, #{sortOrder}, #{description}, #{status}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysPermission permission);

    @Update("UPDATE sys_permission SET name=#{name}, path=#{path}, icon=#{icon}, sort_order=#{sortOrder}, description=#{description}, status=#{status}, update_time=NOW() WHERE id=#{id}")
    int update(SysPermission permission);

    @Update("UPDATE sys_permission SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
