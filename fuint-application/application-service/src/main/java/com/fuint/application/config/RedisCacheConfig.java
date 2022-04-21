package com.fuint.application.config;

import com.fuint.cache.redis.RedissonFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Configuration
public class RedisCacheConfig {

    @Autowired
    private Environment env;

    @Bean
    public JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(env.getProperty("slave.redis.maxTotal")));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(env.getProperty("slave.redis.maxIdle")));
        jedisPoolConfig.setMinIdle(Integer.parseInt(env.getProperty("slave.redis.minIdle")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(env.getProperty("slave.redis.maxWaitMillis")));
        jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(env.getProperty("slave.redis.testOnBorrow")));
        return jedisPoolConfig;
    }

    @Bean(name = "slaveJedisPool")
    public JedisPool createJedisPool() {
        String address = env.getProperty("slave.redis.address");
        int port = Integer.parseInt(env.getProperty("slave.redis.port"));
        JedisPool jedisPool = new JedisPool(createJedisPoolConfig(), address, port);
        return jedisPool;
    }

    @Bean
    public RedissonFactoryBean createRedissonFactoryBean() {
        RedissonFactoryBean redissonFactoryBean = new RedissonFactoryBean();
        List<String> nodeAddresses = new ArrayList<>();
        String nodeAddressesStr = env.getProperty("slave.redis.nodeAddresses");
        String[] nodeAddressesArray = nodeAddressesStr.split(",");
        for (String nodeAddress : nodeAddressesArray) {
            nodeAddresses.add(nodeAddress);
        }
        redissonFactoryBean.setNodeAddresses(nodeAddresses);
        return redissonFactoryBean;
    }
}
