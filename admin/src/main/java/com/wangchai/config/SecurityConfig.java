package com.wangchai.config;

import com.wangchai.filter.JwtAuthenticationTokenFilter;
import com.wangchai.filter.RequestMethodLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security核心配置（5.7+ 弃用WebSecurityConfigurerAdapter，改用组件式配置）
 * 整合跨域配置，解决 allowCredentials=true 与 allowedOrigins=* 冲突问题
 */
@Configuration
@EnableWebSecurity // 开启Web安全
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启方法级权限控制
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    // 注入新增的请求方法日志过滤器
    @Autowired
    private RequestMethodLogFilter requestMethodLogFilter;


    // ========== 密码加密器 ==========
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ========== 认证管理器（登录认证用） ==========
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ========== 跨域配置源（核心：解决跨域异常） ==========
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 关键修正：用 allowedOriginPatterns 替代 allowedOrigin，兼容 allowCredentials=true
        config.addAllowedOriginPattern("*");
        // 允许携带Cookie/Token等凭证（前后端分离鉴权必备）
        config.setAllowCredentials(true);
        // 允许所有HTTP请求方法（GET/POST/PUT/DELETE等）
        config.addAllowedMethod("*");
        // 允许所有请求头（如Token、Content-Type、Authorization等）
        config.addAllowedHeader("*");
        // 预检请求缓存时间（秒）：减少OPTIONS请求次数，提升性能
        config.setMaxAge(3600L);

        // 配置跨域规则生效的路径（所有接口）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ========== 核心安全过滤链配置 ==========
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启CORS（复用上面的跨域配置源）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. 关闭CSRF（前后端分离+无状态session，无需CSRF）
                .csrf(AbstractHttpConfigurer::disable)
                // 3. 无状态Session（JWT鉴权场景必备）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 权限规则配置（保持原有逻辑）
                .authorizeHttpRequests(auth -> auth
                        // 示例：放行登录接口，其他接口匿名访问（根据业务调整）
                                // 只放行登录接口匿名访问
                                .requestMatchers("/login", "/userInfo/forgetPassword", "userInfo/register").anonymous()
                        // 测试01——测试视图与直接查询
//                                .requestMatchers("/employee/EmployeeAgeDistData").anonymous()
                        // 测试02——虚拟列与直接查询
//                                .requestMatchers("/salary/SalaryDeptGapData").anonymous()
                                // 其他所有接口都需要认证
                                .anyRequest().authenticated()
//                        .anyRequest().anonymous()
                )
                .logout(logout -> logout.disable());


        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(requestMethodLogFilter, JwtAuthenticationTokenFilter.class);

        return http.build();
    }
}