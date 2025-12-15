package com.zunf.tankbattlebackend.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {

    // JWT密钥
    private static final String SECRET_KEY = "tank_battle_backend_secret_key_2025";
    
    // Token有效期7天
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;


    /**
     * 生成token
     *
     * @param userId 用户id
     * @return  token
     */
    public static String generateToken(Long userId) {
        long nowMs = System.currentTimeMillis();
        long expMs = nowMs + JWT_TOKEN_VALIDITY * 1000;

        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.SUBJECT, userId);
        payload.put(JWTPayload.ISSUED_AT, nowMs);
        payload.put(JWTPayload.EXPIRES_AT, expMs);

        JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
        return JWTUtil.createToken(payload, signer);
    }


    /**
     * 验证token
     *
     * @param token  token
     * @return  用户id
     */
    public static Long validateToken(String token) {
        try {
            JWTSigner signer = JWTSignerUtil.hs256(SECRET_KEY.getBytes());
            JWT jwt = JWTUtil.parseToken(token);
            if (!jwt.verify(signer)) {
                return null;
            }

            Object expObj = jwt.getPayload(JWTPayload.EXPIRES_AT);
            if (!(expObj instanceof Number)) {
                return null;
            }
            long expMs = ((Number) expObj).longValue();
            if (expMs <= System.currentTimeMillis()) {
                return null;
            }

            Object sub = jwt.getPayload(JWTPayload.SUBJECT);
            return sub == null ? null : Long.parseLong(sub.toString());
        } catch (Exception e) {
            log.error("validateToken error", e);
            return null;
        }
    }
}