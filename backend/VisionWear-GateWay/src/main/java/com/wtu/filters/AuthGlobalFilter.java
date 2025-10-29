package com.wtu.filters;

import cn.hutool.http.HttpStatus;
import com.wtu.properties.JwtProperties;
import com.wtu.utils.JwtUtil;
import com.wtu.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/09/24/20:15
 * @Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AntPathMatcher antPathMatcher=new AntPathMatcher();

    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/wxCheck",
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/swagger/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("开始拦截------");
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        log.info("请求路径: {}", path);
        
        // 判断是否需要拦截
        if (isExcluded(path)){
            // 属于白名单路径，放行
            log.info("路径在白名单中，放行: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取 Access Token
        log.info("准备获取 Access Token，请求头名称: {}", jwtProperties.getAccessTokenName());
        String accessToken = getTokenFromRequest(request, jwtProperties.getAccessTokenName());
        log.info("获取到的 Access Token: {}", accessToken != null ? "存在" : "不存在");
        System.out.println(accessToken);
        
        try {
            // 尝试解析 Access Token
            Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), accessToken);
            Long userId = extractUserId(claims);
            String userName = (String) claims.get("userName");
            
            log.info("Access Token 有效，用户ID: {}", userId);
            
            // ✅ 检查 Token 是否为最新（防止被挤下线）
            if (!isLatestToken(userId, accessToken)) {
                log.warn("用户 {} 的 Token 已失效（被其他设备登录挤下线）", userId);
                return kickedOut(exchange);
            }
            
            // Token 有效且是最新的，放行
            return chain.filter(addUserInfoToRequest(exchange, userId, userName));
            
        } catch (ExpiredJwtException e) {
            // Access Token 过期，尝试使用 Refresh Token 刷新
            log.info("Access Token 已过期，尝试刷新...");
            
            String refreshToken = getTokenFromRequest(request, jwtProperties.getRefreshTokenName());
            
            if (refreshToken != null) {
                Long userId = validateRefreshToken(refreshToken);
                
                if (userId != null) {
                    // Refresh Token 有效，生成新的 Access Token
                    String newAccessToken = generateNewAccessToken(userId);
                    
                    // 将新 Token 添加到响应头
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add("New-Access-Token", newAccessToken);
                    
                    log.info("Token 刷新成功，用户ID: {}", userId);
                    
                    // 从 Refresh Token 中提取用户名
                    String userName = getUserNameFromRedis(userId);
                    
                    return chain.filter(addUserInfoToRequest(exchange, userId, userName));
                }
            }
            
            // Refresh Token 无效或不存在，返回 401
            log.warn("Refresh Token 无效或不存在");
            return unauthorized(exchange);
            
        } catch (Exception e) {
            // 其他异常，返回 401
            log.error("Token 验证失败: {}", e.getMessage());
            return unauthorized(exchange);
        }
    }
    
    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(ServerHttpRequest request, String headerName) {
        List<String> headers = request.getHeaders().get(headerName);
        if (headers != null && !headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }
    
    /**
     * 从 Claims 中提取 userId
     */
    private Long extractUserId(Claims claims) {
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("无法提取 userId");
    }
    
    /**
     * 验证 Refresh Token
     */
    private Long validateRefreshToken(String refreshToken) {
        String key = "token:refresh:" + refreshToken;
        Object userId = redisUtil.get(key);
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
    
    /**
     * 生成新的 Access Token
     */
    private String generateNewAccessToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        // 从 Redis 获取用户名
        String userName = getUserNameFromRedis(userId);
        if (userName != null) {
            claims.put("userName", userName);
        }
        
        return JwtUtil.createJwt(
            jwtProperties.getSecretKey(), 
            jwtProperties.getTtl(), 
            claims
        );
    }
    
    /**
     * 从 Redis 获取用户名
     */
    private String getUserNameFromRedis(Long userId) {
        String userKey = "user:login:" + userId;
        Object userName = redisUtil.hGet(userKey, "userName");
        return userName != null ? userName.toString() : null;
    }
    
    /**
     * 将用户信息添加到请求头
     */
    private ServerWebExchange addUserInfoToRequest(ServerWebExchange exchange, Long userId, String userName) {
        return exchange.mutate()
            .request(builder -> {
                builder.header("userId", userId.toString());
                if (userName != null) {
                    builder.header("userName", userName);
                }
            })
            .build();
    }
    
    /**
     * 返回 401 未授权
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.valueOf(HttpStatus.HTTP_UNAUTHORIZED));
        return response.setComplete();
    }
    
    /**
     * 检查 Token 是否为最新（防止被挤下线）
     */
    private boolean isLatestToken(Long userId, String accessToken) {
        try {
            String userKey = "user:login:" + userId;
            Object latestToken = redisUtil.hGet(userKey, "accessToken");
            return latestToken != null && latestToken.toString().equals(accessToken);
        } catch (Exception e) {
            log.error("检查 Token 是否为最新时出错: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 返回 403 被挤下线响应
     */
    private Mono<Void> kickedOut(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
        
        // 设置响应头，告诉前端是"被挤下线"
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.getHeaders().add("X-Kicked-Out", "true");
        
        String body = "{\"code\":0,\"msg\":\"您的账号已在其他设备登录\",\"data\":null}";
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
        );
    }

    public boolean isExcluded(String path){
        for (String excludePath : EXCLUDE_PATHS){
            if (antPathMatcher.match(excludePath,path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
