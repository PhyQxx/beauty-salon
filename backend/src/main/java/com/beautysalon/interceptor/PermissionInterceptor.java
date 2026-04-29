package com.beautysalon.interceptor;

import com.beautysalon.common.JwtUtil;
import com.beautysalon.entity.SysUser;
import com.beautysalon.service.SysPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 权限拦截器
 * 验证用户角色是否有访问当前接口的权限
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // 不需要权限校验的路径
    private static final Set<String> WHITE_LIST = new java.util.HashSet<>(java.util.Arrays.asList(
            "/api/sys/user/login",
            "/api/sys/user/logout"
    ));

    // 特殊路径映射（URI -> 权限编码），不走常规 action 推导
    private static final Map<String, String> SPECIFIC_PERMISSIONS = new java.util.HashMap<>();
    static {
        SPECIFIC_PERMISSIONS.put("/api/sys/user/login", "sys:user:login");
        SPECIFIC_PERMISSIONS.put("/api/sys/user/logout", "sys:user:logout");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 白名单直接放行
        for (String white : WHITE_LIST) {
            if (uri.startsWith(white)) {
                return true;
            }
        }

        // 非 Controller 请求直接放行
        if (!uri.startsWith("/api/")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "未登录或Token无效");
            return false;
        }

        try {
            // 特殊路径直接放行（登录/登出等）
            String specificPerm = SPECIFIC_PERMISSIONS.get(uri);
            if (specificPerm != null) {
                // 管理员拥有所有权限
                Map<String, Object> claims = jwtUtil.parseToken(token.replace("Bearer ", ""));
                Integer role = (Integer) claims.get("role");
                if (role != null && role == 1) {
                    return true;
                }
                boolean hasPermission = permissionService.hasPermission(role, specificPerm);
                if (!hasPermission) {
                    sendForbidden(response, "权限不足");
                    return false;
                }
                return true;
            }

            // 解析 token 获取用户信息
            Map<String, Object> claims = jwtUtil.parseToken(token.replace("Bearer ", ""));
            if (claims == null) {
                sendUnauthorized(response, "Token解析失败");
                return false;
            }

            Integer role = (Integer) claims.get("role");
            String method = request.getMethod();

            // 提取基础路径：去掉 /api/ 前缀，去掉数字 ID
            // /api/sys/user/123 -> sys/user/123 -> sys/user
            String baseUri = uri.replace("/api/", "").replaceAll("/\\d+", "");
            // 去掉末尾可能存在的斜杠
            while (baseUri.endsWith("/")) {
                baseUri = baseUri.substring(0, baseUri.length() - 1);
            }

            // 推导操作类型
            String action;
            switch (method) {
                case "GET":
                    action = "list";
                    break;
                case "POST":
                    action = "create";
                    break;
                case "PUT":
                    action = "update";
                    break;
                case "DELETE":
                    action = "delete";
                    break;
                default:
                    action = "";
                    break;
            }

            // sys/user + list -> sys:user:list
            String permissionCode = baseUri.replace("/", ":") + ":" + action;

            // 管理员(role=1)拥有所有权限
            if (role != null && role == 1) {
                return true;
            }

            // 校验权限
            boolean hasPermission = permissionService.hasPermission(role, permissionCode);
            if (!hasPermission) {
                log.warn("权限不足: role={}, permission={}, uri={}", role, permissionCode, uri);
                sendForbidden(response, "权限不足，无法执行该操作");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("权限校验异常: {}", e.getMessage());
            sendUnauthorized(response, "权限校验异常");
            return false;
        }
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
