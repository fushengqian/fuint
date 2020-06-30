package com.fuint.coupon.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 调用REST接口并解析数据
 * Created by gang.wang on 2017/4/12.
 */
@Component
public class HttpRESTDataClient {

    public static final Logger logger = LoggerFactory.getLogger(HttpRESTDataClient.class);

    /**
     * 调用黑盒子REST接口获取数据
     * @return
     * @throws Exception
     */
    public static Map<String,Object> getRestfulApiUserIdData(String postUrl,String jsonObject,String method) throws Exception {

        long startTime = System.currentTimeMillis();
        logger.info("HttpRESTPostClient begin ... url={},inParams={}",postUrl, jsonObject);

        //需要请求的restful地址
        URL url = new URL(postUrl);
        //打开restful链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 提交模式
        conn.setRequestMethod(Optional.ofNullable(method).orElse("POST"));//POST GET PUT DELETE

        conn.setConnectTimeout(100000);//连接超时 单位毫秒
        conn.setReadTimeout(100000);//读取超时 单位毫秒

        if("POST".equals(Optional.ofNullable(method).orElse("POST"))) {
            conn.setDoOutput(true);// 是否输入参数
            //设置访问提交模式，表单提交
            conn.setRequestProperty("Content-Type","application/json");
        }

        conn.connect();

        if("POST".equals(Optional.ofNullable(method).orElse("POST"))) {
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            osw.write(jsonObject);
            osw.flush();
            osw.close();
        }

        Map<String,Object> resultMap = new HashMap<>();
        // 获得响应状态
        int resultCode = conn.getResponseCode();
        logger.info("getRestfulApiUserIdData responseCode={}",resultCode);
        if(resultCode == HttpURLConnection.HTTP_OK) {
            resultMap.put("result","success");
            conn.getResponseMessage();
            StringBuffer sb = new StringBuffer("");
            String readLine = new String();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((readLine = responseReader.readLine()) != null) {
                readLine = new String(readLine.getBytes(), "utf-8");
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            if(sb!=null && !"".equals(sb.toString())) {
                resultMap.put("data",sb.toString());
                logger.info("HttpRESTPostClient response data=[{}]",sb.toString());
            }
        }else{
            resultMap.put("result","error");
            resultMap.put("errorCode",conn.getResponseCode());
            logger.error("HttpRESTPostClient response error code:",conn.getResponseCode());
            //throw new RuntimeException("HTTP Request Failed with Error code : " + conn.getResponseCode());
        }

        long endTime = System.currentTimeMillis();
        logger.info("接口返回[用时{},接口URL{}]", (endTime-startTime)/1000.0,postUrl);
        return resultMap;
    }


}
