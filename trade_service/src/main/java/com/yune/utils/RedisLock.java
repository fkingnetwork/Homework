package com.yune.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisLock implements Ilock {
    private StringRedisTemplate stringRedisTemplate;
    private String Key;


    public RedisLock(StringRedisTemplate stringRedisTemplate, String key) {
        this.stringRedisTemplate = stringRedisTemplate;
        Key = key;
    }


    @Override
    public boolean tryLock(Long timeout) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(Key, Thread.currentThread().getName(), timeout, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(b);
    }

    @Override
    public void unlock() {
        stringRedisTemplate.delete(Key);
    }
}
