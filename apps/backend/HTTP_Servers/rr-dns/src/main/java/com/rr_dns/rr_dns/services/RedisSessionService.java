package com.rr_dns.rr_dns.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class RedisSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String SESSION_PREFIX = "auth:session:";

    public RedisSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Salva token com TTL
    public void saveToken(String token, String userId, Duration ttl) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, ttl);
    }

    public boolean isTokenValid(String token) {
        String key = TOKEN_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    public void invalidateToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    // Armazena atributos da sessão
    public void saveSession(String sessionId, Map<String, Object> attributes, Duration ttl) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, attributes, ttl);
    }

    public Map<String, Object> getSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    public void deleteSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
