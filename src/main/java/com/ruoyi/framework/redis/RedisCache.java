package com.ruoyi.framework.redis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.BoundSetOperations;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;

import org.springframework.stereotype.Component;

/**
 * spring redis 工具类
 *
 * @author ruoyi
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component

public class RedisCache {
//    @Autowired
//    public RedisTemplate redisTemplate;

    TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(30 * 1000);

    {
        timedCache.schedulePrune(5);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        timedCache.put(key, value,0);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        if (timeUnit.equals(TimeUnit.SECONDS)) {
            timedCache.put(key, value, timeout * 1000);
        } else if (timeUnit.equals(TimeUnit.MINUTES)) {
            timedCache.put(key, value, timeout * 1000 * 60);
        } else if (timeUnit.equals(TimeUnit.HOURS)) {
            timedCache.put(key, value, timeout * 1000 * 60 * 60);
        } else if (timeUnit.equals(TimeUnit.DAYS)) {
            timedCache.put(key, value, timeout * 1000 * 60 * 60 * 24);
        } else if (timeUnit.equals(TimeUnit.DAYS)) {
            timedCache.put(key, value, timeout);
        } else {
            System.err.println("timeunit: " + timeUnit);
            throw new RuntimeException("错误的时间类型");
        }

    }


    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return timedCache.containsKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        T value = (T) timedCache.get(key);
        return value;
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        timedCache.remove(key);
        return true;
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public boolean deleteObject(final Collection<String> collection) {

        for (String key : collection) {
            timedCache.remove(key);
        }
        return true;
    }


    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        Collection<String> keys = new ArrayList<>();
        Set<String> strings = timedCache.keySet();
        for (String string : strings) {
            if (string.startsWith(pattern)) {
                keys.add(string);
            }
        }
        return keys;
    }
}
