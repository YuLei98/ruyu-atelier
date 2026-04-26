package icu.ruiyu.framework.integration.security.config;

import icu.ruiyu.framework.integration.security.auth.JwtAuthenticationTokenFilter;
import icu.ruiyu.framework.integration.security.auth.JwtAuthenticationProvider;
import icu.ruiyu.framework.integration.security.handler.UnauthorizedResponseHandler;
import icu.ruiyu.framework.integration.security.handler.AccessDeniedResponseHandler;
import icu.ruiyu.framework.integration.ratelimit.RateLimiterFilter;
import icu.ruiyu.framework.integration.ratelimit.RateLimiterService;
import icu.ruiyu.framework.integration.ratelimit.handler.RateLimitResponseHandler;
import icu.ruiyu.framework.common.config.RateLimiterProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    @Resource
    private UnauthorizedResponseHandler unauthorizedHandler;

    @Resource
    private AccessDeniedResponseHandler accessDeniedHandler;

    @Resource
    @Lazy
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private RateLimiterService rateLimiterService;

    @Resource
    private RateLimitResponseHandler rateLimitResponseHandler;

    @Resource
    private RateLimiterProperties rateLimiterProperties;

    @Bean
    public RateLimiterFilter rateLimiterFilter() {
        return new RateLimiterFilter(rateLimiterService, rateLimitResponseHandler, rateLimiterProperties);
    }

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
                .csrf(csrf -> csrf.disable())
                // 基于 Token，不需要 Session
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationRegistry -> authorizationRegistry
                        // 静态资源允许无授权访问
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/api/v1/oauth/**").permitAll()
                        // Chrome DevTools 探测、favicon 和错误页
                        .requestMatchers("/.well-known/**", "/favicon.ico", "/error").permitAll()
                        // 登录注册注销允许匿名访问
                        .requestMatchers("/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/logout", "/test/**").permitAll()
                        // Swagger UI 允许匿名访问
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Actuator 端点允许匿名访问（健康检查）
                        .requestMatchers("/actuator/**").permitAll()
                        // 跨域预检请求
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 禁用缓存
                .headers(headersConfigurer -> headersConfigurer.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                // 使用自定义认证 Provider
                .authenticationProvider(jwtAuthenticationProvider())
                // 添加全局限流过滤器（在 JWT 过滤器之前）
                .addFilterBefore(rateLimiterFilter(), UsernamePasswordAuthenticationFilter.class)
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置未授权和拒绝访问的响应处理
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(unauthorizedHandler));
        return httpSecurity.build();
    }
}
