package com.cmit.payandwithdraw.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SendRequestUtil {
    /**
     * 发送请求到对应的服务器上
     * @param suffix 具体路径
     * @param mediaType 媒体类型
     * @param requestbody 请求报文的body
     * @param httpMethod 请求方式
     */
    public static ResponseEntity<String> toSend(String suffix, MediaType mediaType, Object requestbody, HttpMethod httpMethod) {
        String url = "http://localhost:8081" + suffix;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        HttpEntity requestEntity = new HttpEntity(requestbody, headers);
        //  执行HTTP请求,将返回的结构使用String 类格式化
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, requestEntity, String.class);
        return response;
    }
}
