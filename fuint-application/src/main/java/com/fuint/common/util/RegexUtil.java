package com.fuint.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class RegexUtil {

    private static final Map<String, Pattern> patternMap = new HashMap();

    public RegexUtil() {
        // empty
    }

    /**
     * 检查字符串中是否包含中文字符
     * @param input 待检查的字符串
     * @return 如果包含中文字符返回true，否则返回false
     */
    public static boolean containsChinese(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // 检查字符是否在CJK统一表意文字范围内（包括扩展区）
            if (isChineseCharacter(c)) {
                return true;
            }
        }
        return false;
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

    /**
     * 判断单个字符是否为中文字符
     * @param c 待检查的字符
     * @return 如果是中文字符返回true，否则返回false
     */
    private static boolean isChineseCharacter(char c) {
        // 基本CJK统一表意文字范围：U+4E00到U+9FFF
        // CJK统一表意文字扩展区A：U+3400到U+4DBF
        // CJK统一表意文字扩展区B：U+20000到U+2A6DF（需要特殊处理，因为Java char是16位）
        // 这里我们主要检查基本范围和扩展区A
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (c >= 0x4E00 && c <= 0x9FFF) ||
                (c >= 0x3400 && c <= 0x4DBF) ||
                block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
                block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION;
    }
}
