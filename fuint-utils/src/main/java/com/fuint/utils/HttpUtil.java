package com.fuint.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * http请求工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class HttpUtil {

    public static final int CONNECT_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 60000;
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 发送http请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendRequest(String url) {
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
            return "";
        }

        return sb.toString();
    }

    public static String sendRequest(URL url, String data, Method method, Map<String, String> headers) throws IOException {
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setConnectTimeout(CONNECT_TIMEOUT);
        client.setReadTimeout(READ_TIMEOUT);
        client.setRequestMethod(method.value);
        if (headers != null && headers.size() > 0) {
            Iterator iter = headers.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next().toString();
                client.setRequestProperty(key, headers.get(key));
            }
        }
        if (Method.POST.equals(method)) {
            // 发送数据
            if (data != null) {
                client.setDoOutput(true);
                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
                osw.write(data);
                osw.flush();
                osw.close();
            }
        }
        // 发送请求
        client.connect();
        if (client.getResponseCode() >= 300) {
            throw new ServerUnavailable(url, client.getResponseCode(), client.getResponseMessage());
        }

        // 获取响应
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), UTF8));
        StringBuilder response = new StringBuilder();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            response.append(line);
        }
        return response.toString();
    }

    public enum Method {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE"),
        PUT("PUT");

        /**
         * 值
         */
        private String value;

        private Method(String value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

    }

    public static class ServerUnavailable extends RuntimeException {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        public ServerUnavailable(URL url, int code, String msg) {
            super("url: " + url + ", code: " + code + ", msg: " + msg);
        }
    }

}
