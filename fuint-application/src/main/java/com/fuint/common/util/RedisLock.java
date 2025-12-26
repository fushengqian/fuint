package com.fuint.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
public class RedisLock {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁（非阻塞）
     *
     * @param lockKey    锁的 key
     * @param requestId  请求唯一标识（用于解锁校验）
     * @param expireTime 锁自动过期时间（秒）
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 释放锁（Lua 脚本保证原子性）
     */
    public boolean unlock(String lockKey, String requestId) {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "    return redis.call('del', KEYS[1]) " +
                        "else " +
                        "    return 0 " +
                        "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result == 1L;
    }
}
