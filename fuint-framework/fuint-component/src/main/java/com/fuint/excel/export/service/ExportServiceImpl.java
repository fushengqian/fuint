package com.fuint.excel.export.service;

import com.fuint.excel.export.dto.ExcelExportDto;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 导出Excel文件业务实现类
 * Created by liuguofang on 2016/11/25.
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Configuration configuration;

    private static synchronized Configuration getConfiguration(String srcTemplateName) {
        if (configuration == null) {
            configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

            try {
                configuration.setDirectoryForTemplateLoading(new File(srcTemplateName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
            configuration.setDefaultEncoding("UTF-8");
        }

        return configuration;
    }

    @Override
    public void exportExcel(ExcelExportDto data) {
        try {
            long e = System.currentTimeMillis();
            String srcTemplateName = data.getSrcTemplateFileName();
            logger.info("generate big data excel,start time {}", format.format(new Date()));
            logger.info("generate big data excel,srcTemplateName {}", srcTemplateName);
            Configuration configuration = getConfiguration(data.getSrcPath());
            configuration.clearTemplateCache();
            Template template = configuration.getTemplate(srcTemplateName);
            Map paramMap = data.getDataMap();
            OutputStreamWriter writer = new OutputStreamWriter(data.getOut(), "UTF-8");
            template.process(paramMap, writer);
            data.getOut().flush();
        } catch (Exception e) {
            logger.error("ExportServiceImpl.exportLocalFile{}", e);
        } finally {
            if (null != data.getOut()) {
                try {
                    data.getOut().close();
                } catch (IOException e) {
                    logger.error("ExportServiceImpl.exportLocalFile{}", e);
                }
            }
        }
    }

    @Override
    public void exportLocalFile(ExcelExportDto data) throws Exception {
        // 下载本地文件
        InputStream inStream = null;
        try {
            String fileName = data.getSrcTemplateFileName();
            // 读到流中s
            inStream = new FileInputStream(data.getSrcPath() + File.separator + fileName);// 文件的存放路径
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;
            while ((len = inStream.read(b)) > 0) {
                data.getOut().write(b, 0, len);
            }
            inStream.close();
            data.getOut().flush();
            data.getOut().close();
            // 设置输出的格式
        } catch (FileNotFoundException e) {
            logger.error("ExportServiceImpl.exportLocalFile{}", e);
            throw e;
        } catch (IOException e) {
            logger.error("ExportServiceImpl.exportLocalFile{}", e);
            throw e;
        } finally {
            try {
                if (null != inStream) {
                    inStream.close();
                }
            } catch (IOException e) {
                logger.error("ExportServiceImpl.exportLocalFile{}", e);
            }
            if (null != data.getOut()) {
                try {
                    data.getOut().close();
                } catch (IOException e) {
                    logger.error("ExportServiceImpl.exportLocalFile{}", e);
                }
            }

        }


    }
}
