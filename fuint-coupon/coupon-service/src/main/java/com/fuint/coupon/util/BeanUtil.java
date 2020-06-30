package com.fuint.coupon.util;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * Created by gang.wang on 2017/5/22.
 */
public class BeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public BeanUtil() {
    }

    public static Map<String, Object> mapToHump(Map<String, Object> comMap) {
        HashMap humpMap = new HashMap();
        Iterator var2 = comMap.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();
            String key = (String)entry.getKey();
            String[] keyArray = key.split("\\_");
            int length = keyArray.length;
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < length; ++i) {
                String tmpKey = keyArray[i];
                if(i == 0) {
                    builder.append(tmpKey.substring(0, 1).toLowerCase()).append(tmpKey.substring(1, tmpKey.length()));
                } else {
                    builder.append(tmpKey.substring(0, 1).toUpperCase()).append(tmpKey.substring(1, tmpKey.length()));
                }
            }

            humpMap.put(builder.toString(), entry.getValue());
        }

        return humpMap;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> type) {
        Object t = null;
        String propertyName = null;

        try {
            map = mapToHump(map);
            t = type.newInstance();
            BeanInfo e = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] propertyDescriptors = e.getPropertyDescriptors();
            PropertyDescriptor[] var6 = propertyDescriptors;
            int var7 = propertyDescriptors.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                PropertyDescriptor descriptor = var6[var8];
                propertyName = descriptor.getName();
                if(!propertyName.equals("class") && map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);
                    if(value != null) {
                        String propertyType = descriptor.getPropertyType().getSimpleName();
                        String valueType = value.getClass().getSimpleName();
                        if(!propertyType.equals(valueType)) {
                            value = convertType(propertyType, value);
                        }

                        descriptor.getWriteMethod().invoke(t, new Object[]{value});
                    }
                }
            }
        } catch (Exception var13) {
            logger.error("propertyName=" + propertyName);
            logger.error(var13.getMessage(), var13);
        }

        return (T) t;
    }

    public static <T> List<T> mapToBean(List<Map<String, Object>> list, Class<T> type) {
        ArrayList result = new ArrayList();
        if(list != null && !list.isEmpty()) {
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                Map item = (Map)var3.next();
                Object t = mapToBean((Map)item, type);
                if(t != null) {
                    result.add(t);
                }
            }
        }

        return result;
    }

    public static Map<String, Object> beanToMap(Object bean) {
        HashMap map = new HashMap();

        try {
            BeanInfo e = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = e.getPropertyDescriptors();
            PropertyDescriptor[] var4 = propertyDescriptors;
            int var5 = propertyDescriptors.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                PropertyDescriptor descriptor = var4[var6];
                String propertyName = descriptor.getName();
                if(!propertyName.equals("class")) {
                    Object value = descriptor.getReadMethod().invoke(bean, new Object[0]);
                    if(value != null) {
                        map.put(propertyName, value);
                    }
                }
            }
        } catch (Exception var10) {
            logger.error(var10.getMessage(), var10);
        }

        return map;
    }

    public static <T, S> T copy(S sourceObj, Class<T> targetType) {
        Object targetObj = null;

        try {
            targetObj = targetType.newInstance();
            BeanCopier e = BeanCopier.create(sourceObj.getClass(), targetType, false);
            e.copy(sourceObj, targetObj, (Converter)null);
        } catch (Exception var4) {
            logger.error(var4.getMessage(), var4);
        }

        return (T) targetObj;
    }

    public static <T, S> List<T> copy(List<S> sourceList, Class<T> targetType) {
        ArrayList result = new ArrayList();
        if(sourceList != null && !sourceList.isEmpty()) {
            Iterator var3 = sourceList.iterator();

            while(var3.hasNext()) {
                Object sourceObj = var3.next();
                Object targetObj = copy((Object)sourceObj, targetType);
                result.add(targetObj);
            }
        }

        return result;
    }

    /*public static <T> T getRequestParams(HttpServletRequest request, Class<T> targetType) {
        HashMap params = new HashMap();
        Enumeration namesEnum = request.getParameterNames();

        while(namesEnum.hasMoreElements()) {
            String key = (String)namesEnum.nextElement();
            String value = request.getParameter(key);
            params.put(key, value);
        }

        return mapToBean((Map)params, targetType);
    }*/

    private static Object convertType(String propertyType, Object value) throws ParseException {
        Object obj = null;
        if(propertyType.equals("Date")) {
            obj = DateUtil.parseDate(value.toString());
        } else if(propertyType.equals("BigDecimal")) {
            obj = new BigDecimal(String.valueOf(value));
        } else if(propertyType.equals("Integer")) {
            obj = new Integer(String.valueOf(value));
        } else if(propertyType.equals("Long")) {
            obj = new Long(String.valueOf(value));
        } else if(propertyType.equals("BigInteger")) {
            obj = new BigInteger(String.valueOf(value));
        } else {
            obj = value;
        }

        return obj;
    }
}
