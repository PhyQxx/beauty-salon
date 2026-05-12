package com.beautysalon.config;

import com.beautysalon.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 统一捕获所有 Controller 层的异常，返回标准化错误响应
 *
 * @author BeautySalon Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常（RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("业务异常: path={}, msg={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Result.error(400, e.getMessage()));
    }

    /**
     * 业务异常（自定义业务异常）
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        String msg = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "参数校验失败";
        log.warn("参数校验异常: {}", msg);
        return ResponseEntity.ok(Result.badRequest(msg));
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        String msg = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "参数绑定失败";
        log.warn("参数绑定异常: {}", msg);
        return ResponseEntity.ok(Result.badRequest(msg));
    }

    /**
     * 缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "缺少请求参数: " + e.getParameterName();
        log.warn("缺少请求参数: {}", e.getParameterName());
        return ResponseEntity.ok(Result.badRequest(msg));
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = "参数类型错误: " + e.getName() + " 应为 " + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型");
        log.warn("参数类型错误: {}", e.getMessage());
        return ResponseEntity.ok(Result.badRequest(msg));
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String msg = "不支持的请求方法: " + e.getMethod();
        log.warn("不支持的请求方法: {}", e.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Result.error(405, msg));
    }

    /**
     * 404 找不到处理器
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandler(NoHandlerFoundException e) {
        String msg = "接口不存在: " + e.getRequestURL();
        log.warn("接口不存在: {}", e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.notFound(msg));
    }

    /**
     * 其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: path={}, msg={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.ok(Result.error("系统内部错误，请稍后重试"));
    }

    /**
     * 业务异常类
     */
    public static class BusinessException extends RuntimeException {
        private final int code;

        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
