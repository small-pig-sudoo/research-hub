package com.univ.researchhub.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.lang.IllegalArgumentException;

@Component
public class JwtUtils {

    @Value("${jwt.secret:yourSecretKey}") // 从配置文件读密钥，无配置则用默认
    private String secret;

    @Value("${jwt.expiration:3600000}") // token过期时间（毫秒，默认1小时）
    private Long expiration;

    /**
     * 生成签名密钥（基于配置的secret）
     * 确保secret长度至少512位（64个字符以上），否则会抛出异常
     */
    private Key getSigningKey() {
        // 显式使用UTF-8编码，避免系统默认编码导致密钥长度计算错误
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        // 校验密钥长度：HS512算法要求密钥≥512位（64个字符）
        if (secretBytes.length < 64) {
            throw new IllegalArgumentException(
                    "JWT密钥长度必须至少64个字符（512位）！当前长度：" + secretBytes.length + "个字符。" +
                            "请检查application.yml中的jwt.secret配置。"
            );
        }

        // 使用jjwt提供的Keys工具类生成符合HS512算法要求的密钥
        return Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param role 用户角色（如ADMIN/TEACHER）
     */
    public String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString()) // 主题存用户ID
                .claim("role", role) // 自定义载荷存角色
                .setIssuedAt(now) // 签发时间
                .setExpiration(expiryDate) // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 使用生成的密钥和指定算法
                .compact();
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * 校验token是否过期
     * @param token 要校验的token
     * @return true表示过期，false表示未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            Date expirationDate = claims.getExpiration();
            // 比较过期时间和当前时间：过期时间在当前时间之前，说明已过期
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            // 捕获token过期异常，直接返回true
            return true;
        } catch (Exception e) {
            // 其他异常（如token无效、签名错误等），均视为过期/无效
            return true;
        }
    }

    /**
     * 解析token获取claims（私有工具方法，供内部调用）
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 使用统一的签名密钥
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT令牌已过期", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new RuntimeException("JWT令牌格式无效", e);
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new RuntimeException("JWT签名验证失败（令牌可能被篡改）", e);
        } catch (Exception e) {
            throw new RuntimeException("解析JWT令牌失败", e);
        }
    }
}