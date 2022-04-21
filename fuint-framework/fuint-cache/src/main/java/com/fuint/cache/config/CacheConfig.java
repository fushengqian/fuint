package com.fuint.cache.config;

import com.fuint.cache.redis.RedissonFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis;


import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Configuration
public class CacheConfig {

    @Autowired
    private Environment env;

    @Bean
    public JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(env.getProperty("redis.maxTotal")));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(env.getProperty("redis.maxIdle")));
        jedisPoolConfig.setMinIdle(Integer.parseInt(env.getProperty("redis.minIdle")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(env.getProperty("redis.maxWaitMillis")));
        jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(env.getProperty("redis.testOnBorrow")));
        return jedisPoolConfig;
    }

    @Bean(name = "jedisPool")
    public JedisPool createJedisPool() {
        String address = env.getProperty("redis.address");
        int port = Integer.parseInt(env.getProperty("redis.port"));
        int timeout = Integer.parseInt(env.getProperty("redis.timeout"));
        String auth = env.getProperty("redis.auth");

        //20190920 richard add redis 访问密码和timeout时间
        //JedisPool jedisPool = new JedisPool(createJedisPoolConfig(), address, port);
        JedisPool jedisPool;
        if(auth==null || auth.length()==0) {
             jedisPool = new JedisPool(createJedisPoolConfig(), address, port);
        }
        else
        {
             jedisPool = new JedisPool(createJedisPoolConfig(), address, port, timeout, auth);
        }
        return jedisPool;
    }

    @Bean
    public RedissonFactoryBean createRedissonFactoryBean() {
        RedissonFactoryBean redissonFactoryBean = new RedissonFactoryBean();
        List<String> nodeAddresses = new ArrayList<>();
        String nodeAddressesStr = env.getProperty("redis.nodeAddresses");
        String[] nodeAddressesArray = nodeAddressesStr.split(",");
        for (String nodeAddress : nodeAddressesArray) {
            nodeAddresses.add(nodeAddress);
        }
        redissonFactoryBean.setNodeAddresses(nodeAddresses);
        return redissonFactoryBean;
    }
}
