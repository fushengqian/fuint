package com.fuint.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
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
     * 隐藏手机号中间4位
     *
     * @param phone
     * @return
     * */
    public static String hidePhone(String phone) {
        if (StringUtil.isEmpty(phone)) {
            return "";
        }
        if (phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }


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
     * 功能：将输入字符串的首字母改成驼峰格式
     *
     * @param str
     * @return
     */
    public static String toCamelCase(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 判断是否UTF-8编码
     *
     * @param str
     * @return
     * */
    public static boolean isUtf8(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            String newStr = new String(bytes, "UTF-8");
            return str.equals(newStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否乱码
     *
     * @param str
     * @return
     * */
    public static boolean isErrCode(String str) {
        return !(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(str));
    }

    /**
     * 判断是否数字
     *
     * @param str 字符串
     * @return
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
     * 生成随机会员号(13位数)
     *
     * @return
     * */
    public static String createUserNo() {
        StringBuilder sb = new StringBuilder("8");
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        return sb.toString();
    }

    /**
     * 生成随机键值号
     *
     * @return
     * */
    public static String createAccountKey() {
        StringBuilder sb = new StringBuilder("11");
        sb.append(SeqUtil.getRandomNumber(6));
        sb.append(SeqUtil.getRandomNumber(5));
        String t = TimeUtils.formatDate(new Date(), "yyyyMMddHH");
        return t + sb.toString();
    }

    /**
     * 生成随机商户号
     *
     * @return
     * */
    public static String createMerchantNo() {
        StringBuilder sb = new StringBuilder("8");
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        return sb.toString();
    }

    /**
     * 生成随机结算单号(13位数)
     *
     * @return
     * */
    public static String createSettlementNo() {
        StringBuilder sb = new StringBuilder("8");
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        sb.append(SeqUtil.getRandomNumber(4));
        return sb.toString();
    }

    /**
     * 生成随机订单号
     *
     * @param userId
     * @return
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
     * @param  request
     * @return String
     */
    public static String getIPFromHttpRequest(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        if (!isValidIP(ipAddress)) {
            return "127.0.0.1";
        }

        return ipAddress;
    }

    /**
     * 验证ip地址是否正确
     *
     * @param ip
     * @return
     * */
    public static boolean isValidIP(String ip) {
        if ((ip != null) && (!ip.isEmpty())) {
            return Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ip);
        }
        return false;
    }

    /**
     * 保存上传文件
     *
     * @param file 上传的文件
     * @param filePath 文件路径
     * @return
     * */
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

    /**
     * 判断是否emo表情
     *
     * @param first 字符串
     * @return
     * */
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
     * 处理html中的视频
     *
     * @param html
     * @return
     * */
    public static String fixVideo(String html) {
        // 正则表达式匹配<iframe>标签，并捕获src属性
        String iframeRegex = "<iframe[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(iframeRegex);
        Matcher matcher = pattern.matcher(html);

        // 用于存储替换后的HTML
        StringBuffer result = new StringBuffer();

        // 遍历所有匹配的<iframe>标签
        while (matcher.find()) {
            // 获取src属性的值
            String src = matcher.group(1);

            // 构建<video>标签
            String videoTag = "<video controls><source src=\"" + src + "\" type=\"video/mp4\">Your browser does not support the video tag.</video>";

            // 将匹配的<iframe>标签替换为<video>标签
            matcher.appendReplacement(result, videoTag);
        }

        // 将剩余的HTML内容追加到结果中
        matcher.appendTail(result);

        return result.toString();
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
