package com.beautysalon.aspect;

import com.beautysalon.entity.SysOperLog;
import com.beautysalon.service.SysOperLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperLogAspect {

    @Autowired
    private SysOperLogService operLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // 切入点：所有 Controller 的 public 方法
    @Pointcut("execution(public * com.beautysalon.controller..*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        HttpServletRequest request = getRequest();

        if (request != null) {
            operLog.setRequestUrl(request.getRequestURI());
            operLog.setRequestMethod(request.getMethod());
            operLog.setIpAddress(getIpAddress(request));
            operLog.setUserAgent(request.getHeader("User-Agent"));
            operLog.setOperationTime(LocalDateTime.now());
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        operLog.setMethod(className + "." + methodName);

        // 从方法上获取 @OperLog 注解
        com.beautysalon.annotation.OperLog annotation = signature.getMethod().getAnnotation(com.beautysalon.annotation.OperLog.class);
        if (annotation != null) {
            operLog.setModule(annotation.module());
            operLog.setBusinessType(annotation.businessType());
        }

        // 获取请求参数
        try {
            Object[] args = point.getArgs();
            String params = objectMapper.writeValueAsString(Arrays.stream(args)
                    .filter(a -> !(a instanceof HttpServletRequest) && !(a instanceof HttpServletResponse))
                    .toArray());
            operLog.setRequestParams(params);
        } catch (Exception e) {
            operLog.setRequestParams("解析失败");
        }

        Object result = null;
        try {
            result = point.proceed();
            operLog.setStatus(1);
            // 响应参数（截取前2000字符避免过大）
            try {
                String respStr = objectMapper.writeValueAsString(result);
                operLog.setResponseParams(respStr.length() > 2000 ? respStr.substring(0, 2000) : respStr);
            } catch (Exception ignored) {}
        } catch (Exception e) {
            operLog.setStatus(0);
            operLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            operLog.setDurationMs((int) (System.currentTimeMillis() - startTime));
            // 异步记录日志
            operLogService.logAsync(operLog);
        }

        return result;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
