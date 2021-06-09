package com.fuint.application.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

//没有用到 richard 0827
public class RedisUtil {
    @Resource
    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    //添加
    public void set(String key, String value){
        Jedis jedis = this.jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    //添加，带超时时间
    public void setex(String key, int seconds, String value){
        Jedis jedis = this.jedisPool.getResource();
        jedis.setex(key, seconds, value);
        jedis.close();
    }

    //获取
    public String get(String key){
        Jedis jedis = this.jedisPool.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    //查看某个键是否存在
    public boolean exists(String key){
        Jedis jedis = this.jedisPool.getResource();
        Boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }

    //查看超时时间
    public Long ttl(String key){
        Jedis jedis = this.jedisPool.getResource();
        Long ttl = jedis.ttl(key);
        jedis.close();
        return ttl;
    }

    //删除
    public void del(String key){
        Jedis jedis = this.jedisPool.getResource();
        jedis.del(key);
        jedis.close();
    }

}
