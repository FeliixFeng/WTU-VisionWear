package com.wtu.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 工具类：用于获取当前请求中登录用户的信息
 */
public class UserContext {

    private static final String USER_ID_KEY = "userId";

    /**
     * 从 request 中获取当前登录用户的 ID
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute(USER_ID_KEY);
        return userId == null ? null : Long.valueOf(userId.toString());
    }
}
