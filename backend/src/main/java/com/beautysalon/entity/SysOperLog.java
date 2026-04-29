package com.beautysalon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysOperLog {
    private Long id;
    private String module;           // 操作模块
    private Integer businessType;    // 业务类型: 1=新增 2=修改 3=删除 4=授权 5=登录 6=登出 7=导出 8=导入
    private String method;           // 请求方法
    private String requestMethod;   // 请求方式
    private Integer operatorType;    // 操作人类型: 1=后台用户 2=顾客
    private Long operatorId;        // 操作人ID
    private String operatorName;     // 操作人名称
    private String requestUrl;       // 请求地址
    private String requestParams;    // 请求参数
    private String responseParams;  // 响应参数
    private Integer status;          // 状态: 0=异常 1=正常
    private String errorMsg;         // 错误信息
    private String ipAddress;       // IP地址
    private String userAgent;       // 用户代理
    private LocalDateTime operationTime; // 操作时间
    private Integer durationMs;      // 耗时(毫秒)
}
