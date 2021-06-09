package com.fuint.application.cache;

/**
 * 缓存key的集中生成策略
 * Created by zach on 2017/1/4.
 */
public class CacheKeyUtil {

    // 系统级缓存前缀
    private static final String PREFIX_SYSTEM = "SYS";
    // 业务级缓存前缀
    private static final String PREFIX_BUSINESS = "BUSI";
    // 表级缓存前缀
    private static final String PREFIX_TABLE = "TABLE";
    // 分布式锁缓存前缀
    private static final String PREFIX_LOCK = "LOCK";
    // 全局序列缓存前缀
    private static final String PREFIX_SEQUENCE = "SEQ";

    /**
     * 系统级缓存key构建
     *
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildSystemKey(String systemName, String... keys) {
        return buildKey(PREFIX_SYSTEM, systemName, keys);
    }

    /**
     * 业务级缓存key构建
     *
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildBusinessKey(String systemName, String... keys) {
        return buildKey(PREFIX_BUSINESS, systemName, keys);
    }

    /**
     * 表级缓存key构建
     *
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildTableKey(String systemName, String... keys) {
        return buildKey(PREFIX_TABLE, systemName, keys);
    }

    /**
     * 分布式锁缓存key构建
     *
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildLockKey(String systemName, String... keys) {
        return buildKey(PREFIX_LOCK, systemName, keys);
    }

    /**
     * 分布式锁缓存key构建
     *
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildSequenceKey(String systemName, String... keys) {
        return buildKey(PREFIX_SEQUENCE, systemName, keys);
    }

    /**
     * 自定义缓存key
     *
     * @param prefix     前缀
     * @param systemName 系统名称
     * @param keys       生成键的字符串数组
     * @return String
     */
    public static String buildKey(String prefix, String systemName, String... keys) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix).append(":").append(systemName);
        for (String key : keys) {
            builder.append("_");
            builder.append(key);
        }
        return builder.toString();
    }
}
