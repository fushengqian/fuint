package com.fuint.application.web.backend.components;

import com.fuint.application.service.setting.SettingService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.JsonUtil;
import com.fuint.application.util.AliyunOssUtil;
import com.fuint.application.web.backend.util.JSONUtil;
import com.aliyun.oss.OSS;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传管理控制类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private Environment env;

    @Autowired
    private SettingService settingService;

    /**
     * 上传文件
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/upload", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadFileLocal(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");

        if (action.equals("config")) {
            Map<String, Object> outParams = new HashMap();
            outParams.put("imageActionName", "upload");
            outParams.put("fileActionName", "upload");
            outParams.put("fileFieldName", "file");
            outParams.put("imageFieldName", "file");
            outParams.put("fileUrlPrefix", "");
            outParams.put("imageUrlPrefix", "");

            String resultJason = JsonUtil.toJSonString(outParams);
            return resultJason;
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String sourcePic = request.getParameter("sourcePic");// 页面图片元素的id
        MultipartFile file = multipartRequest.getFile(sourcePic);

        if (file == null) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.size() > 0) {
                file = fileMap.get("file");
            }
        }

        Map<String, String> resultMap = new HashMap<>();
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            resultMap.put("status", "error");
            resultMap.put("message", "上传出错啦");
            return JSONUtil.toJSonString(resultMap);
        }

        String maxSizeStr = env.getProperty("images.upload.maxSize");

        //默认限制5M
        float maxSize = 5;
        try {
            maxSize = Float.parseFloat(maxSizeStr);
        } catch (NumberFormatException e) {
            logger.error("图片允许的大小设置不正确", e);
        }
        if (file.getSize() > (maxSize * 1024 * 1024)) {
            resultMap.put("status", "error");
            resultMap.put("message", "上传的图片不能大于" + maxSize + "MB");
            return JSONUtil.toJSonString(resultMap);
        }

        String fileType = file.getContentType();
        if (fileType.indexOf("image") == -1) {
            resultMap.put("status", "error");
            resultMap.put("message", "上传的图片格式有误.");
            return JSONUtil.toJSonString(resultMap);
        }

        String original = file.getOriginalFilename().toLowerCase();
        if (original.indexOf("jpg") == -1 && original.indexOf("jpeg") == -1 && original.indexOf("png") == -1 && original.indexOf("gif") == -1 && original.indexOf("bmp") == -1) {
            resultMap.put("status", "error");
            resultMap.put("message", "上传的图片格式有误!");
            return JSONUtil.toJSonString(resultMap);
        }

        //保存文件
        try {
            String fileName = saveFile(file, request);
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
                WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
                ServletContext servletContext = webApplicationContext.getServletContext();
                String pathRoot = servletContext.getRealPath("");
                File ossFile = new File(pathRoot + fileName);
                fileName = AliyunOssUtil.upload(ossClient, ossFile, bucketName, folder);
                filePath = domain + fileName;
                url = filePath;
            }
            
            resultMap.put("status", "success");
            resultMap.put("filePath", filePath);
            resultMap.put("fileName", fileName);
            resultMap.put("state", "SUCCESS");
            resultMap.put("original", file.getOriginalFilename());
            resultMap.put("size", file.getSize()+"");
            resultMap.put("title", fileName);
            resultMap.put("type", file.getContentType());
            resultMap.put("url", url);
        } catch (Exception e) {
            resultMap.put("status", "error");
            resultMap.put("message", "上传失败，请检查上传配置！");
        }

        String resultJason = JSONUtil.toJSonString(resultMap);
        return resultJason;
    }

    public String saveFile(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();

        String imageName = fileName.substring(fileName.lastIndexOf("."));

        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        String pathRoot = servletContext.getRealPath("");

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        String baseImage = env.getProperty("images.path");
        String filePath = baseImage + DateUtil.formatDate(new Date(), "yyyyMMdd")+"/";

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
