package com.beautysalon.config;

import com.beautysalon.common.JwtUtil;
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

/**
 * JWT 认证过滤器：从请求头提取 JWT Token，验证并填充 SecurityContext。
 * 必须在 Spring Security 的 AuthorizationFilter 之前运行。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
                String username = (String) claims.get("username");
                Integer role = (Integer) claims.get("role");
                Object userId = claims.get("userId");

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + (role != null && role == 1 ? "ADMIN" : "USER")))
                        );
                authentication.setDetails(new JwtAuthDetails(userId, role));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 同时设置 request attributes 供 PermissionInterceptor 使用
                request.setAttribute("currentUser", username);
                request.setAttribute("currentUserId", userId);
                request.setAttribute("currentRole", role);
            } catch (Exception e) {
                // Token 无效或过期，不设置 SecurityContext（由后续 filter 处理 401）
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 登录/登出接口不需要 JWT 验证
        String uri = request.getRequestURI();
        return "/api/sys/user/login".equals(uri) || "/api/sys/user/logout".equals(uri);
    }

    /**
     * 认证详情：包含 userId 和 role
     */
    public static class JwtAuthDetails implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Object userId;
        private final Integer role;

        public JwtAuthDetails(Object userId, Integer role) {
            this.userId = userId;
            this.role = role;
        }

        public Object getUserId() { return userId; }
        public Integer getRole() { return role; }
    }
}
