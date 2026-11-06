package com.fuint.common.enums;

/**
 * 推荐奖励场景
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum RewardSceneEnum {
    REGISTER("register", "注册奖励"),
    ORDER("order", "交易奖励");

    private String key;

    private String value;

    RewardSceneEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 根据 key 获取名称
     *
     * @param key 场景 key
     * @return 名称
     */
    public static String getName(String key) {
        final RewardSceneEnum[] values = RewardSceneEnum.values();
        for (RewardSceneEnum value : values) {
            if (value.getKey().equals(key)) {
                return value.getValue();
            }
        }
        return null;
    }
}
