package com.fuint.framework.service;

import com.fuint.framework.dto.ExcelExportDto;

/**
 * 导出Excel文件业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ExportService {

    /**
     * 直接导出本地文件
     *
     * @param data srcPath 文件相对路径
     *             srcTemplateFileName 文件名称
     *             out 输出流
     */
    void exportLocalFile(ExcelExportDto data) throws Exception;
}
