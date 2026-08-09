package com.fuint.common.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Excel工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ExcelUtil {

    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, HSSFWorkbook wb) {
        return getHSSFWorkbook(sheetName, title, values, wb, null);
    }

    /**
     * 导出Excel（支持顶部汇总行）
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @param summary 顶部汇总行数据，每个元素为一行
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String []title, String [][]values, HSSFWorkbook wb, String [][]summary) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 创建单元格样式
        HSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 表头居中格式

        // 汇总行起始行号
        int rowIndex = 0;

        // 先写入汇总行
        if (summary != null) {
            for (int i = 0; i < summary.length; i++) {
                HSSFRow summaryRow = sheet.createRow(rowIndex++);
                for (int j = 0; j < summary[i].length; j++) {
                    summaryRow.createCell(j).setCellValue(summary[i][j]);
                }
            }
            // 汇总与明细之间空一行
            rowIndex++;
        }

        // 在sheet中添加表头
        HSSFRow row = sheet.createRow(rowIndex);

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(headerStyle);
        }

        //创建内容
        if (values!=null) {
            //创建内容
            for (int i = 0; i < values.length; i++) {
                row = sheet.createRow(i + rowIndex + 1);
                for (int j = 0; j < values[i].length; j++) {
                    //将内容按顺序赋给对应的列对象
                    row.createCell(j).setCellValue(values[i][j]);
                }
            }
        }
        return wb;
    }

    /**
     * 发送响应流方法
     * @param  response
     * @param  fileName
     * @param  wb
     * */
    public static void setResponseHeader(HttpServletResponse response, String fileName, HSSFWorkbook wb) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    /**
     * 下载Excel模板文件
     *
     * @param response
     * @param templateName
     * @return
     * */
    public static void downLoadTemplate(HttpServletResponse response, String templateName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("template/" + templateName);
        InputStream inputStream = classPathResource.getInputStream();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream out = response.getOutputStream();
        response.setHeader("Content-Disposition", "attachment; filename=" + templateName);
        int b = 0;
        byte[] buffer = new byte[1024*1024];
        while (b != -1) {
            b = inputStream.read(buffer);
            if(b!=-1) out.write(buffer, 0, b);
        }
        inputStream.close();
        out.close();
        out.flush();
    }
}
