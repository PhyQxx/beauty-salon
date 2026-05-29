package com.beautysalon.common;

import com.beautysalon.config.JwtAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供获取当前登录用户信息、门店信息等工具方法
 *
 * @author BeautySalon Team
 */
public class SecurityUtils {

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthDetails) {
            JwtAuthenticationFilter.JwtAuthDetails details = (JwtAuthenticationFilter.JwtAuthDetails) authentication.getDetails();
            Object userId = details.getUserId();
            return userId instanceof Number ? ((Number) userId).longValue() : null;
        }
        return null;
    }

    /**
     * 获取当前门店ID
     */
    public static Long getCurrentStoreId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthDetails) {
            JwtAuthenticationFilter.JwtAuthDetails details = (JwtAuthenticationFilter.JwtAuthDetails) authentication.getDetails();
            return details.getStoreId();
        }
        return null;
    }

    /**
     * 是否为超级管理员
     * 角色码 1 通常代表超级管理员
     */
    public static boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthDetails) {
            JwtAuthenticationFilter.JwtAuthDetails details = (JwtAuthenticationFilter.JwtAuthDetails) authentication.getDetails();
            return details.getRole() != null && details.getRole() == 1;
        }
        return false;
    }
}
