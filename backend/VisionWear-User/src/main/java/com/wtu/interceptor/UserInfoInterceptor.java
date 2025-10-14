package com.wtu.interceptor;

import com.wtu.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 * 从Gateway传递的请求头中提取用户信息，并存储到ThreadLocal中
 */
@Slf4j
@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "userId";
    private static final String USER_NAME_HEADER = "userName";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取用户ID和用户名
        String userIdStr = request.getHeader(USER_ID_HEADER);
        String userName = request.getHeader(USER_NAME_HEADER);

        // 将用户信息存储到ThreadLocal中
        if (userIdStr != null) {
            try {
                Long userId = Long.valueOf(userIdStr);
                UserContext.setUserId(userId);
                log.debug("设置当前请求用户ID: {}", userId);
            } catch (NumberFormatException e) {
                log.warn("用户ID格式错误: {}", userIdStr);
            }
        }
        
        if (userName != null) {
            UserContext.setUserName(userName);
            log.debug("设置当前请求用户名: {}", userName);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContext.clear();
        log.debug("清理ThreadLocal用户信息");
    }
}

