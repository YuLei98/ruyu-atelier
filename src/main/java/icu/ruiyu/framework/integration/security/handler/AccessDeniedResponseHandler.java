package icu.ruiyu.framework.integration.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 授权失败响应处理，返回 403 JSON
 */
@Slf4j
@Component
public class AccessDeniedResponseHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Access denied: {}", accessDeniedException.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println("禁止访问");
        response.getWriter().flush();
    }
}