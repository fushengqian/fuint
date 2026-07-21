package com.fuint.module.clientApi.controller;

import com.aliyun.oss.OSS;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.AliyunOssUtil;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 文件上传管理控制类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-文件上传相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/file")
public class ClientFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFileController.class);

    /**
     * 客户端允许上传的图片类型白名单
     */
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>();

    static {
        ALLOWED_IMAGE_EXTENSIONS.add("jpg");
        ALLOWED_IMAGE_EXTENSIONS.add("jpeg");
        ALLOWED_IMAGE_EXTENSIONS.add("png");
        ALLOWED_IMAGE_EXTENSIONS.add("gif");
        ALLOWED_IMAGE_EXTENSIONS.add("bmp");
        ALLOWED_IMAGE_EXTENSIONS.add("webp");
    }

    /**
     * 系统环境变量
     * */
    private Environment env;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
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

        // 校验文件扩展名白名单（使用精确后缀匹配，防止 xxx.jpg.jsp 绕过）
        String ext = null;
        String lowerName = file.getOriginalFilename().toLowerCase();
        if (lowerName.contains(".")) {
            ext = lowerName.substring(lowerName.lastIndexOf(".") + 1);
        }
        if (ext == null || !ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            return getFailureResult(201, "上传的图片格式有误，仅支持：" + String.join("、", ALLOWED_IMAGE_EXTENSIONS));
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
                String endPoint = env.getProperty("aliyun.oss.endpoint");
                String accessKeyId = env.getProperty("aliyun.oss.accessKeyId");
                String accessKeySecret = env.getProperty("aliyun.oss.accessKeySecret");
                String bucketName = env.getProperty("aliyun.oss.bucketName");
                String folder = env.getProperty("aliyun.oss.folder");
                String domain = env.getProperty("aliyun.oss.domain");

                OSS ossClient = AliyunOssUtil.getOSSClient(accessKeyId, accessKeySecret, endPoint);

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
            String ip = CommonUtil.getIPFromHttpRequest(request);
            logger.info("用户ip:{},上传文件url:{}", ip, url);
        } catch (Exception e) {
            return getFailureResult(201, "上传失败，请检查上传配置及权限");
        }

        return getSuccessResult(resultMap);
    }

    public String saveFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        // 防止路径穿越：移除文件名中的路径分隔符
        if (fileName.contains("/") || fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") > fileName.lastIndexOf("\\")
                    ? fileName.lastIndexOf("/") + 1 : fileName.lastIndexOf("\\") + 1);
        }
        String imageName = fileName.substring(fileName.lastIndexOf("."));
        String pathRoot = env.getProperty("images.root");
        if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
            pathRoot = ResourceUtils.getURL("classpath:").getPath();
        }
        String uuid = SeqUtil.getUUID();
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
