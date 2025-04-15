package com.example.ecommercewebservice.domain.redis;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;

    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }
        return gson.fromJson(jsonValue, clazz);
    }

    public Set<String> getAllKeys() {
        return template.keys("*");
    }

    public <T> void setDataWithTTL(String key, T value, Duration expiredTime) {
        String jsonValue = gson.toJson(value);
        template.opsForValue().set(key, jsonValue);
        template.expire(key, expiredTime.toSeconds(), TimeUnit.SECONDS);
    }

    public void deleteData(String key) {
        template.delete(key);
    }

    public Long getTTL(String key) {
        return template.getExpire(key, TimeUnit.SECONDS);
    }

    public Duration getRemainingTTL(String key) {
        Long seconds = template.getExpire(key, TimeUnit.SECONDS);
        if (seconds == null || seconds < 0) {
            return null;
        }
        return Duration.ofSeconds(seconds);
    }
}