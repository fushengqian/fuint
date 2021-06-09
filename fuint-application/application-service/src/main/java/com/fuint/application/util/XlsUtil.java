package com.fuint.application.util;

import com.fuint.exception.BusinessCheckException;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.text.DecimalFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel表格工具类
 * Created by liuguofang on 2016/11/28.
 */
public class XlsUtil {

    private static final Logger logger = LoggerFactory.getLogger(XlsUtil.class);

    public static void setXlsHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        response.reset();
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Cache-Control", "public");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            String agent = request.getHeader("USER-AGENT");
            String downloadFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
                downloadFileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            }
            response.addHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Excel数据内容
     *
     * @param is           文件流
     * @param isExcel2003  是否是2003的excel
     * @param firstRowNum  开始读取的行号（从0开始）为NULL时从第一行开始
     * @param lastRowNum   结束读取的行号（从0开始）为NULL时最后一行结束
     * @param firstCellNum 结束读取的列号（从0开始）为NULL时第一列开始
     * @param lastCellNum  结束读取的列号（从0开始）为NULL时最后一列结束
     * @return List 包含单元格数据内容的List对象
     */
    public static List<List<String>> readExcelContent(InputStream is, boolean isExcel2003, Integer firstRowNum, Integer lastRowNum, Integer firstCellNum, Integer lastCellNum) throws BusinessCheckException {
        List<List<String>> content = new ArrayList<>();
        Workbook wb;
        Sheet sheet;
        Row row;
        try {
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.error("XlsUtil—>readExcelContent：{}", e);
            throw new BusinessCheckException("导入失败");
        }
        sheet = wb.getSheetAt(0);
        int startRowNum = 0;
        if (null != firstRowNum) {
            startRowNum = firstRowNum;
        }
        // 得到总行数
        int endRowNum = sheet.getLastRowNum();
        if (null != lastRowNum) {
            endRowNum = lastRowNum;
        }
        int startCellNum = 0;
        if (null != firstCellNum) {
            startCellNum = firstCellNum;
        }
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = startRowNum; i <= endRowNum; i++) {
            List<String> rowContent = new ArrayList<>();
            row = sheet.getRow(i);
            int endCellNum = row.getLastCellNum();
            if (null != lastCellNum) {
                endCellNum = lastCellNum;
            }
            for (int j = startCellNum; j <= endCellNum; j++) {
                Cell cell = row.getCell(j);
                String str = "";

                if (null != cell) {
                    if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        cell.setCellType(1);
                    }
                    str = getStringCellValue(cell).trim();
                }
                rowContent.add(str);
            }
            content.add(rowContent);
        }
        return content;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getStringCellValue(Cell cell) {
        String strCell = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    /**
     * 是否是2003的excel，返回true是2003
     *
     * @param fileName
     * @return
     */
    public static boolean isExcel2003(String fileName) {
        return fileName.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 是否是2007的excel，返回true是2007
     *
     * @param fileName
     * @return
     */

    public static boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 返回字符串,如果是null,返回空
     *
     * @param obj
     * @return
     */
    public static String objectConvertToString(Object obj)
    {
       return obj==null ? "":obj.toString();
    }
}
