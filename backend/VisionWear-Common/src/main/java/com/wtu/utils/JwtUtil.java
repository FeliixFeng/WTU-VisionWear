package com.wtu.utils;

import com.wtu.exception.AuthException;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;
public class JwtUtil {
    /**
     * 创建JWT令牌
     * 
     * @param secretKey 密钥
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
        
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //指定过期时间
        long tokenTime = System.currentTimeMillis() + ttlMillis;
        // 将字符串 secretKey 转换为 SecretKey 对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), signatureAlgorithm.getJcaName());

        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(tokenTime))
                .signWith(signatureAlgorithm, secretKeySpec);

        return jwtBuilder.compact();
    }

    /**
     * 解析JWT令牌
     * 
     * @param secretKey 密钥
     * @param token JWT令牌
     * @return 令牌中包含的数据
     * @throws AuthException 如果令牌无效、过期或签名验证失败
     */
    public static Claims parseJwt(String secretKey, String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthException(AuthException.JWT_TOKEN_MISSING);
        }
        
        try {
            // 将字符串 secretKey 转换为 SecretKey 对象
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            return Jwts.parser().setSigningKey(secretKeySpec).parseClaimsJws(token).getBody();
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
}
