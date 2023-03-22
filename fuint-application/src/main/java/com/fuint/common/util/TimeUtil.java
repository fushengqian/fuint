package com.fuint.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 时间工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TimeUtil {

    /**
     * 一天、一分钟、一小时对应的秒数
     */
    private static final Long ONE_MINUTE_TO_SECOND = 60L;
    private static final Long ONE_HOUR_TO_SECOND = ONE_MINUTE_TO_SECOND * 60;

    /**
     * 使用LocalDateTime进行格式化 保证多线程安全
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER2 = DateTimeFormatter.ofPattern("MM-dd");

    public static String showTime(Date now, Date targetDate) {
        String showTime = "";
        if (targetDate != null) {
            // 5. 年内判断
            if (targetDate.getYear() == now.getYear()) {
                // 获取秒数差
                long betweenSeconds = (now.getTime() - targetDate.getTime()) / 1000;
                if (betweenSeconds < ONE_MINUTE_TO_SECOND) {
                    // 1. 1分钟内：刚刚
                    showTime = "刚刚";
                } else if (betweenSeconds < ONE_HOUR_TO_SECOND) {
                    // 2. 60分钟内
                    showTime = betweenSeconds / ONE_MINUTE_TO_SECOND + "分钟前";
                } else if (betweenSeconds < ONE_HOUR_TO_SECOND * 24) {
                    // 3. 24小时内：x小时前
                    showTime = betweenSeconds / ONE_HOUR_TO_SECOND + "小时前";
                } else {
                    // 4. >24小时：x月x日  08-1
                    showTime = dateToLocalDateTime(targetDate).format(DATE_TIME_FORMATTER2);
                }
            } else {
                showTime = dateToLocalDateTime(targetDate).format(DATE_TIME_FORMATTER1);
            }
        }
        return showTime;
    }

    /**
     * date转localDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 日期转换为时间戳，时间戳为秒
     *
     * @param day
     * @param format
     * @return
     * @throws ParseException
     */
    public static int date2timeStamp(String day, String format) {
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

    /**
     * 获取过去几天内的日期数组
     * @param intervals      intervals天内
     * @return              日期数组
     */
    public static ArrayList<String> getDays(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = intervals -1; i >= 0; i--) {
            pastDaysList.add(getPastDate((i + 2)));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        String result = format.format(today);
        return result;
    }
}
