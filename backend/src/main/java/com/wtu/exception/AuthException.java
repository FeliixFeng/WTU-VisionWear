package com.wtu.exception;

public class AuthException extends BaseException {
    // JWT相关错误常量
    public static final String JWT_TOKEN_EXPIRED = "令牌已过期，请重新登录";
    public static final String JWT_SIGNATURE_FAILED = "令牌签名验证失败";
    public static final String JWT_ILLEGAL_ARGUMENT = "令牌格式不正确";
    public static final String JWT_TOKEN_MISSING = "请先登录";
    
    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
