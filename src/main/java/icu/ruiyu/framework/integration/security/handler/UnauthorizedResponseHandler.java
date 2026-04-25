package icu.ruiyu.framework.integration.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 未认证响应处理，返回 401 JSON
 */
@Slf4j
@Component
public class UnauthorizedResponseHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String path = request.getRequestURI();
        // actuator 端点由 permitAll 放行，匿名请求会触发此 handler，降级为 warn
        if (path.startsWith("/actuator/")) {
            log.warn("Anonymous access to {}: {}", path, authException.getMessage());
            return;
        }
        log.error("Unauthorized access: {}", authException.getMessage(), authException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().print("认证失败");
        response.getWriter().flush();
    }
}