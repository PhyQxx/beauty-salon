package com.beautysalon.interceptor;

import com.beautysalon.common.JwtUtil;
import com.beautysalon.config.JwtAuthenticationFilter;
import com.beautysalon.service.SysPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 权限拦截器：验证用户角色是否有访问当前接口的权限。
 * 认证由 JwtAuthenticationFilter 完成，此处只做权限校验。
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    // 不需要权限校验的路径
    private static final Set<String> WHITE_LIST = Set.of(
            "/api/sys/user/login",
            "/api/sys/user/logout"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 白名单直接放行
        if (WHITE_LIST.contains(uri)) {
            return true;
        }

        // 非 Controller 请求直接放行
        if (!uri.startsWith("/api/")) {
            return true;
        }

        // 获取 SecurityContext 中的认证信息（由 JwtAuthenticationFilter 设置）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            sendUnauthorized(response, "未登录或Token无效");
            return false;
        }

        JwtAuthenticationFilter.JwtAuthDetails details =
                (JwtAuthenticationFilter.JwtAuthDetails) auth.getDetails();
        Integer role = details != null ? details.getRole() : null;

        // 管理员(role=1)拥有所有权限
        if (role != null && role == 1) {
            return true;
        }

        // 推导权限码
        String method = request.getMethod();
        String baseUri = uri.replace("/api/", "").replaceAll("/\\d+$", "");
        String action;
        switch (method) {
            case "GET":    action = "list";   break;
            case "POST":   action = "create"; break;
            case "PUT":    action = "update"; break;
            case "DELETE": action = "delete"; break;
            default:       action = "";
        }
        String permissionCode = baseUri.replace("/", ":") + ":" + action;

        boolean hasPermission = permissionService.hasPermission(role, permissionCode);
        if (!hasPermission) {
            sendForbidden(response, "权限不足，无法执行该操作");
            return false;
        }
        return true;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private void sendForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
