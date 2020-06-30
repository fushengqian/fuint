package com.fuint.coupon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * Created by gang.wang on 2017/5/22.
 */
public class JsonUtil {
    public JsonUtil() {
    }

    public static <T> String toString(T t) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        return toString(t, dateFormat);
    }

    public static <T> String toString(T t, String dateFormat) {
        JSON.DEFFAULT_DATE_FORMAT = dateFormat;
        return JSON.toJSONString(t, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect});
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
}
