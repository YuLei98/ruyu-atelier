package icu.ruiyu.framework.integration.restclient;

import java.util.Map;

/**
 * REST 客户端服务接口
 */
public interface RestClient {

    /**
     * GET 请求
     *
     * @param url 请求地址
     * @return 响应体字符串
     */
    String get(String url);

    /**
     * GET 请求（带请求头）
     *
     * @param url    请求地址
     * @param headers 请求头
     * @return 响应体字符串
     */
    String get(String url, Map<String, String> headers);

    /**
     * POST 请求
     *
     * @param url    请求地址
     * @param body   请求体
     * @return 响应体字符串
     */
    String post(String url, String body);

    /**
     * POST 请求（带请求头）
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @return 响应体字符串
     */
    String post(String url, String body, Map<String, String> headers);

    /**
     * PUT 请求
     *
     * @param url  请求地址
     * @param body 请求体
     * @return 响应体字符串
     */
    String put(String url, String body);

    /**
     * DELETE 请求
     *
     * @param url 请求地址
     * @return 响应体字符串
     */
    String delete(String url);
}
