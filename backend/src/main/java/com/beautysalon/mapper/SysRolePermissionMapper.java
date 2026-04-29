package com.beautysalon.mapper;

import com.beautysalon.entity.SysRolePermission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysRolePermissionMapper {

    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId}")
    List<SysRolePermission> selectByRoleId(@Param("roleId") Integer roleId);

    @Insert("<script>" +
            "INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES " +
            "<foreach collection='permissionIds' item='pid' separator=','>" +
            "(#{roleId}, #{pid}, NOW())" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("roleId") Integer roleId, @Param("permissionIds") List<Long> permissionIds);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Integer roleId);
}
