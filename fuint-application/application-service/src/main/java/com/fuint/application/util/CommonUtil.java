package com.fuint.application.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zach 2019/09/04
 */
public class CommonUtil {

    /**
     * 生成随机订单号
     * @param userId
     * */
    public static String createOrderSN(String userId) {
        // 时间是17位
        String date = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder();
        sb.append(date);

        // 目前长益的会员id为9位，不确定后面会不会变更
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
     * 获取系统当前时间戳(精确到秒)
     *
     * @return
     */
    public static int getSysCurrentTimeSec() {
        return Integer.parseInt("" + (System.currentTimeMillis() / 1000));
    }

    /**
     * 获取手机验证码
     *
     * @return
     */
    public static String getVerificationCode(int length) {
        StringBuffer sb = new StringBuffer("");
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 对象转JSON
     *
     * @param obj
     * @return
     */
    public static String objectToJsonStr(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转JSON 字节流
     *
     * @param obj
     * @return
     */
    public static byte[] objectToJsonByte(Object obj) {
        try {
            return new ObjectMapper().writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析json to JsonNode
     *
     * @param json
     * @return
     */
    public static JsonNode jsonToObject(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送短信
     *
     * @param Mobile
     * @param Content
     * @param send_time
     * @return
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    public static int sendSMS(String Mobile, String Content, String send_time) throws MalformedURLException, UnsupportedEncodingException {
        URL url = null;
        String CorpID = "TCLK04491";
        String Pwd = "sms@dragon";
        String send_content = URLEncoder.encode(Content.replaceAll("<br/>", " "), "GBK");
        url = new URL("  http://inolink.com/WS/BatchSend.aspx?CorpID=" + CorpID + "&Pwd=" + Pwd + "&Mobile=" + Mobile + "&Content=" + send_content + "&Cell=&SendTime=" + send_time);
        BufferedReader in = null;
        int inputLine = 0;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            inputLine = new Integer(in.readLine()).intValue();
        } catch (Exception e) {
            inputLine = -2;
        }
        return inputLine;
    }

    /**
     * 读取文件返回字符串
     *
     * @param filePath
     * @return
     */
    public static String readResourceFileToString(String filePath) {
        BufferedReader reader = null;
        String returnStr = "";
        try {
            Resource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                returnStr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnStr;
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
        if (StringUtils.isEmpty(formatStr)) formatStr = "yyyy-MM-dd";
        SimpleDateFormat dateFormater = new SimpleDateFormat(formatStr);
        return dateFormater.format(date);
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @param formatStr
     * @return
     */
    public static Date strToDate(String str, String formatStr) {
        if (StringUtils.isEmpty(formatStr)) formatStr = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 月份减法
     *
     * @param date
     * @param monthNum
     * @return
     */
    public static int reduceMonth(Date date, int monthNum) {
        if (date == null) date = new Date();
        Calendar returnDate = Calendar.getInstance();
        returnDate.setTime(date);
        returnDate.add(Calendar.MONTH, -monthNum);
        return Integer.parseInt("" + (returnDate.getTime().getTime() / 1000));
    }

    /**
     * 月加法
     *
     * @param date
     * @param monthNum
     * @return
     */
    public static int addMonth(Date date, int monthNum) {
        if (date == null) date = new Date();
        Calendar returnDate = Calendar.getInstance();
        returnDate.setTime(date);
        returnDate.add(Calendar.MONTH, monthNum);
        return Integer.parseInt("" + (returnDate.getTime().getTime() / 1000));
    }

    /**
     * 时间戳日加法
     */
    public static int addTimeStamp(int seconds, int dayNum) {
        Date date = new Date(Long.valueOf(seconds + "000"));
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayNum);
        return Integer.parseInt("" + calendar.getTime().getTime() / 1000);
    }
    /**
     * 随机获取List中一部分
     *
     * @param list
     * @param n
     * @return
     */
    public static List fetchRandomList(List list, int n) {
        Set<Integer> set = new HashSet<>();
        List listNew = new ArrayList();
        if (list == null || list.size() <= n) {
            return list;
        } else {
            while (set.size() < n) {
                int random = (int) (Math.random() * list.size());
                if (!set.contains(random)) {
                    set.add(random);
                    listNew.add(list.get(random));
                }
            }
            return listNew;
        }
    }

    /**
     * 获取句子中的所有单词
     *
     * @param sentence
     * @return
     */
    public static HashSet<String> getSentenceWords(String sentence) {
        HashSet<String> words = new HashSet<>();
        StringTokenizer st = new StringTokenizer(sentence, " ,.!?");
        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            if (!words.contains(key)) words.add(key);
        }
        return words;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int length) { //length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    public static boolean isChineseByReg(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str).matches();
    }

    /**
     * html保存成文件
     *
     * @param htmlStr
     * @param filePath
     */
    public static void html2image(String htmlStr, String filePath) {
//        HtmlImageGenerator generator = new HtmlImageGenerator();
//        generator.loadHtml(htmlStr);
//        generator.getBufferedImage();
//        generator.saveAsImage(filePath);
//        Html2Image html2Image = Html2Image.fromHtml(htmlStr);
//        html2Image.getImageRenderer().saveImage(filePath);
    }

//    public static void main(String[] args) {
//        HashSet<String> words = getSentenceWords("transactions and different levels of on-disk persistence? and provides high availability via Redis Sentinel and automatic partitioning with Redis Cluster");
//        Iterator<String> iterator = words.iterator();
//        while(iterator.hasNext()){
//            System.out.println(iterator.next());
//        }

//        int nowTime = getSysCurrentTimeSec();
//        System.out.println("now time :" + nowTime);
//        System.out.println("month time :" + (nowTime - 30 * 24 * 3600));
//        System.out.println("month time2 :" + reduceMonth(new Date(), 1));
//    }
    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static int Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Long time = sdf.parse(dateStr).getTime() / 1000l;
            return time.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
