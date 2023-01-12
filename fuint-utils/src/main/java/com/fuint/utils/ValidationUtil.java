package com.fuint.utils;

import org.apache.commons.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * 统一返回值: true-校验成功(合法) false-校验失败(非法)
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ValidationUtil {

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if ( !isNum.matches() ) {
            return false;
        }
        return true;
    }

    /**
     * 空校验
     *
     * @param param
     * @return
     */
    public static boolean validateEmpty(String param) {
        return !StringUtils.isEmpty(param);
    }

    /**
     * 长度校验
     *
     * @param param
     * @param minLen
     * @param maxLen
     * @return
     */
    public static boolean validateLength(String param, int minLen, int maxLen) {
        if (StringUtils.isEmpty(param)) param = "";
        return param.length() >= minLen && param.length() <= maxLen;
    }

    /**
     * 密码格式校验
     * @param param
     * @return
     */
    public static boolean validatePwdPattern(String param) {
        boolean flag = false;
        try {
            String check = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,20}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(param);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验手机号
     *
     * @param param
     * @return
     */
    public static boolean validateMobile(String param) {
        boolean flag = false;
        try {
            String check = "^[1][3,4,5,7,8][0-9]{9}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(param);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验邮箱
     *
     * @param param
     * @return
     */
    public static boolean validateEmail(String param) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(param);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验身份证号
     * @param param
     * @return
     */
    public static boolean validateIdCard(String param) {
        if (StringUtils.isEmpty(param)) return false;
        IDCard iv = new IDCard();
        return iv.isValidatedAllIdcard(param);
    }

    /**
     * 校验Url
     *
     * @param param
     * @return
     */
    public static boolean validateUrl(String param) {
        boolean flag = false;
        try {
            String check = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(param);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
