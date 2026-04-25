package icu.ruiyu.framework.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API 访问日志过滤器，记录所有 HTTP 请求到 api-access.log
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiAccessLogFilter extends OncePerRequestFilter {

    @Value("${api-access-log.trace-id-header:X-Trace-Id}")
    private String traceIdHeader;

    @Value("${api-access-log.max-body-length:1000}")
    private int maxBodyLength;

    @Value("${api-access-log.logger-name:apiAccessLog}")
    private String apiAccessLogName;

    @Value("${api-access-log.exclude-methods:OPTIONS}")
    private List<String> excludeMethods;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String traceId = request.getHeader(traceIdHeader);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            int status = wrappedResponse.getStatus();
            long latency = System.currentTimeMillis() - startTime;

            String method = request.getMethod();
            String path = request.getRequestURI();
            String query = request.getQueryString();
            if (query == null) {
                query = "null";
            } else {
                // 删除 query 中的换行和制表符
                query = query.replaceAll("[\\r\\n\\t]", "");
            }
            String ip = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null) {
                userAgent = "null";
            }

            // 获取 request body
            byte[] requestBytes = wrappedRequest.getContentAsByteArray();
            String requestBody = truncateBody(new String(requestBytes, StandardCharsets.UTF_8));

            // 获取 response body
            byte[] responseBytes = wrappedResponse.getContentAsByteArray();
            String responseBody = truncateBody(new String(responseBytes, StandardCharsets.UTF_8));

            Logger apiAccessLog = org.slf4j.LoggerFactory.getLogger(apiAccessLogName);
            String timestamp = Instant.now().toString();

            // 格式: timestamp|traceId|method|path|query|ip|userAgent|status|latency|requestBody|responseBody
            apiAccessLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", timestamp, traceId, method, path, query, ip, userAgent, status, latency, requestBody, responseBody);

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String truncateBody(String body) {
        if (body == null || body.isEmpty()) {
            return "null";
        }
        // 删除换行、制表符等空白字符
        body = body.replaceAll("[\\r\\n\\t]", " ");
        // 合并多个空格为单个空格
        body = body.replaceAll("\\s+", " ");
        if (body.length() > maxBodyLength) {
            return body.substring(0, maxBodyLength) + "...[truncated]";
        }
        return body;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        return excludeMethods.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList())
                .contains(request.getMethod().toUpperCase());
    }
}