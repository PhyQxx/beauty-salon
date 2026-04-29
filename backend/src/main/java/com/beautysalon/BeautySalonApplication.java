package com.beautysalon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 美容沙龙管理系统 - Spring Boot 启动类
 *
 * @author BeautySalon Team
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.beautysalon.mapper")
@EnableAsync
public class BeautySalonApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySalonApplication.class, args);
    }
}
