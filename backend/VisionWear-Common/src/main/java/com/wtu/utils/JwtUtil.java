package com.wtu.utils;

import com.wtu.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类 - 使用 jjwt 0.12.x 版本
 */
public class JwtUtil {
    /**
     * 创建JWT令牌
     * 
     * @param secretKey 密钥字符串（建议至少32个字符以确保安全性）
     * @param ttlMillis 过期时间（毫秒）
     * @param claims 要包含在令牌中的数据
     * @return JWT令牌字符串
     */
    public static String createJwt(String secretKey, long ttlMillis, Map<String, Object> claims) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        
        if (claims == null || claims.isEmpty()) {
            throw new IllegalArgumentException("令牌数据不能为空");
        }
        
        // 生成密钥对象
        SecretKey key = generateKey(secretKey);
        
        // 指定过期时间
        long expirationTime = System.currentTimeMillis() + ttlMillis;
        Date expirationDate = new Date(expirationTime);

        // 构建JWT
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     * 
     * @param secretKey 密钥字符串
     * @param token JWT令牌
     * @return 令牌中包含的数据
     * @throws AuthException 如果令牌无效、过期或签名验证失败
     */
    public static Claims parseJwt(String secretKey, String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthException(AuthException.JWT_TOKEN_MISSING);
        }
        
        try {
            // 生成密钥对象
            SecretKey key = generateKey(secretKey);
            
            // 解析JWT
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthException.JWT_TOKEN_EXPIRED, e);
        } catch (SignatureException e) {
            throw new AuthException(AuthException.JWT_SIGNATURE_FAILED, e);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new AuthException(AuthException.JWT_ILLEGAL_ARGUMENT, e);
        } catch (Exception e) {
            throw new AuthException("令牌验证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从字符串密钥生成SecretKey对象
     * 
     * @param secretKey 密钥字符串
     * @return SecretKey对象
     */
    private static SecretKey generateKey(String secretKey) {
        // 将字符串转换为字节数组
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        
        // 如果密钥长度不足32字节（256位），进行填充
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            // 填充剩余部分
            for (int i = keyBytes.length; i < 32; i++) {
                paddedKey[i] = 0;
            }
            keyBytes = paddedKey;
        }
        
        // 使用HMAC SHA-256算法生成密钥
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
