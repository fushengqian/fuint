package com.fuint.common.http;

import com.alibaba.fastjson.JSONObject;
import com.fuint.utils.StringUtil;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;


/**
 * 调用REST接口并解析数据
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
public class HttpRESTDataClient {

    public static final Logger logger = LoggerFactory.getLogger(HttpRESTDataClient.class);

    private static final OkHttpClient client = new OkHttpClient();

    private static HttpClientBuilder httpClientBuilder;

    static {
        httpClientBuilder = HttpClientBuilder.create();
    }

    public static String requestGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().string();
    }

    public static byte[] requestPost(String url, String postData) throws IOException {
        String postBody = postData;
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().bytes();
    }

    /**
     *  请求
     * @param url
     * @param jsonParam
     * @return
     */
    public static InputStream doWXPost(String url, JSONObject jsonParam) {
        InputStream instreams = null;
        HttpPost httpRequst = new HttpPost(url);// 创建HttpPost对象
        try {
            StringEntity se = new StringEntity(jsonParam.toString(),"utf-8");
            se.setContentType("application/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"UTF-8"));
            httpRequst.setEntity(se);
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    instreams = httpEntity.getContent();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instreams;
    }

    public static String requestPost(String url, String contentType, String postData) throws IOException {
        MediaType mediaType = null;
        if (StringUtil.isNotEmpty(contentType)) {
            mediaType = MediaType.parse(contentType);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, postData))
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().string();
    }

    public static String requestPostBody(String url, String body) {
        logger.debug("[HttpRESTDataClient] [requestPostBody] 入参 url={} body={}", url, body);

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try {
            HttpClient client = httpClientBuilder.build();
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "utf-8");
            }
            return "";
        }
        catch (IOException ex) {
            logger.error("[HttpRESTDataClient] [requestPostBody] 请求异常 ex={}", url, ex);
            return "";
        }
    }
}
