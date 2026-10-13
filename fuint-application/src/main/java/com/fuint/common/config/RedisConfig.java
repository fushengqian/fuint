package com.fuint.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
// Spring Framework 6 移除了 CachingConfigurerSupport，改为实现 CachingConfigurer 接口
public class RedisConfig implements CachingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private RedisConnectionFactory redisConnectionFactory;

    private Environment environment;

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
        // Spring Data Redis 3.x 移除了 new Jackson2JsonRedisSerializer(Class) 与 setObjectMapper，
        // 改为构造时传入 ObjectMapper
        return new Jackson2JsonRedisSerializer<>(objectMapper(), Object.class);
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

            // 测试连接 - 增加重试机制
            testRedisConnectionWithRetry(redisTemplate, 3);
            logger.info("Redis连接配置成功");

            return redisTemplate;
        } catch (Exception e) {
            // 打印详细的Redis连接配置信息
            printRedisConfigInfo();
            logger.error("Redis连接配置失败: {}", e.getMessage(), e);

            // 提供更详细的错误信息
            String errorMsg = buildDetailedErrorMessage(e);
            logger.error(errorMsg);

            // 如果是开发环境，可以选择降级处理而不是直接抛出异常
            // 注意：env.profile 配置在 application.properties 中，必须通过 Environment 读取，
            // System.getProperty 取不到（该属性并非 JVM -D 参数），否则生产环境也会被误判为 dev 而静默降级
            String envProfile = environment != null
                ? environment.getProperty("env.profile", "dev")
                : System.getProperty("env.profile", "dev");
            if ("dev".equals(envProfile)) {
                logger.warn("开发环境检测到Redis连接失败，系统将继续运行但部分功能可能受限");
                // 返回一个mock的RedisTemplate或者禁用Redis功能
                return createMockRedisTemplate();
            } else {
                throw new RuntimeException("Redis连接配置失败，请检查Redis服务是否启动和配置是否正确", e);
            }
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

    /**
     * 测试Redis连接（带重试机制）
     */
    private void testRedisConnectionWithRetry(RedisTemplate<String, Object> redisTemplate, int maxRetries) {
        Exception lastException = null;

        for (int i = 1; i <= maxRetries; i++) {
            try {
                logger.info("第{}次尝试连接Redis...", i);
                redisTemplate.getConnectionFactory().getConnection().ping();
                logger.info("Redis连接测试成功");
                return; // 成功则返回
            } catch (Exception e) {
                lastException = e;
                logger.warn("第{}次Redis连接尝试失败: {}", i, e.getMessage());
                if (i < maxRetries) {
                    try {
                        Thread.sleep(1000 * i); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("连接重试被中断", ie);
                    }
                }
            }
        }

        // 所有重试都失败
        throw new RuntimeException("Redis连接重试" + maxRetries + "次后仍然失败", lastException);
    }

    /**
     * 构建详细的错误信息
     */
    private String buildDetailedErrorMessage(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Redis连接详细错误信息 ===\n");

        // 基础错误信息
        sb.append("错误类型: ").append(e.getClass().getSimpleName()).append("\n");
        sb.append("错误消息: ").append(e.getMessage()).append("\n");

        // 常见问题诊断
        sb.append("\n可能的原因:\n");
        sb.append("1. Redis服务未启动或端口被占用\n");
        sb.append("2. Redis配置的主机地址或端口不正确\n");
        sb.append("3. Redis密码配置错误\n");
        sb.append("4. 网络连接问题或防火墙阻止\n");
        sb.append("5. Redis连接超时设置过短\n");

        sb.append("\n建议解决方案:\n");
        sb.append("1. 检查Redis服务状态: net start redis 或 redis-server\n");
        sb.append("2. 验证端口是否监听: netstat -an | findstr 6379\n");
        sb.append("3. 检查配置文件中的Redis密码是否正确\n");
        sb.append("4. 尝试telnet 127.0.0.1 6379测试连接\n");
        sb.append("===============================\n");

        return sb.toString();
    }

    /**
     * 创建Mock的RedisTemplate用于开发环境降级
     */
    private RedisTemplate<String, Object> createMockRedisTemplate() {
        logger.warn("创建降级RedisTemplate，Redis相关功能调用时将报错");

        RedisTemplate<String, Object> mockTemplate = new RedisTemplate<>();
        // 必须保留连接工厂：Spring Data Redis 的 redisKeyValueAdapter 等组件会通过
        // redisTemplate.getConnectionFactory() 获取连接工厂，为 null 会导致容器启动失败
        mockTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置基本序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = jackson2JsonRedisSerializer();
        mockTemplate.setKeySerializer(stringRedisSerializer);
        mockTemplate.setHashKeySerializer(stringRedisSerializer);
        mockTemplate.setDefaultSerializer(jacksonSerializer);

        return mockTemplate;
    }
}
