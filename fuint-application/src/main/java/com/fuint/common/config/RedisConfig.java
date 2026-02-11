package com.fuint.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 配置redis缓存
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
@EnableCaching
@AllArgsConstructor
@EnableRedisHttpSession
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
                Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper());
        return jackson2JsonRedisSerializer;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        try {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setHashKeySerializer(stringRedisSerializer);

            // 测试连接
            redisTemplate.getConnectionFactory().getConnection().ping();
            logger.info("Redis连接配置成功");

            return redisTemplate;
        } catch (Exception e) {
            // 打印详细的Redis连接配置信息
            printRedisConfigInfo();
            logger.error("Redis连接配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("Redis连接配置失败，请检查Redis服务是否启动", e);
        }
    }

    /**
     * 打印Redis连接配置信息
     */
    private void printRedisConfigInfo() {
        try {
            // 获取Redis连接工厂的配置信息
            if (redisConnectionFactory != null) {
                logger.error("=== Redis连接配置信息 ===");
                logger.error("Redis Host: {}", getRedisHost());
                logger.error("Redis Port: {}", getRedisPort());
                logger.error("Redis Database: {}", getRedisDatabase());
                logger.error("Redis Password: {}", getRedisPassword() != null && !getRedisPassword().isEmpty() ? "******" : "(empty)");
                logger.error("Connection Timeout: {}ms", getRedisTimeout());
                logger.error("=========================");
            }
        } catch (Exception ex) {
            logger.error("获取Redis配置信息失败: {}", ex.getMessage());
        }
    }

    /**
     * 获取Redis主机地址
     */
    private String getRedisHost() {
        try {
            return redisConnectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
                ? ((org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) redisConnectionFactory).getHostName()
                : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 获取Redis端口
     */
    private int getRedisPort() {
        try {
            return redisConnectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
                ? ((org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) redisConnectionFactory).getPort()
                : 6379;
        } catch (Exception e) {
            return 6379;
        }
    }

    /**
     * 获取Redis数据库索引
     */
    private int getRedisDatabase() {
        try {
            return redisConnectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
                ? ((org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) redisConnectionFactory).getDatabase()
                : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取Redis密码
     */
    private String getRedisPassword() {
        try {
            return redisConnectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
                ? ((org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) redisConnectionFactory).getPassword()
                : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取连接超时时间
     */
    private long getRedisTimeout() {
        try {
            return redisConnectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
                ? ((org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) redisConnectionFactory).getTimeout()
                : 2000L;
        } catch (Exception e) {
            return 2000L;
        }
    }
}
