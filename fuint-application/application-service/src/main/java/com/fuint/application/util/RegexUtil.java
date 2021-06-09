package com.fuint.application.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gang.wang on 2017/5/22.
 */
public class RegexUtil {

    private static final Map<String, Pattern> patternMap = new HashMap();

    public RegexUtil() {
    }

    public static boolean isEmail(String email) {
        String regex = "^\\w+([-+.]+\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return matches(email, regex);
    }

    public static boolean isMobile(String mobile) {
        String regex = "^1\\d{10}$";
        return matches(mobile, regex);
    }

    public static boolean isPhone(String phone) {
        String regex = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$";
        return matches(phone, regex);
    }

    public static boolean isChinese(String str) {
        String regex = "^[\\u4e00-\\u9fa5]+$";
        return matches(str, regex);
    }

    public static boolean isEnglish(String str) {
        return matches(str, "^[A-Za-z]+$");
    }

    public static boolean isNumber(String str) {
        return matches(str, "^\\d+$");
    }

    public static boolean isAmount(String str) {
        String regex = "^(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d{1,2})$";
        return matches(str, regex);
    }

    public static boolean isDate(String date) {
        String regex = "^(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])$";
        return matches(date, regex);
    }

    public static boolean isTime(String time) {
        String regex = "^(0\\d{1}|1\\d{1}|2[0-3]):[0-5]\\d{1}:([0-5]\\d{1})$";
        return matches(time, regex);
    }

    public static boolean isDateTime(String dateTime) {
        String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
        return matches(dateTime, regex);
    }

    public static boolean matches(String str, String regex) {
        if(null == str) {
            return false;
        } else {
            Pattern p = getPattern(regex);
            Matcher m = p.matcher(str);
            boolean b = m.matches();
            return b;
        }
    }

    private static Pattern getPattern(String regex) {
        Pattern p = (Pattern)patternMap.get(regex);
        if(null == p) {
            p = Pattern.compile(regex);
            patternMap.put(regex, p);
        }

        return p;
    }

}
