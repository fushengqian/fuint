package com.fuint.coupon.cache;


import com.fuint.util.JSONUtil;
import com.fuint.util.StringUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ehcache 操作模板服务类
 * <p>
 * Created by hanxiaoqiang on 2017/6/7.
 */
@Service
public class EhCacheTemplate {

    private String PROMOTION_CACHE = "promotionCache";

    @Autowired
    private CacheManager cacheManager;

    private Cache getCache() {
        return cacheManager.getCache(PROMOTION_CACHE);
    }

    /**
     * 获取缓存值
     *
     * @param key
     * @param clazz
     * @return T
     */
    public <T> T get(String key, Class<T> clazz) {
        if (StringUtil.isNotBlank(key)) {
            Cache cache = getCache();
            Element element = cache.get(key);
            if (element != null && element.getObjectValue() != null) {
                return JSONUtil.parseObject(element.getObjectValue().toString(), clazz);
            }
        }
        return null;
    }

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @return List<T>
     */
    public <T> List<T> array(String key, Class<T> clazz) {
        if (StringUtil.isNotBlank(key)) {
            Cache cache = getCache();
            Element element = cache.get(key);
            if (element != null && element.getObjectValue() != null) {
                return JSONUtil.parseArray(element.getObjectValue().toString(), clazz);
            }
        }
        return null;
    }

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @param clazz
     * @return
     */
    public <T> List<T> batch(String[] keys, Class<T> clazz) {
        if (keys != null && keys.length > 0) {
            List<String> arrayKey = new ArrayList<>();
            for (String key : keys) {
                if (StringUtil.isNotBlank(key)) {
                    arrayKey.add(key);
                }
            }
            if (CollectionUtils.isNotEmpty(arrayKey)) {
                Cache cache = getCache();
                List<T> result = new ArrayList<>();
                Map<Object, Element> values = cache.getAll(arrayKey);
                if (values != null && CollectionUtils.isNotEmpty(values.keySet())) {
                    values.values().stream().filter(element -> element != null && element.getObjectValue() != null).forEach(element -> {
                        List array = JSONUtil.parseArray(element.getObjectValue().toString(), clazz);
                        result.addAll(array);

                    });
                }
                return result;
            }
        }
        return null;
    }

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @return
     */
    public List<String> batch(String[] keys) {
        if (keys != null && keys.length > 0) {
            List<String> arrayKey = new ArrayList<>();
            for (String key : keys) {
                if (StringUtil.isNotBlank(key)) {
                    arrayKey.add(key);
                }
            }
            if (CollectionUtils.isNotEmpty(arrayKey)) {
                Cache cache = getCache();
                List<String> result = new ArrayList<>();
                Map<Object, Element> values = cache.getAll(arrayKey);
                if (values != null && CollectionUtils.isNotEmpty(values.keySet())) {
                    values.values().stream().filter(element -> element != null && element.getObjectValue() != null).forEach(element -> {
                        result.add(element.getObjectValue().toString());

                    });
                }
                return result;
            }
        }
        return null;
    }

    /**
     * 添加缓存
     *
     * @param key
     * @param object
     */
    public void set(String key, Object object) {
        if (StringUtil.isNotBlank(key)) {
            Cache cache = getCache();
            Element element = new Element(key, JSONUtil.toString(object));
            cache.put(element);
        }
    }

    /**
     * 添加缓存
     *
     * @param key
     * @param object
     */
    public void setArray(String key, Collection object) {
        if (StringUtil.isNotBlank(key)) {
            Cache cache = getCache();
            Element element = new Element(key, JSONUtil.toString(object));
            cache.put(element);
        }
    }

    /**
     * 指定key删除缓存
     */
    public void removeKey(String key) {
        Cache cache = getCache();
        cache.remove(key);
    }

    /**
     * 指定key集合删除缓存
     *
     * @param keys
     */
    public void removeKeys(List<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            Cache cache = getCache();
            cache.removeAll(keys);

        }
    }


    /**
     * 清空缓存
     */
    public void removeAll() {
        Cache cache = getCache();
        cache.removeAll();
    }
}
