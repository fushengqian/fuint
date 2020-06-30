package com.fuint.coupon.util;

public class HtmlEncode {


    public static String htmlEncode(String string) {
        if(null == string || "".equals(string))
            return null;
        else{
            String result = string;
            result = result.replaceAll("&", "&");
            result = result.replaceAll("<", "<");
            result = result.replaceAll(">", ">");
           // result = result.replaceAll("\"", "");
            return (result.toString());
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
            //result = result.replaceAll(""", "\"");
            return (result.toString());
        }
    }



    public static void main(String[] args) {
        System.out.println(HtmlEncode.htmlEncode("<script>alert(\"123\");</script>  "));
        System.out.println(HtmlEncode.htmlDecode("<script>alert(\"123\");</script>&nbsp;&nbsp;"));
    }
}