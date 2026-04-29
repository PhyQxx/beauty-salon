package com.beautysalon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysRolePermission {
    private Long id;
    private Integer roleId;
    private Long permissionId;
    private LocalDateTime createTime;
}
