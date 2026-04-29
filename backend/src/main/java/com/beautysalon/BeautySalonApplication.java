package com.beautysalon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 美容沙龙管理系统 - Spring Boot 启动类
 *
 * @author BeautySalon Team
 */
@SpringBootApplication
@MapperScan("com.beautysalon.mapper")
public class BeautySalonApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySalonApplication.class, args);
    }
}
