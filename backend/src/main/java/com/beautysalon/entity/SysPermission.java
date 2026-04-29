package com.beautysalon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysPermission {
    private Long id;
    private String code;           // 权限编码
    private String name;           // 权限名称
    private Integer type;          // 类型: 1=菜单 2=按钮 3=接口
    private Long parentId;         // 父权限ID
    private String path;           // 路由/接口路径
    private String icon;          // 图标
    private Integer sortOrder;     // 排序
    private String description;   // 描述
    private Integer status;        // 状态: 0=禁用 1=启用
    private Integer deleted;       // 删除标记
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
