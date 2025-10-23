package com.fuint.common.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * List 工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ListUtil {

    /**
     * List 集合转为字符串，并在每个元素上加上双引号
     * 例如：{"a","b"}转为 "\"a\",\"b\""
     *
     * @param list      集合对象
     * @param separator 间隔符
     * @return
     */
    public static String listToString(List list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"");
            sb.append(list.get(i));
            sb.append("\"");
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将 List<Map> 转换为 List<DTO>（基于 Spring BeanWrapper）
     *
     * @param mapList
     * @param targetClass
     * @return
     */
    public static <T> List<T> convertMapListToDtoList(List<? extends Map<String, Object>> mapList, Class<T> targetClass) {
        List<T> dtoList = new ArrayList<>();
        if (mapList == null || mapList.isEmpty()) {
            return dtoList;
        }

        for (Map<String, Object> map : mapList) {
            try {
                // 创建 DTO 实例
                T dto = targetClass.getDeclaredConstructor().newInstance();
                // 使用 Spring 的 BeanWrapper 处理属性设置
                BeanWrapper wrapper = new BeanWrapperImpl(dto);
                // 遍历 Map 并设置属性
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String propertyName = entry.getKey();
                    Object value = entry.getValue();
                    // 检查属性是否存在，避免无效属性报错
                    if (wrapper.isWritableProperty(propertyName)) {
                        wrapper.setPropertyValue(propertyName, value);
                    }
                }
                dtoList.add(dto);
            } catch (Exception e) {
                throw new RuntimeException("转换失败：" + e.getMessage(), e);
            }
        }
        return dtoList;
    }
}
