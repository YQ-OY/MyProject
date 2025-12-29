package com.wangchai.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 重载方法：读取指定类型的缓存（解决JSON字符串转实体类）
    public <T> T getCacheObject(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        // 情况1：已经是目标类型，直接返回
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        // 情况2：是JSON字符串，反序列化为目标类型
        if (value instanceof String) {
            return JSON.parseObject((String) value, clazz);
        }
        // 情况3：是JSONObject，转成目标类型
        if (value instanceof com.alibaba.fastjson.JSONObject) {
            return ((com.alibaba.fastjson.JSONObject) value).toJavaObject(clazz);
        }
        // 其他情况返回null
        return null;
    }

    // 原有方法（兼容旧调用）
    public Object getCacheObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 存入缓存（确保存对象而非手动转JSON）
    public void setCacheObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 带过期时间的存储
    public void setCacheObject(String key, Object value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }
}