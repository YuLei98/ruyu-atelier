package icu.ruiyu.framework.integration.http.impl;

import icu.ruiyu.framework.integration.http.HttpClientProperties;
import icu.ruiyu.framework.integration.http.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * HTTP 客户端服务实现
 */
@Slf4j
@Service
public class HttpClientServiceImpl implements HttpClientService {

    @Resource
    private RestTemplate restTemplate;

    private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT", "PATCH");

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

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("HTTP request failed: {} {} - {}", method, url, e.getMessage());
            throw new RuntimeException("HTTP request failed: " + e.getMessage(), e);
        }
    }
}
