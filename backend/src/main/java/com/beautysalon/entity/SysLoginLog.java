package com.beautysalon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysLoginLog {
    private Long id;
    private Long userId;
    private String username;
    private String ipAddress;
    private String loginLocation;
    private String browser;
    private String os;
    private Integer status;       // 状态: 0=失败 1=成功
    private String message;       // 提示信息
    private LocalDateTime loginTime;
}
