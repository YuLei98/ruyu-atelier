package icu.ruiyu.framework.common.config;

import icu.ruiyu.framework.integration.restclient.RestClientProperties;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置 - 使用 Apache HttpClient 连接池
 */
@Configuration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestClientProperties properties) {
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setValidateAfterInactivity(Timeout.ofMilliseconds(500));

        // 构建 HttpClient
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        // 创建支持连接池的请求工厂
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(Timeout.ofMilliseconds(properties.getConnectTimeout()));
        factory.setResponseTimeout(Timeout.ofMilliseconds(properties.getReadTimeout()));

        return new RestTemplate(factory);
    }
}
