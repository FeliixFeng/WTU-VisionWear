package com.wtu.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtu.exception.AuthException;
import com.wtu.exception.ExceptionUtils;
import com.wtu.properties.JwtProperties;
import com.wtu.result.Result;
import com.wtu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 检测是否为动态方法，如果不是，直接放行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String requestURI = request.getRequestURI();
        log.debug("拦截请求: {}, 方法: {}", requestURI, request.getMethod());
        
        try {
            // 从请求头获取token
            String token = request.getHeader(jwtProperties.getTokenName());
            ExceptionUtils.requireNonEmpty(token, AuthException.JWT_TOKEN_MISSING);
            
            // 解析JWT
            Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), token);
            
            // 从claims中获取用户ID并存入请求属性中，供后续使用
            Object userId = claims.get("userId");
            if (userId == null) {
                throw new AuthException("令牌中缺少用户ID信息");
            }
            
            request.setAttribute("userId", userId);
            
            // 记录用户信息到日志
            Object userName = claims.get("userName");
            log.debug("用户 [{}] 访问接口: {}", userName, requestURI);
            
            return true;
        } catch (AuthException e) {
            log.warn("JWT验证失败: {}, URI: {}", e.getMessage(), requestURI);
            handleAuthException(response, e);
            return false;
        } catch (Exception e) {
            log.error("JWT拦截器异常: {}, URI: {}", e.getMessage(), requestURI, e);
            handleAuthException(response, new AuthException("认证处理异常，请重新登录", e));
            return false;
        }
    }
    
    /**
     * 处理认证异常，返回JSON格式的错误信息
     */
    private void handleAuthException(HttpServletResponse response, AuthException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Result<?> result = Result.error(e.getMessage());
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}
