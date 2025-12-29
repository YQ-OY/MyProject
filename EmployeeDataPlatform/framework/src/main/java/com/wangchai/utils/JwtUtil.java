package com.wangchai.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    // 有效期24小时，HS256要求密钥≥32字节（你的密钥长度符合）
    public static final Long JWT_TTL = 12 * 60 * 60 * 1000L;
    public static final String JWT_KEY = "wangchai1234567890wangchai1234567890"; // 32字节

    // 生成UUID
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 生成JWT（默认过期时间）
    public static String createJWT(String subject) {
        return createJWT(getUUID(), subject, JWT_TTL);
    }

    // 生成JWT（自定义过期时间）
    public static String createJWT(String subject, Long ttlMillis) {
        return createJWT(getUUID(), subject, ttlMillis);
    }

    // 生成JWT（核心方法，无AES）
    public static String createJWT(String id, String subject, Long ttlMillis) {
        // 1. 仅生成HS256密钥（彻底删除AES相关）
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_KEY.getBytes());
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expDate = new Date(nowMillis + (ttlMillis == null ? JWT_TTL : ttlMillis));

        // 2. 调用getJwtBuilder，传入HS256密钥
        return getJwtBuilder(id, subject, now, expDate, secretKey).compact();
    }

    // 修正getJwtBuilder（第65行，确保密钥是HS256）
    private static JwtBuilder getJwtBuilder(String id, String subject, Date now, Date expDate, SecretKey secretKey) {
        return Jwts.builder()
                .id(id)
                .subject(subject)
                .issuer("wangchai")
                .issuedAt(now)
                .expiration(expDate)
                .signWith(secretKey); // 这里用的是HS256密钥，算法自动匹配
    }

    // 解析JWT（仅用HS256）
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_KEY.getBytes());
        return Jwts.parser()
                .verifyWith(secretKey) // 仅用HS256验证
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    // 【彻底删除】所有AES相关方法，包括generalKey()
}