package com.beautysalon.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:beauty-salon-jwt-secret-key-2024}")
    private String secret;

    @Value("${jwt.expiration:7200000}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    /**
     * 生成 Access Token
     */
    public String generateToken(Long userId, String username, Integer role, Long storeId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("storeId", storeId);
        claims.put("tokenType", "access");
        return createToken(claims, username, expiration);
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(Long userId, String username, Integer role, Long storeId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("storeId", storeId);
        claims.put("tokenType", "refresh");
        return createToken(claims, username, refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expireMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取 Token 类型
     */
    public String getTokenType(String token) {
        try {
            Object tokenType = parseToken(token).get("tokenType");
            return tokenType != null ? tokenType.toString() : "access";
        } catch (Exception e) {
            return "access";
        }
    }

    /**
     * 获取用户名从 Token
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取用户ID从 Token
     */
    public Long getUserIdFromToken(String token) {
        Object userId = parseToken(token).get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 获取角色从 Token
     */
    public Integer getRoleFromToken(String token) {
        Object role = parseToken(token).get("role");
        return role != null ? ((Number) role).intValue() : null;
    }

    /**
     * 获取门店ID从 Token
     */
    public Long getStoreIdFromToken(String token) {
        Object storeId = parseToken(token).get("storeId");
        if (storeId == null) return null;
        if (storeId instanceof Integer) {
            return ((Integer) storeId).longValue();
        }
        return (Long) storeId;
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }
}
