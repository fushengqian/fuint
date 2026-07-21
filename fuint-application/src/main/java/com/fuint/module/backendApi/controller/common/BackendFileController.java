package com.fuint.module.backendApi.controller.common;

import com.aliyun.oss.OSS;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.UploadService;
import com.fuint.common.util.AliyunOssUtil;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
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
@Api(tags="管理端-文件上传相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/file")
public class BackendFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendFileController.class);

    /**
     * 后台允许上传的文件类型白名单
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>();

    static {
        // 图片
        ALLOWED_EXTENSIONS.add("jpg");
        ALLOWED_EXTENSIONS.add("jpeg");
        ALLOWED_EXTENSIONS.add("png");
        ALLOWED_EXTENSIONS.add("gif");
        ALLOWED_EXTENSIONS.add("bmp");
        ALLOWED_EXTENSIONS.add("svg");
        ALLOWED_EXTENSIONS.add("webp");
        // 文档
        ALLOWED_EXTENSIONS.add("pdf");
        ALLOWED_EXTENSIONS.add("doc");
        ALLOWED_EXTENSIONS.add("docx");
        ALLOWED_EXTENSIONS.add("xls");
        ALLOWED_EXTENSIONS.add("xlsx");
        ALLOWED_EXTENSIONS.add("ppt");
        ALLOWED_EXTENSIONS.add("pptx");
        ALLOWED_EXTENSIONS.add("csv");
        ALLOWED_EXTENSIONS.add("txt");
        // 压缩包
        ALLOWED_EXTENSIONS.add("zip");
    }

    /**
     * 环境变量
     * */
    private Environment env;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 上传文件服务接口
     * */
    private UploadService uploadService;

    /**
     * 后台上传文件
     */
    @ApiOperation(value = "后台上传文件")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject uploadFileLocal(HttpServletRequest request) {
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (action.equals("config")) {
            Map<String, Object> outParams = new HashMap();
            outParams.put("imageActionName", "upload");
            outParams.put("fileActionName", "upload");
            outParams.put("fileFieldName", "file");
            outParams.put("imageFieldName", "file");
            outParams.put("fileUrlPrefix", "");
            outParams.put("imageUrlPrefix", "");
            return getSuccessResult(outParams);
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String sourcePic = request.getParameter("sourcePic");// 页面图片元素的ID
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
            return getFailureResult(201, "上传的文件不能大于" + maxSize + "MB");
        }

        // 校验文件类型白名单
        String ext = null;
        if (originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext)) {
            return getFailureResult(201, "不支持该文件类型，仅允许上传：" + String.join("、", ALLOWED_EXTENSIONS));
        }

        // 防止路径穿越：从原始文件名中提取纯文件名（去掉路径部分）
        String safeFilename = originalFilename;
        if (safeFilename.contains("/") || safeFilename.contains("\\")) {
            safeFilename = safeFilename.substring(safeFilename.lastIndexOf("/") > safeFilename.lastIndexOf("\\")
                    ? safeFilename.lastIndexOf("/") + 1 : safeFilename.lastIndexOf("\\") + 1);
        }

        // 保存文件
        try {
            String fileName = uploadService.saveUploadFile(request, file);
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
            String ip = CommonUtil.getIPFromHttpRequest(request);
            logger.info("用户ip:{},上传文件url:{},account:{}", ip, url, accountInfo.getAccountName());
        } catch (Exception e) {
            return getFailureResult(201, "上传失败，请检查上传配置及权限");
        }

        return getSuccessResult(resultMap);
    }
}
