package api.announcement.services;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis에 값을 저장
    public void putValue(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutInSeconds));
    }

    public void putValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
