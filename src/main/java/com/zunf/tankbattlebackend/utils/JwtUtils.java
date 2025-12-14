package com.zunf.tankbattlebackend.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // JWT密钥
    private static final String SECRET_KEY = "tank_battle_backend_secret_key_2025";
    
    // Token有效期7天
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;


    // 生成token
    public static String generateToken(Long userId) {
        DateTime now = DateTime.now();
        DateTime expireTime = now.offsetNew(DateField.SECOND, (int) JWT_TOKEN_VALIDITY);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.SUBJECT, userId);
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.EXPIRES_AT, expireTime);
        
        JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
        return JWTUtil.createToken(payload, signer);
    }

    // 验证token
    public static Long validateToken(String token) {
        try {
            JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
            JWT jwt = JWTUtil.parseToken(token);
            if (!jwt.verify(signer)) {
                return null;
            }
            
            // 检查是否过期
            Date expireTime = (Date) jwt.getPayload(JWTPayload.EXPIRES_AT);
            if (expireTime == null || expireTime.before(new Date())) {
                return null;
            }
            return Long.parseLong(jwt.getPayload(JWTPayload.SUBJECT).toString());
        } catch (Exception e) {
            return null;
        }
    }
}