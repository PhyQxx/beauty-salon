package com.beautysalon.config;

import com.beautysalon.common.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 认证过滤器：从请求头提取 JWT Token，验证并填充 SecurityContext。
 * 必须在 Spring Security 的 AuthorizationFilter 之前运行。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
                String username = (String) claims.get("username");
                // JJWT 将 JSON 数字解析为 Long，需通过 Number 转换避免 ClassCastException
                Object roleObj = claims.get("role");
                Integer role = roleObj != null ? ((Number) roleObj).intValue() : null;
                Object userIdObj = claims.get("userId");
                Long userId = userIdObj != null ? ((Number) userIdObj).longValue() : null;
                Object storeIdObj = claims.get("storeId");
                Long storeId = storeIdObj != null ? ((Number) storeIdObj).longValue() : null;

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + (role != null && role == 1 ? "ADMIN" : "USER")))
                        );
                authentication.setDetails(new JwtAuthDetails(userId, role, storeId));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 同时设置 request attributes 供 PermissionInterceptor 使用
                request.setAttribute("currentUser", username);
                request.setAttribute("currentUserId", userId);
                request.setAttribute("currentRole", role);
                request.setAttribute("currentStoreId", storeId);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token 过期，返回 401，前端触发无感续期
                SecurityContextHolder.clearContext();
                sendUnauthorized(response, "Token已过期");
                return;
            } catch (Exception e) {
                // Token 无效，不设置 SecurityContext，继续由后续 filter 处理
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 登录/登出/刷新接口不需要 JWT 验证
        String uri = request.getRequestURI();
        return "/api/sys/user/login".equals(uri)
                || "/api/sys/user/logout".equals(uri)
                || "/api/sys/user/refresh".equals(uri);
    }

    /**
     * 认证详情：包含 userId 和 role
     */
    public static class JwtAuthDetails implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Object userId;
        private final Integer role;
        private final Long storeId;

        public JwtAuthDetails(Object userId, Integer role, Long storeId) {
            this.userId = userId;
            this.role = role;
            this.storeId = storeId;
        }

        public Object getUserId() { return userId; }
        public Integer getRole() { return role; }
        public Long getStoreId() { return storeId; }
    }
}
