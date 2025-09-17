package com.wtu.exception;

public class ExceptionUtils {
    /**
     * 抛出业务异常
     */
    public static void throwBizException(String message) {
        throw new BusinessException(message);
    }

    /**
     * 抛出认证异常
     */
    public static void throwAuthException(String message) {
        throw new AuthException(message);
    }

    /**
     * 抛出验证异常
     */
    public static void throwValidationException(String message) {
        throw new ValidationException(message);
    }

    /**
     * 断言对象不为空，否则抛出业务异常
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new BusinessException(message);
        }
        return obj;
    }

    /**
     * 断言条件为真，否则抛出业务异常
     */
    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言字符串不为空，否则抛出业务异常
     */
    public static String requireNonEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new BusinessException(message);
        }
        return str;
    }
}
