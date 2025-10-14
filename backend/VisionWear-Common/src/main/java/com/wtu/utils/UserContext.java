package com.wtu.utils;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的用户信息
 * 线程安全，每个请求独立
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户名
     * @param userName 用户名
     */
    public static void setUserName(String userName) {
        USER_NAME.set(userName);
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    public static String getCurrentUserName() {
        return USER_NAME.get();
    }

    /**
     * 清理当前线程的用户信息
     * 防止内存泄漏，必须在请求结束时调用
     */
    public static void clear() {
        USER_ID.remove();
        USER_NAME.remove();
    }
}
