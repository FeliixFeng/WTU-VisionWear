package com.wtu.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vision.jwt")
public class JwtProperties {

    //属性值在application.yml里设置，这个类用来读取和封装
    private String secretKey;
    private long ttl; // Access Token 过期时间
    private long refreshTtl; // Refresh Token 过期时间
    private String accessTokenName; // Access Token 在请求头中的名称
    private String refreshTokenName; // Refresh Token 在请求头中的名称
}
