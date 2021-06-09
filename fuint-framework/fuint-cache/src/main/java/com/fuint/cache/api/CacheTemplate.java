package com.fuint.cache.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存操作接口
 * Created by fsq on 2017/1/4.
 */
public interface CacheTemplate {
    /**
     * 获取缓存值
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return T
     */
    public <T> T get(String key, Class<T> clazz);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return List<T>
     */
    public <T> List<T> getList(String key, Class<T> clazz);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Map<String, List<T>> getMapArray(String key, Class<T> clazz);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Map<String, T> getMap(String key, Class<T> clazz);

    /**
     * 存储缓存值
     *
     * @param key
     * @param value
     * @param timeout
     * @param <T>
     */
    public <T> void set(String key, T value, Integer timeout);

    /**
     * 储存缓存值
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void set(String key, T value);

    /**
     * 精准删除
     *
     * @param key
     */
    public void remove(String key);

    /**
     * 模糊删除(有性能损耗，不推荐使用)
     *
     * @param key
     */
    public void removeLike(String key);

    /**
     * 判断缓存中是否存在
     *
     * @param key
     * @return Boolean
     */
    public Boolean exists(String key);

    /**
     * 全局序列
     *
     * @param key
     * @return Long
     */
    public Long getSequence(String key);

    /**
     * 基于redis的分布式锁
     *
     * @param key
     * @param waitTime 等待时间
     * @param timeout  超时时间
     * @param timeUnit 单位
     * @return Boolean
     */
    public Boolean lock(String key, Long waitTime, Long timeout, TimeUnit timeUnit);

    /**
     * 释放锁
     *
     * @param key
     */
    public void unLock(String key);
}
