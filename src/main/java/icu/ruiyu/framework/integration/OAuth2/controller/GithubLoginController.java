package icu.ruiyu.framework.integration.OAuth2.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.integration.OAuth2.config.GithubProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class GithubLoginController {
    @Autowired
    GithubProperties githubProperties;


    /**
     * 登录接口，重定向至github
     *
     * @return 跳转url
     */
    @GetMapping("/authorize")
    public String authorize() {
        String url =githubProperties.getAuthorizeUrl() +
                "?client_id=" + githubProperties.getClientId() +
                "&redirect_uri=" + githubProperties.getRedirectUrl();
        System.out.println("redirect url: " + url);
        return "redirect:" + url;
    }

    /**
     * 回调接口，用户同意授权后，GitHub会将授权码传递给此接口
     * @param code GitHub重定向时附加的授权码，只能用一次
     * @return
     */
    @GetMapping("/oauth/redirect")
    @ResponseBody
    public String redirect(@RequestParam("code") String code) throws Exception {
        System.out.println("code:"+code);
        // 使用code获取token
        String accessToken = this.getAccessToken(code);
        // 使用token获取userInfo
        String userInfo = this.getUserInfo(accessToken);
        return userInfo;
    }


    /**
     * 使用授权码获取token
     * @param code
     * @return
     */
    private String getAccessToken(String code) throws Exception {
        String url = githubProperties.getAccessTokenUrl() +
                "?client_id=" + githubProperties.getClientId() +
                "&client_secret=" + githubProperties.getClientSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
        requestHeaders.add("accept", "application/json");
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // post 请求方式
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseStr = response.getBody();
        // 解析响应json字符串
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseStr);
        String accessToken = jsonNode.get("access_token").asText();
        System.out.println("accessToken:"+accessToken);
        return accessToken;
    }

    /**
     *
     * @param accessToken 使用token获取userInfo
     * @return
     */
    private String getUserInfo(String accessToken) {
        String url = githubProperties.getUserInfoUrl();
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
        requestHeaders.add("accept", "application/json");
        // AccessToken放在请求头中
        requestHeaders.add("Authorization", "token " + accessToken);
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // get请求方式
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String userInfo = response.getBody();
        System.out.println("userInfo:"+userInfo);
        return userInfo;
    }

}