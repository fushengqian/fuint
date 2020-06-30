package com.fuint.coupon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间相关的工具类
 * Created by zach
 */
public class TimeUtils {
    /**
     * 日期转换为时间戳，时间戳为秒
     *
     * @param day
     * @param format
     * @return
     * @throws ParseException
     */
    public static int date2timeStamp(String day, String format) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Integer.parseInt("" + sdf.parse(day).getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int date2timeStamp(Date date){
        return Integer.parseInt("" + date.getTime()/1000);
    }
    /**
     * 时间戳(秒)转换为字符日期
     *
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(int seconds, String format) {
        if (seconds == 0) {
            return null;
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 获取当前系统时间戳(秒)
     *
     * @return
     */
    public static int timeStamp() {
        return Integer.parseInt(System.currentTimeMillis() / 1000 + "");
    }

    /**
     * 判断指定日期是否在起始日期区间内
     *
     * @param startDate
     * @param endDate
     * @param date
     * @return boolean
     */
    public static boolean isSection(Date startDate, Date endDate, Date date) {
        if (startDate.getTime() <= date.getTime() && endDate.getTime() >= date.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static String formatDate(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
