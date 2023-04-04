package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.utils.IpUtil;
import com.fuint.utils.StringUtil;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 通用工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class CommonUtil {

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    public static String firstLetterToUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 判断是否数字
     * */
    public static boolean isNumeric(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }

        return true;
    }

    /**
     * 生成密码
     * */
    public static String createPassword(String password, String salt) {
        return MD5Util.getMD5(password + salt);
    }

    /**
     * 验证密码
     * */
    public static String getPassword(String password, String salt) {
        return MD5Util.getMD5(password + salt);
    }

    /**
     * 生成随机会员号(13位数)
     * */
    public static String createUserNo() {
        StringBuilder sb = new StringBuilder("8");
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        return sb.toString();
    }

    /**
     * 生成随机订单号
     * @param userId
     * */
    public static String createOrderSN(String userId) {
        // 时间是17位
        String date = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder();
        sb.append(date);

        // 目前的会员id为9位，不确定后面会不会变更
        if (userId.length() > 9) {
            sb.append(userId.substring(userId.length() - 9, 9));
        }

        if (userId.length() == 9) {
            sb.append(userId);
        }

        // 如果小于9位补位
        if (userId.length() < 9) {
            for (int i = 0; i < userId.length() - 9; i++) {
                sb.append("0");
            }
        }

        // 加上4位随机数
        sb.append(SeqUtil.getRandomNumber(4));
        return sb.toString();
    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return String
     */
    public static String getIPFromHttpRequest(HttpServletRequest request) {
        String remoteIp = request.getHeader("X-Real-IP");
        if (remoteIp == null) {
            remoteIp = request.getHeader("x-forwarded-for");
        }
        if (remoteIp == null) {
            remoteIp = request.getRemoteAddr();
        }

        // 校验IP格式
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(remoteIp);
        if (!matcher.matches()) {
            remoteIp = "127.0.0.1";
        }

        return remoteIp;
    }

    public static void saveMultipartFile(MultipartFile file, String filePath) {
        if (file != null && !file.isEmpty()) {
            try {
                FileOutputStream os = new FileOutputStream(new File(filePath));
                //拿到上传文件的输入流
                os.write(file.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化指定的日期
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String formatDate(Date date, String formatStr) {
        if (date == null) date = new Date();
        if (StringUtil.isEmpty(formatStr)) formatStr = "yyyy-MM-dd";
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        return dateFormater.format(date);
    }

    /**
     * 根据地址获取经纬度
     *
     * @param addr 地址
     * @return
     * */
    public static Map<String, Object> getLatAndLngByAddress(String addr) {
        String address = "";
        try {
            address = java.net.URLEncoder.encode(addr,"UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // key如果失效了就去高德地图官网申请
        String url =  "https://restapi.amap.com/v3/geocode/geo?address="+address+"&output=JSON&key="+"4d57813b7b9157d66899cc4c1f22dc04";

        URL myURL = null;
        URLConnection httpsConn;
        // 进行转码
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            // empty
        }
        StringBuffer sb = new StringBuffer();
        try {
            httpsConn = myURL.openConnection();
            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }
                insr.close();
            }
        } catch (IOException e) {

        }

        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = JSON.parseObject(sb.toString());
        JSONArray geocodes = resultJson.getJSONArray("geocodes");


        String lat = "";
        String lng = "";

        if (geocodes != null) {
            JSONObject jsonObject = geocodes.getJSONObject(0);
            String location = jsonObject.getString("location");

            if (org.apache.commons.lang.StringUtils.isNotEmpty(location)) {
                String latAndLng[] = location.split(",");
                if (latAndLng.length == 2) {
                    lat = latAndLng[1];
                    lng = latAndLng[0];
                }
            }
        }

        map.put("lat", lat);
        map.put("lng", lng);

        return map;
    }

    /**
     * 去除待带script、src的语句，转义替换后的value值
     *
     * @param value
     *
     * @return
     */
    public static String replaceXSS(String value) {
        if (value != null) {
            try {
                value = value.replace("+","%2B");
                value = URLDecoder.decode(value, "utf-8");
            } catch(Exception e) {
                //empty
            }

            StringBuilder buf = new StringBuilder(value.length());
            int len = value.length();
            for (int i = 0; i < len; i++) {
                char codePoint = value.charAt(i);
                if (isEmojiCharacter(codePoint)) {
                    buf.append("*");
                } else {
                    buf.append(codePoint);
                }
            }

            String emojiStr = buf.toString();
            if (!StringUtil.isEmpty(emojiStr)) {
                value = emojiStr;
            }

            // Avoid null characters
            value = value.replaceAll("\0", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid alert:... e­xpressions
            scriptPattern = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid οnlοad= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
        }

        return filter(value);
    }

    public static boolean isEmojiCharacter(char first) {
        return !
                ((first == 0x0) ||
                        (first == 0x9) ||
                        (first == 0xA) ||
                        (first == 0xD) ||
                        ((first >= 0x20) && (first <= 0xD7FF)) ||
                        ((first >= 0xE000) && (first <= 0xFFFD)) ||
                        ((first >= 0x10000)))||


                (first == 0xa9 || first == 0xae || first == 0x2122 ||
                        first == 0x3030 || (first >= 0x25b6 && first <= 0x27bf) ||
                        first == 0x2328 || (first >= 0x23e9 && first <= 0x23fa))
                || ((first >= 0x1F000 && first <= 0x1FFFF))
                || ((first >= 0x2702) && (first <= 0x27B0))
                || ((first >= 0x1F601) && (first <= 0x1F64F));
    }

    /**
     * 过滤特殊字符
     *
     * @param value
     *
     * @return
     */
    public static String filter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i=0; i<value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    result.append("<");
                    break;
                case '>':
                    result.append(">");
                    break;
                case '\'':
                    result.append("'");
                    break;
                case '%':
                    result.append("%");
                    break;
                case ';':
                    result.append(";");
                    break;
                case '(':
                    result.append("(");
                    break;
                case ')':
                    result.append(")");
                    break;
                case '&':
                    result.append("&");
                    break;
                case '+':
                    result.append("+");
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }

        return result.toString();
    }
}
