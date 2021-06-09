package com.fuint.application.util;

import java.time.LocalTime;
import java.util.Optional;

/**
 * Created by gang.wang on 2017/6/7.
 */
public class QuartzCronUtil {


    /**
     * 字符串time转换为对象
     * @param time
     * @return
     */
    public static LocalTime paseLocalTime(String time){
        return Optional.ofNullable(time).map(n->LocalTime.parse(n)).orElse(null);
    }

    /**
     * 动态生成Quartz Cron表达式
     * @param type
     * @param detail
     * @param time
     * @return
     */
    public static String genQuartzCron(String type,String detail,LocalTime time) {
        String cron = null;
        if(type != null && !"".equals(type) && time != null) {
            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();
            switch (type.toUpperCase()){
                case "DAY":
                    cron = second + " " + minute + " " + hour + " * * ?";
                    break;
                case "WEEK":
                    cron = second + " " + minute + " " + hour + " ? * " + detail;
                    break;
                case "MONTH":
                    cron = second + " " + minute + " " + hour + " " + detail + " * ?" ;
                    break;
                default:
                    break;
            }
        }
        return cron;
    }

    public static void main(String[] args){

        String type = "week";
        String detail = "MON";
        String da = "12:10:05";

        //DateUtil

        LocalTime time1 = LocalTime.parse(da);

        String cron =  QuartzCronUtil.genQuartzCron(type, detail, time1);


        System.out.println(cron);

    }

}
