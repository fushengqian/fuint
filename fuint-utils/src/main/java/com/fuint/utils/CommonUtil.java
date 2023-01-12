package com.fuint.utils;

import org.apache.commons.lang.StringUtils;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class CommonUtil {

    /**
     * 格式化指定的日期
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String formatDate(Date date, String formatStr) {
        if (date == null) date = new Date();
        if (StringUtils.isEmpty(formatStr)) formatStr = "yyyy-MM-dd";
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        return dateFormater.format(date);
    }
}
