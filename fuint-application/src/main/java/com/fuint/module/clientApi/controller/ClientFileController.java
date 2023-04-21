package com.fuint.module.clientApi.controller;

import com.fuint.common.service.SettingService;
import com.fuint.common.util.AliyunOssUtil;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import com.aliyun.oss.OSS;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传管理控制类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-文件上传相关接口")
@RestController
@RequestMapping(value = "/clientApi/file")
public class ClientFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFileController.class);

    @Autowired
    private Environment env;

    @Autowired
    private SettingService settingService;

    /**
     * 上传文件
     *
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String sourcePic = request.getParameter("sourcePic");
        MultipartFile file = multipartRequest.getFile(sourcePic);

        if (file == null) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.size() > 0) {
                file = fileMap.get("file");
            }
        }

        Map<String, String> resultMap = new HashMap<>();
        String originalFilename = file.getOriginalFilename();
        if (StringUtil.isEmpty(originalFilename)) {
            return getFailureResult(201, "上传出错啦");
        }

        String maxSizeStr = env.getProperty("images.upload.maxSize");

        // 默认限制2M
        float maxSize = 2;
        try {
            maxSize = Float.parseFloat(maxSizeStr);
        } catch (NumberFormatException e) {
            logger.error("图片允许的大小设置不正确", e);
        }
        if (file.getSize() > (maxSize * 1024 * 1024)) {
            return getFailureResult(201, "上传的图片不能大于" + maxSize + "MB");
        }

        String fileType = file.getContentType();
        if (fileType.indexOf("image") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        String original = file.getOriginalFilename().toLowerCase();
        if (original.indexOf("jpg") == -1 && original.indexOf("jpeg") == -1 && original.indexOf("png") == -1 && original.indexOf("gif") == -1 && original.indexOf("bmp") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        // 保存文件
        try {
            String fileName = saveFile(file);
            String baseImage = settingService.getUploadBasePath();
            String filePath = baseImage + fileName;
            String url = filePath;

            // 上传阿里云oss
            String mode = env.getProperty("aliyun.oss.mode");
            if (mode.equals("1")) { // 检查是否开启上传
                String endpoint = env.getProperty("aliyun.oss.endpoint");
                String accessKeyId = env.getProperty("aliyun.oss.accessKeyId");
                String accessKeySecret = env.getProperty("aliyun.oss.accessKeySecret");
                String bucketName = env.getProperty("aliyun.oss.bucketName");
                String folder = env.getProperty("aliyun.oss.folder");
                String domain = env.getProperty("aliyun.oss.domain");

                OSS ossClient = AliyunOssUtil.getOSSClient(accessKeyId, accessKeySecret, endpoint);

                String pathRoot = env.getProperty("images.root");
                if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
                    pathRoot = ResourceUtils.getURL("classpath:").getPath();
                }

                File ossFile = new File(pathRoot + fileName);
                fileName = AliyunOssUtil.upload(ossClient, ossFile, bucketName, folder);
                filePath = domain + fileName;
                url = filePath;
            }
            
            resultMap.put("status", "success");
            resultMap.put("domain", baseImage);
            resultMap.put("filePath", filePath);
            resultMap.put("fileName", fileName);
            resultMap.put("state", "SUCCESS");
            resultMap.put("original", file.getOriginalFilename());
            resultMap.put("size", file.getSize()+"");
            resultMap.put("title", fileName);
            resultMap.put("type", file.getContentType());
            resultMap.put("url", url);
        } catch (Exception e) {
            return getFailureResult(201, "上传失败，请检查上传配置及权限");
        }

        return getSuccessResult(resultMap);
    }

    public String saveFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String imageName = fileName.substring(fileName.lastIndexOf("."));
        String pathRoot = env.getProperty("images.root");
        if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
            pathRoot = ResourceUtils.getURL("classpath:").getPath();
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String baseImage = env.getProperty("images.path");
        String filePath = baseImage + DateUtil.formatDate(new Date(), "yyyyMMdd") + "/";
        String path = filePath + uuid + imageName;
        try {
            File tempFile = new File(pathRoot + path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            CommonUtil.saveMultipartFile(file, pathRoot + path);
        } catch (Exception e) {
            throw new Exception("上传失败，请检查目录是否可写");
        }
        return path;
    }
}
