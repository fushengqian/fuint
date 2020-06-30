package com.fuint.coupon.util;

import java.util.List;

/**
 * List 工具类
 * Created by zach on 2017/3/13.
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
}
