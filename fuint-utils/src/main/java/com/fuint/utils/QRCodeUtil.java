package com.fuint.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

/**
 * 二维码生成工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class QRCodeUtil {

    public static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    /**
     * 保存二维码
     *
     * @param bytes
     * @return
     * */
    public static void saveQrCodeToLocal(byte[] bytes, String path) {
        try {
            InputStream inputStream = new ByteArrayInputStream(bytes);
            FileOutputStream out = new FileOutputStream(path);

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            inputStream.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
