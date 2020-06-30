package com.fuint.coupon.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fuint.cache.api.CacheTemplate;
import com.fuint.util.DateUtil;
import com.fuint.util.JSONUtil;
import com.fuint.util.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 从库操作服务类
 */
@Component
public class SlaveRedisTemplate implements CacheTemplate {

    private static final Logger logger = LoggerFactory.getLogger(SlaveRedisTemplate.class);

    @Resource
    private JedisPool slaveJedisPool;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            return JSONUtil.parseObject(jedis.get(key), clazz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            return JSONUtil.parseArray(jedis.get(key), clazz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> Map<String, List<T>> getMapArray(String key, Class<T> clazz) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            Map<String, JSONArray> maps = this.get(key, Map.class);
            Map<String, List<T>> conventResult = null;
            if (maps != null && maps.size() > 0) {
                conventResult = new HashMap<>();
                Iterator<String> iterator = maps.keySet().iterator();
                String mapKey;
                while (iterator.hasNext()) {
                    mapKey = iterator.next();
                    List<T> arrays = JSONUtil.parseArray(maps.get(mapKey).toJSONString(), clazz);
                    conventResult.put(mapKey, arrays);
                }
            }
            return conventResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            Map<String, JSONObject> maps = this.get(key, Map.class);
            Map<String, T> conventResult = null;
            if (maps != null && maps.size() > 0) {
                conventResult = new HashMap<>();
                Iterator<String> iterator = maps.keySet().iterator();
                String mapKey;
                while (iterator.hasNext()) {
                    mapKey = iterator.next();
                    conventResult.put(mapKey, JSONUtil.parseObject(maps.get(mapKey).toJSONString(), clazz));
                }
            }
            return conventResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public <T> void set(String key, T value, Integer timeout) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            jedis.setex(key, timeout, JSONUtil.toString(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存缓存值
     *
     * @param key
     * @param value
     */
    @Override
    public <T> void set(String key, T value) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            jedis.set(key, JSONUtil.toString(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(String key) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void removeLike(String key) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            Set<String> keys = jedis.keys(key + "*");
            if (!CollectionUtils.isEmpty(keys)) {
                jedis.del(keys.toArray(new String[keys.size()]));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    @Override
    public Long getSequence(String key) {
        Jedis jedis = slaveJedisPool.getResource();
        try {
            int timeout = 3600;//超时时间为1小时
            Long seq = jedis.incr(key);
            jedis.expire(key, timeout);
            //14位日期+4位数字
            StringBuilder builder = new StringBuilder();
            builder.append(DateUtil.getNow(DateUtil.longFormat));
            builder.append(StringUtil.leftFill(String.valueOf(seq), '0', 4));
            return Long.valueOf(builder.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public Boolean lock(String key, Long waitTime, Long timeout, TimeUnit timeUnit) {
        Boolean flag = Boolean.FALSE;
        RLock lock;
        try {
            lock = redissonClient.getLock(key);
            flag = lock.tryLock(waitTime, timeout, timeUnit);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public void unLock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }
}
