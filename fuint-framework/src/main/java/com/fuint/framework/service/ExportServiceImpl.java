package com.fuint.framework.service;

import com.fuint.framework.dto.ExcelExportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * 导出Excel文件业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
