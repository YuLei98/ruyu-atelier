package icu.ruiyu.framework.integration.restclient.impl;

import icu.ruiyu.framework.integration.restclient.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * REST 客户端服务实现
 */
@Slf4j
@Service
public class RestClientImpl implements RestClient {

    private static final String HTTP_CLIENT_LOGGER = "httpClient";
    private static final String SEPARATOR = " | ";
    private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT", "PATCH");

    @Resource
    private RestTemplate restTemplate;

    @Override
    public String get(String url) {
        return get(url, null);
    }

    @Override
    public String get(String url, Map<String, String> headers) {
        return execute(HttpMethod.GET, url, null, headers);
    }

    @Override
    public String post(String url, String body) {
        return post(url, body, null);
    }

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        return execute(HttpMethod.POST, url, body, headers);
    }

    @Override
    public String put(String url, String body) {
        return execute(HttpMethod.PUT, url, body, null);
    }

    @Override
    public String patch(String url, String body) {
        return execute(HttpMethod.PATCH, url, body, null);
    }

    @Override
    public String delete(String url) {
        return delete(url, null);
    }

    public String delete(String url, Map<String, String> headers) {
        return execute(HttpMethod.DELETE, url, null, headers);
    }

    private String execute(HttpMethod method, String url, String body, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

        long startTime = System.currentTimeMillis();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    String.class
            );
            long duration = System.currentTimeMillis() - startTime;

            String responseBody = response.getBody();
            logToHttpClient(method, url, body, headers, response.getStatusCode().value(), duration, responseBody, null);

            return responseBody;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logToHttpClient(method, url, body, headers, 0, duration, null, e.getMessage());
            throw new RuntimeException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    private void logToHttpClient(HttpMethod method, String url, String body, Map<String, String> headers,
                                  int statusCode, long durationMs, String responseBody, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(SEPARATOR)
          .append(url).append(SEPARATOR)
          .append(statusCode > 0 ? statusCode : "FAILED").append(SEPARATOR)
          .append(durationMs).append("ms");

        if (body != null && !body.isEmpty() && METHODS_WITH_BODY.contains(method.name())) {
            sb.append(SEPARATOR).append("req:").append(body);
        }

        if (responseBody != null && !responseBody.isEmpty()) {
            // 响应体截断，避免日志过长
            String truncatedBody = responseBody.length() > 500
                ? responseBody.substring(0, 500) + "...(truncated)"
                : responseBody;
            sb.append(SEPARATOR).append("res:").append(truncatedBody);
        }

        if (error != null) {
            sb.append(SEPARATOR).append("error:").append(error);
        }

        org.slf4j.LoggerFactory.getLogger(HTTP_CLIENT_LOGGER).info(sb.toString());
    }
}
