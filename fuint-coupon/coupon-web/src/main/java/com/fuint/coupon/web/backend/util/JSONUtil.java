package com.fuint.coupon.web.backend.util;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.text.SimpleDateFormat;


/**
 * JsonUtil
 * 提供Json和对象之间的转换。
 *
 */
public class JSONUtil {

    /**
     * 把对象转换成Json字符串。
     * @param obj 需要转换的对象。
     * @return 转换好的字符串。如果出错会抛出RuntimeException
     */
    public static String toJSonString(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        try {
            JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
            gen.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.writeValue(gen, obj);
            gen.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    /**
     * 把Json字符串转换成对象
     * @param <T> 所要转换的对象类型
     * @param json Json字符串
     * @param clazz 转换对象有类型
     * @return 转换好的对象，如果出错会抛出RuntimeException
     */
    public static <T> T toObject(String json,Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            t = mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    /**
     * 把对象转换成Json字符串。
     * @param obj 需要转换的对象。
     * @return 转换好的字符串。如果出错会抛出RuntimeException
     */
    public static String toJSonStringForSimpleDate(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        try {
            JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
            gen.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            mapper.writeValue(gen, obj);
            gen.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    /**
     * 把Json字符串转换成对象
     * @param <T> 所要转换的对象类型
     * @param json Json字符串
     * @param clazz 转换对象有类型
     * @return 转换好的对象，如果出错会抛出RuntimeException
     */
    public static <T> T toObjectForSimpleDate(String json,Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            t = mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }
}
