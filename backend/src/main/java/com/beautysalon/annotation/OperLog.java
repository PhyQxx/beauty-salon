package com.beautysalon.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {
    /** 操作模块 */
    String module() default "";

    /** 业务类型: 1=新增 2=修改 3=删除 4=授权 5=登录 6=登出 7=导出 8=导入 */
    int businessType() default 0;
}
