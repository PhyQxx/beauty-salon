package com.beautysalon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * 在 Spring Security Filter Chain 之前放行登录/登出接口，
 * 直接跳转到 DispatcherServlet，避免被 Spring Security 的 AuthorizationFilter 拦截。
 */
@Configuration
public class LoginPermitFilter {

    private static final Set<String> WHITE_LIST = Collections.unmodifiableSet(
            new java.util.HashSet<>(Arrays.asList(
                    "/api/sys/user/login",
                    "/api/sys/user/logout"
            ))
    );

    @Autowired
    private DispatcherServlet dispatcherServlet;

    @Bean
    public FilterRegistrationBean<LoginFilter> loginPermitFilterRegistration() {
        FilterRegistrationBean<LoginFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoginFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    public class LoginFilter implements Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String uri = request.getRequestURI();

            if (WHITE_LIST.contains(uri)) {
                dispatcherServlet.service(request, response);
                return;
            }
            chain.doFilter(servletRequest, servletResponse);
        }
    }
}
