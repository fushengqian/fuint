package com.fuint.application.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * Created by gang.wang on 2017/5/22.
 */
public class DateUtil {
    public static final String PATTERN_ISO_DATE = "yyyy-MM-dd";
    public static final String PATTERN_ISO_TIME = "HH:mm:ss";
    public static final String PATTERN_ISO_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_ISO_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_SIMPLE_DATE = "yyyyMMdd";
    public static final String PATTERN_SIMPLE_TIME = "HHmmss";
    public static final String PATTERN_SIMPLE_DATETIME = "yyyyMMddHHmmss";
    public static final String PATTERN_SIMPLE_FULL = "yyyyMMddHHmmssSSS";
    public static final String[] PATTERNS = new String[]{"yyyy-MM-dd", "HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMdd", "HHmmss", "yyyyMMddHHmmss", "yyyyMMddHHmmssSSS"};

    public DateUtil() {
    }

    public static String formatDate(Date date, String pattern) {
        if(StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        return DateFormatUtils.format(date, pattern);
    }

    public static String formatDate(Calendar calendar, String pattern) {
        if(StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        return DateFormatUtils.format(calendar, pattern);
    }

    public static Date parseDate(String strDate) throws ParseException {
        return StringUtils.isEmpty(strDate)?null: DateUtils.parseDate(strDate, PATTERNS);
    }

    public static Date parseDate(String strDate, String... patterns) throws ParseException {
        return StringUtils.isEmpty(strDate)?null:DateUtils.parseDate(strDate, patterns);
    }

    public static String getNow(String pattern) {
        return formatDate((Calendar)Calendar.getInstance(), pattern);
    }

    public static BigDecimal getDiffDays(Date startDate, Date endDate) {
        long time = endDate.getTime() - startDate.getTime();
        return new BigDecimal(time / 86400000L);
    }

    public static Date addDate(Date date, int days) {
        return DateUtils.addDays(date, days);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(1);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(2) + 1;
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(5);
    }

    public static String getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 0);
        calendar.set(5, 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    public static String getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 1);
        calendar.set(5, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / 86400000L;
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static Integer getCurrentYearMonths(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH )+1;
        return Integer.parseInt(String.valueOf(year)+String.valueOf(month));
    }

    public static Date getDateAddHours(Date date,Integer num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, Optional.ofNullable(num).orElse(0));
        return calendar.getTime();
    }

    /**
     * 比较日期（年月日时分秒） 1 date1在date2之后（1 date1大于date2），-1 date1在date2之前（1 date1小于date2），0 date1=date2。
     */

    public static int dateCompare(Date date1, Date date2) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_ISO_DATETIME);
        Date dateFirst = null;
        try {
            dateFirst = dateFormat.parse(dateFormat.format(date1));
            Date dateLast = dateFormat.parse(dateFormat.format(date2));

            if (dateFirst.after(dateLast)) {
                return 1;
            } else if (dateFirst.before(dateLast)) {
                return -1;
            }
            return 0;
        } catch (ParseException e) {
            return -2;
        }
    }

}
