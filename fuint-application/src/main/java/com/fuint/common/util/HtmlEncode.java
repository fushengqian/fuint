package com.fuint.common.util;

/**
 * HtmlEncode 工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class HtmlEncode {

    public static String htmlEncode(String string) {
        if(null == string || "".equals(string))
            return null;
        else{
            String result = string;
            result = result.replaceAll("&", "&");
            result = result.replaceAll("<", "<");
            result = result.replaceAll(">", ">");

            return (result);
        }
    }

    public static String htmlDecode(String string) {
        if(null == string || "".equals(string))
            return null;
        else{
            String result = string;
            result = result.replaceAll("&", "&");
            result = result.replaceAll("<", "<");
            result = result.replaceAll(">", ">");
            return (result);
        }
    }
}