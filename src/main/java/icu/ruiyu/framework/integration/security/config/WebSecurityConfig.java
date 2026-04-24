package icu.ruiyu.framework.integration.security.config;

import icu.ruiyu.framework.integration.security.auth.JwtAuthenticationTokenFilter;
import icu.ruiyu.framework.integration.security.auth.JwtAuthenticationProvider;
import icu.ruiyu.framework.integration.security.handler.UnauthorizedResponseHandler;
import icu.ruiyu.framework.integration.security.handler.AccessDeniedResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UnauthorizedResponseHandler unauthorizedHandler;

    @Autowired
    private AccessDeniedResponseHandler accessDeniedHandler;

    @Autowired
    @Lazy
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 禁用 CSRF
                .csrf(CsrfConfigurer::disable)
                // 基于 Token，不需要 Session
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationRegistry -> authorizationRegistry
                        // 静态资源允许无授权访问
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/authorize", "/oauth/redirect").permitAll()
                        // 登录注册注销允许匿名访问
                        .requestMatchers("/user/login", "/user/register", "/user/logout", "/test/**").permitAll()
                        // 跨域预检请求
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 禁用缓存
                .headers(headersConfigurer -> headersConfigurer.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                // 使用自定义认证 Provider
                .authenticationProvider(jwtAuthenticationProvider())
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置未授权和拒绝访问的响应处理
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(unauthorizedHandler));
        return httpSecurity.build();
    }
}