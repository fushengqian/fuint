package com.fuint.common.http;

import com.fuint.utils.StringUtil;
import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
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
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                return result;
            }
            return "";
        }
        catch (IOException ex) {
            logger.error("[HttpRESTDataClient] [requestPostBody] 请求异常 ex={}", url, ex);
            return "";
        }
    }
}
