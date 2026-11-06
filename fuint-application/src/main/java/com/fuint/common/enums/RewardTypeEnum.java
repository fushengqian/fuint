package com.fuint.common.enums;

/**
 * 奖励类型：佣金 / 卡券 / 积分
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum RewardTypeEnum {
    COMMISSION("commission", "佣金"),
    COUPON("coupon", "卡券"),
    POINT("point", "积分");

    private String key;

    private String value;

    RewardTypeEnum(String key, String value) {
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
     * @param key 类型 key
     * @return 名称
     */
    public static String getName(String key) {
        final RewardTypeEnum[] values = RewardTypeEnum.values();
        for (RewardTypeEnum value : values) {
            if (value.getKey().equals(key)) {
                return value.getValue();
            }
        }
        return null;
    }
}
