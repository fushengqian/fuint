package com.fuint.util;

import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * JavaBean转换工具类
 * Created by fsq on 2016/3/23.
 */
public class BeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 将Map的key采用"驼峰命名"
     *
     * @param comMap
     * @return Map<String, Object>
     */
    public static Map<String, Object> mapToHump(Map<String, Object> comMap) {
        Map<String, Object> humpMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : comMap.entrySet()) {
            String key = entry.getKey();
            String[] keyArray = key.split("\\_");
            int length = keyArray.length;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                String tmpKey = keyArray[i];
                if (i == 0) {
                    builder.append(tmpKey.substring(0, 1).toLowerCase()).append(tmpKey.substring(1, tmpKey.length()));
                } else {
                    builder.append(tmpKey.substring(0, 1).toUpperCase()).append(tmpKey.substring(1, tmpKey.length()));
                }
            }

            humpMap.put(builder.toString(), entry.getValue());
        }
        return humpMap;
    }

    /**
     * Map转换为JavaBean(key采用"数据库字段"命名或者"驼峰命名"  )
     *
     * @param map
     * @param type
     * @return T
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> type) {
        T t = null;
        String propertyName = null;
        try {
            // key转为与JavaBean一致的驼峰命名
            map = mapToHump(map);
            t = type.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                propertyName = descriptor.getName();
                if (!propertyName.equals("class") && map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);

                    // 类型校验
                    if (value != null) {
                        String propertyType = descriptor.getPropertyType().getSimpleName();
                        String valueType = value.getClass().getSimpleName();
                        if (!propertyType.equals(valueType)) {
                            value = convertType(propertyType, value);
                        }

                        descriptor.getWriteMethod().invoke(t, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("propertyName=" + propertyName);
            logger.error(e.getMessage(), e);
        }

        return t;
    }

    /**
     * 数组类型Map转换为数组JavaBean
     *
     * @param list
     * @param type
     * @return List<T>
     */
    public static <T> List<T> mapToBean(List<Map<String, Object>> list, Class<T> type) {
        List<T> result = new ArrayList<T>();
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> item : list) {
                T t = mapToBean(item, type);
                if (t != null) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    /**
     * Bean转换为Map
     *
     * @param bean
     * @return Map<String,Object>
     */
    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Object value = descriptor.getReadMethod().invoke(bean, new Object[0]);
                    if (value != null) {
                        map.put(propertyName, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    /**
     * 两个对象之间复制
     *
     * @param sourceObj
     * @param targetType
     * @param <T>
     * @return T
     */
    public static <T, S> T copy(S sourceObj, Class<T> targetType) {
        T targetObj = null;
        try {
            targetObj = targetType.newInstance();
            BeanCopier copier = BeanCopier.create(sourceObj.getClass(), targetType, false);
            copier.copy(sourceObj, targetObj, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return targetObj;
    }

    /**
     * 两个对象列表之间复制
     *
     * @param sourceList
     * @param targetType
     * @param <T>
     * @return List<T>
     */
    public static <T, S> List<T> copy(List<S> sourceList, Class<T> targetType) {
        List<T> result = new ArrayList<T>();
        if (sourceList != null && !sourceList.isEmpty()) {
            for (S sourceObj : sourceList) {
                T targetObj = copy(sourceObj, targetType);
                result.add(targetObj);
            }
        }
        return result;
    }

    /**
     * 将HttpServletRequest参数转换为JavaBean对象
     *
     * @param request
     * @param targetType
     * @param <T>
     * @return T
     */
    public static <T> T getRequestParams(HttpServletRequest request, Class<T> targetType) {
        Map<String, Object> params = new HashMap<String, Object>();
        Enumeration namesEnum = request.getParameterNames();
        while (namesEnum.hasMoreElements()) {
            String key = (String) namesEnum.nextElement();
            String value = request.getParameter(key);
            params.put(key, value);
        }
        return mapToBean(params, targetType);
    }

    /**
     * 数据类型转换
     *
     * @param propertyType
     * @param value
     * @return Object
     * @throws ParseException
     */
    private static Object convertType(String propertyType, Object value) throws ParseException {
        Object obj = null;
        if (propertyType.equals("Date")) {
            // 日期类型
            obj = DateUtil.parseDateNoTime(value.toString());
        } else if (propertyType.equals("BigDecimal")) {
            // 浮点类型
            obj = new BigDecimal(String.valueOf(value));
        } else if (propertyType.equals("Integer")) {
            // 整型
            obj = new Integer(String.valueOf(value));
        } else if (propertyType.equals("Long")) {
            // Long型
            obj = new Long(String.valueOf(value));
        } else if (propertyType.equals("BigInteger")) {
            // 长整型
            obj = new BigInteger(String.valueOf(value));
        } else {
            obj = value;
        }

        return obj;
    }
}
