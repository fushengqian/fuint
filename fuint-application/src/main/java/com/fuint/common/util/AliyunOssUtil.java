package com.fuint.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * 阿里云OSS存储工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AliyunOssUtil {

    private static final Logger logger = LoggerFactory.getLogger(AliyunOssUtil.class);

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    public static OSS getOSSClient(String accessKeyId, String accessKeySecret, String endpoint) {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 创建存储空间
     *
     * @param ossClient OSS连接
     * @param bucketName 存储空间
     * @return
     */
    public static String createBucketName(OSS ossClient, String bucketName) {
        // 存储空间
        final String bucketNames = bucketName;

        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            logger.info("创建存储空间成功");
            return bucket.getName();
        }

        return bucketNames;
    }

    /**
     * 创建模拟文件夹
     *
     * @param ossClient oss连接
     * @return 文件夹名
     */
    public static String createFolder(OSS ossClient, String bucketName, String folder) {
        // 文件夹名
        final String keySuffixWithSlash = folder;

        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)) {
            // 创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            logger.info("创建文件夹成功");
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            String fileDir = object.getKey();
            return fileDir;
        }

        return keySuffixWithSlash;
    }

    /**
     * 上传图片至OSS
     *
     * @param ossClient oss连接
     * @param file 上传文件（文件全路径如：D:\\image\\cake.jpg）
     * @return String 返回文件url
     */
    public static String upload(OSS ossClient, File file, String bucketName, String folder) {
        String resultStr = null;

        // 先创建存储空间和文件夹
        createBucketName(ossClient, bucketName);
        createFolder(ossClient, bucketName, folder);

        try {
            InputStream is = new FileInputStream(file);
            String fileName = file.getName();
            String date = DateUtil.formatDate(new Date(), "yyyyMMdd");
            ossClient.putObject(bucketName, folder + "/" + date + "/" + fileName, is);
            resultStr = "/" + folder + "/" + date +"/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }

        return resultStr;
    }

    /**
     * 根据key删除OSS上的文件
     *
     * @param ossClient oss连接
     * @param key Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public static void deleteFile(OSS ossClient, String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);

        logger.info("删除" + bucketName + "下的文件" + folder + key + "成功");
    }
}

