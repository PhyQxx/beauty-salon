package com.beautysalon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 启动配置安全检查
 * 确保关键安全配置项已正确设置
 */
@Slf4j
@Component
public class StartupConfigCheck implements ApplicationRunner {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${springfox.documentation.enabled:true}")
    private boolean swaggerEnabled;

    @Override
    public void run(ApplicationArguments args) {
        boolean hasWarning = false;

        // 检查 JWT Secret
        if (!StringUtils.hasText(jwtSecret) || jwtSecret.length() < 32) {
            log.warn("============================================================");
            log.warn("【安全警告】JWT Secret 未设置或长度不足32位！");
            log.warn("当前系统使用不安全的默认密钥，生产环境极易被破解。");
            log.warn("请务必通过环境变量 JWT_SECRET 设置强密钥（建议64位以上随机字符串）。");
            log.warn("============================================================");
            hasWarning = true;
        }

        // 检查数据库密码
        if (!StringUtils.hasText(dbPassword)) {
            log.warn("============================================================");
            log.warn("【安全警告】数据库密码未设置！");
            log.warn("请务必通过环境变量 DB_PASSWORD 设置数据库密码。");
            log.warn("============================================================");
            hasWarning = true;
        }

        // 检查 Redis 密码
        if (!StringUtils.hasText(redisPassword)) {
            log.warn("============================================================");
            log.warn("【安全提示】Redis 密码未设置。");
            log.warn("如果 Redis 暴露在外网，建议设置密码。");
            log.warn("可通过环境变量 REDIS_PASSWORD 配置。");
            log.warn("============================================================");
            hasWarning = true;
        }

        // 检查 Swagger
        if (swaggerEnabled) {
            log.info("Swagger 文档已启用，访问地址: /api/swagger-ui/");
            log.info("生产环境建议通过环境变量 SWAGGER_ENABLED=false 禁用。");
        }

        if (!hasWarning) {
            log.info("启动配置安全检查通过，关键安全项已正确配置。");
        }
    }
}
