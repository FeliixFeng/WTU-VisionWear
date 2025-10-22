package com.wtu.interceptor;

import com.wtu.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 * 从请求头中提取用户ID和用户名，存储到 ThreadLocal 中
 *
 * @author WTU
 */
@Component
@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String userName = request.getHeader("userName");

        if (userId != null) {
            UserContext.setUserId(Long.valueOf(userId));
        }
        if (userName != null) {
            UserContext.setUserName(userName);
        }
        log.debug("UserInfoInterceptor - preHandle: userId={}, userName={}", userId, userName);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        log.debug("UserInfoInterceptor - afterCompletion: UserContext cleared.");
    }
}

