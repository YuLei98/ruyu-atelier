package icu.ruiyu.framework.integration.ratelimit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.common.CommonResult;
import icu.ruiyu.framework.common.config.RateLimiterProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 限流响应处理
 * 当请求被限流时返回统一的错误响应
 */
@Slf4j
@Component
public class RateLimitResponseHandler {

    @Resource
    private ObjectMapper objectMapper;

    public void handle(HttpServletRequest request, HttpServletResponse response,
                       RateLimiterProperties.Global config) {
        try {
            response.setStatus(config.getResponseStatus());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            CommonResult<String> result = CommonResult.error(
                    config.getResponseStatus(),
                    config.getResponseMessage()
            );

            objectMapper.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            log.error("Failed to write rate limit response", e);
        }
    }
}
