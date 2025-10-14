package com.wtu.filters;

import cn.hutool.http.HttpStatus;
import com.wtu.properties.JwtProperties;
import com.wtu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
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
import java.util.List;

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
        //判断是否需要拦截
        if (isExcluded(request.getPath().toString())){
            //属于白名单路径，放行
            return chain.filter(exchange);
        }
        String token = null;
        //获取token
        List<String> headers = request.getHeaders().get(jwtProperties.getTokenName());

        if(headers!=null && !headers.isEmpty()){
            token = headers.get(0);
        }

        log.info("Gateway拦截的token为:{}",token);

        //尝试解析token
        Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), token);
        Long userId = null;
        String userName = null;
        try{
            // 处理userId的类型转换（可能是Integer或Long）
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            }
            userName = (String)claims.get("userName");
        }catch (Exception e){
            //拦截
            log.error("解析token失败: {}", e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(org.springframework.http.HttpStatus.valueOf(HttpStatus.HTTP_UNAUTHORIZED));
            return response.setComplete();
        }

        //传递用户的ID与姓名
        Long finalUserId = userId;
        String finalUserName = userName;
        ServerWebExchange webExchange = exchange.mutate().request(builder -> builder.header("userId", finalUserId.toString())
                        .header("userName",finalUserName))
                        .build();

        //5.放行
        return chain.filter(webExchange);
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
