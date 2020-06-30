package com.fuint.coupon.util;

import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Http协议上传图片工具类
 * Created by liuguofang on 2016/9/13.
 */
public class HttpPostUploadUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpPostUploadUtil.class);

    /**
     * 上传图片 到图片服务器
     *
     * @param urlStr         图片服务器URL
     * @param connectTimeout 上传图片连接超时时间（单位：毫秒）
     * @param readTimeout    上传图片读取超时时间（单位：毫秒）
     * @param textMap        图片服务器要求的图片输入框参数
     * @param fileMap        图片参数
     * @return
     * @throws BusinessCheckException
     */
    public static String formUpload(String urlStr, int connectTimeout, int readTimeout, Map<String, String> textMap, Map<String, MultipartFile> fileMap) throws BusinessCheckException {
        String res = "";
        HttpURLConnection conn = null;
        OutputStream out = null;
        BufferedReader reader = null;
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());
            // text
            if (null != textMap) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            if (null != fileMap) {
                Iterator<Map.Entry<String, MultipartFile>> iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, MultipartFile> entry = iter.next();
                    String inputName = (String) entry.getKey();
                    MultipartFile multipartFile = (MultipartFile) entry.getValue();
                    if (multipartFile == null) {
                        continue;
                    }
                    String fileName = multipartFile.getOriginalFilename();
                    String contentType = multipartFile.getContentType();

                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + fileName + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

                    out.write(strBuf.toString().getBytes());

                    DataInputStream in = new DataInputStream(multipartFile.getInputStream());
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();

            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
        } catch (ProtocolException e) {
            logger.error("上传图片到服务器失败—>ProtocolException", e);
            throw new BusinessCheckException("图片服务器连接出错。");
        } catch (MalformedURLException e) {
            logger.error("上传图片到服务器失败—>MalformedURLException", e);
            throw new BusinessCheckException("图片服务器连接出错。");
        } catch (IOException e) {
            logger.error("上传图片到服务器失败—>IOException", e);
            throw new BusinessCheckException("图片服务器连接出错。");
        } finally {
            closeStream(out);
            closeStream(reader);
            if (null != conn) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    /**
     * 关闭流资源
     * @param closeable
     */
    private static void closeStream(Closeable closeable){
        try {
            if (null != closeable) {
                closeable.close();
                closeable = null;
            }
        }catch (IOException e) {
            logger.error("关闭资源出错", e);
        }
    }

}
