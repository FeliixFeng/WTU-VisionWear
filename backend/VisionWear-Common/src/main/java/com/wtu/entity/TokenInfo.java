package com.wtu.entity;

import lombok.Data;

@Data
public class TokenInfo {
    /**
     * 网页授权接口调用凭证，注意：此access_token与基础接口的access_token不同
     */
    private String accessToken;

    /**
     * access_token接口调用凭证超时时间，单位（秒）
     */
    private String expiresIn;

    /**
     * 用户刷新access_token
     */
    private String refreshToken;

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    private String scope;
}