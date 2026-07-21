package com.fuint.common.service.impl;

import com.fuint.common.service.UploadService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传服务类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class UploadServiceImpl implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    /**
     * 文件上传允许的类型白名单
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>();

    static {
        ALLOWED_EXTENSIONS.add("jpg");
        ALLOWED_EXTENSIONS.add("jpeg");
        ALLOWED_EXTENSIONS.add("png");
        ALLOWED_EXTENSIONS.add("gif");
        ALLOWED_EXTENSIONS.add("bmp");
        ALLOWED_EXTENSIONS.add("svg");
        ALLOWED_EXTENSIONS.add("webp");
        ALLOWED_EXTENSIONS.add("pdf");
        ALLOWED_EXTENSIONS.add("doc");
        ALLOWED_EXTENSIONS.add("docx");
        ALLOWED_EXTENSIONS.add("xls");
        ALLOWED_EXTENSIONS.add("xlsx");
        ALLOWED_EXTENSIONS.add("ppt");
        ALLOWED_EXTENSIONS.add("pptx");
        ALLOWED_EXTENSIONS.add("csv");
        ALLOWED_EXTENSIONS.add("txt");
        ALLOWED_EXTENSIONS.add("zip");
    }

    /**
     * 环境变量
     * */
    private Environment env;

    /**
     * 保存文件
     *
     * @param file excel文件
     * @param request
     * @return
     * */
    public String saveUploadFile(HttpServletRequest request, MultipartFile file) throws Exception {
        if (file == null) {
            throw new BusinessCheckException("上传文件出错！");
        }
        String fileName = file.getOriginalFilename();

        // 校验文件类型白名单
        String ext = null;
        String lowerName = fileName.toLowerCase();
        if (lowerName.contains(".")) {
            ext = lowerName.substring(lowerName.lastIndexOf(".") + 1);
        }
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessCheckException("不支持该文件类型，仅允许上传：" + String.join("、", ALLOWED_EXTENSIONS));
        }

        // 防止路径穿越：移除文件名中的路径分隔符
        if (fileName.contains("/") || fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") > fileName.lastIndexOf("\\")
                    ? fileName.lastIndexOf("/") + 1 : fileName.lastIndexOf("\\") + 1);
        }

        String uploadPath = fileName.substring(fileName.lastIndexOf("."));
        String pathRoot = env.getProperty("images.root");
        if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
            pathRoot = ResourceUtils.getURL("classpath:").getPath();
        }
        String uuid = SeqUtil.getUUID();

        String filePath = "/static/uploadFiles/"+ DateUtil.formatDate(new Date(), "yyyyMMdd")+"/";
        String path = filePath + uuid + uploadPath;

        try {
            File tempFile = new File(pathRoot + path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            CommonUtil.saveMultipartFile(file, pathRoot + path);
        } catch (Exception e) {
            logger.error("上传文件保存出错：", e.getMessage());
        }

        return path;
    }

}
