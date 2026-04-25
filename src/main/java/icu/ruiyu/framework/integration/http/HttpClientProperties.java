package icu.ruiyu.framework.integration.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HTTP 客户端配置属性
 */
@Data
@ConfigurationProperties(prefix = "framework.http")
public class HttpClientProperties {

    /**
     * 连接超时（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时（毫秒）
     */
    private int readTimeout = 10000;

    /**
     * 连接请求超时（毫秒）
     */
    private int connectionRequestTimeout = 3000;

    /**
     * 最大连接数
     */
    private int maxConnections = 100;

    /**
     * 每个路由最大连接数
     */
    private int maxPerRoute = 20;

    /**
     * 空闲连接存活时间（毫秒）
     */
    private long idleConnectionTime = 30000;

    /**
     * 是否启用重试
     */
    private boolean retryEnabled = false;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;
}
