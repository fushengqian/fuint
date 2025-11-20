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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * 文件上传服务类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

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
