/* package com.rr_dns.rr_dns.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class RedisSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String LOGIN_AT_PREFIX = "auth:loginAt:";

    public RedisSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, String userId, Duration ttl) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, ttl);
    }

    public boolean isTokenValid(String token) {
        return redisTemplate.hasKey(TOKEN_PREFIX + token);
    }

    public void invalidateToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    public void saveLoginAt(String token, Long loginAt) {
        String key = LOGIN_AT_PREFIX + token;
        redisTemplate.opsForValue().set(key, loginAt);
    }

    public Long getLoginAt(String token) {
        String key = LOGIN_AT_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null : Long.parseLong(value.toString());
    }

    public void saveSession(String sessionId, Map<String, String> sessionData, Duration ttl) {
        // Salva cada campo no Redis como HASH
        redisTemplate.opsForHash().putAll(sessionId, sessionData);

        // Define TTL da sessão
        if (ttl != null) {
            redisTemplate.expire(sessionId, ttl);
        }
    }
}
 */


package com.rr_dns.rr_dns.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * Stub local de Redis.
 *
 * No ambiente do trabalho final, você pode ligar Redis de verdade.
 * Aqui, para rodar no seu PC sem Docker/Redis, deixamos tudo em no-op.
 */
@Service
public class RedisSessionService {

    public void saveLoginAt(String token, Long time) {
        // No-op: em produção você gravaria no Redis
    }

    public Long getLoginAt(String token) {
        // Para não quebrar, devolve o horário atual
        return System.currentTimeMillis();
    }

    public void saveToken(String jwtToken, String email, Duration ttl) {
        // No-op
    }

    public void saveSession(String sessionId, Map<String, Object> data, Duration ttl) {
        // No-op
    }
}
